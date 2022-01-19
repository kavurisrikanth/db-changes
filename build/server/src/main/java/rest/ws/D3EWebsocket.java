package rest.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import classes.DBResultStatus;
import classes.LoginResult;
import d3e.core.CurrentUser;
import d3e.core.D3ELogger;
import d3e.core.DFile;
import d3e.core.ListExt;
import d3e.core.MD5Util;
import d3e.core.TransactionWrapper;
import gqltosql.schema.DClazz;
import gqltosql.schema.DClazzMethod;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.DParam;
import gqltosql.schema.FieldType;
import gqltosql.schema.IModelSchema;
import gqltosql2.Field;
import gqltosql2.Selection;
import io.reactivex.rxjava3.functions.Cancellable;
import lists.TypeAndId;
import models.AnonymousUser;
import models.CreatableObject;
import models.User;
import security.JwtTokenUtil;
import security.UserProxy;
import store.DBObject;
import store.DatabaseObject;
import store.EntityHelperService;
import store.ListChanges;
import store.ListChanges.Change;
import store.ValidationFailedException;

@Configuration
@EnableWebSocket
public class D3EWebsocket extends BinaryWebSocketHandler implements WebSocketConfigurer {

	private static final long SESSION_TIMEOUT = 15_000;
	private static final long RECONNECT_TIMEOUT = 20 * 60 * 1000;

	private static final int ERROR = 0;
	private static final int CONFIRM_TEMPLATE = 1;
	private static final int HASH_CHECK = 2;
	private static final int TYPE_EXCHANGE = 3;
	private static final int RESTORE = 4;
	private static final int OBJECT_QUERY = 5;
	private static final int DATA_QUERY = 6;
	private static final int SAVE = 7;
	private static final int DELETE = 8;
	private static final int UNSUBSCRIBE = 9;
	private static final int LOGIN = 10;
	private static final int LOGIN_WITH_TOKEN = 11;
	private static final int CONNECT = 12;
	private static final int LOGOUT = 14;
	private static final int OBJECTS = -1;
	private static final int CHANNEL_MESSAGE = -2;
	private static final int CHANNEL_MESSAGE_ACK = -3;
	private static final int RPC_MESSAGE = -4;

	@Autowired
	private TemplateManager templateManager;

	@Autowired
	private TransactionWrapper wrapper;

	@Autowired
	private ObjectFactory<EntityHelperService> helperService;

	@Autowired
	private Channels channels;

	@Autowired
	private RPCHandler rpcHandler;

	@Autowired
	private RocketQuery query;

	@Autowired
	private RocketMutation mutation;

	@Autowired
	private IModelSchema schema;

	@Autowired
	private MasterTemplate master;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${rocket.reconnectPeriode:300}")
	private int reconnectPeriode;
	private Timer timer = new Timer();
	private Map<String, ClientSession> sessions = new HashMap<>();
	private Map<String, ClientSession> disconnectedSessions;
	private Map<ClientSession, Map<String, Cancellable>> subscriptions = new HashMap<>();

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static void printHeapMemoryStatus() {
		Runtime.getRuntime().gc();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		double used = (((double) (total - free) / total) * 100);
		D3ELogger.info(String.format("Memory: Total: %s, Free: %s, Used: %,.2f%%", readableFileSize(total),
				readableFileSize(free), used));
	}

	@PostConstruct
	public void init() {
		disconnectedSessions = new HashMap<>(); // TODO new
		// MapMaker().concurrencyLevel(4).weakValues().expiration(reconnectPeriode,
		// TimeUnit.SECONDS);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				//printHeapMemoryStatus();
				new ArrayList<>(sessions.values()).forEach(s -> {
					try {
						if (s.isLocked()) {
							return;
						}
						s.getSession().sendMessage(new PingMessage());
					} catch (Exception e) {
					}
				});
			}
		}, SESSION_TIMEOUT, SESSION_TIMEOUT);

		schema.getAllTypes();
		master.getTemplateType("");
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this, "/api/rocket").setAllowedOrigins("*");
	}

	@Override
	public boolean supportsPartialMessages() {
		return true;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		D3ELogger.info("D3EWebsocket connected. " + session.getId());
		sessions.put(session.getId(), new ClientSession(session));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String sessionid = session.getId();
		D3ELogger.info("D3EWebsocket connection closed. " + status + ", " + sessionid);
		ClientSession cs = sessions.remove(sessionid);
		disconnectedSessions.put(cs.getId(), cs);
		cs.setSession(null);
		if (cs.getTimeout() == 0) {
			cleanSession(sessionid);
			return;
		}
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					cleanSession(sessionid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, cs.getTimeout());
	}

	protected void cleanSession(String id) {
		D3ELogger.info("Closing connection after timeout. " + id);
		ClientSession csession = disconnectedSessions.remove(id);
		if (csession == null) {
			return;
		}
		Map<String, Cancellable> map = subscriptions.get(csession);
		if (map != null) {
			for (Cancellable c : map.values()) {
				try {
					c.cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		try {
			wrapper.doInTransaction(() -> {
				if (csession.userId != 0) {
					CurrentUser.set(csession.userType, csession.userId, id);
				} else {
					SecurityContextHolder.getContext().setAuthentication(null);
				}
				channels.disconnect(csession);
			});
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		ClientSession cs = sessions.get(session.getId());
		if (cs == null) {
			D3ELogger.info("Session was closed. Ignoring message. (It should not happen)");
			return;
		}
		MDC.put("userId", String.valueOf(cs.userId));
		MDC.put("http", session.getId());
		ByteBuffer payload = message.getPayload();
		cs.stream.writeBytes(payload.array());
		if (!message.isLast()) {
			return;
		}
		RocketMessage reader = new RocketMessage(cs);
		cs.stream = new ByteArrayOutputStream();
		try {
			wrapper.doInTransaction(() -> {
				boolean hasUser = cs.userId != 0;
				if (hasUser) {
					CurrentUser.set(cs.userType, cs.userId, cs.getId());
				} else {
					SecurityContextHolder.getContext().setAuthentication(null);
				}
				try {
					onMessage(cs, reader);
				} finally {
					reader.flush();
					if (hasUser) {
						// Since we are setting CurrentUser for every request, we can reset it after the
						// request is handled
						SecurityContextHolder.getContext().setAuthentication(null);
					}
				}
				// D3ELogger.info("Done");
			});
		} catch (UnexpectedRollbackException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cs.isLocked()) {
//				reader.flush();
			}
			CurrentUser.clear();
		}
	}

	private void onMessage(ClientSession ses, RocketMessage msg) {
		int id = msg.readInt();
		if (id == CHANNEL_MESSAGE) {
			// D3ELogger.info("Rocket Message: -2");
			msg.writeInt(CHANNEL_MESSAGE_ACK);
			onChannelMessage(ses, msg);
			return;
		}
		msg.writeInt(id);
		int type = msg.readByte();
		// D3ELogger.info("Rocket Message: " + type);
		switch (type) {
		case CONFIRM_TEMPLATE:
			onConfirmTemplate(ses, msg);
			break;
		case HASH_CHECK:
			onHashCheck(ses, msg);
			break;
		case TYPE_EXCHANGE:
			onTypeExchange(ses, msg);
			break;
		case RESTORE:
			onRestore(ses, msg);
			break;
		case OBJECT_QUERY:
			onObjectQuery(ses, msg);
			break;
		case DATA_QUERY:
			onDataQuery(ses, msg);
			break;
		case SAVE:
			onSave(ses, msg);
			break;
		case DELETE:
			onDelete(ses, msg);
			break;
		case UNSUBSCRIBE:
			onUnsubscribe(ses, msg);
			break;
		case LOGIN:
			onLogin(ses, msg);
			break;
		case CONNECT:
			onChannelConnect(ses, msg);
			break;
		case LOGIN_WITH_TOKEN:
			onLoginWithToken(ses, msg);
			break;
		case LOGOUT:
			onLogout(ses, msg);
			break;
		case RPC_MESSAGE:
			onRPCMessage(ses, msg);
			break;
		default:
			msg.writeByte(ERROR);
			msg.writeString("Unsupported type: " + type);
		}
	}

	private void onLogin(ClientSession ses, RocketMessage msg) {
		msg.writeByte(LOGIN);
		int usage = msg.readInt();
		D3ELogger.info("Login: usage " + usage);
		String type = msg.readString();
		String email = msg.readString();
		String phone = msg.readString();
		String username = msg.readString();
		String password = msg.readString();
		String deviceToken = msg.readString();
		String token = msg.readString();
		String code = msg.readString();
		try {
			String q;
			if (StringUtils.isNotEmpty(code) && StringUtils.isNotEmpty(token)) {
				q = "loginWithOTP";
			} else if (StringUtils.isNotEmpty(email)) {
				// UserTypeWithEmailAndPassword
				q = "login" + type + "WithEmailAndPassword";
			} else if (StringUtils.isNotEmpty(phone)) {
				// UserTypeWithPhoneAndPassword
				q = "login" + type + "WithPhoneAndPassword";
			} else {
				// UserTypeWithUsernameAndPassword
				q = "login" + type + "WithUsernameAndPassword";
			}
			LoginResult res = query.login(q, email, phone, username, password, deviceToken, token, code);
			if (res.isSuccess()) {
				ses.userId = res.getUserObject().getId();
				ses.userType = res.getUserObject().getClass().getSimpleName();
			}
			msg.writeByte(0);
			TemplateUsage usageType = ses.template.getUsageType(usage);
			new RocketObjectDataFetcher(ses.template, msg, (t) -> fromTypeAndId(t)).fetch(usageType, res);
		} catch (ValidationFailedException e) {
			msg.writeByte(1);
			msg.writeStringList(e.getErrors());
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeStringList(ListExt.asList(e.getMessage()));
			e.printStackTrace();
		}
	}

	private void onLoginWithToken(ClientSession ses, RocketMessage msg) {
		msg.writeByte(LOGIN_WITH_TOKEN);
		D3ELogger.info("Login With Token");
		String token = msg.readString();
		UserProxy userProxy = jwtTokenUtil.validateToken(token);
		if (userProxy != null) {
			ses.userId = userProxy.userId;
			ses.userType = userProxy.type;
			msg.writeByte(0);
		} else {
			msg.writeByte(1);
		}
	}

	private void onLogout(ClientSession ses, RocketMessage msg) {
		msg.writeByte(LOGOUT);
		D3ELogger.info("Logout");
		User user = CurrentUser.get();
		if ((user == null) || (user instanceof AnonymousUser)) {
			msg.writeByte(1);
		} else {
			CurrentUser.clear();
			ses.logout();
			msg.writeByte(0);
		}
	}

	private void onRPCMessage(ClientSession ses, RocketMessage msg) {
		msg.writeByte(RPC_MESSAGE);
		int clsIdx = msg.readInt();
		int methodIdx = msg.readInt();
		TemplateClazz tc = ses.template.getRPCMethod(clsIdx);
		DClazzMethod message = tc.getMethods()[methodIdx];
		if (message == null) {
			msg.writeByte(1);
			msg.writeString("Method not found");
			return;
		}
		int msgSrvIdx = message.getIndex();
		RocketInputContext ctx = new RocketInputContext(helperService.getObject(), ses.template, msg);
		try {
			rpcHandler.handle(clsIdx, msgSrvIdx, ctx, msg);
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeString(e.getMessage());
			e.printStackTrace();
		}
	}

	private void onChannelMessage(ClientSession ses, RocketMessage msg) {
		int chIdx = msg.readInt();
		int msgIndex = msg.readInt();
		TemplateClazz tc = ses.template.getChannel(chIdx);
		DClazzMethod message = tc.getMethods()[msgIndex];
		if (message == null) {
			msg.writeByte(1);
			return;
		}
		int msgSrvIdx = message.getIndex();
		RocketInputContext ctx = new RocketInputContext(helperService.getObject(), ses.template, msg);
		try {
			channels.onMessage(tc.getClazz(), msgSrvIdx, ses, ctx);
			msg.writeByte(0);
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeString(e.getMessage());
			e.printStackTrace();
		}
	}

	private void onChannelConnect(ClientSession ses, RocketMessage msg) {
		D3ELogger.info("Connecting to channel");
		msg.writeByte(CONNECT);
		int chIdx = msg.readInt();
		TemplateClazz tc = ses.template.getChannel(chIdx);
		DClazz dm = tc.getClazz();
		try {
			boolean result = channels.connect(dm, ses, helperService.getObject(), ses.template);
			D3ELogger.info("Channel connect result: " + result);
			if (result) {
				msg.writeByte(0);
			} else {
				msg.writeByte(1);
				msg.writeString("Conenction refused");
			}
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeString(e.getMessage());
			e.printStackTrace();
		}
	}

	private void onUnsubscribe(ClientSession ses, RocketMessage msg) {
		// msg.writeByte(UNSUBSCRIBE);
		String subId = msg.readString();
		Map<String, Cancellable> map = subscriptions.get(ses);
		if (map == null) {
			return;
		}
		Cancellable subscription = map.remove(subId);
		if (subscription != null) {
			try {
				subscription.cancel();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void onDelete(ClientSession ses, RocketMessage msg) {
		msg.writeByte(DELETE);
		int type = msg.readInt();
		long id = msg.readLong();
		try {
			TemplateType tt = ses.template.getType(type);
			D3ELogger.info("Delete: " + tt.getModel().getType() + ", id: " + id);
			DBObject obj = fromTypeAndId(new TypeAndId(tt.getModel().getIndex(), id));
			if (obj instanceof CreatableObject) {
				mutation.delete((CreatableObject) obj, false);
				msg.writeByte(0);
			} else {
				msg.writeByte(1);
				msg.writeStringList(ListExt.asList("Must be Creatable Object"));
			}
		} catch (ValidationFailedException e) {
			msg.writeByte(1);
			msg.writeStringList(e.getErrors());
			throw new UnexpectedRollbackException("Delete failed");
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeStringList(ListExt.asList(e.getMessage()));
			e.printStackTrace();
			throw new UnexpectedRollbackException("Delete failed");
		}
	}

	public void sendChanges(ClientSession session, Map<DBObject, BitSet> objects) {
		// D3ELogger.info("Send changes: ");
		RocketMessage msg = new RocketMessage(session);
		msg.writeInt(OBJECTS);
		msg.writeBoolean(true);
		Template template = session.template;
		int count = objects.size();
		msg.writeInt(count);
		for (var entry : objects.entrySet()) {
			DBObject key = entry.getKey();
			// D3ELogger.info("Send changes: for " + key + ", id: " + key.getId() + " " +
			// entry.getValue());
			writeObject(msg, template, entry.getKey(), entry.getValue());
		}
		msg.flush();
	}

	public void sendEmbeddedChanges(ClientSession session, Map<DBObject, Map<DField, BitSet>> objects) {
		// D3ELogger.info("Send embedded changes: ");
		RocketMessage msg = new RocketMessage(session);
		msg.writeInt(OBJECTS);
		msg.writeBoolean(true);
		Template template = session.template;
		int count = objects.size();
		msg.writeInt(count);
		for (var entry : objects.entrySet()) {
			DBObject parent = entry.getKey();
			Map<DField, BitSet> embeddeds = entry.getValue();
			int typeIdx = template.toClientTypeIdx(parent._typeIdx());
			msg.writeInt(typeIdx);
			msg.writeLong(parent.getId());
			TemplateType type = template.getType(typeIdx);
			embeddeds.forEach((f, bits) -> {
				DBObject em = (DBObject) f.getValue(parent);
				int cfid = type.toClientIdx(f.getIndex());
				msg.writeInt(cfid);
				writeObject(msg, template, em, bits);
			});
			msg.writeInt(-1);
		}
		msg.flush();
	}

	private void writeObject(RocketMessage msg, Template template, DBObject object, BitSet fields) {
		int serverType = object._typeIdx();
		int typeIdx = template.toClientTypeIdx(serverType);
		TemplateType type = template.getType(typeIdx);
		msg.writeInt(typeIdx);
		if (!type.getModel().isEmbedded()) {
			msg.writeLong(object.getId());
		}
		fields.stream().forEach(b -> {
			int cidx = type.toClientIdx(b);
			if (cidx == -1) {
				return;
			}
			DField f = type.getField(cidx);
			if (f instanceof UnknownField) {
				return;
			}
			// D3ELogger.info("w field: " + f.getName());
			if (f.getType() == FieldType.InverseCollection || f.getType() == FieldType.PrimitiveCollection
					|| f.getType() == FieldType.ReferenceCollection) {
				Object val = f.getValue(object);
//				ListChanges listChanges = (ListChanges) object._oldValue(b);
//				if (listChanges != null) {
//					List<Change> changes = listChanges.compile((List) val);
//					if (changes.isEmpty()) {
//						D3ELogger.info("No coll changes");
//						return;
//					}
//					msg.writeInt(cidx);
//					writeListChanges(msg, template, changes, f);
//				} else {
				msg.writeInt(cidx);
				writeCompleteList(msg, template, (List) val, f);
//				}
			} else {
				Object val = f.getValue(object);
				if (f.getReference() != null && f.getReference().isEmbedded()) {
					if (val == null) {
						return;
					}
				}
				msg.writeInt(cidx);
				writeChangeVal(msg, template, f, val);
			}
		});
		msg.writeInt(-1);
	}

	@SuppressWarnings("unchecked")
	private void writeCompleteList(RocketMessage msg, Template template, List newColl, DField field) {
		// D3ELogger.info("List Change all: " + newColl.size());
		msg.writeInt(newColl.size());
		for (Object val : new ArrayList(newColl)) {
			if (field.getType() == FieldType.PrimitiveCollection) {
				msg.writePrimitiveField(val, field, template);
			} else {
				int type;
				long id;
				Object object = val;
				if (object instanceof TypeAndId) {
					type = ((TypeAndId) object).type;
					id = ((TypeAndId) object).id;
				} else {
					type = ((DBObject) object)._typeIdx();
					id = ((DBObject) object).getId();
				}
				int clientType = template.toClientTypeIdx(type);
				msg.writeInt(clientType);
				if (!field.getReference().isEmbedded()) {
					msg.writeLong(id);
				}
				msg.writeInt(-1);
			}
		}
	}

	private void writeListChanges(RocketMessage msg, Template template, List<Change> changes, DField field) {
		// D3ELogger.info("List Changes: " + changes.size() + ", " + changes);
		msg.writeInt(-changes.size());
		for (Change change : changes) {
			if (change.type == ListChanges.ChangeType.Added) {
				// D3ELogger.info("Added At: " + change.index);
				msg.writeInt(change.index + 1);
				if (field.getType() == FieldType.PrimitiveCollection) {
					msg.writePrimitiveField(change.obj, field, template);
				} else {
					Object object = change.obj;
					DBObject dbObj = null;
					if (object instanceof TypeAndId) {
						dbObj = fromTypeAndId((TypeAndId) object);
					} else {
						dbObj = (DBObject) object;
					}
					int clientType = template.toClientTypeIdx(dbObj._typeIdx());
					msg.writeInt(clientType);
					if (!field.getReference().isEmbedded()) {
						msg.writeLong(dbObj.getId());
					}
					msg.writeInt(-1);
				}
			} else {
				// D3ELogger.info("Removed At: " + change.index);
				msg.writeInt(-(change.index + 1));
			}
		}
	}

	private DBObject fromTypeAndId(TypeAndId ti) {
		String type = schema.getType(ti.type).getType();
		return helperService.getObject().get(type, ti.id);
	}

	private void writeChangeVal(RocketMessage msg, Template template, DField field, Object value) {
		if (field.getType() == FieldType.Primitive) {
			msg.writePrimitiveField(value, field, template);
		} else if (field.getType() == FieldType.Reference) {
			if (value == null) {
				msg.writeNull();
			} else {
				if (value instanceof TypeAndId) {
					TypeAndId ti = (TypeAndId) value;
					int typeIdx = template.toClientTypeIdx(ti.type);
					msg.writeInt(typeIdx);
					if (!field.getReference().isEmbedded()) {
						msg.writeLong(ti.id);
					}
					msg.writeInt(-1);
				} else if (value instanceof DFile) {
					DFile file = (DFile) value;
					RocketInputContext.writeDFile(msg, template, file);
					return;
				} else {
					DBObject dbObj = (DBObject) value;
					if (!field.getReference().isEmbedded()) {
						int typeIdx = template.toClientTypeIdx(dbObj._typeIdx());
						msg.writeInt(typeIdx);
						msg.writeLong(dbObj.getId());
						msg.writeInt(-1);
					} else {
						writeObject(msg, template, dbObj, dbObj._changes().changes);
					}
				}
			}
		} else {
			throw new RuntimeException("Unsupported type. " + value.getClass());
		}
	}

	private void onSave(ClientSession ses, RocketMessage msg) {
		msg.writeByte(SAVE);
		RocketInputContext ctx = new RocketInputContext(helperService.getObject(), ses.template, msg);
		DatabaseObject obj = (DatabaseObject) ctx.readObject();
		D3ELogger.info("Save: " + obj._type() + ", LID: " + obj.getLocalId() + ", ID: " + obj.getId());
		try {
			if (obj instanceof CreatableObject) {
				mutation.save((CreatableObject) obj);
				List<long[]> localIds = new ArrayList<>();
				obj.updateMasters(a -> {
					if (a.getId() == 0l) {
						a.setId(a.getLocalId());
					} else if (a.getLocalId() != 0l) {
						localIds.add(
								new long[] { ses.template.toClientTypeIdx(a._typeIdx()), a.getLocalId(), a.getId() });
					}
					a.setLocalId(0);
				});
				if (obj.getId() == 0l) {
					obj.setId(obj.getLocalId());
				} else if (obj.getLocalId() != 0l) {
					localIds.add(
							new long[] { ses.template.toClientTypeIdx(obj._typeIdx()), obj.getLocalId(), obj.getId() });
				}
				obj.setLocalId(0);
				msg.writeByte(0);
				msg.writeInt(localIds.size());
				localIds.forEach((v) -> {
					msg.writeInt((int) v[0]);
					msg.writeLong((int) v[1]);
					msg.writeLong((int) v[2]);
				});
				ctx.writeObject(obj);
			} else {
				msg.writeByte(1);
				msg.writeStringList(ListExt.asList("Only creatable objects can be saved"));
			}
		} catch (ValidationFailedException e) {
			msg.writeByte(1);
			msg.writeStringList(e.getErrors());
			throw new UnexpectedRollbackException("Save failed");
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeStringList(ListExt.asList(e.getMessage()));
			e.printStackTrace();
			throw new UnexpectedRollbackException("Save failed");
		}
	}

	private void onObjectQuery(ClientSession ses, RocketMessage msg) {
		msg.writeByte(OBJECT_QUERY);
		int type = msg.readInt();
		boolean subscribed = msg.readBoolean();
		int usageId = msg.readInt();
		D3ELogger.info("Object Query: type: " + type + " usage " + usageId);
		TemplateUsage usage = ses.template.getUsageType(usageId);
		RocketInputContext ctx = new RocketInputContext(helperService.getObject(), ses.template, msg);
		try {
			TemplateType tt = ses.template.getType(type);
			QueryResult queryRes = query.executeOperation("get" + tt.getModel().getType() + "ById",
					convertToField(usage, ses.template), ctx, subscribed, ses);
			msg.writeByte(0);
			if (subscribed) {
				Cancellable sub = queryRes.changeTracker;
				String subId = newSubId();

				Map<String, Cancellable> map = subscriptions.get(ses);
				if (map == null) {
					map = new HashMap<>();
					subscriptions.put(ses, map);
				}
				map.put(subId, sub);
				msg.writeString(subId);
			}
			writeQueryResult(queryRes, usage, ses.template, msg);
		} catch (ValidationFailedException e) {
			msg.writeByte(1);
			msg.writeStringList(e.getErrors());
		} catch (Exception e) {
			msg.writeByte(1);
			msg.writeStringList(ListExt.asList(e.getMessage()));
			e.printStackTrace();
		}
	}

	private void writeQueryResult(QueryResult res, TemplateUsage usage, Template template, RocketMessage msg) {
		if (res.external) {
			new RocketObjectDataFetcher(template, msg, (t) -> fromTypeAndId(t)).fetch(usage, res.value);
		} else {
			new RocketOutObjectFetcher(template, msg).fetch(usage, res.value);
		}
	}

	private void onDataQuery(ClientSession ses, RocketMessage msg) {
		msg.writeByte(DATA_QUERY);
		String query = msg.readString();
		boolean subscribed = msg.readBoolean();
		int usage = msg.readInt();
		D3ELogger.info("Data Query: " + query + ", usage " + usage);
		TemplateUsage tu = ses.template.getUsageType(usage);
		RocketInputContext ctx = new RocketInputContext(helperService.getObject(), ses.template, msg);
		try {
			Field field = convertToField(tu, ses.template);
			QueryResult res = this.query.executeOperation("get" + query, field, ctx, subscribed, ses);
			msg.writeByte(0);
			if (subscribed) {
				Cancellable sub = res.changeTracker;
				String subId = null;
				if (sub != null) {
					subId = newSubId();
					Map<String, Cancellable> map = subscriptions.get(ses);
					if (map == null) {
						map = new HashMap<>();
						subscriptions.put(ses, map);
					}
					map.put(subId, sub);
				}
				msg.writeString(subId);
			}
			writeQueryResult(res, tu, ses.template, msg);
		} catch (ValidationFailedException e) {
			msg.writeByte(0);
			if (subscribed) {
				msg.writeString(null);
			}
			writeDataQueryErrorResult(tu, ses.template, e.getErrors(), msg);
		} catch (Exception e) {
			msg.writeByte(0);
			if (subscribed) {
				msg.writeString(null);
			}
			writeDataQueryErrorResult(tu, ses.template, ListExt.asList(e.getMessage()), msg);
			e.printStackTrace();
		}
	}

	private void writeDataQueryErrorResult(TemplateUsage tu, Template template, List<String> errors,
			RocketMessage msg) {
		QueryResult res = new QueryResult();
		UsageType type = tu.getTypes()[0];
		TemplateType tt = template.getType(type.getType());
		DModel model = tt.getModel();
		res.value = model.newInstance();
		res.type = model.getType();
		res.external = true;
		model.getField("status").setValue(res.value, DBResultStatus.Errors);
		model.getField("errors").setValue(res.value, errors);
		writeQueryResult(res, tu, template, msg);
	}

	private String newSubId() {
		return UUID.randomUUID().toString();
	}

	private Field convertToField(TemplateUsage tu, Template template) {
		if (tu.getField() != null) {
			return tu.getField();
		}
		Field f = new Field();
		f.setSelections(createSelections(tu.getTypes(), template));
		tu.setField(f);
		return f;
	}

	private List<Selection> createSelections(UsageType[] types, Template template) {
		List<Selection> selections = new ArrayList<>();
		for (UsageType ut : types) {
			selections.add(createSelection(ut, template));
		}
		return selections;
	}

	private Selection createSelection(UsageType ut, Template template) {
		TemplateType type = template.getType(ut.getType());
		List<Field> fields = new ArrayList<>();
		for (UsageField uf : ut.getFields()) {
			try {
				DField<?, ?> field = type.getField(uf.getField());
				if (field instanceof UnknownField) {
					continue;
				}
				Field f = new Field();
				f.setField(field);
				f.setSelections(createSelections(uf.getTypes(), template));
				fields.add(f);
			} catch (Exception e) {
				D3ELogger.info("Unknown field: " + ut.getType() + ", " + uf.getField());
				e.printStackTrace();
			}
		}
		return new Selection(type.getModel(), fields);
	}

	private void onRestore(ClientSession ses, RocketMessage msg) {
		msg.writeByte(RESTORE);
		String sessionId = msg.readString();
		D3ELogger.info("Restore: " + sessionId);
		ClientSession disSes = disconnectedSessions.remove(sessionId);
		if (disSes == null) {
			msg.writeByte(1);
		} else {
			msg.writeByte(0);
			msg.writeString(ses.getId()); // New id
			sessions.put(ses.getId(), disSes);
			try {
				disSes.setSession(ses.getSession());
			} catch (IOException e) {
				msg.writeByte(1);
				e.printStackTrace();
				return;
			}
		}
	}

	private void onConfirmTemplate(ClientSession ses, RocketMessage msg) {
		msg.writeByte(CONFIRM_TEMPLATE);
		msg.writeString(ses.getId());
		String templateHash = msg.readString();
		long timeOut = msg.readLong();
		if (timeOut < 0) {
			ses.setTimeOut(RECONNECT_TIMEOUT);
		} else {
			ses.setTimeOut(timeOut < RECONNECT_TIMEOUT ? timeOut : RECONNECT_TIMEOUT);
		}
		if (templateManager.hasTemplate(templateHash)) {
			ses.template = templateManager.getTemplate(templateHash);
			msg.writeByte(0);
			D3ELogger.info("Template matched: " + templateHash);
		} else {
			msg.writeByte(1);
			D3ELogger.info("Template not matched: " + templateHash);
		}
	}

	private void onHashCheck(ClientSession ses, RocketMessage msg) {
		msg.writeByte(HASH_CHECK);
		int types = msg.readInt();
		int usages = msg.readInt();
		int channels = msg.readInt();
		int rpcs = msg.readInt();
		ses.template = new Template(types, usages, channels, rpcs);

		// Types
		D3ELogger.info("Registering types: " + types);
		List<Integer> unknownTypes = new ArrayList<>();
		for (int i = 0; i < types; i++) {
			String typeHash = msg.readString();
			TemplateType tt = master.getTemplateType(typeHash);
			if (tt == null) {
				unknownTypes.add(i);
			} else {
				ses.template.setTypeTemplate(i, tt);
			}
		}
		D3ELogger.info("Unknown types: " + unknownTypes.size());

		// Usages
		D3ELogger.info("Registering usages: " + usages);
		List<Integer> unknownUsages = new ArrayList<>();
		for (int i = 0; i < usages; i++) {
			String usageHash = msg.readString();
			TemplateUsage ut = master.getUsageTemplate(usageHash);
			ses.template.setUsageTemplate(i, ut);
			if (ut == null) {
				unknownUsages.add(i);
			}
		}
		D3ELogger.info("Unknown usages: " + unknownUsages.size());

		// Channels
		D3ELogger.info("Registering channels: " + channels);
		List<Integer> unknownChannels = new ArrayList<>();
		for (int i = 0; i < channels; i++) {
			String channelHash = msg.readString();
			TemplateClazz tc = master.getChannelTemplate(channelHash);
			if (tc == null) {
				D3ELogger.info("Unknown channel: " + i);
				unknownChannels.add(i);
			} else {
				ses.template.setChannelTemplate(i, tc);
			}
		}
		D3ELogger.info("Unknown channels: " + unknownChannels.size());

		// RPC
		D3ELogger.info("Registering RPCs: " + rpcs);
		List<Integer> unknownRPCs = new ArrayList<>();
		for (int i = 0; i < rpcs; i++) {
			String rpcHash = msg.readString();
			TemplateClazz tc = master.getChannelTemplate(rpcHash);
			if (tc == null) {
				D3ELogger.info("Unknown RPC Class: " + i);
				unknownRPCs.add(i);
			} else {
				ses.template.setRPCTemplate(i, tc);
			}
		}
		D3ELogger.info("Unknown : " + unknownRPCs.size());

		if (unknownTypes.isEmpty() && unknownUsages.isEmpty() && unknownChannels.isEmpty()) {
			msg.writeByte(0);
			computeTemplateMD5AndAddToManager(ses.template);
		} else {
			msg.writeByte(1);
			msg.writeIntegerList(unknownTypes);
			msg.writeIntegerList(unknownUsages);
			msg.writeIntegerList(unknownChannels);
			msg.writeIntegerList(unknownRPCs);
		}
	}

	private void computeTemplateMD5AndAddToManager(Template template) {
		List<String> md5 = new ArrayList<>();
		for (TemplateType tt : template.getTypes()) {
			md5.add(tt.getHash());
		}
		for (TemplateUsage tu : template.getUsages()) {
			md5.add(tu.getHash());
		}
		for (TemplateClazz tc : template.getChannels()) {
			md5.add(tc.getHash());
		}
		template.setHash(MD5Util.md5(md5));
		templateManager.addTemplate(template);
		D3ELogger.info("Template created: " + template.getHash());
	}

	private void onTypeExchange(ClientSession ses, RocketMessage msg) {
		msg.writeByte(TYPE_EXCHANGE);
		Template template = ses.template;
		// Types
		int typesCount = msg.readInt();
		TemplateType[] templateTypes = new TemplateType[typesCount];
		List<Integer> unknownTypes = new ArrayList<>();
		Map<Integer, List<Integer>> typesWithUnknownFields = new HashMap<>();
		int[] parents = new int[typesCount];
		for (int i = 0; i < typesCount; i++) {
			int idx = msg.readInt();
			String type = msg.readString();
			int parent = msg.readInt();
			parents[i] = parent;
			DModel<?> md = schema.getType(type);
			if (md != null) {
				int fieldsCount = msg.readInt();
				TemplateType tt = new TemplateType(md, fieldsCount);
				template.setTypeTemplate(idx, tt);
				for (int j = 0; j < fieldsCount; j++) {
					String field = msg.readString();
					int typeIdx = msg.readInt();
					// TODO check type
					DField<?, ?> df = md.getField(field);
					if (df == null) {
						List<Integer> unknownFields = typesWithUnknownFields.get(idx);
						if (unknownFields == null) {
							unknownFields = new ArrayList<>();
							typesWithUnknownFields.put(idx, unknownFields);
						}
						unknownFields.add(j);
						df = new UnknownField(field);
					}
					tt.addField(j, df);
				}
				templateTypes[i] = tt;
			} else {
				List<String> md5 = new ArrayList<>();
				md5.add(type);
				int fieldsCount = msg.readInt();
				for (int j = 0; j < fieldsCount; j++) {
					String f = msg.readString();
					msg.readInt();
					md5.add(f);
				}
				TemplateType tt = new TemplateType(md, fieldsCount);
				tt.valid = false;
				String hash = MD5Util.md5(md5);
				tt.setHash(hash);
				template.setTypeTemplate(idx, tt);
				templateTypes[i] = tt;
				unknownTypes.add(idx);
			}
		}

		// Update Parents
		for (int i = 0; i < typesCount; i++) {
			TemplateType tt = templateTypes[i];
			int parent = parents[i];
			if (parent != 0) {
				tt.setParent(template.getType(parent));
			}
			tt.computeHash();
		}
		for (int i = 0; i < typesCount; i++) {
			TemplateType tt = templateTypes[i];
			if (tt.valid) {
				master.addTypeTemplate(tt);
			}
		}

		// Usage
		int usageCount = msg.readInt();
		for (int i = 0; i < usageCount; i++) {
			int idx = msg.readInt();
			int types = msg.readInt();
			UsageType[] tus = new UsageType[types];
			for (int j = 0; j < types; j++) {
				UsageType ut = createUsageType(msg);
				tus[j] = ut;
			}
			TemplateUsage tu = new TemplateUsage(tus);
			template.setUsageTemplate(idx, tu);
			master.addUsageTemplate(tu, template);
		}

		// Channels
		int channelCount = msg.readInt();
		for (int i = 0; i < channelCount; i++) {
			int idx = msg.readInt();
			String name = msg.readString();
			// Check if channel with this name exists
			DClazz channel = schema.getChannel(name);
			if (channel != null) {
//				D3ELogger.info("Channel found: " + name);
				int msgCount = msg.readInt();
				TemplateClazz tc = new TemplateClazz(channel, msgCount);
				for (int j = 0; j < msgCount; j++) {
					String messageName = msg.readString();
					DClazzMethod message = channel.getMethod(messageName);
					boolean paramNotFound = false;

					if (message == null) {
//						D3ELogger.info("Message not found: " + messageName);
						// TODO: Add empty message and continue

						// TODO: Do we need this Hash?
						List<String> md5 = new ArrayList<>();
						md5.add(messageName);
						int argCount = msg.readInt();
						for (int k = 0; k < argCount; j++) {
							int type = msg.readInt();
							boolean collection = msg.readBoolean();
							DModel<?> dm = template.getType(type).getModel();
							md5.add(dm.getType());
						}
						tc.addMethod(j, message);
						continue;
					} else {
//						D3ELogger.info("Message found: " + messageName);
						int paramCount = msg.readInt();
						for (int k = 0; k < paramCount; k++) {
							// Getting the parameter types of the method. These are needed for constructing
							// the hash
							int type = msg.readInt();
							boolean collection = msg.readBoolean();
							TemplateType tt = template.getType(type);
							DModel<?> paramType = tt.getModel();
							if (paramType == null) {
								// TODO: Collect the rest, add empty message
								D3ELogger.info("Param not found: " + k);
								paramNotFound = true;
								break;
							}

							if (!paramNotFound) {
								message.addParam(k, new DParam(type, collection));
							}
						}
					}
					tc.addMethod(j, paramNotFound ? null : message);
				}
				template.setChannelTemplate(idx, tc);
				master.addChannelTemplate(tc, template);
			} else {
				// Reject completely, or do what type is doing?
				List<String> md5 = new ArrayList<>();
				md5.add(name);
				int methodsCount = msg.readInt();
				for (int j = 0; j < methodsCount; j++) {
					String methodName = msg.readString();
					md5.add(methodName);
					int paramsCount = msg.readInt();
					for (int k = 0; k < paramsCount; k++) {
						int type = msg.readInt();
						String f = template.getType(type).getModel().getType();
						msg.readBoolean();
						md5.add(f);
					}
				}
				TemplateClazz tt = new TemplateClazz(channel, methodsCount);
				String hash = MD5Util.md5(md5);
				tt.setHash(hash);
				template.setChannelTemplate(idx, tt);
			}
		}

		// RPC
		int rpcCount = msg.readInt();
		for (int i = 0; i < rpcCount; i++) {
			int idx = msg.readInt();
			String name = msg.readString();
			// Check if RPC Class with this name exists
			DClazz rpcClass = schema.getRPC(name);
			if (rpcClass != null) {
//				D3ELogger.info("RPC Class found: " + name);
				int methodCount = msg.readInt();
				TemplateClazz tc = new TemplateClazz(rpcClass, methodCount);
				for (int j = 0; j < methodCount; j++) {
					String rpName = msg.readString();
					DClazzMethod message = rpcClass.getMethod(rpName);
					boolean paramNotFound = false;

					if (message == null) {
//						D3ELogger.info("Remote Procedure not found: " + rpName);
						// TODO: Add empty message and continue

						// TODO: Do we need this Hash?
						List<String> md5 = new ArrayList<>();
						md5.add(rpName);
						int argCount = msg.readInt();
						for (int k = 0; k < argCount; j++) {
							int type = msg.readInt();
							boolean collection = msg.readBoolean();
							DModel<?> dm = template.getType(type).getModel();
							md5.add(dm.getType());
						}
						int returnType = msg.readInt();
						DModel<?> dm = template.getType(returnType).getModel();
						md5.add(dm.getType());
						// TODO: Consider collection in this hash?
						boolean returnColl = msg.readBoolean();
						tc.addMethod(j, message);
						continue;
					} else {
//						D3ELogger.info("Remote Procedure found: " + rpName);
						int paramCount = msg.readInt();
						for (int k = 0; k < paramCount; k++) {
							// Getting the parameter types of the method. These are needed for constructing
							// the hash
							int type = msg.readInt();
							boolean collection = msg.readBoolean();
							TemplateType tt = template.getType(type);
							DModel<?> paramType = tt.getModel();
							if (paramType == null) {
								// TODO: Collect the rest, add empty message
								D3ELogger.info("Param not found: " + k);
								paramNotFound = true;
								break;
							}

							if (!paramNotFound) {
								message.addParam(k, new DParam(type, collection));
							}
						}

						int returnType = msg.readInt();
						boolean returnColl = msg.readBoolean();
						message.setReturnType(returnType, returnColl);
					}
					tc.addMethod(j, paramNotFound ? null : message);
				}
				template.setRPCTemplate(idx, tc);
				master.addRPCTemplate(tc, template);
			} else {
				// Reject completely, or do what type is doing?
				List<String> md5 = new ArrayList<>();
				md5.add(name);
				int methodsCount = msg.readInt();
				for (int j = 0; j < methodsCount; j++) {
					String methodName = msg.readString();
					md5.add(methodName);
					int paramsCount = msg.readInt();
					for (int k = 0; k < paramsCount; k++) {
						int type = msg.readInt();
						String f = template.getType(type).getModel().getType();
						msg.readBoolean();
						md5.add(f);
					}
					int returnType = msg.readInt();
					String f = template.getType(returnType).getModel().getType();
					msg.readBoolean();
					md5.add(f);
				}
				TemplateClazz tt = new TemplateClazz(rpcClass, methodsCount);
				String hash = MD5Util.md5(md5);
				tt.setHash(hash);
				template.setRPCTemplate(idx, tt);
			}
		}

		computeTemplateMD5AndAddToManager(ses.template);
		msg.writeIntegerList(unknownTypes);
		msg.writeInt(typesWithUnknownFields.size());
		typesWithUnknownFields.forEach((idx, fields) -> {
			msg.writeInt(idx);
			msg.writeIntegerList(fields);
		});
	}

	private UsageType createUsageType(RocketMessage msg) {
		int typeIdx = msg.readInt();
		int fieldsCount = msg.readInt();
		UsageType ut = new UsageType(typeIdx, fieldsCount);
		for (int j = 0; j < fieldsCount; j++) {
			int f = msg.readInt();
			int refs = msg.readInt();
			UsageType[] tus = new UsageType[refs];
			for (int k = 0; k < refs; k++) {
				UsageType ref = createUsageType(msg);
				tus[k] = ref;
			}
			UsageField uf = new UsageField(f, tus);
			ut.getFields()[j] = uf;
		}
		return ut;
	}

	public void sendDelete(ClientSession session, List<TypeAndId> objects) {
		// D3ELogger.info("Send Deletes: ");
		RocketMessage msg = new RocketMessage(session);
		msg.writeInt(OBJECTS);
		msg.writeBoolean(false);
		Template template = session.template;
		int count = objects.size();
		msg.writeInt(count);
		for (var entry : objects) {
			msg.writeInt(template.toClientTypeIdx(entry.type));
			msg.writeLong(entry.id);
		}
		msg.flush();

	}
}

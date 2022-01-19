package d3e.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;

public class GraphQLSubscriptionClient implements WebSocketHandler, FlowableOnSubscribe<SubscriptionEvent> {
	private ConnectableFlowable<SubscriptionEvent> flowable;
	private FlowableEmitter<SubscriptionEvent> emitter;
	private long subscriptionId;
	private Map<String, SubscriptionConext> subscriptions = new HashMap<>();
	private WebSocketSession session;
	private ObjectMapper mapper;
	private StringBuilder partialPayload;
	private ConnectionStatus status;
	private String url;
	private long secondsToReconnect;

	private enum ConnectionStatus {
		DISCONNECTED, IN_PROGRESS, CONNECTED,
	}

	public GraphQLSubscriptionClient(ObjectMapper mapper, String url) {
		this.mapper = mapper;
		this.url = url;
		this.flowable = Flowable.create(this, BackpressureStrategy.BUFFER).publish();
		this.flowable.connect();
		this.partialPayload = new StringBuilder();
		this.status = ConnectionStatus.DISCONNECTED;
		this.secondsToReconnect = 1;
	}

	private void init() {
		status = ConnectionStatus.IN_PROGRESS;
		WebSocketClient socketClient = new StandardWebSocketClient();
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		headers.add("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
		headers.add("Sec-WebSocket-Key", "3g2c52ziIQqA5RRBNYviTQ==");
		headers.add("Sec-WebSocket-Protocol", "graphql-ws");
		headers.add("Sec-WebSocket-Version", "13");
		try {
			ListenableFuture<WebSocketSession> future = socketClient.doHandshake(this, headers, new URI(url));
			future.addCallback(s -> {
			}, (s) -> {
				status = ConnectionStatus.DISCONNECTED;
				tryToReconnect();
			});
		} catch (URISyntaxException e) {
			status = ConnectionStatus.DISCONNECTED;
			e.printStackTrace();
		}
	}

	public Object subscribe(SubscriptionConext conext) {
		String id = String.valueOf(++subscriptionId);
		Flowable<Object> stream = this.flowable.filter((e) -> e.getId().equals(id)).map(e -> e.getObj());
		conext.setId(id);
		subscriptions.put(id, conext);
		checkAndConnect(conext);
		return stream.doOnCancel(() -> {
			SubscriptionConext remove = subscriptions.remove(id);
			unsubscribeInternal(remove.getId());
		});
	}

	@Override
	public void subscribe(FlowableEmitter<SubscriptionEvent> emitter) throws Throwable {
		this.emitter = emitter;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.session = session;
		status = ConnectionStatus.CONNECTED;
		secondsToReconnect = 1;
		try {
			JSONObject init = new JSONObject();
			init.put("type", "connection_init");
			init.put("payload", new JSONObject());
			session.sendMessage(new TextMessage(init.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		new ArrayList<>(subscriptions.values()).forEach(s -> subscribeInternal(s));
	}

	private void unsubscribeInternal(String id) {
		if (status != ConnectionStatus.CONNECTED) {
			return;
		}
		try {
			JSONObject start = new JSONObject();
			start.put("id", id);
			start.put("type", "stop");
			session.sendMessage(new TextMessage(start.toString()));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void subscribeInternal(SubscriptionConext ctx) {
		if (status != ConnectionStatus.CONNECTED) {
			return;
		}
		try {
			JSONObject start = new JSONObject();
			start.put("id", ctx.getId());
			start.put("type", "start");
			start.put("payload", new JSONObject(ctx.getInput()));
			String total = start.toString();
			int limit = session.getTextMessageSizeLimit();
			while (true) {
				if (total.length() < limit) {
					session.sendMessage(new TextMessage(total));
					break;
				}
				String first = total.substring(0, limit);
				session.sendMessage(new TextMessage(first, false));
				total = total.substring(limit);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void checkAndConnect(SubscriptionConext conext) {
		if (status == ConnectionStatus.DISCONNECTED) {
			init();
		} else {
			subscribeInternal(conext);
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			partialPayload.append((String) message.getPayload());
			if (!message.isLast()) {
				return;
			}
			String completePayload = partialPayload.toString();
			partialPayload = new StringBuilder();
			JsonNode tree = mapper.readTree(completePayload);
			String type = tree.get("type").asText();
			if (!type.equals("data")) {
				return;
			}
			String id = tree.get("id").asText();
			SubscriptionConext ctx = subscriptions.get(id);
			if(ctx == null) {
				unsubscribeInternal(id);
				return;
			}
			JsonNode payload = tree.get("payload");
			JsonNode data = payload.get("data");
			JsonNode action = data.get("action");
			Object obj;
			if(action == null) {
				Iterator<JsonNode> iterator = data.iterator();
				iterator.hasNext();
				JsonNode next = iterator.next();
				obj = mapper.treeToValue(next, ctx.getReadType());
			} else {
				obj = mapper.treeToValue(action, ctx.getReadType());
			}
			SubscriptionEvent event = new SubscriptionEvent(id, obj);
			emitter.onNext(event);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		exception.printStackTrace(System.err);
		if (status == ConnectionStatus.IN_PROGRESS) {
			return;
		}
		status = ConnectionStatus.DISCONNECTED;
		tryToReconnect();
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		this.session = null;
		if (status == ConnectionStatus.IN_PROGRESS) {
			return;
		}
		status = ConnectionStatus.DISCONNECTED;
		tryToReconnect();
	}

	private void tryToReconnect() {
		if (status == ConnectionStatus.IN_PROGRESS) {
			return;
		}
		try {
			TimeUnit.SECONDS.sleep(secondsToReconnect);
			secondsToReconnect *= 2;
			init();
		} catch (InterruptedException e) {
			tryToReconnect();
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return true;
	}
}

package rest.ws;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import d3e.core.D3ELogger;
import d3e.core.D3ESubscription;
import d3e.core.DFile;
import d3e.core.IterableExt;
import d3e.core.ListExt;
import d3e.core.MapExt;
import d3e.core.SchemaConstants;
import d3e.core.StructBase;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldType;
import gqltosql.schema.IModelSchema;
import gqltosql2.Field;
import gqltosql2.OutObject;
import gqltosql2.Selection;
import io.reactivex.rxjava3.disposables.Disposable;
import lists.TypeAndId;
import security.AppSessionProvider;
import security.UserProxy;
import store.D3EEntityManagerProvider;
import store.D3EPrimaryCache;
import store.DBChange;
import store.DBObject;
import store.DataStoreEvent;
import store.DatabaseObject;
import store.EntityHelperService;
import store.ListChanges;
import store.StoreEventType;

@Service
public class DataChangeTracker implements Runnable{
	@Autowired
	D3EWebsocket socket;

	@Autowired
	private IModelSchema schema;

	@Autowired
	private ObjectFactory<EntityHelperService> helperService;
	
	@Autowired
	D3EEntityManagerProvider emProvider;

	private LinkedBlockingQueue<_Event> eventQueue = new LinkedBlockingQueue<>();
	
	private boolean shutdown = false;

	List<Key> keys = new ArrayList<>();
	Map<Integer, Map<Long, ObjectInterests>> perObjectListeners = new HashMap<>();
	Map<Integer, Vector<ObjectListener>> perTypeListeners = new HashMap<>();
	
	@org.springframework.beans.factory.annotation.Autowired
	private AppSessionProvider sessionProvider;

	static class ObjectInterests {
		Vector<ObjectListener> fieldListeners = new Vector<>();
		Vector<ObjectUsage> refListeners = new Vector<>();

		public boolean isEmpty() {
			return fieldListeners.isEmpty() && refListeners.isEmpty();
		}
	}

	static class ObjectUsage {
		Field field;
		DisposableListener listener;
		int parentType;
		long parentId;
		int fieldIdx;

		public ObjectUsage(int parentType, long parentId, int fieldIdx, Field field, DisposableListener listener) {
			this.parentType = parentType;
			this.parentId = parentId;
			this.fieldIdx = fieldIdx;
			this.field = field;
			this.listener = listener;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectUsage) {
				ObjectUsage other = (ObjectUsage) obj;
				return Objects.equals(other.listener, listener) && Objects.equals(other.field, field);
			}
			return false;

		}

		@Override
		public int hashCode() {
			return Objects.hash(field, listener);
		}
	}

	static class ObjectListener {
		BitSet fields;
		Disposable listener;
		Field field;

		public ObjectListener(BitSet fields, Disposable listener, Field field) {
			this.fields = fields;
			this.listener = listener;
			this.field = field;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectListener) {
				ObjectListener other = (ObjectListener) obj;
				return Objects.equals(other.listener, listener) && Objects.equals(other.fields, fields)
						&& Objects.equals(other.field, field);
			}
			return false;

		}

		@Override
		public int hashCode() {
			return Objects.hash(fields, listener, field);
		}
	}

	static class Key {
		final int type;
		final BitSet fields;

		Key(int type, BitSet fields) {
			this.type = type;
			this.fields = fields;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				Key other = (Key) obj;
				return other.type == type && Objects.equals(other.fields, this.fields);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return type + fields.hashCode();
		}
	}

	class DisposableListener implements Disposable {
		boolean disposed;
		ClientSession session;
		private Field field;
		private TypeAndId obj;
		List<TypeAndId> objects = new ArrayList<>();
		Set<Integer> types = new HashSet<>();

		public DisposableListener(Field field, TypeAndId obj, ClientSession session) {
			this.session = session;
			this.field = field;
			this.obj = obj;
		}

		@Override
		public void dispose() {
			disposed = true;
			onDispose(this);
		}

		@Override
		public boolean isDisposed() {
			return disposed;
		}

	}

	class TypeListener implements Disposable {
		Set<Integer> types = new HashSet<>();
		boolean disposed;
		BiConsumer<DBObject, StoreEventType> listener;
		BitSet fields;
		int type;

		TypeListener(int type, BitSet fields, BiConsumer<DBObject, StoreEventType> listener) {
			this.listener = listener;
			this.type = type;
			this.fields = fields;
		}

		@Override
		public void dispose() {
			disposed = true;
			onDispose(this);
		}

		@Override
		public boolean isDisposed() {
			return disposed;
		}

	}

	enum _EventType {
		DisposeDl, DisposeTl, ListenType, Fire, Fire2, Scan,
	}

	class _Event {
		_EventType type;
		TypeListener tl;
		DisposableListener dl;
		Object obj;
		Field field;
		DBObject obj2;
		StoreEventType changeType;
		UserProxy userProxy;
		public D3EPrimaryCache cache;
		public Map<DBObject,  DBChange> changes;
	}

	@PostConstruct
	public void init() {		
		new Thread(this).start();
	}
	
	@PreDestroy
	public void dispose() {
		shutdown = true;
	}

	public Disposable listen(Object obj, Field field, ClientSession session) {
		int type = 0;
		long id = 0;
		if (obj instanceof OutObject) {
			OutObject outObject = (OutObject) obj;
			type = outObject.getType();
			id = outObject.getId();
		} else if (obj instanceof TypeAndId) {
			TypeAndId typeId = (TypeAndId) obj;
			type = typeId.type;
			id = typeId.id;
			obj = fromTypeAndId(typeId);
		} else {
			DBObject dbObj = (DBObject) obj;
			type = dbObj._typeIdx();
			id = dbObj.getId();
		}
		TypeAndId typeAndId = new TypeAndId(type, id);
		DisposableListener dl = new DisposableListener(field, typeAndId, session);
		_Event event = new _Event();
		event.type = _EventType.Scan;
		event.dl =dl;
		event.field = field;
		event.obj = obj;
		event.userProxy = sessionProvider.getCurrentUserProxy();
		event.cache = (D3EPrimaryCache) emProvider.get().getCache();
		eventQueue.add(event);
		return dl;
	}
	
	@SuppressWarnings("unchecked")
	public void run () {
		while(!shutdown) {
			try {
				_Event event = eventQueue.take();
				switch(event.type) {
				case Scan:
				{
					emProvider.create(event.cache);
					sessionProvider.setUserProxy(event.userProxy);
					scan(null, null, event.obj, event.field, event.dl, null, null, MapExt.Map());
					emProvider.clear();
					break;
				}
				case DisposeTl:
					doOnDispose(event.tl);
					break;
				case DisposeDl:
					doOnDispose(event.dl);
					break;
				case ListenType:
					doOnlisten(event.tl);
					break;
				case Fire:
				{
					emProvider.create(event.cache);
					sessionProvider.setUserProxy(event.userProxy);
					doFire(event.obj2, event.changeType, event.changes);
					emProvider.clear();
					break;
				}
				case Fire2:
				{
					emProvider.create(event.cache);
					sessionProvider.setUserProxy(event.userProxy);
					List<DataStoreEvent> events = (List<DataStoreEvent>) event.obj;
					events.forEach(ev -> {
						if(ev.getEntity() instanceof DBObject) {
							doFire((DBObject) ev.getEntity(), ev.getType(), event.changes);													
						}
					});
					emProvider.clear();
					break;
				}
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	private void doOnDispose(TypeListener tl) {
		int type = tl.type;
		Vector<ObjectListener> fieldListeners = perTypeListeners.get(type);
		if (fieldListeners == null) {
			return;
		}
		fieldListeners.remove(new ObjectListener(tl.fields, tl, null));
	}

	public void onDispose(TypeListener tl) {
		_Event event = new _Event();
		event.type = _EventType.DisposeTl;
		event.tl = tl;
		eventQueue.add(event);
	}

	private void doOnDispose(DisposableListener dl) {
		for (TypeAndId ti : dl.objects) {
			Map<Long, ObjectInterests> objectListeners = perObjectListeners.get(ti.type);
			if (objectListeners != null) {
				ObjectInterests ol = objectListeners.get(ti.id);
				if (ol != null) {
					ListExt.removeWhere(ol.fieldListeners, (fl) -> fl.listener.isDisposed());
					if (ol.fieldListeners.isEmpty()) {
						objectListeners.remove(ti.id);
					}
					ListExt.removeWhere(ol.refListeners, (fl) -> fl.listener.isDisposed());
				}
			}
		}
		for (int type : dl.types) {
			Vector<ObjectListener> fieldListeners = perTypeListeners.get(type);
			if (fieldListeners != null) {
				ListExt.removeWhere(fieldListeners, (ol) -> ol.listener.isDisposed());
				if (fieldListeners.isEmpty()) {
					perTypeListeners.remove(type);
				}
			}
		}
	}
	
	public void onDispose(DisposableListener dl) {
		_Event event = new _Event();
		event.type = _EventType.DisposeDl;
		event.dl = dl;
		eventQueue.add(event);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scan(DBObject parent, DField parentField, Object obj, Field field, DisposableListener dl,
			ObjectsToSend toSend, List<Integer> allParents, Map<DBObject, DBChange> changes) {
		DBChange dbChange = changes.get(obj);
		if(dbChange != null  && dbChange.changes.isEmpty()) {
			return;
		}
		int type = 0;
		long id = 0;
		DBObject dbObj = null;
		if (obj instanceof OutObject) {
			OutObject outObject = (OutObject) obj;
			type = outObject.getType();
			id = outObject.getId();
		} else if (obj instanceof TypeAndId) {
			TypeAndId typeId = (TypeAndId) obj;
			type = typeId.type;
			id = typeId.id;
			obj = fromTypeAndId(typeId);
		} else {
			dbObj = (DBObject) obj;
			type = dbObj._typeIdx();
			if (obj instanceof StructBase) {
				type = ((StructBase) obj)._actualType();
			}
			id = dbObj.getId();
		}
		DModel<?> model = this.schema.getType(type);
		if (model.isEmbedded()) {
			return;
		}
		DModel<?> temp = model;
		BitSet fieldSet = new BitSet(model.getFieldsCount());
		List<Field> fields = ListExt.List();
		while(temp != null) {
			Selection sel = field.getSelectionForType(temp.getIndex());
			if(sel != null) {				
				fieldSet.or(sel.getFieldsSet());
				fields.addAll(sel.getFields());
			}
			temp = temp.getParent();
		}
		if (fields.isEmpty()) {
			return;
		}
		Map<Long, ObjectInterests> perObj = perObjectListeners.get(type);
		if (perObj == null) {
			perObj = new HashMap<>();
			perObjectListeners.put(type, perObj);
		}
		ObjectInterests interests = perObj.get(id);
		if (interests == null) {
			interests = new ObjectInterests();
			perObj.put(id, interests);
		}
		ObjectListener ol = new ObjectListener(fieldSet, dl, field);
		interests.fieldListeners.add(ol);
		if (parent == null) {
			interests.refListeners.add(new ObjectUsage(-1, -1, -1, field, dl));
		} else {
			interests.refListeners
					.add(new ObjectUsage(parent._typeIdx(), parent.getId(), parentField.getIndex(), field, dl));
		}
		TypeAndId typeAndId = new TypeAndId(type, id);
		if (!dl.objects.contains(typeAndId)) {
			dl.objects.add(typeAndId);
		}
		dl.types.add(type);
		if (toSend != null && dbObj != null) {
			BitSet set = field.getBitSet(allParents);
			toSend.add(dl.session, dbObj, set);
		}
//		DModel<?> objType = schema.getType(type);
//		 D3ELogger.info("WATCHING " + SEL.GETTYPE().GETTYPE() + " ID: " + ID + " FIELDS: "
//		 + LISTEXT.MAP(SEL.GETFIELDS(), (F) -> F.GETField().getName()) + " Object Type: " + objType.getType() + " Type : " + type);
		for (Field f : fields) {
			DField dField = f.getField();
			if (dField.getReference() == null || dField.getReference().getType().equals("DFile")) {
				continue;
			}
			FieldType fieldType = dField.getType();
			if (fieldType == FieldType.Reference) {
				Object value;
				if (obj instanceof OutObject) {
					OutObject outObject = (OutObject) obj;
					value = outObject.getFields().get(dField.getName());
				} else {
					value = dField.getValue(obj);
				}
				if (value != null && !(value instanceof DFile)) {
					if (toSend == null) {
						scan(dbObj, dField, value, f, dl, null, null, changes);
					} else {
						DModel dm = dField.getReference();
						List<Integer> allParents2 = new ArrayList<Integer>();
						dm.addAllParents(allParents2);
						allParents2.add(dm.getIndex());
						scan(dbObj, dField, value, f, dl, toSend, allParents2, changes);
					}

				}
			} else if (fieldType == FieldType.ReferenceCollection || fieldType == FieldType.InverseCollection) {
				List value = null;
				if (obj instanceof OutObject) {
					OutObject outObject = (OutObject) obj;
					value = (List) outObject.getFields().get(dField.getName());
				} else {
					value = (List) dField.getValue(obj);
				}
				if (value != null && !value.isEmpty()) {
					if (toSend == null) {
						for (Object o : value) {
							scan(dbObj, dField, o, f, dl, null, null, changes);
						}
					} else {
						DModel dm = dField.getReference();
						List<Integer> allParents2 = new ArrayList<Integer>();
						dm.addAllParents(allParents2);
						allParents2.add(dm.getIndex());
						for (Object o : value) {
							scan(dbObj, dField, o, f, dl, toSend, allParents2, changes);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void remove(DBObject obj, ObjectUsage ou) {
		int type = obj._typeIdx();
		long id = obj.getId();
		Selection sel = ou.field.getSelectionForType(type);
		if (sel.getFields().isEmpty()) {
			return;
		}
		Map<Long, ObjectInterests> perObj = perObjectListeners.get(type);
		if (perObj == null) {
			return;
		}
		ObjectInterests interests = perObj.get(id);
		if (interests == null) {
			return;
		}
		BitSet fieldsSet = sel.getFieldsSet();
		interests.fieldListeners.remove(new ObjectListener(fieldsSet, ou.listener, ou.field));
		interests.refListeners.remove(ou);
		//D3ELogger.info("Un Watching " + sel.getType().getType() + " ID: " + id + " Fields: "
		//		+ ListExt.map(sel.getFields(), (f) -> f.getField().getName()));
		for (Field f : sel.getFields()) {
			DField dField = f.getField();
			if (dField.getReference() == null || dField.getReference().getType().equals("DFile")) {
				continue;
			}
			int fieldIndex = dField.getIndex();
			FieldType fieldType = dField.getType();
			if (fieldType == FieldType.Reference) {
				DBObject value = (DBObject) dField.getValue(obj);
				if (value != null) {
					List<ObjectUsage> refListeners = refListeners(value._typeIdx(), value.getId(), type, id,
							fieldIndex);
					for (var ol : refListeners) {
						remove(value, ol);
					}
				}
			} else if (fieldType == FieldType.ReferenceCollection || fieldType == FieldType.InverseCollection) {
				List<Object> value = (List<Object>) dField.getValue(obj);
				if (value != null && !value.isEmpty()) {
					for (Object o : value) {
						DBObject dbObj = null;
						if (o instanceof TypeAndId) {
							dbObj = fromTypeAndId((TypeAndId) o);
						} else {
							dbObj = (DBObject) o;
						}
						if(dbObj != null) {
							List<ObjectUsage> refListeners = refListeners(dbObj._typeIdx(), dbObj.getId(), type, id,
									fieldIndex);
							for (var ol : refListeners) {
								remove(dbObj, ol);
							}							
						}
					}
				}
			}
		}
	}

	public DBObject fromTypeAndId(TypeAndId ti) {
		DModel<?> model = schema.getType(ti.type);
		if (model.isEmbedded()) {
			return null;
		}
		String type = model.getType();
		DBObject object = helperService.getObject().get(type, ti.id);
		return object;
	}

	public Disposable listen(int type, BitSet fields, BiConsumer<DBObject, StoreEventType> listener) {
		TypeListener dl = new TypeListener(type, fields, listener);		
		_Event event = new _Event();
		event.type = _EventType.ListenType;
		event.tl = dl;
		eventQueue.add(event);
		return dl;
	}
	
	private void doOnlisten(TypeListener dl) {
		Vector<ObjectListener> perType = perTypeListeners.get(dl.type);
		if (perType == null) {
			perType = new Vector<>();
			perTypeListeners.put(dl.type, perType);
		}
		ObjectListener ol = new ObjectListener(dl.fields, dl, null);
		perType.add(ol);
		dl.types.add(dl.type);
	}

	public void fire(DBObject object, StoreEventType changeType) {
		_Event event = new _Event();
		event.type = _EventType.Fire;
		event.obj2 = object;
		event.changeType = changeType;
		event.userProxy = sessionProvider.getCurrentUserProxy();
		event.cache = (D3EPrimaryCache) emProvider.get().getCache();
		Map<DBObject, DBChange> changes = new HashMap<>();
		changes.put(object, object._changes());
		if(object instanceof DatabaseObject) {
			((DatabaseObject) object).visitChildren(a -> changes.put(a, a._changes()));
		}
		event.changes = changes;
		eventQueue.add(event);
	}
	private void doFire(DBObject object, StoreEventType changeType, Map<DBObject, DBChange> changes) {
		DBChange ch = changes.get(object);
		long id = object.getId();
		// D3ELogger.info(
		//		"Fire: " + object._type() + " ID: " + id + " Event:" + changeType.toString() + " Changes: " + ch.changes);
		int type = object._typeIdx();
		if (object instanceof StructBase) {
			type = ((StructBase) object)._actualType();
		}

		boolean isDelete = changeType == StoreEventType.Delete;

		Map<Long, ObjectInterests> objectListeners = perObjectListeners.get(type);
		Set<ObjectListener> listeners = new HashSet<>();
		ObjectInterests interests = null;
		if (objectListeners != null) {
			interests = objectListeners.get(object.getId());
			if (interests != null) {
				for (ObjectListener ol : interests.fieldListeners) {
					if (ol.listener.isDisposed()) {
						// Remove
					} else if (ol.fields == null || isDelete || ol.fields.intersects(ch.changes)) {
						listeners.add(ol);
					}
				}
			}
		}
		Vector<ObjectListener> fieldListeners = perTypeListeners.get(type);
		if (fieldListeners != null) {
			for (ObjectListener ol : fieldListeners) {
				if (ol.listener.isDisposed()) {
					// Remove
				} else if (ol.fields == null || ol.fields.intersects(ch.changes)) {
					listeners.add(ol);
				}
			}
		}
		ObjectsToSend toSend = new ObjectsToSend();
		if (interests != null) {
			DModel<?> model = schema.getType(type);
			for (int field : ch.changes.stream().toArray()) {
				DField dField = model.getField(field);
				int fieldIndex = dField.getIndex();
				FieldType fieldType = dField.getType();
				DModel ref = dField.getReference();
				if(ref == null || ref.getIndex() == SchemaConstants.DFile) {
					continue;
				}
				if (fieldType == FieldType.Reference) {
					if (dField.getReference().isEmbedded() && changeType != StoreEventType.Delete) {
						for (ObjectListener ol : interests.fieldListeners) {
							if (ol.listener instanceof DisposableListener) {
								DisposableListener dl = (DisposableListener) ol.listener;
								Iterable<Field> expand = ListExt.expand(ol.field.getSelections(), s -> s.getFields());
								Iterable<Field> where = IterableExt.where(expand, (i) -> i.getField() == dField);
								BitSet set = new BitSet();
								for (Field f : where) {
									set.or(f.getBitSet(ListExt.asList(dField.getReference().getIndex())));
								}
								DBObject em = (DBObject) dField.getValue(object);
								set.and(em._changes().changes);
								toSend.addEmbedded(dl.session, object, dField, set);
							}
						}
						continue;
					}
					Object _oldValue = ch.oldValues.get(field);
					Object _newValue = dField.getValue(object);
					if (_oldValue != null && _newValue == null) {
						DBObject dbObj = null;
						int oldType = 0;
						long oldId = 0;
						if (_oldValue instanceof TypeAndId) {
							TypeAndId typeId = (TypeAndId) _oldValue;
							oldType = typeId.type;
							oldId = typeId.id;
							dbObj = fromTypeAndId((TypeAndId) _oldValue);
						} else {
							dbObj = (DBObject) _oldValue;
							oldType = dbObj._typeIdx();
							oldId = dbObj.getId();
						}
						List<ObjectUsage> refListeners = refListeners(oldType, oldId, type, id, fieldIndex);
						if (dbObj != null) {
							for (var ol : refListeners) {
								remove(dbObj, ol);
							}
						}
					}
					if (_newValue != null) {
						DBObject dbObj = null;
						if (_newValue instanceof TypeAndId) {
							dbObj = fromTypeAndId((TypeAndId) _newValue);
						} else {
							dbObj = (DBObject) _newValue;
						}
						DModel<?> dm = schema.getType(dbObj._typeIdx());
						List<Integer> allParents = new ArrayList<Integer>();
						dm.addAllParents(allParents);
						allParents.add(dm.getIndex());
						for (ObjectUsage ou : interests.refListeners) {
							Iterable<Field> expand = ListExt.expand(ou.field.getSelections(), s -> s.getFields());
							Iterable<Field> where = IterableExt.where(expand, (i) -> i.getField() == dField);
							for (Field f : where) {
								scan(object, dField, dbObj, f, ou.listener, toSend, allParents, changes);
							}
						}
					}
				} else if (fieldType == FieldType.ReferenceCollection || fieldType == FieldType.InverseCollection) {
					List _oldValue = (List) ch.oldValues.get(field);
					List _newValue = (List) dField.getValue(object);
					for (Object o : _oldValue) {
						if (_newValue.contains(o)) {
							continue;
						}
						DBObject dbObj = null;
						int oldType = 0;
						long oldId = 0;
						if (o instanceof TypeAndId) {
							TypeAndId typeId = (TypeAndId) o;
							oldType = typeId.type;
							oldId = typeId.id;
							dbObj = fromTypeAndId((TypeAndId) o);
						} else {
							dbObj = (DBObject) o;
							oldType = dbObj._typeIdx();
							oldId = dbObj.getId();
						}
						List<ObjectUsage> refListeners = refListeners(oldType, oldId, type, id, fieldIndex);
						if (dbObj != null) {
							for (var ol : refListeners) {
								remove(dbObj, ol);
							}
						}
					}

					for (Object o : new ArrayList<Object>(_newValue)) {
						if (_oldValue.contains(o)) {
							if (!(o instanceof DBObject)) {
								continue;
							}

							// Tracking changes in child collection
							DBObject dbObj = (DBObject) o;
							if (dbObj._changes() == null || dbObj._changes().changes.isEmpty()) {
								continue;
							}
						}
						DBObject dbObj = null;
						if (o instanceof TypeAndId) {
							dbObj = fromTypeAndId((TypeAndId) o);
						} else {
							dbObj = (DBObject) o;
						}
						if(dbObj != null) {
							DModel<?> dm = schema.getType(dbObj._typeIdx());
							List<Integer> allParents = new ArrayList<Integer>();
							dm.addAllParents(allParents);
							allParents.add(dm.getIndex());
							for (ObjectUsage ou : interests.refListeners) {
								Iterable<Field> expand = ListExt.expand(ou.field.getSelections(), s -> s.getFields());
								Iterable<Field> where = IterableExt.where(expand, (i) -> i.getField() == dField);
								for (Field f : where) {
									scan(object, dField, dbObj, f, ou.listener, toSend, allParents, changes);
								}
							}							
						}
					}
				}
			}
		}
		if (listeners.isEmpty()) {
			return;
		}

		if (isDelete) {
			List<ObjectUsage> refListeners = refListeners(type, id);
			if (refListeners != null) {
				for (var refL : refListeners) {
					remove(object, refL);
				}
			}
		}

		for (ObjectListener ol : listeners) {
			if (ol.listener instanceof DisposableListener) {
				DisposableListener dl = (DisposableListener) ol.listener;
				if (isDelete) {
					toSend.delete(dl.session, new TypeAndId(type, id));
				} else {
					BitSet set = new BitSet();
					set.or(ol.fields);
					set.and(ch.changes);
					toSend.add(dl.session, object, set);
				}
			} else {
				TypeListener tl = (TypeListener) ol.listener;
				tl.listener.accept(object, changeType);
			}
		}
		toSend.send(socket);
	}

	private List<ObjectUsage> refListeners(int type, long id) {
		Map<Long, ObjectInterests> objectListeners = perObjectListeners.get(type);
		if (objectListeners != null) {
			ObjectInterests interests = objectListeners.get(id);
			if (interests != null) {
				return ListExt.from(interests.refListeners, false);
			}
		}
		return Collections.emptyList();
	}

	private List<ObjectUsage> refListeners(int type, long id, int parentType, long parentId, int parentFieldIndex) {
		Map<Long, ObjectInterests> objectListeners = perObjectListeners.get(type);
		if (objectListeners != null) {
			ObjectInterests interests = objectListeners.get(id);
			if (interests != null) {
				return ListExt.where(interests.refListeners, ol -> ol.parentType == parentType
						&& ol.parentId == parentId && ol.fieldIdx == parentFieldIndex);
			}
		}
		return Collections.emptyList();
	}

	public void fire(List<DataStoreEvent> changes) {
		if(changes.isEmpty()) {
			return;
		}
		_Event event = new _Event();
		event.type = _EventType.Fire2;
		Map<DBObject, DBChange> dbChanges = new HashMap<>();
		for(DataStoreEvent ds : changes) {
			Object object = ds.getEntity();
			if(object instanceof DBObject) {
				dbChanges.put((DBObject)object, ((DBObject) object)._changes());			
			}
			if(object instanceof DatabaseObject) {
				((DatabaseObject) object).visitChildren(a -> dbChanges.put(a, a._changes()));
			}
		}
		event.changes = dbChanges;
		event.obj = changes;
		event.userProxy = sessionProvider.getCurrentUserProxy();
		event.cache = (D3EPrimaryCache) emProvider.get().getCache();
		eventQueue.add(event);
	}

}

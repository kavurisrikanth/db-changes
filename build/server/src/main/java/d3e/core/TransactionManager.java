package d3e.core;

import java.util.ArrayList;
import java.util.List;

import store.DBObject;
import store.DataStoreEvent;
import store.DatabaseObject;
import store.StoreEventType;

public class TransactionManager {

	private static ThreadLocal<TransactionManager> threadLocal = new ThreadLocal<>();

	private List<Object> added = new ArrayList<>();
	private List<Object> updated = new ArrayList<>();
	private List<Object> deleted = new ArrayList<>();

	public void add(Object obj) {
		if (added.contains(obj)) {
			return;
		}
		if (updated.contains(obj)) {
			return;
		}
		if (deleted.contains(obj)) {
			deleted.remove(obj);
		}
		added.add(obj);
	}

	public void update(Object obj) {
		if (added.contains(obj)) {
			return;
		}
		if (updated.contains(obj)) {
			return;
		}
		if (deleted.contains(obj)) {
			return;
		}
		updated.add(obj);
	}

	public void delete(Object obj) {
		if (added.contains(obj)) {
			added.remove(obj);
			return;
		}
		if (updated.contains(obj)) {
			updated.remove(obj);
			return;
		}
		deleted.add(obj);
	}

	public List<DataStoreEvent> getChanges() {
		List<DataStoreEvent> list = new ArrayList<>();	

		added.forEach(a -> list.add(new DataStoreEvent(StoreEventType.Insert, a)));
		updated.forEach(a -> list.add(new DataStoreEvent(StoreEventType.Update, a)));
		deleted.forEach(a -> list.add(new DataStoreEvent(StoreEventType.Delete, a)));
		return list;
	}

	public boolean isEmpty() {
		return added.isEmpty() && updated.isEmpty() && deleted.isEmpty();
	}

	public boolean getIsEmpty() {
		return isEmpty();
	}

	public static TransactionManager get() {
		return threadLocal.get();
	}

	public static void set(TransactionManager manager) {
		threadLocal.set(manager);
	}

	public static void remove() {
		threadLocal.remove();
	}

	public void clearChanges() {
		for (Object obj : this.added) {
			if (obj instanceof DBObject) {
				((DBObject) obj)._clearChanges();
			}
			if(obj instanceof DatabaseObject) {
				((DatabaseObject) obj).visitChildren(a -> a._clearChanges());
			}
		}
		for (Object obj : this.updated) {
			if (obj instanceof DBObject) {
				((DBObject) obj)._clearChanges();
			}
			if(obj instanceof DatabaseObject) {
				((DatabaseObject) obj).visitChildren(a -> a._clearChanges());
			}
		}
	}

}

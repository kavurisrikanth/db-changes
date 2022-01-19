package store;

public class DataStoreEvent {

	private StoreEventType type;
	private Object entity;

	public DataStoreEvent(StoreEventType type, Object entity) {
		this.type = type;
		this.entity = entity;
	}

	public StoreEventType getType() {
		return type;
	}

	public void setType(StoreEventType type) {
		this.type = type;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
}

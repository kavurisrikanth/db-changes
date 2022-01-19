package d3e.core;

public class SubscriptionEvent {

	private String id;
	private Object obj;

	public SubscriptionEvent(String id, Object obj) {
		this.id = id;
		this.obj = obj;
	}

	public String getId() {
		return id;
	}

	public Object getObj() {
		return obj;
	}
}

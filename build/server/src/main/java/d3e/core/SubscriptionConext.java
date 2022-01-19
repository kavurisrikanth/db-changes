package d3e.core;

public class SubscriptionConext {
	private String id;
	private final Class<?> readType;
	private final String input;

	public SubscriptionConext(Class<?> readType, String input) {
		this.readType = readType;
		this.input = input;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getInput() {
		return input;
	}

	public Class<?> getReadType() {
		return readType;
	}
}

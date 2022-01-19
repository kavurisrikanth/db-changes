package rest.ws;

public class UsageField {
	private int field;
	private UsageType[] types;

	public UsageField(int field, UsageType[] types) {
		this.field = field;
		this.types = types;
	}

	public int getField() {
		return field;
	}

	public UsageType[] getTypes() {
		return types;
	}

	public UsageType getType(int type) {
		for (UsageType t : types) {
			if (t.getType() == type) {
				return t;
			}
		}
		return null;
	}
}

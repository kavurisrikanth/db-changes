package rest.ws;

public class UsageType {
	private int type;
	private UsageField[] fields;

	public UsageType(int type, int fieldsCount) {
		this.type = type;
		this.fields = new UsageField[fieldsCount];
	}

	public int getType() {
		return type;
	}

	public UsageField[] getFields() {
		return fields;
	}
}

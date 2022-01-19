package rest.ws;

import gqltosql2.Field;

public class TemplateUsage {
	private String hash;
	private UsageType[] types;
	private Field field;

	public TemplateUsage(UsageType[] types) {
		this.types = types;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public UsageType[] getTypes() {
		return types;
	}
}

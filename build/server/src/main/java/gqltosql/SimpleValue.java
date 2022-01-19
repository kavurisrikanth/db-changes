package gqltosql;

import org.json.JSONObject;

public class SimpleValue implements IValue {

	private String field;
	private int index;

	public SimpleValue(String field, int index) {
		this.field = field;
		this.index = index;
	}

	@Override
	public Object read(Object[] row, JSONObject obj) throws Exception {
		Object val = row[index];
		obj.put(field, val);
		return val;
	}
	
	@Override
	public String toString() {
		return field;
	}
}

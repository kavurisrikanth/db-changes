package gqltosql;

import org.json.JSONObject;

public class EmbeddedValue extends RefValue {

	public EmbeddedValue(String field) {
		super(field, -1);
	}
	
	@Override
	public JSONObject read(Object[] row, JSONObject obj) throws Exception {
		JSONObject read = super.read(row, obj);
		return read;
	}
}

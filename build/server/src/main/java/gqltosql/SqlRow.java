package gqltosql;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SqlRow extends JSONObject {

	private Set<String> types = new HashSet<>();
	private SqlRow dup;

	public SqlRow() {
	}

	public void addType(String type) {
		types.add(type);
	}

	public boolean isOfType(String type) {
		return types.contains(type);
	}

	public void duplicate(SqlRow dup) {
		if(this.dup == dup) {
			return;
		}
		if(this.dup != null) {
			this.dup.duplicate(dup);
		} else {
			this.dup = dup;
		}
	}

	public SqlRow getDuplicate() {
		return dup;
	}

	public void addCollectionField(String field, JSONArray val) throws JSONException {
		put(field, val);
		if(dup != null) {
			dup.addCollectionField(field, val);
		}
	}
}

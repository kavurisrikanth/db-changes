package rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import d3e.core.DFile;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.VariableReference;
import store.EntityHelperService;

public class ObjectValueInputContext extends ArgumentInputContext {

	private Map<String, Object> value;

	public ObjectValueInputContext(ObjectValue value, EntityHelperService helperService,
			Map<Long, Object> inputObjectCache, Map<String, DFile> files, JSONObject variables) {
		super(null, helperService, inputObjectCache, files, variables);
		List<ObjectField> fields = value.getObjectFields();
		Map<String, Object> obj = new HashMap<>();
		fields.forEach(o -> obj.put(o.getName(), o.getValue()));
		this.value = obj;
	}

	@Override
	Object readAny(String field) {
		Object v = value.get(field);
		if (v instanceof VariableReference) {
			try {
				return variables.get(((VariableReference) v).getName());
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		return v;
	}

	@Override
	public boolean has(String field) {
		return value.containsKey(field);
	}
}

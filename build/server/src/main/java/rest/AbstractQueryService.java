package rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import d3e.core.D3ELogger;
import graphql.ExecutionInput;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.NodeUtil;
import graphql.language.NodeUtil.GetOperationResult;
import graphql.language.Selection;
import graphql.parser.Parser;

public abstract class AbstractQueryService {

	protected void logErrors(JSONObject errors) {
        D3ELogger.info("Errors: " + errors.toString());
	}

	protected GetOperationResult parseOperation(JSONObject req) throws Exception {
		JSONObject variables;
		if (req.has("variables")) {
			Object obj = req.get("variables");
			variables = obj instanceof JSONObject ? (JSONObject) obj : new JSONObject();
		} else {
			variables = new JSONObject();
		}
		req.put("variables", variables);
		ExecutionInput input = ExecutionInput.newExecutionInput().query(req.getString("query")).build();
		Parser parser = new Parser();
		Document document = parser.parseDocument(input.getQuery());
		GetOperationResult operation = NodeUtil.getOperation(document, input.getOperationName());
		return operation;
	}

	protected List<Field> getFields(GetOperationResult operation) throws Exception {
		List<Selection> selections = operation.operationDefinition.getSelectionSet().getSelections();
		List<Field> operations = new ArrayList<>();
		for (Selection s : selections) {
			if (s instanceof Field) {
				Field f = (Field) s;
				operations.add(f);
			} else {
				throw new RuntimeException("Unsupported opertation: " + s);
			}
		}
		return operations;
	}

	protected List<Field> parseFields(JSONObject req) throws Exception {
		GetOperationResult operation = parseOperation(req);
		return getFields(operation);
	}

	public static Field inspect(Field field, String path) {
		if (path.isEmpty()) {
			return field;
		}
		String[] subFields = path.split("\\.");
		return inspect(field, 0, subFields);
	}

	protected static Field inspect(Field field, int i, String... subFields) {
		if (i == subFields.length) {
			return field;
		}
		for (Selection s : field.getSelectionSet().getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				if (f.getName().equals(subFields[i])) {
					return inspect(f, i + 1, subFields);
				}
			}
		}
		return null;
	}

	protected Map<Long, JSONObject> byId(JSONArray list) throws Exception {
		Map<Long, JSONObject> byId = new HashMap<>();
		for (int i = 0; i < list.length(); i++) {
			JSONObject obj = list.getJSONObject(i);
			byId.put(obj.getLong("id"), obj);
		}
		return byId;
	}
}

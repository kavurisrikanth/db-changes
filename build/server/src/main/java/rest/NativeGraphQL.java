package rest;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import graphql.language.Field;
import graphql.language.NodeUtil.GetOperationResult;

@RestController
@RequestMapping("api/native/")
public class NativeGraphQL extends AbstractQueryService {

	@Autowired
	private NativeQuery query;

	@Autowired
	private NativeMutation mutation;

	@PostMapping(path = "/graphql", produces = MediaType.APPLICATION_JSON_VALUE)
	public String run(@RequestBody String body) throws Exception {
		JSONObject req = new JSONObject(body);
		GetOperationResult operation = parseOperation(req);
		JSONObject variables = req.getJSONObject("variables");
		List<Field> fields = getFields(operation);
		String name = operation.operationDefinition.getOperation().name();
		String queryStr = null;
		try {		    
		  	queryStr = req.getString("query");
		} catch (JSONException e) {
		}
		if (name.equalsIgnoreCase("query")) {
			return query.executeFields(queryStr, fields, variables);
		} else if (name.equalsIgnoreCase("mutation")) {
			return mutation.executeFields(queryStr, fields, variables);
		} else {
			throw new RuntimeException("Unsupported operation: " + name);
		}
	}
}

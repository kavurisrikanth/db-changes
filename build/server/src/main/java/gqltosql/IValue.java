package gqltosql;

import org.json.JSONObject;

public interface IValue {

	Object read(Object[] row, JSONObject obj) throws Exception;

}

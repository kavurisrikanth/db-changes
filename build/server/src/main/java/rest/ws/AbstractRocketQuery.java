package rest.ws;

import java.util.List;

import graphql.language.Field;
import graphql.language.Selection;
import io.reactivex.rxjava3.functions.Cancellable;

public class AbstractRocketQuery {
	protected QueryResult singleResult(String type, boolean external, Object value) {
		return singleResult(type, external, value, null);
	}

	protected QueryResult singleResult(String type, boolean external, Object value, Cancellable changeTracker) {
		QueryResult r = new QueryResult();
		r.type = type;
		r.external = external;
		r.value = value;
		r.changeTracker = changeTracker;
		return r;
	}

	protected QueryResult listResult(String type, boolean external, Object value, Cancellable changeTracker) {
		QueryResult r = new QueryResult();
		r.type = type;
		r.external = external;
		r.isList = true;
		r.value = value;
		r.changeTracker = changeTracker;
		return r;
	}

	protected static Field inspect(Field field, String path) {
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
		for (Selection<?> s : field.getSelectionSet().getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				if (f.getName().equals(subFields[i])) {
					return inspect(f, i + 1, subFields);
				}
			}
		}
		return null;
	}

	public static gqltosql2.Field inspect2(gqltosql2.Field field, String path) {
		if (path.isEmpty()) {
			return field;
		}
		String[] subFields = path.split("\\.");
		return inspect2(field, 0, subFields);
	}

	protected static gqltosql2.Field inspect2(gqltosql2.Field field, int i, String... subFields) {
		if (i == subFields.length) {
			return field;
		}
		for (gqltosql2.Selection s : field.getSelections()) {
			List<gqltosql2.Field> fields = s.getFields();
			for (gqltosql2.Field f : fields) {
				if (f.getField().getName().equals(subFields[i])) {
					gqltosql2.Field res = inspect2(f, i + 1, subFields);
					if (res != null) {
						return res;
					}
				}
			}
		}
		return null;
	}
}

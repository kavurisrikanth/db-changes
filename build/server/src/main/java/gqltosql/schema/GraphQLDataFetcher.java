package gqltosql.schema;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import classes.ClassUtils;
import d3e.core.D3ELogger;
import d3e.core.DFile;
import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.language.TypeName;
import store.DatabaseObject;

public class GraphQLDataFetcher {

	private IModelSchema schema;
	private boolean needLocalId;

	public GraphQLDataFetcher(IModelSchema schema) {
		this.schema = schema;
	}

	public GraphQLDataFetcher(IModelSchema schema, boolean needLocalId) {
		this.schema = schema;
		this.needLocalId = needLocalId;
	}
	
	public Object fetch(Field field, String type, Object value) {
		DModel<?> md = schema.getType(type);
		return fetchValue(field, value, md);
	}

	public JSONArray fetchList(Field field, String type, List<?> value) {
		try {
			JSONArray array = new JSONArray();
			value.forEach(v -> array.put(fetch(field, type, v)));
			return array;
		} catch (ConcurrentModificationException e) {
			D3ELogger.info("Retrying fetchList: " + field.getName() + " - " + type);
			return fetchList(field, type, value);
		}
	}

	public Object fetchValue(Field field, Object value, DModel<?> type) {
		if (value == null) {
			return JSONObject.NULL;
		}
		if (value instanceof DFile) {
			return fetchDFile(field, (DFile) value);
		}
		if (value instanceof DatabaseObject) {
			JSONObject res = new JSONObject();
			SelectionSet set = field.getSelectionSet();
			fetchReferenceInternal(set, res, type, value);
			return res;
		}
		if (value instanceof Collection) {
			JSONArray array = new JSONArray();
			Collection<?> coll = (Collection<?>) value;
			coll.forEach(v -> array.put(fetchValue(field, v, type)));
			return array;
		}
		return value;
	}

	private Object fetchDFile(Field field, DFile value) {
		JSONObject res = new JSONObject();
		for (Selection s : field.getSelectionSet().getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				try {
					if (f.getName().equals("id")) {
						res.put("id", value.getId());
					} else if (f.getName().equals("name")) {
						res.put("name", value.getName());
					} else if (f.getName().equals("size")) {
						res.put("size", value.getSize());
					}
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return res;
	}

	private void fetchReferenceInternal(SelectionSet set, JSONObject res, DModel<?> type, Object value) {
		for (Selection s : set.getSelections()) {
			if (s instanceof Field) {
				Field f = (Field) s;
				DField df = type.getField(f.getName());
				try {
					if (df != null) {
						res.put(f.getName(), df.fetchValue(value, new DataFetcherImpl(f, df)));
					} else if (f.getName().equals("__typename")) {
						res.put("__typename", ClassUtils.getClass(value).getSimpleName());
					} else if (f.getName().equals("localId")) {
						DatabaseObject db = (DatabaseObject) value;
						res.put("localId", needLocalId ? db.getLocalId() : 0l);
					}
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			} else if (s instanceof InlineFragment) {
				InlineFragment in = (InlineFragment) s;
				TypeName typeName = in.getTypeCondition();
				if (value.getClass().getSimpleName().equals(typeName.getName())) {
					DModel<?> dm = schema.getType(typeName.getName());
					fetchReferenceInternal(in.getSelectionSet(), res, dm, value);
				}
			}
		}
	}

	private class DataFetcherImpl implements IDataFetcher {

		private Field field;
		private DField<?, ?> df;

		public DataFetcherImpl(Field field, DField<?, ?> df) {
			this.field = field;
			this.df = df;
		}

		@Override
		public Object onPrimitiveValue(Object value, DField df) {
			if (value == null) {
				return JSONObject.NULL;
			} else if (value instanceof DFile) {
				return fetchDFile(field, (DFile) value);
			} else {
				return value;
			}
		}

		@Override
		public Object onReferenceValue(Object value) {
			if (value == null) {
				return JSONObject.NULL;
			}

			JSONObject res = new JSONObject();
			SelectionSet set = field.getSelectionSet();
			fetchReferenceInternal(set, res, df.getReference(), value);
			return res;
		}

		@Override
		public Object onEmbeddedValue(Object value) {
			return onReferenceValue(value);
		}

		@Override
		public Object onPrimitiveList(List<?> value, DField df) {
			try {
				JSONArray array = new JSONArray();
				value.forEach(v -> array.put(onPrimitiveValue(v, df)));
				return array;
			} catch (ConcurrentModificationException e) {
				D3ELogger.info("Retrying fetchList: " + field.getName());
				return onPrimitiveList(value, df);
			}
		}

		@Override
		public Object onReferenceList(List<?> value) {
			try {
				JSONArray array = new JSONArray();
				value.forEach(v -> array.put(onReferenceValue(v)));
				return array;
			} catch (ConcurrentModificationException e) {
				D3ELogger.info("Retrying fetchList: " + field.getName());
				return onReferenceList(value);
			}
		}

		@Override
		public Object onFlatValue(List<?> value) {
			try {
				JSONArray array = new JSONArray();
				value.forEach(v -> array.put(onReferenceValue(v)));
				return array;
			} catch (ConcurrentModificationException e) {
				D3ELogger.info("Retrying fetchList: " + field.getName());
				return onFlatValue(value);
			}
		}

		@Override
		public Object onInverseValue(List<?> value) {
			try {
				JSONArray array = new JSONArray();
				value.forEach(v -> array.put(onReferenceValue(v)));
				return array;
			} catch (ConcurrentModificationException e) {
				D3ELogger.info("Retrying fetchList: " + field.getName());
				return onInverseValue(value);
			}
		}
	}
}

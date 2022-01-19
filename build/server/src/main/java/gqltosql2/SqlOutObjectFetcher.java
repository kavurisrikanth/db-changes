package gqltosql2;

import java.util.Collection;
import java.util.List;

import org.json.JSONException;

import d3e.core.DFile;
import d3e.core.ListExt;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldType;
import gqltosql.schema.IDataFetcher;
import gqltosql.schema.IModelSchema;
import store.DatabaseObject;

public class SqlOutObjectFetcher {

	private IModelSchema schema;

	public SqlOutObjectFetcher(IModelSchema schema) {
		this.schema = schema;
	}

	public Object fetchValue(Field field, Object value, DModel<?> type) {
		if (value == null) {
			return null;
		}
		if (value instanceof DFile) {
			return fetchDFile(field, (DFile) value);
		}
		if (value instanceof DatabaseObject) {
			return fetchReference(field, (DatabaseObject) value);
		}
		if (value instanceof Collection) {
			DField<?, ?> df = field.getField();
			List list = df.getType() == FieldType.PrimitiveCollection ? new OutPrimitiveList() : new OutObjectList();
			Collection<?> coll = (Collection<?>) value;
			coll.forEach(v -> list.add(fetchValue(field, v, type)));
			return list;
		}
		return value;
	}

	private OutObject fetchReference(Field field, DatabaseObject value) {
		OutObject res = new OutObject();
		DModel<?> parent = schema.getType(value._typeIdx());
		res.setId(value.getId());
		res.addType(parent.getIndex());
		while (parent != null) {
			DModel<?> type = parent;
			Selection selec = ListExt.firstWhere(field.getSelections(), s -> s.getType() == type);
			if (selec != null) {
				fetchReferenceInternal(selec, res, type, value);
			}
			parent = parent.getParent();
		}
		return res;
	}

	private OutObject fetchDFile(Field field, DFile value) {
		OutObject res = new OutObject();
		List<Selection> selections = field.getSelections();
		Selection selec = selections.get(0);
		for (Field f : selec.getFields()) {
			try {
				DField<?, ?> df = f.getField();
				if (df.getName().equals("id")) {
					res.add("id", value.getId());
				} else if (df.getName().equals("name")) {
					res.add("name", value.getName());
				} else if (df.getName().equals("size")) {
					res.add("size", value.getSize());
				}
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		return res;
	}

	private void fetchReferenceInternal(Selection set, OutObject res, DModel<?> type, Object value) {
		for (Field s : set.getFields()) {
			DField df = s.getField();
			try {
				Object val = df.fetchValue(value, new DataFetcherImpl(s, df));
				res.add(df.getName(), val);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class DataFetcherImpl implements IDataFetcher {

		private Field field;

		public DataFetcherImpl(Field field, DField<?, ?> df) {
			this.field = field;
		}

		@Override
		public Object onPrimitiveValue(Object value, DField df) {
			if (value == null) {
				return null;
			} else if (value instanceof DFile) {
				return fetchDFile(field, (DFile) value);
			} else {
				return value;
			}
		}

		@Override
		public OutObject onReferenceValue(Object value) {
			if (value == null) {
				return null;
			}
			return fetchReference(field, (DatabaseObject) value);
		}

		@Override
		public Object onEmbeddedValue(Object value) {
			return onReferenceValue(value);
		}

		@Override
		public Object onPrimitiveList(List<?> value, DField field) {
			OutPrimitiveList list = new OutPrimitiveList();
			value.forEach(v -> list.add(onPrimitiveValue(v, field)));
			return list;
		}

		@Override
		public OutObjectList onReferenceList(List<?> value) {
			OutObjectList list = new OutObjectList();
			value.forEach(v -> list.add(onReferenceValue(v)));
			return list;
		}

		@Override
		public OutObjectList onFlatValue(List<?> value) {
			OutObjectList list = new OutObjectList();
			value.forEach(v -> list.add(onReferenceValue(v)));
			return list;
		}

		@Override
		public Object onInverseValue(List<?> value) {
			return onReferenceList(value);
		}
	}
}

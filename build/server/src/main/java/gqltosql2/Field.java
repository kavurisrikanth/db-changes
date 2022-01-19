package gqltosql2;

import java.util.BitSet;
import java.util.List;

import d3e.core.ListExt;
import gqltosql.schema.DField;

public class Field {

	private DField<?, ?> field;
	private List<Selection> selections;

	public void setField(DField<?, ?> field) {
		this.field = field;
	}

	public DField<?, ?> getField() {
		return field;
	}

	public void setSelections(List<Selection> selections) {
		this.selections = selections;
	}

	public List<Selection> getSelections() {
		return selections;
	}

	public Selection getSelectionForType(int type) {
		return ListExt.firstWhere(selections, (i) -> i.getType().getIndex() == type, null);
	}

	public BitSet getBitSet(List<Integer> types) {
		BitSet set = new BitSet();
		for (Selection s : selections) {
			if (types.contains(s.getType().getIndex())) {
				set.or(s.getFieldsSet());
			}
		}
		return set;
	}

	public Field inspect2(String path) {
		if (path.isEmpty()) {
			return this;
		}
		String[] subFields = path.split("\\.");
		return inspect2(this, 0, subFields);
	}

	protected static Field inspect2(Field field, int i, String... subFields) {
		if (i == subFields.length) {
			return field;
		}
		for (Selection s : field.getSelections()) {
			List<Field> fields = s.getFields();
			for (Field f : fields) {
				if (f.getField().getName().equals(subFields[i])) {
					Field res = inspect2(f, i + 1, subFields);
					if (res != null) {
						return res;
					}
				}
			}
		}
		return null;
	}
}

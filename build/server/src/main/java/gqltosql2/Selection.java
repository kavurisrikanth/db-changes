package gqltosql2;

import java.util.BitSet;
import java.util.List;

import gqltosql.schema.DModel;

public class Selection {
	private final DModel<?> type;
	private final List<Field> fields;
	public Selection(DModel<?> type, List<Field> fields) {
		this.type = type;
		this.fields = fields;
	}
	
	private BitSet fieldsSet;

	public DModel<?> getType() {
		return type;
	}

	public List<Field> getFields() {
		return fields;
	}

	public BitSet getFieldsSet() {
		if(fieldsSet == null) {
			fieldsSet = new BitSet(type.getFieldsCount());
			for(Field f : fields) {
				fieldsSet.set(f.getField().getIndex());
			}
		}
		return this.fieldsSet;
	}
}

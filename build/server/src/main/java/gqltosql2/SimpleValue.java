package gqltosql2;

public class SimpleValue implements IValue {

	private String field;
	private int index;

	public SimpleValue(String field, int index) {
		this.field = field;
		this.index = index;
	}

	@Override
	public Object read(Object[] row, OutObject obj) throws Exception {
		Object val = row[index];
		obj.add(field, val);
		return val;
	}

	@Override
	public String toString() {
		return field;
	}
}

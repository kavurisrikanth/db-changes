package gqltosql2;

public class EmbeddedValue extends RefValue {

	public EmbeddedValue(String field) {
		super(field, true, -1);
	}

	@Override
	public OutObject read(Object[] row, OutObject obj) throws Exception {
		OutObject read = super.read(row, obj);
		if (read != null) {
			read.setId(obj.getId());
		}
		return read;
	}
}

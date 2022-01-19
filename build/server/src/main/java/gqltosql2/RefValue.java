package gqltosql2;

import java.util.HashMap;

public class RefValue implements IValue {

	private String field;
	private QueryReader reader;

	public RefValue(String field, boolean embedded, int index) {
		this.field = field;
		this.reader = new QueryReader(index, embedded);
	}

	public QueryReader getReader() {
		return reader;
	}

	@Override
	public OutObject read(Object[] row, OutObject obj) throws Exception {
		OutObject read = reader.read(row, new HashMap<>());
		obj.add(field, read);
		return read;
	}

	@Override
	public String toString() {
		return field;
	}
}

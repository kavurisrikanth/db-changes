package gqltosql2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QueryTypeReader {
	private int id;
	private int type;
	private List<IValue> fields = new ArrayList<>();

	public QueryTypeReader(int type) {
		this.type = type;
		this.id = -1;
	}

	public int getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public void read(Object[] row, OutObject obj) throws Exception {
		if (id != -1) {
			if ((row[id] instanceof BigInteger)) {
				BigInteger bid = ((BigInteger) row[id]);
				if (bid == null) {
					return;
				}
			} else if (row[id] instanceof String) {
				obj.add("id", row[id]);
			}
		}
		obj.addType(type);
		for (IValue f : fields) {
			f.read(row, obj);
		}
	}

	public void add(String field, int index) {
		if (field.equals("id")) {
			id = index;
			return;
		}
		fields.add(new SimpleValue(field, index));
	}

	public QueryReader addRef(String field, int index) {
		RefValue rv = new RefValue(field, false, index);
		fields.add(rv);
		return rv.getReader();
	}

	public QueryReader addEmbedded(String field) {
		EmbeddedValue rv = new EmbeddedValue(field);
		fields.add(rv);
		return rv.getReader();
	}

	@Override
	public String toString() {
		return String.valueOf(type);
	}
}

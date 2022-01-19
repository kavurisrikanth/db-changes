package gqltosql2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

public class OutObject {
	private long id;
	private Set<Integer> types = new HashSet<>();
	private Map<String, Object> fields = new HashMap<>();
	private OutObject dup;

	public long getId() {
		return id;
	}

	public int getType() {
		Long index = getLong("__typeindex");
		if (index == null || index == 0l) {
			for (Integer i : types) {
				if (i != -1) {
					return i;
				}
			}
		}
		return index.intValue();
	}

	public void addType(int type) {
		types.add(type);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int length() {
		return fields.size();
	}

	public void add(String field, Object value) {
		if (field.equals("__typeindex") && value instanceof Long) {
			Long pri = (Long) value;
			if (pri.toString().equals("-1") && types.contains(225)) {
				System.out.println();
			}
		}
		fields.put(field, value);
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public String getString(String field) {
		Object val = get(field);
		if (val == null) {
			return null;
		}
		return (String) val;
	}

	public Long getLong(String field) {
		if (field.equals("id")) {
			return id;
		}
		Object val = get(field);
		if (val == null) {
			return 0l;
		}
		if (val instanceof Integer) {
			return (long) ((int) val);
		}
		return (Long) val;
	}

	public OutObject getObject(String field) {
		Object val = get(field);
		return (OutObject) val;
	}

	public void remove(String field) {
		fields.remove(field);
	}

	public boolean isOfType(int type) {
		return types.contains(type);
	}

	public void duplicate(OutObject dup) {
		if (this.dup == dup) {
			return;
		}
		if (this.dup != null) {
			this.dup.duplicate(dup);
		} else {
			this.dup = dup;
		}
	}

	public OutObject getDuplicate() {
		return dup;
	}

	public void addCollectionField(String field, OutObjectList val) throws JSONException {
		add(field, val);
		if (dup != null) {
			dup.addCollectionField(field, val);
		}
	}

	public Object getPrimitive(String field) {
		return fields.get(field);
	}

	public boolean has(String field) {
		return fields.containsKey(field);
	}

	public Object get(String field) {
		return fields.get(field);
	}

	public long getMemorySize() {
		long size = 64 + 8; // Id
		size += (types.size() * 8);
		size += 64;// fields map
		for (Entry<String, Object> e : fields.entrySet()) {
			size += e.getKey().getBytes().length;
			if (e.getValue() != null) {
				Object value = e.getValue();
				if (value instanceof OutObject) {
					OutObject o = (OutObject) value;
					size += o.getMemorySize();
				} else if (value instanceof OutObjectList) {
					OutObjectList o = (OutObjectList) value;
					size += o.getMemorySize();
				} else {
					size += 8;
				}
			}
		}
		return size;
	}
}

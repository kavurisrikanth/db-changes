package store;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class DBChange {
	public DBChange(int count) {
		changes = new BitSet(count);
		oldValues = new HashMap<>();
	}
	public final BitSet changes;
	public final Map<Integer, Object> oldValues;
	public void set(int field, Object oldValue) {
		if(!changes.get(field)) {
			changes.set(field);
			oldValues.put(field, oldValue);
		}
	}
	
	public void unset(int field) {
		changes.clear(field);
		oldValues.remove(field);
	}
}

package store;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

public class DBChange {
	public final BitSet changes;
	public final Map<Integer, Object> oldValues;
	public final Consumer<Boolean> onSet;
	public final Runnable onUnset;
	
	public DBChange(int count, Consumer<Boolean> onSet, Runnable onUnset) {
		changes = new BitSet(count);
		oldValues = new HashMap<>();
		this.onSet = onSet;
		this.onUnset = onUnset;
	}
	
	public void set(int field, Object oldValue) {
		if(!changes.get(field)) {
			changes.set(field);
			oldValues.put(field, oldValue);
		}
	}
	
	public void unset(int field, boolean clear) {
		if (clear) {
			changes.clear(field);
		}
		oldValues.remove(field);
	}
	
	public boolean isEmpty() {
		return changes.isEmpty();
	}
	
	public void onFieldChange(int fieldIdx, Object oldValue, Object newValue) {
		Object _old = oldValues.get(fieldIdx);
		if (_old != null && Objects.equals(newValue, _old)) {
			// Discard
//			System.err.println("*** No changes detected in field \"" + fieldIdx + "\" in type \"" + _type() + "\"");
			unset(fieldIdx, true);
			if (changes.isEmpty()) {
				// If there are still changes, do not send unset to master
				onUnset.run();
			}
			return;
		}
		if (_old == null) {
			set(fieldIdx, oldValue);
		}
		onSet.accept(false);
	}
	
	public void onCollFieldChange(int fieldIdx, Object oldValue, Object newValue) {
		ArrayList lc = (ArrayList) oldValues.get(fieldIdx);
		if (lc == null) {
			lc = new ArrayList((List) oldValue);
			set(fieldIdx, lc);
		} else if (newValue != null) {
			List newList = ((List) newValue);
			if (CollectionUtils.isEqualCollection(lc, newList)) {
//				System.err.println("*** No changes detected in field \"" + fieldIdx + "\" in type \"" + _type() + "\"");
				
				// The new collection has matched with the old reference collection.
				// So, there are is no effective change on the collection field due to adds/removes
				// But there could be changes due to childs. So, we can remove the old collection that
				// we have in the Map, but we should not unset the bit if there are changes.
				boolean hasChanges = newList.stream().anyMatch(x -> {
					if (!(x instanceof DBObject)) {
						return false;
					}
					DBObject o = (DBObject) x;
					return !o._changes().isEmpty();
				});
				unset(fieldIdx, !hasChanges);
				if (!hasChanges) {
					// If there are still changes, do not send unset to master
					onUnset.run();
				}
				return;
			}
		}
		if(oldValue instanceof D3EPersistanceList) {
			D3EPersistanceList pl = (D3EPersistanceList) oldValue;
			if(pl.isInverse()) {
				onSet.accept(true);
				return;
			}
		}
		onSet.accept(false);
	}
	
	public void onInvCollFieldChange(int field, Object oldValue) {
		ArrayList lc = (ArrayList) oldValues.get(field);
		if (lc == null) {
			lc = new ArrayList((List) oldValue);
			set(field, lc);
		}
	}

	public void onChildFieldChange(int _childIdx, boolean set) {
		if (set) {
			// Set the bit, but this does not need an oldValue
			changes.set(_childIdx);
		} else {
			// If there is an oldValue, then that means a set operation was performed on the master object for the child field.
			// So, that cannot be cleared simply because a field in the child changed.
			// So we do nothing in that case
			if (!oldValues.containsKey(_childIdx)) {
				changes.clear(_childIdx);
			}
		}
	}

	public void onChildCollFieldChange(int _childIdx, boolean set) {
		// TODO Auto-generated method stub
		if (set) {
			// Same as for single child
			changes.set(_childIdx);
		} else {
			Object old = oldValues.get(_childIdx);
			if (old == null) {
				// If there is no oldValue, then we can unset because the list structure was not changed
				changes.clear(_childIdx);
				return;
			}
			// TODO: Any other case?
		}
	}
}

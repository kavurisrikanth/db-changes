package store;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;;

public abstract class DBObject {

	protected transient long localId;
	private transient DBChange _changes;

	protected transient int _childIdx = -1;
	protected transient boolean inProxy;

	public DBObject() {
		this._changes = new DBChange(_fieldsCount());
	}

	public void setLocalId(long localId) {
		this.localId = localId;
	}

	public long getLocalId() {
		return localId;
	}

	protected abstract int _fieldsCount();

	public long getId() {
	  return 0;
	}

	public void setId(long id) {}
	
	public abstract int _typeIdx();

	public abstract String _type();

	public DBChange _changes() {
		return _changes;
	}
	
	public DatabaseObject _masterObject() {
		return null;
	}
	
	protected void _checkProxy() {
		DatabaseObject master = _masterObject();
		if(master != null) {
			master._checkProxy();
		}
	}
	
	public boolean isOld() {
		return false;
	}
	
	public void fieldChanged(int field, Object oldValue) {
		fieldChanged(field, oldValue, null);
	}
	
	public void fieldChanged(int field, Object oldValue, Object newValue) {
		if(inProxy || isOld()) {
			return;
		}
		Object _old = _changes.oldValues.get(field);
		if (_old != null && Objects.equals(newValue, _old)) {
			// Discard
			System.err.println("*** No changes detected in field \"" + field + "\" in type \"" + _type() + "\"");
			this._changes.unset(field);
			onPropertyUnset();
			return;
		}
		if (_old == null) {
			this._changes.set(field, oldValue);
		}
		onPropertySet(false);
	}
	
	public void collFieldChanged(int field, Object oldValue) {
		collFieldChanged(field, oldValue, null);
	}
	
	public void collFieldChanged(int field, Object oldValue, Object newValue) {
		if(inProxy || isOld()) {
			return;
		}
		ArrayList lc = (ArrayList) _changes.oldValues.get(field);
		if (lc == null) {
			lc = new ArrayList((List) oldValue);
			this._changes.set(field, lc);
		} else if (newValue != null) {
			if (CollectionUtils.isEqualCollection(lc, (List) newValue)) {
				System.err.println("*** No changes detected in field \"" + field + "\" in type \"" + _type() + "\"");
				this._changes.unset(field);
				onPropertyUnset();
				return;
			}
		}
		if(oldValue instanceof D3EPersistanceList) {
			D3EPersistanceList pl = (D3EPersistanceList) oldValue;
			if(pl.isInverse()) {
				onPropertySet(true);
				return;
			}
		}
		onPropertySet(false);
	}
	
	public void invCollFieldChanged(int field, Object oldValue) {
		if(inProxy || isOld()) {
			return;
		}
		ArrayList lc = (ArrayList) _changes.oldValues.get(field);
		if (lc == null) {
			lc = new ArrayList((List) oldValue);
			this._changes.set(field, lc);
		}
	}

	public void _clearChanges() {
		this._changes = new DBChange(_fieldsCount());
	}
	
	protected void onPropertySet(boolean inverse) {
		informChangeToMaster(true);
	}
	
	protected void onPropertyUnset() {
		informChangeToMaster(false);
	}
	
	protected void informChangeToMaster(boolean set) {
		DatabaseObject _master = this._masterObject();
		if (_master == null) {
			return;
		}
//		_master._handleChildChange(this._childIdx, set, this);
		_master._handleChildChange(this._childIdx);
	}
	
	public void _setChildIdx(int childIdx) {
		this._childIdx = childIdx;
	}
	
	public void _clearChildIdx() {
		this._childIdx = -1;
	}
}

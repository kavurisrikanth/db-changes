package store;

import java.util.ArrayList;
import java.util.List;

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
		if(inProxy || isOld()) {
			return;
		}
		this._changes.set(field, oldValue);
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
				this._changes.unset(field);
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
		informChangeToMaster();
	}
	
	protected void informChangeToMaster() {
		DatabaseObject _master = this._masterObject();
		if (_master == null) {
			return;
		}
		_master._handleChildChange(this._childIdx);
	}
	
	public void _setChildIdx(int childIdx) {
		this._childIdx = childIdx;
	}
	
	public void _clearChildIdx() {
		this._childIdx = -1;
	}
}

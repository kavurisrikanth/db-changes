package store;

public abstract class DBObject {

	protected transient long localId;
	private transient DBChange _changes;

	protected transient int _childIdx = -1;
	protected transient boolean inProxy;

	public DBObject() {
		this._changes = new DBChange(_fieldsCount(), this::onPropertySet, this::onPropertyUnset);
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
		_changes.onFieldChange(field, oldValue, newValue);
	}
	
	public void collFieldChanged(int field, Object oldValue) {
		collFieldChanged(field, oldValue, null);
	}
	
	public void collFieldChanged(int field, Object oldValue, Object newValue) {
		if(inProxy || isOld()) {
			return;
		}
		_changes.onCollFieldChange(field, oldValue, newValue);
	}
	
	public void invCollFieldChanged(int field, Object oldValue) {
		if(inProxy || isOld()) {
			return;
		}
		_changes.onInvCollFieldChange(field, oldValue);
	}
	
	public void childFieldChanged(int field, boolean set) {
		if(inProxy || isOld()) {
			return;
		}
		_changes.onChildFieldChange(field, set);
	}
	
	public void childCollFieldChanged(int field, boolean set) {
		if(inProxy || isOld()) {
			return;
		}
		_changes.onChildCollFieldChange(field, set);
	}

	public void _clearChanges() {
		this._changes = new DBChange(_fieldsCount(), this::onPropertySet, this::onPropertyUnset);
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
		_master._handleChildChange(this._childIdx, set);
	}
	
	public void _setChildIdx(int childIdx) {
		this._childIdx = childIdx;
	}
	
	public void _clearChildIdx() {
		this._childIdx = -1;
	}
}

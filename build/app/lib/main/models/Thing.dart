import '../classes/DBResult.dart';
import '../rocket/MessageDispatch.dart';
import '../utils/CloneContext.dart';
import '../utils/CollectionUtils.dart';
import '../utils/DBObject.dart';
import 'ChildModel.dart';

class Thing extends DBObject {
  static const int _CHILD = 0;
  static const int _CHILDCOLL = 1;
  int id = 0;
  DBObject otherMaster;
  ChildModel _child;
  List<ChildModel> _childColl = [];
  Thing({ChildModel child, List<ChildModel> childColl}) {
    this.setChild(child ?? null);

    this.setChildColl(childColl ?? []);
  }
  String get d3eType {
    return 'Thing';
  }

  void clear() {
    this.d3eChanges.clear();
  }

  ChildModel get child {
    return _child;
  }

  void setChild(ChildModel val) {
    bool isValChanged = _child != val;

    if (!isValChanged) {
      return;
    }

    if (_child != null) {
      _child.otherMaster = null;
    }

    if (val != null) {
      val.otherMaster = this;

      val.childPropertyInMaster = _CHILD;
    }

    this.updateD3EChanges(_CHILD, _child);

    updateObservable('child', _child, val);

    _child = val;

    fire('child', this);
  }

  List<ChildModel> get childColl {
    return _childColl;
  }

  void setChildColl(List<ChildModel> val) {
    bool isValChanged = CollectionUtils.isNotEquals(_childColl, val);

    if (!isValChanged) {
      return;
    }

    if (_childColl != null) {
      _childColl.forEach((one) => one.otherMaster = null);
    }

    if (val != null) {
      for (ChildModel o in val) {
        o.otherMaster = this;

        o.childPropertyInMaster = _CHILDCOLL;
      }
    }

    if (!this.d3eChanges.contains(_CHILDCOLL)) {
      List<ChildModel> _old = List.from(_childColl);

      this.updateD3EChanges(_CHILDCOLL, _old);
    }

    updateObservableColl('childColl', _childColl, val);

    _childColl.clear();

    _childColl.addAll(val);

    fire('childColl', this);
  }

  void addToChildColl(ChildModel val, [int index = -1]) {
    List<ChildModel> _old = [];

    bool _isNewChange = !this.d3eChanges.contains(_CHILDCOLL);

    if (_isNewChange) {
      _old = List.from(_childColl);
    }

    val.otherMaster = this;

    val.childPropertyInMaster = _CHILDCOLL;

    if (index == -1) {
      if (!_childColl.contains(val)) _childColl.add(val);
    } else {
      _childColl.insert(index, val);
    }

    fire('childColl', this, val, true);

    updateObservable('childColl', null, val);

    if (_isNewChange) {
      this.updateD3EChanges(_CHILDCOLL, _old);
    }
  }

  void removeFromChildColl(ChildModel val) {
    List<ChildModel> _old = [];

    bool _isNewChange = !this.d3eChanges.contains(_CHILDCOLL);

    if (_isNewChange) {
      _old = List.from(_childColl);
    }

    _childColl.remove(val);

    val.otherMaster = null;

    fire('childColl', this, val, false);

    removeObservable('childColl', val);

    if (_isNewChange) {
      this.updateD3EChanges(_CHILDCOLL, _old);
    }
  }

  Object get(int field) {
    switch (field) {
      case _CHILD:
        {
          return this._child;
        }

      case _CHILDCOLL:
        {
          return this._childColl;
        }
      default:
        {
          return null;
        }
    }
  }

  void updateD3EChanges(int index, Object value) {
    if (lockedChanges()) {
      return;
    }

    super.updateD3EChanges(index, value);
  }

  void restore() {
    /*
TODO: Might be removed
*/

    this.d3eChanges.restore(this);
  }

  Thing deepClone({clearId = true}) {
    CloneContext ctx = CloneContext(clearId: clearId);

    return ctx.startClone(this);
  }

  void collectChildValues(CloneContext ctx) {
    ctx.collectChild(_child);

    ctx.collectChilds(_childColl);
  }

  void deepCloneIntoObj(DBObject dbObj, CloneContext ctx) {
    Thing obj = (dbObj as Thing);

    obj.id = id;

    ctx.cloneChild(_child, (v) => obj.setChild(v));

    ctx.cloneChildList(_childColl, (v) => obj.setChildColl(v));
  }

  Future<DBResult> save() async {
    return (await MessageDispatch.get().save(this));
  }

  Future<DBResult> delete() async {
    return (await MessageDispatch.get().delete(this));
  }

  void set(int field, Object value) {
    switch (field) {
      case _CHILD:
        {
          this.setChild((value as ChildModel));
          break;
        }

      case _CHILDCOLL:
        {
          this.setChildColl((value as List).cast<ChildModel>());
          break;
        }
    }
  }
}

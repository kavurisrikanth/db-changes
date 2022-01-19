import '../classes/DBResult.dart';
import '../utils/CloneContext.dart';
import '../utils/DBObject.dart';

class ChildModel extends DBObject {
  static const int _NUM = 0;
  int id = 0;
  DBObject otherMaster;
  int _num = 0;
  int childPropertyInMaster = 0;
  ChildModel({int num}) {
    this.setNum(num ?? 0);
  }
  String get d3eType {
    return 'ChildModel';
  }

  void clear() {
    this.d3eChanges.clear();
  }

  int get num {
    return _num;
  }

  void setNum(int val) {
    bool isValChanged = _num != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_NUM, _num);

    _num = val;

    fire('num', this);
  }

  Object get(int field) {
    switch (field) {
      case _NUM:
        {
          return this._num;
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

    this.otherMaster?.updateChildChanges(this.childPropertyInMaster);
  }

  void restore() {
    /*
TODO: Might be removed
*/

    this.d3eChanges.restore(this);
  }

  ChildModel deepClone({clearId = true}) {
    CloneContext ctx = CloneContext(clearId: clearId);

    return ctx.startClone(this);
  }

  void collectChildValues(CloneContext ctx) {}
  void deepCloneIntoObj(DBObject dbObj, CloneContext ctx) {
    ChildModel obj = (dbObj as ChildModel);

    obj.id = id;

    obj.setNum(_num);
  }

  Future<DBResult> save() async {
    if (this.otherMaster != null) {
      return this.otherMaster.save();
    }

    return DBResult();
  }

  Future<DBResult> delete() async {
    if (this.otherMaster != null) {
      return this.otherMaster.delete();
    }

    return DBResult();
  }

  void set(int field, Object value) {
    switch (field) {
      case _NUM:
        {
          this.setNum((value as int));
          break;
        }
    }
  }
}

import 'ObjectObservable.dart';
import '../classes/DBResult.dart';

import 'CloneContext.dart';
import 'D3EObjectChanges.dart';
import 'DBSaveStatus.dart';

abstract class DBObject extends ObjectObservable {
  D3EObjectChanges d3eChanges = D3EObjectChanges();
  String get d3eType;
  bool _locked = false;
  DBSaveStatus _saveStatus = DBSaveStatus.New;
  int get id;
  set id(int id);
  DBSaveStatus get saveStatus => _saveStatus;
  set saveStatus(DBSaveStatus saveStatus) {
    this._saveStatus = saveStatus;
  }

  void collectChildValues(CloneContext ctx) {}
  void deepCloneIntoObj(DBObject obj, CloneContext ctx) {}

  void updateChildChanges(int index) {
    if (!this.d3eChanges.contains(index)) {
      Object val = get(index);
      if (val is List) {
        val = List.from(val as List);
      }
      updateD3EChanges(index, val);
    }
  }

  void updateD3EChanges(int index, Object value) {
    if (_locked) {
      return;
    }
    //print('changed field $index in $d3eType');
    this.d3eChanges.add(index, value);
  }

  void lock() {
    _locked = true;
  }

  bool lockedChanges() {
    return _locked;
  }

  void unlock() {
    _locked = false;
  }

  void clear() {}
  Future<DBResult> save() async {}
  Future<DBResult> delete() async {}

  Object get(int field) {}
  void set(int field, Object value) {}
}

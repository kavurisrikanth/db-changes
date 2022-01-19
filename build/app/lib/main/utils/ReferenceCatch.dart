import '../rocket/D3ETemplate.dart';
import 'DBSaveStatus.dart';

import 'DBObject.dart';

class ReferenceCatch {
  Map<int, Map<int, DBObject>> all = {};
  Map<int, Map<int, int>> refCounts = {};

  ReferenceCatch();

  static ReferenceCatch _instance;

  factory ReferenceCatch.get() {
    if (_instance == null) {
      _instance = ReferenceCatch();
    }
    return _instance;
  }

  void updateLocalId(int type, int localId, int id) {
    if (localId == id) {
      return;
    }
    Map<int, DBObject> byId = all[type];
    if (byId == null) {
      return null;
    }
    DBObject obj = byId[localId];
    if (obj != null) {
      obj.id = id;
      byId[id] = obj;
      obj.saveStatus = DBSaveStatus.Saved;
      byId.remove(localId);
    }
  }

  DBObject findObject(int type, int id) {
    if (id == null || id == 0) {
      return null;
    }
    Map<int, DBObject> byId = all[type];
    if (byId == null) {
      return null;
    }
    return byId[id];
  }

  void addObject(DBObject obj) {
    if (obj.id == null || obj.id == 0) {
      return;
    }
    int type = D3ETemplate.typeInt(obj.d3eType);
    Map<int, DBObject> byId = all[type];
    if (byId == null) {
      byId = Map();
      all[type] = byId;
    }
    byId[obj.id] = obj;
  }

  void updateReference(DBObject old, DBObject obj) {
    if (old == obj) {
      return;
    }
    if (old != null) {
      int type = D3ETemplate.typeInt(old.d3eType);
      Map<int, int> rc = refCounts[type];
      if (rc == null) {
        rc = Map();
        refCounts[type] = rc;
      }
      int count = rc[old.id];
      if (count != null) {
        rc[old.id] = count - 1;
      }
    }
    if (obj != null) {
      int type = D3ETemplate.typeInt(obj.d3eType);
      Map<int, int> rc = refCounts[type];
      if (rc == null) {
        rc = Map();
        refCounts[type] = rc;
      }
      int count = rc[obj.id];
      if (count == null) {
        count = 0;
      }
      rc[obj.id] = count + 1;
    }
  }
}

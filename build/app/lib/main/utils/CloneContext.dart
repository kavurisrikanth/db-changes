import '../rocket/D3ETemplate.dart';
import '../rocket/D3ETemplateTypes.dart';
import '../classes/core.dart';
import '../classes/ExpressionString.dart';
import '../classes/BlockString.dart';

import 'DBObject.dart';

class CloneContext {
  Map<Object, Object> cache = Map<Object, Object>();
  bool reverting = false;
  bool clearId = false;
  CloneContext({this.clearId = false});

  void revert(DBObject obj) {
    reverting = true;
    DBObject cloned = cache[obj];
    Map<Object, Object> revertCache = Map<Object, Object>();
    cache.forEach((k, v) => revertCache[v] = k);
    cache = revertCache;
    bool oldClearId = this.clearId;
    clearId = false;
    startClone(cloned);
    clearId = oldClearId;
  }

  DBObject startClone(DBObject obj) {
    DBObject cloned = cloneRef(obj);
    obj.deepCloneIntoObj(cloned, this);
    if (clearId) {
      cloned.id = 0;
    }
    return cloned;
  }

  void collectChilds<T extends DBObject>(List<T> exist) {
    exist.forEach((e) => collectChild(e));
  }

  void collectChild<T extends DBObject>(T exist) {
    if (exist == null) {
      return;
    }
    int typeIndex = D3ETemplate.typeInt(exist.d3eType);
    D3ETemplateType type = D3ETemplate.types[typeIndex];
    T newObj = type.creator();
    newObj.id = exist.id;
    cache[exist] = newObj;
    exist.collectChildValues(this);
  }

  void cloneChildList<T extends DBObject>(
      List<T> exist, Consumer<List<T>> setter) {
    List<T> cloned = cloneRefList(exist);
    setter(cloned);
    for (int i = 0; i < exist.length; i++) {
      exist[i].deepCloneIntoObj(cloned[i], this);
      if (clearId) {
        cloned[i].id = 0;
      }
    }
  }

  void cloneChildSet<T extends DBObject>(
      Set<T> exist, Consumer<Set<T>> setter) {
    Set<T> cloned = cloneRefSet(exist);
    setter(cloned);
    exist.forEach((e) {
      T c = cloned.firstWhere((c) => c.id == e.id);
      if (clearId) {
        c.id = 0;
      }
      e.deepCloneIntoObj(c, this);
    });
  }

  List<T> cloneRefList<T extends DBObject>(List<T> list) {
    List<T> cloned = [];
    list.forEach((l) => cloned.add(cloneRef(l)));
    return cloned;
  }

  Set<T> cloneRefSet<T extends DBObject>(Set<T> list) {
    Set<T> cloned = {};
    list.forEach((l) => cloned.add(cloneRef(l)));
    return cloned;
  }

  void cloneChild<T extends DBObject>(DBObject exist, Consumer<T> setter) {
    if (exist == null) {
      setter(null);
    } else {
      T cloned = cloneRef(exist);
      setter(cloned);
      exist.deepCloneIntoObj(cloned, this);
      if (clearId) {
        cloned.id = 0;
      }
    }
  }

  ExpressionString cloneExpressionString(ExpressionString obj) {
    if (reverting) {
      return cache[obj];
    }
    ExpressionString clone = ExpressionString(obj.content);
    clone.attachment = obj.attachment;
    cache[obj] = clone;
    return clone;
  }

  BlockString cloneBlockString(BlockString obj) {
    if (reverting) {
      return cache[obj];
    }
    BlockString clone = BlockString(obj.content);
    clone.attachment = obj.attachment;
    cache[obj] = clone;
    return clone;
  }

  T cloneRef<T extends DBObject>(T obj) {
    if (obj == null) {
      return null;
    }
    if (reverting) {
      return cache[obj];
    }
    DBObject exist;
    if (cache.containsKey(obj)) {
      exist = cache[obj];
    } else {
      int typeIndex = D3ETemplate.typeInt(obj.d3eType);
      D3ETemplateType type = D3ETemplate.types[typeIndex];
      exist = type.creator();
      exist.id = obj.id;
      cache[obj] = exist;
    }
    return exist as T;
  }
}

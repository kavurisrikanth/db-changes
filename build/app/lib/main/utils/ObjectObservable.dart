import '../classes/core.dart';
import 'dart:async';

typedef void PathHandler();
typedef void ChangeHandler(Object parent, Object value, bool added);

abstract class IObservable {
  void removeObservable(String childPropName, ObjectObservable child);
  void updateObservable(String childPropName, ObjectObservable oldChild,
      ObjectObservable newChild);
  void updateObservableColl(String childPropName,
      List<ObjectObservable> oldChild, List<ObjectObservable> newChild);
  void fire(String path, Object parent, [Object value, bool added = false]);
  void _fireInternal(String path, Object parent, Object value, bool added,
      Map<IObservable, Set<String>> visited);
  void dispose();
  Set<String> getDependency(String childPropName);
  void addDependency(
      String childPropName, IObservable parent, Set<String> paths);
}

class _Parent {
  IObservable parent;
  Set<String> deps;
  _Parent(this.parent, this.deps);
}

class ObjectObservable implements IObservable {
  bool initDone = false;
  String get nameOfModel {
    return '';
  }

  //Can have more parents with same property name.
  Map<String, List<_Parent>> parents = Map();
  Map<String, List<ObjectObservable>> childs = Map();
  Map<String, List<PathHandler>> pathListeners = Map();
  Map<String, List<ChangeHandler>> changeListeners = Map();

  void dispose() {
    pathListeners.clear();
    changeListeners.clear();
    childs.forEach((k, cs) => cs.forEach((v) => v._removeParent(this, k)));
  }

  void removeObservable(String childPropName, ObjectObservable child) {
    if (child != null) {
      childs.remove(childPropName);
      child._removeParent(this, childPropName);
    }
  }

  void updateObservable(String childPropName, ObjectObservable oldChild,
      ObjectObservable newChild) {
    removeObservable(childPropName, oldChild);
    if (newChild != null) {
      List<ObjectObservable> cs = childs[childPropName];
      if (cs == null) {
        cs = [];
        childs[childPropName] = cs;
      }
      cs.add(newChild);
      newChild._addParent(this, childPropName);
    }
  }

  void updateObservableColl(
      String childPropName,
      Iterable<ObjectObservable> oldChild,
      Iterable<ObjectObservable> newChild) {
    if (oldChild != null) {
      oldChild.forEach((o) => removeObservable(childPropName, o));
    }
    if (newChild != null) {
      newChild.forEach((o) => updateObservable(childPropName, null, o));
    }
  }

  void initListeners() {
    initDone = true;
  }

  void _removeParent(IObservable parent, String childPropName) {
    List<_Parent> parents = this.parents[childPropName];
    if (parents == null) {
      return;
    }
    parents.removeWhere((p) => p.parent == parent);
    if (this.parents.isEmpty) {
      this.dispose();
    }
  }

  void _addParent(IObservable parent, String childPropName) {
    List<_Parent> parents = this.parents[childPropName];
    if (parents == null) {
      parents = [];
      this.parents[childPropName] = parents;
    }
    if (!initDone) {
      this.initListeners();
    }
    Set<String> paths = parent.getDependency(childPropName);
    parents.add(_Parent(parent, paths));
    _addPathsToChildren(paths);
  }

  void _addPathsToChildren(Set<String> paths) {
    this.childs.forEach((key, children) {
      children.forEach((child) => child.addDependency(
          key,
          this,
          paths
              .where((x) => x.startsWith(key + '.'))
              .map((f) => f.substring(key.length + 1))
              .toSet()));
    });
  }

  Set<String> getDependency(String name) {
    return {
      ...pathListeners.keys,
      ...changeListeners.keys,
      ...this.parents.values.expand((f) => f).expand((x) => x.deps)
    }
        .where((x) => x.startsWith(name + '.'))
        .map((f) => f.substring(name.length + 1))
        .toSet();
  }

  void addDependency(
      String childPropName, IObservable parent, Set<String> paths) {
    if (paths.isEmpty) {
      return;
    }
    List<_Parent> parents = this.parents[childPropName];
    _Parent p =
        parents.firstWhere((p) => p.parent == parent, orElse: () => null);
    if (p == null) {
      return;
    }
    p.deps.addAll(paths);
    _addPathsToChildren(paths);
  }

  Runnable on(List<String> paths, PathHandler handler) {
    List<Runnable> disposibles = [];
    paths.forEach((p) {
      List<PathHandler> handlers = pathListeners[p];
      if (handlers == null) {
        handlers = [];
        pathListeners[p] = handlers;
      }
      handlers.add(handler);
      disposibles.add(() => handlers.remove(handler));
    });
    _addPathsToChildren(paths.toSet());
    return () => disposibles.forEach((d) => d());
  }

  Runnable on2(String path, ChangeHandler handler) {
    List<ChangeHandler> handlers = changeListeners[path];
    if (handlers == null) {
      handlers = [];
      changeListeners[path] = handlers;
    }
    handlers.add(handler);
    _addPathsToChildren({path});
    return () => handlers.remove(handler);
  }

  void fire(String path, Object parent, [Object value, bool added = false]) {
    Map<IObservable, Set<String>> visited = {};
    _fireInternal(path, parent, value, added, visited);
    _fireInternal('*', parent, value, added, visited);
  }

  void _fireInternal(String path, Object parent, Object value, bool added,
      Map<IObservable, Set<String>> visited) {
    if (visited[this] != null && visited[this].contains(path)) {
      return;
    } else {
      if (visited[this] == null) {
        visited[this] = {};
      }
      visited[this].add(path);
    }
    List<PathHandler> handlers = pathListeners[path];
    if (handlers != null) {
      handlers.forEach((h) => schedule(() => h(), parent, false));
    }
    List<ChangeHandler> changeHandlers = changeListeners[path];
    if (changeHandlers != null) {
      changeHandlers.forEach((h) =>
          schedule(() => h(parent, value, added), parent, path.endsWith('*')));
    }
    if (this.nameOfModel != '') {
      childs.forEach((k, cs) => cs.forEach((v) => v._fireInternal(
          'master' + this.nameOfModel + '.' + path,
          parent,
          value,
          added,
          visited)));
    }

    //Inform to master if change is not from master
    if (!path.startsWith('master') || path == 'master') {
      Map<String, List<IObservable>> map;
      this.parents.forEach((name, ps) => ps.forEach((p) {
            if (p.deps.contains(path)) {
              if (map == null) {
                map = Map<String, List<IObservable>>();
              }
              List<IObservable> list = map[name + '.' + path];
              if (list == null) {
                list = [];
                map[name + '.' + path] = list;
              }
              list.add(p.parent);
            }
          }));
      map?.forEach((key, val) {
        val.forEach((i) => i._fireInternal(key, parent, value, added, visited));
      });
    }
  }

  static void schedule(Runnable o, Object p, bool star) {
    if (star) {
      if (starParents.contains(p)) {
        return;
      }
      starParents.add(p);
    }
    if (actions.isEmpty) {
      Timer.run(() {
        while (actions.isNotEmpty) {
          Runnable r = actions.removeAt(0);
          r();
        }
        starParents.clear();
      });
    }
    actions.add(o);
  }

  static List<Runnable> actions = [];
  static Set<Object> starParents = {};
}

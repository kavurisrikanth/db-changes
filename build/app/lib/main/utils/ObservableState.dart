import 'package:flutter/widgets.dart';
import 'ObjectObservable.dart';

abstract class ObservableState<T extends StatefulWidget> extends State<T>
    implements IObservable {
  ObjectObservable observable = new ObjectObservable();
  bool enableBuild = false;

  @override
  initState() {
    super.initState();
    observable.initListeners();
  }

  void rebuild() {
    if (enableBuild && !context.owner.debugBuilding) {
      setState(() {});
    }
  }

  void removeObservable(String childPropName, ObjectObservable child) {
    observable.removeObservable(childPropName, child);
  }

  void updateObservable(String childPropName, ObjectObservable oldChild,
      ObjectObservable newChild) {
    observable.updateObservable(childPropName, oldChild, newChild);
  }

  void updateObservableColl(
      String childPropName,
      Iterable<ObjectObservable> oldChild,
      Iterable<ObjectObservable> newChild) {
    observable.updateObservableColl(childPropName, oldChild, newChild);
  }

  void updateSyncCollProperty(String prop, Iterable<ObjectObservable> value) {
    if (value != null) {
      value.forEach((v) => observable.updateObservable(prop, null, v));
    }
  }

  void updateSyncProperty(String prop, ObjectObservable value) {
    observable.updateObservable(prop, null, value);
  }

  void on(List<String> paths, PathHandler handler) {
    observable.on(paths, handler);
  }

  void on2(String path, ChangeHandler handler) {
    observable.on2(path, handler);
  }

  void fire(String path, Object parent, [Object value, bool added = false]) {
    observable.fire(path, parent, value, added);
  }

  void dispose() {
    observable.dispose();
    this.enableBuild = false;
    super.dispose();
  }

  Set<String> getDependency(String childPropName) {
    return observable.getDependency(childPropName);
  }

  void addDependency(
      String childPropName, IObservable parent, Set<String> paths) {
    observable.addDependency(childPropName, parent, paths);
  }
}

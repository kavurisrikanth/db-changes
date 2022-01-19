import 'DBObject.dart';

class D3EObjectChanges {
  Map<int, Object> _values = Map();
  bool _locked = false;
  D3EObjectChanges();

  Map<int, Object> get values => this._values;

  void clear() {
    if (_locked) return;
    _values.clear();
  }

  void add(int field, Object value) {
    if (_locked) return;
    _values.putIfAbsent(field, () => value);
  }

  void replaceValue(int field, Object value) {
    _values[field] = value;
  }

  Object getValue(int field) {
    return _values[field];
  }

  bool contains(int field) {
    return _values.containsKey(field) ?? false;
  }

  bool get hasChanges {
    return _values.isNotEmpty ?? false;
  }

  void restore(DBObject obj) {
    if (_locked) return;
    _values.forEach((key, value) {
      obj.set(key, value);
    });
    clear();
  }

  void lock() {
    _locked = true;
  }

  void unlock() {
    _locked = false;
  }
}

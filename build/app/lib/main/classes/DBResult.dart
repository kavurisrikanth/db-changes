import '../utils/CollectionUtils.dart';
import '../utils/DBObject.dart';
import 'DBResultStatus.dart';

class DBResult extends DBObject {
  int _id = 0;
  static const int _ERRORS = 0;
  static const int _STATUS = 1;
  DBResultStatus _status = DBResultStatus.Success;
  List<String> _errors = [];
  DBResult({List<String> errors, DBResultStatus status}) {
    if (errors != null) {
      this.setErrors(errors);
    }

    if (status != null) {
      this.setStatus(status);
    }
  }
  int get id {
    return _id;
  }

  set id(int id) {
    this._id = id;
  }

  String get d3eType {
    return 'DBResult';
  }

  void clear() {}
  @override
  void initListeners() {
    super.initListeners();
  }

  DBResultStatus get status {
    return _status;
  }

  void setStatus(DBResultStatus val) {
    bool isValChanged = _status != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_STATUS, _status.index);

    _status = val;

    fire('status', this);
  }

  List<String> get errors {
    return _errors;
  }

  void setErrors(List<String> val) {
    bool isValChanged = CollectionUtils.isNotEquals(_errors, val);

    if (!isValChanged) {
      return;
    }

    if (!this.d3eChanges.contains(_ERRORS)) {
      List<String> _old = List.from(_errors);

      this.updateD3EChanges(_ERRORS, _old);
    }

    _errors.clear();

    _errors.addAll(val);

    fire('errors', this);
  }

  void addToErrors(String val, [int index = -1]) {
    List<String> _old = [];

    bool _isNewChange = !this.d3eChanges.contains(_ERRORS);

    if (_isNewChange) {
      _old = List.from(_errors);
    }

    if (index == -1) {
      if (!_errors.contains(val)) _errors.add(val);
    } else {
      _errors.insert(index, val);
    }

    fire('errors', this, val, true);

    if (_isNewChange) {
      this.updateD3EChanges(_ERRORS, _old);
    }
  }

  void removeFromErrors(String val) {
    List<String> _old = [];

    bool _isNewChange = !this.d3eChanges.contains(_ERRORS);

    if (_isNewChange) {
      _old = List.from(_errors);
    }

    _errors.remove(val);

    fire('errors', this, val, false);

    if (_isNewChange) {
      this.updateD3EChanges(_ERRORS, _old);
    }
  }

  void set(int field, Object value) {
    switch (field) {
      case _STATUS:
        {
          this.setStatus(DBResultStatus.values[(value as int)]);
          break;
        }

      case _ERRORS:
        {
          this.setErrors((value as List).cast<String>());
          break;
        }
    }
  }

  Object get(int field) {
    switch (field) {
      case _STATUS:
        {
          return this._status.index;
        }

      case _ERRORS:
        {
          return this._errors;
        }
      default:
        {
          return null;
        }
    }
  }

  bool operator ==(Object other) {
    return identical(this, other) ||
        other is DBResult &&
            _status == other._status &&
            _errors == other._errors;
  }

  @override
  int get hashCode {
    return (_status?.hashCode ?? 0) + (_errors?.hashCode ?? 0);
  }
}

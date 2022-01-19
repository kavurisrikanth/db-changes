import '../models/User.dart';
import '../utils/DBObject.dart';

class LoginResult extends DBObject {
  int _id = 0;
  static const int _FAILUREMESSAGE = 0;
  static const int _SUCCESS = 1;
  static const int _TOKEN = 2;
  static const int _USEROBJECT = 3;
  bool _success = false;
  User _userObject;
  String _token = '';
  String _failureMessage = '';
  LoginResult(
      {String failureMessage, bool success, String token, User userObject}) {
    if (failureMessage != null) {
      this.setFailureMessage(failureMessage);
    }

    if (success != null) {
      this.setSuccess(success);
    }

    if (token != null) {
      this.setToken(token);
    }

    if (userObject != null) {
      this.setUserObject(userObject);
    }
  }
  int get id {
    return _id;
  }

  set id(int id) {
    this._id = id;
  }

  String get d3eType {
    return 'LoginResult';
  }

  void clear() {}
  @override
  void initListeners() {
    super.initListeners();
  }

  bool get success {
    return _success;
  }

  void setSuccess(bool val) {
    bool isValChanged = _success != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_SUCCESS, _success);

    _success = val;

    fire('success', this);
  }

  User get userObject {
    return _userObject;
  }

  void setUserObject(User val) {
    bool isValChanged = _userObject != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_USEROBJECT, _userObject);

    updateObservable('userObject', _userObject, val);

    _userObject = val;

    fire('userObject', this);
  }

  String get token {
    return _token;
  }

  void setToken(String val) {
    bool isValChanged = _token != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_TOKEN, _token);

    _token = val;

    fire('token', this);
  }

  String get failureMessage {
    return _failureMessage;
  }

  void setFailureMessage(String val) {
    bool isValChanged = _failureMessage != val;

    if (!isValChanged) {
      return;
    }

    this.updateD3EChanges(_FAILUREMESSAGE, _failureMessage);

    _failureMessage = val;

    fire('failureMessage', this);
  }

  void set(int field, Object value) {
    switch (field) {
      case _SUCCESS:
        {
          this.setSuccess((value as bool));
          break;
        }

      case _USEROBJECT:
        {
          this.setUserObject((value as User));
          break;
        }

      case _TOKEN:
        {
          this.setToken((value as String));
          break;
        }

      case _FAILUREMESSAGE:
        {
          this.setFailureMessage((value as String));
          break;
        }
    }
  }

  Object get(int field) {
    switch (field) {
      case _SUCCESS:
        {
          return this._success;
        }

      case _USEROBJECT:
        {
          return this._userObject;
        }

      case _TOKEN:
        {
          return this._token;
        }

      case _FAILUREMESSAGE:
        {
          return this._failureMessage;
        }
      default:
        {
          return null;
        }
    }
  }

  bool operator ==(Object other) {
    return identical(this, other) ||
        other is LoginResult &&
            _success == other._success &&
            _userObject == other._userObject &&
            _token == other._token &&
            _failureMessage == other._failureMessage;
  }

  @override
  int get hashCode {
    return (_success?.hashCode ?? 0) +
        (_userObject?.hashCode ?? 0) +
        (_token?.hashCode ?? 0) +
        (_failureMessage?.hashCode ?? 0);
  }
}

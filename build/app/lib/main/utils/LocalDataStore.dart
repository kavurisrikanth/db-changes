import 'JSONUtils.dart';
import 'package:localstorage/localstorage.dart';
import '../models/User.dart';

class LocalDataStore {
  User _currentUser;
  String _token;
  final LocalStorage storage = new LocalStorage('d3e');

  // To avoid setting null
  static bool _initialized = false;
  static LocalDataStore _store;

  static bool _authenticated = false;

  // TODO: When performing a logout (or session timeout), remove data from shared_preferences

  static LocalDataStore get() {
    if (!_initialized) {
      _store = LocalDataStore();
      _store._currentUser = null;
      _store._token = null;
      _initialized = true;
    }
    return _store;
  }

  void setUser(User user, String token) async {
    _currentUser = user;
    _token = token;

    if (user == null || token == null) {
      storage.deleteItem('token');
      storage.deleteItem('user');
    } else if (!LocalDataStore.authenticated) {
      storage.setItem('token', token);

      storage.setItem('user', _toStore(user));
      LocalDataStore.auth();
    }
  }

  Future<User> currentUser() async {
    await getToken();
    return _currentUser;
  }

  Future<String> getToken() async {
    if (_token == null) {
      await storage.ready;
      _token = storage.getItem('token') as String;
      _currentUser = _fromStore(storage.getItem('user'));
      if (_currentUser != null) {
        _currentUser.clear();
      }
    }
    return _token;
  }

  User _fromStore(Map data) {
    return JSONUtils.fromJson(data);
  }

  Map _toStore(User data) {
    return JSONUtils.toJson(data);
  }

  static get authenticated {
    return _authenticated;
  }

  static void auth() {
    _authenticated = true;
  }

  static void unauth() {
    _authenticated = false;
  }
}

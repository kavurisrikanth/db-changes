import 'package:graphql_flutter/graphql_flutter.dart';
import '../classes/Env.dart';
import 'LocalDataStore.dart';

class DummyStore implements Store {
  dynamic read(String key) {
    return null;
  }

  void write(
    String key,
    dynamic value,
  ) {}

  @override
  void delete(String dataId) {}

  @override
  Map<String, dynamic> get(String dataId) {
    return null;
  }

  @override
  void put(String dataId, Map<String, dynamic> value) {}

  @override
  void putAll(Map<String, Map<String, dynamic>> data) {}

  @override
  Map<String, Map<String, dynamic>> toMap() {
    return {};
  }

  @override
  void reset() {}
}

class GraphQLClientInit {
  static GraphQLClient _client;
  static String token;
  static GraphQLClient get() {
    if (_client == null) {
      final _httpLink = HttpLink(Env.get().baseHttpUrl + '/api/native/graphql');
      final AuthLink _authLink = AuthLink(getToken: () async {
        if (token == null) {
          return '';
        }
        return 'Bearer $token';
      });
      final Link _link = _authLink.concat(_httpLink);
      _client =
          GraphQLClient(link: _link, cache: GraphQLCache(store: DummyStore()));
    }
    return _client;
  }
}

import 'LoginResult.dart';
import 'dart:convert';
import 'package:http/http.dart' as rest;
import 'Env.dart';
import 'package:graphql_flutter/graphql_flutter.dart' hide Subscription;
import '../models/User.dart';
import '../rocket/D3ETemplate.dart';
import '../rocket/MessageDispatch.dart';
import '../utils/GraphQLClientInit.dart';
import '../utils/LocalDataStore.dart';
import '../utils/ReferenceCatch.dart';

class Query {
  GraphQLClient _client;
  static Query _queryObject;
  ReferenceCatch _referenceCatch;
  Query._init() {
    this._client = GraphQLClientInit.get();

    this._referenceCatch = ReferenceCatch.get();
  }
  factory Query.get() {
    if (_queryObject == null) {
      _queryObject = Query._init();
    }

    return _queryObject;
  }
  Future<User> currentUser() async {
    GraphQLClientInit.token = (await LocalDataStore.get().getToken());

    return (await LocalDataStore.get().currentUser());
  }

  Future<bool> logout() async {
    LocalDataStore.get().setUser(null, null);

    (await MessageDispatch.get().logout());

    return true;
  }
}

import 'JSONUtils.dart';
import 'package:localstorage/localstorage.dart';

import 'DBObject.dart';
import 'ReferenceCatch.dart';

class Device {
  static final LocalStorage storage = LocalStorage('auth');
  static final ReferenceCatch _referenceCatch = ReferenceCatch.get();

  static void put(String key, Object value) async {
    if (value is DBObject) {
      Map json = JSONUtils.toJson(value);
      await storage.setItem(key, json);
    } else {
      // value is primitive
      await storage.setItem(key, value);
    }
  }

  static Future<Object> get(String key) async {
    // Will be either primitive or Map, since that's what we stored.
    await storage.ready;
    dynamic value = await storage.getItem(key);

    if (value is Map) {
      DBObject obj = JSONUtils.fromJson(value);
      return obj;
    }

    return value;
  }
}

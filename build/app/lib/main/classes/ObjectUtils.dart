import '../utils/DBObject.dart';

class ObjectUtils {
  static bool isEquals(Object a, Object b) {
    if (a == b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    if (a is DBObject && b is DBObject) {
      return a.d3eType == b.d3eType && a.id == b.id;
    }
    return false;
  }

  static bool isNotEquals(Object a, Object b) {
    return !isEquals(a, b);
  }
}

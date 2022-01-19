class CollectionUtils {
  static bool isEquals(Iterable<Object> left, Iterable<Object> right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null) {
      return false;
    }
    if (left.length != right.length) {
      return false;
    }
    Iterator<Object> leftIt = left.iterator;
    Iterator<Object> rightIt = right.iterator;
    while (leftIt.moveNext() && rightIt.moveNext()) {
      if (leftIt.current != rightIt.current) {
        return false;
      }
    }
    return true;
  }

  static int collectionHash<T>(Iterable<T> list) {
    if (list == null) {
      return 0;
    }
    int hash = list.hashCode;
    for (var a in list) {
      hash += a.hashCode;
    }
    return hash;
  }

  static bool isNotEquals(Iterable<Object> left, Iterable<Object> right) {
    return !isEquals(left, right);
  }

  static T getLargest<T extends Comparable<T>>(List<T> items) {
    items.sort((one, two) => one.compareTo(two));
    return items.last;
  }
}

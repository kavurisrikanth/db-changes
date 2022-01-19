import 'UniqueCheckerClient.dart';

class RPCServices {
  static UniqueCheckerClient _uniqueChecker;
  static UniqueCheckerClient getUniqueChecker() {
    if (_uniqueChecker == null) {
      _uniqueChecker = UniqueCheckerClient();
    }

    return _uniqueChecker;
  }
}

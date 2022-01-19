import '../utils/DBObject.dart';
import 'ConnectionStatus.dart';

class ConnectionEvent extends DBObject {
  int _id = 0;
  ConnectionStatus _status = ConnectionStatus.Connecting;
  ConnectionEvent({ConnectionStatus status}) {
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
    return 'ConnectionEvent';
  }

  void clear() {}
  @override
  void initListeners() {
    super.initListeners();
  }

  ConnectionStatus get status {
    return _status;
  }

  void setStatus(ConnectionStatus val) {
    bool isValChanged = _status != val;

    if (!isValChanged) {
      return;
    }

    _status = val;

    fire('status', this);
  }

  bool operator ==(Object other) {
    return identical(this, other) ||
        other is ConnectionEvent && _status == other._status;
  }

  @override
  int get hashCode {
    return (_status?.hashCode ?? 0);
  }
}

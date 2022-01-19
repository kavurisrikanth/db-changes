import 'dart:async';
import 'dart:math';

import '../classes/core.dart';

import '../classes/ConnectionEvent.dart';
import '../classes/ConnectionStatus.dart';

import '../utils/EventBus.dart';

import '../utils/D3EObjectChanges.dart';
import '../utils/DBSaveStatus.dart';
import '../classes/DBResult.dart';
import '../classes/DBResultStatus.dart';
import '../classes/IdGenerator.dart';
import '../classes/LoginResult.dart';
import '../utils/DBObject.dart';
import '../utils/LocalDataStore.dart';
import '../utils/ReferenceCatch.dart';

import 'BufferReader.dart';
import 'Channels.dart';
import 'D3EWebClient.dart';
import 'D3EDisposable.dart';
import 'D3ETemplate.dart';
import 'D3ETemplateTypes.dart';
import 'WSReader.dart';
import 'WSWriter.dart';

class Resp {
  int id;
  WSReader reader;
  Resp(this.id, this.reader);
}

enum _ConnectionStatus { Connecting, TypeExchange, Ready, Disconnected }

class ObjectSyncDisposable implements D3EDisposable {
  String subId;
  bool _disposed = false;
  ObjectSyncDisposable(this.subId);

  @override
  void dispose() {
    _disposed = true;
    if (subId == null) {
      return;
    }
    MessageDispatch.get()._unsubscribe(subId);
  }

  bool get disposed {
    return _disposed;
  }
}

class MessageDispatch {
  static const bool allowParallelReq = true;
  static const bool allowParallelMutation = false;
  static const int ERROR = 0;
  static const int CONFIRM_TEMPLATE = 1;
  static const int HASH_CHECK = 2;
  static const int TYPE_EXCHANGE = 3;
  static const int RESTORE = 4;
  static const int OBJECT_QUERY = 5;
  static const int DATA_QUERY = 6;
  static const int SAVE = 7;
  static const int DELETE = 8;
  static const int UNSUBSCRIBE = 9;
  static const int LOGIN = 10;
  static const int LOGIN_WITH_TOKEN = 11;
  static const int CONNECT = 12;
  static const int DISCONNECT = 13;
  static const int LOGOUT = 14;
  static const int OBJECTS = -1;
  static const int CHANNEL_MESSAGE = -2;
  // -3 -> CHANNEL_MESSAGE_ACK
  static const int RPC_MESSAGE = -4;

  int _retryCount = 0;
  _ConnectionStatus _status = _ConnectionStatus.Disconnected;
  D3EWebClient _client;
  ReferenceCatch _cache;
  String _sessionId;
  Stream<Resp> respStream;
  Map<DBObject, String> subscriptions = Map();
  Completer<bool> readyCompleter;
  Completer _reqInProgress;
  Completer _mutationInProgress;
  int _pendingMutation = 0;

  static MessageDispatch _dispatch;

  factory MessageDispatch.get() {
    if (_dispatch == null) {
      _dispatch = MessageDispatch();
    }
    return _dispatch;
  }

  MessageDispatch();

  Future<void> _init() async {
    EventBus.get().fire(ConnectionEvent(status: ConnectionStatus.Connecting));
    _status = _ConnectionStatus.Connecting;
    print('Status: $_status');
    readyCompleter = Completer<bool>();
    this._cache = ReferenceCatch.get();
    this._client = D3EWebClient.get();
    Stream st = this._client.broadcastStream();
    respStream = st.map((e) {
      WSReader reader = WSReader(_cache, BufferReader(e));
      int rid = reader.readInteger();
      return Resp(rid, reader);
    });
    respStream.listen((resp) {
      if (resp.id == OBJECTS) {
        _onObjects(resp);
      } else if (resp.id == CHANNEL_MESSAGE) {
        //read channel message
        _onChannelMessage(resp);
      }
    }, onDone: () {
      EventBus.get().fire(ConnectionEvent(status: ConnectionStatus.Connected));
      if (subscriptions.isNotEmpty) {
        _reconnect();
      }
    }, onError: (e) {
      EventBus.get()
          .fire(ConnectionEvent(status: ConnectionStatus.ConnectionFailed));
      if (subscriptions.isNotEmpty) {
        _reconnect();
      }
    }, cancelOnError: true);
    bool res = false;
    if (_sessionId != null) {
      print('Trying to restore session');
      res = await _restore();
    }
    if (!res) {
      await _confirmTemplate();
      String token = await LocalDataStore.get().getToken();
      if (token != null) {
        await _doAuth(token);
      }
      Channels.reset();
    } else {
      print('Restore Success');
    }
    _status = _ConnectionStatus.Ready;
    _retryCount = 0;
    readyCompleter.complete();
  }

  Future<void> _doAuth(String token) async {
    WSWriter writer = WSWriter(_cache);
    int mid = IdGenerator.get().next();
    writer.writeInteger(mid);
    writer.writeByte(LOGIN_WITH_TOKEN);
    writer.writeString(token);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((e) => e.id == mid).then((r) => r.reader);
    reader.readByte(); //Must be LOGIN_WITH_TOKEN
    int res = reader.readByte();
    if (res == 1) {
      EventBus.get().fire(ConnectionEvent(status: ConnectionStatus.AuthFailed));
      LocalDataStore.get().setUser(null, null);
    }
  }

  void _onObjects(Resp resp) {
    //print('OnObjects: start ' + resp.id.toString());
    WSReader reader = resp.reader;
    bool update = reader.readBoolean();
    int count = reader.readInteger();
    while (count > 0) {
      if (update) {
        DBObject obj = reader.readRef(0, null);
        if (obj != null) {
          obj.saveStatus = DBSaveStatus.Saved;
          print('OnObjects: ${obj.d3eType} ID: ' + obj.id.toString());
        }
      } else {
        DBObject obj =
            _cache.findObject(reader.readInteger(), reader.readInteger());
        if (obj != null) {
          obj.saveStatus = DBSaveStatus.Deleted;
        }
      }
      count--;
    }
    reader.done();
    //print('OnObjects: end ' + resp.id.toString());
  }

  void _onChannelMessage(Resp resp) {
    Channels.onMessage(resp.reader);
  }

  void _reconnect() {
    _status = _ConnectionStatus.Disconnected;
    this._client?.disconnect();
    int ms = 2 ^ _retryCount;
    int timeOut = min(ms, 1000) * 10;
    print('Will reconnect on $timeOut ms : $ms');
    Timer(Duration(milliseconds: timeOut), () async {
      _retryCount++;
      await _init();
    });
  }

  Future<bool> _restore() async {
    WSWriter writer = WSWriter(_cache);
    int mid = IdGenerator.get().next();
    writer.writeInteger(mid);
    writer.writeByte(RESTORE);
    writer.writeString(_sessionId);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((e) => e.id == mid).then((r) => r.reader);
    reader.readByte(); //Must be CONFIRM_TEMPLATE
    int res = reader.readByte();
    if (res == 0) {
      _sessionId = reader.readString();
      return true;
    }
    EventBus.get()
        .fire(ConnectionEvent(status: ConnectionStatus.RestoreFailed));
    _sessionId = null;
    return false;
  }

  Future<bool> _confirmTemplate() async {
    _status = _ConnectionStatus.TypeExchange;
    print('Status: $_status');
    WSWriter writer = WSWriter(_cache);
    int mid = IdGenerator.get().next();
    writer.writeInteger(mid);
    writer.writeByte(CONFIRM_TEMPLATE);
    writer.writeString(D3ETemplate.HASH);
    writer.writeInteger(-1);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((e) => e.id == mid).then((r) => r.reader);
    reader.readByte(); //Must be CONFIRM_TEMPLATE

    this._sessionId = reader.readString();
    int res = reader.readByte();
    if (res == 0) {
      return true;
    }

    //Hash Check
    writer = WSWriter(_cache);
    mid = IdGenerator.get().next();
    writer.writeInteger(mid);
    writer.writeByte(HASH_CHECK);

    //Write all types & usages
    List<D3ETemplateType> types = D3ETemplate.types;
    writer.writeInteger(types.length);
    List<D3EUsage> usages = D3ETemplate.usages;
    writer.writeInteger(usages.length);

    // Channels
    writer.writeInteger(ChannelConstants.TOTAL_CHANNEL_COUNT);

    // RPCs
    writer.writeInteger(RPCConstants.TOTAL_RPC_CLASS_COUNT);

    for (D3ETemplateType t in types) {
      writer.writeString(t.hash);
    }
    for (D3EUsage u in usages) {
      writer.writeString(u.hash);
    }
    for (D3ETemplateClass c in ChannelConstants.channels) {
      writer.writeString(c.hash);
    }
    for (D3ETemplateClass c in RPCConstants.classes) {
      writer.writeString(c.hash);
    }

    _client.send(writer.out);
    reader =
        await respStream.firstWhere((e) => e.id == mid).then((r) => r.reader);
    reader.readByte(); //Must be HASH_CHECK
    res = reader.readByte();
    if (res == 0) {
      return true;
    }
    List<int> unknownTypes = reader.readIntegerList();
    List<int> unknownUsages = reader.readIntegerList();
    List<int> unknownChannels = reader.readIntegerList();
    List<int> unknownRPClasses = reader.readIntegerList();

    //Type Exchange
    writer = WSWriter(_cache);
    mid = IdGenerator.get().next();
    writer.writeInteger(mid);
    writer.writeByte(TYPE_EXCHANGE);

    //Types
    writer.writeInteger(unknownTypes.length);
    unknownTypes.forEach((e) {
      writer.writeInteger(e);
      D3ETemplateType t = D3ETemplate.types[e];
      writer.writeString(t.name);
      writer.writeInteger(t.parent);
      writer.writeInteger(t.fields.length);
      for (D3ETemplateField f in t.fields) {
        writer.writeString(f.name);
        writer.writeInteger(f.type);
      }
    });

    //Usages
    writer.writeInteger(unknownUsages.length);
    unknownUsages.forEach((e) {
      writer.writeInteger(e);
      D3EUsage u = D3ETemplate.usages[e];
      writer.writeInteger(u.types.length);
      for (D3ETypeUsage ut in u.types) {
        _writeTypeUsage(ut, writer);
      }
    });

    // Channels
    writer.writeInteger(unknownChannels.length);
    unknownChannels.forEach((e) {
      writer.writeInteger(e);
      D3ETemplateClass c = ChannelConstants.channels[e];
      writer.writeString(c.name);
      writer.writeInteger(c.methods.length);
      for (D3ETemplateMethod tm in c.methods) {
        writer.writeString(tm.name);
        writer.writeInteger(tm.params.length);
        for (D3ETemplateParam tp in tm.params) {
          writer.writeInteger(tp.type);
          writer.writeBoolean(tp.collection);
        }
      }
    });

    // Remote Procedure Calls
    writer.writeInteger(unknownRPClasses.length);
    unknownRPClasses.forEach((e) {
      writer.writeInteger(e);
      D3ETemplateClass c = RPCConstants.classes[e];
      writer.writeString(c.name);
      writer.writeInteger(c.methods.length);
      for (D3ETemplateMethodWithReturn tm in c.methods) {
        writer.writeString(tm.name);
        writer.writeInteger(tm.params.length);
        for (D3ETemplateParam tp in tm.params) {
          writer.writeInteger(tp.type);
          writer.writeBoolean(tp.collection);
        }
        writer.writeInteger(tm.returnType);
        writer.writeBoolean(tm.returnCollection);
      }
    });

    _client.send(writer.out);
    reader =
        await respStream.firstWhere((e) => e.id == mid).then((r) => r.reader);
    reader.readByte(); //Must be TYPE_EXCHANGE
    List<int> unusedTypes = reader.readIntegerList();
    unusedTypes.forEach((type) {
      D3ETemplate.types[type].unknown = true;
    });
    int typesWithUnusedFieldsCount = reader.readInteger();
    for (int x = 0; x < typesWithUnusedFieldsCount; x++) {
      int type = reader.readInteger();
      List<int> unusedFields = reader.readIntegerList();
      unusedFields.forEach((field) {
        D3ETemplate.types[type].fields[field].unknown = true;
      });
    }
    return true;
  }

  Future<void> checkAndInit() async {
    print('Status: $_status');
    if (_status == _ConnectionStatus.Ready) {
      return;
    } else if (_status == _ConnectionStatus.TypeExchange ||
        _status == _ConnectionStatus.Connecting) {
      await readyCompleter.future;
    } else if (_status == _ConnectionStatus.Disconnected) {
      await _init();
    }
  }

  void _writeTypeUsage(D3ETypeUsage u, WSWriter reader) {
    reader.writeInteger(u.type);
    reader.writeInteger(u.fields.length);
    for (D3EFieldUsage f in u.fields) {
      reader.writeInteger(f.field);
      if (f.types == null || f.types.isEmpty) {
        reader.writeInteger(0);
      } else {
        reader.writeInteger(f.types.length);
        for (D3ETypeUsage tu in f.types) {
          _writeTypeUsage(tu, reader);
        }
      }
    }
  }

  Future<T> query<T>(int type, int id, int usage) async {
    await checkAndInit();
    await waitForAccess(false);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    print('Query: ' +
        qid.toString() +
        ' - ' +
        type.toString() +
        ' - ' +
        id.toString());
    writer.writeInteger(qid);
    writer.writeByte(OBJECT_QUERY);
    writer.writeInteger(type);
    writer.writeBoolean(false);
    writer.writeInteger(usage);
    writer.writeInteger(id);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    print('Done: ' +
        qid.toString() +
        ' - ' +
        type.toString() +
        ' - ' +
        id.toString());
    reader.readByte(); // Must be OBJECT_QUERY
    int res = reader.readInteger();
    releaseAccess(false);
    if (res == 0) {
      DBObject obj = reader.readRef(0, null);
      reader.done();
      return obj as T;
    } else {
      List<String> errors = reader.readStringList();
      print('ObjectQuery Errors: ' + errors.toString());
      return null;
    }
  }

  Future<bool> waitForAccess(bool isMutation) async {
    if (isMutation) {
      if (!allowParallelMutation) {
        if (_mutationInProgress != null && !_mutationInProgress.isCompleted) {
          await _mutationInProgress.future;
        }
        _mutationInProgress = Completer();
      }
    }
    if (!allowParallelReq) {
      while (_reqInProgress != null && !_reqInProgress.isCompleted) {
        await _reqInProgress.future;
      }
      _reqInProgress = Completer();
    }
    return true;
  }

  void releaseAccess(bool isMutation) {
    if (isMutation) {
      bool wasBusy = _pendingMutation > 2;
      _pendingMutation--;
      bool isBusy = _pendingMutation > 2;
      if (wasBusy && !isBusy) {
        EventBus.get()
            .fire(ConnectionEvent(status: ConnectionStatus.ConnectionNormal));
      }
      _mutationInProgress?.complete();
      _mutationInProgress = null;
    }
    _reqInProgress?.complete();
    _reqInProgress = null;
  }

  Future<T> dataQuery<T>(String query, int usage, bool hasInput, DBObject input,
      {bool synchronize = false}) async {
    await checkAndInit();
    await waitForAccess(false);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    print('DataQuery: ' +
        qid.toString() +
        ' - ' +
        query +
        ' - ' +
        (hasInput ? input.d3eType : 'null'));
    writer.writeInteger(qid);
    writer.writeByte(DATA_QUERY);
    writer.writeString(query);
    writer.writeBoolean(synchronize);
    writer.writeInteger(usage);
    if (hasInput) {
      writer.writeObj(input);
    }
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    print('Done: ' +
        qid.toString() +
        ' - ' +
        query +
        ' - ' +
        (hasInput ? input.d3eType : 'null'));
    reader.readByte(); // Must be DATA_QUERY
    int res = reader.readInteger();
    releaseAccess(false);
    if (res == 0) {
      String subId;
      if (synchronize) {
        subId = reader.readString();
      }
      DBObject obj = reader.readRef(0, null);
      if (synchronize && subId != null) {
        this.subscriptions[obj] = subId;
      }
      reader.done();
      return obj as T;
    } else {
      List<String> errors = reader.readStringList();
      print('DataQuery Errors: ' + errors.toString());
      return null;
    }
  }

  void checkAndSendBusy() {
    bool wasBusy = _pendingMutation > 2;
    _pendingMutation++;
    bool isBusy = _pendingMutation > 2;
    if (isBusy && !wasBusy) {
      EventBus.get()
          .fire(ConnectionEvent(status: ConnectionStatus.ConnectionBusy));
    }
  }

  Future<DBResult> save(DBObject input) async {
    checkAndSendBusy();
    await checkAndInit();
    await waitForAccess(true);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    print('Save: ' +
        qid.toString() +
        ' - ' +
        input.d3eType +
        ' - ' +
        input.id.toString());
    writer.writeInteger(qid);
    writer.writeByte(SAVE);
    writer.writeObjFull(input);
    Map<DBObject, D3EObjectChanges> changes = Map();
    backupObjectChanges(input, changes);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    print('Done: ' +
        qid.toString() +
        ' - ' +
        input.d3eType +
        ' - ' +
        input.id.toString());
    reader.readByte(); // Must be SAVE
    int res = reader.readByte();
    releaseAccess(true);
    if (res == 0) {
      int size = reader.readInteger();
      for (int i = 0; i < size; i++) {
        int type = reader.readInteger();
        D3ETemplateType t = D3ETemplate.types[type];
        int localId = reader.readInteger();
        int id = reader.readInteger();
        _cache.updateLocalId(type, localId, id);
      }
      Object ref = reader.readRef(0, null);
      reader.done();
      return DBResult(status: DBResultStatus.Success);
    } else {
      restoreObjectChanges(input, changes);
      return DBResult(
          status: DBResultStatus.Errors, errors: reader.readStringList());
    }
  }

  void backupObjectChanges(
      DBObject obj, Map<DBObject, D3EObjectChanges> changes) {
    int type = D3ETemplate.typeInt(obj.d3eType);
    D3ETemplateType tt = D3ETemplate.types[type];
    while (tt != null) {
      changes[obj] = obj.d3eChanges;
      obj.d3eChanges = D3EObjectChanges();
      int index = tt.parentFields;
      for (D3ETemplateField f in tt.fields) {
        if (f.child) {
          if (f.collection) {
            List values = obj.get(index);
            values.forEach((e) => backupObjectChanges(e, changes));
          } else {
            DBObject o = obj.get(index);
            if (o != null) {
              backupObjectChanges(o, changes);
            }
          }
        }
        index++;
      }
      if (tt.parent != 0) {
        tt = D3ETemplate.types[tt.parent];
      } else {
        tt = null;
      }
    }
  }

  void restoreObjectChanges(
      DBObject obj, Map<DBObject, D3EObjectChanges> changes) {
    int type = D3ETemplate.typeInt(obj.d3eType);
    D3ETemplateType tt = D3ETemplate.types[type];
    while (tt != null) {
      obj.d3eChanges = changes[obj];
      int index = tt.parentFields;
      for (D3ETemplateField f in tt.fields) {
        if (f.child) {
          if (f.collection) {
            List values = obj.get(index);
            values.forEach((e) => restoreObjectChanges(e, changes));
          } else {
            DBObject o = obj.get(index);
            if (o != null) {
              restoreObjectChanges(o, changes);
            }
          }
        }
        index++;
      }
      if (tt.parent != 0) {
        tt = D3ETemplate.types[tt.parent];
      } else {
        tt = null;
      }
    }
  }

  Future<DBResult> delete(DBObject input) async {
    checkAndSendBusy();
    await checkAndInit();
    await waitForAccess(true);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    writer.writeInteger(qid);
    writer.writeByte(DELETE);
    String type = input.d3eType;
    int typeIdx = D3ETemplate.typeInt(type);
    writer.writeInteger(typeIdx);
    writer.writeInteger(input.id);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    reader.readByte(); // Must be DELETE
    int res = reader.readByte();
    releaseAccess(true);
    if (res == 0) {
      return DBResult(status: DBResultStatus.Success);
    } else {
      return DBResult(
          status: DBResultStatus.Errors, errors: reader.readStringList());
    }
  }

  Future<LoginResult> login(String type, int usage,
      {String email,
      String phone,
      String username,
      String password,
      String deviceToken,
      String token,
      String code}) async {
    await checkAndInit();
    await waitForAccess(false);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    writer.writeInteger(qid);
    writer.writeByte(LOGIN);
    writer.writeInteger(usage);
    writer.writeString(type);
    writer.writeString(email);
    writer.writeString(phone);
    writer.writeString(username);
    writer.writeString(password);
    writer.writeString(deviceToken);
    writer.writeString(token);
    writer.writeString(code);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    reader.readByte(); // Must be LOGIN
    int restInt = reader.readByte(); // Response
    releaseAccess(false);
    if (restInt == 1) {
      return LoginResult(
          success: false, failureMessage: reader.readStringList().toString());
    }
    LoginResult res = reader.readRef(0, null);
    reader.done();
    if (res.success) {
      try {
        LocalDataStore.get().setUser(res.userObject, res.token);
      } catch (e) {
        print('Exception: ' + e.toString());
      }
    }
    return res;
  }

  Future<void> logout() async {
    await checkAndInit();
    await waitForAccess(false);
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    writer.writeInteger(qid);
    writer.writeByte(LOGOUT);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    reader.readByte(); // Must be LOGOUT
    int restInt = reader.readByte(); // Response
    releaseAccess(false);
  }

  D3EDisposable syncObject(DBObject obj, int usage) {
    ObjectSyncDisposable dis = ObjectSyncDisposable(null);
    if (obj == null) {
      return dis;
    }
    print('Sync Object : ' + obj.d3eType);
    Timer.run(() async {
      String subId = await _syncObject(obj, usage);
      if (subId != null) {
        if (dis.disposed) {
          _unsubscribe(subId);
        } else {
          dis.subId = subId;
        }
      }
    });
    return dis;
  }

  Future<String> _syncObject(DBObject obj, int usage) async {
    await checkAndInit();
    await waitForAccess(false);
    String type = obj.d3eType;
    int typeIndex = D3ETemplate.typeInt(type);
    int id = obj.id;

    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    print('Query: ' +
        qid.toString() +
        ' - ' +
        type.toString() +
        ' - ' +
        id.toString());
    writer.writeInteger(qid);
    writer.writeByte(OBJECT_QUERY);
    writer.writeInteger(typeIndex);
    writer.writeBoolean(true);
    writer.writeInteger(usage);
    writer.writeInteger(id);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == qid).then((m) => m.reader);
    print('Done: ' +
        qid.toString() +
        ' - ' +
        type.toString() +
        ' - ' +
        id.toString());
    reader.readByte(); // Must be OBJECT_QUERY
    int res = reader.readInteger();
    releaseAccess(false);
    if (res == 0) {
      String subId = reader.readString();
      reader.readRef(0, null);
      reader.done();
      return subId;
    } else {
      return null;
    }
  }

  void dispose(DBObject obj) {
    String subId = this.subscriptions[obj];
    if (subId != null) {
      _unsubscribe(subId);
    }
  }

  Future<void> _unsubscribe(String subId) async {
    await checkAndInit();
    WSWriter writer = WSWriter(_cache);
    int qid = IdGenerator.get().next();
    print('Unsubscribe: ' + qid.toString());
    writer.writeInteger(qid);
    writer.writeByte(UNSUBSCRIBE);
    writer.writeString(subId);
    _client.send(writer.out);
  }

  void close() {
    _status = _ConnectionStatus.Disconnected;
    this.subscriptions.clear();
    this._sessionId = null;
    this._client.disconnect();
  }

  Future<bool> connect(int channelIdx) async {
    await checkAndInit();
    WSWriter writer = WSWriter(_cache);
    int id = IdGenerator.get().next();
    writer.writeInteger(id);
    writer.writeByte(CONNECT);
    writer.writeInteger(channelIdx);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == id).then((m) => m.reader);
    reader.readByte(); // CONNECT
    int code = reader.readByte();
    if (code == 1) {
      return false;
    }
    return true;
  }

  Future<bool> disconnect(int channelIdx) async {
    await checkAndInit();
    WSWriter writer = WSWriter(_cache);
    int id = IdGenerator.get().next();
    writer.writeInteger(id);
    writer.writeByte(DISCONNECT);
    writer.writeInteger(channelIdx);
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == id).then((m) => m.reader);
    reader.readByte(); // DISCONNECT
    int code = reader.readByte();
    if (code == 1) {
      return false;
    }
    bool result = reader.readBoolean();
    return result;
  }

  // Called when client wants to send a message to server
  WSWriter channelMessage(int channelIdx, int msgIdx) {
    WSWriter writer = WSWriter(_cache);
    writer.writeInteger(CHANNEL_MESSAGE);
    writer.writeInteger(channelIdx);
    writer.writeInteger(msgIdx);
    return writer;
  }

  // RPC method
  Future<WSReader> rpcMessage(int clsIdx, int methodIdx,
      {Consumer<WSWriter> args}) async {
    await checkAndInit();
    print(' w init done');
    WSWriter writer = WSWriter(_cache);
    int id = IdGenerator.get().next();
    writer.writeInteger(id);
    writer.writeByte(RPC_MESSAGE);
    print(' w RPC message: id: ' +
        id.toString() +
        ', cls: ' +
        clsIdx.toString() +
        ', method: ' +
        methodIdx.toString());
    writer.writeInteger(clsIdx);
    writer.writeInteger(methodIdx);
    if (args != null) {
      args(writer);
    }
    _client.send(writer.out);
    WSReader reader =
        await respStream.firstWhere((m) => m.id == id).then((m) => m.reader);
    reader.readByte(); // RPC_MESSAGE
    return reader;
  }

  void send(WSWriter w) {
    if (w == null) {
      return;
    }
    _client.send(w.out);
  }
}

import '../classes/core.dart';
import 'MessageDispatch.dart';

typedef void DisconnectCallBack();

class ClientChannel<S, C> {
  C _client;
  S server;
  int channelIdx;
  DisconnectCallBack onDisconnect;

  ClientChannel(this.channelIdx, this.server, this.onDisconnect);

  Future<S> connect(C client) async {
    _client = client;
    await MessageDispatch.get().connect(channelIdx);
    return server;
  }

  Future<bool> disconnect() async {
    await MessageDispatch.get().disconnect(channelIdx);
    onDisconnect();
    return true;
  }

  C get client => _client;
}

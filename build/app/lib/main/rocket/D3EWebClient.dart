import '../classes/Env.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class D3EWebClient {
  String _url;

  WebSocketChannel _channel;

  D3EWebClient._init(this._url) {
    this._connect();
  }

  static D3EWebClient _ins;

  factory D3EWebClient.get() {
    if (_ins == null) {
      String url = Env.get().baseWSurl + '/api/rocket';
      _ins = D3EWebClient._init(url);
    }
    return _ins;
  }

  void _connect() {
    _channel = WebSocketChannel.connect(Uri.parse(_url));
  }

  void disconnect() {
    try {
      _channel.sink.close();
    } catch (e) {
      _channel = null;
    }
    _ins = null;
  }

  void send(List<int> msg) {
    _channel.sink.add(msg);
  }

  Stream broadcastStream() {
    return _channel.stream.asBroadcastStream();
  }
}

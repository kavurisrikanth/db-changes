import 'package:global_configuration/global_configuration.dart';

class Env {
  static Env _configObj;
  String _baseHttpUrl;
  String _baseWSurl;
  Env._init();
  factory Env.get() {
    if (_configObj == null) {
      _configObj = Env._init();
    }

    return _configObj;
  }
  void load(GlobalConfiguration configuration) {
    _baseHttpUrl = configuration.getValue<String>('baseHttpUrl');

    _baseWSurl = configuration.getValue<String>('baseWSurl');
  }

  String get baseHttpUrl {
    return _baseHttpUrl;
  }

  String get baseWSurl {
    return _baseWSurl;
  }
}

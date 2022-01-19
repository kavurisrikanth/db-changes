import 'main/rocket/MessageDispatch.dart';
import 'main/classes/Env.dart';
import 'package:flutter/widgets.dart';
import 'MyApp.dart';
import 'package:global_configuration/global_configuration.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  (await GlobalConfiguration().loadFromPath('resource/settings.json'));

  Env.get().load(GlobalConfiguration());

  runApp(MyApp());
}

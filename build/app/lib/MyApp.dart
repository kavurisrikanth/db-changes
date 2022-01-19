import 'main/classes/PageNavigator.dart';
import 'main/components/WelcomePage.dart';
import 'main/components/ThemeWrapper.dart';
import 'package:flutter/widgets.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/services.dart';
import 'main/components/ThemeBuilder.dart';

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    StyleThemeData.current = StyleThemeData.createprimary();

    GoogleFonts.poppins();

    return ThemeBuilder(builder: (context) {
      return WidgetsApp(
          shortcuts:
              Map<ShortcutActivator, Intent>.from(WidgetsApp.defaultShortcuts)
                ..addAll(<LogicalKeySet, Intent>{
                  LogicalKeySet(LogicalKeyboardKey.arrowDown):
                      DirectionalFocusIntent(TraversalDirection.down),
                  LogicalKeySet(LogicalKeyboardKey.arrowUp):
                      DirectionalFocusIntent(TraversalDirection.up),
                  LogicalKeySet(LogicalKeyboardKey.arrowLeft):
                      DirectionalFocusIntent(TraversalDirection.left),
                  LogicalKeySet(LogicalKeyboardKey.arrowRight):
                      DirectionalFocusIntent(TraversalDirection.right)
                }),
          title: 'ChangesTest - App',
          textStyle: StyleThemeData.current.textStyle,
          color: StyleThemeData.current.color,
          onGenerateRoute: (rs) {
            return PageRouteBuilder(
                pageBuilder: (ctx, _, __) =>
                    PageNavigator.of(ctx).wrapTheme(WelcomePage()));
          });
    });
  }
}

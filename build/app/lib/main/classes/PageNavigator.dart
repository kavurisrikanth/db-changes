import '../components/PageRouter.dart';
import '../components/ThemeBuilder.dart';
import '../components/ThemeWrapper.dart';
import '../components/WelcomePage.dart';
import 'dart:html';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';

class PageNavigator {
  BuildContext ctx;
  PageNavigator(this.ctx);
  static PageNavigator of(BuildContext ctx) {
    return PageNavigator(ctx);
  }

  Widget wrapTheme(Widget widget) {
    MediaQueryData mediaQuery = MediaQuery.of(ctx);

    return ThemeWrapper(
        data: StyleThemeData.current,
        child: Overlay(initialEntries: [
          OverlayEntry(
              builder: (_) =>
                  Padding(padding: mediaQuery.padding, child: widget))
        ]));
  }

  void push(
      bool replace, Widget widget, String title, String path, String target) {
    if (kIsWeb) {
      if (replace) {
        window.history.replaceState(null, title, path);
      } else {
        window.history.pushState(null, title, path);
      }
    }

    widget = wrapTheme(widget);

    NavigatorState state = target == null
        ? Navigator.of(ctx, rootNavigator: true)
        : PageRouter.routers[target];

    if (replace) {
      state.pushReplacement(
          PageRouteBuilder(pageBuilder: (ctx, _, __) => widget));
    } else {
      state.push(PageRouteBuilder(pageBuilder: (ctx, _, __) => widget));
    }
  }

  void pop() {
    Navigator.pop(ctx);
  }

  void pushWelcomePage({bool replace = true, String target}) {
    push(replace, WelcomePage(), '', '#welcome page', target);
  }

  void applyTheme({String theme}) {
    switch (theme) {
      case 'primary':
        {
          StyleThemeData.current = StyleThemeData.createprimary();

          ThemeBuilder.of(this.ctx).rebuild();
          break;
        }
      default:
        {
          StyleThemeData.current = StyleThemeData.createprimary();

          ThemeBuilder.of(this.ctx).rebuild();
        }
    }
  }

  List<String> getProjectThemeNames() {
    return ['primary'];
  }

  String getCurrentThemeName() {
    return StyleThemeData.current.themeName;
  }
}

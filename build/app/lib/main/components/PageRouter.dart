import 'dart:async';
import '../classes/PageNavigator.dart';
import '../utils/ObservableState.dart';
import 'package:flutter/widgets.dart';

class PageRouter extends StatefulWidget {
  static Map<String, NavigatorState> routers = Map();
  static void register(String target, NavigatorState state) {
    routers[target] = state;
  }

  static void unregister(String name) {
    routers.remove(name);
  }

  final String target;

  PageRouter({Key key, this.target}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _PageRouterState();
}

class _Observer extends NavigatorObserver {}

class _PageRouterState extends ObservableState<PageRouter> {
  final _Observer observer = _Observer();
  void initState() {
    super.initState();
    Timer(Duration(milliseconds: 100), () {
      PageRouter.register(widget.target, observer.navigator);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Navigator(
      restorationScopeId: widget.target,
      onGenerateRoute: (rs) {
        return PageRouteBuilder(
            pageBuilder: (ctx, _, __) =>
                PageNavigator.of(ctx).wrapTheme(Container()));
      },
      onGenerateInitialRoutes: Navigator.defaultGenerateInitialRoutes,
      observers: [observer],
    );
  }

  void dispose() {
    super.dispose();
    PageRouter.unregister(widget.target);
  }
}

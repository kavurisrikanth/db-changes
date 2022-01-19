import 'package:flutter/material.dart';
import '../utils/ObservableState.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter/foundation.dart' show kIsWeb;

class ScrollView2 extends StatefulWidget {
  final Widget child;
  final Axis scrollDirection;
  ScrollView2({Key key, this.child, this.scrollDirection}) : super(key: key);
  @override
  _ScrollViewState createState() => _ScrollViewState();
}

class ScrollData {
  double _dx = 0;
  double _dy = 0;
  ScrollData parent;
  double get dx => parent != null ? parent.dx + _dx : _dx;
  double get dy => parent != null ? parent.dy + _dy : _dy;
}

class ScrollWrapper extends InheritedWidget {
  final ScrollData data;
  ScrollWrapper({@required this.data, @required child}) : super(child: child);
  bool updateShouldNotify(ScrollWrapper old) => data != old.data;
  static ScrollData of(BuildContext context) {
    final ScrollWrapper wrapper =
        context.dependOnInheritedWidgetOfExactType<ScrollWrapper>();

    return wrapper?.data;
  }
}

class _ScrollViewState extends ObservableState<ScrollView2> {
  final ScrollController controller = ScrollController();
  final ScrollData data = ScrollData();

  @override
  void initState() {
    super.initState();
    controller.addListener(() {
      if (widget.scrollDirection == Axis.horizontal) {
        data._dx = controller.offset;
      } else {
        data._dy = controller.offset;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    ScrollData parent = ScrollWrapper.of(context);
    data.parent = parent;
    if (kIsWeb) {
      return ScrollWrapper(
          data: data,
          child: Scrollbar(
              isAlwaysShown: true,
              controller: controller,
              thickness: 12.0,
              interactive: true,
              hoverThickness: 12,
              radius: Radius.zero,
              child: SingleChildScrollView(
                child: Container(
                    margin: widget.scrollDirection == Axis.vertical
                        ? EdgeInsets.only(right: 15)
                        : EdgeInsets.only(bottom: 15),
                    child: widget.child),
                controller: controller,
                scrollDirection: widget.scrollDirection,
              )));
    } else {
      return ScrollWrapper(
          data: data,
          child: SingleChildScrollView(
            child: widget.child,
            controller: controller,
            scrollDirection: widget.scrollDirection,
          ));
    }
  }

  @override
  void dispose() {
    super.dispose();
  }
}

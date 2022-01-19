import 'package:flutter/widgets.dart' hide Image;
import 'package:flutter/scheduler.dart';

class LayoutBounds extends StatefulWidget {
  final Widget child;
  LayoutBounds({this.child});

  @override
  LayoutBoundsState createState() => LayoutBoundsState();

  static LayoutBoundsState of(BuildContext context) {
    final LayoutBoundsState result =
        context.findAncestorStateOfType<LayoutBoundsState>();
    return result;
  }
}

class LayoutBoundsState extends State<LayoutBounds> {
  RenderBox box;

  @override
  void initState() {
    super.initState();
    SchedulerBinding.instance.addPostFrameCallback((_) {
      box = context.findRenderObject() as RenderBox;
    });
  }

  @override
  Widget build(BuildContext context) {
    return this.widget.child;
  }
}

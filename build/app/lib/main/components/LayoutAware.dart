import 'package:flutter/widgets.dart' hide Image;
import 'package:flutter/scheduler.dart';
import 'dart:async';
import 'LayoutBounds.dart';

typedef void LayoutCallback(Rect bounds, Offset globalPos);

class LayoutAware extends StatefulWidget {
  final Widget child;
  final LayoutCallback onBoundsChange;
  LayoutAware({this.child, this.onBoundsChange});

  @override
  _LayoutAwareState createState() => _LayoutAwareState();
}

class _LayoutAwareState extends State<LayoutAware> {
  Rect bounds;
  Timer _timer;

  @override
  void initState() {
    super.initState();
  }

  void update() {
    if (this.widget.onBoundsChange != null) {
      _timer = Timer(Duration(milliseconds: 20), () {
        if (_timer == null) {
          return;
        }
        RenderBox box = context.findRenderObject() as RenderBox;
        LayoutBoundsState layoutBounds = LayoutBounds.of(context);
        RenderBox bounds = layoutBounds?.box;
        final pos = box.localToGlobal(Offset.zero, ancestor: bounds);
        final globalPos = box.localToGlobal(Offset.zero);
        final size = box.size;
        Rect rect = new Rect.fromLTWH(pos.dx, pos.dy, size.width, size.height);
        if (this.bounds != rect) {
          this.bounds = rect;
          this.widget.onBoundsChange(rect, globalPos);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    update();
    return this.widget.child;
  }

  @override
  void dispose() {
    _timer?.cancel();
    _timer = null;
    super.dispose();
  }
}

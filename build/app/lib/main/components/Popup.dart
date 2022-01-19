import 'dart:async';
import 'dart:math' as math;
import '../utils/EventBus.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'LayoutAware.dart';
import 'PopupEvent.dart';

typedef void _OnShowChange();

typedef PopupBuilder = Widget Function(BuildContext context);

enum PopUpPosition { Center, Top, Left, Right, Bottom, Positioned }

class Popup {
  Popup(
      {Key key,
      this.verticalOffset = 0.0,
      this.horizontalOffset = 0.0,
      this.position = PopUpPosition.Center,
      this.float = false,
      this.model = true,
      this.autoClose = true,
      this.onClose,
      this.constraints,
      this.showConnector = false,
      this.positionX = 0.0,
      this.positionY = 0.0,
      this.target,
      @required this.child,
      this.color})
      : assert(child != null);

  final Widget child;

  final bool model;

  final bool autoClose;

  final bool float;

  final BoxConstraints constraints;

  final double verticalOffset;

  final double horizontalOffset;

  final PopUpPosition position;

  final _OnShowChange onClose;

  final bool showConnector;

  final Color color;

  final double positionX;

  final double positionY;

  final Rect target;

  static const bool _defaultPreferBelow = true;
  static const bool _defaultShowConnector = false;
  static const BoxConstraints _defaultConstriants = BoxConstraints();
  static int openPopupsCount = 0;

  bool preferBelow;
  OverlayEntry _entry;
  Offset currentPos;
  Rect childRect;
  Rect _placeHolderBounds;
  Offset _mouseDownAt;
  Offset _positionAtMouseDown;
  FocusNode _lastFocus;

  void initState() {
    GestureBinding.instance.pointerRouter.addGlobalRoute(_handlePointerEvent);
  }

  void _mouseDown(Offset offset) {
    this._mouseDownAt = offset;
    _positionAtMouseDown = currentPos;
  }

  void _mouseMove(Offset offset) {
    currentPos = _positionAtMouseDown + (offset - _mouseDownAt);
    _entry?.markNeedsBuild();
  }

  void _mouseUp(Offset offset) {
    currentPos = _positionAtMouseDown + (offset - _mouseDownAt);
    _entry?.markNeedsBuild();
  }

  void boundsCallBack(Offset offset, Size childSize) {
    currentPos = offset;
    childRect = offset & childSize;
  }

  void hidePopup({bool closeCall = false}) {
    if (closeCall && this.onClose != null) {
      this.onClose();
      //ToSend Event when hiding by clicking outside the popup
    }
    openPopupsCount--;
    EventBus.get().fire(PopupEvent(openPopupsCount));
    _removeEntry();
    _lastFocus?.requestFocus();
  }

  void showPopup(BuildContext context) {
    if (_entry != null) {
      return;
    }
    openPopupsCount++;
    this._lastFocus = FocusManager.instance.primaryFocus;
    EventBus.get().fire(PopupEvent(openPopupsCount));
    initState();
    _createNewEntry(context);
  }

  void _createNewEntry(BuildContext context) {
    // We create this widget outside of the overlay entry's builder to prevent
    // updated values from happening to leak into the overlay when the overlay
    // rebuilds.
    _entry = OverlayEntry(
      builder: (BuildContext context) => Directionality(
        textDirection: Directionality.of(context),
        child: _PopupOverlay(
            boundsCallBack: boundsCallBack,
            popUpchild: child,
            model: model,
            float: float,
            mouseDown: _mouseDown,
            mouseMove: _mouseMove,
            mouseUp: _mouseUp,
            offset: currentPos,
            verticalOffset: verticalOffset,
            horizontalOffset: horizontalOffset,
            positionX: positionX,
            positionY: positionY,
            position: position,
            constraints: constraints,
            showConnector: showConnector,
            autoClose: autoClose,
            target: target,
            color: color),
      ),
    );

    Overlay.of(context).insert(_entry);
  }

  void _removeEntry() {
    _entry?.remove();
    _entry = null;
  }

  void _handlePointerEvent(PointerEvent event) {
    if (_entry == null || !(event is PointerDownEvent)) {
      return;
    }
    if (autoClose && !childRect.contains(event.position)) {
      hidePopup(closeCall: true);
    }
  }

  void _onPlaceholderBounds(Rect bounds, Offset offset) {
    _placeHolderBounds = bounds;
  }

  void dispose() {
    if (_entry != null) {
      GestureBinding.instance.pointerRouter
          .removeGlobalRoute(_handlePointerEvent);
      openPopupsCount--;
      EventBus.get().fire(PopupEvent(openPopupsCount));
      _removeEntry();
      _lastFocus?.requestFocus();
    }
  }
}

/// A delegate for computing the layout of a popup to be displayed above or
/// bellow a target specified in the global coordinate system.
typedef void _BoundsCall(Offset offset, Size size);

class _PopupPositionDelegate extends SingleChildLayoutDelegate {
  /// Creates a delegate for computing the layout of a tooltip.
  ///
  /// The arguments must not be null.
  _PopupPositionDelegate({
    @required this.verticalOffset,
    @required this.horizontalOffset,
    @required this.position,
    this.bounds,
    this.target,
  }) : assert(verticalOffset != null);

  // /// The offset of the target the tooltip is positioned near in the global
  // /// coordinate system.
  Rect target;

  /// The amount of vertical distance between the target and the displayed
  /// tooltip.
  final double verticalOffset;

  /// The amount of vertical distance between the target and the displayed
  /// tooltip.
  final double horizontalOffset;

  /// Whether the tooltip is displayed below its widget by default.
  ///
  /// If there is insufficient space to display the tooltip in the preferred
  /// direction, the tooltip will be displayed in the opposite direction.
  final PopUpPosition position;

  final _BoundsCall bounds;

  @override
  BoxConstraints getConstraintsForChild(BoxConstraints constraints) =>
      constraints.loosen();

  @override
  Offset getPositionForChild(Size size, Size childSize) {
    Offset positionBounds = positionDependentBox(
      target: target,
      size: size,
      childSize: childSize,
      verticalOffset: verticalOffset,
      horizontalOffset: horizontalOffset,
      side: position,
    );
    this.bounds(positionBounds, childSize);
    return positionBounds;
  }

  @override
  bool shouldRelayout(_PopupPositionDelegate oldDelegate) {
    return verticalOffset != oldDelegate.verticalOffset ||
        horizontalOffset != oldDelegate.horizontalOffset ||
        position != oldDelegate.position;
  }
}

typedef void BoundsUpdater(Rect rect, double scrollX, double scrollY);
typedef void _BoundsCallback(Offset offset, Size size);
typedef void _PointerCallback(Offset offset);

class _PopupOverlay extends StatelessWidget {
  const _PopupOverlay(
      {Key key,
      this.popUpchild,
      this.verticalOffset = 0,
      this.horizontalOffset = 0,
      this.position = PopUpPosition.Center,
      this.model = true,
      this.float = false,
      this.mouseDown,
      this.mouseUp,
      this.mouseMove,
      this.offset,
      this.boundsCallBack,
      this.constraints,
      this.showConnector,
      this.positionX = 0,
      this.positionY = 0,
      this.autoClose,
      this.target,
      this.color})
      : super(key: key);
  final Offset offset;
  final bool model;
  final _PointerCallback mouseMove;
  final _PointerCallback mouseDown;
  final _PointerCallback mouseUp;
  final bool float;
  final Widget popUpchild;
  final double verticalOffset;
  final double horizontalOffset;
  final PopUpPosition position;
  final _BoundsCallback boundsCallBack;
  final BoxConstraints constraints;
  final bool showConnector;
  final Color color;
  final double positionX;
  final double positionY;
  final bool autoClose;
  final Rect target;

  Widget body() {
    return LayoutAware(
        onBoundsChange: (r, o) => boundsCallBack(o, r.size),
        child: Listener(
          onPointerDown: (e) => mouseDown(e.localPosition),
          onPointerUp: (e) => mouseUp(e.localPosition),
          onPointerMove: (e) => mouseMove(e.localPosition),
          child: Container(color: Color(0xFFFFFFFF), child: popUpchild),
        ));
  }

  @override
  Widget build(BuildContext context) {
    MediaQueryData query = MediaQuery.of(context);
    Size size = query.size;
    Widget child;
    if (position == PopUpPosition.Center && float) {
      child = Stack(
        children: [
          AbsorbPointer(
            absorbing: true,
            child: Container(),
          ),
          if (model)
            Container(
              color: Color.fromARGB(20, 20, 20, 20),
              width: size.width,
              height: size.height,
            ),
          if (offset == null)
            Center(child: body())
          else
            Positioned(
              child: body(),
              left: offset.dx,
              top: offset.dy,
            )
        ],
      );
    } else if (position == PopUpPosition.Positioned) {
      child = Stack(children: [
        AbsorbPointer(
          absorbing: true,
          child: Container(),
        ),
        if (model)
          Container(
            color: Color.fromARGB(20, 20, 20, 20),
            width: size.width,
            height: size.height,
          ),
        Positioned(
          child: body(),
          left: offset == null ? positionX : offset.dx,
          top: offset == null ? positionY : offset.dy,
        )
      ]);
    } else if (model && !autoClose) {
      child = Stack(children: [
        Container(
          color: Color.fromARGB(20, 20, 20, 20),
          width: size.width,
          height: size.height,
        ),
        customLayout(context)
      ]);
    } else {
      child = customLayout(context);
    }
    return FocusScope(
        autofocus: true, child: Padding(padding: query.padding, child: child));
  }

  Widget customLayout(BuildContext context) {
    MediaQueryData query = MediaQuery.of(context);
    return CustomSingleChildLayout(
      delegate: _PopupPositionDelegate(
        target: target,
        verticalOffset: verticalOffset,
        horizontalOffset: horizontalOffset,
        position: position,
        bounds: boundsCallBack,
      ),
      child: Container(
        constraints: constraints,
        child: SingleChildScrollView(
            child: Column(children: [
          if (showConnector)
            Transform.rotate(
                angle: math.pi / 4,
                child: Align(
                    heightFactor: 0.5,
                    child: Container(
                      color: color,
                      width: 12,
                      height: 12,
                      transform: Matrix4.identity()..translate(0.0, 10.5),
                    ))),
          getPositionedChildForSide(query),
        ])),
      ),
    );
  }

  Widget getPositionedChildForSide(MediaQueryData query) {
    if (target != null) {
      return Container(
          color: Color(0xFFFFFFFF), child: Scrollbar(child: popUpchild));
    }

    switch (position) {
      case PopUpPosition.Left:
        {
          return Container(
              color: Color(0xFFFFFFFF),
              height: query.size.height - query.padding.top,
              child: Scrollbar(
                  child: popUpchild, isAlwaysShown: false, thickness: 0.0));
        }
      case PopUpPosition.Right:
        {
          return Container(
              color: Color(0xFFFFFFFF),
              height: query.size.height - query.padding.top,
              child: Scrollbar(
                  child: popUpchild, isAlwaysShown: false, thickness: 0.0));
        }
      case PopUpPosition.Top:
        {
          return Container(
              color: Color(0xFFFFFFFF),
              width: query.size.width,
              child: Scrollbar(
                  child: popUpchild, isAlwaysShown: false, thickness: 0.0));
        }
      case PopUpPosition.Bottom:
        {
          return Container(
              color: Color(0xFFFFFFFF),
              width: query.size.width,
              child: Scrollbar(
                  child: popUpchild, isAlwaysShown: false, thickness: 0.0));
        }
      case PopUpPosition.Positioned:
        {
          return Container(
              color: Color(0xFFFFFFFF),
              child: Scrollbar(
                  child: popUpchild, isAlwaysShown: false, thickness: 0.0));
        }
      default:
        {}
    }
    return Container(
        color: Color(0xFFFFFFFF),
        child:
            Scrollbar(child: popUpchild, isAlwaysShown: false, thickness: 0.0));
  }
}

Offset positionDependentBox({
  @required Size size,
  @required Size childSize,
  @required PopUpPosition side,
  Rect target = Rect.zero,
  double verticalOffset = 0.0,
  double horizontalOffset = 0.0,
}) {
  assert(size != null);
  assert(childSize != null);
  assert(verticalOffset != null);
  assert(side != null);
  double y = 0;
  double x = 0;
  if (side == PopUpPosition.Center) {
    x = size.width / 2 - childSize.width / 2;
    y = size.height / 2 - childSize.height / 2;
    // show in center
  } else if (side == PopUpPosition.Bottom && target == null) {
    x = 0;
    y = size.height - childSize.height;
  } else if (side == PopUpPosition.Top && target == null) {
    x = 0;
    y = 0;
  } else if (side == PopUpPosition.Left && target == null) {
    x = 0;
  } else if (side == PopUpPosition.Right && target == null) {
    x = size.width - childSize.width;
  } else if (side == PopUpPosition.Bottom || side == PopUpPosition.Top) {
    final bool fitsBelow =
        target.bottom + verticalOffset + childSize.height <= size.height;
    final bool fitsAbove = target.top - verticalOffset - childSize.height >= 0;
    final bool tooltipBelow =
        side == PopUpPosition.Bottom ? fitsBelow : !(fitsAbove || !fitsBelow);
    if (tooltipBelow) {
      y = math.min(target.bottom + verticalOffset, size.height);
    } else {
      y = target.top - verticalOffset - childSize.height;
    }
    if (y < 0) {
      y = 0;
    }
    x = target.left;
    if (x < 0) {
      x = 0;
    }
    if (x + childSize.width > size.width) {
      x = size.width - childSize.width;
    }
  } else {
    final bool fitsRight =
        target.right + horizontalOffset + childSize.width <= size.width;
    final bool fitsLeft = target.left - horizontalOffset - childSize.width >= 0;
    final bool tooltipRight = side == PopUpPosition.Right
        ? fitsRight || !fitsLeft
        : !(fitsLeft || !fitsRight);
    if (tooltipRight) {
      x = math.min(target.right + horizontalOffset, size.width);
    } else {
      x = target.left - horizontalOffset - childSize.width;
    }
    y = target.top;
    if (y < 0) {
      y = 0;
    }
    if (y + childSize.height > size.height) {
      y = size.height - childSize.height;
    }
  }
  return Offset(x, y);
}

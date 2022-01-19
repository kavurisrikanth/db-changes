import '../utils/ObservableState.dart';
import '../utils/ObjectObservable.dart';
import '../classes/core.dart';
import 'CustomCursor.dart';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

typedef void _ButtonOnPressed();

typedef void _ButtonOnLongPressed();

typedef KeyEventResult _ChildWrapperOnKey(
    FocusNode focusNode, RawKeyEvent event, ButtonRefs state);

typedef void _ChildWrapperOnTap(ButtonRefs state);

typedef void _ChildWrapperOnLongPress(ButtonRefs state);

class Button extends StatefulWidget {
  final bool disable;
  final Color backgroundColor;
  final Color focusedColor;
  final double cornerRadius;
  final EdgeInsets padding;
  final _ButtonOnPressed onPressed;
  final _ButtonOnLongPressed onLongPressed;
  final Widget child;
  Button(
      {Key key,
      this.disable = false,
      this.backgroundColor,
      this.focusedColor,
      this.cornerRadius = 3.0,
      this.padding,
      this.child,
      this.onPressed,
      this.onLongPressed})
      : super(key: key);
  @override
  _ButtonState createState() => _ButtonState();
}

/// To store state data for Button
class ButtonRefs {
  ChildWrapperState childWrapper = ChildWrapperState();
  ButtonRefs();
}

class ChildWrapperState extends ObjectObservable {
  bool _focus = false;
  ChildWrapperState();
  bool get focus {
    return _focus;
  }

  setFocus(bool val) {
    bool isValChanged = _focus != val;

    if (!isValChanged) {
      return;
    }

    _focus = val;

    fire('focus', this);
  }
}

class ChildWrapperWithState extends StatefulWidget {
  final ButtonRefs state;
  final Widget child;
  final _ChildWrapperOnLongPress onHandleLongPress;
  final _ChildWrapperOnTap onHandleTap;
  final _ChildWrapperOnKey onKey;
  final Color backgroundColor;
  final bool childPresent;
  final double cornerRadius;
  final bool disable;
  final Color focusedColor;
  final EdgeInsets padding;
  ChildWrapperWithState(
      {Key key,
      this.state,
      this.child,
      this.onHandleLongPress,
      this.onHandleTap,
      this.onKey,
      this.backgroundColor,
      this.childPresent,
      this.cornerRadius,
      this.disable,
      this.focusedColor,
      this.padding})
      : super(key: key);
  @override
  _ChildWrapperWithState createState() => _ChildWrapperWithState();
}

class _ChildWrapperWithState extends ObservableState<ChildWrapperWithState> {
  @override
  initState() {
    super.initState();

    updateObservable('childWrapper', null, childWrapper);

    initListeners();

    enableBuild = true;
  }

  void initListeners() {
    this.on(['childPresent', 'childWrapper', 'childWrapper.focus'], rebuild);
  }

  void childWrapperOnFocusChange(val) => childWrapper.setFocus(val);
  _ChildWrapperOnLongPress get onHandleLongPress =>
      this.widget.onHandleLongPress;
  _ChildWrapperOnTap get onHandleTap => this.widget.onHandleTap;
  _ChildWrapperOnKey get onKey => this.widget.onKey;
  Widget get child => widget.child;
  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Focus(
        child: GestureDetector(
            onTap: () {
              onHandleTap(state);
            },
            onLongPress: () {
              onHandleLongPress(state);
            },
            child: Container(
                padding: this.padding,
                decoration: !childWrapper.focus
                    ? BoxDecoration(
                        border: Border.all(color: Color(0x0), width: 1.0),
                        borderRadius: BorderRadius.only(
                            topLeft: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius),
                            topRight: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius),
                            bottomLeft: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius),
                            bottomRight: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius)),
                        color: this.backgroundColor != null
                            ? this.backgroundColor
                            : HexColor.fromHexInt(0x0))
                    : BoxDecoration(
                        border: Border.all(
                            color: this.focusedColor != null
                                ? this.focusedColor
                                : HexColor.fromHexInt(0x42000000),
                            width: 1.0),
                        borderRadius: BorderRadius.only(
                            topLeft: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius),
                            topRight: RadiusExt.elliptical(
                                x: this.cornerRadius, y: this.cornerRadius),
                            bottomLeft:
                                RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius),
                            bottomRight: RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius)),
                        color: this.backgroundColor != null ? this.backgroundColor : HexColor.fromHexInt(0x0)),
                foregroundDecoration: this.disable ? BoxDecoration(color: HexColor.fromHexInt(0x61dddddd), borderRadius: BorderRadius.only(topLeft: RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius), topRight: RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius), bottomLeft: RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius), bottomRight: RadiusExt.elliptical(x: this.cornerRadius, y: this.cornerRadius))) : null,
                child: Center(widthFactor: 1.0, heightFactor: 1.0, child: childPresent ? this.widget.child : Container()))),
        onFocusChange: (val) {
          childWrapperOnFocusChange(val);
        });
  }

  ButtonRefs get state => widget.state;
  Color get backgroundColor => widget.backgroundColor;
  bool get childPresent => widget.childPresent;
  ChildWrapperState get childWrapper => widget.state.childWrapper;
  double get cornerRadius => widget.cornerRadius;
  bool get disable => widget.disable;
  Color get focusedColor => widget.focusedColor;
  EdgeInsets get padding => widget.padding;
}

class _ButtonState extends ObservableState<Button> {
  ButtonRefs state = ButtonRefs();
  final FocusNode focusNode = FocusNode();
  @override
  initState() {
    super.initState();

    initListeners();

    enableBuild = true;
  }

  void initListeners() {
    this.on(['childPresent'], rebuild);
  }

  bool get disable {
    return this.widget.disable;
  }

  Color get backgroundColor {
    return this.widget.backgroundColor;
  }

  Color get focusedColor {
    return this.widget.focusedColor;
  }

  double get cornerRadius {
    return this.widget.cornerRadius;
  }

  EdgeInsets get padding {
    return this.widget.padding;
  }

  @override
  Widget build(BuildContext context) {
    return CustomCursor(
        child: ChildWrapperWithState(
            state: state,
            child: child,
            onHandleLongPress: onHandleLongPress,
            onHandleTap: onHandleTap,
            onKey: onKey,
            backgroundColor: backgroundColor,
            childPresent: childPresent,
            cornerRadius: cornerRadius,
            disable: disable,
            focusedColor: focusedColor,
            padding: padding));
  }

  void onHandleTap(ButtonRefs state) {
    if (this.onPressed != null && !this.disable) {
      this.onPressed();
    }
  }

  void onHandleLongPress(ButtonRefs state) {
    if (this.onLongPressed != null && !this.disable) {
      this.onLongPressed();
    }
  }

  KeyEventResult onKey(
      FocusNode focusNode, RawKeyEvent event, ButtonRefs state) {
    if (event is RawKeyDownEvent && !this.disable) {
      if (event.logicalKey == LogicalKeyboardKey.enter ||
          event.logicalKey == LogicalKeyboardKey.space) {
        if (this.onPressed != null) {
          this.onPressed();
        }

        return KeyEventResult.handled;
      }
    }

    return KeyEventResult.ignored;
  }

  _ButtonOnPressed get onPressed => this.widget.onPressed;
  _ButtonOnLongPressed get onLongPressed => this.widget.onLongPressed;
  bool get childPresent => this.widget.child != null;
  Widget get child => widget.child;
  ChildWrapperState get childWrapper => state.childWrapper;
}

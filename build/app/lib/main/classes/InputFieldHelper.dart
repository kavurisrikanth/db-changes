import 'dart:ui';
import 'package:flutter/widgets.dart';
import 'core.dart';

class InputFieldHelper {
  InputFieldHelper();
  static EdgeInsets getMarginInfo() {
    return EdgeInsets.all(0.0);
  }

  static EdgeInsets getPaddingInfo(bool dense, EdgeInsets padding) {
    if (padding != null) {
      return padding;
    }
    if (dense) {
      return EdgeInsets.only(left: 2.0, right: 0.0, top: 2.0, bottom: 2.0);
    } else {
      return EdgeInsets.only(left: 8.0, right: 8.0, top: 5.0, bottom: 5.0);
    }
  }

  static Color getDefaultBorderColor() {
    return HexColor.fromHexInt(0xffbfbfbf);
  }

  static Border getBorderInfo(Color borderColor) {
    return Border.all(
        width: 1.0,
        color: borderColor != null
            ? borderColor
            : InputFieldHelper.getDefaultBorderColor());
  }

  static FocusNode validateFocusNode(FocusNode node) {
    if (node != null) {
      return node;
    } else {
      return FocusNode();
    }
  }

  static TextStyle validateTextStyle(TextStyle textStyle) {
    if (textStyle != null) {
      return textStyle;
    } else {
      return TextStyle(color: HexColor.fromHexInt(0xff262626));
    }
  }

  static TextStyle validatePalceHolderStyle(
      TextStyle textStyle, Color placeHolderColor) {
    if (textStyle != null) {
      return textStyle.copyWith(
          color: (placeHolderColor != null
              ? placeHolderColor
              : Color(0x61000000)));
    } else {
      return TextStyle(color: Color(0x61000000));
    }
  }

  static Color getActiveBorderColor(Color cursorColor) {
    if (cursorColor != null) {
      return cursorColor;
    } else {
      return HexColor.fromHexInt(0xff14acff);
    }
  }

  static EdgeInsets getScrollPaddingInfo(EdgeInsets padding) {
    if (padding != null) {
      return padding;
    } else {
      return EdgeInsets.only(top: 20.0, left: 20.0, right: 20.0, bottom: 20.0);
    }
  }

  static ToolbarOptions getToolBarOptions(
      ToolbarOptions toolBar, bool obscureText) {
    if (toolBar != null) {
      return toolBar;
    } else {
      if (obscureText) {
        return ToolbarOptions(selectAll: true, paste: true);
      } else {
        return ToolbarOptions(
            copy: true, cut: true, selectAll: true, paste: true);
      }
    }
  }

  static TextStyle getErrorTextStyle() {
    return TextStyle(color: HexColor.fromHexInt(0xffff0000), fontSize: 10.0);
  }
}

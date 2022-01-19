import 'dart:math';

import 'package:flutter/widgets.dart';
import 'package:flutter/services.dart';

class AnimationControllerExt {
  static AnimationController unbounded(
      {double value,
      Duration duration,
      Duration reverseDuration,
      String debugLabel,
      TickerProvider vsync,
      AnimationBehavior animationBehavior}) {
    return AnimationController.unbounded(
        vsync: vsync,
        animationBehavior: animationBehavior,
        debugLabel: debugLabel,
        duration: duration,
        reverseDuration: reverseDuration,
        value: value);
  }
}

class BorderRadiusExt {
  static BorderRadius only(
      {Radius topLeft,
      Radius topRight,
      Radius bottomLeft,
      Radius bottomRight}) {
    return BorderRadius.only(
        topLeft: topLeft,
        topRight: topRight,
        bottomLeft: bottomLeft,
        bottomRight: bottomRight);
  }
}

class RadiusExt {
  static Radius elliptical({double x = 0.0, double y = 0.0}) {
    return Radius.elliptical(x, y);
  }
}

class ColorExt {
  static Color fromARGB({int alpha, int red, int green, int blue}) {
    return Color.fromARGB(alpha, red, green, blue);
  }
}

class EdgeInsetsExt {
  static EdgeInsets fromLTRB({
    double left,
    double top,
    double right,
    double bottom,
  }) {
    return EdgeInsets.fromLTRB(left, top, right, bottom);
  }
}

class LocaleExt {
  static Locale fromSubtags(
      {String countryCode, String languageCode, String scriptCode}) {
    return Locale.fromSubtags(
        countryCode: countryCode,
        languageCode: languageCode,
        scriptCode: scriptCode);
  }
}

class OffsetExt {
  static Offset getOffset({double dx, double dy}) {
    return Offset(dx, dy);
  }
}

class RectI {
  final int left;
  final int top;
  final int width;
  final int height;
  const RectI(this.left, this.top, this.width, this.height);
  RectI translate(int x, int y) {
    return RectI(left + x, top + y, width, height);
  }

  int get right {
    return left + width;
  }

  int get bottom {
    return top + height;
  }

  bool contains(int x, int y) {
    return x >= left && x <= right && y >= top && y <= bottom;
  }

  bool overlaps(RectI other) {
    if (right <= other.left || other.right <= left) return false;
    if (bottom <= other.top || other.bottom <= top) return false;
    return true;
  }

  RectI expandToInclude(RectI other) {
    int left = min(this.left, other.left);
    int top = min(this.top, other.top);
    int right = max(this.right, other.right);
    int bottom = max(this.bottom, other.bottom);
    return RectI(left, top, right - left, bottom - top);
  }
}

class SizeExt {
  static Size getSize({double width, double height}) {
    return Size(width, height);
  }
}

class IconDataExt {
  static IconData getIconData({int codePoint, String fontFamily}) {
    return IconData(codePoint, fontFamily: fontFamily);
  }
}

class HexColor {
  static Color fromHexStr(String hexString) {
    String hexCode = hexString;
    if (hexCode.length == 0) {
      hexCode = '00000000';
    }
    final buffer = StringBuffer();
    if (hexCode.length == 6 || hexCode.length == 7) buffer.write('ff');
    buffer.write(hexCode.replaceFirst('#', ''));
    try {
      return Color(int.parse(buffer.toString(), radix: 16));
    } catch (e) {
      return Color.fromARGB(0, 255, 255, 255);
    }
  }

  static Color fromHexInt(int hex) {
    return Color(hex);
  }

  static String toHexStr(Color color, {bool leadingHashSign = false}) {
    try {
      return '${leadingHashSign ? '#' : ''}'
          '${color.alpha.toRadixString(16).padLeft(2, '0')}'
          '${color.red.toRadixString(16).padLeft(2, '0')}'
          '${color.green.toRadixString(16).padLeft(2, '0')}'
          '${color.blue.toRadixString(16).padLeft(2, '0')}';
    } catch (e) {
      return '';
    }
  }

  static int toHexInt(Color color) {
    return color.value;
  }
}

class RectExt {
  static Rect fromLTRB({double left, double top, double right, double bottom}) {
    return Rect.fromLTRB(left, top, right, bottom);
  }
}

class AssetImageExt {
  static AssetImage forAssetImage(
      {String assetName, AssetBundle bundle, String package}) {
    return AssetImage(assetName, bundle: bundle, package: package);
  }
}

class ColorFilterExt {
  static ColorFilter filterMode({Color color, BlendMode blendMode}) {
    return ColorFilter.mode(color, blendMode);
  }
}

class FixedColumnWidthExt {
  static FixedColumnWidth getFixedColumnWidth({double value}) {
    return FixedColumnWidth(value);
  }
}

class FlexColumnWidthExt {
  static FlexColumnWidth getFlexColumnWidth({double value}) {
    return FlexColumnWidth(value);
  }
}

class IntrinsicColumnWidthExt {
  static IntrinsicColumnWidth getIntrinsicColumnWidth() {
    return IntrinsicColumnWidth();
  }
}

typedef U OneFunction<T, U>(T t);
typedef R BiFunction<T, U, R>(T t, U u);
typedef void Runnable();
typedef void Consumer<T>(T t);
typedef void BiConsumer<T, U>(T t, U u);
typedef int Comparator<T>(T a, T b);
typedef T Supplier<T>();
typedef bool BiPredicate<T, U>(T t, U u);

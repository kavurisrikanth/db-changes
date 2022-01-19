import 'dart:ui';
import 'core.dart';

class ColorUtils {
  static Map<String, Color> colorCache = Map();
  ColorUtils();
  static Color parseColor(String code) {
    String color = code;

    Color res = ColorUtils.colorCache[color];

    if (res != null) {
      return res;
    }

    try {
      res = HexColor.fromHexStr(color);

      ColorUtils.colorCache[color] = res;

      return res;
    } catch (e) {
      return ColorExt.fromARGB(alpha: 0, red: 0, green: 0, blue: 0);
    }
  }
}

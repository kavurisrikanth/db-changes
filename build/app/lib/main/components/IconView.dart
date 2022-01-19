import 'package:flutter/widgets.dart';

class IconView extends StatelessWidget {
  final IconData icon;
  final double size;
  final Color color;
  final TextDirection textDirection;

  IconView({this.icon, this.size, this.color, this.textDirection});
  @override
  Widget build(BuildContext context) {
    Color effectiveColor = color ?? DefaultTextStyle.of(context).style.color;
    return Icon(this.icon,
        size: size, color: effectiveColor, textDirection: textDirection);
  }
}

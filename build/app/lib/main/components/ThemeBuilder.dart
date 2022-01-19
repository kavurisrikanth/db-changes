import 'package:flutter/widgets.dart';

class ThemeBuilder extends StatefulWidget {
  final Function(BuildContext) builder;

  const ThemeBuilder({Key key, this.builder}) : super(key: key);

  @override
  ThemeBuilderState createState() => new ThemeBuilderState();

  static ThemeBuilderState of(BuildContext context) {
    return context.findAncestorStateOfType<ThemeBuilderState>();
  }
}

class ThemeBuilderState extends State<ThemeBuilder> {
  @override
  Widget build(BuildContext context) {
    return widget.builder(context);
  }

  void rebuild() {
    setState(() {});
  }
}

import 'package:flutter/widgets.dart';

class D3EShortcutManager extends ShortcutManager {
  @override
  KeyEventResult handleKeypress(BuildContext context, RawKeyEvent event) {
    FocusNode node = FocusManager.instance.primaryFocus;
    if (node != null &&
        !event.isAltPressed &&
        !event.isControlPressed &&
        !event.isMetaPressed &&
        event.character != null) {
      Widget w = node.context.widget;
      if (w is EditableText) {
        return KeyEventResult.ignored;
      }
    }
    return super.handleKeypress(context, event);
  }
}

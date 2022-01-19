import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter/services.dart';
import '../classes/CursorType.dart';

class CustomCursor extends MouseRegion {
  CustomCursor(
      {Widget child, CursorType cursorStyle = CursorType.click, String url})
      : super(
            cursor: CustomCursor.stringToSystemMouseCursors(cursorStyle),
            child: child);

  static SystemMouseCursor stringToSystemMouseCursors(CursorType type) {
    switch (type) {
      case CursorType.click:
        {
          return SystemMouseCursors.click;
        }

      case CursorType.basic:
        {
          return SystemMouseCursors.basic;
        }

      case CursorType.none:
        {
          return SystemMouseCursors.none;
        }

      case CursorType.text:
        {
          return SystemMouseCursors.text;
        }

      case CursorType.forbidden:
        {
          return SystemMouseCursors.forbidden;
        }

      case CursorType.grab:
        {
          return SystemMouseCursors.grab;
        }
      case CursorType.cell:
        {
          return SystemMouseCursors.cell;
        }

      case CursorType.allScroll:
        {
          return SystemMouseCursors.allScroll;
        }
      case CursorType.contextMenu:
        {
          return SystemMouseCursors.contextMenu;
        }
      case CursorType.copy:
        {
          return SystemMouseCursors.copy;
        }
      case CursorType.help:
        {
          return SystemMouseCursors.help;
        }
      case CursorType.move:
        {
          return SystemMouseCursors.move;
        }
      case CursorType.noDrop:
        {
          return SystemMouseCursors.noDrop;
        }
      case CursorType.precise:
        {
          return SystemMouseCursors.precise;
        }
      case CursorType.progress:
        {
          return SystemMouseCursors.progress;
        }
      case CursorType.resizeColumn:
        {
          return SystemMouseCursors.resizeColumn;
        }
      case CursorType.resizeDown:
        {
          return SystemMouseCursors.resizeDown;
        }
      case CursorType.resizeDownLeft:
        {
          return SystemMouseCursors.resizeDownLeft;
        }
      case CursorType.resizeDownRight:
        {
          return SystemMouseCursors.resizeDownRight;
        }
      case CursorType.resizeLeft:
        {
          return SystemMouseCursors.resizeLeft;
        }
      case CursorType.resizeRow:
        {
          return SystemMouseCursors.resizeRow;
        }
      case CursorType.resizeLeftRight:
        {
          return SystemMouseCursors.resizeLeftRight;
        }
      case CursorType.resizeRight:
        {
          return SystemMouseCursors.resizeRight;
        }
      case CursorType.resizeRow:
        {
          return SystemMouseCursors.resizeRow;
        }
      case CursorType.resizeUp:
        {
          return SystemMouseCursors.resizeUp;
        }
      case CursorType.resizeUpDown:
        {
          return SystemMouseCursors.resizeUpDown;
        }
      case CursorType.resizeUpLeft:
        {
          return SystemMouseCursors.resizeUpLeft;
        }
      case CursorType.resizeUpLeftDownRight:
        {
          return SystemMouseCursors.resizeUpLeftDownRight;
        }
      case CursorType.resizeUpRight:
        {
          return SystemMouseCursors.resizeUpRight;
        }
      case CursorType.verticalText:
        {
          return SystemMouseCursors.verticalText;
        }
      case CursorType.wait:
        {
          return SystemMouseCursors.wait;
        }
      case CursorType.zoomIn:
        {
          return SystemMouseCursors.zoomIn;
        }
      case CursorType.zoomOut:
        {
          return SystemMouseCursors.zoomOut;
        }

      default:
        {
          return SystemMouseCursors.click;
        }
    }
  }
}

import 'ConnectionStatus.dart';
import 'CursorType.dart';
import 'DBResultStatus.dart';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class StringToEnumType {
  static Alignment stringToAlignment(String str) {
    switch (str) {
      case 'topLeft':
        {
          return Alignment.topLeft;
        }

      case 'topCenter':
        {
          return Alignment.topCenter;
        }

      case 'topRight':
        {
          return Alignment.topRight;
        }

      case 'centerLeft':
        {
          return Alignment.centerLeft;
        }

      case 'center':
        {
          return Alignment.center;
        }

      case 'centerRight':
        {
          return Alignment.centerRight;
        }

      case 'bottomLeft':
        {
          return Alignment.bottomLeft;
        }

      case 'bottomCenter':
        {
          return Alignment.bottomCenter;
        }

      case 'bottomRight':
        {
          return Alignment.bottomRight;
        }
      default:
        {
          return null;
        }
    }
  }

  static BlendMode stringToBlendMode(String str) {
    switch (str) {
      case 'clear':
        {
          return BlendMode.clear;
        }

      case 'src':
        {
          return BlendMode.src;
        }

      case 'dst':
        {
          return BlendMode.dst;
        }

      case 'srcOver':
        {
          return BlendMode.srcOver;
        }

      case 'dstOver':
        {
          return BlendMode.dstOver;
        }

      case 'srcIn':
        {
          return BlendMode.srcIn;
        }

      case 'dstIn':
        {
          return BlendMode.dstIn;
        }

      case 'srcOut':
        {
          return BlendMode.srcOut;
        }

      case 'dstOut':
        {
          return BlendMode.dstOut;
        }

      case 'srcATop':
        {
          return BlendMode.srcATop;
        }

      case 'dstATop':
        {
          return BlendMode.dstATop;
        }

      case 'xor':
        {
          return BlendMode.xor;
        }

      case 'plus':
        {
          return BlendMode.plus;
        }

      case 'modulate':
        {
          return BlendMode.modulate;
        }

      case 'screen':
        {
          return BlendMode.screen;
        }

      case 'overlay':
        {
          return BlendMode.overlay;
        }

      case 'darken':
        {
          return BlendMode.darken;
        }

      case 'lighten':
        {
          return BlendMode.lighten;
        }

      case 'colorDodge':
        {
          return BlendMode.colorDodge;
        }

      case 'colorBurn':
        {
          return BlendMode.colorBurn;
        }

      case 'hardLight':
        {
          return BlendMode.hardLight;
        }

      case 'softLight':
        {
          return BlendMode.softLight;
        }

      case 'difference':
        {
          return BlendMode.difference;
        }

      case 'exclusion':
        {
          return BlendMode.exclusion;
        }

      case 'multiply':
        {
          return BlendMode.multiply;
        }

      case 'hue':
        {
          return BlendMode.hue;
        }

      case 'saturation':
        {
          return BlendMode.saturation;
        }

      case 'color':
        {
          return BlendMode.color;
        }

      case 'luminosity':
        {
          return BlendMode.luminosity;
        }
      default:
        {
          return null;
        }
    }
  }

  static BorderStyle stringToBorderStyle(String str) {
    switch (str) {
      case 'none':
        {
          return BorderStyle.none;
        }

      case 'solid':
        {
          return BorderStyle.solid;
        }
      default:
        {
          return null;
        }
    }
  }

  static BoxFit stringToBoxFit(String str) {
    switch (str) {
      case 'fill':
        {
          return BoxFit.fill;
        }

      case 'contain':
        {
          return BoxFit.contain;
        }

      case 'cover':
        {
          return BoxFit.cover;
        }

      case 'fitWidth':
        {
          return BoxFit.fitWidth;
        }

      case 'fitHeight':
        {
          return BoxFit.fitHeight;
        }

      case 'none':
        {
          return BoxFit.none;
        }

      case 'scaleDown':
        {
          return BoxFit.scaleDown;
        }
      default:
        {
          return null;
        }
    }
  }

  static BoxShape stringToBoxShape(String str) {
    switch (str) {
      case 'rectangle':
        {
          return BoxShape.rectangle;
        }

      case 'circle':
        {
          return BoxShape.circle;
        }
      default:
        {
          return null;
        }
    }
  }

  static Clip stringToClip(String str) {
    switch (str) {
      case 'none':
        {
          return Clip.none;
        }

      case 'hardEdge':
        {
          return Clip.hardEdge;
        }

      case 'antiAlias':
        {
          return Clip.antiAlias;
        }

      case 'antiAliasWithSaveLayer':
        {
          return Clip.antiAliasWithSaveLayer;
        }
      default:
        {
          return null;
        }
    }
  }

  static ConnectionStatus stringToConnectionStatus(String str) {
    switch (str) {
      case 'Connecting':
        {
          return ConnectionStatus.Connecting;
        }

      case 'Connected':
        {
          return ConnectionStatus.Connected;
        }

      case 'ConnectionBusy':
        {
          return ConnectionStatus.ConnectionBusy;
        }

      case 'ConnectionNormal':
        {
          return ConnectionStatus.ConnectionNormal;
        }

      case 'ConnectionFailed':
        {
          return ConnectionStatus.ConnectionFailed;
        }

      case 'RestoreFailed':
        {
          return ConnectionStatus.RestoreFailed;
        }

      case 'AuthFailed':
        {
          return ConnectionStatus.AuthFailed;
        }
      default:
        {
          return null;
        }
    }
  }

  static CrossAxisAlignment stringToCrossAxisAlignment(String str) {
    switch (str) {
      case 'start':
        {
          return CrossAxisAlignment.start;
        }

      case 'end':
        {
          return CrossAxisAlignment.end;
        }

      case 'center':
        {
          return CrossAxisAlignment.center;
        }

      case 'stretch':
        {
          return CrossAxisAlignment.stretch;
        }

      case 'baseline':
        {
          return CrossAxisAlignment.baseline;
        }
      default:
        {
          return null;
        }
    }
  }

  static CursorType stringToCursorType(String str) {
    switch (str) {
      case 'click':
        {
          return CursorType.click;
        }

      case 'basic':
        {
          return CursorType.basic;
        }

      case 'none':
        {
          return CursorType.none;
        }

      case 'text':
        {
          return CursorType.text;
        }

      case 'forbidden':
        {
          return CursorType.forbidden;
        }

      case 'cell':
        {
          return CursorType.cell;
        }

      case 'precise':
        {
          return CursorType.precise;
        }

      case 'grab':
        {
          return CursorType.grab;
        }

      case 'move':
        {
          return CursorType.move;
        }

      case 'noDrop':
        {
          return CursorType.noDrop;
        }

      case 'resizeUpRightDownLeft':
        {
          return CursorType.resizeUpRightDownLeft;
        }

      case 'alias':
        {
          return CursorType.alias;
        }

      case 'copy':
        {
          return CursorType.copy;
        }

      case 'allScroll':
        {
          return CursorType.allScroll;
        }

      case 'resizeUpLeftDownRight':
        {
          return CursorType.resizeUpLeftDownRight;
        }

      case 'resizeUpDown':
        {
          return CursorType.resizeUpDown;
        }

      case 'resizeLeftRight':
        {
          return CursorType.resizeLeftRight;
        }

      case 'resizeRow':
        {
          return CursorType.resizeRow;
        }

      case 'zoomIn':
        {
          return CursorType.zoomIn;
        }

      case 'zoomOut':
        {
          return CursorType.zoomOut;
        }

      case 'resizeRight':
        {
          return CursorType.resizeRight;
        }

      case 'resizeColumn':
        {
          return CursorType.resizeColumn;
        }

      case 'resizeDownRight':
        {
          return CursorType.resizeDownRight;
        }

      case 'resizeDownLeft':
        {
          return CursorType.resizeDownLeft;
        }

      case 'resizeUpRight':
        {
          return CursorType.resizeUpRight;
        }

      case 'resizeUpLeft':
        {
          return CursorType.resizeUpLeft;
        }

      case 'resizeUp':
        {
          return CursorType.resizeUp;
        }

      case 'resizeLeft':
        {
          return CursorType.resizeLeft;
        }

      case 'resizeDown':
        {
          return CursorType.resizeDown;
        }

      case 'grabbing':
        {
          return CursorType.grabbing;
        }

      case 'wait':
        {
          return CursorType.wait;
        }

      case 'progress':
        {
          return CursorType.progress;
        }

      case 'contextMenu':
        {
          return CursorType.contextMenu;
        }

      case 'help':
        {
          return CursorType.help;
        }

      case 'verticalText':
        {
          return CursorType.verticalText;
        }
      default:
        {
          return null;
        }
    }
  }

  static DBResultStatus stringToDBResultStatus(String str) {
    switch (str) {
      case 'Success':
        {
          return DBResultStatus.Success;
        }

      case 'Errors':
        {
          return DBResultStatus.Errors;
        }
      default:
        {
          return null;
        }
    }
  }

  static FontStyle stringToFontStyle(String str) {
    switch (str) {
      case 'normal':
        {
          return FontStyle.normal;
        }

      case 'italic':
        {
          return FontStyle.italic;
        }
      default:
        {
          return null;
        }
    }
  }

  static FontWeight stringToFontWeight(String str) {
    switch (str) {
      case 'w100':
        {
          return FontWeight.w100;
        }

      case 'w200':
        {
          return FontWeight.w200;
        }

      case 'w300':
        {
          return FontWeight.w300;
        }

      case 'w400':
        {
          return FontWeight.w400;
        }

      case 'w500':
        {
          return FontWeight.w500;
        }

      case 'w600':
        {
          return FontWeight.w600;
        }

      case 'w700':
        {
          return FontWeight.w700;
        }

      case 'w800':
        {
          return FontWeight.w800;
        }

      case 'w900':
        {
          return FontWeight.w900;
        }

      case 'normal':
        {
          return FontWeight.normal;
        }

      case 'bold':
        {
          return FontWeight.bold;
        }
      default:
        {
          return null;
        }
    }
  }

  static ImageRepeat stringToImageRepeat(String str) {
    switch (str) {
      case 'noRepeat':
        {
          return ImageRepeat.noRepeat;
        }

      case 'repeat':
        {
          return ImageRepeat.repeat;
        }

      case 'repeatX':
        {
          return ImageRepeat.repeatX;
        }

      case 'repeatY':
        {
          return ImageRepeat.repeatY;
        }
      default:
        {
          return null;
        }
    }
  }

  static KeyEventResult stringToKeyEventResult(String str) {
    switch (str) {
      case 'handled':
        {
          return KeyEventResult.handled;
        }

      case 'ignored':
        {
          return KeyEventResult.ignored;
        }

      case 'skipRemainingHandlers':
        {
          return KeyEventResult.skipRemainingHandlers;
        }
      default:
        {
          return null;
        }
    }
  }

  static LogicalKeyboardKey stringToLogicalKeyboardKey(String str) {
    switch (str) {
      case 'escape':
        {
          return LogicalKeyboardKey.escape;
        }

      case 'enter':
        {
          return LogicalKeyboardKey.enter;
        }

      case 'backspace':
        {
          return LogicalKeyboardKey.backspace;
        }

      case 'tab':
        {
          return LogicalKeyboardKey.tab;
        }

      case 'space':
        {
          return LogicalKeyboardKey.space;
        }

      case 'minus':
        {
          return LogicalKeyboardKey.minus;
        }

      case 'equal':
        {
          return LogicalKeyboardKey.equal;
        }

      case 'delete':
        {
          return LogicalKeyboardKey.delete;
        }

      case 'end':
        {
          return LogicalKeyboardKey.end;
        }

      case 'arrowRight':
        {
          return LogicalKeyboardKey.arrowRight;
        }

      case 'arrowLeft':
        {
          return LogicalKeyboardKey.arrowLeft;
        }

      case 'arrowDown':
        {
          return LogicalKeyboardKey.arrowDown;
        }

      case 'arrowUp':
        {
          return LogicalKeyboardKey.arrowUp;
        }

      case 'controlLeft':
        {
          return LogicalKeyboardKey.controlLeft;
        }

      case 'shiftLeft':
        {
          return LogicalKeyboardKey.shiftLeft;
        }

      case 'altLeft':
        {
          return LogicalKeyboardKey.altLeft;
        }

      case 'metaLeft':
        {
          return LogicalKeyboardKey.metaLeft;
        }

      case 'controlRight':
        {
          return LogicalKeyboardKey.controlRight;
        }

      case 'shiftRight':
        {
          return LogicalKeyboardKey.shiftRight;
        }

      case 'altRight':
        {
          return LogicalKeyboardKey.altRight;
        }

      case 'metaRight':
        {
          return LogicalKeyboardKey.metaRight;
        }

      case 'save':
        {
          return LogicalKeyboardKey.save;
        }

      case 'redo':
        {
          return LogicalKeyboardKey.redo;
        }

      case 'shift':
        {
          return LogicalKeyboardKey.shift;
        }

      case 'meta':
        {
          return LogicalKeyboardKey.meta;
        }

      case 'alt':
        {
          return LogicalKeyboardKey.alt;
        }

      case 'control':
        {
          return LogicalKeyboardKey.control;
        }

      case 'zoomIn':
        {
          return LogicalKeyboardKey.zoomIn;
        }

      case 'zoomOut':
        {
          return LogicalKeyboardKey.zoomOut;
        }

      case 'zoomToggle':
        {
          return LogicalKeyboardKey.zoomToggle;
        }

      case 'close':
        {
          return LogicalKeyboardKey.close;
        }

      case 'copy':
        {
          return LogicalKeyboardKey.copy;
        }

      case 'cut':
        {
          return LogicalKeyboardKey.cut;
        }

      case 'undo':
        {
          return LogicalKeyboardKey.undo;
        }

      case 'again':
        {
          return LogicalKeyboardKey.again;
        }

      case 'select':
        {
          return LogicalKeyboardKey.select;
        }

      case 'open':
        {
          return LogicalKeyboardKey.open;
        }

      case 'home':
        {
          return LogicalKeyboardKey.home;
        }

      case 'insert':
        {
          return LogicalKeyboardKey.insert;
        }

      case 'capsLock':
        {
          return LogicalKeyboardKey.capsLock;
        }

      case 'slash':
        {
          return LogicalKeyboardKey.slash;
        }

      case 'f1':
        {
          return LogicalKeyboardKey.f1;
        }

      case 'f2':
        {
          return LogicalKeyboardKey.f2;
        }

      case 'f3':
        {
          return LogicalKeyboardKey.f3;
        }

      case 'f4':
        {
          return LogicalKeyboardKey.f4;
        }

      case 'f5':
        {
          return LogicalKeyboardKey.f5;
        }

      case 'f6':
        {
          return LogicalKeyboardKey.f6;
        }

      case 'f7':
        {
          return LogicalKeyboardKey.f7;
        }

      case 'f8':
        {
          return LogicalKeyboardKey.f8;
        }

      case 'f9':
        {
          return LogicalKeyboardKey.f9;
        }

      case 'f10':
        {
          return LogicalKeyboardKey.f10;
        }

      case 'f11':
        {
          return LogicalKeyboardKey.f11;
        }

      case 'f12':
        {
          return LogicalKeyboardKey.f12;
        }

      case 'keyA':
        {
          return LogicalKeyboardKey.keyA;
        }

      case 'keyB':
        {
          return LogicalKeyboardKey.keyB;
        }

      case 'keyC':
        {
          return LogicalKeyboardKey.keyC;
        }

      case 'keyD':
        {
          return LogicalKeyboardKey.keyD;
        }

      case 'keyE':
        {
          return LogicalKeyboardKey.keyE;
        }

      case 'keyF':
        {
          return LogicalKeyboardKey.keyF;
        }

      case 'keyG':
        {
          return LogicalKeyboardKey.keyG;
        }

      case 'keyH':
        {
          return LogicalKeyboardKey.keyH;
        }

      case 'keyI':
        {
          return LogicalKeyboardKey.keyI;
        }

      case 'keyJ':
        {
          return LogicalKeyboardKey.keyJ;
        }

      case 'keyK':
        {
          return LogicalKeyboardKey.keyK;
        }

      case 'keyL':
        {
          return LogicalKeyboardKey.keyL;
        }

      case 'keyM':
        {
          return LogicalKeyboardKey.keyM;
        }

      case 'keyN':
        {
          return LogicalKeyboardKey.keyN;
        }

      case 'keyO':
        {
          return LogicalKeyboardKey.keyO;
        }

      case 'keyP':
        {
          return LogicalKeyboardKey.keyP;
        }

      case 'keyQ':
        {
          return LogicalKeyboardKey.keyQ;
        }

      case 'keyR':
        {
          return LogicalKeyboardKey.keyR;
        }

      case 'keyS':
        {
          return LogicalKeyboardKey.keyS;
        }

      case 'keyT':
        {
          return LogicalKeyboardKey.keyT;
        }

      case 'keyU':
        {
          return LogicalKeyboardKey.keyU;
        }

      case 'keyV':
        {
          return LogicalKeyboardKey.keyV;
        }

      case 'keyW':
        {
          return LogicalKeyboardKey.keyW;
        }

      case 'keyX':
        {
          return LogicalKeyboardKey.keyX;
        }

      case 'keyY':
        {
          return LogicalKeyboardKey.keyY;
        }

      case 'keyZ':
        {
          return LogicalKeyboardKey.keyZ;
        }

      case 'digit1':
        {
          return LogicalKeyboardKey.digit1;
        }

      case 'digit2':
        {
          return LogicalKeyboardKey.digit2;
        }

      case 'digit3':
        {
          return LogicalKeyboardKey.digit3;
        }

      case 'digit4':
        {
          return LogicalKeyboardKey.digit4;
        }

      case 'digit5':
        {
          return LogicalKeyboardKey.digit5;
        }

      case 'digit6':
        {
          return LogicalKeyboardKey.digit6;
        }

      case 'digit7':
        {
          return LogicalKeyboardKey.digit7;
        }

      case 'digit8':
        {
          return LogicalKeyboardKey.digit8;
        }

      case 'digit9':
        {
          return LogicalKeyboardKey.digit9;
        }

      case 'digit0':
        {
          return LogicalKeyboardKey.digit0;
        }

      case 'pageUp':
        {
          return LogicalKeyboardKey.pageUp;
        }

      case 'pageDown':
        {
          return LogicalKeyboardKey.pageDown;
        }

      case 'bracketLeft':
        {
          return LogicalKeyboardKey.bracketLeft;
        }

      case 'bracketRight':
        {
          return LogicalKeyboardKey.bracketRight;
        }

      case 'numpadAdd':
        {
          return LogicalKeyboardKey.numpadAdd;
        }

      case 'numpadSubtract':
        {
          return LogicalKeyboardKey.numpadSubtract;
        }

      case 'quote':
        {
          return LogicalKeyboardKey.quote;
        }

      case 'comma':
        {
          return LogicalKeyboardKey.comma;
        }

      case 'period':
        {
          return LogicalKeyboardKey.period;
        }

      case 'exclamation':
        {
          return LogicalKeyboardKey.exclamation;
        }

      case 'at':
        {
          return LogicalKeyboardKey.at;
        }

      case 'numberSign':
        {
          return LogicalKeyboardKey.numberSign;
        }

      case 'dollar':
        {
          return LogicalKeyboardKey.dollar;
        }
      default:
        {
          return null;
        }
    }
  }

  static MainAxisAlignment stringToMainAxisAlignment(String str) {
    switch (str) {
      case 'start':
        {
          return MainAxisAlignment.start;
        }

      case 'end':
        {
          return MainAxisAlignment.end;
        }

      case 'center':
        {
          return MainAxisAlignment.center;
        }

      case 'spaceBetween':
        {
          return MainAxisAlignment.spaceBetween;
        }

      case 'spaceAround':
        {
          return MainAxisAlignment.spaceAround;
        }

      case 'spaceEvenly':
        {
          return MainAxisAlignment.spaceEvenly;
        }
      default:
        {
          return null;
        }
    }
  }

  static MainAxisSize stringToMainAxisSize(String str) {
    switch (str) {
      case 'min':
        {
          return MainAxisSize.min;
        }

      case 'max':
        {
          return MainAxisSize.max;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextAlign stringToTextAlign(String str) {
    switch (str) {
      case 'left':
        {
          return TextAlign.left;
        }

      case 'right':
        {
          return TextAlign.right;
        }

      case 'center':
        {
          return TextAlign.center;
        }

      case 'justify':
        {
          return TextAlign.justify;
        }

      case 'start':
        {
          return TextAlign.start;
        }

      case 'end':
        {
          return TextAlign.end;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextBaseline stringToTextBaseline(String str) {
    switch (str) {
      case 'alphabetic':
        {
          return TextBaseline.alphabetic;
        }

      case 'ideographic':
        {
          return TextBaseline.ideographic;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextDecoration stringToTextDecoration(String str) {
    switch (str) {
      case 'none':
        {
          return TextDecoration.none;
        }

      case 'underline':
        {
          return TextDecoration.underline;
        }

      case 'overline':
        {
          return TextDecoration.overline;
        }

      case 'lineThrough':
        {
          return TextDecoration.lineThrough;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextDecorationStyle stringToTextDecorationStyle(String str) {
    switch (str) {
      case 'solid':
        {
          return TextDecorationStyle.solid;
        }

      case 'double':
        {
          return TextDecorationStyle.double;
        }

      case 'dotted':
        {
          return TextDecorationStyle.dotted;
        }

      case 'dashed':
        {
          return TextDecorationStyle.dashed;
        }

      case 'wavy':
        {
          return TextDecorationStyle.wavy;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextDirection stringToTextDirection(String str) {
    switch (str) {
      case 'rtl':
        {
          return TextDirection.rtl;
        }

      case 'ltr':
        {
          return TextDirection.ltr;
        }
      default:
        {
          return null;
        }
    }
  }

  static TextOverflow stringToTextOverflow(String str) {
    switch (str) {
      case 'clip':
        {
          return TextOverflow.clip;
        }

      case 'fade':
        {
          return TextOverflow.fade;
        }

      case 'ellipsis':
        {
          return TextOverflow.ellipsis;
        }

      case 'visible':
        {
          return TextOverflow.visible;
        }
      default:
        {
          return null;
        }
    }
  }

  static VerticalDirection stringToVerticalDirection(String str) {
    switch (str) {
      case 'up':
        {
          return VerticalDirection.up;
        }

      case 'down':
        {
          return VerticalDirection.down;
        }
      default:
        {
          return null;
        }
    }
  }
}

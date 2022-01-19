import 'dart:ui';
import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import '../classes/BrowserUtil.dart';
import '../classes/core.dart';
import '../classes/InputFieldHelper.dart';
import 'package:flutter/foundation.dart';

typedef void _InputFieldOnChanged(String text);

typedef void _InputFieldOnEditingComplete();

typedef void _InputFieldOnSubmitted(String text);

typedef void _InputFieldOnTap();

class _TextFieldSelectionGestureDetectorBuilder
    extends TextSelectionGestureDetectorBuilder {
  _TextFieldSelectionGestureDetectorBuilder({
    @required _InputFieldState state,
  })  : _state = state,
        super(delegate: state);

  final _InputFieldState _state;

  @override
  void onForcePressStart(ForcePressDetails details) {
    super.onForcePressStart(details);
    if (delegate.selectionEnabled && shouldShowSelectionToolbar) {
      editableText.showToolbar();
    }
  }

  @override
  void onForcePressEnd(ForcePressDetails details) {
    // Not required.
  }

  @override
  void onSingleLongTapMoveUpdate(LongPressMoveUpdateDetails details) {
    if (delegate.selectionEnabled) {
      renderEditable.selectWordsInRange(
        from: details.globalPosition - details.offsetFromOrigin,
        to: details.globalPosition,
        cause: SelectionChangedCause.longPress,
      );
    }
  }

  @override
  void onSingleTapUp(TapUpDetails details) {
    editableText.hideToolbar();
    if (delegate.selectionEnabled) {
      renderEditable.selectPosition(cause: SelectionChangedCause.tap);
    }
    _state._requestKeyboard();
    if (_state.widget.onTap != null) _state.widget.onTap();
  }

  @override
  void onSingleLongTapStart(LongPressStartDetails details) {
    if (delegate.selectionEnabled) {
      renderEditable.selectWord(cause: SelectionChangedCause.longPress);
    }
  }
}

class DefaultTextSelectionControls extends TextSelectionControls {
  @override
  Widget buildHandle(
      BuildContext context, TextSelectionHandleType type, double textLineHeight,
      [VoidCallback onTap, double startGlyphHeight, double endGlyphHeight]) {
    return Container();
  }

  @override
  Widget buildToolbar(
      BuildContext context,
      Rect globalEditableRegion,
      double textLineHeight,
      Offset position,
      List<TextSelectionPoint> endpoints,
      TextSelectionDelegate delegate,
      ClipboardStatusNotifier clipboardStatus,
      Offset offset) {
    return Container();
  }

  @override
  Offset getHandleAnchor(TextSelectionHandleType type, double textLineHeight,
      [double startGlyphHeight, double endGlyphHeight]) {
    return Offset.zero;
  }

  @override
  Size getHandleSize(double textLineHeight) {
    return Size.zero;
  }
}

class InputField extends StatefulWidget {
  final TextEditingController controller;
  final TextInputType keyboardType;
  final TextStyle style;
  final StrutStyle strutStyle;
  final TextAlign textAlign;
  final TextDirection textDirection;
  final TextCapitalization textCapitalization;
  final bool autofocus;
  final bool obscureText;
  final bool autocorrect;
  final int maxLines;
  final int minLines;
  final bool expands;
  final int maxLength;
  final bool maxLengthEnforced;
  final bool enabled;
  final double cursorWidth;
  final Radius cursorRadius;
  final Color cursorColor;
  final Brightness keyboardAppearance;
  final EdgeInsets scrollPadding;
  final EdgeInsets padding;
  final bool enableInteractiveSelection;
  final DragStartBehavior dragStartBehavior;
  final ScrollPhysics scrollPhysics;
  final FocusNode focusNode;
  final bool readOnly;
  final ToolbarOptions toolbarOptions;
  final bool showCursor;
  final bool enableSuggestions;
  final bool dense;
  final String placeHolder;
  final Color placeHolderColor;
  final Color activeColor;
  final Color backgroundColor;
  final double cornerRadius;
  final Color inActiveColor;
  final bool isRenderIngnores;
  final _InputFieldOnChanged onChanged;
  final _InputFieldOnEditingComplete onEditingComplete;
  final _InputFieldOnSubmitted onSubmitted;
  final _InputFieldOnTap onTap;
  InputField(
      {Key key,
      this.keyboardType = TextInputType.text,
      this.style,
      this.strutStyle,
      this.textAlign = TextAlign.start,
      this.textDirection = TextDirection.ltr,
      this.textCapitalization = TextCapitalization.none,
      this.autofocus = false,
      this.obscureText = false,
      this.autocorrect = true,
      this.maxLines = 1,
      this.minLines = 0,
      this.expands = false,
      this.maxLength = 0,
      this.cornerRadius = 0.0,
      this.maxLengthEnforced = true,
      this.enabled = false,
      this.cursorWidth = 2.0,
      this.cursorRadius,
      this.cursorColor,
      this.keyboardAppearance = Brightness.dark,
      this.scrollPadding,
      this.padding,
      this.enableInteractiveSelection = true,
      this.dragStartBehavior = DragStartBehavior.start,
      this.scrollPhysics,
      this.focusNode,
      this.readOnly = false,
      this.toolbarOptions,
      this.showCursor = true,
      this.enableSuggestions = true,
      this.dense = false,
      this.placeHolder = '',
      this.placeHolderColor,
      this.activeColor,
      this.backgroundColor,
      this.inActiveColor,
      this.isRenderIngnores = false,
      this.onChanged,
      this.onEditingComplete,
      this.onSubmitted,
      this.onTap,
      this.controller})
      : super(key: key);
  @override
  _InputFieldState createState() => _InputFieldState();
}

class _InputFieldState extends State<InputField>
    implements TextSelectionGestureDetectorBuilderDelegate {
  bool _isActive = false;
  bool _isActiveField = false;
  String _textValue = '';
  FocusNode _node;
  TextEditingController _controller = TextEditingController();
  TextEditingController get _effectiveController =>
      widget.controller ?? _controller;

  _TextFieldSelectionGestureDetectorBuilder _selectionGestureDetectorBuilder;

  String _editCompleteString = '';

  @override
  initState() {
    super.initState();

    _selectionGestureDetectorBuilder =
        _TextFieldSelectionGestureDetectorBuilder(state: this);

    initListeners();

    init();
  }

  FocusNode get focusNode {
    return this.widget.focusNode;
  }

  void initListeners() {}

  void didUpdateWidget(InputField oldWidget) {
    super.didUpdateWidget(oldWidget);
  }

  TextInputType get keyboardType {
    return this.widget.keyboardType;
  }

  TextStyle get style {
    return this.widget.style;
  }

  StrutStyle get strutStyle {
    return this.widget.strutStyle;
  }

  TextAlign get textAlign {
    return this.widget.textAlign;
  }

  TextDirection get textDirection {
    return this.widget.textDirection;
  }

  TextCapitalization get textCapitalization {
    return this.widget.textCapitalization;
  }

  bool get autofocus {
    return this.widget.autofocus;
  }

  bool get obscureText {
    return this.widget.obscureText;
  }

  bool get autocorrect {
    return this.widget.autocorrect;
  }

  int get maxLines {
    return this.widget.maxLines;
  }

  int get minLines {
    return this.widget.minLines;
  }

  bool get expands {
    return this.widget.expands;
  }

  int get maxLength {
    return this.widget.maxLength;
  }

  bool get maxLengthEnforced {
    return this.widget.maxLengthEnforced;
  }

  bool get enabled {
    return this.widget.enabled;
  }

  double get cursorWidth {
    return this.widget.cursorWidth;
  }

  Radius get cursorRadius {
    return this.widget.cursorRadius;
  }

  Color get cursorColor {
    return this.widget.cursorColor;
  }

  Brightness get keyboardAppearance {
    return this.widget.keyboardAppearance;
  }

  EdgeInsets get scrollPadding {
    return this.widget.scrollPadding;
  }

  EdgeInsets get padding {
    return this.widget.padding;
  }

  bool get enableInteractiveSelection {
    return this.widget.enableInteractiveSelection;
  }

  DragStartBehavior get dragStartBehavior {
    return this.widget.dragStartBehavior;
  }

  ScrollPhysics get scrollPhysics {
    return this.widget.scrollPhysics;
  }

  bool get readOnly {
    return this.widget.readOnly;
  }

  ToolbarOptions get toolbarOptions {
    return this.widget.toolbarOptions;
  }

  bool get showCursor {
    return this.widget.showCursor;
  }

  bool get enableSuggestions {
    return this.widget.enableSuggestions;
  }

  bool get dense {
    return this.widget.dense;
  }

  String get placeHolder {
    return this.widget.placeHolder;
  }

  Color get placeHolderColor {
    return this.widget.placeHolderColor;
  }

  Color get activeColor {
    return this.widget.activeColor;
  }

  Color get backgroundColor {
    return this.widget.backgroundColor;
  }

  double get cornerRadius {
    return this.widget.cornerRadius;
  }

  Color get inActiveColor {
    return this.widget.inActiveColor;
  }

  void setIsActiveField(bool val) {
    if (!mounted) return;
    setState(() {
      _isActiveField = val;
    });
  }

  bool get isActiveField {
    return _isActiveField;
  }

  bool get isActive {
    return _isActive;
  }

  void setIsActive(bool val) {
    if (!mounted) return;
    setState(() {
      _isActive = val;
    });
  }

  bool get isRenderIngnores {
    return this.widget.isRenderIngnores;
  }

  String get textValue {
    return _textValue;
  }

  void setTextValue(String val) {
    setState(() {
      _textValue = val;
    });
  }

  void setNode(FocusNode nodeData) {
    setState(() {
      _node = nodeData;
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget child = MouseRegion(
        onEnter: (event) {
          setIsActive(true);
        },
        onExit: (event) {
          setIsActive(false);
        },
        child: Stack(key: Key(isActive.toString()), children: [
          if (!this.isActiveField && !this.isRenderIngnores)
            Align(
                alignment: Alignment.centerLeft,
                child: Row(mainAxisSize: MainAxisSize.max, children: [
                  if (this.textValue.length <= 0)
                    Expanded(
                        child: Text(
                            this.placeHolder != null ? this.placeHolder : '',
                            style: InputFieldHelper.validatePalceHolderStyle(
                                InputFieldHelper.validateTextStyle(this.style),
                                this.placeHolderColor)))
                ])),
          Align(
              alignment:
                  this.maxLines == 1 ? Alignment.center : Alignment.topLeft,
              child: EditableText(
                  key: editableTextKey,
                  selectionControls: DefaultTextSelectionControls(),
                  focusNode: _node,
                  keyboardType: this.keyboardType,
                  strutStyle: this.strutStyle,
                  textAlign: this.textAlign,
                  textDirection: this.textDirection,
                  autofocus: this.autofocus,
                  obscureText: this.obscureText,
                  autocorrect: this.autocorrect,
                  textCapitalization: this.textCapitalization,
                  selectionColor: HexColor.fromHexInt(0xff14acff),
                  maxLines: this.maxLines,
                  minLines: this.minLines != 0 ? this.minLines : null,
                  cursorWidth: this.cursorWidth,
                  cursorRadius: this.cursorRadius,
                  keyboardAppearance: this.keyboardAppearance,
                  scrollPadding:
                      InputFieldHelper.getScrollPaddingInfo(this.scrollPadding),
                  enableInteractiveSelection: this.enableInteractiveSelection,
                  dragStartBehavior: this.dragStartBehavior,
                  readOnly: this.readOnly,
                  toolbarOptions: InputFieldHelper.getToolBarOptions(
                      this.toolbarOptions, this.obscureText),
                  showCursor: this.isActiveField,
                  enableSuggestions: this.enableSuggestions,
                  style: InputFieldHelper.validateTextStyle(this.style),
                  cursorColor: this.cursorColor != null
                      ? this.cursorColor
                      : HexColor.fromHexInt(0xff14acff),
                  backgroundCursorColor: HexColor.fromHexInt(0xFF999999),
                  rendererIgnoresPointer: this.isRenderIngnores,
                  controller: _effectiveController,
                  onChanged: (text) {
                    onInputChanged(text);
                  },
                  onSelectionHandleTapped: () {},
                  onEditingComplete: () {
                    onInputEditingComplete();
                  },
                  onSubmitted: (text) {
                    onInputSubmitted(text);
                  })),
        ]));

    child = RepaintBoundary(
        child: Container(
            key: Key(isActive.toString()),
            padding: InputFieldHelper.getPaddingInfo(this.dense, this.padding),
            decoration: BoxDecoration(
                color: this.backgroundColor,
                borderRadius:
                    BorderRadius.all(Radius.circular(this.cornerRadius)),
                border: this.isActiveField
                    ? InputFieldHelper.getBorderInfo(this.activeColor != null
                        ? this.activeColor
                        : HexColor.fromHexInt(0xff14acff))
                    : InputFieldHelper.getBorderInfo(this.inActiveColor)),
            child: this.isRenderIngnores
                ? child
                : _selectionGestureDetectorBuilder.buildGestureDetector(
                    behavior: HitTestBehavior.translucent,
                    child: child,
                  )));

    child = DefaultTextEditingShortcuts(
      child: DefaultTextEditingActions(
        child: child,
      ),
    );
    return child;
  }

  void init() {
    if (widget.controller == null) {
      _controller = TextEditingController();
    } else {
      _textValue = widget.controller.text;
    }
    setNode(InputFieldHelper.validateFocusNode(widget.focusNode));
    _node.addListener(onFocusNodeHandler);
  }

  void onFocusNodeHandler() {
    if (_node.hasFocus) {
      BrowserUtil.enable = false;
      setIsActive(true);
      setIsActiveField(true);
      if (this.onTap != null) {
        this.onTap();
      }
    } else {
      BrowserUtil.enable = true;
      setIsActive(false);
      setIsActiveField(false);
      this.onValueEditComplete();
    }
  }

  void onActive(bool active) {
    if (isActive != active) {
      this.setIsActive(true);
    }
  }

  void onInputEditingComplete() {
    this.setIsActive(false);
    onValueEditComplete();
  }

  void onInputSubmitted(String text) {
    this.setIsActive(false);
    if (this.onSubmitted != null) {
      this.onSubmitted(text);
    }
  }

  void onValueEditComplete() {
    if (this.onEditingComplete != null &&
        _editCompleteString != _effectiveController.text) {
      this._editCompleteString = _effectiveController.text;
      this.onEditingComplete();
    }
  }

  void onInputChanged(String text) {
    this.setTextValue(text);
    if (this.onChanged != null) {
      this.onChanged(text);
      Future.delayed(
          Duration(milliseconds: 2000),
          () => {
                if (text == _effectiveController.text && _node.hasFocus)
                  {onValueEditComplete()}
              });
    }
  }

  void dispose() {
    BrowserUtil.enable = true;
    super.dispose();
  }

  _InputFieldOnChanged get onChanged {
    return this.widget.onChanged;
  }

  _InputFieldOnEditingComplete get onEditingComplete {
    return this.widget.onEditingComplete;
  }

  _InputFieldOnSubmitted get onSubmitted {
    return this.widget.onSubmitted;
  }

  _InputFieldOnTap get onTap {
    return this.widget.onTap;
  }

  TextEditingController get controller {
    return this.widget.controller;
  }

/*///////////////////////////////////////////////////////////////// */
  final GlobalKey<EditableTextState> editableTextKey =
      GlobalKey<EditableTextState>();

  EditableTextState get _editableText => editableTextKey.currentState;

  @override
  // TODO: implement forcePressEnabled
  bool get forcePressEnabled => true;

  @override
  // TODO: implement selectionEnabled
  bool get selectionEnabled => true;

  void _requestKeyboard() {
    _editableText?.requestKeyboard();
  }
}

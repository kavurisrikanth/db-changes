import 'package:flutter/widgets.dart';
import '../classes/core.dart';
import 'InputField.dart';
import 'ColorPiker.dart';

const double _height = 180.0;

class ColorWithName {
  String colorName;
  String hexaCode;
  ColorWithName({String colorName, String hexaCode}) {
    setColorName(colorName);
    setHexaCode(hexaCode);
  }
  setColorName(String val) {
    bool isValChanged = colorName != val;

    if (!isValChanged) {
      return;
    }
    this.colorName = val;
  }

  setHexaCode(String val) {
    bool isValChanged = hexaCode != val;

    if (!isValChanged) {
      return;
    }

    this.hexaCode = val;
  }

  String get colorname {
    return this.colorName;
  }

  String get hexacode {
    return this.hexaCode;
  }
}

typedef void _ColorPickerNativeOnColorChanged(Color value);

class _SliderIndicatorPainter extends CustomPainter {
  final double position;
  _SliderIndicatorPainter(this.position);
  @override
  void paint(Canvas canvas, Size size) {
    canvas.drawCircle(Offset(size.width / 2, position), 13,
        Paint()..color = Color(0xFF000000));
  }

  @override
  bool shouldRepaint(_SliderIndicatorPainter old) {
    return true;
  }
}

class ColorPickerNative extends StatefulWidget {
  final Color initialColor;
  final _ColorPickerNativeOnColorChanged onColorChanged;
  ColorPickerNative({this.initialColor, this.onColorChanged});
  @override
  _ColorPickerState createState() => _ColorPickerState();
}

class _ColorPickerState extends State<ColorPickerNative> {
  TextEditingController _hexaColorController = TextEditingController();
  final List<Color> _colors = [
    Color.fromARGB(255, 255, 0, 0),
    Color.fromARGB(255, 255, 128, 0),
    Color.fromARGB(255, 255, 255, 0),
    Color.fromARGB(255, 128, 255, 0),
    Color.fromARGB(255, 0, 255, 0),
    Color.fromARGB(255, 0, 255, 128),
    Color.fromARGB(255, 0, 255, 255),
    Color.fromARGB(255, 0, 128, 255),
    Color.fromARGB(255, 0, 0, 255),
    Color.fromARGB(255, 127, 0, 255),
    Color.fromARGB(255, 255, 0, 255),
    Color.fromARGB(255, 255, 0, 127),
    Color.fromARGB(255, 128, 128, 128),
    Color.fromARGB(255, 0, 0, 0),
  ];
  double _colorSliderPosition = 0;
  double _shadeSliderPosition;
  Color _currentColor;
  Color _shadedColor;
  @override
  initState() {
    super.initState();
    _colorSliderPosition = widget.initialColor != null
        ? _calculateColorPosition(widget.initialColor) * 13
        : _colorSliderPosition;
    _currentColor = widget.initialColor != null
        ? widget.initialColor
        : _calculateSelectedColor(_colorSliderPosition);
    _shadeSliderPosition = widget.initialColor != null
        ? _calculateShapedColorPosition(widget.initialColor)
        : _height / 2; //center the shader selector
    _shadedColor = _calculateShadedColor(_shadeSliderPosition);
    _shadedColor =
        widget.initialColor != null ? widget.initialColor : _shadedColor;
    _hexaColorController.text = HexColor.toHexStr(_shadedColor);
  }

  _colorChangeHandler(double position) {
    //handle out of bounds positions
    if (position > _height) {
      position = _height;
    }
    if (position < 0) {
      position = 0;
    }
    setState(() {
      _colorSliderPosition = position;
      _currentColor = _calculateSelectedColor(_colorSliderPosition);
      _shadedColor = _calculateShadedColor(_shadeSliderPosition);
      _hexaColorController.text = HexColor.toHexStr(_shadedColor);
      widget.onColorChanged(_shadedColor);
    });
  }

  _shadeChangeHandler(double position) {
    //handle out of bounds gestures
    if (position > _height) position = _height;
    if (position < 0) position = 0;
    setState(() {
      _shadeSliderPosition = position;
      _shadedColor = _calculateShadedColor(_shadeSliderPosition);
      widget.onColorChanged(_shadedColor);
    });
  }

  Color _calculateShadedColor(double position) {
    double aplhaValue = position * (255 / 180);
    Color color = Color.fromARGB(aplhaValue.round(), _currentColor.red,
        _currentColor.green, _currentColor.blue);
    setState(() {
      _hexaColorController.text = HexColor.toHexStr(color);
    });

    return color;
  }

  int _calculateShapedColorPosition(Color color) {
    int alphValue = color.alpha;
    double markPosition = (180 * alphValue) / 255;
    return markPosition.round();
  }

  int _calculateColorPosition(Color color) {
    int redVal;
    int greenVal;
    int blueVal;

    if (color.green > 128) {
      greenVal = 255;
    } else if (color.green < 127) {
      greenVal = 0;
    } else {
      greenVal = color.green;
    }
    if (color.red > 128) {
      redVal = 255;
    } else if (color.red < 127) {
      redVal = 0;
    } else {
      redVal = color.red;
    }
    if (color.blue > 128) {
      blueVal = 255;
    } else if (color.blue < 127) {
      blueVal = 0;
    } else {
      blueVal = color.blue;
    }
    Color colorValue = Color.fromARGB(255, redVal, greenVal, blueVal);
    int index = _colors.indexOf(colorValue);

    return index;
  }

  Color _calculateSelectedColor(double position) {
    //determine color
    double positionInColorArray = (position / _height * (_colors.length - 1));
    int index = positionInColorArray.truncate();
    double remainder = positionInColorArray - index;
    if (remainder == 0.0) {
      _currentColor = _colors[index];
    } else {
      //calculate new color
      int redValue = _colors[index].red == _colors[index + 1].red
          ? _colors[index].red
          : (_colors[index].red +
                  (_colors[index + 1].red - _colors[index].red) * remainder)
              .round();
      int greenValue = _colors[index].green == _colors[index + 1].green
          ? _colors[index].green
          : (_colors[index].green +
                  (_colors[index + 1].green - _colors[index].green) * remainder)
              .round();
      int blueValue = _colors[index].blue == _colors[index + 1].blue
          ? _colors[index].blue
          : (_colors[index].blue +
                  (_colors[index + 1].blue - _colors[index].blue) * remainder)
              .round();
      _currentColor = Color.fromARGB(255, redValue, greenValue, blueValue);
    }
    return _currentColor;
  }

  void onHexCodeChanges(String text) {
    Color hexCodeColor = HexColor.fromHexStr(text);
    _colorSliderPosition = _calculateColorPosition(hexCodeColor) * 13.0;
    setState(() {
      _colorSliderPosition =
          _calculateShapedColorPosition(hexCodeColor).toDouble();
      _shadedColor = hexCodeColor;
      _currentColor = hexCodeColor;
      widget.onColorChanged(_shadedColor);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Wrap(
              spacing: 10,
              children: <Widget>[
                Container(
                    margin: EdgeInsets.only(top: 10),
                    height: 180,
                    width: 180,
                    child: colorPickerArea(_currentColor)),
                GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onVerticalDragStart: (DragStartDetails details) {
                    _colorChangeHandler(details.localPosition.dy);
                  },
                  onVerticalDragUpdate: (DragUpdateDetails details) {
                    _colorChangeHandler(details.localPosition.dy);
                  },
                  onTapDown: (TapDownDetails details) {
                    _colorChangeHandler(details.localPosition.dy);
                  },
                  //This outside padding makes it much easier to grab the   slider because the gesture detector has
                  // the extra padding to recognize gestures inside of
                  child: Padding(
                    padding: EdgeInsets.all(15),
                    child: Container(
                      width: 10,
                      height: _height,
                      decoration: BoxDecoration(
                        color: Color(0xff343443),
                        borderRadius: BorderRadius.circular(10),
                        gradient: LinearGradient(
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                            colors: _colors),
                      ),
                      child: CustomPaint(
                        painter: _SliderIndicatorPainter(_colorSliderPosition),
                      ),
                    ),
                  ),
                ),
                GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onVerticalDragStart: (DragStartDetails details) {
                    _shadeChangeHandler(details.localPosition.dy);
                  },
                  onVerticalDragUpdate: (DragUpdateDetails details) {
                    _shadeChangeHandler(details.localPosition.dy);
                  },
                  onTapDown: (TapDownDetails details) {
                    _shadeChangeHandler(details.localPosition.dy);
                  },
                  //This outside padding makes it much easier to grab the slider because the gesture detector has
                  // the extra padding to recognize gestures inside of
                  child: Padding(
                    padding: EdgeInsets.all(15),
                    child: Stack(
                      children: [
                        Container(
                            width: 10,
                            height: _height,
                            child: Image.asset('images/grid.png',
                                repeat: ImageRepeat.repeat)),
                        Container(
                          width: 10,
                          height: _height,
                          decoration: BoxDecoration(
                            color: Color(0xff343443),
                            borderRadius: BorderRadius.circular(10),
                            gradient: LinearGradient(
                                begin: Alignment.topCenter,
                                end: Alignment.bottomCenter,
                                colors: getAlphaValueBaseList(_currentColor)),
                          ),
                          child: CustomPaint(
                            painter:
                                _SliderIndicatorPainter(_shadeSliderPosition),
                          ),
                        )
                      ],
                    ),
                  ),
                  onTap: () {
                    this.onColorChanged(_shadedColor);
                  },
                ),
              ],
            ),
            Container(
              child: Wrap(
                crossAxisAlignment: WrapCrossAlignment.center,
                spacing: 10,
                children: [
                  Text('Hex'),
                  Container(
                      width: 75,
                      child: InputField(
                        dense: true,
                        controller: _hexaColorController,
                        onChanged: (text) {
                          onHexCodeChanges(text);
                        },
                      ))
                ],
              ),
            ),
          ],
        ));
  }

  Widget colorPickerArea(Color pickerColor) {
    return ClipRRect(
      borderRadius: BorderRadius.zero,
      child: ColorPickerArea(
        HSVColor.fromColor(pickerColor),
        (HSVColor color) {
          setState(() {
            _currentColor = color.toColor();
            _colorSliderPosition =
                _calculateColorPosition(_currentColor) * 13.0;
            _hexaColorController.text = HexColor.toHexStr(_currentColor);
          });
          _shadedColor = color.toColor();
          widget.onColorChanged(_shadedColor);
        },
        PaletteType.hsv,
      ),
    );
  }

  _ColorPickerNativeOnColorChanged get onColorChanged {
    return this.widget.onColorChanged;
  }

  List<Color> getAlphaValueBaseList(Color color) {
    List<Color> colors = [];
    for (int i = 0; i <= 255; i++) {
      colors.add(Color.fromARGB(i, color.red, color.green, color.blue));
    }
    return colors;
  }
}

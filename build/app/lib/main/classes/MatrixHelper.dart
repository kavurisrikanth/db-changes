import 'dart:ui';
import 'package:flutter/cupertino.dart';
import 'dart:math' as math;

class MatrixHelper {
  MatrixHelper();
  static Matrix4 getMatrixData(Offset offSet, double data) {
    return Matrix4.identity()
      ..translate(offSet.dx, offSet.dy)
      ..scale(data);
  }

  static Matrix4 rotateWithAngle(double angle) {
    double val = angle * math.pi / 180;
    return Matrix4.rotationZ(val);
  }
}

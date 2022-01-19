import 'package:intl/intl.dart';

class DateTimeExt {
  static bool gt(DateTime a, DateTime b) {
    return a.isAfter(b);
  }

  static bool gte(DateTime a, DateTime b) {
    return a.isAfter(b) || a == b;
  }

  static bool lt(DateTime a, DateTime b) {
    return a.isBefore(b);
  }

  static bool lte(DateTime a, DateTime b) {
    return a.isBefore(b) || a == b;
  }

  static DateTime parseTime(String time) {
    DateTime dt;
    try {
      dt = DateTime.parse(time);
    } on FormatException catch (e) {
      try {
        DateFormat timeOnly = DateFormat('HH:mm:ss');
        dt = timeOnly.parse(time);
      } on FormatException catch (e1) {
        try {
          DateFormat timeOnly = DateFormat('HH:mm:ss.SSS');
          dt = timeOnly.parse(time);
        } on FormatException catch (e2) {}
      }
    }

    return dt;
  }

  static DateTime parseDate(String time) {
    DateTime dt;
    try {
      dt = DateTime.parse(time);
    } on FormatException catch (e) {
      try {
        DateFormat dateOnly = DateFormat('YYYY-MM-DD');
        dt = dateOnly.parse(time);
      } on FormatException catch (e1) {}
    }

    return dt;
  }
}

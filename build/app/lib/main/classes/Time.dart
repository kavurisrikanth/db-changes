import 'DateTimeExt.dart';
import 'Date.dart';

class Time {
  DateTime _dateTime;

  Time._init() {
    this._dateTime = DateTime.now();
  }

  int get hour {
    return this._dateTime.hour;
  }

  int get minutes {
    return this._dateTime.minute;
  }

  int get seconds {
    return this._dateTime.second;
  }

  int get millisecond {
    return this._dateTime.millisecond;
  }

  int get microSeconds {
    return this._dateTime.millisecond;
  }

  bool isBefore(Time other) {
    // TODO: Does this work?
    return this._dateTime.isBefore(other._dateTime);
  }

  bool isAfter(Time other) {
    // TODO: Does this work?
    return this._dateTime.isAfter(other._dateTime);
  }

  bool isAtSameMomentAs(Time other) {
    // TODO: Does this work?
    return this._dateTime.isAtSameMomentAs(other._dateTime);
  }

  Time plusHours(int hours) {
    return _add(hours, 'hours');
  }

  Time plusMinutes(int mins) {
    return _add(mins, 'minutes');
  }

  Time plusSeconds(int seconds) {
    return _add(seconds, 'seconds');
  }

  Time plusMilliSeconds(int milliSeconds) {
    return _add(milliSeconds, 'milliseconds');
  }

  Time plusMicroSeconds(int microSeconds) {
    return _add(microSeconds, 'microseconds');
  }

  Time _add(int quantity, String what) {
    if (quantity == 0) {
      return this;
    }
    Time copy = _copy();
    switch (what) {
      case 'hours':
        copy._dateTime.add(Duration(hours: quantity));
        break;
      case 'minutes':
        copy._dateTime.add(Duration(minutes: quantity));
        break;
      case 'seconds':
        copy._dateTime.add(Duration(seconds: quantity));
        break;
      case 'milliseconds':
        copy._dateTime.add(Duration(milliseconds: quantity));
        break;
      case 'microseconds':
        copy._dateTime.add(Duration(microseconds: quantity));
        break;
      default:
        return this;
    }
    return copy;
  }

  Time _copy() {
    Time newTime = Time._from(
        _dateTime.year,
        _dateTime.month,
        _dateTime.day,
        _dateTime.hour,
        _dateTime.minute,
        _dateTime.second,
        _dateTime.millisecond,
        _dateTime.microsecond);
    return newTime;
  }

  Time._from(int year, int month, int day, int hour, int minute, int second,
      int millisecond, int microSeconds) {
    this._dateTime = DateTime(
        year, month, day, hour, minute, second, millisecond, microSeconds);
  }

  Time._of(
      int hour, int minute, int second, int millisecond, int microSeconds) {
    this._dateTime = DateTime(DateTime.now().year, DateTime.now().month,
        DateTime.now().day, hour, minute, second, millisecond, microSeconds);
  }

  factory Time.now() {
    return Time._init();
  }
  factory Time.of(
      int hour, int minutes, int seconds, int milliSeconds, int microSeconds) {
    return Time._of(hour, minutes, seconds, milliSeconds, microSeconds);
  }

  Time.fromDateTime(DateTime dt) {
    this._dateTime = dt;
  }

  static Time parse(String time) {
    DateTime dt = DateTimeExt.parseTime(time);
    if (dt == null) {
      return null;
    }

    Time t = Time.fromDateTime(dt);
    return t;
  }

  @override
  String toString() {
    if (this._dateTime == null) {
      return super.toString();
    }
    return this._dateTime.toString();
  }

  DateTime toDateTime([Date date]) {
    return _dateTime;
  }
}

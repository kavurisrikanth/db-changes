import 'DateTimeExt.dart';
import 'Time.dart';

class Date {
  DateTime _dateTime;

  Date._now() {
    this._dateTime = DateTime.now();
  }

  Date._of(int year, int month, int dayOfMonth) {
    this._dateTime = DateTime(year, month, dayOfMonth);
  }

  int compareTo(Date other) {
    return this._dateTime.compareTo(other._dateTime);
  }

  int get dayOfMonth {
    return this._dateTime.day;
  }

  int get day {
    return this._dateTime.day;
  }

  int get dayOfWeek {
    return this._dateTime.weekday;
  }

  int get month {
    return this._dateTime.month;
  }

  int get year {
    return this._dateTime.year;
  }

  bool isLeapYear() {
    int year = this._dateTime.year;
    if (year % 4 != 0) {
      return false;
    }

    // Divisible by 4
    if (year % 100 != 0) {
      return true;
    }

    // Divisible by 4 and 100
    return year % 400 == 0;
  }

  bool isBefore(Date other) {
    return this._dateTime.isBefore(other._dateTime);
  }

  bool isAfter(Date other) {
    return this._dateTime.isAfter(other._dateTime);
  }

  bool gt(Date other) {
    return this.isAfter(other);
  }

  bool lt(Date other) {
    return this.isBefore(other);
  }

  bool gte(Date other) {
    return this.isAfter(other) || this == other;
  }

  bool lte(Date other) {
    return this.isBefore(other) || this == other;
  }

  Date plusYears(int years) {
    return _add(years, 'years');
  }

  Date plusMonths(int months) {
    return _add(months, 'months');
  }

  Date plusWeeks(int weeks) {
    return _add(weeks, 'weeks');
  }

  Date plusDays(int days) {
    return _add(days, 'days');
  }

  Date _add(int quantity, String what) {
    if (quantity == 0) {
      return this;
    }

    switch (what) {
      case 'years':
        return Date._of(this.year + quantity, this.month, this.dayOfMonth);
      case 'months':
        int curMonth = this.month;
        int curYear = this.year;
        if (curMonth + quantity == 0) {
          curMonth = 12;
          curYear = curYear - 1;
        } else {
          if ((curMonth + quantity) % 13 == 0) {
            curMonth = 1;
            curYear = curYear + 1;
          } else {
            curMonth = curMonth + quantity;
          }
        }
        return Date._of(curYear, curMonth, this.dayOfMonth);
      case 'weeks':
        Date copy = Date._of(this.year, this.month, this.dayOfMonth);
        DateTime time = copy._dateTime.add(Duration(days: quantity * 7));
        copy = Date.of(time.year, time.month, time.day);
        return copy;
      case 'days':
        Date copy = Date._of(this.year, this.month, this.dayOfMonth);
        DateTime time = copy._dateTime.add(Duration(days: quantity));
        copy = Date.of(time.year, time.month, time.day);
        return copy;
      default:
        return this;
    }
  }

  factory Date.now() {
    return Date._now();
  }

  static Date parse(String value) {
    DateTime dt = DateTimeExt.parseDate(value);
    if (dt == null) {
      return null;
    }

    Date t = Date._fromDateTime(dt);
    return t;
  }

  Date._fromDateTime(DateTime dt) {
    this._dateTime = dt;
  }

  factory Date.of(int year, int month, int dayOfMonth) {
    return Date._of(year, month, dayOfMonth);
  }

  @override
  String toString() {
    if (this._dateTime == null) {
      return super.toString();
    }
    return this._dateTime.toString();
  }

  DateTime toDateTime([Time time]) {
    return _dateTime;
  }
}

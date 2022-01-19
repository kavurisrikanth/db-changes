import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:intl/intl.dart';

typedef void _D3ETimeViewOnChangedDate(DateTime date);

class D3ETimeView extends StatefulWidget {
  final DateTime initialTime;
  final _D3ETimeViewOnChangedDate onChangedTime;
  D3ETimeView({Key key, this.initialTime, this.onChangedTime})
      : super(key: key);
  @override
  _D3ETimeViewState createState() => _D3ETimeViewState();
}

class _D3ETimeViewState extends State<D3ETimeView> {
  TimeOfDay time;
  DateTime internalTime;
  @override
  initState() {
    super.initState();
    init();
  }

  void init() {
    if (this.widget.initialTime == null) {
      time = TimeOfDay.now();
      internalTime = DateTime.now();
    } else {
      time = TimeOfDay(
          hour: this.widget.initialTime.hour,
          minute: this.widget.initialTime.minute);
      internalTime = this.widget.initialTime;
    }
  }

  void didUpdateWidget(D3ETimeView oldWidget) {
    super.didUpdateWidget(oldWidget);
  }

  Future<Null> _selectTime(BuildContext context) async {
    internalTime = DateTime.now();
    if (this.widget.initialTime == null) {
      time = TimeOfDay.now();
    } else {
      time = TimeOfDay(
          hour: this.widget.initialTime.hour,
          minute: this.widget.initialTime.minute);
      internalTime = this.widget.initialTime;
      internalTime = DateTime(
          internalTime.year,
          internalTime.month,
          internalTime.day,
          this.widget.initialTime.hour,
          this.widget.initialTime.minute,
          this.widget.initialTime.second,
          this.widget.initialTime.millisecond,
          this.widget.initialTime.millisecond);
    }

    final TimeOfDay newtime = await showTimePicker(
      context: context,
      initialTime: TimeOfDay(hour: this.time.hour, minute: this.time.minute),
    );
    if (newtime != null) {
      setState(() {
        this.internalTime = DateTime(internalTime.year, internalTime.month,
            internalTime.day, newtime.hour, newtime.minute);
      });
    }
    if (this.internalTime != null) {
      onChangedTime(internalTime);
    }
  }

  @override
  Widget build(BuildContext context) {
    return FlatButton(
        onPressed: () => _selectTime(context),
        child: Text(new DateFormat.jm().format(this.widget.initialTime == null
            ? DateTime.now()
            : DateTime(
                internalTime.year,
                internalTime.month,
                internalTime.day,
                this.widget.initialTime.hour,
                this.widget.initialTime.minute,
                this.widget.initialTime.second,
                this.widget.initialTime.millisecond,
                this.widget.initialTime.millisecond))));
  }

  _D3ETimeViewOnChangedDate get onChangedTime {
    return this.widget.onChangedTime;
  }
}

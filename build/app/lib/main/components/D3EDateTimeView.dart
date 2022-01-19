import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:intl/intl.dart';

typedef void _D3EDateTimeViewOnChangedDate(DateTime date);

class D3EDateTimeView extends StatefulWidget {
  final DateTime initialDate;
  final DateTime firstDate;
  final DateTime lastDate;
  final _D3EDateTimeViewOnChangedDate onChangedDateTime;
  D3EDateTimeView(
      {Key key,
      this.initialDate,
      this.firstDate,
      this.lastDate,
      this.onChangedDateTime})
      : super(key: key);
  @override
  _D3EDateTimeViewState createState() => _D3EDateTimeViewState();
}

class _D3EDateTimeViewState extends State<D3EDateTimeView> {
  DateTime initialDate;
  DateTime firstDate;
  DateTime lastDate;
  @override
  initState() {
    super.initState();

    //init();
  }

  void init() {
    if (this.widget.initialDate == null) {
      initialDate = DateTime.now();
    } else {
      initialDate = this.widget.initialDate;
    }
    if (this.widget.firstDate == null) {
      firstDate = DateTime(1800, 1, 1);
    } else {
      firstDate = this.widget.firstDate;
    }
    if (this.widget.lastDate == null) {
      lastDate = DateTime(2200, 12, 31);
    } else {
      lastDate = this.widget.lastDate;
    }
  }

  void didUpdateWidget(D3EDateTimeView oldWidget) {
    super.didUpdateWidget(oldWidget);
  }

  Future<Null> _selectDate(BuildContext context) async {
    final DateTime picked = await showDatePicker(
      context: context,
      initialDate: this.widget.initialDate == null
          ? DateTime.now()
          : this.widget.initialDate,
      firstDate: this.widget.firstDate == null
          ? DateTime(1800, 1, 1)
          : this.widget.firstDate,
      lastDate: this.widget.lastDate == null
          ? DateTime(2200, 12, 31)
          : this.widget.lastDate,
    );
    if (picked != null) {
      setState(() {
        initialDate = picked;
      });
    }
  }

  Future<Null> _selectTime(BuildContext context) async {
    final TimeOfDay time = await showTimePicker(
      context: context,
      initialTime:
          TimeOfDay(hour: initialDate.hour, minute: initialDate.minute),
    );
    if (time != null) {
      setState(() {
        initialDate = DateTime(initialDate.year, initialDate.month,
            initialDate.day, time.hour, time.minute);
      });
    }
    onChangedDate(initialDate);
  }

  Future<Null> _selectDateAndTime(BuildContext context) async {
    await _selectDate(context);
    await _selectTime(context);
  }

  @override
  Widget build(BuildContext context) {
    return FlatButton(
        onPressed: () => _selectDateAndTime(context),
        child: Text(new DateFormat.yMd().add_jm().format(
            this.widget.initialDate == null
                ? DateTime.now()
                : this.widget.initialDate)));
  }

  _D3EDateTimeViewOnChangedDate get onChangedDate {
    return this.widget.onChangedDateTime;
  }
}

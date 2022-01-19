import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:intl/intl.dart';

typedef void _D3EDateViewOnChangedDate(DateTime date);

class D3EDateView extends StatefulWidget {
  final DateTime initialDate;
  final DateTime firstDate;
  final DateTime lastDate;
  final _D3EDateViewOnChangedDate onChangedDate;
  D3EDateView(
      {Key key,
      this.initialDate,
      this.firstDate,
      this.lastDate,
      this.onChangedDate})
      : super(key: key);
  @override
  _D3EDateViewState createState() => _D3EDateViewState();
}

class _D3EDateViewState extends State<D3EDateView> {
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

  void didUpdateWidget(D3EDateView oldWidget) {
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
    onChangedDate(initialDate);
  }

  @override
  Widget build(BuildContext context) {
    return FlatButton(
        onPressed: () => _selectDate(context),
        child: Text(new DateFormat.yMd().format(this.widget.initialDate == null
            ? DateTime.now()
            : this.widget.initialDate)));
  }

  _D3EDateViewOnChangedDate get onChangedDate {
    return this.widget.onChangedDate;
  }
}

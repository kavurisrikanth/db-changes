package classes;

import d3e.core.ListExt;
import d3e.core.SchemaConstants;
import java.util.List;
import store.DBObject;

public class ReportOutput extends DBObject {
  public static final int _OPTIONS = 0;
  public static final int _COLUMNS = 1;
  public static final int _SUBCOLUMNS = 2;
  public static final int _ATTRIBUTES = 3;
  public static final int _ROWS = 4;
  private long id;
  private List<ReportOutOption> options = ListExt.List();
  private List<ReportOutColumn> columns = ListExt.List();
  private List<ReportOutColumn> subColumns = ListExt.List();
  private List<ReportOutAttribute> attributes = ListExt.List();
  private List<ReportOutRow> rows = ListExt.List();

  public ReportOutput() {}

  public ReportOutput(
      List<ReportOutAttribute> attributes,
      List<ReportOutColumn> columns,
      List<ReportOutOption> options,
      List<ReportOutRow> rows,
      List<ReportOutColumn> subColumns) {
    this.attributes = attributes;
    this.columns = columns;
    this.options = options;
    this.rows = rows;
    this.subColumns = subColumns;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<ReportOutOption> getOptions() {
    return options;
  }

  public void setOptions(List<ReportOutOption> options) {
    collFieldChanged(_OPTIONS, this.options);
    this.options = options;
  }

  public void addToOptions(ReportOutOption val, long index) {
    collFieldChanged(_OPTIONS, this.options);
    if (index == -1) {
      this.options.add(val);
    } else {
      this.options.add(((int) index), val);
    }
  }

  public void removeFromOptions(ReportOutOption val) {
    collFieldChanged(_OPTIONS, this.options);
    this.options.remove(val);
  }

  public List<ReportOutColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<ReportOutColumn> columns) {
    collFieldChanged(_COLUMNS, this.columns);
    this.columns = columns;
  }

  public void addToColumns(ReportOutColumn val, long index) {
    collFieldChanged(_COLUMNS, this.columns);
    if (index == -1) {
      this.columns.add(val);
    } else {
      this.columns.add(((int) index), val);
    }
  }

  public void removeFromColumns(ReportOutColumn val) {
    collFieldChanged(_COLUMNS, this.columns);
    this.columns.remove(val);
  }

  public List<ReportOutColumn> getSubColumns() {
    return subColumns;
  }

  public void setSubColumns(List<ReportOutColumn> subColumns) {
    collFieldChanged(_SUBCOLUMNS, this.subColumns);
    this.subColumns = subColumns;
  }

  public void addToSubColumns(ReportOutColumn val, long index) {
    collFieldChanged(_SUBCOLUMNS, this.subColumns);
    if (index == -1) {
      this.subColumns.add(val);
    } else {
      this.subColumns.add(((int) index), val);
    }
  }

  public void removeFromSubColumns(ReportOutColumn val) {
    collFieldChanged(_SUBCOLUMNS, this.subColumns);
    this.subColumns.remove(val);
  }

  public List<ReportOutAttribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<ReportOutAttribute> attributes) {
    collFieldChanged(_ATTRIBUTES, this.attributes);
    this.attributes = attributes;
  }

  public void addToAttributes(ReportOutAttribute val, long index) {
    collFieldChanged(_ATTRIBUTES, this.attributes);
    if (index == -1) {
      this.attributes.add(val);
    } else {
      this.attributes.add(((int) index), val);
    }
  }

  public void removeFromAttributes(ReportOutAttribute val) {
    collFieldChanged(_ATTRIBUTES, this.attributes);
    this.attributes.remove(val);
  }

  public List<ReportOutRow> getRows() {
    return rows;
  }

  public void setRows(List<ReportOutRow> rows) {
    collFieldChanged(_ROWS, this.rows);
    this.rows = rows;
  }

  public void addToRows(ReportOutRow val, long index) {
    collFieldChanged(_ROWS, this.rows);
    if (index == -1) {
      this.rows.add(val);
    } else {
      this.rows.add(((int) index), val);
    }
  }

  public void removeFromRows(ReportOutRow val) {
    collFieldChanged(_ROWS, this.rows);
    this.rows.remove(val);
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ReportOutput;
  }

  @Override
  public String _type() {
    return "ReportOutput";
  }

  @Override
  public int _fieldsCount() {
    return 5;
  }

  public void _convertToObjectRef() {
    this.options.forEach((a) -> a._convertToObjectRef());
    this.columns.forEach((a) -> a._convertToObjectRef());
    this.subColumns.forEach((a) -> a._convertToObjectRef());
    this.attributes.forEach((a) -> a._convertToObjectRef());
    this.rows.forEach((a) -> a._convertToObjectRef());
  }
}

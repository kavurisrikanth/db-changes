package classes;

import d3e.core.SchemaConstants;
import store.DBObject;

public class ReportOutAttribute extends DBObject {
  public static final int _KEY = 0;
  public static final int _VALUE = 1;
  private long id;
  private String key;
  private String value;

  public ReportOutAttribute() {}

  public ReportOutAttribute(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    fieldChanged(_KEY, this.key);
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    fieldChanged(_VALUE, this.value);
    this.value = value;
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ReportOutAttribute;
  }

  @Override
  public String _type() {
    return "ReportOutAttribute";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void _convertToObjectRef() {}
}

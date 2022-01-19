package classes;

import d3e.core.ListExt;
import d3e.core.SchemaConstants;
import java.util.List;
import store.DBObject;

public class ReportOutColumn extends DBObject {
  public static final int _TYPE = 0;
  public static final int _VALUE = 1;
  public static final int _ATTRIBUTES = 2;
  private long id;
  private String type;
  private String value;
  private List<ReportOutAttribute> attributes = ListExt.List();

  public ReportOutColumn() {}

  public ReportOutColumn(List<ReportOutAttribute> attributes, String type, String value) {
    this.attributes = attributes;
    this.type = type;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    fieldChanged(_TYPE, this.type);
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    fieldChanged(_VALUE, this.value);
    this.value = value;
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

  @Override
  public int _typeIdx() {
    return SchemaConstants.ReportOutColumn;
  }

  @Override
  public String _type() {
    return "ReportOutColumn";
  }

  @Override
  public int _fieldsCount() {
    return 3;
  }

  public void _convertToObjectRef() {
    this.attributes.forEach((a) -> a._convertToObjectRef());
  }
}

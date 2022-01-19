package classes;

import d3e.core.ListExt;
import d3e.core.SchemaConstants;
import java.util.List;
import store.DBObject;

public class ReportOutCell extends DBObject {
  public static final int _KEY = 0;
  public static final int _TYPE = 1;
  public static final int _VALUE = 2;
  public static final int _ATTRIBUTES = 3;
  private long id;
  private String key;
  private String type;
  private String value;
  private List<ReportOutAttribute> attributes = ListExt.List();

  public ReportOutCell() {}

  public ReportOutCell(List<ReportOutAttribute> attributes, String key, String type, String value) {
    this.attributes = attributes;
    this.key = key;
    this.type = type;
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
    return SchemaConstants.ReportOutCell;
  }

  @Override
  public String _type() {
    return "ReportOutCell";
  }

  @Override
  public int _fieldsCount() {
    return 4;
  }

  public void _convertToObjectRef() {
    this.attributes.forEach((a) -> a._convertToObjectRef());
  }
}

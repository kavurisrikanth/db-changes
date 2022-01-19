package classes;

import d3e.core.ListExt;
import d3e.core.SchemaConstants;
import java.util.List;
import store.DBObject;

public class DBResult extends DBObject {
  public static final int _STATUS = 0;
  public static final int _ERRORS = 1;
  private long id;
  private DBResultStatus status;
  private List<String> errors = ListExt.List();

  public DBResult() {}

  public DBResult(List<String> errors, DBResultStatus status) {
    this.errors = errors;
    this.status = status;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public DBResultStatus getStatus() {
    return status;
  }

  public void setStatus(DBResultStatus status) {
    fieldChanged(_STATUS, this.status);
    this.status = status;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    collFieldChanged(_ERRORS, this.errors);
    this.errors = errors;
  }

  public void addToErrors(String val, long index) {
    collFieldChanged(_ERRORS, this.errors);
    if (index == -1) {
      this.errors.add(val);
    } else {
      this.errors.add(((int) index), val);
    }
  }

  public void removeFromErrors(String val) {
    collFieldChanged(_ERRORS, this.errors);
    this.errors.remove(val);
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.DBResult;
  }

  @Override
  public String _type() {
    return "DBResult";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void _convertToObjectRef() {}
}

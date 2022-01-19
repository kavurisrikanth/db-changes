package classes;

public class DBOperation {
  public long type = 0l;
  public Object data;

  public DBOperation(long type, Object data) {
    this.type = type;
    this.data = data;
  }

  public long getType() {
    return this.type;
  }

  public Object getData() {
    return this.data;
  }
}

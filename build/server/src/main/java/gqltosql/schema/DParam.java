package gqltosql.schema;

public class DParam {
  private int type;
  private boolean collection;
  
  public DParam(int type) {
    this(type, false);
  }
  
  public DParam(int type, boolean collection) {
    this.type = type;
    this.collection = collection;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isCollection() {
    return collection;
  }

  public void setCollection(boolean collection) {
    this.collection = collection;
  }
}

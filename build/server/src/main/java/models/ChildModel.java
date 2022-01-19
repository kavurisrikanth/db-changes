package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.apache.solr.client.solrj.beans.Field;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@Entity
public class ChildModel extends DatabaseObject {
  public static final int _NUM = 0;
  @Field private long num = 0l;
  @Field @ManyToOne private Thing masterThing;
  private transient ChildModel old;

  public ChildModel() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ChildModel;
  }

  @Override
  public String _type() {
    return "ChildModel";
  }

  @Override
  public int _fieldsCount() {
    return 1;
  }

  public DatabaseObject _masterObject() {
    if (masterThing != null) {
      return masterThing;
    }
    return null;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
  }

  public void updateFlat(DatabaseObject obj) {
    super.updateFlat(obj);
    if (masterThing != null) {
      masterThing.updateFlat(obj);
    }
  }

  public long getNum() {
    _checkProxy();
    return this.num;
  }

  public void setNum(long num) {
    _checkProxy();
    if (Objects.equals(this.num, num)) {
      return;
    }
    fieldChanged(_NUM, this.num);
    this.num = num;
  }

  public Thing getMasterThing() {
    return this.masterThing;
  }

  public void setMasterThing(Thing masterThing) {
    this.masterThing = masterThing;
  }

  public ChildModel getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((ChildModel) old);
  }

  public String displayName() {
    return "ChildModel";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof ChildModel && super.equals(a);
  }

  public ChildModel deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    ChildModel _obj = ((ChildModel) dbObj);
    _obj.setNum(num);
  }

  public ChildModel cloneInstance(ChildModel cloneObj) {
    if (cloneObj == null) {
      cloneObj = new ChildModel();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setNum(this.getNum());
    return cloneObj;
  }

  public ChildModel createNewInstance() {
    return new ChildModel();
  }

  public boolean needOldObject() {
    return true;
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

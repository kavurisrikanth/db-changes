package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "RefModel")
@Entity
public class RefModel extends CreatableObject {
  public static final int _NUM = 0;
  @Field private long num = 0l;

  public RefModel() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.RefModel;
  }

  @Override
  public String _type() {
    return "RefModel";
  }

  @Override
  public int _fieldsCount() {
    return 1;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
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
    fieldChanged(_NUM, this.num, num);
    this.num = num;
  }

  public String displayName() {
    return "RefModel";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof RefModel && super.equals(a);
  }

  public RefModel deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    RefModel _obj = ((RefModel) dbObj);
    _obj.setNum(num);
  }

  public RefModel cloneInstance(RefModel cloneObj) {
    if (cloneObj == null) {
      cloneObj = new RefModel();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setNum(this.getNum());
    return cloneObj;
  }

  public RefModel createNewInstance() {
    return new RefModel();
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

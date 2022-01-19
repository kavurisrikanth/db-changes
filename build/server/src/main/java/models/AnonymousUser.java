package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.function.Consumer;
import javax.persistence.Entity;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DBObject;
import store.DatabaseObject;

@SolrDocument(collection = "AnonymousUser")
@Entity
public class AnonymousUser extends User {
  public AnonymousUser() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.AnonymousUser;
  }

  @Override
  public String _type() {
    return "AnonymousUser";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof AnonymousUser && super.equals(a);
  }

  public AnonymousUser deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public AnonymousUser cloneInstance(AnonymousUser cloneObj) {
    if (cloneObj == null) {
      cloneObj = new AnonymousUser();
    }
    super.cloneInstance(cloneObj);
    return cloneObj;
  }

  public AnonymousUser createNewInstance() {
    return new AnonymousUser();
  }

  @Override
  public boolean _isEntity() {
    return true;
  }

  @Override
  protected void _handleChildChange(int _childIdx, boolean set, DBObject trigger) {
    super._handleChildChange(_childIdx, set, trigger);
  }
}

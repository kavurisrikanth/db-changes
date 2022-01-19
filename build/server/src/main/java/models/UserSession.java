package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "UserSession")
@Entity
public abstract class UserSession extends CreatableObject {
  public static final int _USERSESSIONID = 0;
  @Field @NotNull private String userSessionId;

  public UserSession() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.UserSession;
  }

  @Override
  public String _type() {
    return "UserSession";
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

  public String getUserSessionId() {
    _checkProxy();
    return this.userSessionId;
  }

  public void setUserSessionId(String userSessionId) {
    _checkProxy();
    if (Objects.equals(this.userSessionId, userSessionId)) {
      return;
    }
    fieldChanged(_USERSESSIONID, this.userSessionId, userSessionId);
    this.userSessionId = userSessionId;
  }

  public String displayName() {
    return "UserSession";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof UserSession && super.equals(a);
  }

  public UserSession deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    UserSession _obj = ((UserSession) dbObj);
    _obj.setUserSessionId(userSessionId);
  }

  public UserSession cloneInstance(UserSession cloneObj) {
    super.cloneInstance(cloneObj);
    cloneObj.setUserSessionId(this.getUserSessionId());
    return cloneObj;
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

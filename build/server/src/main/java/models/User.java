package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "User")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends CreatableObject {
  public static final int _ISACTIVE = 0;
  public static final int _DEVICETOKEN = 1;
  @Field private boolean isActive = false;
  @Field private String deviceToken;

  public User() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.User;
  }

  @Override
  public String _type() {
    return "User";
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

  public boolean isIsActive() {
    _checkProxy();
    return this.isActive;
  }

  public void setIsActive(boolean isActive) {
    _checkProxy();
    if (Objects.equals(this.isActive, isActive)) {
      return;
    }
    fieldChanged(_ISACTIVE, this.isActive, isActive);
    this.isActive = isActive;
  }

  public String getDeviceToken() {
    _checkProxy();
    return this.deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    _checkProxy();
    if (Objects.equals(this.deviceToken, deviceToken)) {
      return;
    }
    fieldChanged(_DEVICETOKEN, this.deviceToken, deviceToken);
    this.deviceToken = deviceToken;
  }

  public String displayName() {
    return "";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof User && super.equals(a);
  }

  public User deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    User _obj = ((User) dbObj);
    _obj.setIsActive(isActive);
    _obj.setDeviceToken(deviceToken);
  }

  public User cloneInstance(User cloneObj) {
    super.cloneInstance(cloneObj);
    cloneObj.setIsActive(this.isIsActive());
    cloneObj.setDeviceToken(this.getDeviceToken());
    return cloneObj;
  }

  @Override
  public String toString() {
    return displayName();
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

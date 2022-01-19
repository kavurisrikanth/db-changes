package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

public class ChangePasswordRequest extends CreatableObject {
  public static final int _NEWPASSWORD = 0;
  @Field @NotNull private String newPassword;

  public ChangePasswordRequest() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ChangePasswordRequest;
  }

  @Override
  public String _type() {
    return "ChangePasswordRequest";
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

  public String getNewPassword() {
    _checkProxy();
    return this.newPassword;
  }

  public void setNewPassword(String newPassword) {
    _checkProxy();
    if (Objects.equals(this.newPassword, newPassword)) {
      return;
    }
    fieldChanged(_NEWPASSWORD, this.newPassword, newPassword);
    this.newPassword = newPassword;
  }

  public String displayName() {
    return "ChangePasswordRequest";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof ChangePasswordRequest && super.equals(a);
  }

  public ChangePasswordRequest deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    ChangePasswordRequest _obj = ((ChangePasswordRequest) dbObj);
    _obj.setNewPassword(newPassword);
  }

  public ChangePasswordRequest cloneInstance(ChangePasswordRequest cloneObj) {
    if (cloneObj == null) {
      cloneObj = new ChangePasswordRequest();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setNewPassword(this.getNewPassword());
    return cloneObj;
  }

  public boolean transientModel() {
    return true;
  }

  public ChangePasswordRequest createNewInstance() {
    return new ChangePasswordRequest();
  }
}

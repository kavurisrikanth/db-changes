package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "OneTimePassword")
@Entity
public class OneTimePassword extends CreatableObject {
  public static final int _INPUT = 0;
  public static final int _INPUTTYPE = 1;
  public static final int _USERTYPE = 2;
  public static final int _SUCCESS = 3;
  public static final int _ERRORMSG = 4;
  public static final int _TOKEN = 5;
  public static final int _CODE = 6;
  public static final int _USER = 7;
  public static final int _EXPIRY = 8;
  @Field @NotNull private String input;
  @Field @NotNull private String inputType;
  @Field @NotNull private String userType;
  @Field private boolean success = false;
  @Field private String errorMsg;
  @Field private String token;
  @Field private String code;

  @Field
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Field private LocalDateTime expiry;

  public OneTimePassword() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.OneTimePassword;
  }

  @Override
  public String _type() {
    return "OneTimePassword";
  }

  @Override
  public int _fieldsCount() {
    return 9;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
  }

  public String getInput() {
    _checkProxy();
    return this.input;
  }

  public void setInput(String input) {
    _checkProxy();
    if (Objects.equals(this.input, input)) {
      return;
    }
    fieldChanged(_INPUT, this.input);
    this.input = input;
  }

  public String getInputType() {
    _checkProxy();
    return this.inputType;
  }

  public void setInputType(String inputType) {
    _checkProxy();
    if (Objects.equals(this.inputType, inputType)) {
      return;
    }
    fieldChanged(_INPUTTYPE, this.inputType);
    this.inputType = inputType;
  }

  public String getUserType() {
    _checkProxy();
    return this.userType;
  }

  public void setUserType(String userType) {
    _checkProxy();
    if (Objects.equals(this.userType, userType)) {
      return;
    }
    fieldChanged(_USERTYPE, this.userType);
    this.userType = userType;
  }

  public boolean isSuccess() {
    _checkProxy();
    return this.success;
  }

  public void setSuccess(boolean success) {
    _checkProxy();
    if (Objects.equals(this.success, success)) {
      return;
    }
    fieldChanged(_SUCCESS, this.success);
    this.success = success;
  }

  public String getErrorMsg() {
    _checkProxy();
    return this.errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    _checkProxy();
    if (Objects.equals(this.errorMsg, errorMsg)) {
      return;
    }
    fieldChanged(_ERRORMSG, this.errorMsg);
    this.errorMsg = errorMsg;
  }

  public String getToken() {
    _checkProxy();
    return this.token;
  }

  public void setToken(String token) {
    _checkProxy();
    if (Objects.equals(this.token, token)) {
      return;
    }
    fieldChanged(_TOKEN, this.token);
    this.token = token;
  }

  public String getCode() {
    _checkProxy();
    return this.code;
  }

  public void setCode(String code) {
    _checkProxy();
    if (Objects.equals(this.code, code)) {
      return;
    }
    fieldChanged(_CODE, this.code);
    this.code = code;
  }

  public User getUser() {
    _checkProxy();
    return this.user;
  }

  public void setUser(User user) {
    _checkProxy();
    if (Objects.equals(this.user, user)) {
      return;
    }
    fieldChanged(_USER, this.user);
    this.user = user;
  }

  public LocalDateTime getExpiry() {
    _checkProxy();
    return this.expiry;
  }

  public void setExpiry(LocalDateTime expiry) {
    _checkProxy();
    if (Objects.equals(this.expiry, expiry)) {
      return;
    }
    fieldChanged(_EXPIRY, this.expiry);
    this.expiry = expiry;
  }

  public String displayName() {
    return "OneTimePassword";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof OneTimePassword && super.equals(a);
  }

  public OneTimePassword deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    OneTimePassword _obj = ((OneTimePassword) dbObj);
    _obj.setInput(input);
    _obj.setInputType(inputType);
    _obj.setUserType(userType);
    _obj.setSuccess(success);
    _obj.setErrorMsg(errorMsg);
    _obj.setToken(token);
    _obj.setCode(code);
    _obj.setUser(user);
    _obj.setExpiry(expiry);
  }

  public OneTimePassword cloneInstance(OneTimePassword cloneObj) {
    if (cloneObj == null) {
      cloneObj = new OneTimePassword();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setInput(this.getInput());
    cloneObj.setInputType(this.getInputType());
    cloneObj.setUserType(this.getUserType());
    cloneObj.setSuccess(this.isSuccess());
    cloneObj.setErrorMsg(this.getErrorMsg());
    cloneObj.setToken(this.getToken());
    cloneObj.setCode(this.getCode());
    cloneObj.setUser(this.getUser());
    cloneObj.setExpiry(this.getExpiry());
    return cloneObj;
  }

  public OneTimePassword createNewInstance() {
    return new OneTimePassword();
  }

  public void collectCreatableReferences(List<Object> _refs) {
    super.collectCreatableReferences(_refs);
    _refs.add(this.user);
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

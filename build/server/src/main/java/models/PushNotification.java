package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.solr.client.solrj.beans.Field;
import store.D3EPersistanceList;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

public class PushNotification extends CreatableObject {
  public static final int _DEVICETOKENS = 0;
  public static final int _TITLE = 1;
  public static final int _BODY = 2;
  public static final int _PATH = 3;
  @Field private List<String> deviceTokens = new D3EPersistanceList<>(this, _DEVICETOKENS);
  @Field private String title;
  @Field private String body;
  @Field private String path;

  public PushNotification() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.PushNotification;
  }

  @Override
  public String _type() {
    return "PushNotification";
  }

  @Override
  public int _fieldsCount() {
    return 4;
  }

  public void addToDeviceTokens(String val, long index) {
    if (index == -1) {
      this.deviceTokens.add(val);
    } else {
      this.deviceTokens.add(((int) index), val);
    }
  }

  public void removeFromDeviceTokens(String val) {
    this.deviceTokens.remove(val);
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
  }

  public List<String> getDeviceTokens() {
    return this.deviceTokens;
  }

  public void setDeviceTokens(List<String> deviceTokens) {
    if (Objects.equals(this.deviceTokens, deviceTokens)) {
      return;
    }
    ((D3EPersistanceList<String>) this.deviceTokens).setAll(deviceTokens);
  }

  public String getTitle() {
    _checkProxy();
    return this.title;
  }

  public void setTitle(String title) {
    _checkProxy();
    if (Objects.equals(this.title, title)) {
      return;
    }
    fieldChanged(_TITLE, this.title, title);
    this.title = title;
  }

  public String getBody() {
    _checkProxy();
    return this.body;
  }

  public void setBody(String body) {
    _checkProxy();
    if (Objects.equals(this.body, body)) {
      return;
    }
    fieldChanged(_BODY, this.body, body);
    this.body = body;
  }

  public String getPath() {
    _checkProxy();
    return this.path;
  }

  public void setPath(String path) {
    _checkProxy();
    if (Objects.equals(this.path, path)) {
      return;
    }
    fieldChanged(_PATH, this.path, path);
    this.path = path;
  }

  public String displayName() {
    return "PushNotification";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof PushNotification && super.equals(a);
  }

  public PushNotification deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    PushNotification _obj = ((PushNotification) dbObj);
    _obj.setDeviceTokens(deviceTokens);
    _obj.setTitle(title);
    _obj.setBody(body);
    _obj.setPath(path);
  }

  public PushNotification cloneInstance(PushNotification cloneObj) {
    if (cloneObj == null) {
      cloneObj = new PushNotification();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setDeviceTokens(new ArrayList<>(this.getDeviceTokens()));
    cloneObj.setTitle(this.getTitle());
    cloneObj.setBody(this.getBody());
    cloneObj.setPath(this.getPath());
    return cloneObj;
  }

  public boolean transientModel() {
    return true;
  }

  public PushNotification createNewInstance() {
    return new PushNotification();
  }
}

package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.solr.client.solrj.beans.Field;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

public class SMSMessage extends D3EMessage {
  public static final int _DLTTEMPLATEID = 4;
  @Field private String dltTemplateId;

  public SMSMessage() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.SMSMessage;
  }

  @Override
  public String _type() {
    return "SMSMessage";
  }

  @Override
  public int _fieldsCount() {
    return 5;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
  }

  public String getDltTemplateId() {
    _checkProxy();
    return this.dltTemplateId;
  }

  public void setDltTemplateId(String dltTemplateId) {
    _checkProxy();
    if (Objects.equals(this.dltTemplateId, dltTemplateId)) {
      return;
    }
    fieldChanged(_DLTTEMPLATEID, this.dltTemplateId, dltTemplateId);
    this.dltTemplateId = dltTemplateId;
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof SMSMessage && super.equals(a);
  }

  public SMSMessage deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    SMSMessage _obj = ((SMSMessage) dbObj);
    _obj.setDltTemplateId(dltTemplateId);
  }

  public SMSMessage cloneInstance(SMSMessage cloneObj) {
    if (cloneObj == null) {
      cloneObj = new SMSMessage();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setDltTemplateId(this.getDltTemplateId());
    return cloneObj;
  }

  public boolean transientModel() {
    return true;
  }

  public SMSMessage createNewInstance() {
    return new SMSMessage();
  }

  @Override
  protected void _handleChildChange(int _childIdx, boolean set) {
    super._handleChildChange(_childIdx, set);
  }
}

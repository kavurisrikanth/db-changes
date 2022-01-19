package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@Entity
public class ReportConfigOption extends DatabaseObject {
  public static final int _IDENTITY = 0;
  public static final int _VALUE = 1;
  @Field @NotNull private String identity;
  @Field @NotNull private String value;
  @Field @ManyToOne private ReportConfig masterReportConfig;
  private transient ReportConfigOption old;

  public ReportConfigOption() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ReportConfigOption;
  }

  @Override
  public String _type() {
    return "ReportConfigOption";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public DatabaseObject _masterObject() {
    if (masterReportConfig != null) {
      return masterReportConfig;
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
    if (masterReportConfig != null) {
      masterReportConfig.updateFlat(obj);
    }
  }

  public String getIdentity() {
    _checkProxy();
    return this.identity;
  }

  public void setIdentity(String identity) {
    _checkProxy();
    if (Objects.equals(this.identity, identity)) {
      return;
    }
    fieldChanged(_IDENTITY, this.identity, identity);
    this.identity = identity;
  }

  public String getValue() {
    _checkProxy();
    return this.value;
  }

  public void setValue(String value) {
    _checkProxy();
    if (Objects.equals(this.value, value)) {
      return;
    }
    fieldChanged(_VALUE, this.value, value);
    this.value = value;
  }

  public ReportConfig getMasterReportConfig() {
    return this.masterReportConfig;
  }

  public void setMasterReportConfig(ReportConfig masterReportConfig) {
    this.masterReportConfig = masterReportConfig;
  }

  public ReportConfigOption getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((ReportConfigOption) old);
  }

  public String displayName() {
    return "ReportConfigOption";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof ReportConfigOption && super.equals(a);
  }

  public ReportConfigOption deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    ReportConfigOption _obj = ((ReportConfigOption) dbObj);
    _obj.setIdentity(identity);
    _obj.setValue(value);
  }

  public ReportConfigOption cloneInstance(ReportConfigOption cloneObj) {
    if (cloneObj == null) {
      cloneObj = new ReportConfigOption();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setIdentity(this.getIdentity());
    cloneObj.setValue(this.getValue());
    return cloneObj;
  }

  public ReportConfigOption createNewInstance() {
    return new ReportConfigOption();
  }

  public boolean needOldObject() {
    return true;
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

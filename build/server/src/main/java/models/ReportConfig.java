package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.ChildDocument;
import store.D3EPersistanceList;
import store.DBObject;
import store.Database;
import store.DatabaseObject;
import store.ICloneable;

@Entity
public class ReportConfig extends DatabaseObject {
  public static final int _IDENTITY = 0;
  public static final int _VALUES = 1;
  @Field @NotNull private String identity;

  @Field
  @ChildDocument
  @OrderColumn
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReportConfigOption> values = new D3EPersistanceList<>(this, _VALUES);

  private transient ReportConfig old;

  public ReportConfig() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.ReportConfig;
  }

  @Override
  public String _type() {
    return "ReportConfig";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void addToValues(ReportConfigOption val, long index) {
    val.setMasterReportConfig(this);
    val._setChildIdx(_VALUES);
    if (index == -1) {
      this.values.add(val);
    } else {
      this.values.add(((int) index), val);
    }
  }

  public void removeFromValues(ReportConfigOption val) {
    val._clearChildIdx();
    this.values.remove(val);
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
    for (ReportConfigOption obj : this.getValues()) {
      visitor.accept(obj);
      obj.setMasterReportConfig(this);
      obj._setChildIdx(_VALUES);
      obj.updateMasters(visitor);
    }
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
    for (ReportConfigOption obj : this.getValues()) {
      visitor.accept(obj);
      obj.visitChildren(visitor);
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

  public List<ReportConfigOption> getValues() {
    return this.values;
  }

  public void setValues(List<ReportConfigOption> values) {
    if (Objects.equals(this.values, values)) {
      return;
    }
    ((D3EPersistanceList<ReportConfigOption>) this.values).setAll(values);
    this.values.forEach(
        (one) -> {
          one.setMasterReportConfig(this);
          one._setChildIdx(_VALUES);
        });
  }

  public ReportConfig getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((ReportConfig) old);
  }

  public void recordOld(CloneContext ctx) {
    this.setOld(ctx.getFromCache(this));
    this.getValues().forEach((one) -> one.recordOld(ctx));
  }

  public String displayName() {
    return "ReportConfig";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof ReportConfig && super.equals(a);
  }

  public ReportConfig deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void collectChildValues(CloneContext ctx) {
    super.collectChildValues(ctx);
    ctx.collectChilds(values);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    ReportConfig _obj = ((ReportConfig) dbObj);
    _obj.setIdentity(identity);
    ctx.cloneChildList(values, (v) -> _obj.setValues(v));
  }

  public ReportConfig cloneInstance(ReportConfig cloneObj) {
    if (cloneObj == null) {
      cloneObj = new ReportConfig();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setIdentity(this.getIdentity());
    cloneObj.setValues(
        this.getValues().stream()
            .map((ReportConfigOption colObj) -> colObj.cloneInstance(null))
            .collect(Collectors.toList()));
    return cloneObj;
  }

  public ReportConfig createNewInstance() {
    return new ReportConfig();
  }

  public boolean needOldObject() {
    return true;
  }

  public void collectCreatableReferences(List<Object> _refs) {
    super.collectCreatableReferences(_refs);
    Database.collectCollctionCreatableReferences(_refs, this.values);
  }

  @Override
  public boolean _isEntity() {
    return true;
  }

  @Override
  protected void _handleChildChange(int _childIdx) {
    switch (_childIdx) {
      case _VALUES:
        {
          this.collFieldChanged(_childIdx, this.values);
          break;
        }
    }
  }
}

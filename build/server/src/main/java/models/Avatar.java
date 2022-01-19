package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.ChildDocument;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@Entity
public class Avatar extends DatabaseObject {
  public static final int _IMAGE = 0;
  public static final int _CREATEFROM = 1;
  @Field @ChildDocument @Embedded private D3EImage image = new D3EImage();
  @Field private String createFrom;
  private transient Avatar old;

  public Avatar() {
    super();
    this.image.setMasterAvatar(this);
    this.image._setChildIdx(_IMAGE);
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.Avatar;
  }

  @Override
  public String _type() {
    return "Avatar";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
    if (image != null) {
      image.setMasterAvatar(this);
      image.updateMasters(visitor);
    }
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
    if (image != null) {
      image.visitChildren(visitor);
    }
  }

  public D3EImage getImage() {
    _checkProxy();
    return this.image;
  }

  public void setImage(D3EImage image) {
    _checkProxy();
    if (Objects.equals(this.image, image)) {
      return;
    }
    fieldChanged(_IMAGE, this.image, image);
    if (image == null) {
      image = new D3EImage();
    }
    this.image = image;
    this.image.setMasterAvatar(this);
    this.image._setChildIdx(_IMAGE);
  }

  public String getCreateFrom() {
    _checkProxy();
    return this.createFrom;
  }

  public void setCreateFrom(String createFrom) {
    _checkProxy();
    if (Objects.equals(this.createFrom, createFrom)) {
      return;
    }
    fieldChanged(_CREATEFROM, this.createFrom, createFrom);
    this.createFrom = createFrom;
  }

  public Avatar getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((Avatar) old);
  }

  public String displayName() {
    return "Avatar";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof Avatar && super.equals(a);
  }

  public Avatar deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void collectChildValues(CloneContext ctx) {
    super.collectChildValues(ctx);
    ctx.collectChild(image);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    Avatar _obj = ((Avatar) dbObj);
    ctx.cloneChild(image, (v) -> _obj.setImage(v));
    _obj.setCreateFrom(createFrom);
  }

  public Avatar cloneInstance(Avatar cloneObj) {
    if (cloneObj == null) {
      cloneObj = new Avatar();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setImage(this.getImage().cloneInstance(null));
    cloneObj.setCreateFrom(this.getCreateFrom());
    return cloneObj;
  }

  public Avatar createNewInstance() {
    return new Avatar();
  }

  public boolean needOldObject() {
    return true;
  }

  public void collectCreatableReferences(List<Object> _refs) {
    super.collectCreatableReferences(_refs);
    _refs.add(this.image.getFile());
  }

  @Override
  public boolean _isEntity() {
    return true;
  }

  @Override
  protected void _handleChildChange(int _childIdx) {
    switch (_childIdx) {
      case _IMAGE:
        {
          this.fieldChanged(_childIdx, this.image);
          break;
        }
    }
  }
}

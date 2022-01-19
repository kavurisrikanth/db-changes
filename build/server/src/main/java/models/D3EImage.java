package models;

import d3e.core.CloneContext;
import d3e.core.DFile;
import d3e.core.SchemaConstants;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import org.apache.solr.client.solrj.beans.Field;
import store.DBObject;
import store.DatabaseObject;
import store.ICloneable;

@Embeddable
public class D3EImage extends DBObject implements ICloneable {
  public static final int _SIZE = 0;
  public static final int _WIDTH = 1;
  public static final int _HEIGHT = 2;
  public static final int _FILE = 3;
  @Field private long size = 0l;
  @Field private long width = 0l;
  @Field private long height = 0l;

  @Field
  @ManyToOne(fetch = FetchType.LAZY)
  private DFile file;

  private transient Avatar masterAvatar;

  public D3EImage() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.D3EImage;
  }

  @Override
  public String _type() {
    return "D3EImage";
  }

  @Override
  public int _fieldsCount() {
    return 4;
  }

  public DatabaseObject _masterObject() {
    if (masterAvatar != null) {
      return masterAvatar;
    }
    return null;
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {}

  public void visitChildren(Consumer<DBObject> visitor) {}

  public long getSize() {
    _checkProxy();
    return this.size;
  }

  public void setSize(long size) {
    _checkProxy();
    if (Objects.equals(this.size, size)) {
      return;
    }
    fieldChanged(_SIZE, this.size);
    this.size = size;
  }

  public long getWidth() {
    _checkProxy();
    return this.width;
  }

  public void setWidth(long width) {
    _checkProxy();
    if (Objects.equals(this.width, width)) {
      return;
    }
    fieldChanged(_WIDTH, this.width);
    this.width = width;
  }

  public long getHeight() {
    _checkProxy();
    return this.height;
  }

  public void setHeight(long height) {
    _checkProxy();
    if (Objects.equals(this.height, height)) {
      return;
    }
    fieldChanged(_HEIGHT, this.height);
    this.height = height;
  }

  public DFile getFile() {
    _checkProxy();
    return this.file;
  }

  public void setFile(DFile file) {
    _checkProxy();
    if (Objects.equals(this.file, file)) {
      return;
    }
    fieldChanged(_FILE, this.file);
    this.file = file;
  }

  public Avatar getMasterAvatar() {
    return this.masterAvatar;
  }

  public void setMasterAvatar(Avatar masterAvatar) {
    this.masterAvatar = masterAvatar;
  }

  public String displayName() {
    return "D3EImage";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof D3EImage && super.equals(a);
  }

  public D3EImage deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    D3EImage _obj = ((D3EImage) dbObj);
    _obj.setSize(size);
    _obj.setWidth(width);
    _obj.setHeight(height);
    _obj.setFile(file);
  }

  public D3EImage cloneInstance(D3EImage cloneObj) {
    if (cloneObj == null) {
      cloneObj = new D3EImage();
    }
    cloneObj.setSize(this.getSize());
    cloneObj.setWidth(this.getWidth());
    cloneObj.setHeight(this.getHeight());
    cloneObj.setFile(this.getFile());
    return cloneObj;
  }

  public void clear() {
    this.size = 0l;
    this.width = 0l;
    this.height = 0l;
    this.file = null;
  }

  public boolean emptyEmbeddedModel() {
    if (this.size != 0l) {
      return false;
    }
    if (this.width != 0l) {
      return false;
    }
    if (this.height != 0l) {
      return false;
    }
    if (this.file != null) {
      return false;
    }
    return true;
  }

  public D3EImage createNewInstance() {
    return new D3EImage();
  }
}

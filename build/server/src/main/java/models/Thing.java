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
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.ChildDocument;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.D3EPersistanceList;
import store.DBObject;
import store.Database;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "Thing")
@Entity
public class Thing extends CreatableObject {
  public static final int _CHILD = 0;
  public static final int _CHILDCOLL = 1;

  @Field
  @ChildDocument
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private ChildModel child;

  @Field
  @ChildDocument
  @OrderColumn
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChildModel> childColl = new D3EPersistanceList<>(this, _CHILDCOLL);

  public Thing() {
    super();
  }

  @Override
  public int _typeIdx() {
    return SchemaConstants.Thing;
  }

  @Override
  public String _type() {
    return "Thing";
  }

  @Override
  public int _fieldsCount() {
    return 2;
  }

  public void addToChildColl(ChildModel val, long index) {
    collFieldChanged(_CHILDCOLL, this.childColl);
    val.setMasterThing(this);
    val._setChildIdx(_CHILDCOLL);
    if (index == -1) {
      this.childColl.add(val);
    } else {
      this.childColl.add(((int) index), val);
    }
  }

  public void removeFromChildColl(ChildModel val) {
    collFieldChanged(_CHILDCOLL, this.childColl);
    val._clearChildIdx();
    this.childColl.remove(val);
  }

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
    if (child != null) {
      visitor.accept(child);
      child.setMasterThing(this);
      child.updateMasters(visitor);
    }
    for (ChildModel obj : this.getChildColl()) {
      visitor.accept(obj);
      obj.setMasterThing(this);
      obj._setChildIdx(_CHILDCOLL);
      obj.updateMasters(visitor);
    }
  }

  public void visitChildren(Consumer<DBObject> visitor) {
    super.visitChildren(visitor);
    if (child != null) {
      visitor.accept(child);
      child.visitChildren(visitor);
    }
    for (ChildModel obj : this.getChildColl()) {
      visitor.accept(obj);
      obj.visitChildren(visitor);
    }
  }

  public ChildModel getChild() {
    _checkProxy();
    return this.child;
  }

  public void setChild(ChildModel child) {
    _checkProxy();
    if (Objects.equals(this.child, child)) {
      return;
    }
    fieldChanged(_CHILD, this.child);
    this.child = child;
    if (this.child != null) {
      this.child.setMasterThing(this);
      this.child._setChildIdx(_CHILD);
    }
  }

  public List<ChildModel> getChildColl() {
    return this.childColl;
  }

  public void setChildColl(List<ChildModel> childColl) {
    if (Objects.equals(this.childColl, childColl)) {
      return;
    }
    collFieldChanged(_CHILDCOLL, this.childColl);
    this.childColl.clear();
    this.childColl.addAll(childColl);
    this.childColl.forEach(
        (one) -> {
          one.setMasterThing(this);
          one._setChildIdx(_CHILDCOLL);
        });
  }

  public String displayName() {
    return "Thing";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof Thing && super.equals(a);
  }

  public Thing deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void collectChildValues(CloneContext ctx) {
    super.collectChildValues(ctx);
    ctx.collectChild(child);
    ctx.collectChilds(childColl);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    Thing _obj = ((Thing) dbObj);
    ctx.cloneChild(child, (v) -> _obj.setChild(v));
    ctx.cloneChildList(childColl, (v) -> _obj.setChildColl(v));
  }

  public Thing cloneInstance(Thing cloneObj) {
    if (cloneObj == null) {
      cloneObj = new Thing();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setChild(this.getChild() == null ? null : this.getChild().cloneInstance(null));
    cloneObj.setChildColl(
        this.getChildColl().stream()
            .map((ChildModel colObj) -> colObj.cloneInstance(null))
            .collect(Collectors.toList()));
    return cloneObj;
  }

  public Thing createNewInstance() {
    return new Thing();
  }

  public void collectCreatableReferences(List<Object> _refs) {
    super.collectCreatableReferences(_refs);
    Database.collectCreatableReferences(_refs, this.child);
    Database.collectCollctionCreatableReferences(_refs, this.childColl);
  }

  @Override
  public boolean _isEntity() {
    return true;
  }

  @Override
  protected void _handleChildChange(int _childIdx) {
    switch (_childIdx) {
      case _CHILD:
        {
          this.fieldChanged(_childIdx, this.child);
          break;
        }
      case _CHILDCOLL:
        {
          this.collFieldChanged(_childIdx, this.childColl);
          break;
        }
    }
  }
}

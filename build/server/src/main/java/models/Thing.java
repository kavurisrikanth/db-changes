package models;

import d3e.core.CloneContext;
import d3e.core.SchemaConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
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
  public static final int _MSG = 0;
  public static final int _NUMS = 1;
  public static final int _CHILD = 2;
  public static final int _CHILDCOLL = 3;
  @Field private String msg;
  @Field @ElementCollection private List<Long> nums = new D3EPersistanceList<>(this, _NUMS);

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
    return 4;
  }

  public void addToNums(long val, long index) {
//    addedToCollField(_NUMS, this.nums, val, index);
    if (index == -1) {
      this.nums.add(val);
    } else {
      this.nums.add(((int) index), val);
    }
  }

  public void removeFromNums(long val) {
    removedFromCollField(_NUMS, this.nums, val);
    this.nums.remove(val);
  }

  public void addToChildColl(ChildModel val, long index) {
    addedToCollField(_CHILDCOLL, this.childColl, val, index);
    val.setMasterThing(this);
    val._setChildIdx(_CHILDCOLL);
    if (index == -1) {
      this.childColl.add(val);
    } else {
      this.childColl.add(((int) index), val);
    }
  }

  public void removeFromChildColl(ChildModel val) {
    removedFromCollField(_CHILDCOLL, this.childColl, val);
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

  public String getMsg() {
    _checkProxy();
    return this.msg;
  }

  public void setMsg(String msg) {
    _checkProxy();
    if (Objects.equals(this.msg, msg)) {
      return;
    }
    fieldChanged(_MSG, this.msg, msg);
    this.msg = msg;
  }

  public List<Long> getNums() {
    return this.nums;
  }

  public void setNums(List<Long> nums) {
    if (Objects.equals(this.nums, nums)) {
      return;
    }
//    collFieldChanged(_NUMS, this.nums, nums);
    
//    this.nums.clear();
//    this.nums.addAll(nums);
    
    ((D3EPersistanceList<Long>) this.nums).setAll(nums);
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
    fieldChanged(_CHILD, this.child, child);
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
    collFieldChanged(_CHILDCOLL, this.childColl, childColl);
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
    _obj.setMsg(msg);
    _obj.setNums(nums);
    ctx.cloneChild(child, (v) -> _obj.setChild(v));
    ctx.cloneChildList(childColl, (v) -> _obj.setChildColl(v));
  }

  public Thing cloneInstance(Thing cloneObj) {
    if (cloneObj == null) {
      cloneObj = new Thing();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setMsg(this.getMsg());
    cloneObj.setNums(new ArrayList<>(this.getNums()));
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

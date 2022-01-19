package helpers;

import classes.ThingUtil;
import models.ChildModel;
import models.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ThingRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("Thing")
public class ThingEntityHelper<T extends Thing> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ThingRepository thingRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public Thing newInstance() {
    return new Thing();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long childIndex = 0l;
    if (entity.getChild() != null) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(entity.getChild());
      if (onCreate) {
        helper.validateOnCreate(entity.getChild(), validationContext.child("child", null, 0l));
      } else {
        helper.validateOnUpdate(entity.getChild(), validationContext.child("child", null, 0l));
      }
    }
    long childCollIndex = 0l;
    for (ChildModel obj : entity.getChildColl()) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(obj);
      if (onCreate) {
        helper.validateOnCreate(obj, validationContext.child("childColl", null, childCollIndex++));
      } else {
        helper.validateOnUpdate(obj, validationContext.child("childColl", null, childCollIndex++));
      }
    }
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, true, false);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, false, true);
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) thingRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {
    if (entity.getChild() != null) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(entity.getChild());
      helper.setDefaults(entity.getChild());
    }
    for (ChildModel obj : entity.getChildColl()) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.setDefaults(obj);
    }
  }

  @Override
  public void compute(T entity) {
    if (entity.getChild() != null) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(entity.getChild());
      helper.compute(entity.getChild());
    }
    for (ChildModel obj : entity.getChildColl()) {
      ChildModelEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.compute(obj);
    }
  }

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performAction_Go(Thing entity) {
    {
      /*
      Your code here.
      */
      ThingUtil.handleThing(entity);
    }
  }

  public void performOnUpdateActions(Thing entity) {
    performAction_Go(entity);
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    performOnUpdateActions(entity);
    return true;
  }

  public T getOld(long id) {
    return ((T) getById(id).clone());
  }
}

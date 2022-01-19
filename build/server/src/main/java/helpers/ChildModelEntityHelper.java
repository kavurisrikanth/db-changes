package helpers;

import models.ChildModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ChildModelRepository;
import repository.jpa.ThingRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("ChildModel")
public class ChildModelEntityHelper<T extends ChildModel> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ChildModelRepository childModelRepository;
  @Autowired private ThingRepository thingRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ChildModel newInstance() {
    return new ChildModel();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {}

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
    return id == 0l ? null : ((T) childModelRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {}

  @Override
  public void compute(T entity) {}

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    return true;
  }
}

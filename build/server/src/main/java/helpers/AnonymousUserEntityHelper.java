package helpers;

import models.AnonymousUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import store.EntityValidationContext;

@Service("AnonymousUser")
public class AnonymousUserEntityHelper<T extends AnonymousUser> extends UserEntityHelper<T> {
  @Autowired private AnonymousUserRepository anonymousUserRepository;

  public AnonymousUser newInstance() {
    return new AnonymousUser();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    super.validateInternal(entity, validationContext, onCreate, onUpdate);
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    super.validateOnCreate(entity, validationContext);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    super.validateOnUpdate(entity, validationContext);
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) anonymousUserRepository.getOne(id));
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

  public T getOld(long id) {
    return ((T) getById(id).clone());
  }
}

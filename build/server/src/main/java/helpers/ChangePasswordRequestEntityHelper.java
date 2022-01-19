package helpers;

import d3e.core.Services;
import models.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("ChangePasswordRequest")
public class ChangePasswordRequestEntityHelper<T extends ChangePasswordRequest>
    implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ChangePasswordRequest newInstance() {
    return new ChangePasswordRequest();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldNewPassword(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getNewPassword();
    if (it == null) {
      validationContext.addFieldError("newPassword", "newPassword is required.");
      return;
    }
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldNewPassword(entity, validationContext, onCreate, onUpdate);
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
    return null;
  }

  @Override
  public void setDefaults(T entity) {}

  @Override
  public void compute(T entity) {}

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performAction_SetNewPassword(ChangePasswordRequest entity) {
    {
      Services.get().getChangePasswordService().change(entity);
    }
  }

  public void performOnCreateActions(ChangePasswordRequest entity) {
    performAction_SetNewPassword(entity);
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    performOnCreateActions(entity);
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

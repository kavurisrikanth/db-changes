package helpers;

import d3e.core.OTPService;
import models.OneTimePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.OneTimePasswordRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("OneTimePassword")
public class OneTimePasswordEntityHelper<T extends OneTimePassword> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public OneTimePassword newInstance() {
    return new OneTimePassword();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldInput(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getInput();
    if (it == null) {
      validationContext.addFieldError("input", "input is required.");
      return;
    }
  }

  public void validateFieldInputType(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getInputType();
    if (it == null) {
      validationContext.addFieldError("inputType", "inputType is required.");
      return;
    }
  }

  public void validateFieldUserType(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getUserType();
    if (it == null) {
      validationContext.addFieldError("userType", "userType is required.");
      return;
    }
  }

  public void validateFieldSuccess(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    boolean it = entity.isSuccess();
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldInput(entity, validationContext, onCreate, onUpdate);
    validateFieldInputType(entity, validationContext, onCreate, onUpdate);
    validateFieldUserType(entity, validationContext, onCreate, onUpdate);
    validateFieldSuccess(entity, validationContext, onCreate, onUpdate);
    validateFieldTokenUnique(entity, validationContext);
    isErrorMsgExists(entity);
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, true, false);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, false, true);
  }

  public boolean isErrorMsgExists(T entity) {
    try {
      if (!entity.isSuccess()) {
        return true;
      } else {
        entity.setErrorMsg("");
        return false;
      }
    } catch (RuntimeException e) {
      return false;
    }
  }

  public void validateFieldTokenUnique(T entity, EntityValidationContext validationContext) {
    if (!(oneTimePasswordRepository.checkTokenUnique(entity.getId(), entity.getToken()))) {
      validationContext.addFieldError("token", "OneTimePassword with given token already exists");
    }
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) oneTimePasswordRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {}

  @Override
  public void compute(T entity) {}

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performAction_Create(OneTimePassword entity) {
    {
      OTPService.get().create(entity);
    }
  }

  public void performOnCreateActions(OneTimePassword entity) {
    performAction_Create(entity);
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

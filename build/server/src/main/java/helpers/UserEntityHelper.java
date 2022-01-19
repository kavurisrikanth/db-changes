package helpers;

import models.OneTimePassword;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.UserRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("User")
public class UserEntityHelper<T extends User> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private UserRepository userRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
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
    return id == 0l ? null : ((T) userRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {}

  @Override
  public void compute(T entity) {}

  private void deleteUserInOneTimePassword(T entity, EntityValidationContext deletionContext) {
    if (EntityHelper.haveUnDeleted(this.oneTimePasswordRepository.getByUser(entity))) {
      deletionContext.addEntityError(
          "This User cannot be deleted as it is being referred to by OneTimePassword.");
    }
  }

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void validateOnDelete(T entity, EntityValidationContext deletionContext) {
    this.deleteUserInOneTimePassword(entity, deletionContext);
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

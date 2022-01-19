package helpers;

import models.D3EImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AvatarRepository;
import repository.jpa.DFileRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("D3EImage")
public class D3EImageEntityHelper<T extends D3EImage> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private DFileRepository dFileRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public D3EImage newInstance() {
    return new D3EImage();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldSize(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getSize();
  }

  public void validateFieldWidth(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getWidth();
  }

  public void validateFieldHeight(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getHeight();
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldSize(entity, validationContext, onCreate, onUpdate);
    validateFieldWidth(entity, validationContext, onCreate, onUpdate);
    validateFieldHeight(entity, validationContext, onCreate, onUpdate);
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

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    return true;
  }
}

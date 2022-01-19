package helpers;

import d3e.core.D3EResourceHandler;
import d3e.core.ListExt;
import models.Avatar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AvatarRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("Avatar")
public class AvatarEntityHelper<T extends Avatar> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private D3EResourceHandler resourceHandler;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public Avatar newInstance() {
    return new Avatar();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long imageIndex = 0l;
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      if (onCreate) {
        helper.validateOnCreate(entity.getImage(), validationContext.child("image", null, 0l));
      } else {
        helper.validateOnUpdate(entity.getImage(), validationContext.child("image", null, 0l));
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
    return id == 0l ? null : ((T) avatarRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      helper.setDefaults(entity.getImage());
    }
  }

  @Override
  public void compute(T entity) {
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      helper.compute(entity.getImage());
    }
  }

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performImageAction(T entity) {
    if (entity.getImage() != null && entity.getImage().getFile() != null) {
      entity
          .getImage()
          .setFile(resourceHandler.saveImage(entity.getImage().getFile(), ListExt.List()));
    }
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    performImageAction(entity);
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    performImageAction(entity);
    return true;
  }
}

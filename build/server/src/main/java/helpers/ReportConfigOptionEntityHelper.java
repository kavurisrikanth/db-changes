package helpers;

import models.ReportConfigOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ReportConfigOptionRepository;
import repository.jpa.ReportConfigRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("ReportConfigOption")
public class ReportConfigOptionEntityHelper<T extends ReportConfigOption>
    implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ReportConfigOptionRepository reportConfigOptionRepository;
  @Autowired private ReportConfigRepository reportConfigRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ReportConfigOption newInstance() {
    return new ReportConfigOption();
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldIdentity(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getIdentity();
    if (it == null) {
      validationContext.addFieldError("identity", "identity is required.");
      return;
    }
  }

  public void validateFieldValue(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getValue();
    if (it == null) {
      validationContext.addFieldError("value", "value is required.");
      return;
    }
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldIdentity(entity, validationContext, onCreate, onUpdate);
    validateFieldValue(entity, validationContext, onCreate, onUpdate);
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
    return id == 0l ? null : ((T) reportConfigOptionRepository.getOne(id));
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

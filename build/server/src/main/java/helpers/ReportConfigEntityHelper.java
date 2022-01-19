package helpers;

import models.ReportConfig;
import models.ReportConfigOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ReportConfigRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;

@Service("ReportConfig")
public class ReportConfigEntityHelper<T extends ReportConfig> implements EntityHelper<T> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ReportConfigRepository reportConfigRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ReportConfig newInstance() {
    return new ReportConfig();
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

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldIdentity(entity, validationContext, onCreate, onUpdate);
    long valuesIndex = 0l;
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      if (onCreate) {
        helper.validateOnCreate(
            obj, validationContext.child("values", obj.getIdentity(), valuesIndex++));
      } else {
        helper.validateOnUpdate(
            obj, validationContext.child("values", obj.getIdentity(), valuesIndex++));
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
    return id == 0l ? null : ((T) reportConfigRepository.getOne(id));
  }

  @Override
  public void setDefaults(T entity) {
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.setDefaults(obj);
    }
  }

  @Override
  public void compute(T entity) {
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.compute(obj);
    }
  }

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

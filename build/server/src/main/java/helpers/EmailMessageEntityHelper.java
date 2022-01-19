package helpers;

import d3e.core.D3EResourceHandler;
import java.util.stream.Collectors;
import models.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.DFileRepository;
import store.EntityValidationContext;

@Service("EmailMessage")
public class EmailMessageEntityHelper<T extends EmailMessage> extends D3EMessageEntityHelper<T> {
  @Autowired private DFileRepository dFileRepository;
  @Autowired private D3EResourceHandler resourceHandler;

  public EmailMessage newInstance() {
    return new EmailMessage();
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
    return null;
  }

  @Override
  public void setDefaults(T entity) {
    this.setDefaultCreatedOn(entity);
  }

  @Override
  public void compute(T entity) {}

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performFileAction(T entity) {
    entity.setInlineAttachments(
        entity.getInlineAttachments().stream()
            .filter((one) -> one != null)
            .map((one) -> resourceHandler.save(one))
            .collect(Collectors.toList()));
    entity.setAttachments(
        entity.getAttachments().stream()
            .filter((one) -> one != null)
            .map((one) -> resourceHandler.save(one))
            .collect(Collectors.toList()));
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    performFileAction(entity);
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    performFileAction(entity);
    return true;
  }

  public T getOld(long id) {
    return ((T) getById(id).clone());
  }
}

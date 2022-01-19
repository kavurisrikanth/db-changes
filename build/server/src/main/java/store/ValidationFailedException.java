package store;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import classes.MutateResultStatus;

@SuppressWarnings("serial")
public class ValidationFailedException extends RuntimeException {
  private List<String> errors;
  private MutateResultStatus status;
  private boolean statusProvided = false;
  
  public static ValidationFailedException fromValidationContext(EntityValidationContext ctx) {
    // Assumed that ctx has errors
    ValidationFailedException e = new ValidationFailedException(Stream.concat(ctx.getErrors().stream(), ctx.getThrowableErrors().stream()).collect(Collectors.toList()));
    if (ctx.hasServerError()) {
      e.setStatus(MutateResultStatus.ServerError);
      ctx.markServerError(false);
    }
    ctx.showAllExceptions();
    return e;
  }

  public ValidationFailedException(List<String> errors) {
    super(errors.toString());
    this.errors = errors;
  }

  public ValidationFailedException(MutateResultStatus status, List<String> errors) {
    super(errors.toString());
    this.errors = errors;
    this.status = status;
    this.statusProvided = true;
  }

  public List<String> getErrors() {
    return errors;
  }

  public boolean hasStatus() {
    return statusProvided;
  }

  public MutateResultStatus getStatus() {
    return status;
  }
  
  public void setStatus(MutateResultStatus status) {
    this.status = status;
    this.statusProvided = true;
  }
}

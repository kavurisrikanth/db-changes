package store;

import java.util.List;

import d3e.core.ListExt;

public class ValidationContextImpl implements EntityValidationContext {
	private List<Throwable> exceptions = ListExt.List();
	private List<String> errors = ListExt.List();
	private List<String> throwableErrors = ListExt.List();
	private boolean serverError = false;
	private EntityMutator mutator;
	
	public ValidationContextImpl(EntityMutator mutator) {
	  	this.mutator = mutator;
  	}

	@Override
	public boolean hasErrors() {
		return ListExt.isNotEmpty(errors) || ListExt.isNotEmpty(throwableErrors);
	}

	@Override
	public List<String> getErrors() {
		return errors;
	}

	@Override
	public List<String> getThrowableErrors() {
		return throwableErrors;
	}

	@Override
	public void addFieldError(String field, String error) {
		errors.add(field + ": " + error);
	}

	@Override
	public void addEntityError(String error) {
		errors.add(error);
	}

	@Override
	public void addThrowableError(Throwable t, String error) {
	  	this.exceptions.add(t);
	  	this.addEntityError(error);
	}

	@Override
	public EntityValidationContext child(String field, String identity, long index) {
		return this;
	}

	@Override
	public void markServerError(boolean value) {
		this.serverError = value;
	}
	
	@Override
	public boolean hasServerError() {
		return this.serverError;
	}

	@Override
	public void showAllExceptions() {
		this.exceptions.forEach(one -> one.printStackTrace(System.err));
		// TODO: Do we clear exceptions?
	  	this.exceptions.clear();
	}

	@Override
	public boolean isInDelete(Object obj) {
		if (this.mutator == null) {
			return false;
		}
		return this.mutator.isInDelete(obj);
	}
}

package store;

import java.util.List;

import d3e.core.ListExt;

public interface EntityValidationContext {

	boolean hasErrors();

	// TODO: Change return type as per further requirement
	List<String> getErrors();

	default List<String> getThrowableErrors() {
		return ListExt.List();
	}

	void addFieldError(String field, String error);

	void addEntityError(String error);

	default void addThrowableError(Throwable t, String error) {}

	EntityValidationContext child(String field, String identity, long index);
	
	default void markServerError(boolean value) {}

	default boolean hasServerError() {
	  	return false;
	}

	default void showAllExceptions() {}

	default boolean isInDelete(Object obj) {
	  	return false;
	}
}

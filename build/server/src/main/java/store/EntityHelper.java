package store;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import rest.IDocumentReader;

public interface EntityHelper<T extends Object> {

	void setDefaults(T entity);

	void compute(T entity);

	void validateOnCreate(T entity, EntityValidationContext context);

	void validateOnUpdate(T entity, EntityValidationContext context);

	Boolean onCreate(T obj, boolean internal);

	Boolean onUpdate(T obj, boolean internal);

	Boolean onDelete(T obj, boolean internal, EntityValidationContext deletionContext);

	T clone(T entity);

	default void toJson(T entity, IDocumentReader r) {}

	T getById(long input);
	
	default void validateOnDelete(T entity, EntityValidationContext deletionContext) {}
    
    default Object newInstance() {
		return null;
	}

	default T getOld(long id) {
		return null;
	}

	default boolean union(Supplier<Boolean>... providers) {
		for (Supplier<Boolean> p : providers) {
			if (p.get()) {
				return true;
			}
		}
		return false;
	}

	default boolean intersect(Supplier<Boolean>... providers) {
		for (Supplier<Boolean> p : providers) {
			if (!p.get()) {
				return false;
			}
		}
		return true;
	}

	default boolean exclude(boolean from, boolean what) {
		if (what) {
			return false;
		}
		return from;
	}
	
	public static <E extends DatabaseObject> boolean haveUnDeleted(List<E> list) {
		return list.stream().anyMatch(x -> !x.isDeleted());
	}
}

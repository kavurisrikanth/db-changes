package gqltosql.schema;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DRefField<T, R> extends DField<T, R> {

	private Function<T, R> getter;
	private BiConsumer<T, R> setter;
	private boolean child;

	public DRefField(DModel<T> decl, int index, String name, String column, boolean child, DModel<?> ref, Function<T, R> getter,
			BiConsumer<T, R> setter) {
		super(decl, index, name, column);
		this.child = child;
		setRef(ref);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public boolean isChild() {
		return child;
	}

	@Override
	public FieldType getType() {
		return FieldType.Reference;
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return null;
	}

	@Override
	public Object getValue(T _this) {
		return getter.apply(_this);
	}

	@Override
	public Object fetchValue(T _this, IDataFetcher fetcher) {
		return fetcher.onReferenceValue(getValue(_this));
	}

	@Override
	public void setValue(T _this, R value) {
		setter.accept(_this, value);
	}
}

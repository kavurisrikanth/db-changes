package gqltosql.schema;

import java.util.List;
import java.util.function.Function;

public class DInverseCollField<T, R> extends DField<T, List<R>> {

	private Function<T, List<R>> getter;

	public DInverseCollField(DModel<T> decl, int index, String name, String column, DModel<?> ref, Function<T, List<R>> getter) {
		super(decl, index, name, column);
		this.getter = getter;
		setRef(ref);
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return null;
	}

	@Override
	public FieldType getType() {
		return FieldType.InverseCollection;
	}

	@Override
	public List<R> getValue(T _this) {
		return getter.apply(_this);
	}

	@Override
	public Object fetchValue(T _this, IDataFetcher fetcher) {
		return fetcher.onInverseValue(getValue(_this));
	}

	@Override
	public void setValue(T _this, List<R> value) {
		throw new RuntimeException("Can not set value to inverse field");
	}
}

package gqltosql.schema;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DFlatField<T, R> extends DField<T, List<R>> {

	private Function<T, List<R>> getter;

	private String[] flatPaths;

	public DFlatField(DModel<T> decl, int index, String name, String column, String collTable, DModel<?> ref,
			Function<T, List<R>> getter, String... flatPaths) {
		super(decl, index, name, column);
		this.flatPaths = flatPaths;
		setCollTable(collTable);
		setRef(ref);
		this.getter = getter;
	}

	public String[] getFlatPaths() {
		return flatPaths;
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return null;
	}

	@Override
	public FieldType getType() {
		return FieldType.ReferenceCollection;
	}

	@Override
	public List<R> getValue(T _this) {
		return getter.apply(_this);
	}

	@Override
	public Object fetchValue(T _this, IDataFetcher fetcher) {
		return fetcher.onFlatValue(getValue(_this));
	}

	@Override
	public void setValue(T _this, List<R> value) {
		throw new RuntimeException("Can not set value to flat field");
	}
}

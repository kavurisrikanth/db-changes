package gqltosql.schema;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DPrimCollField<T, R> extends DField<T, List<R>> {

	private Function<T, List<R>> getter;

	private BiConsumer<T, List<R>> setter;

	private FieldPrimitiveType primType;

	private int enumType;

	public DPrimCollField(DModel<T> decl, int index, String name, String column, String collTable, FieldPrimitiveType primType,
			Function<T, List<R>> getter, BiConsumer<T, List<R>> setter) {
		super(decl, index, name, column);
		setCollTable(collTable);
		this.primType = primType;
		this.setter = setter;
		this.getter = getter;
	}

	@Override
	public int getEnumType() {
		return enumType;
	}

	public void setEnumType(int enumType) {
		this.enumType = enumType;
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return primType;
	}

	@Override
	public FieldType getType() {
		return FieldType.PrimitiveCollection;
	}

	@Override
	public List<R> getValue(T _this) {
		return getter.apply(_this);
	}

	@Override
	public Object fetchValue(T _this, IDataFetcher fetcher) {
		return fetcher.onPrimitiveList(getValue(_this), this);
	}

	@Override
	public void setValue(T _this, List<R> value) {
		setter.accept(_this, value);
	}
}

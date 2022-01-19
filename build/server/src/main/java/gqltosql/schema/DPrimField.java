package gqltosql.schema;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DPrimField<T, R> extends DField<T, R> {

	private Function<T, R> getter;

	private BiConsumer<T, R> setter;

	private FieldPrimitiveType primType;

	private int enumType;
	
	private Function<T, R> def;

	public DPrimField(DModel<T> decl, int index, String name, String column, FieldPrimitiveType primType, Function<T, R> getter,
	    BiConsumer<T, R> setter) {
	  this(decl, index, name, column, primType, getter, setter, null);
	}
	
	public DPrimField(DModel<T> decl, int index, String name, String column, FieldPrimitiveType primType, Function<T, R> getter,
			BiConsumer<T, R> setter, Function<T, R> def) {
		super(decl, index, name, column);
		this.primType = primType;
		this.getter = getter;
		this.setter = setter;
		this.def = def;
	}

	public void setEnumType(int enumType) {
		this.enumType = enumType;
	}

	public int getEnumType() {
		return enumType;
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return primType;
	}

	@Override
	public FieldType getType() {
		return FieldType.Primitive;
	}

	@Override
	public R getValue(T _this) {
		return getter.apply(_this);
	}

	@Override
	public Object fetchValue(T _this, IDataFetcher fetcher) {
		return fetcher.onPrimitiveValue(getValue(_this), this);
	}

	@Override
	public void setValue(T _this, R value) {
		setter.accept(_this, value);
	}
	
	public R getDefaultValue(T _this) {
		if (this.def == null) {
			return null;
		}
		return this.def.apply(_this);
	}
}

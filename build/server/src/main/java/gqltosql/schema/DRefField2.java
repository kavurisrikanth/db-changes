package gqltosql.schema;

import java.util.function.BiConsumer;
import java.util.function.Function;

import lists.TypeAndId;

public class DRefField2<T, R> extends DRefField<T, R> {

	private Function<T, TypeAndId> refGetter;

	public DRefField2(DModel<T> decl, int index, String name, String column, boolean child, DModel<?> ref,
			Function<T, R> getter, BiConsumer<T, R> setter, Function<T, TypeAndId> refGetter) {
		super(decl, index, name, column, child, ref, getter, setter);
		this.refGetter = refGetter;
	}

	@Override
	public Object getValue(T _this) {
		Object val = refGetter.apply(_this);
		return val != null ? val : super.getValue(_this);
	}
}

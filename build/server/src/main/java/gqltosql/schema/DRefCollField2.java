package gqltosql.schema;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import lists.TypeAndId;

public class DRefCollField2<T, R> extends DRefCollField<T, R> {

	private Function<T, List<TypeAndId>> refGetter;

	public DRefCollField2(DModel<T> decl, int index, String name, String column, boolean child, String collTable,
			DModel<?> ref, Function<T, List<R>> getter, BiConsumer<T, List<R>> setter,
			Function<T, List<TypeAndId>> refGetter) {
		super(decl, index, name, column, child, collTable, ref, getter, setter);
		this.refGetter = refGetter;
	}

	@Override
	public List getValue(T _this) {
		List val = refGetter.apply(_this);
		return val != null ? val : super.getValue(_this);
	}
}

package gqltosql.schema;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DDocCollField<T, R> extends DRefCollField<T, R> {

	private Function<T, List<String>> docGetter;
	private BiConsumer<T, List<String>> docSetter;

	public DDocCollField(DModel<T> decl, int index, String name, String column, boolean child, String collTable,
			DModel<?> ref, Function<T, List<R>> getter, BiConsumer<T, List<R>> setter,
			Function<T, List<String>> docGetter, BiConsumer<T, List<String>> docSetter) {
		super(decl, index, name, column, child, collTable, ref, getter, setter);
		this.docGetter = docGetter;
		this.docSetter = docSetter;
	}

	public void setDocValue(T _this, List<String> value) {
		docSetter.accept(_this, value);
	}

	public List<String> getDocValue(T _this) {
		return docGetter.apply(_this);
	}
}

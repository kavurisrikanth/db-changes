package gqltosql.schema;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DDocField<T, R> extends DRefField<T, R> {

	private Function<T, String> docGetter;
	private BiConsumer<T, String> docSetter;

	public DDocField(DModel<T> decl, int index, String name, String column, boolean child, DModel<?> ref,
			Function<T, R> getter, BiConsumer<T, R> setter, Function<T, String> docGetter,
			BiConsumer<T, String> docSetter) {
		super(decl, index, name, column, child, ref, getter, setter);
		this.docGetter = docGetter;
		this.docSetter = docSetter;
	}

	public void setDocValue(T _this, String value) {
		docSetter.accept(_this, value);
	}

	public String getDocValue(T _this) {
		return docGetter.apply(_this);
	}
}

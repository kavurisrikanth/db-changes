package gqltosql.schema;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DEmbField<T, R> extends DRefField<T, R> {

	private String prefix;

	public DEmbField(DModel<T> decl, int index, String name, String column, String prefix, DModel<?> ref,
			Function<T, R> getter, BiConsumer<T, R> setter) {
		super(decl, index, name, column, true, ref, getter, setter);
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}
}

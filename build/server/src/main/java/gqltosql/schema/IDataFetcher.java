package gqltosql.schema;

import java.util.List;

public interface IDataFetcher {

	Object onPrimitiveValue(Object value, DField df);

	Object onReferenceValue(Object value);

	Object onEmbeddedValue(Object value);

	Object onPrimitiveList(List<?> value, DField df);

	Object onReferenceList(List<?> value);

	Object onFlatValue(List<?> value);

	Object onInverseValue(List<?> value);
}

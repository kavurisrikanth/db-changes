package rest.ws;

import gqltosql.schema.DField;
import gqltosql.schema.FieldPrimitiveType;
import gqltosql.schema.FieldType;
import gqltosql.schema.IDataFetcher;

public class UnknownField extends DField<Object, Object> {

	public UnknownField(String name) {
		super(null, 0, name, name);
	}

	@Override
	public FieldType getType() {
		return null;
	}

	@Override
	public FieldPrimitiveType getPrimitiveType() {
		return null;
	}

	@Override
	public Object getValue(Object _this) {
		return null;
	}

	@Override
	public Object fetchValue(Object _this, IDataFetcher fetcher) {
		return null;
	}

	@Override
	public void setValue(Object _this, Object value) {
		
	}

}
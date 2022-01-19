package gqltosql2;

import java.util.List;
import java.util.Map;

import gqltosql.schema.IModelSchema;
import store.IEntityManager;

public class SqlColumn implements ISqlColumn {

	private String column;
	private String field;

	public SqlColumn(String column, String field) {
		this.column = column;
		this.field = field;
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + '.' + column, field);
	}

	@Override
	public String getFieldName() {
		return field;
	}

	@Override
	public String toString() {
		return field;
	}

	@Override
	public SqlAstNode getSubQuery() {
		return null;
	}

	@Override
	public void extractDeepFields(IEntityManager em, IModelSchema schema, int type, List<OutObject> rows)
			throws Exception {
	}

	@Override
	public void updateSubField(Map<Long, OutObject> parents, List<OutObject> all) throws Exception {
	}
}

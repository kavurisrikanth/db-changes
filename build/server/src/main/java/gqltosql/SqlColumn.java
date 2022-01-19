package gqltosql;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.json.JSONArray;

import gqltosql.schema.IModelSchema;

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
	public void extractDeepFields(EntityManager em, IModelSchema schema, String type, List<SqlRow> rows)
			throws Exception {
	}

	@Override
	public void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception {
	}
}

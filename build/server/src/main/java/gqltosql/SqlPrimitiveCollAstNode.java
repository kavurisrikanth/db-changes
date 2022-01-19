package gqltosql;

import gqltosql.schema.IModelSchema;

public class SqlPrimitiveCollAstNode extends SqlAstNode {
	private String collTable;
	private String idColumn;
	private String column;
	private String field;

	public SqlPrimitiveCollAstNode(IModelSchema schema, String path, String collTable, String idColumn, String column,
			String field) {
		super(schema, path, null, null, false);
		this.collTable = collTable;
		this.idColumn = idColumn;
		this.column = column;
		this.field = field;
	}

	@Override
	public SqlQueryContext createCtx() {
		SqlQueryContext ctx = new SqlQueryContext(this, -1);
		String from = ctx.getFrom();
		ctx.getQuery().setFrom(collTable, from);
		ctx.addSelection(from + "." + idColumn, "_parent");
		ctx.getQuery().addWhere(from + "." + idColumn + " in ?1");
		return ctx;
	}

	@Override
	public void selectColumns(SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + "." + column, field);
	}
}

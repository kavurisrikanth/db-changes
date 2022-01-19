package gqltosql;

import gqltosql.schema.IModelSchema;

public class SqlCollAstNode extends SqlAstNode {

	private String collTable;
	private String idColumn;
	private String column;

	public SqlCollAstNode(IModelSchema schema, String path, String type, String table, String collTable,
			String idColumn, String column) {
		super(schema, path, type, table, false);
		this.collTable = collTable;
		this.idColumn = idColumn;
		this.column = column;
	}

	@Override
	public SqlQueryContext createCtx() {
		SqlQueryContext ctx = new SqlQueryContext(this, 1);
		String from = ctx.nextAlias();
		ctx.getQuery().setFrom(collTable, from);
		ctx.addSelection(from + "." + idColumn, "_parent");
		ctx.getQuery().addWhere(from + "." + idColumn + " in ?1");
		SqlQueryContext sub = ctx.subType(getType());
		String join = sub.getFrom();
		ctx.addJoin(getTableName(), join, join + "._id = " + from + "." + column);
		return ctx;
	}
}

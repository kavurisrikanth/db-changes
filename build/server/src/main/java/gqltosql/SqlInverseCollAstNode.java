package gqltosql;

import gqltosql.schema.IModelSchema;

public class SqlInverseCollAstNode extends SqlAstNode {

	private String column;

	public SqlInverseCollAstNode(IModelSchema schema, String path, String type, String table, String column) {
		super(schema, path, type, table, false);
		this.column = column;
	}

	@Override
	public SqlQueryContext createCtx() {
		SqlQueryContext ctx = new SqlQueryContext(this, 1);
		SqlQueryContext sub = ctx.subPrefix(getType());
		String from = sub.getFrom();
		sub.getQuery().setFrom(getTableName(), from);
		sub.addSelection(from + "." + column, "_parent");
		sub.getQuery().addWhere(from + "." + column + " in ?1");
		return ctx;
	}
}

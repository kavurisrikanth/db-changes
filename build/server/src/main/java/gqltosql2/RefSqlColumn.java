package gqltosql2;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONException;

import gqltosql.schema.IModelSchema;
import store.IEntityManager;

public class RefSqlColumn implements ISqlColumn {

	private SqlAstNode sub;
	private String column;
	private String field;

	public RefSqlColumn(SqlAstNode sub, String column, String field) {
		this.sub = sub;
		this.column = column;
		this.field = field;
	}

	@Override
	public String getFieldName() {
		return field;
	}

	public SqlAstNode getSub() {
		return sub;
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
		if (sub.isEmbedded()) {
			QueryReader reader = ctx.getTypeReader().addEmbedded(field);
			SqlQueryContext prefix = ctx.subPrefix(getFieldName());
			SqlQueryContext sc = prefix.subReader(reader);
			sc.addSqlColumns(sub);
		} else {
			QueryReader reader = ctx.addRefSelection(ctx.getFrom() + '.' + column, field);
			SqlQueryContext prefix = ctx.subPrefix(getFieldName());
			String join = prefix.getTableAlias(String.valueOf(sub.getType()));
			ctx.addJoin(sub.getTableName(), join, join + "._id = " + ctx.getFrom() + '.' + column);
			SqlQueryContext sc = prefix.subReader(reader);
			sc.addSqlColumns(sub);
		}
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
	public void updateSubField(Map<Long, OutObject> parents, List<OutObject> all) throws Exception {
	}

	@Override
	public void extractDeepFields(IEntityManager em, IModelSchema schema, int type, List<OutObject> rows)
			throws Exception {
		sub.executeSubQuery(em, (t) -> rows.stream().map(o -> {
			try {
				OutObject row = o.getObject(getFieldName());
				return row != null && row.isOfType(t) ? row : null;
			} catch (JSONException ex) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList()));
	}
}

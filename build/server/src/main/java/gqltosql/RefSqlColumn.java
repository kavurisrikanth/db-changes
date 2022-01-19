package gqltosql;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;

import gqltosql.schema.IModelSchema;

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
			String join = prefix.getTableAlias(sub.getType());
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
	public void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception {
	}

	@Override
	public void extractDeepFields(EntityManager em, IModelSchema schema, String type, List<SqlRow> rows)
			throws Exception {
		sub.executeSubQuery(em, (t) -> rows.stream().map(o -> {
			try {
				SqlRow row = (SqlRow) o.getJSONObject(getFieldName());
				return row.isOfType(t) ? row : null;
			} catch (JSONException ex) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList()));
	}
}

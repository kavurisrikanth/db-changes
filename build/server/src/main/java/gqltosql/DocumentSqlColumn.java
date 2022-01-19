package gqltosql;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;

import gqltosql.schema.DField;
import gqltosql.schema.GraphQLDataFetcher;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import rest.JSONInputContext;

public class DocumentSqlColumn implements ISqlColumn {

	private Field field;
	private DField df;
	private boolean doNotRead;

	public DocumentSqlColumn(Field field, DField<?, ?> df) {
		this.field = field;
		this.df = df;
	}

	@Override
	public String getFieldName() {
		return df.getName();
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + '.' + df.getColumnName(), getFieldName());
	}

	@Override
	public SqlAstNode getSubQuery() {
		return null;
	}

	@Override
	public void extractDeepFields(EntityManager em, IModelSchema schema, String type, List<SqlRow> rows)
			throws Exception {
		if (doNotRead) {
			return;
		}
		rows.forEach(o -> {
			try {
				if (o.has(df.getName())) {
					String doc = o.getString(df.getName());
					Object obj = JSONInputContext.fromJsonString(doc, df.getReference().getType(), o.getLong("id"));
					read(schema, o, obj);
				}
			} catch (JSONException ex) {
			}
		});
	}

	@Override
	public void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception {
	}

	@Override
	public String toString() {
		return getFieldName();
	}

	public void doNotRead() {
		doNotRead = true;
	}

	public void read(IModelSchema schema, SqlRow o, Object obj) throws JSONException {
		GraphQLDataFetcher fetcher = new GraphQLDataFetcher(schema);
		Object res = obj;
		o.put(df.getName(), fetcher.fetchValue(field, res, df.getReference()));
		o.put(df.getName(), res);
	}
}

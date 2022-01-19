package gqltosql2;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import gqltosql.schema.DField;
import gqltosql.schema.IModelSchema;
import rest.JSONInputContext;
import store.IEntityManager;

public class DocumentSqlColumn implements ISqlColumn {

	private Field field;
	private DField df;
	private boolean doNotRead;
	private String column;

	public DocumentSqlColumn(Field field, DField<?, ?> df, String column) {
		this.field = field;
		this.df = df;
		this.column = column;
	}

	@Override
	public String getFieldName() {
		return df.getName();
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
		ctx.addSelection(ctx.getFrom() + '.' + column, getFieldName());
	}

	@Override
	public SqlAstNode getSubQuery() {
		return null;
	}

	@Override
	public void extractDeepFields(IEntityManager em, IModelSchema schema, int type, List<OutObject> rows)
			throws Exception {
		if (doNotRead) {
			return;
		}
		rows.forEach(o -> {
			try {
				if (o.has(df.getName())) {
					String doc = o.getString(df.getName());
					Object obj = JSONInputContext.fromJsonString(doc, df.getReference().getType(), o.getId());
					o.remove(df.getName());
					read(schema, o, obj);
				}
			} catch (JSONException ex) {
			}
		});
	}

	@Override
	public void updateSubField(Map<Long, OutObject> parents, List<OutObject> all) throws Exception {
	}

	@Override
	public String toString() {
		return getFieldName();
	}

	public void doNotRead() {
		doNotRead = true;
	}

	public void read(IModelSchema schema, OutObject o, Object obj) {
		SqlOutObjectFetcher fetcher = new SqlOutObjectFetcher(schema);
		Object res = obj;
		o.add(df.getName(), fetcher.fetchValue(field, res, df.getReference()));
	}
}

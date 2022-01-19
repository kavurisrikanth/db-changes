package gqltosql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;

import gqltosql.schema.DField;
import gqltosql.schema.DFlatField;
import gqltosql.schema.DModel;
import gqltosql.schema.GraphQLDataFetcher;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import rest.JSONInputContext;
import store.DatabaseObject;
import store.EntityHelper;
import store.EntityHelperService;

public class DocumentFlatSqlColumn implements ISqlColumn {

	private Field field;
	private DFlatField df;
	private Map<String, DocumentSqlColumn> selectedColumns = new HashMap();

	public DocumentFlatSqlColumn(Field field, DFlatField<?, ?> df) {
		this.field = field;
		this.df = df;
	}

	@Override
	public String getFieldName() {
		return df.getName();
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
		String[] flatPaths = df.getFlatPaths();
		DModel declType = df.declType();
		for (String fn : flatPaths) {
			DField f = declType.getField(fn);
			if (f != null) {
				ISqlColumn column = table.getColumn(f.getName());
				if (column instanceof DocumentSqlColumn) {
					((DocumentSqlColumn) column).doNotRead();
					selectedColumns.put(column.getFieldName(), (DocumentSqlColumn) column);
				} else {
					ctx.addSelection(ctx.getFrom() + '.' + f.getColumnName(), f.getName());
				}
			}
		}
	}

	@Override
	public SqlAstNode getSubQuery() {
		return null;
	}

	@Override
	public void extractDeepFields(EntityManager em, IModelSchema schema, String type, List<SqlRow> rows)
			throws Exception {
		EntityHelperService instance = EntityHelperService.getInstance();
		EntityHelper<?> helper = instance.get(type);
		DatabaseObject ins = (DatabaseObject) helper.newInstance();
		rows.forEach(o -> {
			try {
				String[] flatPaths = df.getFlatPaths();
				DModel declType = df.declType();
				for (String fn : flatPaths) {
					if (o.has(fn)) {
						String doc = o.getString(fn);
						DatabaseObject obj = JSONInputContext.fromJsonString(doc, df.getReference().getType(),
								o.getLong("id"));
						obj.updateMasters(c -> ins.updateFlat(c));
						ins.updateFlat(obj);
						o.remove(fn);
						if (selectedColumns.containsKey(fn)) {
							DocumentSqlColumn clm = selectedColumns.get(fn);
							clm.read(schema, o, obj);
						}
					}
				}
				GraphQLDataFetcher fetcher = new GraphQLDataFetcher(schema);
				List<Object> res = df.getValue(ins);
				o.put(df.getName(), fetcher.fetchValue(field, res, df.getReference()));
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
}

package gqltosql2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.persistence.Query;

import org.json.JSONException;

import d3e.core.D3ELogger;
import gqltosql.schema.DModel;
import gqltosql.schema.IModelSchema;
import store.IEntityManager;

public class SqlAstNode {

	private Map<Integer, SqlTable> tables = new HashMap<>();
	private int type;
	private String table;
	private String path;
	private boolean embedded;
	private IModelSchema schema;

	public SqlAstNode(IModelSchema schema, String path, int type, String table, boolean embedded) {
		this.schema = schema;
		this.path = path;
		this.type = type;
		this.table = table;
		this.embedded = embedded;
		addTable(schema.getType(type));
	}

	public SqlQueryContext createCtx() {
		SqlQueryContext ctx = new SqlQueryContext(this, -1);
		ctx.getQuery().setFrom(getTableName(), ctx.getFrom());
		ctx.getQuery().addWhere(ctx.getFrom() + "._id in (:ids)");
		return ctx;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	public void addColumn(DModel<?> type, ISqlColumn column) {
		addTable(type);
		SqlTable tbl = tables.get(type.getIndex());
		tbl.addColumn(column);
	}

	private void addTable(DModel<?> type) {
		int[] allTypes = type.getAllTypes();
		for (int i : allTypes) {
			SqlTable tbl = tables.get(i);
			if (tbl == null) {
				DModel<?> temp = schema.getType(i);
				tables.put(i, tbl = new SqlTable(i, temp.getTableName(), temp.isEmbedded()));
			}
		}
	}

	public String getTableName() {
		return table;
	}

	public int getType() {
		return type;
	}

	public Map<Integer, SqlTable> getTables() {
		return tables;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return table;
	}

	public boolean isEmpty() {
		return tables.values().stream().allMatch(t -> t.getColumns().isEmpty());
	}

	public void selectColumns(SqlQueryContext ctx) {
		if (isEmpty()) {
			ctx.subType(type);
			return;
		}
		SqlQueryContext typeCtx = ctx.subType(getType());
		DModel<?> dModel = schema.getType(type);
		if (!dModel.isEmbedded() && dModel.getAllTypes().length > 1) {
			StringBuilder b = new StringBuilder();
			b.append("(case");
			int[] allTypes = findAllTypes(type);
			for (int x = allTypes.length - 1; x >= 0; x--) {
				int t = allTypes[x];
				b.append(" when ").append(ctx.getTableAlias(String.valueOf(t))).append("._id is not null then ")
						.append(t);
			}
			b.append(" else -1 end)");
			typeCtx.addSelection(b.toString(), "__typeindex");
		}
		getTables().get(getType()).addSelections(typeCtx);

		getTables().forEach((type, table) -> {
			if (type.equals(getType())) {
				return;
			}
			SqlQueryContext sub = ctx.subType(type);
			String join = sub.getFrom();
			sub.addJoin(table.getTableName(), join, join + "._id = " + typeCtx.getFrom() + "._id");
			table.addSelections(sub);
		});
	}

	private int[] findAllTypes(int type) {
		return schema.getType(type).getAllTypes();
	}

	public OutObjectList executeQuery(IEntityManager em, Set<Long> ids, Map<Long, OutObject> byId) throws Exception {
		if (ids.isEmpty()) {
			return new OutObjectList();
		}
		SqlQueryContext ctx = createCtx();
		ctx.addSqlColumns(this);
		SqlQuery query = ctx.getQuery();
		String sql = query.createSQL();
		D3ELogger.displaySql(getPath(), sql, ids);
		Query q = em.createNativeQuery(sql);
		q.setParameter("ids", ids);
		List<?> rows = q.getResultList();
		QueryReader reader = query.getReader();
		OutObjectList result = new OutObjectList();
		for (Object r : rows) {
			OutObject obj = reader.read(r, byId);
			result.add(obj);
		}
		if (!result.isEmpty()) {
			executeSubQuery(em, (t) -> result);
		}
		return result;
	}

	public void executeSubQuery(IEntityManager em, Function<Integer, List<OutObject>> listSupplier) throws Exception {
		for (Map.Entry<Integer, SqlTable> e : tables.entrySet()) {
			Integer type = e.getKey();
			SqlTable table = e.getValue();
			List<OutObject> apply = listSupplier.apply(type);
			for (ISqlColumn c : table.getColumns()) {
				c.extractDeepFields(em, schema, type, apply);
				SqlAstNode sub = c.getSubQuery();
				if (sub != null) {
					Map<Long, OutObject> objById = new HashMap<>();
					apply.forEach(o -> {
						try {
							OutObject row = objById.get(o.getId());
							if (row != null) {
								row.duplicate(o);
							} else {
								objById.put(o.getId(), o);
							}
						} catch (JSONException ex) {
						}
					});
					OutObjectList array = sub.executeQuery(em, objById.keySet(), objById);
					c.updateSubField(objById, array);
				}
			}

		}
	}
}

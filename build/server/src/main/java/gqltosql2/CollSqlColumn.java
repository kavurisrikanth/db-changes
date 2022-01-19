package gqltosql2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gqltosql.schema.IModelSchema;
import store.IEntityManager;

public class CollSqlColumn implements ISqlColumn {

	private String field;
	private SqlAstNode sub;

	public CollSqlColumn(SqlAstNode sub, String field) {
		this.sub = sub;
		this.field = field;
	}

	@Override
	public String getFieldName() {
		return field;
	}

	@Override
	public void addColumn(SqlTable table, SqlQueryContext ctx) {
	}

	@Override
	public String toString() {
		return field;
	}

	@Override
	public SqlAstNode getSubQuery() {
		return sub;
	}

	@Override
	public void extractDeepFields(IEntityManager em, IModelSchema schema, int type, List<OutObject> rows)
			throws Exception {
	}

	@Override
	public void updateSubField(Map<Long, OutObject> parents, List<OutObject> all) throws Exception {
		Map<Long, OutPrimitiveList> values = new HashMap<>();
		for (int i = 0; i < all.size(); i++) {
			OutObject obj = all.get(i);
			Long parentId = obj.getLong("_parent");
			OutPrimitiveList val = values.get(parentId);
			if (val == null) {
				values.put(parentId, val = new OutPrimitiveList());
			}
			val.add(obj.getPrimitive(getFieldName()));
		}
		for (Map.Entry<Long, OutObject> e : parents.entrySet()) {
			OutPrimitiveList val = values.get(e.getKey());
			if (val == null) {
				val = new OutPrimitiveList();
			}
			e.getValue().add(field, val);
		}
	}
}

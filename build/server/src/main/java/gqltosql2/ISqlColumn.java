package gqltosql2;

import java.util.List;
import java.util.Map;

import gqltosql.schema.IModelSchema;
import store.IEntityManager;

public interface ISqlColumn {

	String getFieldName();

	void addColumn(SqlTable table, SqlQueryContext ctx);

	SqlAstNode getSubQuery();

	void updateSubField(Map<Long, OutObject> parents, List<OutObject> all) throws Exception;

	void extractDeepFields(IEntityManager em, IModelSchema schema, int type, List<OutObject> rows) throws Exception;
}

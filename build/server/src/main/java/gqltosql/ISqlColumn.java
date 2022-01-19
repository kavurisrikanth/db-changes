package gqltosql;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.json.JSONArray;

import gqltosql.schema.IModelSchema;

public interface ISqlColumn {

	String getFieldName();

	void addColumn(SqlTable table, SqlQueryContext ctx);

	SqlAstNode getSubQuery();

	void updateSubField(Map<Long, SqlRow> parents, JSONArray all) throws Exception;

	void extractDeepFields(EntityManager em, IModelSchema schema, String type, List<SqlRow> rows) throws Exception;
}

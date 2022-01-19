package gqltosql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONObject;

import d3e.core.SetExt;
import gqltosql.schema.DField;
import gqltosql.schema.DFlatField;
import gqltosql.schema.DModel;
import gqltosql.schema.DModelType;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.Selection;
import graphql.language.TypeName;

public class GqlToSql {

	private static final String TYPENAME = "__typename";
	private IModelSchema schema;
	private EntityManager em;

	public GqlToSql(EntityManager em, IModelSchema schema) {
		this.em = em;
		this.schema = schema;
	}

	public JSONArray execute(String parentType, Field field, List<SqlRow> objs) throws Exception {
		if (objs.isEmpty()) {
			return new JSONArray();
		}
		SqlAstNode sqlNode = prepareSqlNode(field.getSelectionSet().getSelections(), parentType);
		Set<Long> ids = new HashSet<>();
		Map<Long, SqlRow> byId = new HashMap<>();
		for (SqlRow obj : objs) {
			long id = obj.getLong("id");
			ids.add(id);
			byId.put(id, obj);
		}
		JSONArray result = sqlNode.executeQuery(em, ids, byId);
		return result;
	}

	public JSONObject execute(String parentType, Field field, Long id) throws Exception {
		JSONArray array = execute(parentType, field, SetExt.asSet(id));
		if (array.length() != 0) {
			return array.getJSONObject(0);
		}
		return null;
	}

	public JSONArray execute(String parentType, Field field, Set<Long> ids) {
		try {
			SqlAstNode sqlNode = prepareSqlNode(field.getSelectionSet().getSelections(), parentType);
			JSONArray result = sqlNode.executeQuery(em, ids, new HashMap<>());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}

	private SqlAstNode prepareSqlNode(List<Selection> selections, String parentType) {
		DModel<?> dm = schema.getType(parentType);
		SqlAstNode node = new SqlAstNode(schema, "this", dm.getType(), dm.getTableName(), dm.isEmbedded());
		addReferenceField(node, selections, dm, dm);
		return node;
	}

	private void addField(SqlAstNode node, Field field, DModel<?> parentType, DModel<?> nonEmbeddedModel) {
		if (field.getName().equals(TYPENAME)) {
			node.setNeedType(true);
			return;
		}
		DField<?, ?> df = parentType.getField(field.getName());
		if (df == null) {
			return;
		}
		switch (df.getType()) {
		case Primitive:
			addPrimitiveField(node, field, df);
			break;
		case Reference:
			addReferenceField(node, field, df, nonEmbeddedModel);
			break;
		case PrimitiveCollection:
			addPrimitiveCollectionField(node, field, df);
			break;
		case ReferenceCollection:
			addReferenceCollectionField(node, field, df, nonEmbeddedModel);
			break;
		case InverseCollection:
			addInverseCollectionField(node, field, df, nonEmbeddedModel);
			break;
		default:
			break;
		}
	}

	private void addPrimitiveCollectionField(SqlAstNode node, Field field, DField<?, ?> df) {
		DModel<?> dcl = df.declType();
		SqlAstNode sub = new SqlPrimitiveCollAstNode(schema, node.getPath() + "." + field.getName(),
				df.getCollTableName(dcl.getTableName()), dcl.getTableName() + "_id", df.getColumnName(),
				field.getName());
		addColumn(node, df, new CollSqlColumn(sub, field.getName()));
	}

	private void addReferenceCollectionField(SqlAstNode node, Field field, DField<?, ?> df,
			DModel<?> nonEmbeddedModel) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentFlatSqlColumn(field, (DFlatField<?, ?>) df));
		} else if (df.declType().isEmbedded()) {
			DModel<?> dcl = nonEmbeddedModel;
			SqlCollAstNode sub = new SqlCollAstNode(schema, node.getPath() + "." + field.getName(), dm.getType(),
					dm.getTableName(), df.getCollTableName(dcl.getTableName()), dcl.getTableName() + "_id",
					df.getColumnName());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), nonEmbeddedModel);
			addColumn(node, df, new RefCollSqlColumn(sub, field.getName()));
		} else if (dm.getModelType() == DModelType.MODEL) {
			DModel<?> dcl = df.declType();
			SqlCollAstNode sub = new SqlCollAstNode(schema, node.getPath() + "." + field.getName(), dm.getType(),
					dm.getTableName(), df.getCollTableName(dcl.getTableName()), dcl.getTableName() + "_id",
					df.getColumnName());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), df.getReference());
			addColumn(node, df, new RefCollSqlColumn(sub, field.getName()));
		}
	}

	private void addInverseCollectionField(SqlAstNode node, Field field, DField<?, ?> df, DModel<?> nonEmbeddedModel) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentFlatSqlColumn(field, (DFlatField<?, ?>) df));
		} else if (dm.isEmbedded()) {
			SqlInverseCollAstNode sub = new SqlInverseCollAstNode(schema, node.getPath() + "." + field.getName(),
					dm.getType(), dm.getTableName(), df.getColumnName());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), nonEmbeddedModel);
			addColumn(node, df, new RefCollSqlColumn(sub, field.getName()));
		} else if (dm.getModelType() == DModelType.MODEL || dm.isEmbedded()) {
			SqlInverseCollAstNode sub = new SqlInverseCollAstNode(schema, node.getPath() + "." + field.getName(),
					dm.getType(), dm.getTableName(), df.getColumnName());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), df.getReference());
			addColumn(node, df, new RefCollSqlColumn(sub, field.getName()));
		}
	}

	private void addReferenceField(SqlAstNode node, Field field, DField<?, ?> df, DModel<?> nonEmbeddedModel) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentSqlColumn(field, df));
		} else if (dm.isEmbedded()) {
			SqlAstNode sub = new SqlAstNode(schema, node.getPath() + "." + field.getName(), dm.getType(),
					dm.getTableName(), dm.isEmbedded());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), nonEmbeddedModel);
			addColumn(node, df, new RefSqlColumn(sub, df.getColumnName(), field.getName()));
		} else if (dm.getModelType() == DModelType.MODEL) {
			SqlAstNode sub = new SqlAstNode(schema, node.getPath() + "." + field.getName(), dm.getType(),
					dm.getTableName(), dm.isEmbedded());
			addReferenceField(sub, field.getSelectionSet().getSelections(), df.getReference(), df.getReference());
			addColumn(node, df, new RefSqlColumn(sub, df.getColumnName(), field.getName()));
		}
	}

	private void addColumn(SqlAstNode node, DField<?, ?> df, ISqlColumn column) {
		node.addColumn(df.declType(), column);
	}

	private void addReferenceField(SqlAstNode node, List<Selection> selections, DModel<?> parentType,
			DModel<?> nonEmbeddedModel) {
		for (Selection<?> selection : selections) {
			if (selection instanceof FragmentSpread) {
				throw new RuntimeException("TODO: FragmentSpread not implemented yet");
			} else if (selection instanceof InlineFragment) {
				InlineFragment in = (InlineFragment) selection;
				TypeName typeName = in.getTypeCondition();
				DModel<?> dm = schema.getType(typeName.getName());
				addReferenceField(node, in.getSelectionSet().getSelections(), dm, nonEmbeddedModel);
			} else {
				addField(node, (Field) selection, parentType, nonEmbeddedModel);
			}
		}
	}

	private void addPrimitiveField(SqlAstNode node, Field field, DField<?, ?> df) {
		addColumn(node, df, new SqlColumn(df.getColumnName(), field.getName()));
	}
}

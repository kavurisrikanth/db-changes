package gqltosql2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import d3e.core.SetExt;
import gqltosql.schema.DEmbField;
import gqltosql.schema.DField;
import gqltosql.schema.DFlatField;
import gqltosql.schema.DModel;
import gqltosql.schema.DModelType;
import gqltosql.schema.IModelSchema;
import store.D3EEntityManagerProvider;

public class GqlToSql {

	private IModelSchema schema;
	private D3EEntityManagerProvider em;

	public GqlToSql(D3EEntityManagerProvider em, IModelSchema schema) {
		this.em = em;
		this.schema = schema;
	}

	public OutObjectList execute(String typeStr, Field field, List<OutObject> objs) throws Exception {
		if (objs.isEmpty()) {
			return new OutObjectList();
		}
		DModel<?> type = schema.getType(typeStr);
		SqlAstNode sqlNode = prepareSqlNode(field.getSelections(), type);
		Set<Long> ids = new HashSet<>();
		Map<Long, OutObject> byId = new HashMap<>();
		for (OutObject obj : objs) {
			long id = obj.getLong("id");
			ids.add(id);
			byId.put(id, obj);
		}
		OutObjectList result = sqlNode.executeQuery(em.get(), ids, byId);
		return result;
	}

	public OutObject execute(String typeStr, Field field, Long id) throws Exception {
		DModel<?> type = schema.getType(typeStr);
		OutObjectList array = execute(type, field, SetExt.asSet(id));
		if (array.size() != 0) {
			return array.get(0);
		}
		return null;
	}

	public OutObjectList execute(DModel<?> type, Field field, Set<Long> ids) {
		try {
			SqlAstNode sqlNode = prepareSqlNode(field.getSelections(), type);
			OutObjectList result = sqlNode.executeQuery(em.get(), ids, new HashMap<>());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new OutObjectList();
		}
	}

	private SqlAstNode prepareSqlNode(List<Selection> selections, DModel<?> type) {
		SqlAstNode node = new SqlAstNode(schema, "this", type.getIndex(), type.getTableName(), type.isEmbedded());
		addReferenceField(node, selections, "");
		return node;
	}

	private void addField(SqlAstNode node, Field field, DModel<?> parentType, String embeddedPrefix) {
		DField<?, ?> df = field.getField();
		if (df == null) {
			return;
		}
		switch (df.getType()) {
		case Primitive:
			addPrimitiveField(node, field, df, embeddedPrefix);
			break;
		case Reference:
			addReferenceField(node, field, df, embeddedPrefix);
			break;
		case PrimitiveCollection:
			addPrimitiveCollectionField(node, field, df);
			break;
		case ReferenceCollection:
			addReferenceCollectionField(node, field, df);
			break;
		case InverseCollection:
			addInverseCollectionField(node, field, df);
			break;
		default:
			break;
		}
	}

	private void addPrimitiveCollectionField(SqlAstNode node, Field field, DField<?, ?> df) {
		DModel<?> dcl = df.declType();
		SqlAstNode sub = new SqlPrimitiveCollAstNode(schema, node.getPath() + "." + df.getName(),
				df.getCollTableName(dcl.getTableName()), dcl.getTableName() + "_id", df.getColumnName(), df.getName());
		addColumn(node, df, new CollSqlColumn(sub, df.getName()));
	}

	private void addReferenceCollectionField(SqlAstNode node, Field field, DField<?, ?> df) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentFlatSqlColumn(field, (DFlatField<?, ?>) df));
		} else if (dm.getModelType() == DModelType.MODEL) {
			DModel<?> dcl = df.declType();
			SqlCollAstNode sub = new SqlCollAstNode(schema, node.getPath() + "." + df.getName(), dm.getIndex(),
					dm.getTableName(), df.getCollTableName(dcl.getTableName()), dcl.getTableName() + "_id",
					df.getColumnName());
			addReferenceField(sub, field.getSelections(), "");
			addColumn(node, df, new RefCollSqlColumn(sub, df.getName()));
		}
	}

	private void addInverseCollectionField(SqlAstNode node, Field field, DField<?, ?> df) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentFlatSqlColumn(field, (DFlatField<?, ?>) df));
		} else if (dm.isEmbedded()) {
			SqlInverseCollAstNode sub = new SqlInverseCollAstNode(schema, node.getPath() + "." + df.getName(),
					dm.getIndex(), dm.getTableName(), df.getColumnName());
			addReferenceField(sub, field.getSelections(), "");
			addColumn(node, df, new RefCollSqlColumn(sub, df.getName()));
		} else if (dm.getModelType() == DModelType.MODEL) {
			SqlInverseCollAstNode sub = new SqlInverseCollAstNode(schema, node.getPath() + "." + df.getName(),
					dm.getIndex(), dm.getTableName(), df.getColumnName());
			addReferenceField(sub, field.getSelections(), "");
			addColumn(node, df, new RefCollSqlColumn(sub, df.getName()));
		}
	}

	private void addReferenceField(SqlAstNode node, Field field, DField<?, ?> df, String embeddedPrefix) {
		DModel<?> dm = df.getReference();
		if (dm.isDocument()) {
			addColumn(node, df, new DocumentSqlColumn(field, df, embeddedPrefix + df.getColumnName()));
		} else if (dm.isEmbedded()) {
			SqlAstNode sub = new SqlAstNode(schema, node.getPath() + "." + df.getName(), dm.getIndex(),
					dm.getTableName(), dm.isEmbedded());
			DEmbField<?, ?> emb = (DEmbField<?, ?>) df;
			addReferenceField(sub, field.getSelections(), emb.getPrefix());
			addColumn(node, df, new RefSqlColumn(sub, df.getColumnName(), df.getName()));
		} else if (dm.getModelType() == DModelType.MODEL) {
			SqlAstNode sub = new SqlAstNode(schema, node.getPath() + "." + df.getName(), dm.getIndex(),
					dm.getTableName(), dm.isEmbedded());
			addReferenceField(sub, field.getSelections(), "");
			addColumn(node, df, new RefSqlColumn(sub, embeddedPrefix + df.getColumnName(), df.getName()));
		}
	}

	private void addColumn(SqlAstNode node, DField<?, ?> df, ISqlColumn column) {
		node.addColumn(df.declType(), column);
	}

	private void addReferenceField(SqlAstNode node, List<Selection> selections, String embeddedPrefix) {
		for (Selection selection : selections) {
			DModel<?> parentType = selection.getType();
			for (Field field : selection.getFields()) {
				addField(node, field, parentType, embeddedPrefix);
			}
		}
	}

	private void addPrimitiveField(SqlAstNode node, Field field, DField<?, ?> df, String embeddedPrefix) {
		addColumn(node, df, new SqlColumn(embeddedPrefix + df.getColumnName(), df.getName()));
	}
}

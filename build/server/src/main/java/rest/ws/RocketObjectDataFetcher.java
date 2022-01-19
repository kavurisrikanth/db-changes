package rest.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import d3e.core.DFile;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.IDataFetcher;
import lists.TypeAndId;
import store.DBObject;

public class RocketObjectDataFetcher {

	private RocketMessage msg;
	private Template template;
	private Function<TypeAndId, DBObject> toObject;

	public RocketObjectDataFetcher(Template template, RocketMessage msg, Function<TypeAndId, DBObject> toObject) {
		this.template = template;
		this.msg = msg;
		this.toObject = toObject;
	}

	public void fetch(TemplateUsage usage, Object value) {
		fetchValue(usage, value);
	}

	private void fetchValue(TemplateUsage usage, Object value) {
		if (value == null) {
			msg.writeNull();
		} else if (value instanceof DFile) {
			msg.writeDFile((DFile) value);
		} else if (value instanceof String) {
			msg.writeString((String) value);
		} else if (value instanceof Long) {
			msg.writeLong((long) value);
		} else if (value instanceof Boolean) {
			msg.writeBoolean((boolean) value);
		} else if (value instanceof Double) {
			msg.writeDouble((double) value);
		} else if (value instanceof Collection) {
			List<?> coll = new ArrayList<>((Collection<?>) value);
			msg.writeInt(coll.size());
			coll.forEach(v -> fetchValue(usage, v));
		} else {
			fetchReference(usage.getTypes(), (DBObject) value);
		}
	}

	private void fetchReference(UsageType[] types, Object value) {
		int serverType = 0;
		long id = 0;
		if (value instanceof TypeAndId) {
			TypeAndId typeId = (TypeAndId) value;
			serverType = typeId.type;
			id = typeId.id;
		} else {
			if(value instanceof DFile) {
				msg.writeDFile((DFile) value);
				return;
			}
			DBObject dbObj = (DBObject) value;
			serverType = dbObj._typeIdx();
			id = dbObj.getId();
		}
		int typeIdx = template.toClientTypeIdx(serverType);
		msg.writeInt(typeIdx);
		TemplateType type = template.getType(typeIdx);
		// D3ELogger.info("w ref: " + type.getModel().getType());
		if(!type.getModel().isEmbedded()) {
			msg.writeLong(id);
		}
		List<Integer> indxes = selectAllTypes(typeIdx);
		for (UsageType ut : types) {
			if (indxes.contains(ut.getType())) {
				TemplateType tt = template.getType(ut.getType());
				fetchReferenceInternal(tt, ut, value);
			}
		}
		msg.writeInt(-1);
	}

	private List<Integer> selectAllTypes(int typeIdx) {
		List<Integer> indxes = new ArrayList<>();
		while (typeIdx != -1) {
			TemplateType tt = template.getType(typeIdx);
			indxes.add(typeIdx);
			DModel<?> parent = tt.getModel().getParent();
			if (parent != null) {
				typeIdx = template.toClientTypeIdx(parent.getIndex());
			} else {
				typeIdx = -1;
			}
		}
		return indxes;
	}

	private void fetchReferenceInternal(TemplateType type, UsageType usage, Object value) {
		if(usage.getFields().length > 0) {
			if(value instanceof TypeAndId) {
				value = toObject.apply((TypeAndId) value);
			}
			for (UsageField f : usage.getFields()) {
				DField df = type.getField(f.getField());
				if(df instanceof UnknownField) {
					continue;
				}
				// D3ELogger.info("w field: " + df.getName());
				msg.writeInt(f.getField());
				df.fetchValue(value, new DataFetcher(f, df.getEnumType()));
			}
		}
	}

	

	private class DataFetcher implements IDataFetcher {

		private UsageField field;
		private int enumType;

		public DataFetcher(UsageField field, int enumType) {
			this.field = field;
			this.enumType = enumType;
		}

		@Override
		public Object onPrimitiveValue(Object val, DField df) {
			msg.writePrimitiveField(val, df, template);
			return null;
		}

		@Override
		public Object onReferenceValue(Object value) {
			if (value == null) {
				msg.writeNull();
				return null;
			}
			fetchReference(field.getTypes(), value);
			return null;
		}

		@Override
		public Object onEmbeddedValue(Object value) {
			if (value == null) {
				msg.writeNull();
			}
			return onReferenceValue(value);
		}

		@Override
		public Object onPrimitiveList(List<?> value, DField df) {
			// D3ELogger.info("w pri List: " + value.size());
			msg.writeInt(value.size());
			value.forEach(v -> onPrimitiveValue(v, df));
			return null;
		}

		@Override
		public Object onReferenceList(List<?> value) {
			// D3ELogger.info("w ref List: " + value.size());
			msg.writeInt(value.size());
			value.forEach(v -> onReferenceValue(v));
			return null;
		}

		@Override
		public Object onFlatValue(List<?> value) {
			return onReferenceList(new ArrayList<>(value));
		}

		@Override
		public Object onInverseValue(List<?> value) {
			return onReferenceList(value);
		}
	}
}

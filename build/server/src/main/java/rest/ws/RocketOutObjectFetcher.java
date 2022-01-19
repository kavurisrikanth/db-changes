package rest.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import d3e.core.SchemaConstants;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql2.OutObject;

public class RocketOutObjectFetcher {

	private RocketMessage msg;
	private Template template;

	public RocketOutObjectFetcher(Template template, RocketMessage msg) {
		this.template = template;
		this.msg = msg;
	}

	public void fetch(TemplateUsage usage, Object value) {
		fetchValue(usage, value);
	}

	private void fetchValue(TemplateUsage usage, Object value) {
		if (value == null) {
			msg.writeNull();
		} else if (value instanceof Collection) {
			List<?> coll = new ArrayList<>((Collection<?>) value);
			// D3ELogger.info("w coll: " + coll.size());
			msg.writeInt(coll.size());
			coll.forEach(v -> fetchValue(usage, v));
		} else if (value instanceof OutObject) {
			OutObject db = (OutObject) value;
			// D3ELogger.info("w ref: " + db.getTypes());
			int clientType = db.getType();
			int typeIdx = template.toClientTypeIdx(clientType);
			msg.writeInt(typeIdx);
			msg.writeLong(db.getId());
			for (UsageType ut : usage.getTypes()) {
				TemplateType tt = template.getType(ut.getType());
				fetchReferenceInternal(tt, ut, db);
			}
			msg.writeInt(-1);
		} else {
			throw new RuntimeException("Unsupported type. " + value.getClass());
		}
	}

	private void fetchPrimitive(Object value, DField df) {
		msg.writePrimitiveField(value, df, template);
	}

	private void fetchDFile(OutObject db) {
		String id = db.getString("id");
		if (id == null) {
			msg.writeString(null);
			return;
		}
		msg.writeString(id);
		msg.writeString(db.getString("name"));
		msg.writeLong(db.getLong("size"));
		msg.writeString(db.getString("mimeType"));
	}

	private void fetchReferenceInternal(TemplateType type, UsageType usage, OutObject value) {
		for (UsageField f : usage.getFields()) {
			DField df = type.getField(f.getField());
			if (df instanceof UnknownField) {
				continue;
			}
			// D3ELogger.info("w field: " + df.getName());
			msg.writeInt(f.getField());
			Object val = value.get(df.getName());
			fetchFieldValue(f, val, df);
		}
	}

	private void fetchFieldValue(UsageField field, Object value, DField df) {
		if (value == null) {
			DModel<?> ref = df.getReference();
			if (ref != null && ref.isEmbedded()) {
				msg.writeInt(template.toClientTypeIdx(ref.getIndex()));
				msg.writeNull();
			} else {
				msg.writeNull();
			}
		} else if (value instanceof Collection) {
			List<?> coll = new ArrayList<>((Collection<?>) value);
			// D3ELogger.info("w coll: " + coll.size());
			msg.writeInt(coll.size());
			coll.forEach(v -> fetchFieldValue(field, v, df));
		} else if (value instanceof OutObject) {
			OutObject db = (OutObject) value;
			// D3ELogger.info("w ref: " + db.getTypes());
			int typeIdx = template.toClientTypeIdx(db.getType());
			DModel<?> ref = df.getReference();
			if (ref.getIndex() == SchemaConstants.DFile) {
				fetchDFile(db);
				return;
			}
			msg.writeInt(typeIdx);
			if (!ref.isEmbedded()) {
				msg.writeLong(db.getId());
			}
			UsageType[] types = field.getTypes();
			for (UsageType type : types) {
				TemplateType tt = template.getType(type.getType());
				fetchReferenceInternal(tt, type, db);
			}
			msg.writeInt(-1);
		} else {
			UsageType[] types = field.getTypes();
			fetchPrimitive(value, df);
		}
	}
}

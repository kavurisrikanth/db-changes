package rest.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import d3e.core.DFile;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldType;
import store.DBObject;
import store.EntityHelper;
import store.EntityHelperService;

public class RocketInputContext {

	private RocketMessage msg;
	Template template;
	private Map<Long, DBObject> localCache = new HashMap<>();
	private EntityHelperService helperService;

	public RocketInputContext(EntityHelperService helperService, Template template, RocketMessage msg) {
		this.helperService = helperService;
		this.template = template;
		this.msg = msg;
	}

	public long readLong() {
		long l = msg.readLong();
		return l;
	}

	public void writeObjectList(List<?> list) {
		if (list == null) {
			return;
		}
		int size = list.size();
		msg.writeInt(size);
		for (Object obj : list) {
			writeObject((DBObject) obj);
		}
	}

	public void writeObject(DBObject obj) {
		if (obj == null) {
			// D3ELogger.info("w obj: null");
			msg.writeInt(-1);
			return;
		}
		int typeIdx = template.toClientTypeIdx(obj._typeIdx());
		// D3ELogger.info("w obj: " + typeIdx + " " + obj._type());
		msg.writeInt(typeIdx);
		TemplateType tt = template.getType(typeIdx);
		if (!tt.getModel().isEmbedded()) {
			msg.writeLong(obj.getId());
		}
		while (tt != null) {
			writeProperties(obj, tt);
			tt = tt.getParentType();
		}
		msg.writeInt(-1);
	}

	private void writeProperties(DBObject obj, TemplateType tt) {
		DField<?, ?>[] fields = tt.getFields();
		int i = tt.getParentClientCount();
		for (DField df : fields) {
			// D3ELogger.info("w field: " + i + " " + df.getName());
			if (df instanceof UnknownField) {
				i++;
				continue;
			}
			FieldType ft = df.getType();
			Object val = df.getValue(obj);
			if (df.getReference() != null && df.getReference().isEmbedded() && val == null) {
				i++;
				continue;
			}
			msg.writeInt(i++);
			if (val == null) {
				// D3ELogger.info("w null: ");
				msg.writeInt(-1);
				continue;
			}
			switch (ft) {
			case Primitive:
				writePrimitive(val, df);
				break;
			case PrimitiveCollection:
				List vals = (List) val;
				// D3ELogger.info("w primitive List: " + vals.size());
				msg.writeInt(vals.size());
				for (Object o : vals) {
					writePrimitive(o, df);
				}
				break;
			case Reference:
				DModel ref = df.getReference();
				if (ref.isEmbedded()) {
					writeEmbedded((DBObject) val, df.getReference());
				} else if (df.isChild()) {
					writeObject((DBObject) val);
				} else if (ref.getType().equals("DFile")) {
					writeDFile(msg, template, (DFile) val);
				} else {
					writeRef((DBObject) val);
				}
				break;
			case InverseCollection:
			case ReferenceCollection:
				List coll = (List) val;
				// D3ELogger.info("w ref List: " + coll.size());
				msg.writeInt(coll.size());
				for (Object o : coll) {
					if (df.isChild()) {
						writeObject((DBObject) o);
					} else {
						writeRef((DBObject) o);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private void writeEmbedded(DBObject obj, DModel ref) {
		if (obj == null) {
			msg.writeInt(template.toClientTypeIdx(ref.getIndex()));
			msg.writeInt(-1);
			return;
		}
		writeObject(obj);
	}

	private void writeRef(DBObject obj) {
		if (obj == null) {
			// D3ELogger.info("w ref: null");
			msg.writeInt(-1);
			return;
		}
		int typeIdx = template.toClientTypeIdx(obj._typeIdx());
		msg.writeInt(typeIdx);
		// D3ELogger.info("w ref: " + typeIdx + " " + obj._type());
		msg.writeLong(obj.getId());
		msg.writeInt(-1);
	}

	private void writePrimitive(Object val, DField df) {
		msg.writePrimitiveField(val, df, template);
	}

	public static void writeDFile(RocketMessage msg, Template template, DFile val) {
		msg.writeString(val.getId());
		msg.writeString(val.getName());
		msg.writeLong(val.getSize());
		msg.writeString(val.getMimeType());
	}

	private void readEmbedded(Object obj) {
		TemplateType tt = readType();
		if (tt == null) {
			// D3ELogger.info("r emb: null");
			return;
		}
		// D3ELogger.info("r emb: " + tt.getModel().getType());
		readObjectProperties(obj, tt);
	}

	public <T> List<T> readObjectCollection() {
		long num = readLong();
		List result = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			T obj = readObject();
			result.add(obj);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T readObject() {
		TemplateType tt = readType();
		if (tt == null) {
			// D3ELogger.info("r obj: null");
			return null;
		}
		long id = readLong();
		// D3ELogger.info("r obj: " + id);
		Object obj;
		if (id <= 0) {
			obj = localCache.get(id);
			if (obj == null) {
				obj = tt.getModel().newInstance();
				if (obj instanceof DBObject) {
					DBObject dbobj = (DBObject) obj;
					if (id < 0) {
						localCache.put(id, dbobj);
					}
					dbobj.setLocalId(id);
				}
			}
		} else {
			EntityHelper<?> entity = helperService.get(tt.getModel().getType());
			obj = entity.getById(id);
		}
		readObjectProperties(obj, tt);
		return (T) obj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void readObjectProperties(Object obj, TemplateType tt) {
		while (true) {
			int fi = msg.readInt();
			if (fi < 0) {
				break;
			}
			DField df = tt.getField(fi);
			// D3ELogger.info("r field: " + fi + " " + df.getName());
			FieldType ft = df.getType();
			switch (ft) {
			case Primitive:
				Object val = readPrimitive(df);
				df.setValue(obj, val);
				break;
			case PrimitiveCollection:
				List vals = readPrimitiveCollection(df, (List) df.getValue(obj));
				df.setValue(obj, vals);
				break;
			case Reference:
				if (df.getReference().isEmbedded()) {
					readEmbedded(df.getValue(obj));
				} else if (df.getReference().getType().equals("DFile")) {
					df.setValue(obj, readDFile());
				} else {
					df.setValue(obj, readObject());
				}
				break;
			case ReferenceCollection: {
				List colls = readReferenceCollection((List) df.getValue(obj));
				df.setValue(obj, colls);
			}
				break;
			case InverseCollection:
				throw new RuntimeException("Can not read InverseCollectgion: " + df.getName());
			default:
				break;
			}
		}
	}

	private List readColl(List old, Supplier itemReader) {
		int size = msg.readInt();
		// D3ELogger.info("r coll: " + size);
		if (size < 0) {
			old = new ArrayList<>(old);
			size = -size;
			for (int i = 0; i < size; i++) {
				int idx = msg.readInt();
				if (idx < 0) {
					idx = -idx;
					idx--;
					old.remove(idx);
				} else {
					idx--;
					Object obj = itemReader.get();
					if (idx >= old.size()) {
						old.add(obj);
					} else {
						old.add(idx, obj);
					}
				}
			}
			return old;
		} else {
			List colls = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				colls.add(itemReader.get());
			}
			return colls;
		}
	}

	private List readReferenceCollection(List old) {
		return readColl(old, () -> readObject());
	}

	private List readPrimitiveCollection(DField field, List old) {
		return readColl(old, () -> readPrimitive(field));
	}

	private Object readPrimitive(DField df) {
		return msg.readPrimitive(df, template);
	}

	public <T> T readEnum() {
		TemplateType type = readType();
		if (type == null) {
			// D3ELogger.info("r enum: null");
			return null;
		}
		int fidx = msg.readInt();
		DField<?, ?> field = type.getField(fidx);
		// D3ELogger.info("r enum: " + field.getName());
		return (T) field.getValue(null);
	}

	public <T extends Enum> void writeEnum(int enumType, T val) {
		int clientTypeIdx = template.toClientTypeIdx(enumType);
		msg.writeInt(clientTypeIdx);
		TemplateType type = template.getType(clientTypeIdx);
		DField<?, ?> field = type.getModel().getField(val.name());
		int clientIdx = type.toClientIdx(field.getIndex());
		msg.writeInt(clientIdx);
	}

	public double readDouble() {
		double d = msg.readDouble();
		// D3ELogger.info("r double: " + d);
		return d;
	}

	public boolean readBoolean() {
		boolean b = msg.readBoolean();
		// D3ELogger.info("r bool: " + b);
		return b;
	}

	public Object readDFile() {
		return msg.readDField();
	}

	public TemplateType readType() {
		int type = msg.readInt();
		if (type == -1) {
			return null;
		}
		return template.getType(type);
	}

	public String readString() {
		String s = msg.readString();
		return s;
	}

	public void writeStringList(List<String> list) {
		msg.writeStringList(list);
	}

	public void writeBoolean(boolean val) {
		msg.writeBoolean(val);
	}

	public void writeDFile(DFile val) {
		msg.writeDFile(val);
	}

	public void writeString(String str) {
		msg.writeString(str);
	}

	public void writeLong(long val) {
		msg.writeLong(val);
	}
}

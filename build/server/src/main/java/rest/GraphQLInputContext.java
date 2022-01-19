package rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import d3e.core.DFile;
import store.DatabaseObject;
import store.EntityHelper;
import store.EntityHelperService;

public abstract class GraphQLInputContext {

	EntityHelperService helperService;
	Map<Long, Object> inputObjectCache;
	Map<String, DFile> files;

	public GraphQLInputContext(EntityHelperService helperService) {
		this.helperService = helperService;
		this.inputObjectCache = new HashMap<>();
		this.files = new HashMap<>();
	}

	public GraphQLInputContext(EntityHelperService helperService, Map<Long, Object> inputObjectCache,
			Map<String, DFile> files) {
		this.helperService = helperService;
		this.inputObjectCache = inputObjectCache;
		this.files = files;
	}

	protected abstract GraphQLInputContext createContext(String field);

	public final long readLong(String field1, String field2) {
		GraphQLInputContext ctx = createContext(field1);
		if (ctx == null) {
			return 0;
		}
		return ctx.readLong(field2);
	}

	public final DFile readDFile(String field) {
		GraphQLInputContext ctx = createContext(field);
		if (ctx == null) {
			return null;
		}
		return readDFileInternal(ctx);
	}

	protected final DFile readDFileInternal(GraphQLInputContext ctx) {
		String id = ctx.readString("id");
		DFile entity = files.get(id);
		if (entity != null) {
			return entity;
		} else {
			entity = new DFile();
		}
		if (ctx.has("size")) {
			entity.setSize(ctx.readLong("size"));
		}
		if (ctx.has("name")) {
			entity.setName(ctx.readString("name"));
		}
		if (ctx.has("id")) {
			entity.setId(id);
		}
		return entity;
	}

	protected <T> T readObject(String type, boolean readFully) {
		if (has("__typeName")) {
			type = readString("__typeName");
		}
		EntityHelper helper = helperService.get(type);
		Object obj;
		if (has("id")) {
			obj = readRef(helper, readLong("id"));
		} else {
			obj = helper.newInstance();
		}
		if (readFully) {
//			helper.fromInput(obj, this);
		}
		return (T) obj;
	}

	protected <T> T readRef(EntityHelper<T> helper, long id) {
		if (id > 0) {
			T obj = helper.getById(id);
			return obj;
		}
		if (id == 0) {
			return null;
		}
		T obj = (T) inputObjectCache.get(id);
		if (obj == null) {
			obj = (T) helper.newInstance();
			if (obj instanceof DatabaseObject) {
				((DatabaseObject) obj).setLocalId(id);
				inputObjectCache.put(id, (DatabaseObject) obj);
			}
		}
		return obj;
	}

	protected final <T extends Enum<?>> T readEnumInternal(String name, Class<T> cls) {
		T[] constants = cls.getEnumConstants();
		for (T t : constants) {
			if (t.name().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public final <T extends Enum<?>> T readEnum(String field, Class<T> cls) {
		String name = readString(field);
		if (name == null) {
			return null;
		}
		return readEnumInternal(name, cls);
	}

	public <T extends IGraphQLInput> T readInto(String field, T into) {
		GraphQLInputContext ctx = createContext(field);
		if (ctx == null) {
			return null;
		}
		into.fromInput(ctx);
		return into;
	}

	public <T> T readEmbedded(String field, String type, T exists) {
		GraphQLInputContext ctx = createContext(field);
		if (ctx == null) {
			return null;
		}
		EntityHelper helper = helperService.get(type);
//		helper.fromInput(exists, ctx);
		return exists;
	}

	public abstract List<Long> readLongColl(String field);

	public abstract List<String> readStringColl(String field);
	
	public abstract List<Long> readIntegerColl(String field);

	public abstract <T> List<T> readChildColl(String field, String type);

	public abstract <T> List<T> readRefColl(String field, String type);

	public abstract <T> List<T> readUnionColl(String field, String type);

	public abstract <T extends Enum<?>> List<T> readEnumColl(String field, Class<T> cls);

	public abstract List<DFile> readDFileColl(String field);

	public abstract <T> T readRef(String field, String type);

	public abstract <T> T readChild(String field, String type);

	public abstract <T> T readUnion(String field, String type);

	public abstract long readLong(String field);

	public abstract String readString(String field);

	public abstract boolean has(String field);

	public abstract long readInteger(String field);

	public abstract double readDouble(String field);

	public abstract boolean readBoolean(String field);

	public abstract Duration readDuration(String field);

	public abstract LocalDateTime readDateTime(String field);

	public abstract LocalTime readTime(String field);

	public abstract LocalDate readDate(String field);
}

package store;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import d3e.core.D3ELogger;
import d3e.core.DFile;
import d3e.core.SchemaConstants;
import gqltosql.schema.DDocField;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldPrimitiveType;
import gqltosql.schema.FieldType;
import gqltosql.schema.IModelSchema;

@Service
public class D3EEntityManagerProvider {

	@Autowired
	private D3EQueryBuilder queryBuilder;

	@Autowired
	private IModelSchema schema;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	private ThreadLocal<IEntityManager> entityManager = new ThreadLocal<>();

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationStart(ApplicationReadyEvent event) {
		if (event.getApplicationContext().getEnvironment().acceptsProfiles(Profiles.of("test")))
			return;
		try {
			URL url = ResourceUtils.getURL("classpath:db/migration/type_functions.sql");
			byte[] bdata = FileCopyUtils.copyToByteArray(url.openStream());
			String data = new String(bdata, StandardCharsets.UTF_8);
			jdbcTemplate.getJdbcTemplate().execute(data);
			D3ELogger.info("Functions are created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean create(D3EPrimaryCache cache) {
		IEntityManager mgr = entityManager.get();
		if (mgr != null) {
			return false;
		}
		entityManager.set(new EntityManagerImpl(cache));
		return true;
	}

	public void clear() {
		entityManager.remove();
	}

	public IEntityManager get() {
		IEntityManager em = entityManager.get();
		if(em == null) {
			create(null);
			em = entityManager.get();
		}
		return em;
	}

	static class RowField {
		DField field;
		List<RowField> subFields;

		public RowField(DField df) {
			this.field = df;
		}

		public RowField(DField df, List<RowField> subFields) {
			this.field = df;
			this.subFields = subFields;
		}
	}

	private class SingleObjectMapper implements RowMapper<DatabaseObject> {

		private D3EPrimaryCache cache;
		private DatabaseObject obj;
		private DModel dm;
		private List<RowField> selectedFields;

		public SingleObjectMapper(D3EPrimaryCache cache, DatabaseObject obj, DModel dm, List<RowField> selectedFields) {
			this.cache = cache;
			this.obj = obj;
			this.dm = dm;
			this.selectedFields = selectedFields;
		}

		@Override
		public DatabaseObject mapRow(ResultSet rs, int rowNum) throws SQLException {
			int i = 1;
			rs.getObject(i++); // Just read id;
			readObject(rs, i, obj, selectedFields);
			return obj;
		}

		private int readObject(ResultSet rs, int i, Object obj, List<RowField> fields) throws SQLException {
			for (RowField rf : fields) {
				DField df = rf.field;
				if (rf.subFields != null) { // Embedded
					i = readObject(rs, i, df.getValue(obj), rf.subFields);
					continue;
				}
				FieldType type = df.getType();
				switch (type) {
				case Primitive:
					Object pri = readPrimitive(df, rs, i);
					df.setValue(obj, pri);
					i++;
					break;
				case Reference:
					DModel ref = df.getReference();
					if (ref.getIndex() == SchemaConstants.DFile) {
						DFile file = cache.getOrCreateDFile(rs.getString(i++));
						df.setValue(obj, file);
					} else {
						if (ref.isDocument()) {
							DDocField dc = (DDocField) df;
							dc.setDocValue(obj, rs.getString(i++));
						} else {
							long id = rs.getLong(i++);
							int refType = rs.getInt(i++);
							Object val = cache.getOrCreate(schema.getType(refType), id);
							df.setValue(obj, val);
						}
					}
					break;
				case InverseCollection:
				case PrimitiveCollection:
				case ReferenceCollection:
					break;
				default:
					break;

				}
			}
			return i;
		}
	}

	private class DFileMapper implements RowMapper<DFile> {

		private D3EPrimaryCache cache;
		private DFile file;

		public DFileMapper(D3EPrimaryCache cache, DFile file) {
			this.cache = cache;
			this.file = file;
		}

		@Override
		public DFile mapRow(ResultSet rs, int rowNum) throws SQLException {
			int i = 1;
			rs.getString(i++); // ID
			String name = rs.getString(i++);
			long size = rs.getLong(i++);
			String mimeType = rs.getString(i++);
			file.setName(name);
			file.setSize(size);
			file.setMimeType(mimeType);
			return file;
		}

	}

	private class CollectionMapper implements RowMapper<Object> {

		private D3EPrimaryCache cache;
		private DField field;

		public CollectionMapper(D3EPrimaryCache cache, DField field) {
			this.cache = cache;
			this.field = field;
		}

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			switch (field.getType()) {
			case PrimitiveCollection:
				return readPrimitive(field, rs, 1);
			case InverseCollection:
			case ReferenceCollection:
				if (field.getReference().getIndex() == SchemaConstants.DFile) {
					return cache.getOrCreateDFile(rs.getString(1));
				}
				long id = rs.getLong(1);
				int refType = rs.getInt(2);
				return cache.getOrCreate(schema.getType(refType), id);
			}
			return null;
		}
	}

	private Object readPrimitive(DField df, ResultSet rs, int i) throws SQLException {
		FieldPrimitiveType pt = df.getPrimitiveType();
		switch (pt) {
		case Boolean:
			return rs.getBoolean(i);
		case Date:
			Date date = rs.getDate(i);
			return date == null ? null : date.toLocalDate();
		case DateTime:
			Timestamp timestamp = rs.getTimestamp(i);
			return timestamp == null ? null : timestamp.toLocalDateTime();
		case Double:
			return rs.getDouble(i);
		case Duration:
			throw new UnsupportedOperationException();
		case Enum:
			String str = rs.getString(i);
			DModel<?> enmType = schema.getType(df.getEnumType());
			DField<?, ?> field = enmType.getField(str);
			if (field == null) {
				field = enmType.getField(0);
			}
			Object val = field.getValue(null);
			return val;
		case Integer:
			return rs.getLong(i);
		case String:
			return rs.getString(i);
		case Time:
			Time time = rs.getTime(i);
			return time == null ? null : time.toLocalTime();
		default:
			throw new UnsupportedOperationException();
		}
	}

	private class EntityManagerImpl implements IEntityManager {

		private D3EPrimaryCache cache;

		public EntityManagerImpl(D3EPrimaryCache cache) {
			if(cache == null) {
				cache = new D3EPrimaryCache(); 
			}
			this.cache = cache;
		}

		@Override
		public void createId(DatabaseObject obj) {
			if (obj.getId() == 0l) {
				long id = jdbcTemplate.getJdbcTemplate().queryForObject("select nextval('_d3e_sequence')",
						(rs, num) -> rs.getLong(1));
				D3ELogger.info("NextSeq: " + id);
				obj.setId(id);
			}
			DModel<?> type = schema.getType(obj._typeIdx());
			for (DField df : type.getFields()) {
				if (df.isChild()) {
					DModel ref = df.getReference();
					if (ref.isDocument()) {
						continue;
					}
					if (!ref.isEmbedded()) {
						if (df.getType() == FieldType.Reference) {
							DatabaseObject child = (DatabaseObject) df.getValue(obj);
							if (child != null) {
								createId(child);
							}
						} else if (df.getType() == FieldType.ReferenceCollection) {
							List list = (List) df.getValue(obj);
							for (Object o : list) {
								createId((DatabaseObject) o);
							}
						}
					}
				}
			}
		}

		@Override
		public void persistFile(DFile file) {
			D3EQuery query = queryBuilder.generateCreateDFileQuery(file);
			execute(query);
		}

		@Override
		public void persist(DatabaseObject entity) {
			if (entity.getSaveStatus() == DBSaveStatus.New) {
				entity.setSaveStatus(DBSaveStatus.Saved);
				D3EQuery query = queryBuilder.generateCreateQuery(schema.getType(entity._typeIdx()), entity);
				execute(query);
			} else {
				BitSet _changes = entity._changes().changes;
				if (_changes.isEmpty()) {
					return;
				}
				D3EQuery query = queryBuilder.generateUpdateQuery(schema.getType(entity._typeIdx()), entity);
				execute(query);
			}
		}

		@Override
		public void delete(DatabaseObject entity) {
			if (entity.saveStatus != DBSaveStatus.Saved) {
				return;
			}
			D3EQuery query = queryBuilder.generateDeleteQuery(schema.getType(entity._typeIdx()), entity);
			execute(query);
		}

		@Override
		public <T> T find(int type, long id) {
			DModel<?> dm = schema.getType(type);
			return (T) load(dm, id);
		}

		private DatabaseObject load(DModel<?> dm, long id) {
			return cache.getOrCreate(dm, id);
		}

		@Override
		public <T> T getById(int type, long id) {
			DModel<?> dm = schema.getType(type);
			StringBuilder sb = new StringBuilder();
			sb.append("select _id from ").append(dm.getTableName()).append(" where _id = ").append(id);
			String query = sb.toString();
			D3ELogger.info("By Id: type: " + type + ", id: " + id + " , " + query);
			List<Map<String, Object>> list = jdbcTemplate.getJdbcTemplate().queryForList(query);
			if (list.isEmpty()) {
				return null;
			}
			return (T) cache.getOrCreate(dm, id);
		}

		@Override
		public <T> List<T> findAll(int type) {
			// TODO Auto-generated method stub
			throw new RuntimeException();
		}

		private void execute(D3EQuery query) {
			if (query == null) {
				return;
			}
			if (query.pre != null) {
				execute(query.pre);
			}
			if (query.query != null) {
				String q = query.query;
				List<Object> args = query.args;
				D3ELogger.info("Insert/Update: " + q);
				Object[] argsArray = new Object[args.size()];
				for (int i = 0; i < args.size(); i++) {
					Object arg = args.get(i);
					if (arg instanceof DatabaseObject) {
						long id = ((DatabaseObject) arg).getId();
						if (id == 0l) {
							throw new RuntimeException(
									"object references an unsaved instance - save the instance before flushing");
						}
						arg = id;
					}
					if (arg instanceof DFile) {
						String id = ((DFile) arg).getId();
						if (id == null || id.isEmpty()) {
							throw new RuntimeException(
									"object references an unsaved instance - save the instance before flushing");
						}
						arg = id;
					}
					argsArray[i] = arg;
				}
				jdbcTemplate.getJdbcTemplate().update(q, argsArray);
			}
			if (query.next != null) {
				execute(query.next);
			}
		}

		@Override
		public Query createNativeQuery(String sql) {
			return new QueryImpl(schema, cache, jdbcTemplate, sql);
		}

		@Override
		public void unproxy(DatabaseObject obj) {
			DModel<?> type = schema.getType(obj._typeIdx());
			List<RowField> selectedFields = new ArrayList<>();
			String query = queryBuilder.generateSelectAllQuery(type, selectedFields, obj.getId());
			// D3ELogger.info("Unproxy Object: " + obj.getId() + " : " + query);
			jdbcTemplate.getJdbcTemplate().query(query, new SingleObjectMapper(cache, obj, type, selectedFields));
		}

		@Override
		public void unproxyCollection(D3EPersistanceList<?> list) {
			DBObject master = list.getMaster();
			DModel<?> type = schema.getType(master._typeIdx());
			DField<?, ?> field = type.getField(list.getField());
			String query = queryBuilder.generateSelectCollectionQuery(type, field, master.getId());
			// D3ELogger.info("Unproxy Collection: " + master.getId() + ", " +
			// field.getName() + " : " + query);
			List<Object> result = jdbcTemplate.getJdbcTemplate().query(query, new CollectionMapper(cache, field));
			list._unproxy(result);
		}

		@Override
		public void unproxyDFile(DFile file) {
			String query = queryBuilder.generateSelectDFileQuery(file);
			// D3ELogger.info("Unproxy DFile: " + file.getId() + " : " + query);
			jdbcTemplate.getJdbcTemplate().query(query, new DFileMapper(cache, file));
		}

		@Override
		public D3EPrimaryCache getCache() {
			return cache;
		}		
	}
}

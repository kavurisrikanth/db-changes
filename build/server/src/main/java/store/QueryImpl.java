package store;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import d3e.core.D3ELogger;
import d3e.core.ListExt;
import gqltosql.schema.DModel;
import gqltosql.schema.IModelSchema;

public class QueryImpl implements Query {

	private NamedParameterJdbcTemplate template;
	private String sql;
	private Map<String, Object> values;
	private D3EPrimaryCache cache;
	private IModelSchema schema;

	public QueryImpl(IModelSchema schema, D3EPrimaryCache cache, NamedParameterJdbcTemplate template, String sql) {
		this.schema = schema;
		this.cache = cache;
		this.template = template;
		this.sql = sql;
		this.values = new HashMap<>();
	}

	private class ObjectArrayRowMapper implements RowMapper<Object> {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			int cols = rs.getMetaData().getColumnCount();
			Object[] arr = new Object[cols];
			for (int i = 0; i < cols; i++) {
				arr[i] = rs.getObject(i + 1);
			}
			return arr.length == 1 ? arr[0] : arr;
		}

	}

	@Override
	public <T> T getObjectFirstResult(int type) {
		List list = getResultList();
		if (list.isEmpty()) {
			return null;
		}
		long id = (long) list.get(0);
		DModel<?> dm = schema.getType(type);
		return (T) cache.getOrCreate(dm, id);
	}

	@Override
	public <T> List<T> getObjectResultList(int type) {
		List list = getResultList();
		DModel<?> dm = schema.getType(type);
		return ListExt.map(list, i -> cache.getOrCreate(dm, (long) i));
	}

	@Override
	public List getResultList() {
		//D3ELogger.info("Query: " + sql + "; vals: " + values);
		return template.query(sql, values, new ObjectArrayRowMapper());
	}

	@Override
	public Object getSingleResult() {
		List list = getResultList();
		if (list.isEmpty()) {
			throw new NoResultException();
		}
		if (list.size() > 1) {
			throw new NonUniqueResultException();
		}
		return list.get(0);
	}

	@Override
	public int executeUpdate() {
		return template.update(sql, values);
	}

	@Override
	public Query setMaxResults(int maxResult) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMaxResults() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFirstResult(int startPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFirstResult() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Query setHint(String hintName, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getHints() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Query setParameter(Parameter<T> param, T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Object value) {
		this.values.put(name, value);
		return this;
	}

	@Override
	public Query setParameter(String name, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(String name, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Calendar value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setParameter(int position, Date value, TemporalType temporalType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Parameter<?>> getParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<?> getParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(String name, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<?> getParameter(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Parameter<T> getParameter(int position, Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBound(Parameter<?> param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getParameterValue(Parameter<T> param) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getParameterValue(String name) {
		return values.get(name);
	}

	@Override
	public Object getParameterValue(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setFlushMode(FlushModeType flushMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FlushModeType getFlushMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query setLockMode(LockModeType lockMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public LockModeType getLockMode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		throw new UnsupportedOperationException();
	}

}

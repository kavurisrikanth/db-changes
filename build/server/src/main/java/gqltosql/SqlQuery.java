package gqltosql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import d3e.core.ListExt;

public class SqlQuery {

	private QueryReader reader;
	private Set<String> columnAliases = new HashSet<>();
	private List<String> columns = new ArrayList<>();
	private List<String> joins = new ArrayList<>();
	private List<String> where = new ArrayList<>();
	private List<String> groupBys = new ArrayList<>();

	public SqlQuery(QueryReader reader) {
		this.reader = reader;
	}

	public void setFrom(String tableName, String alias) {
		joins.add(tableName + " " + alias);
	}

	public void addSelection(String column, String field, QueryTypeReader reader) {
		reader.add(field, columns.size());
		columns.add(column + " as " + prepareFieldAlias(field, 0));
	}

	private String prepareFieldAlias(String field, int i) {
		String alias;
		if (i == 0) {
			alias = field;
		} else {
			alias = field + "_" + i;
		}
		if (columnAliases.contains(alias)) {
			return prepareFieldAlias(field, i + 1);
		}
		columnAliases.add(alias);
		return alias;
	}

	public QueryReader addRefSelection(String column, String field, QueryTypeReader reader) {
		QueryReader r = reader.addRef(field, columns.size());
		columns.add(column + " as " + prepareFieldAlias(field, 0));
		return r;
	}

	public void addJoin(String tableName, String join, String on) {
		joins.add(tableName + " " + join + " on " + on);
	}

	public void addWhere(String condition) {
		where.add(condition);
	}

	public void addGroupBy(String column) {
		if (groupBys.contains(column)) {
			return;
		}
		groupBys.add(column);
	}

	public String createSQL() {
		StringBuilder b = new StringBuilder();
		b.append("select ").append(ListExt.join(columns, ", ")).append(" from ")
				.append(ListExt.join(joins, " left join "));
		if (!where.isEmpty()) {
			b.append(" where ").append(ListExt.join(where, " and "));
		}
		if (!groupBys.isEmpty()) {
			b.append(" group by ").append(ListExt.join(groupBys, ", "));
		}
		return b.toString();
	}

	public QueryReader getReader() {
		return reader;
	}
}

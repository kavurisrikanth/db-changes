package d3e.core;

import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class D3ELogger {
	private static final Logger LOG = LogManager.getLogger(D3ELogger.class);
	public static boolean DISABLE_LOG;

	private static boolean showSql;

	private static boolean showGraphql;

	public static boolean isShowSql() {
		return showSql;
	}

	public static void setShowSql(boolean showSql) {
		D3ELogger.showSql = showSql;
	}

	public static void setShowGraphql(boolean showGraphql) {
		D3ELogger.showGraphql = showGraphql;
	}

	public static void info(String msg) {
		if (DISABLE_LOG) {
			System.err.println(msg);
		} else {
			LOG.info(msg);
		}
	}

	public static void error(String msg) {
		if (DISABLE_LOG) {
			System.err.println(msg);
		} else {
			LOG.error(msg);
		}
	}

	public static void printStackTrace(Throwable t) {
		if (DISABLE_LOG) {
			t.printStackTrace(System.err);
		} else {
			LOG.error(t.getMessage(), t);
		}
	}

	public static void displaySql(String path, String sql, Set<Long> ids) {
		if (!D3ELogger.showSql) {
			return;
		}

		boolean hasPath = path != null && !path.isEmpty();
		boolean hasSql = sql != null && !sql.isEmpty();
		boolean hasIds = ids != null && !ids.isEmpty();
		if (!hasPath && !hasSql && !hasIds) {
			return;
		}
		StringBuilder b = new StringBuilder("GQL TO SQL: \n");
		System.out.println();
		System.out.println("*** SQL ***");
		if (hasPath) {
			b.append("Path: " + path);
		}
		if (hasSql) {
			b.append("Execute SQL: " + sql);
		}
		if (hasIds) {
			b.append("Ids: " + ids);
		}
		info(b.toString());
	}

	public static void displaySql(String sql, Map<String, Object> params) {
		if (!D3ELogger.showSql) {
			return;
		}

		boolean hasSql = sql != null && !sql.isEmpty();
		boolean hasParams = params != null && !params.isEmpty();
		if (!hasSql && !hasParams) {
			return;
		}
		StringBuilder b = new StringBuilder("DQ SQL: \n");
		if (hasSql) {
			b.append("Execute SQL: " + sql);
		}
		if (hasParams) {
			b.append("Parameters: " + params);
		}
		info(b.toString());
	}

	public static void displayGraphQL(String field, String op, JSONObject variables) {
		if (!D3ELogger.showGraphql) {
			return;
		}
		boolean hasOp = op != null && !op.isEmpty();
		boolean hasVars = variables != null && variables.length() > 0;
		if (!hasOp && !hasVars) {
			return;
		}
		StringBuilder b = new StringBuilder("GraphQl: \n");
		if (hasOp) {
			b.append("Operation: " + op);
		}
		if (hasVars) {
			b.append("Variables: " + variables);
		}
		info(b.toString());
	}

	public static void query(String sql, Query query) {
		if (!D3ELogger.isShowSql()) {
			return;
		}
		Map<String, Object> params = MapExt.Map();
		query.getParameters().forEach(p -> {
			String name = p.getName();
			Object value;
			try {
				value = query.getParameterValue(name);
			} catch (Exception e) {
				value = e.getMessage();
			}
			params.put(name, value);
		});
		D3ELogger.displaySql(sql, params);
	}
}

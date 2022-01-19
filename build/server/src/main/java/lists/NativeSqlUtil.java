package lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gqltosql.SqlRow;
import gqltosql2.OutObject;
import gqltosql2.OutObjectList;
import store.DatabaseObject;
import store.IEntityManager;

public class NativeSqlUtil {

	public static Set<Long> getAllIds(Iterable<NativeObj> rows) {
		Set<Long> ids = new HashSet<>();
		for (NativeObj s : rows) {
			ids.add(s.getId());
		}
		return ids;
	}

	public static <T extends DatabaseObject> List<T> sort(List<T> objs, List<NativeObj> rows) {
		Map<Long, T> objById = objs.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
		List<T> result = rows.stream().map(r -> objById.get(r.getId())).collect(Collectors.toList());
		return result;
	}

	public static JSONArray sort(JSONArray objs, List<NativeObj> rows) throws JSONException {
		Map<Long, JSONObject> objById = new HashMap<>();
		for (int i = 0; i < objs.length(); i++) {
			JSONObject o = objs.getJSONObject(i);
			objById.put(o.getLong("id"), o);
		}
		JSONArray result = new JSONArray();
		for (NativeObj o : rows) {
			result.put(objById.get(o.getId()));
		}
		return result;
	}

	public static List<NativeObj> createNativeObj(List<?> list, int id) {
		return list.stream().map(o -> new NativeObj(o, id)).collect(Collectors.toList());
	}

	public static JSONArray getJSONArray(List<NativeObj> listRef, List<SqlRow> sqlDecl1) throws JSONException {
		JSONArray list = new JSONArray();
		for (NativeObj obj : listRef) {
			SqlRow _o1_0Sql = new SqlRow();
			_o1_0Sql.put("id", obj.getId());
			sqlDecl1.add(_o1_0Sql);
			list.put(_o1_0Sql);
		}
		return list;
	}

	public static JSONObject getJSONObject(NativeObj ref, List<SqlRow> sqlDecl0) throws JSONException {
		if (ref == null) {
			return null;
		}
		SqlRow row = new SqlRow();
		row.put("id", ref.getId());
		sqlDecl0.add(row);
		return row;
	}

	public static OutObject getOutObject(NativeObj ref, int type, List<OutObject> sqlDecl0) {
		if (ref == null) {
			return null;
		}
		OutObject row = new OutObject();
		row.addType(type);
		row.setId(ref.getId());
		sqlDecl0.add(row);
		return row;
	}
	
	public static OutObjectList getOutObjectList(List<NativeObj> listRef, int type, List<OutObject> sqlDecl1) throws JSONException {
		OutObjectList list = new OutObjectList();
		for (NativeObj obj : listRef) {
			OutObject _o1_0Sql = new OutObject();
			_o1_0Sql.setId(obj.getId());
			_o1_0Sql.addType(type);
			sqlDecl1.add(_o1_0Sql);
			list.add(_o1_0Sql);
		}
		return list;
	}

	public static <T> List<T> getList(IEntityManager em, List<NativeObj> listRef, int type) {
		List<T> res = new ArrayList<T>();
		listRef.forEach(r -> res.add(em.find(type, r.getId())));
		return res;
	}

	public static <T> T get(IEntityManager em, NativeObj ref, int type) {
		if (ref == null) {
			return null;
		}
		return em.find(type, ref.getId());
	}

	public static <R> List<NativeObj> groupBy(List<NativeObj> rows, Function<NativeObj, R> groupBy,
			Function<NativeObj, NativeObj> group, BiFunction<NativeObj, NativeObj, NativeObj> map) {
		Collector<NativeObj, ?, List<NativeObj>> toList = Collectors.toList();
		if (group != null) {
			toList = Collectors.mapping(group, toList);
		}
		Map<R, List<NativeObj>> groups = rows.stream().collect(Collectors.groupingBy(groupBy, toList));
		List<NativeObj> result = new ArrayList<>();
		groups.forEach((k, v) -> result.add(map.apply(v.get(0), combine(v))));
		return result;
	}

	public static NativeObj combine(List<NativeObj> rows) {
		if (rows.isEmpty()) {
			return null;
		}
		List<Object>[] row = new List[rows.get(0).size()];
		for (int i = 0; i < row.length; i++) {
			row[i] = new ArrayList<>();
		}
		for (NativeObj o : rows) {
			Object[] one = o.getRow();
			for (int j = 0; j < one.length; j++) {
				row[j].add(one[j]);
			}
		}
		return new NativeObj(row, -1);
	}
}

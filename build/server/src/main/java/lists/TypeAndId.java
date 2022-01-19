package lists;

import java.util.List;

import d3e.core.ListExt;
import store.DBObject;

public class TypeAndId {
	public TypeAndId(int type, long id) {
		this.type = type;
		this.id = id;
	}

	public int type;
	public long id;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeAndId) {
			TypeAndId other = (TypeAndId) obj;
			return other.type == type && other.id == id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.type ^ (int) this.id;
	}

	public static TypeAndId from(DBObject obj) {
		if (obj == null) {
			return null;
		}
		return new TypeAndId(obj._typeIdx(), obj.getId());
	}

	public static List<TypeAndId> fromList(List<? extends DBObject> list) {
		return ListExt.map(list, a -> new TypeAndId(a._typeIdx(), a.getId()));
	}
}

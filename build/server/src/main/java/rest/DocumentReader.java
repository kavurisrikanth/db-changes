package rest;

import java.util.Map;

import org.json.JSONObject;

import d3e.core.DFile;
import store.DatabaseObject;
import store.EntityHelperService;

public class DocumentReader extends JSONInputContext {

	private IdGenerator id;

	public DocumentReader(JSONObject json, EntityHelperService helperService, Map<Long, Object> inputObjectCache,
			Map<String, DFile> files, JSONObject variables, IdGenerator id) {
		super(json, helperService, inputObjectCache, files, variables);
		this.id = id;
	}

	@Override
	protected <T> T readObject(String type, boolean readFully) {
		T obj = super.readObject(type, readFully);
		if (obj instanceof DatabaseObject) {
			((DatabaseObject) obj).setId(id.next());
		}
		return obj;
	}

	@Override
	protected JSONInputContext createReadContext(JSONObject json) {
		return new DocumentReader(json, helperService, inputObjectCache, files, json, id);
	}

	static class IdGenerator {
		private long start;

		public IdGenerator(long start) {
			this.start = start;
		}

		public long next() {
			return ++start;
		}
	}
}

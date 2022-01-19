package store;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import d3e.core.D3ETempResourceHandler;
import d3e.core.DFile;

@Service
public class Database {
	private static Database instance;
	@Autowired
	private EntityMutator mutator;
	@Autowired
	private D3ETempResourceHandler saveHandler;

	@PostConstruct
	public void init() {
		instance = this;
	}

	public static Database get() {
		return instance;
	}

	public void save(Object obj) {
		if (!(obj instanceof DatabaseObject)) {
			return;
		}
		mutator.save((DatabaseObject) obj, true);
	}

	public void saveAll(List<Object> objects) {
		objects.forEach(obj -> save(obj));
	}

	public void update(Object obj) {
		if (!(obj instanceof DatabaseObject)) {
			return;
		}
		mutator.update((DatabaseObject) obj, true);
	}

	public void updateAll(List<Object> objects) {
		objects.forEach(obj -> update(obj));
	}

	public void delete(Object obj) {
		if (!(obj instanceof DatabaseObject)) {
			return;
		}
		mutator.delete((DatabaseObject) obj, true);
	}

	public void deleteAll(List<Object> objects) {
		objects.forEach(obj -> delete(obj));
	}

	public void preUpdate(DatabaseObject obj) {
		mutator.preUpdate(obj);
	}

	public void preDelete(DatabaseObject obj) {
		mutator.preDelete(obj);
	}

	public static void markDirty(DatabaseObject obj, boolean inverse) {
		Database database = get();
		if (database == null) {
			return;
		}
		EntityMutator mutator = database.mutator;
		if (mutator == null) {
			return;
		}
		mutator.markDirty(obj, inverse);
	}

	public static void collectCreatableReferences(List<Object> _refs, DatabaseObject obj) {
		if (obj != null) {
			obj.collectCreatableReferences(_refs);
		}
	}

	public static void collectCollctionCreatableReferences(List<Object> _refs, List<? extends DatabaseObject> coll) {
		coll.forEach(o -> collectCreatableReferences(_refs, o));
	}

	public void unproxy(DatabaseObject obj) {
		mutator.unproxy(obj);
	}

	public void unproxyCollection(D3EPersistanceList<?> list) {
		mutator.unproxyCollection(list);
	}

	public void saveDFile(DFile file) {
		mutator.saveDFile(file);
	}

	public void unproxyDFile(DFile file) {
		mutator.unproxyDFile(file);
	}

	public DFile createFileWithContent(String name, String content) {
		DFile file = saveHandler.save(name, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
		return file;
	}
}

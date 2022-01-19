package store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import d3e.core.DFile;
import gqltosql.schema.DField;
import gqltosql.schema.DModel;
import gqltosql.schema.FieldType;

public class D3EPrimaryCache {

	private Map<Integer, Map<Long, DatabaseObject>> data = new HashMap<>();
	private Map<String, DFile> files = new HashMap<>();

	public DatabaseObject get(int type, long id) {
		Map<Long, DatabaseObject> byType = data.get(type);
		if (byType == null) {
			return null;
		}
		return byType.get(id);
	}

	public void add(DatabaseObject ins, int type) {
		Map<Long, DatabaseObject> byType = data.get(type);
		if (byType == null) {
			byType = new HashMap<>();
			data.put(type, byType);
		}
		byType.put(ins.getId(), ins);
	}

	public DatabaseObject getOrCreate(DModel<?> dm, long id) {
		if (id == 0l) {
			return null;
		}
		DatabaseObject obj = get(dm.getIndex(), id);
		if (obj == null) {
			DatabaseObject ins = (DatabaseObject) dm.newInstance();
			ins.setId(id);
			ins._markProxy();
			ins.postLoad();
			ins.setSaveStatus(DBSaveStatus.Saved);
			markCollectionsAsProxy(dm, ins);
			add(ins, dm.getIndex());
			return ins;
		}
		return obj;
	}

	private void markCollectionsAsProxy(DModel<?> dm, DatabaseObject ins) {
		for (DField df : dm.getFields()) {
			FieldType type = df.getType();
			switch (type) {
			case InverseCollection:
			case PrimitiveCollection:
			case ReferenceCollection:
				List list = (List) df.getValue(ins);
				if (list instanceof D3EPersistanceList) {
					((D3EPersistanceList) list)._markProxy();
				}
			}
		}
		if (dm.getParent() != null) {
			markCollectionsAsProxy(dm.getParent(), ins);
		}
	}

	public DFile getOrCreateDFile(String id) {
		if (id == null) {
			return null;
		}
		DFile file = files.get(id);
		if (file == null) {
			file = new DFile();
			file.setId(id);
			file._markProxy();
			files.put(id, file);
		}
		return file;
	}

}

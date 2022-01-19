package store;

import java.util.List;

import d3e.core.DFile;

public interface IEntityManager {

	public void persist(DatabaseObject entity);

	public void delete(DatabaseObject entity);

	public <T> T find(int type, long id);

	public <T> T getById(int type, long id);

	public <T> List<T> findAll(int type);

	public void unproxy(DatabaseObject obj);

	public void unproxyCollection(D3EPersistanceList<?> list);

	public void persistFile(DFile o);

	public Query createNativeQuery(String sql);

	public void unproxyDFile(DFile file);
	
	public void createId(DatabaseObject obj);

	public Object getCache();

}

package repository.jpa;

import java.util.List;

import store.DatabaseObject;

public interface D3ERepository<T extends DatabaseObject> {
	public T findById(long id);

	public T getOne(long id);

	public List<T> findAll();
}

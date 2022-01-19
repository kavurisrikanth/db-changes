package store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import d3e.core.DFile;
import d3e.core.TransactionManager;
import models.CreatableObject;

@Component
public class DBEntityMutator implements EntityMutator {

	private class Context {
		private Set<DatabaseObject> saveQueue;
		private Set<Object> deleteQueue;
		private Set<DatabaseObject> dirtyQueue = new HashSet<>();
		private final Map<DatabaseObject, DatabaseObject> clones = new HashMap<>();
		private ValidationContextImpl context;
		private Set<DatabaseObject> externalObjects = new HashSet<>();
		private List<DatabaseObject> persistObjects = new ArrayList<>();
		private List<DatabaseObject> removeObjects = new ArrayList<>();
		private List<Object> actionsDone = new ArrayList<>();
		private List<DFile> persistFiles = new ArrayList<>();
	}

	private final D3EEntityManagerProvider manager;

	private ObjectFactory<EntityHelperService> helperService;

	static ThreadLocal<Context> threadLocalMutator = new ThreadLocal<>();

	@Autowired
	public DBEntityMutator(D3EEntityManagerProvider manager, ObjectFactory<EntityHelperService> helperService) {
		this.manager = manager;
		this.helperService = helperService;
	}

	@Override
	public void saveDFile(DFile file) {
		Context ctx = threadLocalMutator.get();
		if (ctx == null) {
			return;
		}
		ctx.persistFiles.add(file);
	}

	@Override
	public void markDirty(DatabaseObject obj, boolean inverse) {
		if (!obj._isEntity()) {
			if (obj.getId() == 0l || obj.isOld) {
				return;
			}
			TransactionManager tm = TransactionManager.get();
			if (tm != null && !obj.transientModel()) {
				tm.update(obj);
			}
			return;
		}
		if (obj.isNew()) {
			return;
		}
		if (inverse) {
			TransactionManager tm = TransactionManager.get();
			if (tm != null && !obj.transientModel()) {
				tm.update(obj);
			}
			return;
		}
		Context ctx = getContext();
		DatabaseObject master = findMaster(obj);
		if (master == null || ctx.saveQueue.contains(master) || ctx.deleteQueue.contains(master)) {
			return;
		}
		ctx.dirtyQueue.add(master);
	}

	private CreatableObject findMaster(DatabaseObject obj) {
		if (obj instanceof CreatableObject) {
			return (CreatableObject) obj;
		}
		Object _masterObject = obj._masterObject();
		if (_masterObject == null) {
			return null;
		}
		return findMaster((DatabaseObject) _masterObject);
	}

	@Override
	public void unproxyDFile(DFile file) {
		manager.get().unproxyDFile(file);
	}

	@Override
	public void unproxy(DatabaseObject obj) {
		manager.get().unproxy(obj);
	}

	@Override
	public void unproxyCollection(D3EPersistanceList<?> list) {
		manager.get().unproxyCollection(list);
	}

	public void save(DatabaseObject obj, boolean internal) {
		saveOrUpdate(obj, internal);
	}

	public void update(DatabaseObject obj, boolean internal) {
		saveOrUpdate(obj, internal);
	}

	public void saveOrUpdate(DatabaseObject obj, boolean internal) {
		boolean created = createContextIfNotExist();
		Context ctx = getContext();
		if (ctx.deleteQueue.contains(obj)) {
			return;
		}
		if (!internal) {
			ctx.externalObjects.add(obj);
		}
		if (obj.isInConvert() || obj.isDeleted()) {
			return;
		}
		obj.updateMasters(a -> {
		});
		if (ctx.saveQueue.contains(obj)) {
			return;
		}
		ctx.saveQueue.add(obj);
		ctx.dirtyQueue.remove(obj);
		if (obj._isEntity() && !ctx.persistObjects.contains(obj)) {
			manager.get().createId(obj);
			ctx.persistObjects.add(obj);
		}
		preUpdate(obj);
		if (!ctx.actionsDone.contains(obj)) {
			ctx.actionsDone.add(obj);
		}
		List<Object> refs = new ArrayList<>();
		obj.collectCreatableReferences(refs);
		refs.stream().filter(o -> o instanceof DatabaseObject).filter(o -> !ctx.saveQueue.contains(o))
				.map(o -> (DatabaseObject) o).filter(o -> isActionDone(ctx, o)).forEach(o -> ctx.dirtyQueue.add(o));
		ctx.saveQueue.remove(obj);
		if (created) {
			finish();
		}
	}

	private boolean isActionDone(Context ctx, DatabaseObject o) {
		return o.isNew() && !ctx.actionsDone.contains(o);
	}

	@Override
	public void preUpdate(DatabaseObject entity) {
		EntityHelper<DatabaseObject> helper = (EntityHelper<DatabaseObject>) getHelper(
				entity.getClass().getSimpleName());
		if (helper != null) {
			Context ctx = getContext();
			helper.setDefaults(entity);
			helper.compute(entity);
			if (isActionDone(ctx, entity)) {
				helper.onCreate(entity, ctx.externalObjects.contains(entity));
			} else {
				if (entity.canCreateOldObject()) {
					entity.createOldObject();
				}
				helper.onUpdate(entity, ctx.externalObjects.contains(entity));
			}
			// Removing to have old value in SubscriptionHelpers
			// entity.recordOld(new CloneContext(true));
			try {
				if (isActionDone(ctx, entity)) {
					helper.validateOnCreate(entity, ctx.context);
				} else {
					helper.validateOnUpdate(entity, ctx.context);
				}
			} catch (RuntimeException e) {
				ctx.context.markServerError(true);
				ctx.context.addThrowableError(e, "Something went wrong.");
			}
			if (ctx.context.hasErrors()) {
				throw ValidationFailedException.fromValidationContext(ctx.context);
			}
		}
		if (entity.isNew()) {
			TransactionManager.get().add(entity);
		} else {
			TransactionManager.get().update(entity);
		}
	}

	@Override
	public void preDelete(DatabaseObject entity) {
		entity.setDeleted(true);
		EntityHelper<DatabaseObject> helper = (EntityHelper<DatabaseObject>) getHelper(
				entity.getClass().getSimpleName());
		Context ctx = getContext();
		if (helper != null) {
			helper.validateOnDelete(entity, ctx.context);
		}
		if (ctx.context.hasErrors()) {
			throw ValidationFailedException.fromValidationContext(ctx.context);
		}
		TransactionManager.get().delete(entity);
		if (helper != null) {
			helper.onDelete(entity, ctx.externalObjects.contains(entity), ctx.context);
		}
		if (ctx.context.hasErrors()) {
			throw ValidationFailedException.fromValidationContext(ctx.context);
		}
	}

	@Override
	public void clear() {
		clearContext();
	}

	private void clearContext() {
		threadLocalMutator.set(null);
	}

	private boolean createContextIfNotExist() {
		Context context = threadLocalMutator.get();
		if (context != null) {
			return false;
		}
		Context ctx = new Context();
		ctx.saveQueue = new HashSet<DatabaseObject>();
		ctx.deleteQueue = new HashSet<Object>();
		ctx.context = new ValidationContextImpl(this);
		threadLocalMutator.set(ctx);
		return true;
	}

	private Context getContext() {
		Context context = threadLocalMutator.get();
		if (context != null) {
			return context;
		}
		Context ctx = new Context();
		ctx.saveQueue = new HashSet<DatabaseObject>();
		ctx.deleteQueue = new HashSet<Object>();
		ctx.context = new ValidationContextImpl(this);
		threadLocalMutator.set(ctx);
		return ctx;
	}

	public boolean finish() {
		try {
			return doFinish();
		} finally {
			clearContext();
		}
	}

	private boolean doFinish() {
		Context ctx = getContext();
		if (ctx.dirtyQueue.isEmpty()) {
			IEntityManager em = manager.get();
			ctx.persistFiles.forEach(o -> em.persistFile(o));
			ctx.persistObjects.forEach(o -> em.persist(o));
			ctx.removeObjects.stream().forEach(o -> em.delete(o));
			return true;
		}
		Set<DatabaseObject> dirtySet = new HashSet<>(ctx.dirtyQueue);
		ctx.dirtyQueue.clear();
		for (DatabaseObject obj : dirtySet) {
			saveOrUpdate(obj, true);
		}
		return doFinish();
	}

	public <T extends DatabaseObject> boolean delete(T obj, boolean internal) {
		boolean created = createContextIfNotExist();
		Context ctx = getContext();
		if (ctx.deleteQueue.contains(obj)) {
			return false;
		}
		ctx.deleteQueue.add(obj);
		boolean done = deleteInternal(obj, internal);
		ctx.deleteQueue.remove(obj);
		if (created) {
			finish();
		}
		return done;
	}

	private <T extends DatabaseObject> boolean deleteInternal(T entity, boolean internal) {
		if (entity.isDeleted()) {
			return false;
		}
		Context ctx = getContext();
		if (!internal) {
			ctx.externalObjects.add(entity);
		}
		ctx.persistObjects.remove(entity);
		preDelete(entity);
		if (entity._isEntity() && !ctx.removeObjects.contains(entity)) {
			ctx.removeObjects.add(entity);
		}
		return true;
	}

	public <T extends DatabaseObject> void peformDeleteOrphan(Collection<T> oldList, Collection<T> newList) {
		List<T> deletedList = new ArrayList<T>();
		for (T t : oldList) {
			if (!newList.contains(t)) {
				deletedList.add(t);
			}
		}
		oldList.clear();
		oldList.addAll(newList);
		for (T t : deletedList) {
			this.delete(t, true);
		}
	}

	public <T extends DatabaseObject, H extends EntityHelper<T>> H getHelper(String fullType) {
		return (H) this.helperService.getObject().get(fullType);
	}

	public <T extends DatabaseObject, H extends EntityHelper<T>> H getHelperByInstance(Object fullType) {
		return (H) this.helperService.getObject().get(fullType.getClass().getSimpleName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void processOnLoad(Object entity) {
		EntityHelper helper = getHelper(entity.getClass().getSimpleName());
		Context ctx = getContext();
		if (helper != null) {
			if (ctx.clones.containsKey(entity)) {
				return;
			}
			Object clone = helper.clone((DatabaseObject) entity);
			ctx.clones.put((DatabaseObject) entity, (DatabaseObject) clone);
		}

	}

	@Override
	public boolean isInDelete(Object obj) {
		Context ctx = getContext();
		return ctx.deleteQueue.contains(obj) || ctx.removeObjects.contains(obj);
	}
}

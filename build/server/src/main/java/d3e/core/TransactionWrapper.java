package d3e.core;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;

import rest.ws.DataChangeTracker;
import store.D3EEntityManagerProvider;
import store.DataStoreEvent;
import store.EntityMutator;

@Component
public class TransactionWrapper {

	@Autowired
	private D3ESubscription subscription;

	@Autowired
	private TransactionDeligate deligate;

	@Autowired
	private D3EEntityManagerProvider managerProvider;

	@Autowired
	private DataChangeTracker changeTracker;

	@Autowired
	private EntityMutator mutator;

	public void doInTransaction(TransactionDeligate.ToRun run) throws ServletException, IOException {
		boolean created = createTransactionManager();
		boolean success = false;
		try {
			if (created) {
				managerProvider.create(null);
			}
			deligate.run(created, run);
			if (created) {
				publishEvents();
			}
			success = true;
		} catch (UnexpectedRollbackException e) {
			if (created) {
				D3ELogger.info("Transaction failed");
			}
			throw e;
		} catch (Exception e) {
			D3ELogger.printStackTrace(e);
			throw new RuntimeException(e);
		} finally {
			if (created) {
				if (!success) {
					TransactionManager manager = TransactionManager.get();
					if (manager != null) {
						manager.clearChanges();
					}
				}
				TransactionManager.remove();
				mutator.clear();
				managerProvider.clear();
			}
		}
	}

	private void publishEvents() throws ServletException, IOException {
		deligate.readOnly(() -> {
			TransactionManager manager = TransactionManager.get();
			TransactionManager.remove();
			createTransactionManager();
			List<DataStoreEvent> changes = manager.getChanges();
			changeTracker.fire(changes);
			changes.forEach(event -> {
				try {
					subscription.handleContextStart(event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			manager.clearChanges();
		});
		if (!TransactionManager.get().isEmpty()) {
			publishEvents();
		}
	}

	private boolean createTransactionManager() {
		TransactionManager manager = TransactionManager.get();
		if (manager == null) {
			manager = new TransactionManager();
			TransactionManager.set(manager);
			return true;
		}
		return false;
	}
}

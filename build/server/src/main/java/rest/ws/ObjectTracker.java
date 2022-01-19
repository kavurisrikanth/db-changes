package rest.ws;

import gqltosql2.Field;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;

public class ObjectTracker implements Cancellable {

	private DataChangeTracker dataChangeTracker;
	private ClientSession session;
	private Field field;
	private Disposable disposable;

	public ObjectTracker(DataChangeTracker dataChangeTracker, ClientSession session, Field field) {
		this.dataChangeTracker = dataChangeTracker;
		this.session = session;
		this.field = field;
	}

	public void init(Object one) {
		disposable = dataChangeTracker.listen(one, field, session);
	}

	@Override
	public void cancel() throws Throwable {
		disposable.dispose();
	}

}

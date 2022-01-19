package rest.ws;

import gqltosql2.Field;
import gqltosql2.OutObject;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;

public class OutObjectTracker implements Cancellable {

	private ClientSession session;
	private Field field;
	private OutObject object;
	private DataChangeTracker dataChangeTracker;
	private Disposable disposable;

	public OutObjectTracker(DataChangeTracker dataChangeTracker, ClientSession session, Field field) {
		this.dataChangeTracker = dataChangeTracker;
		this.session = session;
		this.field = field;
		
	}
	public void init(OutObject object) {
		if(object != null) {
			disposable = dataChangeTracker.listen(object, field, session);			
		}
	}

	@Override
	public void cancel() throws Throwable {
		disposable.dispose();		
	}

}

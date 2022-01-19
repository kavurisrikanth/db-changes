package rest.ws;

import io.reactivex.rxjava3.functions.Cancellable;

public class QueryResult {

	public String type;
	public boolean external;
	public boolean isList;
	public Object value;
	public Cancellable changeTracker;
}

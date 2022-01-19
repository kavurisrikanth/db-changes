package d3e.core;

public class ErrorObjectException extends Throwable {

	private Object error;

	public Object getError() {
		return error;
	}

	public void setError(Object error) {
		this.error = error;
	}

	public ErrorObjectException(Object error) {
		super();
		this.error = error;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

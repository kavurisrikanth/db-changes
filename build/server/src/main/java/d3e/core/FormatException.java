package d3e.core;

public class FormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String source;
	private long offset;

	public FormatException(NumberFormatException e) {
		super(e.getCause());
	}

	public FormatException(String message, String source, long offset) {
		this.message = message;
		this.source = source;
		this.offset = offset;
	}
}

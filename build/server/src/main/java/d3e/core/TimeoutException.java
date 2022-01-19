package d3e.core;

import java.time.Duration;

public class TimeoutException extends java.util.concurrent.TimeoutException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Duration duration;

	public TimeoutException(String message, Duration duration) {
		super(message);
		this.duration = duration;
	}

	public Duration getDuration() {
		return duration;
	}
}

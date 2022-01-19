package d3e.core;

import io.reactivex.rxjava3.core.FlowableEmitter;

public class EventSink<T> {
	private final FlowableEmitter<T> emitor;

	public EventSink(FlowableEmitter<T> emitor) {
		this.emitor = emitor;
	}

	void add(T event) {
		emitor.onNext(event);
	}

	void addError(Object error) {
		if (error instanceof Throwable) {

			emitor.onError((Throwable) error);
		} else {
			emitor.onError(new ErrorObjectException(error));
		}
	}

	void close() {
		emitor.onComplete();
	}
}

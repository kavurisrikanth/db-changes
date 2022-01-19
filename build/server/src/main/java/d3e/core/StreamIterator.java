package d3e.core;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class StreamIterator<T> {
	private Flowable<T> stream;
	private T current;
	private Throwable error;
	private Disposable disposable;
	private FlowableEmitter<Object> trigger;

	public StreamIterator(Flowable<T> stream) {
		this.stream = stream;
		Flowable.create((ss) -> {
			trigger = ss;
		}, BackpressureStrategy.BUFFER).subscribe();
		// TODO
		Flowable<T> flatMapSingle = stream.window(1).flatMapSingle(i -> i.singleOrError());
		this.disposable = stream.subscribe(i -> this.current = i, e -> this.error = e);
	}

	public T getCurrent() {
		return current;
	}

	public Single<Boolean> moveNext() {
		trigger.onNext(null);
		// TODO
		return null;
	}

	public Single<Object> cancel() {
		this.disposable.dispose();
		return null;
	}
}

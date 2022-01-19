package d3e.core;

import java.util.function.Consumer;
import java.util.function.Function;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class StreamSubscription<T> {

	private Disposable subscribe;

	public StreamSubscription(Disposable subscribe) {
		this.subscribe = subscribe;
	}

	public Single<T> cancel() {
		subscribe.dispose();
		return null;
	}

	public void onError(Function handleError) {
		// TODO

	}

	public void onDone(Runnable handleDone) {
		// TODO

	}

	public void pause(Single resumeSignal) {
		// TODO
	}

	public void resume() {
		// TODO
	}

	public static boolean getIsPaused() {
		// TODO
		return true;
	}

	// void onData(void handleData(T data));
	public <T> void onData(Consumer<T> handleData) {
		// TODO

	}
	// Future<E> asFuture<E>([E futureValue]);

	public <E> Single<E> asFuture(E futureValue) {
		// TODO
		return null;

	}

}

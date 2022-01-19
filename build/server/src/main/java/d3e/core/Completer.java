package d3e.core;

import io.reactivex.rxjava3.core.Single;

public class Completer {
	public Completer() {
	}

	public void sync() {
		// TODO
		// return null;
	}

	public static <T> Single<T> getFuture() {
		// TODO
		return null;
	}

	public <T> void complete(Object value) {
		// TODO
	}

	void completeError(Object error) {
		// TODo
	}

	public boolean getIsCompleted() {
		// TODO
		return false;
	}

}

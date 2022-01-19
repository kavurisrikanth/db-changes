package d3e.core;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FutureExt<T> {
  
	private static final Single<?> NULL = Single.never();

	public static <T> Single<T> Future(Supplier<T> computation) {
		return null;
		// TODO
	}

	public static <T> Single<T> microtask(Supplier<T> computation) {
		// TODO
		return null;
	}

	public static <T> Single<T> sync(Supplier<T> computation) {
		return null;
	}

	public static <T> Single<T> value(T value) {
		if (value instanceof Single) {
			return (Single<T>) value;
		}
		if (value == null) {
			return (Single<T>) NULL;
		}
		return Single.just(value);
	}

	public static <T> T await(Single<T> on) {
		if (on == null) {
			return null;
		}
		if (on == NULL) {
			return null;
		}
		return on.blockingGet();
	}

	public static <T> Single<T> error(Object error) {
		if (error instanceof Throwable) {
			return Single.error((Throwable) error);
		} else {
			return Single.error(new ErrorObjectException(error));
		}
	}

	public static <T> Single<T> delayed(Duration duration, Supplier<T> computation) {
		Supplier<T> supplier = computation != null ? computation : () -> null;
		// return supplier.get().delay(duration.toMillis(), TimeUnit.MILLISECONDS);
		return null;
	}

	public static <T> Single<List<T>> wait(Iterable<Single<T>> futures, @Param("cleanUp") Consumer<T> cleanUp,
			@Param("eagerError") boolean eagerError) {
		// TODO
		return null;
	}

	public static <T> Single<T> any(Iterable<Single<T>> futures) {
		return Flowable.fromIterable(futures).flatMapSingle(i -> i).firstOrError();
	}

	public static <T> Single<Object> forEach(Iterable<T> elements, Function<T, Object> action) {
		return Single.create((ss) -> {
			elements.forEach(i -> {
				Object value = action.apply(i);
				if (value instanceof Single) {
					try {
						Object res = ((Single) value).blockingGet();
						ss.onSuccess(res);
					} catch (Exception e) {
						ss.onError(e);
					}
				} else {
					ss.onSuccess(value);
				}
			});
		});
	}

	public static <T> Single<Object> doWhile(Supplier<Single<Boolean>> action) {
		return Single.create((ss) -> {
			boolean keepGoing = true;
			while (keepGoing) {
				Single<Boolean> value = action.get();
				try {
					keepGoing = value.blockingGet();
				} catch (Exception e) {
					ss.onError(e);
				}
			}
			ss.onSuccess(null);
		});
	}

	public static <R, T> Single<R> then(Single<R> on, Function<R, T> onValue,
			@Param("onError") Function<R, T> onError) {
		// TODO
		return null;
	}

	public static <T> Single<T> catchError(Single<T> on, Consumer<Object> onError,
			@Param("test") Function<Object, Boolean> test) {
		// TODO
		return null;
	}

	public static <T> Single<T> whenComplete(Single<T> on, Supplier<T> action) {
		// TODO
		return null;
	}

	public static <T> Flowable<T> asStream(Single<T> single) {
		return single.toFlowable();
	}

	public static <T> Single<T> timeout(Single<T> single, Duration timeLimit,
			@Param("onTimeout") Supplier<T> onTimeout) {
//		return single.timeout(timeLimit.toMillis(), TimeUnit.MILLISECONDS, Schedulers.computation(),
//				Single.fromCallable(() -> onTimeout.get()).flatMap(i -> i));
		return null;
	}
}

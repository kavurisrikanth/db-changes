package d3e.core;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;

public class StreamExt {
	public StreamExt() {

	}

	public static <T> Flowable<T> fromFuture(Single<T> future) {
		return future.toFlowable();
	}

	public static <T> Flowable<T> fromFutures(Iterable<Single<T>> futures) {
		return Flowable.fromIterable(futures).flatMapSingle(p -> p);
	}

	public static <T> StreamSubscription<T> listen(Flowable<T> source, Consumer<T> onData, boolean cancelOnError,
			Runnable onDone, Function onError) {
		Disposable subscribe = source.subscribe(o -> {
			onData.accept(o);
		});
		return new StreamSubscription<>(subscribe);
	}

	public static <T> Single pipe(Flowable<T> source, StreamConsumer<T> streamConsumer) {
		// TODO
		return null;
	}

	public static <T, S> Flowable<S> transform(Flowable<T> source, StreamTransformer<T, S> streamTransformer) {
		// TODO
		return null;
	}

	// static boolean isBroadcast=false;
	public static <T> boolean getIsBroadcast(Flowable<T> stream) {
		// TODO

		return false;
	}

  public static <T> Single<Long> getLength(Flowable<T> stream) {
		return stream.count();
	}

	public static <T> Single<Boolean> getIsEmpty(Flowable<T> stream) {
		return stream.isEmpty();
	}

	public static <T> Flowable<T> fromSingle(Single<T> future) {
		return future.toFlowable();
	}

	public static <T> Flowable<T> fromSingles(Iterable<Single<T>> futures) {
		return Flowable.fromIterable(futures).flatMap((p) -> p.toFlowable());
	}

	public static <T> Flowable<T> fromIterable(Iterable<T> elements) {
		return Flowable.fromIterable(elements);
	}

  public static <T> Flowable<T> periodic(Duration period, Function<Long, T> computation) {
		return Flowable.timer(period.toMillis(), TimeUnit.MILLISECONDS)
				.map(i -> computation == null ? null : computation.apply(i));
	}

	public static <T> Flowable<T> eventTransformed(Flowable<T> source) {
		// TODO
		return null;
	}

	// Flowable<T> where(boolean test(T event));
	public static <T> Flowable<T> where(Flowable<T> it, Function<T, Boolean> test) {
		return it.filter((i) -> test.apply(i));
	}

	// Flowable<E> asyncMap<E>(SingleOr<E> convert(T event));
	public static <T, E> Flowable<E> asyncMap(Flowable<T> it, Function<T, Single<E>> convert) {
		return it.flatMapSingle(p -> convert.apply(p));
	}

	// Flowable<E> asyncExpand<E>(Flowable<E> convert(T event));
	public static <T, E> Flowable<E> asyncExpand(Flowable<T> it, Function<T, Flowable<E>> convert) {
		return it.flatMap(p -> convert.apply(p));
	}

	// Flowable<S> expand<S>(Iterable<S> convert(T element));
	public static <T, S> Flowable<S> expand(Flowable<T> it, Function<T, Iterable<S>> convert) {
		return it.flatMap((p) -> Flowable.fromIterable(convert.apply(p)));
	}

	// Single<boolean> every(boolean test(T element));
	public static <T> Single<Boolean> every(Flowable<T> it, Function<T, Boolean> test) {
		return it.all(p -> test.apply(p));
	}

	public static <T> Single<Boolean> any(Flowable<T> it, Function<T, Boolean> test) {
		return it.any(p -> test.apply(p));
	}
	// Single<boolean> contains(Object needle);

	public static <T> Single<Boolean> contains(Flowable<T> it, Object needle) {
		return it.contains(needle);
	}

	// Single<String> join([String separator = '']);
	public static <T> Single<String> join(Flowable<T> it, String separator) {
		return it.toList().map(p -> String.join(separator, p.toArray(new String[p.size()])));
	}

	// Flowable<T> takeWhile(boolean test(T element));
	public static <T> Flowable<T> takeWhile(Flowable<T> it, Function<T, Boolean> test) {
		return it.takeWhile((Predicate<T>) p -> test.apply(p));
	}

	// Flowable<T> skipWhile(boolean test(T element));
	public static <T> Flowable<T> skipWhile(Flowable<T> it, Function<T, Boolean> test) {
		return null;
	}
//    Single<T> firstWhere(boolean test(T element), {T orElse()});

	public static <T> Single<T> firstWhere(Flowable<T> it, Function<T, Boolean> test, Supplier<T> orElse) {
		// TODO ambWith needs to changed to something else.
		return it.filter(i -> test.apply(i)).firstElement().toSingle().ambWith(Single.fromSupplier(() -> orElse.get()));
	}

	// Single<T> lastWhere(boolean test(T element), {T orElse()});
	public static <T> Single<T> lastWhere(Flowable<T> it, Function<T, Boolean> test, Supplier<T> orElse) {
		// TODO ambWith needs to changed to something else.
		return it.filter(i -> test.apply(i)).lastElement().toSingle().ambWith(Single.fromSupplier(() -> orElse.get()));
	}

	// Single<T> singleWhere(boolean test(T element), {T orElse()});
	public static <T> Single<T> singleWhere(Flowable<T> it, Function<T, Boolean> test, Supplier<T> orElse) {
		// TODO ambWith needs to changed to something else.
		return it.filter(i -> test.apply(i)).singleOrError().ambWith(Single.fromSupplier(() -> orElse.get()));
	}

	// Single<T> reduce(T combine(T previous, T element));
	public static <T> Single<T> reduce(Flowable<T> it, BiFunction<T, T, T> combine) {
		return it.reduce((i, j) -> combine.apply(i, j)).toSingle();
	}

	// Single<S> fold<S>(S initialValue, S combine(S previous, T element));
	public static <T, E> Single<T> fold(Flowable<T> it, T initalValue, BiFunction<T, E, T> combine) {
		return null;
	}
	// Flowable<R> cast<R>();

	public <T> Flowable<T> cast(Flowable<T> it) {
		// TODO
		return null;

	}

	// Single<List<T>> toList();
	public <T> Single<List<T>> toList(Flowable<T> it) {
		return it.toList();

	}

	// Single<Set<T>> toSet();
	public static <T> Single<Set<T>> toSet(Flowable<T> it) {
		return it.toList().map(i -> i.stream().collect(Collectors.toSet()));

	}

	// Single<E> drain<E>([E futureValue]);
	public static <T> Single<T> drain(Flowable<T> it, T futureValue) {
		return it.skipWhile(i -> true).single(futureValue);

	}

//    Flowable<T> take(Integer count);
	public <T> Flowable<T> take(Flowable<T> it, Integer count) {
		return it.take(count);
	}

	// Flowable<T> distinct([boolean equals(T previous, T next)]);
	public static <T> Flowable<T> distinct(Flowable<T> it, BiFunction<T, T, Boolean> equals) {
		return it.distinctUntilChanged((i, j) -> equals.apply(i, j));
	}

	// Single<T> get first;
	public static <T> Single<T> getFirst(Flowable<T> stream) {
		return stream.firstOrError();
	}

	// Single<T> get last;
	public static <T> Single<T> getLast(Flowable<T> stream) {
		return stream.lastOrError();
	}

	// Single<T> get single;
	public static <T> Single<T> getSingle(Flowable<T> stream) {
		return stream.singleOrError();
	}
	// Single<T> elementAt(Integer index);

	public <T> Single<T> elementAt(Flowable<T> stream, Integer index) {
		return stream.elementAt(index).toSingle();
	}

	// Flowable<T> handleError(Function onError, {boolean test(Object error)});

	public static <T> Flowable<T> handleError(Flowable<T> it, Function<T, T> onError,
			@Param("test") Function<Object, Boolean> test) {
		// TODO
		return null;
	}

	// Flowable<T> castFrom<S, T>(Flowable<S> source);
	@SuppressWarnings("unchecked")
	public static <S, T> Flowable<T> castFrom(Flowable<S> stream) {
		return (Flowable<T>) stream;
	}

	public static <T> Flowable<T> timeout(Flowable<T> stream, Duration timeLimit, Consumer<EventSink<T>> onTimeout) {
		Flowable<T> other = Flowable.create((FlowableEmitter<T> emitor) -> {
			EventSink<T> sink = new EventSink<>(emitor);
			onTimeout.accept(sink);
		}, BackpressureStrategy.BUFFER);

		return stream.timeout((t) -> Flowable.timer(timeLimit.toMillis(), TimeUnit.MILLISECONDS), other);
	}

}

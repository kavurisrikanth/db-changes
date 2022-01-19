package d3e.core;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function3;

public class StreamTransformer<S, T> {

	// Stream<T> bind(Stream<S> stream)
	public static <S, T> StreamTransformer<S, T> fromBind(Function<Flowable<S>, Flowable<T>> bind) {
		// TODO
		return null;
	}

//    const factory StreamTransformer(StreamSubscription<T> onListen(Stream<S> stream, boolean cancelOnError));
	public static <S, T> StreamTransformer<S, T> StreamTransformer(
			BiFunction<Flowable<S>, Boolean, StreamSubscription<T>> onListen) {

		return null;
	}

	// Stream<T> bind(Stream<S> stream)
	public static <S, T> StreamTransformer<S, T> fromHandlers(BiConsumer<S, EventSink<T>> handleData,
			Consumer<EventSink<T>> handleDone, Consumer3<Object, StackTrace, EventSink<T>> handleError) {
		// TODO
		return null;
	}

	public Flowable<T> bind(Flowable<S> stream) {
		// TODO
		return null;
	}

}

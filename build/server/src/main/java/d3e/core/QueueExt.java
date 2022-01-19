package d3e.core;

import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Function;

public class QueueExt extends IterableExt{

	public static <E> Queue<E> ListQueue() {
		return new ListQueue<>();
	}

	public static <E> Queue<E> from(Iterable<E> elements) {
		return ListQueue.from(elements);
	}

	public static <E> Queue<E> of(Iterable<E> elements) {
		return ListQueue.of(elements);
	}

	public static <S, T> Queue<T> castFrom(Queue<S> source) {
		return (Queue<T>) source;
	}

	public static <R, S> Queue<R> cast(Queue<S> source) {
		return (Queue<R>) source;
	}

	public static <E> E removeFirst(Queue<E> source) {
		return source.remove();
	}

	public static <E> E removeLast(Queue<E> source) {
		if (source instanceof Deque) {
			return (E) ((Deque) source).removeLast();
		}
		return null;
	}

	/// Adds [value] at the beginning of the queue.
	public static <E> void addFirst(Queue<E> source, E value) {
		if (source instanceof Deque) {
			((Deque) source).addFirst(value);
		}
	}

	/// Adds [value] at the end of the queue.
	public static <E> void addLast(Queue<E> source, E value) {
		source.add(value);
	}

	/// Adds all elements of [iterable] at the end of the queue. The
	/// length of the queue is extended by the length of [iterable].
	public static <E> void addAll(Queue<E> source, Iterable<E> iterable) {
		source.addAll(ListExt.of(iterable, false));
	}

	/// Removes all elements matched by [test] from the queue.
	///
	/// The `test` function must not throw or modify the queue.
	public static <E> void removeWhere(Queue<E> source, Function<E, Boolean> test) {
		Iterator<E> iterator = source.iterator();
		while (iterator.hasNext()) {
			if (test.apply(iterator.next())) {
				iterator.remove();
			}
		}
	}

	/// Removes all elements not matched by [test] from the queue.
	///
	/// The `test` function must not throw or modify the queue.
	public static <E> void retainWhere(Queue<E> source, Function<E, Boolean> test) {
		removeWhere(source, e -> !test.apply(e));
	}
}

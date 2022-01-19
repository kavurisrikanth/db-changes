package d3e.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

public class IterableExt {

	public static Iterable IterableExt() {
		// TODO
		return null;
	}

  public static <E> Iterable<E> generate(long count, Function<Long, E> generator) {
		return ListExt.<E>generate(count, generator, true);
	}

	public static <E> Iterable<E> empty() {
		return Collections.emptyList();
	}

	public <T> Iterable<T> whereType() {
		// TODO
		return null;

	}

	public static <T> long length(Iterable<T> source) {
		return (long) Iterables.size(source);
	}

	@SuppressWarnings("unchecked")
	public static <S, T> Iterable<T> castFrom(Iterable<S> source) {
		return (Iterable<T>) source;
	}

	public static <E> Iterator<E> getIterator(Iterable<E> it) {
		return it.iterator();
	}

	public static <E, R> Iterable<R> cast(Iterable<E> it) {
		return (Iterable<R>) it;
	}

	public static <E> Iterable<E> followedBy(Iterable<E> it, Iterable<E> other) {
		return Iterables.concat(it, other);
	}

	public static <E, T> Iterable<T> map(Iterable<E> it, Function<E, T> f) {
		return Iterables.transform(it, f::apply);
	}

	public static <E> Iterable<E> where(Iterable<E> it, Function<E, Boolean> test) {
		return Iterables.filter(it, test::apply);
	}

	public static <E, T> Iterable<T> expand(Iterable<E> it, Function<E, Iterable<T>> f) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return Streams.stream(it).flatMap(e -> Streams.stream(f.apply(e))).iterator();
			}
		};
	}

	public static <E> boolean contains(Iterable<E> it, E element) {
		return Iterables.contains(it, element);
	}

	public static <E> E reduce(Iterable<E> it, BiFunction<E, E, E> combine) {
		return Streams.stream(it).reduce(combine::apply).orElse(null);
	}

	public static <E, T> T fold(Iterable<E> it, T initialValue, BiFunction<T, E, T> combine) {
		for (E e : it) {
			initialValue = combine.apply(initialValue, e);
		}
		return initialValue;
	}

	public static <E> boolean every(Iterable<E> it, Function<E, Boolean> test) {
		return Streams.stream(it).allMatch(test::apply);
	}

	public static <E> String join(Iterable<E> it, String separator) {
		return String.join(separator, Streams.stream(it).map(String::valueOf).toArray(String[]::new));
	}

	public static <E> boolean any(Iterable<E> it, Function<E, Boolean> test) {
		return Streams.stream(it).anyMatch(test::apply);
	}

	public static <E> List<E> toList(Iterable<E> it) {
		return ListExt.from(it, false);
	}

	public static <E> List<E> toList(Iterable<E> it, boolean growable) {
		return ListExt.from(it, growable);
	}

	public static <E> Set<E> toSet(Iterable<E> it) {
		return new HashSet<E>(toList(it, false));
	}

	public static <E> long getLength(Iterable<E> it) {
		return (long) Iterables.size(it);
	}

	public static <E> boolean isEmpty(Iterable<E> it) {
		return Iterables.isEmpty(it);
	}

	public static <E> boolean isNotEmpty(Iterable<E> it) {
		return !isEmpty(it);
	}

	public static <E> Iterable<E> take(Iterable<E> it, long count) {
		return Iterables.limit(it, (int) (long) count);
	}

	public static <E> Iterable<E> takeWhile(Iterable<E> it, Function<E, Boolean> test) {
		return new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				Iterator<E> iterator = it.iterator();
				return new Iterator<E>() {
					boolean afterWhile;

					@Override
					public boolean hasNext() {
						if (afterWhile) {
							return false;
						}
						if (iterator.hasNext()) {
							if (test.apply(iterator.next())) {
								return true;
							}
						}
						afterWhile = true;
						return false;
					}

					@Override
					public E next() {
						return iterator.next();
					}
				};
			}
		};
	}

	public static <E> Iterable<E> skip(Iterable<E> it, long count) {
		return Iterables.skip(it, (int) (long) count);
	}

	public static <E> Iterable<E> skipWhile(Iterable<E> it, Function<E, Boolean> test) {
		return new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				Iterator<E> iterator = it.iterator();
				return new Iterator<E>() {
					boolean afterWhile;

					@Override
					public boolean hasNext() {
						if (afterWhile) {
							return iterator.hasNext();
						}
						afterWhile = true;
						while (iterator.hasNext()) {
							if (test.apply(iterator.next())) {
								continue;
							}
							return true;
						}
						return false;
					}

					@Override
					public E next() {
						return iterator.next();
					}
				};
			}
		};
	}

	public static <E> E getFirst(Iterable<E> source) {
		return Iterables.getFirst(source, null);
	}

	public static <E> E getLast(Iterable<E> source) {
		return Iterables.getLast(source);
	}

	public static <E> E getSingle(Iterable<E> source) {
		Iterator<E> iterator = source.iterator();
		if (!iterator.hasNext()) {
			throw new RuntimeException("No Elements");
		}
		E next = iterator.next();
		if (iterator.hasNext()) {
			throw new RuntimeException("Too many Elements");
		}
		return next;
	}

	public static <E> E firstWhere(Iterable<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		if (orElse == null) {
			orElse = () -> null;
		}
		return Streams.stream(source).filter(test::apply).findFirst().orElseGet(orElse);
	}

	public static <E> E lastWhere(Iterable<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		E result = null;
		boolean foundMatching = false;
		for (E element : source) {
			if (test.apply(element)) {
				result = element;
				foundMatching = true;
			}
		}
		if (foundMatching) {
			return result;
		}
		if (orElse != null) {
			return orElse.get();
		}
		return null;
	}

	public static <E> E singleWhere(Iterable<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		E result = null;
		boolean foundMatching = false;
		for (E element : source) {
			if (test.apply(element)) {
				if (foundMatching) {
					throw new RuntimeException("Too many elements found");
				}
				result = element;
				foundMatching = true;
			}
		}
		if (foundMatching) {
			return result;
		}
		if (orElse != null) {
			return orElse.get();
		}
		return null;
	}

	public static <E> E elementAt(Iterable<E> source, long index) {
		return ListExt.from(source, false).get((int) (long) index);
	}
}

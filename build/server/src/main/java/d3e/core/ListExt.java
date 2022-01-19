package d3e.core;

//import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

public class ListExt {
	public static <E> List<E> build(Consumer<List<E>> builder) {
		List<E> list = new ArrayList<>();
		builder.accept(list);
		return list;
	}

	// static boolean growable=false;
	public static <E> List<E> filled(long length, E fill, boolean growable) {
		// setgrowable(growable);
		List<E> arrayList = new ArrayList<>((int) (long) length);
		for (int i = 0; i < length; i++) {
			arrayList.add(fill);
		}
		return arrayList;
	}
//	private static void setgrowable(boolean growable2) {
//		growable=growable2;	
//	}
//	private static boolean getgrowable() {
//		return growable;	
//	}

	public static void setLength(long lengh) {
		// if(getgrowable())
		// TODO
	}

	public static <E> List<E> List(long length) {
    if (length == 0) {
			return List();
		}
		return filled(length, null, false);
	}

	public static <E> List<E> asList(E... es) {
		return new ArrayList<>(Arrays.asList(es));
	}

	public static <E> List<E> from(Iterable<E> elements, boolean growable) {
		List<E> arrayList = new ArrayList<>();
		elements.forEach(arrayList::add);
		return arrayList;
	}
	public static <E> List<E> from(E[] elements, boolean growable) {
		return from(Arrays.asList(elements), growable);
	}

	public static <E> List<E> of(Iterable<E> elements, boolean growable) {
		return from(elements, growable);
	}

  public static <E> List<E> generate(long length, Function<Long, E> f, boolean growable) {
		ArrayList<E> arrayList = new ArrayList<>();
		for (long i = 0; i < length; i++) {
			arrayList.add(f.apply(i));
		}
		return arrayList;
	}

	public static <E> List<E> unmodifiable(Iterable<E> elements) {
		return of(elements, true);
	}

	@SuppressWarnings("unchecked")
	public static <E, S> List<E> castFrom(List<S> source) {
		return (List<E>) source;
	}

	public static <E> List<E> List() {
		return new ArrayList<>();
	}

	public static <T> void copyRange(List<T> target, long at, List<T> source, long start, long end) {
		target.addAll((int) (long) at, source.subList((int) (long) start, (int) (long) end));
	}

	public static <T> void writeIterable(List<T> target, long at, Iterable<T> source) {
		target.addAll((int) (long) at, ListExt.of(source, false));
	}

	@SuppressWarnings("unchecked")
	public static <R, T> List<R> cast(List<T> source) {
		return (List<R>) source;
	}

	public static <E> void first(List<E> source, E value) {
		source.set(0, value);
	}

	public static <E> void last(List<E> source, E value) {
		source.set(source.size(), value);
	}

	public static <E> List<E> sublist(List<E> source, long start, long end) {
		return source.subList((int) (long) start, end == -1? source.size() : (int) (long) end);
	}
	
	public static <E> List<E> slice(List<E> source, long startAt, long resultSize) {
		long endAt = resultSize + startAt - 1;
		if (endAt >= source.size()) {
			endAt = -1;
		}
		return sublist(source, startAt, endAt);
	}

	public static <E> Set length(List<E> source, Integer newLength) {
		// TODO
		return null;
	}

	public static <E> long length(List<E> source) {
		return (long) source.size();
	}

	public static <E> void addAll(List<E> source, Iterable<? extends E> iterable) {
		iterable.forEach(source::add);
	}

	public static <E> List<E> reversed(List<E> source) {
		List<E> other = new ArrayList<E>(source);
		Collections.reverse(other);
		return other;
	}

  public static <E> void sort(List<E> source, BiFunction<E, E, Long> compare) {
		source.sort((i, e) -> (int) (long) compare.apply(i, e));
	}

	public static <E> void shuffle(List<E> source, Random random) {
		Collections.shuffle(source, random);
	}

	public static <E> long indexOf(List<E> source, E element, long start) {
		return start + source.subList((int) (long) start, source.size()).indexOf(element);
	}

	public static <E> long indexWhere(List<E> source, Function<E, Boolean> test, long start) {
		ListIterator<E> iterator = source.listIterator((int) (long) start);
		while (iterator.hasNext()) {
			if (test.apply(iterator.next())) {
				return start;
			}
			start++;
		}
		return -1l;
	}

	public static <E> long lastIndexWhere(List<E> source, Function<E, Boolean> test, long start) {
		for (long i = source.size() - 1; i >= 0 && start >= source.size(); i--) {
			if (test.apply(source.get((int) i))) {
				return i;
			}
		}
		return -1l;
	}

	public static <E> long lastIndexOf(List<E> source, E element, long start) {
		return start + source.subList((int) (long) start, source.size()).lastIndexOf(element);
	}

	public static <E> void insert(List<E> source, long index, E element) {
		source.add((int) (long) index, element);
	}

	public static <E> void insertAll(List<E> source, long index, Iterable<E> iterable) {
		source.addAll((int) (long) index, of(iterable, false));
	}

	public static <E> void setAll(List<E> source, long index, Iterable<E> iterable) {
		for (E e : iterable) {
			source.set((int) (long) (index++), e);
		}
	}

	public static <E> E removeAt(List<E> source, long index) {
		return source.remove((int) (long) index);
	}

	public static <E> E removeLast(List<E> source) {
		return removeAt(source, (long) (source.size() - 1));
	}

	public static <E> void removeWhere(List<E> source, Function<E, Boolean> test) {
		ListIterator<E> it = source.listIterator();
		while (it.hasNext()) {
			if (test.apply(it.next())) {
				it.remove();
			}
		}
	}

	public static <E> void retainWhere(List<E> source, Function<E, Boolean> test) {
		removeWhere(source, (e) -> !test.apply(e));
	}

	public static <E> List<E> plus(List<E> source, List<E> other) {
		List<E> list = of(source, false);
		list.addAll(other);
		return list;
	}

	public static <E> Iterable<E> getRange(List<E> source, long start, long end) {
		return source.subList((int) (long) start, (int) (long) end);
	}

	public static <E> void setRange(List<E> source, long start, long end, Iterable<E> iterable, long skipCount) {
		List<E> list = of(iterable, false);
		Iterator<E> it = list.subList((int) (long) skipCount, list.size()).iterator();
		for (; start < end; start++) {
			if (it.hasNext()) {
				source.set((int) (long) start, it.next());
			} else {
				throw new RuntimeException("Too few elements");
			}
		}
	}

	public static <E> void removeRange(List<E> source, long start, long end) {
		for (long i = start; i < end; i++) {
			source.remove(start);
		}
	}

	public static <E> void fillRange(List<E> source, long start, long end, E fillValue) {
		for (; start < end; start++) {
			source.set((int) (long) start, fillValue);
		}
	}

	public static <E> void replaceRange(List<E> source, long start, long end, Iterable<E> replacement) {
		Iterator<E> it = replacement.iterator();
		for (; start < end; start++) {
			if (it.hasNext()) {
				source.set((int) (long) start, it.next());
			} else {
				throw new RuntimeException("Too few elements");
			}
		}
	}

  public static <E> Map<Long, E> asMap(List<E> source) {
		long i = 0;
    Map<Long, E> map = new HashMap<>();
		for (E e : source) {
			map.put(i++, e);
		}
		return map;
	}

	public static <E> E elementAt(List<E> source, long index) {
		return source.get((int) (long) index);
	}

	public static <E> Iterable<E> followedBy(List<E> source, Iterable<E> other) {
		return Iterables.concat(source, other);
	}

	public static <E> boolean isNotEmpty(List<E> source) {
		return !source.isEmpty();
	}

	public static <E> E first(List<E> source) {
		return source.isEmpty()? null : source.get(0);
	}

	public static <E> E last(List<E> source) {
		return source.get(source.size() - 1);
	}

	public static <E> E single(List<E> source) {
		return IterableExt.getSingle(source);
	}

	public static <E> boolean every(List<E> source, Function<E, Boolean> test) {
		return source.stream().allMatch(test::apply);
	}

	public static <E> boolean any(List<E> source, Function<E, Boolean> test) {
		return source.stream().anyMatch(test::apply);
	}

	public static <E> E firstWhere(List<E> source, Function<E, Boolean> test) {
		return source.stream().filter(test::apply).findFirst().orElse(null);
	}

	public static <E> E firstWhere(List<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		if (orElse == null) {
			orElse = () -> null;
		}
		return source.stream().filter(test::apply).findFirst().orElseGet(orElse);
	}

	public static <E> E lastWhere(List<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		return IterableExt.lastWhere(source, test, orElse);
	}

	public static <E> E singleWhere(List<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		return IterableExt.singleWhere(source, test, orElse);
	}

	public static <E> String join(List<E> source, String separator) {
		return String.join(separator, source.stream().map(String::valueOf).toArray(String[]::new));
	}

	public static <E> List<E> where(List<E> source, Function<E, Boolean> test) {
		return IterableExt.toList(Iterables.filter(source, test::apply));
	}

	public static <T> Iterable<T> whereType(List<T> source) {
		throw new UnsupportedOperationException();
	}

	public static <E, T> List<T> map(List<E> source, Function<E, T> f) {
		return IterableExt.toList(Iterables.transform(source, f::apply));
	}

	public static <T, E> Iterable<T> expand(List<E> source, Function<E, Iterable<T>> f) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return source.stream().flatMap(e -> Streams.stream(f.apply(e))).iterator();
			}
		};
	}

	public static <E> E reduce(List<E> source, BiFunction<E, E, E> combine) {
		return source.stream().reduce(combine::apply).orElse(null);
	}

	public static <T, E> T fold(List<E> source, T initialValue, BiFunction<T, E, T> combine) {
		for (E e : source) {
			initialValue = combine.apply(initialValue, e);
		}
		return initialValue;
	}

	public static <E> Iterable<E> skip(List<E> source, long count) {
		return source.subList((int) (long) count, source.size());
	}

	public static <E> Iterable<E> skipWhile(List<E> source, Function<E, Boolean> test) {
		return IterableExt.skipWhile(source, test);
	}

	public static <E> Iterable<E> take(List<E> source, long count) {
		return source.subList(0, (int) (long) count);
	}

	public static <E> Iterable<E> takeWhile(List<E> source, Function<E, Boolean> test) {
		return IterableExt.takeWhile(source, test);
	}

	public static <E> List<E> toList(List<E> source, boolean growable) {
		return from(source, growable);
	}

	public static <E> Set<E> toSet(List<E> source) {
		return new HashSet<>(source);
	}

	public static <E> E get(List<E> source, long index) {
		return source.get((int) index);
	}
	
	public static <E> E set(List<E> source, long index, E element) {
		return source.set((int) index, element);
	}
	
	public static <E, R extends Comparable<R>> List<E> orderBy(List<E> source, Function<E, R> compare, boolean asc) {
		return source.stream().sorted((x, y) -> compare.apply(x).compareTo(compare.apply(y)) * (asc ? 1 : -1)).collect(Collectors.toList());
	}
	
	public static <E, B, R> List<R> groupBy(List<E> source, Function<E, B> by, BiFunction<B, List<E>, R> map) {
		Map<B, List<E>> groups = MapExt.Map();
		for (E e : source) {
			B group = by.apply(e);
			List<E> list = groups.get(group);
			if (list == null) {
				list = new ArrayList<>();
				groups.put(group, list);
			}
			list.add(e);
		}
		List<R> result = new ArrayList<>();
		groups.forEach((k, v) -> {
			result.add(map.apply(k, v));
		});
		return result;
	}
}

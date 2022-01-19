package d3e.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapExt {

	public static <K, V> Map<K, V> Map() {
		Map<K, V> map = new HashMap<K, V>();
		return map;
	}

	public static <K, V> Map<K, V> from(Map<K, V> other) {
		return new HashMap<K, V>(other);
	}

	public static <K, V> Map<K, V> of(Map<K, V> other) {
		return new HashMap<K, V>(other);
	}

	public static <K, V> Map<K, V> unmodifiable(Map<K, V> other) {
		return Collections.unmodifiableMap(other);
	}

	public static <K, V> Map<K, V> identity() {
		// TODO
		return null;
	}

	public static <K, V, T> Map<K, V> fromIterable(Iterable<T> iterable, @Param("key") Function<T, K> key,
			@Param("value") Function<T, V> value) {
		Map<K, V> map = new HashMap<>();
		iterable.forEach(t -> map.put(key.apply(t), value.apply(t)));
		return map;
	}

	public static <K, V> Map<K, V> fromIterables(Iterable<K> keys, Iterable<V> values) {
		Map<K, V> map = new HashMap<>();
		Iterator<K> ki = keys.iterator();
		Iterator<V> vi = values.iterator();
		while (ki.hasNext() && vi.hasNext()) {
			map.put(ki.next(), vi.next());
		}
		return map;
	}

	public static <K, V, K2, V2> Map<K2, V2> castFrom(Map<K, V> source) {
		// TODO
		return null;
	}

	public static <K, V> Map<K, V> fromEntries(Iterable<Map.Entry<K, V>> entries) {
		// TODO
		return null;
	}

	public static <K, V, RK, RV> Map<RK, RV> cast(Map<K, V> source) {
		// TODO
		return null;
	}

	public static <K, V> void set(Map<K, V> source, K key, V value) {
		source.put(key, value);
	}

	public static <K, V> Iterable<Map.Entry<K, V>> getEntries(Map<K, V> source) {
		return source.entrySet();
	}

	public static <K2, V2, K, V> Map<K2, V2> map(Map<K, V> source, BiFunction<K, V, Map.Entry<K2, V2>> f) {
		// TODO
		return null;
	}

	public static <K, V> void addEntries(Map<K, V> source, Iterable<Map.Entry<K, V>> newEntries) {
		// TODO

	}

	public static <K, V> V update(Map<K, V> source, K key, Function<V, V> update,
			@Param("ifAbsent") Supplier<V> ifAbsent) {
		// TODO
		return null;
	}

	public static <K, V> void updateAll(Map<K, V> source, BiFunction<K, V, V> update) {
		// TODO
	}

	public static <K, V> void removeWhere(Map<K, V> source, BiPredicate<K, V> predicate) {
		new HashSet<>(source.keySet()).forEach(k -> {
			if (predicate.test(k, source.get(k))) {
				source.remove(k);
			}
		});
	}

	public static <K, V> void addAll(Map<K, V> source, Map<K, V> another) {
		source.putAll(another);
	}

	public static <K, V> V putIfAbsent(Map<K, V> source, K key, Supplier<V> ifAbsent) {
		source.putIfAbsent(key, ifAbsent.get());
		return source.get(key);
	}

	public static <K, V> Iterable<K> keys(Map<K, V> source) {
		return source.keySet();
	}

	public static <K, V> long length(Map<K, V> source) {
		return (long) source.size();
	}

	public static <K, V> boolean isNotEmpty(Map<K, V> source) {
		return !source.isEmpty();
	}
}

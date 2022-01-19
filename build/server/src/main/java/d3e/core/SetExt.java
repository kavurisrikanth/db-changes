package d3e.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

public class SetExt {
	public static <E> Set<E> build(Consumer<Set<E>> builder) {
		Set<E> set = new HashSet<>();
		builder.accept(set);
		return set;
	}

	public static <E> Set<E> filled(long length, E fill, boolean growable) {
		Set<E> arrayList = new HashSet<>((int) (long) length);
		for (int i = 0; i < length; i++) {
			arrayList.add(fill);
		}
		return arrayList;
	}

	public static <E> Set<E> Set(long length) {
		return filled(length, null, false);
	}

	public static <E> Set<E> Set(long length, E fill) {
		return filled(length, fill, false);
	}

  public static <E> Set<E> Set(long length, Function<Long, E> f) {
		return generate(length, f, false);
	}

	public static <E> Set<E> from(Iterable<E> elements) {
		Set<E> arrayList = new HashSet<>();
		elements.forEach(arrayList::add);
		return arrayList;
	}

	public static <E> Set<E> Set(Iterable<E> elements) {
		Set<E> arrayList = new HashSet<>();
		if (elements != null) {
			elements.forEach(arrayList::add);
		}
		return arrayList;
	}

	public static <E> Set<E> of(Iterable<E> elements) {
		return from(elements);
	}

  public static <E> Set<E> generate(long length, Function<Long, E> f, boolean growable) {
		Set<E> arrayList = new HashSet<>();
		for (long i = 0; i < length; i++) {
			arrayList.add(f.apply(i));
		}
		return arrayList;
	}

	public static <E> Set<E> unmodifiable(Iterable<E> elements) {
		return of(elements);
	}

	@SuppressWarnings("unchecked")
	public static <E, S> Set<E> castFrom(Set<S> source) {
		return (Set<E>) source;
	}

	public static <E> Set<E> Set() {
		return new HashSet<>();
	}

	public static <E> Set<E> asSet(E... es) {
		return new HashSet<>(Arrays.asList(es));
	}

	@SuppressWarnings("unchecked")
	public static <R, T> Set<R> cast(Set<T> source) {
		return (Set<R>) source;
	}

	public static <E> long length(Set<E> source) {
		return (long) source.size();
	}

	public static <E> void addAll(Set<E> source, Iterable<E> iterable) {
		iterable.forEach(source::add);
	}

	public static <E> void removeWhere(Set<E> source, Function<E, Boolean> test) {
		Iterator<E> it = source.iterator();
		while (it.hasNext()) {
			if (test.apply(it.next())) {
				it.remove();
			}
		}
	}
	
	public static <E> void removeAll(Set<E> source, Iterable<E> elements) {
		elements.forEach(source::remove);
	}
	
	public static <E> void retainAll(Set<E> source, Iterable<E> elements) {
		
		if(source == null)
	    {
	        throw new NullPointerException("collection is null");
	    };
	    while(elements.iterator().hasNext())
	    {   
	        if(!source.contains(elements.iterator().next()))
	        {
	        	elements.iterator().remove(); 
	        }
	    }

	}
	
	public static <E> boolean containsAll(Set<E> source,Iterable<E> other) {
		Iterator<E> otherObj = other.iterator();
		boolean containes = false;
		if(otherObj.hasNext()) {
			if(source.contains(otherObj.next())) {
				containes = true;
			}else {
			   return false;
			}
		}
		return containes;
	}
	//http://www.java2s.com/Code/Java/Collections-Data-Structure/Setoperationsunionintersectiondifferencesymmetricdifferenceissubsetissuperset.htm
	public static <E> Set<E> intersection(Set<E> source,Set<E> other){
	    Set<E> tmp = new HashSet<E>();
	    for (E x : source)
	      if (other.contains(x))
	        tmp.add(x);
	    return tmp;
		
	}
	
	public static <E>Set<E> union(Set<E> source,Set<E> other){
		 Set<E> tmp = new HashSet<E>(source);
		    tmp.addAll(other);
		    return tmp;
	}
	 public static <E>Set<E> difference(Set<E> source,Set<E> other){
		    Set<E> tmpA;
		    Set<E> tmpB;
		    tmpA = union(source, other);
		    tmpB = intersection(source, other);
		    return differenceFrom(tmpA, tmpB);
	 }
  
    public static <E>Set<E> differenceFrom(Set<E> source,Set<E> other){
    	  Set<E> tmp = new HashSet<E>(source);
    	    tmp.removeAll(other);
    	    return tmp;
	}

	public static <E> void retainWhere(Set<E> source, Function<E, Boolean> test) {
		removeWhere(source, (e) -> !test.apply(e));
	}

	public static <E> Set<E> plus(Set<E> source, Set<E> other) {
		Set<E> list = Set(source);
		list.addAll(other);
		return list;
	}

	public static <E> Iterable<E> followedBy(Set<E> source, Iterable<E> other) {
		return Iterables.concat(source, other);
	}

	public static <E> boolean isNotEmpty(Set<E> source) {
		return !source.isEmpty();
	}

	public static <E> E first(Set<E> source) {
		return Iterables.getFirst(source, null);
	}

	public static <E> E last(Set<E> source) {
		return Iterables.getLast(source);
	}

	public static <E> E single(Set<E> source) {
		return IterableExt.getSingle(source);
	}

	public static <E> boolean every(Set<E> source, Function<E, Boolean> test) {
		return source.stream().allMatch(test::apply);
	}

	public static <E> boolean any(Set<E> source, Function<E, Boolean> test) {
		return source.stream().anyMatch(test::apply);
	}

	public static <E> E firstWhere(Set<E> source, Function<E, Boolean> test) {
		return source.stream().filter(test::apply).findFirst().orElse(null);
	}

	public static <E> E firstWhere(Set<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		return source.stream().filter(test::apply).findFirst().orElseGet(orElse);
	}

	public static <E> E lastWhere(Set<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		return IterableExt.lastWhere(source, test, orElse);
	}

	public static <E> E singleWhere(Set<E> source, Function<E, Boolean> test, Supplier<E> orElse) {
		return IterableExt.singleWhere(source, test, orElse);
	}
	public static <E> E elementAt(Set<E> source,long index) {
		return IterableExt.elementAt(source, index);
	}

	public static <E> String join(Set<E> source, String separator) {
		return String.join(separator, source.stream().map(String::valueOf).toArray(String[]::new));
	}

	public static <E> Iterable<E> where(Set<E> source, Function<E, Boolean> test) {
		return Iterables.filter(source, test::apply);
	}

	public <T> Iterable<T> whereType() {
		throw new UnsupportedOperationException();
	}

	public static <E, T> Iterable<T> map(Set<E> source, Function<E, T> f) {
		return Iterables.transform(source, f::apply);
	}

	public static <T, E> Iterable<T> expand(Set<E> source, Function<E, Iterable<T>> f) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return source.stream().flatMap(e -> Streams.stream(f.apply(e))).iterator();
			}
		};
	}

	public static <E> E reduce(Set<E> source, BiFunction<E, E, E> combine) {
		return source.stream().reduce(combine::apply).orElse(null);
	}

	public static <T, E> T fold(Set<E> source, T initialValue, BiFunction<T, E, T> combine) {
		for (E e : source) {
			initialValue = combine.apply(initialValue, e);
		}
		return initialValue;
	}

	public static <E> Iterable<E> skip(Set<E> source, long count) {
		return IterableExt.skip(source, count);
	}

	public static <E> Iterable<E> skipWhile(Set<E> source, Function<E, Boolean> test) {
		return IterableExt.skipWhile(source, test);
	}

	public static <E> Iterable<E> take(Set<E> source, long count) {
		return IterableExt.take(source, count);
	}

	public static <E> Iterable<E> takeWhile(Set<E> source, Function<E, Boolean> test) {
		return IterableExt.takeWhile(source, test);
	}

	public static <E> List<E> toList(Set<E> source, boolean growable) {
		return ListExt.from(source, growable);
	}

	public static <E> Set<E> toSet(Set<E> source) {
		return new HashSet<>(source);
	}
	
	public static <E> Set<E> identity() {
		return null;
	}
}

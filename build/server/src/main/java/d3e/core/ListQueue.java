package d3e.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public class ListQueue<E> extends LinkedList<E> implements Iterable<E>, Queue<E> {

	public ListQueue() {
		super();
	}

	public ListQueue(Integer initialCapacity) {
		super();
	}

	private ListQueue(Iterable<E> elements) {
		super(ListExt.of(elements, false));
	}

	public static <E> ListQueue<E> from(Iterable<E> elements) {
		return new ListQueue<>(elements);
	}

	public static <E> ListQueue<E> of(Iterable<E> elements) {
		return new ListQueue<>(elements);
	}

	public <R> ListQueue<R> cast() {
		return (ListQueue<R>) this;
	}

	public long length() {
		return (long)size();
	}

	public E first() {
		return get(0);
	}

	public E last() {
		return get(size() - 1);
	}

	public E single() {
		return ListExt.single(this);
	}

	public E elementAt(long index) {
		return get((int)(long)index);
	}

	public List<E> toList() {
		return toList(true);
	}

	public List<E> toList(boolean growable) {
		return ListExt.of(this, false);
	}

	public void addAll(Iterable<E> elements) {
		super.addAll(ListExt.of(elements, false));
	}

	public void removeWhere(Function<E, Boolean> test) {
		ListExt.removeWhere(this, test);
	}

	public void retainWhere(Function<E, Boolean> test) {
		ListExt.retainWhere(this, test);
	}
}

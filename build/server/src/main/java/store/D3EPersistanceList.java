package store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class D3EPersistanceList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int field;
	private DBObject master;
	private transient boolean proxy;

	private boolean inverse;

	public D3EPersistanceList(DBObject master, int field) {
		this(master, field, false);
	}
	
	public D3EPersistanceList(DBObject master, int field, boolean inverse) {
		this.master = master;
		this.field = field;
		this.inverse = inverse;
	}

	public DBObject getMaster() {
		return master;
	}

	public int getField() {
		return field;
	}

	public void _unproxy(List result) {
		super.addAll(result);
	}

	public void _markProxy() {
		this.proxy = true;
	}

	private void _checkProxy() {
		if (this.proxy) {
			Database.get().unproxyCollection(this);
			this.proxy = false;
		}
	}

	private void _fieldChanged() {
		master.collFieldChanged(field, this);
	}

	@Override
	public int size() {
		_checkProxy();
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		_checkProxy();
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		_checkProxy();
		return super.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		_checkProxy();
		return super.iterator();
	}

	@Override
	public Object[] toArray() {
		_checkProxy();
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		_checkProxy();
		return super.toArray(a);
	}

	@Override
	public boolean add(E e) {
		_checkProxy();
		boolean res = super.add(e);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public boolean remove(Object o) {
		_checkProxy();
		boolean res = super.remove(o);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		_checkProxy();
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		_checkProxy();
		boolean res = super.addAll(c);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		_checkProxy();
		boolean res = super.addAll(index, c);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		_checkProxy();
		boolean res = super.removeAll(c);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		_checkProxy();
		boolean res = super.retainAll(c);
		if (res) {
			_fieldChanged();
		}
		return res;
	}

	@Override
	public void clear() {
		_checkProxy();
		if (!this.isEmpty()) {
			super.clear();
			_fieldChanged();			
		}
	}

	@Override
	public E get(int index) {
		_checkProxy();
		return super.get(index);
	}

	@Override
	public E set(int index, E element) {
		_checkProxy();
		E res = super.set(index, element);
		_fieldChanged();
		return res;
	}

	@Override
	public void add(int index, E element) {
		_checkProxy();
		super.add(index, element);
		_fieldChanged();
	}

	@Override
	public E remove(int index) {
		_checkProxy();
		E res = super.remove(index);
		_fieldChanged();
		return res;
	}

	@Override
	public int indexOf(Object o) {
		_checkProxy();
		return super.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		_checkProxy();
		return super.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		_checkProxy();
		return super.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		_checkProxy();
		return super.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		_checkProxy();
		return super.subList(fromIndex, toIndex);
	}

	@Override
	public <T> T[] toArray(IntFunction<T[]> generator) {
		_checkProxy();
		return super.toArray(generator);
	}

	@Override
	public Stream<E> stream() {
		_checkProxy();
		return super.stream();
	}

	@Override
	public Stream<E> parallelStream() {
		_checkProxy();
		return super.parallelStream();
	}

	@Override
	public void trimToSize() {
		_checkProxy();
		super.trimToSize();
	}

	@Override
	public void ensureCapacity(int minCapacity) {
		_checkProxy();
		super.ensureCapacity(minCapacity);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		_checkProxy();
		super.removeRange(fromIndex, toIndex);
		_fieldChanged();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		// TODO: How do we detect if something inside the list has changed?
		_checkProxy();
		super.forEach(action);
	}

	@Override
	public Spliterator<E> spliterator() {
		_checkProxy();
		return super.spliterator();
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		_checkProxy();
		boolean result = super.removeIf(filter);
		if (result) {
			_fieldChanged();
		}
		return result;
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		_checkProxy();
		super.replaceAll(operator);
		_fieldChanged();
	}

	@Override
	public void sort(Comparator<? super E> c) {
		_checkProxy();
		super.sort(c);
		_fieldChanged();
	}

	@Override
	public String toString() {
		_checkProxy();
		return super.toString();
	}

	@Override
	public Object clone() {
		_checkProxy();
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		_checkProxy();
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		_checkProxy();
		return super.hashCode();
	}

	public boolean isInverse() {
		return inverse;
	}
}

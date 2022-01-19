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

	private void _fieldChanged(List<E> ghost) {
		master.collFieldChanged(field, this, ghost);
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
		List<E> ghost = new ArrayList<>(this);
		if (ghost.add(e)) {
			_fieldChanged(ghost);
		}
		return super.add(e);
	}

	@Override
	public boolean remove(Object o) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		if (ghost.remove(o)) {
			_fieldChanged(ghost);
		}
		return super.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		_checkProxy();
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		if (ghost.addAll(c)) {
			_fieldChanged(ghost);
		}
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		if (ghost.addAll(index, c)) {
			_fieldChanged(ghost);
		}
		return super.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		if (ghost.removeAll(c)) {
			_fieldChanged(ghost);
		}
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		if (ghost.retainAll(c)) {
			_fieldChanged(ghost);
		}
		return super.retainAll(c);
	}

	@Override
	public void clear() {
		_checkProxy();
		if (!this.isEmpty()) {
			List<E> ghost = new ArrayList<>(this);
			ghost.clear();
			_fieldChanged(ghost);
			super.clear();
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
		List<E> ghost = new ArrayList<>(this);
		ghost.set(index, element);
		_fieldChanged(ghost);
		E res = super.set(index, element);
		return res;
	}

	@Override
	public void add(int index, E element) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		ghost.add(index, element);
		_fieldChanged(ghost);
		super.add(index, element);
	}

	@Override
	public E remove(int index) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		ghost.remove(index);
		_fieldChanged(ghost);
		E res = super.remove(index);
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
		List<E> ghost = new ArrayList<>(this);
		if (ghost.removeIf(filter)) {
			_fieldChanged(ghost);
		}
		return super.removeIf(filter);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		ghost.replaceAll(operator);
		_fieldChanged(ghost);
		super.replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super E> c) {
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		ghost.sort(c);
		_fieldChanged(ghost);
		super.sort(c);
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
	
	public void setAll(List<E> nums) {
		if (nums.isEmpty()) {
			// Nothing to do
			return;
		}
		_checkProxy();
		List<E> ghost = new ArrayList<>(this);
		ghost.clear();
		if (ghost.addAll(nums)) {
			_fieldChanged(ghost);
		}
		super.clear();
		super.addAll(nums);
	}
}

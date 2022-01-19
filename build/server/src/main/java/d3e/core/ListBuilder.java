package d3e.core;

public class ListBuilder<T> {
	public java.util.List<T> list = d3e.core.ListExt.List(0l);

	public ListBuilder() {
	}

	public ListBuilder<T> add(T item) {
		this.list.add(item);
		return this;
	}

	public <E extends T> ListBuilder<T> spread(java.lang.Iterable<E> items) {
		items.forEach((i) -> this.list.add(i));
		return this;
	}

	public ListBuilder<T> ifThenElse(boolean value, java.util.function.Consumer<ListBuilder<T>> then,
			java.util.function.Consumer<ListBuilder<T>> andElse) {
		if (value) {
			then.accept(this);
		} else {
			andElse.accept(this);
		}
		return this;
	}

	public ListBuilder<T> ifThen(boolean value, java.util.function.Consumer<ListBuilder<T>> then) {
		if (value) {
			then.accept(this);
		}
		return this;
	}

	public <C> ListBuilder<T> forItems(java.lang.Iterable<C> items,
			java.util.function.BiConsumer<ListBuilder<T>, C> then) {
		items.forEach((i) -> then.accept(this, i));
		return this;
	}

	public java.util.List<T> build() {
		return this.list;
	}
}

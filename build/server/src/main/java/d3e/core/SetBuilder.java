package d3e.core;

public class SetBuilder<T> {
	public java.util.Set<T> set = d3e.core.SetExt.Set();

	public SetBuilder() {
	}

	public SetBuilder<T> add(T item) {
		this.set.add(item);
		return this;
	}

	public <E extends T> SetBuilder<T> spread(java.lang.Iterable<E> items) {
		items.forEach((i) -> this.set.add(i));
		return this;
	}

	public SetBuilder<T> ifThenElse(boolean value, java.util.function.Consumer<SetBuilder<T>> then,
			java.util.function.Consumer<SetBuilder<T>> andElse) {
		if (value) {
			then.accept(this);
		} else {
			andElse.accept(this);
		}
		return this;
	}

	public SetBuilder<T> ifThen(boolean value, java.util.function.Consumer<SetBuilder<T>> then) {
		if (value) {
			then.accept(this);
		}
		return this;
	}

	public <C> SetBuilder<T> forItems(java.lang.Iterable<C> items,
			java.util.function.BiConsumer<SetBuilder<T>, C> then) {
		items.forEach((i) -> then.accept(this, i));
		return this;
	}

	public java.util.Set<T> build() {
		return this.set;
	}
}

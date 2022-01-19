package d3e.core;

import java.util.Iterator;

public class IteratorExt {

	public static <E> boolean moveNext(Iterator<E> it) {
		return it.hasNext();
	}

	public static <E> E getCurrent(Iterator<E> it) {
		return it.next();
	}
}

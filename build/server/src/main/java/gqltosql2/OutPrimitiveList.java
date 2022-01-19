package gqltosql2;

import java.util.ArrayList;

public class OutPrimitiveList extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long getMemorySize() {
		long size = 64 + size() * 8;
		return size;
	}
}

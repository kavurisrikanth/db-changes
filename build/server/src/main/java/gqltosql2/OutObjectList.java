package gqltosql2;

import java.util.ArrayList;

public class OutObjectList extends ArrayList<OutObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long getMemorySize() {
		long size = 64;
		for (OutObject e : this) {
			size += e.getMemorySize();
		}
		return size;
	}

}

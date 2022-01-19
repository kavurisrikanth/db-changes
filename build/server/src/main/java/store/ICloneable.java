package store;

import d3e.core.CloneContext;

public interface ICloneable {

	public default void collectChildValues(CloneContext ctx) {
	}

	public default void deepCloneIntoObj(ICloneable cloned, CloneContext ctx) {
	}

	public ICloneable createNewInstance();
}

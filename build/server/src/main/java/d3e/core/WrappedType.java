package d3e.core;

import java.util.List;

public class WrappedType extends Type {

	private List<Type> subs;
	private Type outer;

	public WrappedType(Type outer, List<Type> subs) {
		super(outer.getName());
		this.outer = outer;
		this.subs= subs;
	}

	public List<Type> getSubs() {
		return subs;
	}

	public void setSubs(List<Type> subs) {
		this.subs = subs;
	}

	public Type getOuter() {
		return outer;
	}

	public void setOuter(Type outer) {
		this.outer = outer;
	}

}

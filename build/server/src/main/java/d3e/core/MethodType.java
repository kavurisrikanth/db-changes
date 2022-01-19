package d3e.core;

public class MethodType extends Type {

	private Type on;
	private Type gen;

	public MethodType(Type on, String name, Type gen) {
		super(name);
		this.on = on;
		this.gen = gen;
	}

	public Type getOn() {
		return on;
	}

	public void setOn(Type on) {
		this.on = on;
	}

	public Type getGen() {
		return gen;
	}

	public void setGen(Type gen) {
		this.gen = gen;
	}

}

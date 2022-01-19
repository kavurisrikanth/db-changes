package d3e.core;

import java.util.List;

public class Type {
	
	private String name;

	public Type(String name){
		this.name =name;		
	}
	
	public String getName() {
		return name;		
	}
	
	public static Type find(String name) {
		return new Type(name == null? "Object" : name);
	}
	public static Type wrap(Type outer, List<Type> args) {
		return new WrappedType(outer, args);
	}

    public static Type methodType(Type on, String name, Type gen) {
    	return new MethodType(on,name,gen);
    }
}

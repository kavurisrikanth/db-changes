package d3e.core;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FunctionExt {	
	public long getHashCode() {
		return (long)super.hashCode();
	}
	public static void apply(Function function,List positionalArguments,Map<Object, Object> namedArguments) {
		//TODO
	}
	public boolean equals(Object other) {
		//TODO
		return true;
	}
	
	
}

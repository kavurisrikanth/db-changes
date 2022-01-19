package store;

public class ObjectUtil {

	public static boolean equalsIgnoreCase(String object1, String object2) {
		if (object1 == object2) {
			return true;
		}
		if (object1 == null || object2 == null) {
			return false;
		}
		return object1.equalsIgnoreCase(object2);
	}

}

package classes;

public class IdGenerator {
	
  private static long id = 0l;

  public static synchronized long getNext() {
    id++;
    return id;
  }
}

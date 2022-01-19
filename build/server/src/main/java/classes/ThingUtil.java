package classes;

import models.ChildModel;
import models.Thing;

public class ThingUtil {
  public ThingUtil() {}

  public static void handleThing(Thing thing) {
    /*
     Make changes
    */
    long num = thing.getChild().getNum();
    thing.getChild().setNum(num + 1l);
    /*
     Revert
    */
    thing.getChild().setNum(num);
  }
}

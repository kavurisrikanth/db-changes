package classes;

import models.Thing;

public class ThingUtil {
  public ThingUtil() {}

  public static void handleThing(Thing thing) {
    /*
     Make changes
     Integer num = thing.child.num;
     thing.child.num = num + 1;
    */
    String msg = thing.getMsg();
    thing.setMsg("World");
    /*
     Revert
     thing.child.num = num;
    */
    thing.setMsg(msg);
  }
}

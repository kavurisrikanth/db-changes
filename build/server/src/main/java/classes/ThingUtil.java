package classes;

import models.ChildModel;
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
    
    ChildModel child = thing.getChild();
    ChildModel child2 = new ChildModel();
    thing.setChild(child2);
    
    thing.setChild(child);
  }
}

package classes;

import java.util.ArrayList;
import java.util.List;

import d3e.core.ListExt;
import models.ChildModel;
import models.Thing;

public class ThingUtil {
  public ThingUtil() {}

  public static void handleThing(Thing thing) {
    /*
     Make changes
     Integer num = thing.child.num;
     thing.child.num = num + 1;
     
     
    String msg = thing.getMsg();
    thing.setMsg("World");
    
    ChildModel child = thing.getChild();
    ChildModel child2 = new ChildModel();
    thing.setChild(child2);
    
    
	  List<Long> nums = new ArrayList<>(thing.getNums());
	  List<Long> copy = new ArrayList<>(nums);
	  copy.add(3l);
	  thing.setNums(copy);
    */
	  
	  thing.addToNums(1, -1);
	  
	  thing.removeFromNums(1);
    /*
     Revert
     thing.child.num = num;
     
	  thing.setMsg(msg);

	  thing.setChild(child);
	  
	  thing.setNums(nums);
    */
  }
}

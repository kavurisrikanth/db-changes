package classes;

import d3e.core.ListExt;
import java.util.List;

public class Range {
  public Range() {}

  public static List<Long> to(long n) {
    List<Long> res = ListExt.asList();
    for (long x = 0l; x < n; x++) {
      res.add(x);
    }
    return res;
  }
}

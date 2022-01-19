package classes;

import d3e.core.ListExt;
import d3e.core.StringExt;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TreeHelper {
  public TreeHelper() {}

  public static <T> void expandLast(List<T> result, Function<T, Iterable<T>> expand) {
    for (T i : expand.apply(ListExt.last(result))) {
      result.add(i);
      TreeHelper.expandLast(result, expand);
    }
  }

  public static <T> List<T> expandAll(List<T> base, Function<T, Iterable<T>> expand) {
    List<T> result = ListExt.List(0l);
    for (T i : base) {
      result.add(i);
      TreeHelper.expandLast(result, expand);
    }
    return result;
  }

  public static <T, R> R doOn(T value, Function<T, R> fun) {
    return fun.apply(value);
  }

  public static <T> void expandAndSearchLast(
      String searchText,
      List<T> result,
      T next,
      Function<T, Iterable<T>> expand,
      Function<T, String> toStringProvider) {
    for (T i : expand.apply(next)) {
      if (Objects.equals(searchText, "")
          || StringExt.contains(
              toStringProvider.apply(i).toLowerCase(), searchText.toLowerCase(), 0l)) {
        result.add(i);
      }
      TreeHelper.expandAndSearchLast(searchText, result, i, expand, toStringProvider);
    }
  }

  public static <T> List<T> expandAndSearchAll(
      String searchText,
      List<T> base,
      Function<T, Iterable<T>> expand,
      Function<T, String> toStringProvider) {
    List<T> result = ListExt.List(0l);
    for (T i : base) {
      if (Objects.equals(searchText, "")
          || StringExt.contains(
              toStringProvider.apply(i).toLowerCase(), searchText.toLowerCase(), 0l)) {
        result.add(i);
      }
      TreeHelper.expandAndSearchLast(searchText, result, i, expand, toStringProvider);
    }
    return result;
  }
}

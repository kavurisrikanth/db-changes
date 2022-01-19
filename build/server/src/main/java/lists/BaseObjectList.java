package lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseObjectList {

	protected List<Long> collectIds(List<Object[]> objects, int groupStart, int groupEnd, int index) {
		List<Long> group = new ArrayList<>();
		if (groupStart == groupEnd) {
			return group;
		}
		for (int x = groupStart; x < groupEnd; x++) {
			String ids = (String) objects.get(x)[index];
			List<Long> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
					.map(i -> Long.parseLong(i.trim())).collect(Collectors.toList());
			group.addAll(allIds);
		}
		return group;
	}

	protected <T> List<T> collectGroupByItems(List<Object[]> objects, int groupStart, int groupEnd, int groupColumn,
			GroupByInputMapper<T> itemMapper, Object[] percentTotals) {
		List<T> group = new ArrayList<>();
		if (groupStart == groupEnd) {
			return group;
		}
		Object currentValue = objects.get(groupStart)[groupColumn];
		Integer newStartX = groupStart;
		Integer newEndX = groupStart;
		for (int x = groupStart; x < groupEnd; x++, newEndX++) {
			java.lang.Object[] objArray = objects.get(x);
			// If prev and current are same. then continue
			if (objArray[groupColumn].equals(currentValue)) {
				continue;
			}
			// Now read these items.
			group.add(itemMapper.map(objects, newStartX, newEndX, groupColumn + 1, currentValue, percentTotals));
			newStartX = newEndX;
			currentValue = objArray[groupColumn];
		}
		if (newStartX != newEndX) {
			group.add(itemMapper.map(objects, newStartX, newEndX, groupColumn + 1, currentValue, percentTotals));
		}
		return group;
	}

	public interface GroupByInputMapper<T> {

		T map(List<Object[]> objects, int groupStart, int groupEnd, int groupColumn, Object currentValue, Object[] percentTotals);
	}
}

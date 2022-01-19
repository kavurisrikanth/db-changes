package store;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class ListChanges {
	public static enum ChangeType {
		Added, Removed
	}

	public static class Change {
		public final ChangeType type;
		public final Object obj;
		public final int index;

		public Change(ChangeType type, int index, Object obj) {
			this.type = type;
			this.index = index;
			this.obj = obj;
		}
		@Override
		public String toString() {
			return type.toString() + " at " + String.valueOf(index) + " : "+String.valueOf(obj);
		}
	}

	private List old;
	private List<Change> compiledResult;

	public ListChanges(List old) {
		this.old = old;
	}

	public List<Change> compile(List list) {
		if (compiledResult != null) {
			return this.compiledResult;
		}
		compiledResult = new ArrayList<>();
		int x = 0;
		int xCount = list.size();
		int y = 0;
		int yCount = old.size();
		int lookAhead = 1;
		while (x < xCount) {
			Object xobj = list.get(x);
			if (y == yCount) {
				compiledResult.add(new Change(ChangeType.Added, x, xobj));
			} else {
				int temp = 0;
				boolean found = false;
				while( temp <= lookAhead && (y + temp) < yCount) {
					Object yobj = old.get(y + temp);
					if (Objects.equals(xobj, yobj)) {
						found = true;
						while(temp > 0) {
							temp--;
							yobj = old.get(y + temp);
							compiledResult.add(new Change(ChangeType.Removed, x, yobj));
						}
						y++;
						break;
					}
					temp++;
				}
				if(!found) {
					compiledResult.add(new Change(ChangeType.Added, x, xobj));
				}
			}
			x++;
		}
		while (y < yCount) {
			Object yobj = old.get(y);
			compiledResult.add(new Change(ChangeType.Removed, x, yobj));
			y++;
		}

		return compiledResult;
	}

	public List getOld() {
		return old;
	}

}

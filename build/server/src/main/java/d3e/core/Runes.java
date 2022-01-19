/**
 * Created to support the getRunes() method in String.
 */

package d3e.core;

import java.util.Iterator;

public class Runes {

  private Iterator<Long> codePoints;

	public Runes(String s) {
    codePoints = s.codePoints().asLongStream().iterator();
	}

  public Iterator<Long> getCodePoints() {
		return codePoints;
	}
}

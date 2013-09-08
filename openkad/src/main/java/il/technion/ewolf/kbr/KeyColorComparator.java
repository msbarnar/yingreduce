package il.technion.ewolf.kbr;

import java.util.Comparator;

/**
 * Compare Key's colors distance from a given color
 * @author eyal.kibbar@gmail.com
 */
public class KeyColorComparator extends KeyComparator {

  public static class NodeKeyColorComparator extends NodeKeyComparator {

    private final int color;
    private final int nrColors;

    public NodeKeyColorComparator(final Key key, final int nrColors) {
        super(key);
      this.color = key.getColor(nrColors);
      this.nrColors = nrColors;
    }

    public int compare(Node n1, Node n2) {
      int c1 = Math.abs(n1.getKey().getColor(nrColors) - color);
      int c2 = Math.abs(n2.getKey().getColor(nrColors) - color);

      if (c1 == c2)
        return super.compare(n1, n2);


      return c1 - c2;
    }
  }

	private final int color;
	private final int nrColors;
	
	public KeyColorComparator(Key key, int nrColors) {
		super(key);
		this.color = key.getColor(nrColors);
		this.nrColors = nrColors;
	}
	
	@Override
	public int compare(Key n1, Key n2) {
		int c1 = Math.abs(n1.getColor(nrColors) - color);
		int c2 = Math.abs(n2.getColor(nrColors) - color);
		
		if (c1 == c2)
			return super.compare(n1, n2);
		
		
		return c1 - c2;
		
	}

}

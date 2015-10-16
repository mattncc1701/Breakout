package XmlImporter.pattern;

import java.awt.Point;

public class PatternStripeBox implements BlockPatternInstance {
	int length = 8;
	int i = 0;
	boolean change = false;

	// @Override
	public boolean changeBlock() {
		return (i==length-1);
	}

	// @Override
	public Point nextBlock(Point previous) {
		i++;
		if(i < length)
		{
			return new Point(previous.x + 1, previous.y);
		}else
		{
			i = 0;
			return new Point(previous.x - length + 1, previous.y + 1);
		}
	}

	// @Override
	public void parseArgs(String[] args) {
		if(args.length > 1)
		{
			try{
				int val = Integer.parseInt(args[1]);
				length = val;
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	// @Override
	public int preferredSize() {
		return length * length;
	}

}

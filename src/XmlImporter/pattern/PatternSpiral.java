package XmlImporter.pattern;

import java.awt.Point;

public class PatternSpiral implements BlockPatternInstance {
	private boolean change = false;
	private int sideLength = 1;
	private int dx = 1;
	private int dy = 1;
	private int i = 0;
	
	// @Override
	public boolean changeBlock() {
		return change;
	}

	// @Override
	public int preferredSize()
	{
		return 50;
	}
	
	// @Override
	public Point nextBlock(Point previous) {
		Point next = new Point(previous.x + dx, previous.y + dy);
		if(++i == sideLength)
		{
			sideLength++;
			i = 0;
			if(dx == 1 && dy == 1)
			{
				dy = -1;
				change = false;
			}
			else if(dx == 1 && dy == -1) 
			{
				dx = -1;
			}
			else if(dx == -1 && dy == -1)
			{
				dy = 1;
			}
			else if(dx == -1 && dy == 1)
			{
				dx = 1;
				change = true;
			}
		}
		return next;
	}
	
	// @Override
	public void parseArgs(String[] args)
	{}
}

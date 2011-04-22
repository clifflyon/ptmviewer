package browser;

public class ZOrderMatrix
{

/**
	 * Create a matrix such that each element contains
	 * the index of a z-filling curve.
	 * This matrix can be used to indexing a quadtree efficiently.
	 * 
	 * Example (level=2):
	 * 
	 *    0  1  4  5
	 *    2  3  6  7
	 *    8  9  12 13
	 *    10 11 14 15
	 * 
	 * @param level The level of resolution
	 * @return The matrix with the z-filling curve indexing.
	 */
	static public int[][] createZMatrix(int level)
	{		
		int size = (int)Math.pow(2.0, (double)level);
		int[][] zmatrix = new int[size][size];
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
			{
				zmatrix[r][c] = ZIndex(r, c, level);
			}
		
		return zmatrix;
	}
	
	static public int ZIndex(int r, int c, int level)
	{
		int p = 1;
		int e = 1;
		int index = 0;
		for (int k = 0; k  < level; k++)
		{
			if ((c&p) != 0)
				index += e;
			e *= 2;
			
			if ((r&p) != 0)
				index += e; 
			e *= 2;
			p *= 2;
		}
		
		return index;
	}
}

	
import java.awt.Color;
import java.awt.Point;

public class Board {
	/* 2d array of Colors (x, y) that stores which spots are filled and their color */
	private Color[][] grid;
	/* An array of integers that stores how many filled blocks are in each row starting from the bottom row */
	private int[] widths;
	/* An array of integers that stores the height that a column has been filled up to, 
	 * aka the index of the open spot right above the top filled spot in the column */
	private int[] heights;
	// backup variables
	/* Used by the client to check if they can undo one placement */
	private boolean committed;
	/* backup for the widths variable of the board */
	private int[] bkupWidths;
	/* backup for the heights variable of the board */
	private int[] bkupHeights; 
	/* backup for the grid */
	private Color[][] bkupGrid;
	
	public final static int DEFAULT_WIDTH = 10;
	public final static int DEFAULT_HEIGHT = 20;
	/* The piece was successfully placed, no rows were filled */
	public final static int PLACE_OK = 1;
	/* The piece was successfully place and at least one row was filled */
	public final static int PLACE_ROW_FILLED = 2;
	/* The piece could not be placed because part of the piece is out of bounds */
	public final static int PLACE_OUT_BOUNDS = 3;
	/* The piece could not be placed because it would overlap existing filled spots */
	public final static int PLACE_BAD = 4;
	
	public Board() {
		this.grid = new Color[DEFAULT_WIDTH][DEFAULT_HEIGHT]; // default elements are null
		this.widths = new int[DEFAULT_HEIGHT]; // default elements are 0
		this.heights = new int[DEFAULT_WIDTH]; // default elements are 0
		this.committed = true;
	}
	
	/**
	 * Takes a piece, x, and y and places the piece in the board with its lower
	 * left corner at x, y. Use undo() to undo the most recent placement.
	 * @return PLACE_OK for a successful placement, PLACE_ROW_FILLED for a
	 * successful placement that resulted in at least one row being filled, 
	 * PLACE_OUT_BOUNDS for a placement where part of the piece is out of bounds,
	 * and PLACE_BAD for a placement where the piece would overlap already filled 
	 * blocks 
	 */
	public int place(Piece piece, int x, int y) {
		// set up backup
		committed = false;
		bkupWidths = new int[widths.length];
		bkupHeights = new int[heights.length];
		bkupGrid = new Color[grid.length][grid[0].length];
		System.arraycopy(widths, 0, bkupWidths, 0, widths.length);
		System.arraycopy(heights, 0, bkupHeights, 0, heights.length);
		for (int i = 0; i < grid.length; i++) {
			System.arraycopy(grid[i], 0, bkupGrid[i], 0, grid[0].length);
		}
		
		boolean rowFilled = false;
		/* Check each point of piece's body to see if it is in a valid spot */
		for (Point pt : piece.getBody()) {
			int ptX = pt.x + x;
			int ptY = pt.y + y;
			if (ptX < 0 || ptX >= grid.length || ptY < 0 || ptY >= grid[0].length) {
				return PLACE_OUT_BOUNDS;
			} else if (grid[ptX][ptY] != null) {
				return PLACE_BAD;
			} else {
				grid[ptX][ptY] = piece.getColor();
				widths[ptY]++;
				if (heights[ptX] < ptY) { heights[ptX] = ptY; }
				if (widths[ptY] == grid[0].length) { rowFilled = true; }
			}
		}
		
		if (rowFilled) {
			return PLACE_ROW_FILLED;
		} else {
			return PLACE_OK;
		}
	}
	
	public void clearRows() {
		// count the number of filled rows
		int count = 0;
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] == grid.length) {
				count++; // TODO
			}
		}
	}
}

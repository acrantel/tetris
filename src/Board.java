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
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	public Board(int width, int height) {
		this.grid = new Color[width][height];
		this.widths = new int[height];
		this.heights = new int[width];
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
		this.committed = false;
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
				if (heights[ptX] < ptY + 1) { heights[ptX] = ptY + 1; }
				if (widths[ptY] == grid.length) { rowFilled = true; }
			}
		}
		
		if (rowFilled) {
			return PLACE_ROW_FILLED;
		} else {
			return PLACE_OK;
		}
	}
	
	/**
	 * dropHeight() computes the y value where the origin (0, 0) of a piece will come
	 * to rest if dropped in the given column from infinitely high. This method uses
	 * the heights array and the skirt of the piece to compute the y value quickly
	 */
	public int dropHeight(Piece p, int x) {
		int originY = -1;
		for (int i = 0; i < p.getSkirt().length; i++) {
			if (originY < heights[i+x] - p.getSkirt()[i]) {
				originY = heights[i+x] - p.getSkirt()[i];
			}
		}
		return originY;
	}
	
	/**
	 * Clears all the full rows in grid, should be called after place() call
	 */
	public void clearRows() {
		// commited should have already been set to false in place()
		// we do not save the state of this board, because state of undo board
		// has already been saved in the previous place() call
		this.committed = false;
		
		int curTopRow = 0;
		int totalCleared = 0;
		for (int i = 0; i < widths.length; i++) {
			if (widths[i] == grid.length) {
				totalCleared++;
				// clear this row
				for (int c = 0; c < grid.length; c++) {
					grid[c][curTopRow] = null;
				}
				widths[i] = 0;
			} else if (widths[i] == 0) {
				break;
			} else {
				// transfer this row to its new home and clear the src row (i)
				for (int c = 0; c < grid.length; c++) {
					grid[c][curTopRow] = grid[c][i];
					grid[c][i] = null;
				}
				widths[curTopRow] = widths[i]; // transfer the width too
				widths[i] = 0; // set the widths of the "src" row to 0
				curTopRow++;
			}
		}
		// move the heights array down by totalCleared
		for (int k = 0; k < heights.length; k++) {
			heights[k] = Math.max(heights[k] - totalCleared, 0); 
		}
	}
	
	@Override
	public String toString() {
		String res = "{";
		for (int r = this.grid[0].length - 1; r >= 0; r--) {
			res += "{";
			for (int c = 0; c < grid.length; c++) {
				if (grid[c][r] == null) {
					res += "-, ";
				} else {
					res += "X, "; 
				}//grid[c][r] + ", ";
			}
			res += "}\n";
		}
		return res;
	}
	
	// UNDO FUNCTIONALITY
	/**
	 * Undos the most recent place() or place()/clearRows() operation if 
	 * the board hasn't been committed yet, basically returning the board 
	 * to its original state. Once commit has been called, it is not possible
	 * to go back to the original state.
	 */
	public void undo() {
		if (!committed) {
			// reset the grid
			this.grid = this.bkupGrid;
			this.heights = this.bkupHeights;
			this.widths = this.bkupWidths;
		}
	}
	
	/**
	 * Commits the board state, so that it is no longer possible to go back to the 
	 * original state.
	 */
	public void commit() {
		this.committed = true;
	}

}

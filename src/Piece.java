/* Copyright 2019, Serena Li, All rights reserved. */
import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;


public final class Piece {
	/* 4 points that make up the piece's first rotation */
	private Point[] body;
	/* an int array as long as the width of the piece that stores the lowest y value for each x value in the body */
	private int[] skirt;
	// overall width & height
	private int width;
	private int height;
	// "next" rotation - used by nextRotation
	private Piece next;
	/* Color of the piece */
	private Color color;
	
	/* singleton array of first rotations */
	static private Piece[] pieces = null;
	
	/**
	 * Defines a new piece given the Points that make up its body
	 */
	private Piece(Point[] points, Color color) {
		this.color = color;
		this.body = points;
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Point p : points) {
			if (p.x < minX) { minX = p.x; }
			if (p.x > maxX) { maxX = p.x; }
			if (p.y < minY) { minY = p.y; }
			if (p.y > maxY) { maxY = p.y; }
		}
		this.width = maxX - minX + 1;
		this.height = maxY - minY + 1;
		
		this.skirt = new int[width];
		Arrays.fill(skirt, Integer.MAX_VALUE);
		for (Point p : points) {
			if (skirt[p.x - minX] > p.y) { skirt[p.x - minX] = p.y; }
		}
	}
	
	/**
	 * @return width of piece measured in blocks
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return height of piece measured in blocks
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Caller should not modify returned array
	 * @return this piece's body
	 */
	public Point[] getBody() {
		return body;
	}

	/**
	 * Caller should not modify returned array. A piece's skirt gives the lowest
	 * y value for each possible x value. It is used for computing where the piece
	 * will land.
	 * @return skirt of the piece
	 */
	public int[] getSkirt() {
		return skirt;
	}

	/**
	 * Returns a piece that is 90 degrees counterclockwise rotated from the
	 * receiver
	 */
	public Piece nextRotation() {
		return next;
	}
	
	/**
	 * Returns true if two pieces are the same -- their bodies contain the same
	 * points, not necessarily in the same order. Used internally to detect if 
	 * two rotations are effectively the same. 
	 */
	public boolean equals(Piece other) {
		if (this.getWidth() != other.getWidth() || this.getHeight() != other.getHeight()
				|| other.getBody().length != this.getBody().length) {
			return false;
		}
		boolean hashTb[][] = new boolean[other.getWidth()][other.getHeight()]; // default initialized to false
		for (int i = 0; i < other.getBody().length; i++) {
			hashTb[other.getBody()[i].x][other.getBody()[i].y] = true;
		}
		for (int k = 0; k < this.getBody().length; k++) {
			if (!hashTb[this.getBody()[k].x][this.getBody()[k].y]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		String res = "{";
		for (int i = 0; i < this.getBody().length; i++) {
			res += "(" + this.getBody()[i].x + ", " + this.getBody()[i].y + ")"
					+ (i == this.getBody().length-1 ? "}" : ", ");
		}
		return super.toString() + res;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Returns an array containing the first rotation of each of the 7 standard
	 * tetris pieces. The next rotation can be obtained from each piece with 
	 * nextRotation(), which gives the next piece in the circular rotation
	 * linked list
	 */
	public static Piece[] getPieces() {
		if (pieces == null) {
			pieces = new Piece[] {
					setRotationCycle(new Piece(parsePoints("0 0 0 1 0 2 0 3"), Color.CYAN)), // 0, I tetronimo
					setRotationCycle(new Piece(parsePoints("0 0 0 1 0 2 1 0"), Color.ORANGE)), // 1, L tetronimo
					setRotationCycle(new Piece(parsePoints("0 0 1 0 1 1 1 2"), Color.BLUE)), // 2, J tetronimo
					setRotationCycle(new Piece(parsePoints("0 0 1 0 1 1 2 1"), Color.GREEN)), // 3, S tetronimo
					setRotationCycle(new Piece(parsePoints("0 1 1 1 1 0 2 0"), Color.RED)), // 4, Z tetronimo
					setRotationCycle(new Piece(parsePoints("0 0 0 1 1 0 1 1"), Color.YELLOW)), // 5, O tetronimo
					setRotationCycle(new Piece(parsePoints("0 0 1 0 1 1 2 0"), new Color(153, 0, 204))), // 6, T tetronimo
					};
		}
		return pieces;
	}
	
	/**
	 * Sets up a circular linked list of rotation states using Piece.next
	 * @param startPiece The first rotation
	 * @return the original piece
	 */
	private static Piece setRotationCycle(Piece startPiece) {
		Piece prevPiece = startPiece;
		Piece nextPiece = calcNextPiece(prevPiece);
		while (!nextPiece.equals(startPiece)) {
			prevPiece.next = nextPiece;
			prevPiece = nextPiece;
			nextPiece = calcNextPiece(prevPiece);
		}
		prevPiece.next = startPiece;
		return startPiece;
	}
	
	/** 
	 * Calculate the next rotation for this piece
	 */
	private static Piece calcNextPiece(Piece p) {
		// 1. Transpose matrix	2. Reverse each row
		Point[] newPts = new Point[p.getBody().length]; // TODO
		for (int i = 0; i < newPts.length; i++) {
			newPts[i] = new Point(p.getBody()[i].y, (p.getWidth() - 1) - p.getBody()[i].x);
		}
		return new Piece(newPts, p.color);
	}
	
	/**
	 * Converts a string array with coordinates to a Point array
	 * @param coords the coordinate string, formatted as "1 1 0 1 1 0" aka (1, 1), (0, 1), (1, 0)
	 */
	private static Point[] parsePoints(String coords) {
		String[] split = coords.split(" ");
		Point[] result = new Point[split.length/2];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Point(Integer.parseInt(split[i*2]), Integer.parseInt(split[i*2+1]));
		}
		return result;
	}
	
	
}

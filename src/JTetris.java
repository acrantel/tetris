/* Copyright 2019, Serena Li, All rights reserved. */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class JTetris extends JComponent {
	/* A Board object that stores the current state of the board */
	private Board board;
	/* All the possible pieces */
	private Piece[] pieces;
	
	/* The piece that the user is currently manipulating */
	private Piece curPiece;
	/* The current x value of the manipulated piece */
	private int curX;
	/* the current y value of the manipulated piece */
	private int curY;
	/* if the current piece was moved by the player */
	private boolean moved;
	
	/* Indicates if the game going on right now. gamePlaying is false when paused or when game is over */
	private boolean gamePlaying;
	/* If the game has been lost */
	private boolean gameLost;
	/* If we should display the board */
	private boolean displayBoard;
	/* Random generator for the pieces */
	private Randp<Piece> random;
	
	/* Timer for moving the piece down */
	private Timer moveDownTimer;
	// the amount of time to wait before
	private int delay = 400;
	// the previous move
	private String prevMove;
	
	/* Width of the playable area of the board */
	public static final int BOARD_WIDTH = 10;
	/* Height of the playable area of the board */
	public static final int BOARD_HEIGHT = 20;
	/* the starting place for all pieces, and the end of the game if a piece lands in this area */
	public static final int TOP_SPACE = 4;
	
	private static final String DROP = "DROP";
	private static final String RIGHT = "RIGHT";
	private static final String LEFT = "LEFT";
	private static final String ROTATE = "ROTATE";
	private static final String DOWN = "DOWN";
	
	public JTetris() {}
	@Override
	public void paintComponent(Graphics g) {
		if (displayBoard) {
			Graphics2D g2 = (Graphics2D) g;
			int blockHeight = (this.getHeight()) / (BOARD_HEIGHT + TOP_SPACE);
			int blockWidth = (this.getWidth()) / (BOARD_WIDTH);
			int usableHeight = (this.getHeight() - (this.getHeight()) % blockHeight);
			int usableWidth = (this.getWidth() - (this.getWidth()) % blockWidth);
			g2.drawRect(0, 0, usableWidth+1, usableHeight+1);
			
			
			
			// draw dividing line between the top space and the playable board
			g2.drawLine(0, blockHeight*TOP_SPACE, usableWidth, blockHeight*TOP_SPACE);
			
			for (int x = 0; x < BOARD_WIDTH; x++) {
				for (int y = 0; y < BOARD_HEIGHT+TOP_SPACE; y++) {
					Color color = board.at(x, y);
					if (color != null) {
						g2.setColor(color);
						g2.fillRect(x*blockWidth + 1, usableHeight - (y+1)*blockHeight + 1, blockWidth-3, blockHeight-3);
					}
				}
			}
			if (gameLost) { // display "GAME OVER"
				g2.drawString("GAME OVER", 0, usableHeight/2);
			}
		}
	}
	
	/**
	 * Sets up the board, pieces, and key bindings. Starts the timer for moving the pieces
	 * down every x milliseconds.
	 */
	public void startGame() {
		gamePlaying = true;
		gameLost = false;
		displayBoard = true;
		this.board = new Board(BOARD_WIDTH, BOARD_HEIGHT + TOP_SPACE);
		this.pieces = Piece.getPieces();
		this.random = new Randp<Piece>(pieces);
		// set up key bindings
		this.getInputMap().put(KeyStroke.getKeyStroke("UP"), ROTATE);
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), RIGHT);
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), LEFT);
		this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), DOWN);
		this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), DROP);
		
		this.getActionMap().put(ROTATE, new MoveAction(ROTATE));
		this.getActionMap().put(RIGHT, new MoveAction(RIGHT));
		this.getActionMap().put(LEFT, new MoveAction(LEFT));
		this.getActionMap().put(DOWN, new MoveAction(DOWN));
		this.getActionMap().put(DROP, new MoveAction(DROP));
		this.addNewPiece();
		// set up the timer
		ActionListener dropPiece = new DropPieceListener(this);
		this.moveDownTimer = new Timer(delay, dropPiece);
		moveDownTimer.start();
	}
	
	/**
	 * Sets curPiece to piece, sets curX to x, curY to Y, and 
	 * places the piece on the board.
	 * @param piece
	 * @param x
	 * @param y
	 * @return
	 */
	public int setCurrent(Piece piece, int x, int y) {
		board.undo();
		int result = board.place(piece, x, y);
		if (result == Board.PLACE_OK || result == Board.PLACE_ROW_FILLED) {
			this.curPiece = piece;
			this.curX = x;
			this.curY = y;
			moved = false;
		} else { // if the place failed, undo it
			board.undo();
		}
		return result;
	}
	
	/**
	 * Randomly generates the next piece to put on the top of the board.
	 * @return The next piece to put on the board.
	 */
	public Piece pickNextPiece() {
		return random.next();
	}
	
	/**
	 * Adds a new piece to the top of the board and sets this as the current piece.
	 * This should only be called with board in a commited state.
	 */
	public int addNewPiece() {
		if (gamePlaying && !gameLost) {
			if (!board.isCommited()) {
				return Board.PLACE_BAD;
			}
			Piece nextPiece = pickNextPiece();
			int result = setCurrent(nextPiece, (BOARD_WIDTH-nextPiece.getWidth())/2, BOARD_HEIGHT);
			repaint();
			return result;
		} else {
			return Board.PLACE_BAD;
		}
	}
	
	/**
	 * Calculates and returns the new (x, y) of the current piece 
	 * if the move is applied to it.
	 * @param mv the move to apply to the piece
	 * @return {x, y} the position that mv would do to the current piece
	 */
	public int[] getNewPos(String mv) {
		switch (mv) {
		case DOWN: return new int[] {this.curX, this.curY-1};
		case LEFT: return new int[] {this.curX - 1, this.curY};
		case RIGHT: return new int[] {this.curX + 1, this.curY};
		case ROTATE: return new int[] {this.curX, this.curY};
		case DROP: return new int[] {this.curX, board.dropHeight(curPiece, this.curX)};
		default: return new int[] {-1, -1};
		}
	}
	
	/**
	 * Does the move mv to the current Piece
	 * @param mv The move to execute
	 */
	public void step(String mv) {
		if (!gamePlaying || gameLost) { // don't do anything if the game is over or paused
			return;
		}
		if (curPiece != null && mv != ROTATE) {
			board.undo();
			int[] pos = getNewPos(mv);
			int testPlace = board.place(curPiece, pos[0], pos[1]);
			// check if the piece has landed
			if ((testPlace == Board.PLACE_BAD || testPlace == Board.PLACE_OUT_BOUNDS) && (mv == DOWN || mv == DROP)) {
				if (board.getMaxHeight()+1 > BOARD_HEIGHT) { // if the piece has gone over the top of the board, player has lost
					board.undo();
					board.place(curPiece, curX, curY);
					gamePlaying = false;
					gameLost = true;
				} else { // otherwise replace the piece where it was, clear the rows, and add a new piece
					board.undo();
					board.place(curPiece, curX, curY);
					board.clearRows();
					board.commit();
					repaint();
					this.addNewPiece();
				}
			} else if (testPlace == Board.PLACE_OK || testPlace == Board.PLACE_ROW_FILLED) {
				this.setCurrent(curPiece, pos[0], pos[1]);
				repaint();
			} else { // if we can't move the piece to this place
				board.undo();
				board.place(curPiece, curX, curY);
			}
		} else if (curPiece != null && mv == ROTATE) {
			board.undo();
			// rotate piece around the center
			int centerX = curX + curPiece.getWidth()/2;
			int centerY = curY + curPiece.getHeight()/2;
			// calculate the bottom left corner of the new piece
			Piece nextPiece = curPiece.nextRotation();
			int newX = centerX - nextPiece.getWidth()/2; // make sure newX is less than board width and greater= to 0
			int newY = centerY - nextPiece.getHeight()/2; 
			// check if we can place this on the board
			int result = board.place(nextPiece, newX, newY);
			board.undo();
			if (result == Board.PLACE_OK || result == Board.PLACE_ROW_FILLED) {
				this.setCurrent(nextPiece, newX, newY);
			} else { // if the rotation intersects with other blocks or the wall
				// implement a wall kick
				// check one space right, one space left, one space down, one space down-right, one space down-left
				final int[][] wallkick = { {1, 0}, {-1, 0}, {0, -1}, {1, -1}, {-1, -1} };
				for (int i = 0; i < wallkick.length; i++) {
					int r = board.place(nextPiece, newX+wallkick[i][0], newY+wallkick[i][1]);
					if (r == Board.PLACE_OK || r == Board.PLACE_ROW_FILLED) {
						board.undo();
						this.setCurrent(nextPiece, newX+wallkick[i][0], newY+wallkick[i][1]);
						return;
					}
					board.undo();
				}
				// otherwise we can't rotate the object
				this.setCurrent(nextPiece, curX, curY);
			} 
		}
	}
	
	class MoveAction extends AbstractAction {
		private String move;
		MoveAction(String move) {
			this.move = move;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JTetris x = (JTetris)e.getSource();
			x.step(move);
			x.repaint();
		}
		
	}
	
	class DropPieceListener implements ActionListener {
		private JTetris t;
		DropPieceListener(JTetris t) {
			this.t = t;
		}
		@Override
		public void actionPerformed(ActionEvent evt) {
			t.step(JTetris.DOWN);
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(BOARD_WIDTH*20, (BOARD_HEIGHT+TOP_SPACE)*20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTetris tComp = new JTetris();
		frame.add(tComp);
		frame.setVisible(true);
		tComp.grabFocus();
		tComp.startGame();
	}
}

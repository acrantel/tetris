/* Copyright 2019, Serena Li, All rights reserved. */
public class Debug {
	public static void main(String[] args) {
		Board b = new Board();
		Piece[] pieces = Piece.getPieces();
		System.out.println(b.place(pieces[0], 0, 0));
		b.commit();
		System.out.println(b);
		
		System.out.println(b.place(pieces[1], 1, 0));
		b.commit();
		System.out.println(b);
		
		System.out.println(b.place(pieces[6], 3, 0));
		b.commit();
		System.out.println(b);
		
		System.out.println(b.place(pieces[2].nextRotation(), 6, 0));
		b.commit();
		System.out.println(b);
		
		System.out.println(b.place(pieces[3].nextRotation(), 8, 0));
		b.clearRows();
		b.commit();
		System.out.println(b);
		
		int dy =  b.dropHeight(pieces[4], 2);
		System.out.println("where z should drop if origin has x of 2: " + dy);
		System.out.println(b.place(pieces[4], 2, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 3);
		System.out.println("where z should drop if origin has x of 3: " + dy);
		System.out.println(b.place(pieces[4], 3, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 4);
		System.out.println("where z should drop if origin has x of 4: " + dy);
		System.out.println(b.place(pieces[4], 4, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 1);
		System.out.println("where z should drop if origin has x of 1: " + dy);
		System.out.println(b.place(pieces[4], 1, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 2);
		System.out.println("where z should drop if origin has x of 2: " + dy);
		System.out.println(b.place(pieces[4], 2, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 7);
		System.out.println("where z should drop if origin has x of 7: " + dy);
		System.out.println(b.place(pieces[4], 7, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");

		dy =  b.dropHeight(pieces[4], 0);
		System.out.println("where z should drop if origin has x of 0: " + dy);
		System.out.println(b.place(pieces[4], 0, dy));
		System.out.println(b);
		System.out.println("----------------------------------------------------------");
		
		b.undo();
		System.out.println("b undone one step");
		System.out.println(b);
		System.out.println("----------------------------------------------------------");
	}
}

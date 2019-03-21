/* Copyright 2019, Serena Li, All rights reserved. */
import java.util.Random;

public class Randp<T> {
	private T[] arr;
	private int numsLeft;
	private Random r;
	public Randp(T[] arr) {
		r = new Random();
		this.arr = arr;
		numsLeft = arr.length;
	}
	
	public T next() {
		if (numsLeft <= 0) {
			numsLeft = arr.length;
		}
		int randIndex = r.nextInt(numsLeft);
		T result = arr[randIndex];
		// swap the element at the end of the currently viable list with the element at randIndex
		swap(randIndex, numsLeft-1);
		numsLeft--;
		return result;
	}
	
	private void swap(int a, int b) {
		T temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}
	
}


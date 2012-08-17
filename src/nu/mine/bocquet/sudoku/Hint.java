package nu.mine.bocquet.sudoku;

import java.util.Vector;

public class Hint {
	Vector<Integer> digit;
	Vector<Position> position;
	Vector<String> hints;
	int level;
	public Hint() {
		this.hints = new Vector<String>();
		this.digit = new Vector<Integer>();
		this.position = new Vector<Position>();
	}
}

package nu.mine.bocquet.sudoku;

import java.util.HashSet;
import java.util.Set;

public class Area {
	private HashSet<Cell> [] digits;
	public Set<Cell> cells(int digit) {
		assert digit >= 1;
		assert digit <= 9;
		return this.digits[digit];
	}
	
	public Area() {
		@SuppressWarnings("unchecked") HashSet<Cell>[] l = (HashSet<Cell>[])new HashSet[10];
		this.digits = l;
		for (int d = 1; d <= 9; d++) {
			this.digits[d] = new HashSet<Cell>();
		}
 	}
	public void remove(int digit, Cell c) {
		assert digit >= 1;
		assert digit <= 9;
		assert this.digits[digit].contains(c);
		this.digits[digit].remove(c);
	}
	public void add(int digit, Cell c) {
		assert digit >= 1;
		assert digit <= 9;
		assert !this.digits[digit].contains(c);
		this.digits[digit].add(c);
	}
}

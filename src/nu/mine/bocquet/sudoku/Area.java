package nu.mine.bocquet.sudoku;

import java.util.HashSet;
import java.util.Set;

public class Area {
	private HashSet<Cell> [] digits;
	private int number;
	private String kind;
	public Set<Cell> cells(int digit) {
		assert digit >= 1;
		assert digit <= 9;
		return this.digits[digit];
	}
	public Cell[] unsolvedCells () {
		HashSet<Cell> res = new HashSet<Cell>();
		for(HashSet<Cell> h : this.digits) {
			if (h == null) {
				continue;
			}
			for(Cell c : h) {
				if (! c.isSolved()) {
					res.add(c);
				}
			}
		}
		return res.toArray(new Cell[]{});
	}
	public int[] unsolvedDigitss () {
		int [] res = new int[9];
		int nbDigits = 0;
		for(int d = 1; d <= 9; d++) {
			if (this.cells(d).size() == 1) {
				res[nbDigits++] = d;
			}
		}
		return java.util.Arrays.copyOf(res, nbDigits);
	}
	// number from 0 to 8, kind is either column, line or zone
	public Area(int number, String kind) {
		this.number = number;
		this.kind = kind;
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
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}
	/**
	 * @return a displayable name
	 */
	public String name() {
		return kind + ' ' + (number + 1);
	}
}

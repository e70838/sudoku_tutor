/**
 * 
 */
package nu.mine.bocquet.sudoku;

/**
 * @author le nombre 23
 *
 */
public final class Position {
	private int line, column, zone, index;
	public int I() { return this.index; }
	public int L() { return this.line; }
	public int C() { return this.column; }
	public int Z() { return this.zone; }
	public int[] CLZ() { return new int[]{this.column, 9+this.line, 18+this.zone}; }
	public Position (int index) {
		assert index >= 0;
		assert index < 81;
		this.index = index;
		this.column = index % 9;
		this.line = index / 9;
		this.zone = this.line - this.line % 3
			+ this.column / 3;
		assert this.zone >= 0;
		assert this.zone < 9;
	}
	public Position (int line, int column)
	{
		assert line >= 0;
		assert column >= 0;
		assert line < 9;
		assert column < 9;
		this.line = line;
		this.column = column;
		this.index = line * 9 + column;
		this.zone = this.line - this.line % 3
			+ this.column / 3;
		assert this.zone >= 0;
		assert this.zone < 9;
	}
	public String toString() {
		return "L" + (this.line + 1) + "-C" + (this.column + 1) + "-Z" + (this.zone + 1);
	}
}

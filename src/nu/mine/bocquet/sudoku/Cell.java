package nu.mine.bocquet.sudoku;

public class Cell {
	private int [] m_candidate;
	private boolean [] m_isCandidate;
	private Position position;
	public boolean isCandidate(int digit) { return this.m_isCandidate[digit]; }
	public int [] candidates() { return this.m_candidate; }
	public Cell (int [] candidates, Position position) {
		this.position = position;
		this.m_candidate = candidates;
		this.m_isCandidate = new boolean[10];
		for (int i = 0; i < 10; i++) {this.m_isCandidate[i] = false; }
		for (int d : candidates) {
			assert d > 0;
			assert ! this.m_isCandidate[d];
			this.m_isCandidate[d] = true;
		}
	}
	public String toString () {
		StringBuffer s = new StringBuffer();
		for (int c : this.m_candidate) {
			s.append((char)('0' + c));
		}
		return s.toString();
	}
	public void removeCandidate(int digit) {
		assert this.m_isCandidate[digit];
		this.m_isCandidate[digit] = false;
		int[] c = new int[this.m_candidate.length - 1];
		int i = 0;
		for (int d : this.m_candidate) {
			if (d != digit) {
				c[i++] = d;
			}
		}
		this.m_candidate = c;
	}
	public Position getPosition() {
		return position;
	}
}

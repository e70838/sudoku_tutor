package nu.mine.bocquet.sudoku;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

public class Sudoku {
	private Area[] columns;
	private Area[] lines;
	private Area[] zones;
	public Cell[] grille;
	public void dump(PrintStream os) {
		for (int i = 0; i < 81; i++) {
			Cell c = grille[i];
			for (int digit : c.candidates()) {
				os.print (digit);
			}
			if ((i + 1) % 9 == 0) {
				os.println();
			} else {
				os.print (' ');
			}
		}
	}
	private boolean hint1Check(Hint h, Cell cell, int digit, Area area, String name) {
		Set<Cell> s = area.cells(digit);
		for (Cell c : s) {
			if (c != cell) {
				if (h.position.size() == 0) {
					h.hints.add("A digit can be present only once in a " + name);
					h.hints.add("The digit " + digit + " is present in cell " + cell.getPosition());
				}
				h.position.add(c.getPosition());
				h.digit.add(digit);
				h.hints.add("It can not be present in cell " + c.getPosition());
			}
		}
		return h.position.size() != 0;
	}
	private boolean hint2Check (Hint h, Area[] areas, String name) {
		for (int i = 0; i < 9; i++) {
			for (int digit = 1; digit <= 9; digit++) {
				Set<Cell> s = areas[i].cells(digit);
				if (s.size() == 1) {
					Cell c = s.iterator().next();
					int [] candidates = c.candidates();
					if (candidates.length != 1) {
						h.hints.add("Check digits that have only one candidate in a " + name);
						h.hints.add("Digit " + digit + " in " + name + ' ' + (i + 1));
						for (int d : candidates) {
							if (d != digit) {
								h.digit.add(d);
								h.position.add(c.getPosition());
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	private boolean hint3Check (Hint h, Area[] areas, String name) {
		for (int i = 0; i < 9; i++) {
			Area a = areas[i];
			for (int d1 = 1; d1 <= 9; d1++) {
				if (a.cells(d1).size() == 2) {
					Cell[] s1 = a.cells(d1).toArray(new Cell[0]);
					for (int d2 = d1 + 1; d2 <= 9; d2++) {
						Set<Cell> s2 = a.cells(d2);
						if (s2.size() == 2 && s2.contains(s1[0]) && s2.contains(s1[1])) {
							for (Cell c : s1) {
								int[] candidates = c.candidates();
								if (candidates.length > 2) {
									for (int d3 : candidates) {
										if ((d3 == d1) || (d3 == d2)) {
											continue;
										}
										h.position.add(c.getPosition());
										h.digit.add(d3);
										if (h.hints.size() == 0) {
											h.hints.add("Hidden pair: when 2 digits can be in only 2 cells of a " + name + ", these cells can not have other candidates");
											h.hints.add("in " + name + ' ' + (i + 1));
											h.hints.add("digit " + d1 + " and digit " + d2);
										}
										h.hints.add("excludes candidate " + d3 + " in cell " + h.position);

									}
								}
							}
							if (h.hints.size() != 0) { return true; }
						}
					}
				}
			}
		}
		return false;
	}
	public Hint hint() {
		// level 1
		Hint h = new Hint();
		h.level = 1; // if a digit is known, it shall be removed from the candidates of its line, column and zone
		for (int i = 0; i < 81; i++) {
			Cell cell = grille[i];
			if (cell.candidates().length == 1) {
				int digit = cell.candidates()[0];
				Position p = cell.getPosition();
				if (hint1Check (h, cell, digit, columns[p.C()], "column")) { return h; }
				if (hint1Check (h, cell, digit, lines[p.L()], "line")) { return h; }
				if (hint1Check (h, cell, digit, zones[p.Z()], "zone")) { return h; }
			}
		}
		h.level = 2; // if a digit exists only once in a line, column or zone, remove others candidates
		if (hint2Check (h, columns, "column")) { return h; }
		if (hint2Check (h, lines, "line")) { return h; }
		if (hint2Check (h, zones, "zone")) { return h; }
		h.level = 3; // hidden pairs
		if (hint3Check (h, columns, "column")) { return h; }
		if (hint3Check (h, lines, "line")) { return h; }
		if (hint3Check (h, zones, "zone")) { return h; }
		
		h.level = 5; // advanced hatching
		for (int digit = 1; digit <= 9; digit++) {
			for (int col = 0; col < 9; col++) {
				Set<Cell> cells = this.columns[col].cells(digit);
				Iterator<Cell> it = cells.iterator();
				boolean oneZone = true;
				Cell c = it.next();
				int z = c.getPosition().Z();
				while (it.hasNext()) {
					c = it.next();
					if (c.getPosition().Z() != z) {
						oneZone = false;
					}
				}
				if (oneZone) {
					Cell [] zcells = zones[z].cells(digit).toArray(new Cell[0]);
					if (zcells.length != cells.size()) {
						h.hints.add("Partial position:");
						h.hints.add("digit " + digit);
						h.hints.add("in zone " + (z + 1) + " must be in column " + (col + 1));						
						for (Cell zcell : zcells) {
							if (!cells.contains(zcell)) {
								h.digit.add(digit);
								h.position.add(zcell.getPosition());
							}
						}
						return h;
					}
				}
			}
			for (int line = 0; line < 9; line++) {
				Set<Cell> cells = this.lines[line].cells(digit);
				Iterator<Cell> it = cells.iterator();
				boolean oneZone = true;
				Cell c = it.next();
				int z = c.getPosition().Z();
				while (it.hasNext()) {
					c = it.next();
					if (c.getPosition().Z() != z) {
						oneZone = false;
					}
				}
				if (oneZone) {
					Cell [] zcells = zones[z].cells(digit).toArray(new Cell[0]);
					if (zcells.length != cells.size()) {
						h.hints.add("Partial position:");
						h.hints.add("digit " + digit);
						h.hints.add("in zone " + (z + 1) + " must be in line " + (line + 1));						
						for (Cell zcell : zcells) {
							if (!cells.contains(zcell)) {
								h.digit.add(digit);
								h.position.add(zcell.getPosition());
							}
						}
						return h;
					}
				}
			}
			for (int zone = 0; zone < 9; zone++) {
				Set<Cell> cells = this.zones[zone].cells(digit);
				Iterator<Cell> it = cells.iterator();
				boolean oneColumn = true;
				boolean oneLine = true;
				Cell c = it.next();
				int col = c.getPosition().C();
				int line = c.getPosition().L();
				while (it.hasNext()) {
					c = it.next();
					if (c.getPosition().C() != col) {
						oneColumn = false;
					}
					if (c.getPosition().L() != line) {
						oneLine = false;
					}
				}
				if (oneColumn) {
					Cell [] ccells = this.columns[col].cells(digit).toArray(new Cell[0]);
					if (ccells.length != cells.size()) {
						h.hints.add("Partial position:");
						h.hints.add("digit " + digit);
						h.hints.add("in column " + (col + 1) + " must be in zone " + (zone + 1));						
						for (Cell ccell : ccells) {
							if (!cells.contains(ccell)) {
								h.digit.add(digit);
								h.position.add(ccell.getPosition());
							}
						}
						return h;
					}
				}
				if (oneLine) {
					Cell [] ccells = this.lines[line].cells(digit).toArray(new Cell[0]);
					if (ccells.length != cells.size()) {
						h.hints.add("Partial position:");
						h.hints.add("digit " + digit);
						h.hints.add("in line " + (line + 1) + " must be in zone " + (zone + 1));						
						for (Cell ccell : ccells) {
							if (!cells.contains(ccell)) {
								h.digit.add(digit);
								h.position.add(ccell.getPosition());
							}
						}
						return h;
					}
				}
			}
		}
		
		h.level = 8; // X-Wing
		for (int digit = 1; digit <= 9; digit++) {
			for (int leftColumn = 0; leftColumn < 9; leftColumn++) {
				Cell [] col1 = this.columns[leftColumn].cells(digit).toArray(new Cell[0]);
				if (col1.length == 2) {
					int l1 = col1[0].getPosition().L();
					int l2 = col1[1].getPosition().L();
					for (int rightColumn = leftColumn + 1; rightColumn < 9; rightColumn++) {
						Set<Cell> scol2 = this.columns[rightColumn].cells(digit); 
						if (scol2.size() == 2) {
							Iterator<Cell> it = scol2.iterator();
							int lc1 = it.next().getPosition().L();
							int lc2 = it.next().getPosition().L();
							if ((lc1 == l1 && lc2 == l2) || (lc1 == l2 && lc2 == l1)) {
								int [] lines = {l1, l2};
								for (int l : lines) {
									for (Cell c : this.lines[l].cells(digit)) {
										int col = c.getPosition().C();
										if (col != leftColumn && col != rightColumn) {
											if (h.hints.size() == 0) {
												h.hints.add("X-Wing for digit " + digit);
												h.hints.add("in column " + (leftColumn + 1) + " and column " + (rightColumn + 1));
											}
											h.hints.add("can not be in " + c.getPosition());
											h.digit.add(digit);
											h.position.add(c.getPosition());
										}
									}
								}
								if (h.hints.size() != 0) {
									return h;
								}
							}
						}
					}
				}
			}
			for (int leftLine = 0; leftLine < 9; leftLine++) {
				Cell [] lin1 = this.lines[leftLine].cells(digit).toArray(new Cell[0]);
				if (lin1.length == 2) {
					int c1 = lin1[0].getPosition().C();
					int c2 = lin1[1].getPosition().C();
					for (int rightLine = leftLine + 1; rightLine < 9; rightLine++) {
						Set<Cell> slin2 = this.lines[rightLine].cells(digit); 
						if (slin2.size() == 2) {
							Iterator<Cell> it = slin2.iterator();
							int lc1 = it.next().getPosition().C();
							int lc2 = it.next().getPosition().C();
							if ((lc1 == c1 && lc2 == c2) || (lc1 == c2 && lc2 == c1)) {
								int [] columns = {c1, c2};
								for (int cls : columns) {
									for (Cell c : this.columns[cls].cells(digit)) {
										int lin = c.getPosition().L();
										if (lin != leftLine && lin != rightLine) {
											if (h.hints.size() == 0) {
												h.hints.add("X-Wing for digit " + digit);
												h.hints.add("in line " + (leftLine + 1) + " and line " + (rightLine + 1));
											}
											h.hints.add("can not be in " + c.getPosition());
											h.digit.add(digit);
											h.position.add(c.getPosition());
										}
									}
								}
								if (h.hints.size() != 0) {
									return h;
								}
							}
						}
					}
				}
			}
		}
		h.level = 9; // Y-Wing
		for (int i = 0; i < 81; i++) {
			Cell cell = this.grille[i];
			int [] candidates = cell.candidates();
			if (candidates.length != 2) {
				continue;
			}
			int l = cell.getPosition().L();
			int c = cell.getPosition().C();
			for (int x = 0; x < 9; x++) {
				Cell serre1 = this.grille[x + l * 9];
				if (serre1 == cell) { continue; }
				int [] candidates1 = serre1.candidates();
				if (candidates1.length != 2) { continue; }
				if (candidates[1] == candidates1[0]) {
					int tmp = candidates[1];
					candidates[1] = candidates[0];
					candidates[0] = tmp;
				} else if (candidates[1] == candidates1[1]) {
					int tmp = candidates[1];
					candidates[1] = candidates[0];
					candidates[0] = tmp;
					tmp = candidates1[1];
					candidates1[1] = candidates1[0];
					candidates1[0] = tmp;
				} else {
					continue;
				}
				for (int y = 0; y < 9; y++) {
					Cell serre2 = this.grille[c + y * 9];
					if (serre2 == cell) { continue; }
					int [] candidates2 = serre2.candidates();
					if (candidates2.length != 2) { continue; }
					if (candidates2[1] == candidates[1]) {
						int tmp = candidates2[1];
						candidates2[1] = candidates2[0];
						candidates2[0] = tmp;
					}
					if (candidates2[0] == candidates[1] && candidates2[1] == candidates1[1]) {
						Cell proie = this.grille[x + y * 9];
						if (proie.isCandidate(candidates2[1])) {
							h.digit.add(candidates2[1]);
							h.position.add(proie.getPosition());
							h.hints.add("Y-Wing");
							h.hints.add("Eagle is " + cell.getPosition());
							return h;
						}
					}
				}
			}
		}
		h.level = 10; // W-Wing
		// for each pair of cells containing the same two digit wx,
		// a cell visible by both can not have w as candidates if there is 
		// a strong link on x
		
		h.level = 13; // colouring
		int [] l = new int[3];
		for (int digit = 1; digit <= 9; digit++) {
			Colouring dlx = new Colouring();
			for (int c = 0; c < 81; c++) {
				if (this.grille[c].isCandidate(digit)) {
					Position p = this.grille[c].getPosition();
					l[0] = 1 + p.C();
					l[1] = 1 + 9 + p.L();
					l[2] = 1 + 9 + 9 + p.Z();
					dlx.addRow(l);
				}
			}
			if (dlx.solve(h, digit, this.grille)) {
				return h;
			}
		}
		
		System.out.println("No hint");
		return null;
	}
	public void knownDigit (int line, int column, int digit) {
		Position p = new Position(line, column);
		Cell c = grille[p.I()];
		for (int d = 1; d <= 9; d++) {
			if (d != digit) {
				this.columns[p.C()].remove(d, c);
				this.lines[p.L()].remove(d, c);
				this.zones[p.Z()].remove(d, c);
				c.removeCandidate(d);
			}
		}
		assert c.candidates().length == 1;
	}
	public void removeCandidate (int line, int column, int digit) {
		Position p = new Position(line, column);
		Cell c = grille[p.I()];
		this.columns[p.C()].remove(digit, c);
		this.lines[p.L()].remove(digit, c);
		this.zones[p.Z()].remove(digit, c);
		c.removeCandidate(digit);
	}

	public int nbSolutions() {
		SolutionCounter sc = new SolutionCounter();
		for (Cell c : this.grille) {
			Position p = c.getPosition();
			int [] d = c.candidates();
			if (d.length == 1) {
				sc.knownDigit(p.L(), p.C(), d[0]);
			}
		}
		return sc.solve(1000);
	}

	public Sudoku() {
		grille = new Cell[81];
		columns = new Area[9];
		lines = new Area[9];
		zones = new Area[9];
		for (int i = 0; i < 9; i++) {
			columns[i] = new Area();
			lines[i] = new Area();
			zones[i] = new Area();
		}
		for (int i = 0; i < 81; i++) {
			int candidates[] = new int[9];
			for (int j = 0; j < 9; j++) { candidates[j] = j + 1; }
			Position p = new Position(i);
			Cell c = new Cell(candidates, p);
			grille[i] = c;
			for (int d : candidates) {
				this.columns[p.C()].add(d, c);
				this.lines[p.L()].add(d, c);
				this.zones[p.Z()].add(d, c);
			}
		}
	}
	
	static class Colouring extends DancingLinks {
		int [] canBePresent;
		int digit, nbSolution;
		public Colouring() {
			super(27, 244);
			this.nbSolution = 0;
			this.canBePresent   = new int[81];
			for (int i = 0; i < 81; i++) {
				this.canBePresent[i] = 0;
				
			}
		}
		public boolean solution(int[][] res) {
			//System.out.println("********* a solution has been found");
			this.nbSolution++;
			for (int[] row : res) {
				assert row[0] >= 1;
				assert row[0] <= 9;
				assert row[1] >= 10;
				assert row[1] <= 18;
				assert row[2] >= 19;
				assert row[2] <= 27;
				this.canBePresent[row[0]-1 + (row[1]-1-9)*9]++;
			}
			return true;
		}
		public boolean solve(Hint h, int digit, Cell[] grille) {
			this.search(0);
			for (int c = 0; c < 81; c++) {
				if (! grille[c].isCandidate(digit)) {
					continue;
				}
				if (grille[c].candidates().length == 1) {
					continue;
				}
				if (this.canBePresent[c] == 0) {
					h.digit.add(digit);
					h.position.add(grille[c].getPosition());
					h.hints.add("Colouring: digit " + digit + " is not possible in " + grille[c].getPosition());
					// return true;
				}
				if (this.canBePresent[c] == this.nbSolution) {
					for (int d : grille[c].candidates()) {
						if (d != digit) {
							h.digit.add(d);
							h.position.add(grille[c].getPosition());
						}
					}
					h.hints.add("Colouring: digit " + digit + " must be in " + grille[c].getPosition());
					//return true;
				}
			}
			return h.hints.size() > 0;
		}
	}

	static class SolutionCounter extends DancingLinks {
		int nbSolution;
		private int maxCount;
		public SolutionCounter() {
			super(9*9*4, 9 * 9 * 9 * 4);
			this.nbSolution = 0;
			int [] row = new int[4];
			for (int l = 0; l < 9; l++) {
				for (int c = 0; c < 9; c ++) {
					row[0] = l * 9 + c + 1;
					int v1 = 81 + c * 9;
					int v2 = 81 + 81 + l * 9;
					int z = l - l % 3 + c / 3;
					int v3 = 81 + 81 + 81 + z * 9;
					for (int digit = 1; digit <= 9; digit++) {
						row[1] = v1 + digit;
						row[2] = v2 + digit;
						row[3] = v3 + digit;
						this.addRow(row);
					}
				}
			}
		}
		public void knownDigit(int l, int c, int digit) {
			int z = l - l % 3 + c / 3;
			int c0 = l * 9 + c + 1;
			int c1 = 81 + c * 9 + digit;
			int c2 = 81 + 81 + l * 9 + digit;
			int c3 = 81 + 81 + 81 + z * 9 + digit;
			int cellNum = this.nbColumns * (1 + c0) + digit;
			assert this.C[cellNum] == c0;
			assert this.C[cellNum + 1] == c1;
			assert this.C[cellNum + 2] == c2;
			assert this.C[cellNum + 3] == c3;
			this.coverColumn(c0);
			this.coverColumn(c1);
			this.coverColumn(c2);
			this.coverColumn(c3);
		}
		public boolean solution(int[][] res) {
			this.nbSolution++;
			return this.nbSolution < this.maxCount;
		}
		public int solve(int maxCount) {
			this.maxCount = maxCount;
			this.search(0);
			return this.nbSolution;
		}
	}

}

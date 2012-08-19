package nu.mine.bocquet.sudoku;

/**
 * 
 */

/**
 * @author le nombre 23
 *
 */
public class Main {

	public static void setup(Sudoku s, String l1, String l2, String l3, String l4, String l5,
			String l6, String l7, String l8, String l9) {
		String [] lines = new String[]{l1, l2, l3, l4, l5, l6, l7, l8, l9};
		for (int l = 0; l < lines.length; l++) {
			String line = lines[l];
			for (int col = 0; col < line.length(); col++) {
				char d = line.charAt(col);
				switch (d) {
				case '.': break;
				case '1' : case '2' : case '3' : case '4' : case '5' :
				case '6' : case '7' : case '8' : case '9' :
					s.knownDigit(l, col, d - '0');
					break;
				default:
					assert false;
				}
			}
		}
	}
	public static void main0 (String[] args) {
		Integer [] a = {0, 1, 2, 3, 4, 5};
		Perm<Integer> permutations = new Perm<Integer>(a, 2, a.length -2);
		for (Pair<Integer[]> pair : permutations) {
			Integer[] p = pair.first;
			Integer[] q = pair.second;
			System.out.print ("Iteration: ");
			for (Integer c : p) {
				System.out.print (c + " ");
			}
			System.out.print (" - ");
			for (Integer c : q) {
				System.out.print (c + " ");
			}
			System.out.println();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Sudoku s = new Sudoku();
// 		setup(s,
//				".1.3.7.5.",
//				"7.......3",
//				"...8.1...",
//				"4.5...3.7",
//				"....3....",
//				"8.3...5.2",
//				"...9.6...",
//				"6.......5",
//				".9.1.4.6.");
// 		setup(s,
//				"9...8.2..",
//				"...27....",
//				"3.1.4...7",
//				".97.....5",
//				".6.....8.",
//				"4.....37.",
//				"6...2.5.3",
//				"....93...",
//				"..4.5...6"); 		
// 		setup(s,  // X-Wing Swordfish (W-Wing Tigre 4*Cobra) ou (APE 3*Cobra)
//				"9...1...4",
//				"...3.7...",
//				"..5...2..",
//				".6.2.4.3.",
//				"4.......7",
//				".1.9.6.8.",
//				"..2...5..",
//				"...6.8...",
//				"7...4...1");
// 		setup(s,  // coloriage Y-Wing Swordfish griffe du tigre 3*cobra approche du tigre
//				"4.......9",
//				".5.2.7.4.",
//				"..8...1..",
//				".2.3.9.8.",
//				".........",
//				".1.5.6.7.",
//				"..6...5..",
//				".7.1.8.3.",
//				"9.......4");
// 		s.removeCandidate(4, 4, 4);
// 		setup(s,  // 7-8 n�45 6juil/6ao�t
//				".....3.2.",
//				"..5...7..",
//				".4..1....",
//				".3..5..4.",
//				"..6..8...",
//				"9..2.4..1",
//				"..8..6...",
//				"1..7..9..",
//				".2.....3.");
// 		setup(s,  // 7-8 n�15 6juil/6ao�t
//				".6..4..2.",
//				".1..7.3..",
//				"9.....5..",
//				".8...4.6.",
//				"4..32....",
//				".5....1..",
//				"6.....4..",
//				"..7..9...",
//				"...1..8.."); 		
 		setup(s,  // n�48
				"...8..5..",
				".3..4...6",
				"....1.2..",
				".4..3....",
				"6......7.",
				"..8..9...",
				"2...6..1.",
				".9..2..4.",
				".5.7..8.."); 		
// 		setup(s,  // n�49
//				".8..2.5..",
//				".7..9..6.",
//				"..3..4...",
//				".6.....9.",
//				"..1..8...",
//				"4..5...7.",
//				"...1...8.",
//				".2.6..3..",
//				"5...7...."); 		
// 		setup(s,  // n�50
//				"....3....",
//				"1..7..6..",
//				".4...9...",
//				"..5.6....",
//				"2.8...1.4",
//				"7..2...9.",
//				"9....1.2.",
//				".3.5.....",
//				"..6....7."); 		
 		System.out.println ("**** Nb of solutions: " + s.nbSolutions());
 		
 		Hint h;
		int count [] = new int[20];
		while ((h = s.hint()) != null) {
			if (h.level >= 3) {
				System.out.println ("Hint of level " + h.level);
				for (String hs : h.hints) {
					System.out.println(hs);
				}
			}
			for (int i = 0; i < h.position.size(); i++) {
				s.removeCandidate(h.position.get(i).L(), h.position.get(i).C(), h.digit.get(i));
			}
			count[h.level]++;
		}
		s.dump(System.out);
		for (int i = 0; i < count.length; i++) {
			if (count[i] > 0) {
				System.out.println("Count of level " + i + " -> " + count[i]);
			}
		}
 		System.out.println ("**** Nb of solutions: " + s.nbSolutions());

		DancingLinks dlx = new DancingLinks(7, 49);
		dlx.addRow(new int[]{1, 4, 7});
		dlx.addRow(new int[]{1, 4});
		dlx.addRow(new int[]{4, 5, 7});
		dlx.addRow(new int[]{3, 5, 6});
		dlx.addRow(new int[]{2, 3, 6, 7});
		dlx.addRow(new int[]{2, 7});
		dlx.search(0);
		System.out.println("Fini");
	}
}

package nu.mine.bocquet.sudoku;

public class DancingLinks {
	int nbColumns;
	int nbLines;
	int [] R, L, D, U; // right, left, down, up;
	int [] C; // column
	int [] S; // nb of rows of the column
	int [] O; // the list of rows of the solution
	int lastCell;
	
	public DancingLinks (int nbColumns, int nbCells) {
		int maxNbCells = nbColumns + nbCells + 1;
		this.nbColumns = nbColumns;
		this.R = new int[maxNbCells];
		this.L = new int[maxNbCells];
		this.D = new int[maxNbCells];
		this.U = new int[maxNbCells];
		this.C = new int[maxNbCells];
		this.S = new int[this.nbColumns + 1];
		for (int i = 0; i <= this.nbColumns; i++) {
			this.C[i] = i;
			this.R[i] = i + 1;
			this.L[i] = i - 1;
			this.D[i] = this.U[i] = i;
			this.S[i] = 0;
		}
		this.L[0] = this.nbColumns;
		this.R[this.nbColumns] = 0;
		this.O = new int[this.nbColumns];
		this.lastCell = this.nbColumns + 1;
	}
	
	public void addRow(int [] c) {
		int prev = 0;
		for (int v : c) {
			assert v > 0;
			assert v <= this.nbColumns;
			int newCell = this.lastCell++;
			if (prev == 0) {
				L[newCell] = R[newCell] = newCell;
			} else {
				L[newCell] = prev; R[newCell] = R[prev];
				R[prev] = newCell; L[R[newCell]] = newCell;
			}
			C[newCell] = v;
			U[newCell] = U[v]; D[newCell] = v;
			U[v] = newCell; D[U[newCell]] = newCell;
			prev = newCell;
		}
	}
	
    final void coverColumn(final int c) {
		assert c <= this.nbColumns;
		L[R[c]] = L[c];
    	R[L[c]] = R[c];
    	for (int i = D[c] ; i != c ; i = D[i]){
    		for (int j = R[i] ; j != i ; j = R[j]) {
    			U[D[j]] = U[j];
    			D[U[j]] = D[j];
    			S[C[j]]--;
    		}
    	}
    }

    private final void uncoverColumn(final int c) {
		assert c <= this.nbColumns;
		for (int i = U[c]; i!= c; i = U[i]) {
    		for (int j = L[i] ; j != i; j = L[j]) {
    			U[D[j]] = j;
    			D[U[j]] = j;
    			S[C[j]]++;
    		}
    	}
        L[R[c]] = c;
        R[L[c]] = c;
    }
    
    public boolean solution(int[][] res) {
		System.out.println("A solution has been found");
		for (int[] row : res) {
			for (int j : row) {
				System.out.print(j + " ");
			}
			System.out.println();
		}
		return true; // continue search
    }
    
    /** Solves an exact cover problem using Algorithm X (dancing links). */
    public boolean search(int k) {
    	if (R[0] == 0) {
    		int [][] l_res = new int[k][];
    		int [] l_tmp = new int[this.nbColumns];
    		for (int row = 0; row < k; row++) {
    			int t0 = O[row];
    			while (L[t0] < t0) { t0 = L[t0]; }
    			int t = t0;
    			int i = 0;
    			do {
    				l_tmp[i++] = C[t];
    				t = R[t];
    			} while (t != t0);
    			l_res[row] = java.util.Arrays.copyOf(l_tmp, i);
    		}
    		return solution(l_res);
    	}
    	int c = R[0];
    	// choose a column
    	for (int cc = R[c]; cc != 0; cc = R[cc]) {
    		if (S[cc] < S[c]) {
    			c = cc;
    			//if (S[c] == 1) {
    			//	break;
    			//}
    		}
    	}
    	// assert S[c] > 0; TODO
    	assert c <= this.nbColumns;
    	// System.out.println ("Exploring column " + c);
    	coverColumn(c);
    	for (int r = D[c]; r != c; r = D[r]) {
    		O[k] = r;
    		// System.out.print ("Covering columns ");
    		for (int j = R[r] ; j != r; j = R[j]) {
    			// System.out.print(C[j] + " ");
    			coverColumn(C[j]);
    		}
    		// System.out.println ();
    		if (search(k+1) == false) {
    			return false;
    		}
    		// System.out.print ("Uncovering columns ");
    		for (int j = L[r]; j != r; j = L[j]) {
    			// System.out.print(C[j] + " ");
    			uncoverColumn(C[j]);
    		}
    		// System.out.println ();
    	}
    	// System.out.println ("Finish exploration of column " + c);
    	uncoverColumn(c);
        return true;
    }
}

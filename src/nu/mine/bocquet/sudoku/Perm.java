package nu.mine.bocquet.sudoku;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Perm<E> implements Iterable<Pair<E[]>> {
	E[] elements;
	int minNb, maxNb;
	public Perm (E[] elements, int minNb, int maxNb) {
		this.elements = elements;
		this.minNb = minNb;
		this.maxNb = maxNb;
		assert minNb <= maxNb;
		assert minNb >= 1;
		assert maxNb <= elements.length;
	}
	
	@Override
	public Iterator<Pair<E[]>> iterator() {
		return new PermIterator();
	}
	
	class PermIterator implements Iterator<Pair<E[]>> {
		int currentNb;
		boolean m_hasNext;
		int [] state;
		PermIterator() {
			this.currentNb = Perm.this.minNb;
			this.m_hasNext = true;
			this.state = new int[Perm.this.elements.length];
			for (int i = 0; i < this.state.length; i++) {
				this.state[i] = i;
			}
		}
		@Override
		public boolean hasNext() {
			return this.m_hasNext;
		}
		@Override
		public Pair<E[]> next() {
			if (!this.m_hasNext) throw new NoSuchElementException();
			@SuppressWarnings("unchecked")
			E [] choosen = (E[]) Array.newInstance(Perm.this.elements[1].getClass(), this.currentNb);
			@SuppressWarnings("unchecked")
			E [] notChoosen = (E[]) Array.newInstance(Perm.this.elements[1].getClass(), Perm.this.elements.length - this.currentNb);
			Pair<E[]> res = new Pair<E[]>(choosen, notChoosen);
			for (int i = 0; i < choosen.length; i++) {
				choosen[i] = Perm.this.elements[this.state[i]];
			}
			int nbNotChoosen = 0;
			int theMax = Perm.this.elements.length - 1;
			for (int i = choosen.length-1 ; i >= 0; i--) {
				while (this.state[i] < theMax) {
					notChoosen[nbNotChoosen++] = Perm.this.elements[theMax--];
				}
				theMax--;
			}
			while (0 <= theMax) {
				notChoosen[nbNotChoosen++] = Perm.this.elements[theMax--];
			}
			
			int prevMax = Perm.this.elements.length;
			for (int i = this.currentNb - 1; i >= 0; i--) {
				if (this.state[i] < prevMax - 1) {
					this.state[i]++;
					while (++i < this.currentNb) {
						this.state[i] = this.state[i-1]+1;
					}
					return res;
				}
				prevMax = this.state[i];
			}
			this.currentNb++;
			for (int i = 0; i < this.state.length; i++) {
				this.state[i] = i;
			}
			this.m_hasNext = this.currentNb <= Perm.this.maxNb;
			return res;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}		
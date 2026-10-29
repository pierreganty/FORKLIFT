package engine;

import java.util.Iterator;

import tuples.Pair;


public class ContextIterator
implements Iterator<Pair<Pair<Integer, Integer>, Boolean>> {

	private final SetOfContexts cxt;
	private StateRelation rel;
	private boolean change;
	private Iterator<Pair<Integer, Integer>> iter;

	public ContextIterator (SetOfContexts cxt) {
		this.cxt = cxt;
		change = false;
		rel = cxt.getFirst(); // start with false is important here see "next"
		iter = rel.iterator();
	}

	public boolean hasNext() {
		if (iter.hasNext()) return true;
		if (change == false) {
			change = true;
			rel = cxt.getSecond();
			iter = rel.iterator();
			return hasNext();
		}
		return false;
	}

	public Pair<Pair<Integer, Integer>, Boolean> next() {
		if (this.hasNext()) return new Pair<>(iter.next(), change);
		return null;
	}
}

package engine;

import java.util.Iterator;

import tuples.Pair;

public class StateRelationIterator
implements Iterator<Pair<Integer, Integer>> {

	private final StateRelation rel;
	private Integer state_key = null;
	Iterator<Integer> iter_key;
	Iterator<Integer> iter_value;

	public StateRelationIterator (StateRelation rel) {
		this.rel = rel;
		iter_key = rel.keySet().iterator();
		iter_value = new EmptyIterator<>();
	}

	public boolean hasNext() {
		if (iter_value.hasNext()) return true;
		if (iter_key.hasNext()) {
			state_key = iter_key.next();
			iter_value = rel.get(state_key).iterator();
			return hasNext();
		}				
		return false;
	}

	public Pair<Integer, Integer> next() {
		if (this.hasNext()) return new Pair<>(state_key, iter_value.next());
		return null;
	}
}

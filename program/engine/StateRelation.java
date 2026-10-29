package engine;

import java.util.*;
import tuples.*;


public final class StateRelation
extends HashMap<Integer, SetOfStates>
implements QuasiOrder<StateRelation>, Iterable<Pair<Integer, Integer>> {

	public void add (Integer initB, Integer stateB) {
		Set<Integer> set = computeIfAbsent(initB, k -> new SetOfStates());
		set.add(stateB);	
	}
	
	public boolean addAll (Integer initB, Collection<Integer> stateB) {
		Set<Integer> set = computeIfAbsent(initB, k -> new SetOfStates());
		return set.addAll(stateB);
	}
	
	public boolean contains (Integer initB, Integer stateB) {
		Set<Integer> set = get(initB);
		if (set == null) return false;
		return set.contains(stateB);
	}

	public boolean smaller_than (StateRelation rel) {
		for (Integer fromB : keySet()) {
			if (rel.get(fromB) == null) return false;
			for (Integer state : get(fromB))
				if (rel.get(fromB).contains(state) == false) return false;
		}
		return true;
	}

	public String toString() {
		StringJoiner outersj = new StringJoiner(";", "{", "}" );
		for (Pair<Integer, Integer> pair : this) {
			StringJoiner sj = new StringJoiner(",", "(", ")" );
			sj.add("STATE_" + pair.first());
			sj.add("STATE_" + pair.second());
			outersj.add(sj.toString());
		}
		return outersj.toString();
	}


	public Iterator<Pair<Integer, Integer>> iterator() {
		return new StateRelationIterator(this);
	}
}

	
	
package engine;

import java.util.*;

import automata.*;
import tuples.*;


class PostVariable<T extends QuasiOrder<T>> {
	private final Map<Integer, Set<Pair<T, Word>>> content = new HashMap<>();



	//+++++++++++++++ Getters +++++++++++++++
	public int size () {
		int count = 0;
		// We can't check isFinal without StateManager, so just count all
		for (Integer key : content.keySet()) {
			count += content.get(key).size();
		}
		return count;
	}
	boolean isEmpty () {return content.isEmpty();}
	Set<Integer> keySet () {return content.keySet();}
	Set<Pair<T, Word>> get (Integer stateA) {return content.get(stateA);}
	Set<Pair<T, Word>> getNotNull (Integer stateA) { return content.getOrDefault(stateA, new HashSet<>()); }	
	//+++++++++++++++ Getters +++++++++++++
	

	void add (Integer q, T set, Word w) {
		Set<Pair<T, Word>> set_set = content.computeIfAbsent(q, k -> new HashSet<>());
		set_set.add(new Pair<>(set, w));
	}
	
	
	boolean addIfMin (Integer q, T new_set, Word w) {
		Set<Pair<T, Word>> set_set = content.computeIfAbsent(q, k -> new HashSet<>());
		for (Iterator<Pair<T, Word>> i = set_set.iterator(); i.hasNext(); ) {
			T set = i.next().first();
			if (set.smaller_than(new_set) == true) return false;
			if (new_set.smaller_than(set) == true) i.remove();
		}
		set_set.add(new Pair<>(new_set, w));
		return true;
	}

	
	boolean addIfMax (Integer q, T new_set, Word w) {
		Set<Pair<T, Word>> set_set = content.computeIfAbsent(q, k -> new HashSet<>());
		for (Iterator<Pair<T, Word>> i = set_set.iterator(); i.hasNext(); ) {
			T set =  i.next().first();
			if (new_set.smaller_than(set) == true) return false;
			if (set.smaller_than(new_set) == true) i.remove();
		}
		set_set.add(new Pair<>(new_set, w));
		return true;
	}


	public String toString() {
		if (this.isEmpty())
			return "$$ empty post variable $$";
		else {
			StringBuilder s = new StringBuilder();
			for (Integer stateId : keySet()) {
				s.append("AT STATE_" + stateId);
				StringJoiner outsj = new StringJoiner("\n  ", ":\n  ", "\n");
				for (Pair<T, Word> pair : this.get(stateId)) {
					StringJoiner sj = new StringJoiner("\n  ", "> ", "");
					sj.add(pair.second().toString());
					sj.add(pair.first().toString());
					outsj.add(sj.toString());
				}
				s.append(outsj).append("\n");
			}
			return s.toString();
		}
	}
}



package engine;

import java.util.*;
import java.util.stream.Collectors;


public class SetOfStates extends HashSet<Integer> implements QuasiOrder<SetOfStates> {
	public boolean smaller_than (SetOfStates set) { return set.containsAll(this); }
	
	public String toString() {
		return this.stream()
			.map(id -> "STATE_" + id)
			.collect(Collectors.joining(";", "{", "}"));
	}
}

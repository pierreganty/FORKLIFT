package automata;

import engine.*;
import tuples.Pair;
import java.util.*;

/**
 * State is now represented as an integer ID.
 * Use StateManager for state ID to metadata mapping and operations.
 * This type is kept for backward compatibility but is just an int wrapper.
 */
public record State(int id) {
	public int hashCode() { return 31 * id; }
	
	public boolean equals(Object other) {
		if (other instanceof State) return id == ((State) other).id;
		return false;
	}
	
	public String toString() {
		return "STATE_ID_" + id;
	}
	
	public String getName() {
		// Can't resolve without StateManager reference
		return "STATE_ID_" + id;
	}
	
	public boolean isFinal() {
		// Can't resolve without StateManager reference
		return false;
	}
	
	public SetOfStates getSuccessors(int symbol) {
		// Can't resolve without StateManager reference
		return null;
	}
	
	public Set<Integer> getSymbols() {
		// Can't resolve without StateManager reference
		return new HashSet<>();
	}
	
	public HashSet<Pair<State, Boolean>> getSuccessorsFNotNull(Word word) {
		return new HashSet<>();
	}
	
	public SetOfStates getSuccessorsNotNull(Word word) {
		return new SetOfStates();
	}
	
	public int getId() { return id; }
}

package automata;

import engine.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for state ID to metadata mapping.
 * Each automaton has its own StateManager instance.
 * Provides O(1) lookup from int ID to state properties.
 */
public class StateManager {
	private final List<String> stateNames = new ArrayList<>();
	private final List<Boolean> stateFinals = new ArrayList<>();
	private final List<SetOfStates[]> stateSuccessors = new ArrayList<>();
	private final int alphabetSize;
	
	public StateManager(int alphabetSize) {
		this.alphabetSize = alphabetSize;
	}
	
	/**
	 * Add a new state and return its ID.
	 */
	public int addState(String name, boolean isFinal) {
		int id = stateNames.size();
		stateNames.add(name);
		stateFinals.add(isFinal);
		stateSuccessors.add(new SetOfStates[alphabetSize]);
		return id;
	}
	
	/**
	 * Get the name for a state ID.
	 */
	public String getName(int stateId) {
		return stateNames.get(stateId);
	}
	
	/**
	 * Check if a state is final.
	 */
	public boolean isFinal(int stateId) {
		return stateFinals.get(stateId);
	}
	
	/**
	 * Get successors for a state and symbol.
	 */
	public SetOfStates getSuccessors(int stateId, int symbolId) {
		SetOfStates[] succ = stateSuccessors.get(stateId);
		if (symbolId < 0 || symbolId >= succ.length) return null;
		return succ[symbolId];
	}
	
	/**
	 * Add a successor for a state and symbol.
	 */
	public void addSuccessor(int fromStateId, int symbolId, int toStateId) {
		SetOfStates[] succ = stateSuccessors.get(fromStateId);
		if (succ[symbolId] == null) {
			SetOfStates singleton = new SetOfStates();
			singleton.add(toStateId);
			succ[symbolId] = singleton;
		} else {
			succ[symbolId].add(toStateId);
		}
	}
	
	/**
	 * Get the number of states registered.
	 */
	public int size() {
		return stateNames.size();
	}
	
	/**
	 * Get the alphabet size.
	 */
	public int getAlphabetSize() {
		return alphabetSize;
	}
	
	/**
	 * Get a string representation of a state ID.
	 */
	public String toString(int stateId) {
		return getName(stateId);
	}
	
	/**
	 * Get the set of symbol IDs for which a state has successors.
	 */
	public java.util.Set<Integer> getSymbols(int stateId) {
		java.util.Set<Integer> symbols = new java.util.HashSet<>();
		SetOfStates[] succ = stateSuccessors.get(stateId);
		for (int i = 0; i < succ.length; i++) {
			if (succ[i] != null) symbols.add(i);
		}
		return symbols;
	}
}

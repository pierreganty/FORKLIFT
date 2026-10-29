package automata;

import java.nio.file.Path;
import java.util.*;
import engine.*;
import tuples.*;


public class BuchiAutomaton {
	private final int alphabetSize;
	private final SetOfStates states;
	private final int init;
	private final SetOfStates finals;
	private final StateManager stateManager;
	private final SymbolManager symbolManager;
	// -- Transitions are stored through StateManager


	// +++++++++++++++ Getters +++++++++++++++
	public int getAlphabetSize() {return alphabetSize;}
	public SetOfStates getStates() {return states;}
	public int getInitialState() {return init;}
	public SetOfStates getFinalStates() {return finals;}
	public StateManager getStateManager() {return stateManager;}
	public SymbolManager getSymbolManager() {return symbolManager;}
	
	// For backward compatibility with old code that passes Set<Symbol>
	public Set<Symbol> getAlphabet() {
		Set<Symbol> alphabet = new HashSet<>();
		for (int i = 0; i < alphabetSize; i++) {
			alphabet.add(new Symbol(i));
		}
		return alphabet;
	}
	// +++++++++++++ Getters +++++++++++++



	public BuchiAutomaton(Path file, final Set<Symbol> stating_alphabet) throws Exception {
		this(file, stating_alphabet, null);
	}
	
	public BuchiAutomaton(Path file, final Set<Symbol> stating_alphabet, SymbolManager sharedSymbolManager) throws Exception {
		AutomatonParser parser = new AutomatonParser(file);

		// -- Symbols: use shared SymbolManager if provided
		symbolManager = (sharedSymbolManager != null) ? sharedSymbolManager : new SymbolManager();
		
		Map<String, Integer> symbols_register = new HashMap<>();
		
		// First, register symbols from stating_alphabet
		for (Symbol a : stating_alphabet) {
			int id = symbolManager.addSymbol(a.getName());
			symbols_register.put(a.getName(), id);
		}
		
		// Then register symbols from the file
		for (String name : parser.getAlphabet()) {
			if (symbols_register.containsKey(name) == false) {
				int id = symbolManager.addSymbol(name);
				symbols_register.put(name, id);
			}
		}
		
		alphabetSize = symbolManager.size();
		
		// Create StateManager for this automaton
		stateManager = new StateManager(alphabetSize);

		// -- States
		Map<String, Integer> states_register = new HashMap<>();
		states = new SetOfStates();
		finals = new SetOfStates();
		
		for (String name : parser.getStates()) {
			boolean isFinal = parser.getFinals().contains(name);
			int stateId = stateManager.addState(name, isFinal);
			states_register.put(name, stateId);
			states.add(stateId);
			if (isFinal) finals.add(stateId);
		}
		init = states_register.get(parser.getInitial());

		// -- Edges
		for(Triplet<String,String,String> t : parser.getTransitions()) {
			int from = states_register.get(t.first());
			int symbolId = symbols_register.get(t.second());
			int to = states_register.get(t.third());
			stateManager.addSuccessor(from, symbolId, to);
		}
	}


	public String toString() {
		StringBuilder s = new StringBuilder("Automaton:");
		s.append("\n  States: ").append(states);
		s.append("\n  Initial: ").append(stateManager.toString(init));
		s.append("\n  Finals: ").append(finals);
		s.append("\n  Transitions:\n");
		for (Integer stateId : states) {
			for (int symbolId = 0; symbolId < alphabetSize; symbolId++) {
				SetOfStates succ = stateManager.getSuccessors(stateId, symbolId);
				if (succ != null && !succ.isEmpty()) {
					s.append("    ").append(stateManager.toString(stateId));
					s.append(", ").append(symbolManager.toString(symbolId));
					s.append(" -> ").append(succ.toString());
					s.append("\n");
				}
			}
		}
		return s.toString();
	}


	public boolean membership (SetOfStates U, Word period) {
		Set<Triplet<Integer, Integer, Boolean>> S = new HashSet<>();
		Set<Pair<Integer, Integer>> P = new HashSet<>();
		
		for (Integer start : U) {
			S.clear(); P.clear();
			if (reachable_final_product(start, 0, period, S, P)) return true;
		}
		return false;
	}
	
	private boolean reachable_final_product (int from, int i, Word period, Set<Triplet<Integer, Integer, Boolean>> S, Set<Pair<Integer, Integer>> P) {
		S.add(new Triplet<>(from, i, false));
		P.add(new Pair<>(from, i));

		SetOfStates posts = stateManager.getSuccessors(from, period.getIndex(i));
		if (posts != null) {
			for (Integer to : posts) {
				int ii = (i+1 == period.getLength())?0:i+1;				
				if (S.contains(new Triplet<>(to, ii, false)) == false) {
					if (reachable_final_product(to, ii, period, S, P)) return true;
				}
			
				if (stateManager.isFinal(to)) {	
					if (P.contains(new Pair<>(to, ii))) return true;
					if (S.contains(new Triplet<>(to, ii, true)) == false) {
						if (iterable_final_product(to, ii, period, S, P)) return true;
					}
				}
			}
		}

		

		P.remove(new Pair<>(from, i));
		return false;
	}
	
	private boolean iterable_final_product (int from, int i, Word period, Set<Triplet<Integer, Integer, Boolean>> S, Set<Pair<Integer, Integer>> P) {
		S.add(new Triplet<>(from, i, true));
		
		SetOfStates posts = stateManager.getSuccessors(from, period.getIndex(i));
		if (posts != null) {
			for (Integer to : posts) {
				int ii = (i+1 == period.getLength())?0:i+1;
				if (P.contains(new Pair<>(to, ii))) return true;
				if (S.contains(new Triplet<>(to, ii, true)) == false) { 
					if (iterable_final_product(to, ii, period, S, P)) return true;
				}
			}
		}
		return false;
	}

	
}





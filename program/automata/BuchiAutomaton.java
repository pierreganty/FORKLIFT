package automata;

import java.nio.file.Path;
import java.util.*;
import engine.*;
import tuples.*;


public class BuchiAutomaton {
	private final int alphabetSize;
	private final SetOfStates states = new SetOfStates();
	private final State init;
	private final SetOfStates finals = new SetOfStates();
	// -- Transitions are stored through states


	// +++++++++++++++ Getters +++++++++++++++
	public int getAlphabetSize() {return alphabetSize;}
	public SetOfStates getStates() {return states;}
	public State getInitialState() {return init;}
	public SetOfStates getFinalStates() {return finals;}
	// +++++++++++++++ Getters +++++++++++++++



	public BuchiAutomaton(Path file, final Set<Symbol> stating_alphabet) throws Exception {
		AutomatonParser parser = new AutomatonParser(file);

		// -- Symbols: register all symbol names in SymbolManager
		Map<String, Integer> symbols_register = new HashMap<>();
		
		// First, register symbols from stating_alphabet
		for (Symbol a : stating_alphabet) {
			int id = SymbolManager.addSymbol(a.getName());
			symbols_register.put(a.getName(), id);
		}
		
		// Then register symbols from the file
		for (String name : parser.getAlphabet()) {
			if (symbols_register.containsKey(name) == false) {
				int id = SymbolManager.addSymbol(name);
				symbols_register.put(name, id);
			}
		}
		
		alphabetSize = SymbolManager.size();

		// -- States
		int state_id_maker = 0;
		Map<String,State> states_register = new HashMap<>();
		for (String name : parser.getStates()) {
			State q;
			if (parser.getFinals().contains(name)) {
				q = new State(name, state_id_maker++, true, alphabetSize);
				finals.add(q);
			}
			else
				q = new State(name, state_id_maker++, false, alphabetSize);
			states_register.put(name, q);
			states.add(q);
		}
		init = states_register.get(parser.getInitial());

		// -- Edges
		for(Triplet<String,String,String> t : parser.getTransitions()) {
			State from = states_register.get(t.first());
			int symbolId = symbols_register.get(t.second());
			State to = states_register.get(t.third());
			from.addSuccessor(to, symbolId);
		}
	}


	public String toString() {
		StringBuilder s = new StringBuilder("Automaton:");
		s.append("\n  States: ").append(states);
		s.append("\n  Initial: ").append(init.toString());
		s.append("\n  Finals: ").append(finals);
		s.append("\n  Transitions:\n");
		for (State state : states) {
			for (int symbolId = 0; symbolId < alphabetSize; symbolId++) {
				if (state.getSuccessors(symbolId) != null) {
					s.append("    ").append(state);
					s.append(", ").append(SymbolManager.toString(symbolId));
					s.append(" -> ").append(state.getSuccessors(symbolId).toString());
					s.append("\n");
				}
			}
		}
		return s.toString();
	}


	public boolean membership (SetOfStates U, Word period) {
		Set<Triplet<State, Integer, Boolean>> S = new HashSet<>();
		Set<Pair<State, Integer>> P = new HashSet<>();
		
		for (State start : U) {
			S.clear(); P.clear();
			if (reachable_final_product(start, 0, period, S, P)) return true;
		}
		return false;
	}
	
	private boolean reachable_final_product (State from, int i, Word period, Set<Triplet<State, Integer, Boolean>> S, Set<Pair<State, Integer>> P) {
		S.add(new Triplet<State, Integer, Boolean>(from, i, false));
		P.add(new Pair<State, Integer>(from, i));

		SetOfStates posts = from.getSuccessors(period.getIndex(i));
		if (posts != null) {
			for (State to : posts) {
				int ii = (i+1 == period.getLength())?0:i+1;				
				if (S.contains(new Triplet<>(to, ii, false)) == false) {
					if (reachable_final_product(to, ii, period, S, P)) return true;
				}
			
				if (to.isFinal()) {	
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
	
	private boolean iterable_final_product (State from, int i, Word period,Set<Triplet<State, Integer, Boolean>> S, Set<Pair<State, Integer>> P) {
		S.add(new Triplet<State, Integer, Boolean>(from, i, true));
		
		SetOfStates posts = from.getSuccessors(period.getIndex(i));
		if (posts != null) {
			for (State to : posts) {
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





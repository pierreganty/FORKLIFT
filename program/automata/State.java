package automata;


import java.util.*;



import engine.*;
import tuples.Pair;


public class State {
	private final int id;
	private final String name;
	private final boolean is_final;
	private SetOfStates[] succ;


	// +++++++++++++++ Getters +++++++++++++++
	public boolean isFinal() {return is_final;}
	public String getName() {return name;}
	public int getId() {return id;}
	public Set<Integer> getSymbols () {
		Set<Integer> symbols = new HashSet<>();
		for (int i = 0; i < succ.length; i++) {
			if (succ[i] != null) symbols.add(i);
		}
		return symbols;
	}
	public SetOfStates getSuccessors (int symbol) { 
		if (symbol < 0 || symbol >= succ.length) return null;
		return succ[symbol]; 
	}
	public HashSet<Pair<State, Boolean>> getSuccessorsFNotNull (Word word)  {
		HashSet<Pair<State, Boolean>> set = new HashSet<>();
		HashSet<Pair<State, Boolean>> set_next = new HashSet<>();
		HashSet<Pair<State, Boolean>> set_tmp;
		set.add(new Pair<>(this, this.isFinal()));
		for (int index = 0; index < word.getLength(); index++) {			
			for (Pair<State, Boolean> pair : set) {
				SetOfStates posts = pair.first().getSuccessors(word.getIndex(index));
				if (posts != null) {
					for (State state : posts)
						set_next.add(new Pair<>(state, pair.second() || state.isFinal()));
				}
			}
			set_tmp = set;
			set = set_next;
			set_next = set_tmp;
		}
		return set;
	}
	public SetOfStates getSuccessorsNotNull (Word word)  {
		SetOfStates set = new SetOfStates();
		SetOfStates set_next = new SetOfStates();
		SetOfStates set_tmp;
		set.add(this);
		for (int index = 0; index < word.getLength(); index++) {			
			for (State from : set) {
				SetOfStates posts = from.getSuccessors(word.getIndex(index));
				if (posts != null) set_next.addAll(posts);
			}
			set_tmp = set;
			set = set_next;
			set_next = set_tmp;
		}
		return set;
	}
	// +++++++++++++++ Getters +++++++++++++++


	State (String name, int id, boolean acceptance, int alphabetSize) {
		this.name = name;
		this.id = id;
		is_final = acceptance;
		this.succ = new SetOfStates[alphabetSize];
	}


	void addSuccessor(State q, int symbolId) {
		if (succ[symbolId] == null) {
			SetOfStates singleton = new SetOfStates();
			singleton.add(q);
			succ[symbolId] = singleton;
		}
		else succ[symbolId].add(q);
	}

	
	public boolean equals (Object other) {
		if (other instanceof State) return id == ((State) other).id;
		return false;
	}
	

	public String toString() {return name;}
	public int hashCode() {return 31 * id;}
}

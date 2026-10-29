package engine;

import java.util.Set;
import automata.*;
import tuples.*;


public final class PostF extends Post<SetOfContexts> {	
	public PostF (Integer initA, SetOfStates initB, StateManager stateManager, SymbolManager symbolManager) { 
		super(stateManager, symbolManager);
		initialize(initA, initB); 
	}
	
	
	protected void initialize(Integer initA, SetOfStates setB) {
		for (int symbol : stateManager.getSymbols(initA)) {
			SetOfStates posts = stateManager.getSuccessors(initA, symbol);
			if (posts != null && !posts.isEmpty()) {
				SetOfContexts contexts = new SetOfContexts();
				for (Integer fromB : setB) {
					SetOfStates succ = stateManager.getSuccessors(fromB, symbol);
					if (succ != null) {
						for (Integer toB : succ)
							contexts.add(fromB, toB, stateManager.isFinal(fromB) || stateManager.isFinal(toB));
					}
				}
				for (Integer toA : posts) { 
					updates.add(toA, contexts, new Word(symbol));
					content.add(toA, contexts, new Word(symbol));
				}
			}
		}
	}
	
	
	protected SetOfContexts post (SetOfContexts current, int symbol) {
		SetOfContexts postB = new SetOfContexts();
		for (Pair<Pair<Integer, Integer>, Boolean> pair : current) {
			Integer initB = pair.first().first();
			Integer fromB = pair.first().second();
			Boolean accept = pair.second();
			SetOfStates succ = stateManager.getSuccessors(fromB, symbol);
			if (succ != null) {
				for (Integer toB : succ)
					postB.add(initB, toB, accept || stateManager.isFinal(toB));
			}
		}
		return postB;
	}
	
	
	protected boolean addExtreme(Integer stateA, SetOfContexts postB, Word word) {
		return content.addIfMin(stateA, postB, word);
	}
	
}


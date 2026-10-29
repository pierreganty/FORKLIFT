package engine;

import java.util.Set;
import automata.*;
import tuples.*;

public abstract sealed class Post<T extends QuasiOrder<T>> permits PostI, PostF {
	protected final PostVariable<T> content = new PostVariable<>();
	protected PostVariable<T> updates = new PostVariable<>();
	protected final StateManager stateManager;
	protected final SymbolManager symbolManager;

	//+++++++++++++++ Getters +++++++++++++++
	public int size () {return content.size();}
	public Set<Pair<T, Word>> getSetOfT(Integer stateA) { return content.getNotNull(stateA); }
	// +++++++++++++ Getters +++++++++++++
	
	
	public Post(StateManager stateManager, SymbolManager symbolManager) {
		this.stateManager = stateManager;
		this.symbolManager = symbolManager;
	}
	
	abstract protected T post(T current, int symbol);
	abstract protected boolean addExtreme(Integer stateA, T postB, Word word);

	
	public boolean apply () {
		PostVariable<T> buffer = new PostVariable<>();

		for (Integer fromA : updates.keySet()) {
			for (int symbol : stateManager.getSymbols(fromA)) {
				SetOfStates posts = stateManager.getSuccessors(fromA, symbol);
				if (posts != null && !posts.isEmpty()) {
					for (Pair<T, Word> pair : updates.get(fromA)) {
						T postB = post(pair.first(), symbol);
						Word word = new Word(pair.second(), symbol);
						for (Integer toA : posts) {
							if (addExtreme(toA, postB, word))
								buffer.add(toA, postB, word);
						}
					}
				}
			}
		}
		
		updates = buffer;
		return (updates.isEmpty() == false); 
	}
}

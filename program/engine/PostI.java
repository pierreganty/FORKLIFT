package engine;


import automata.*;


public final class PostI extends Post<SetOfStates> {
	private final boolean reversed_inclusion;
	
	public PostI (Integer initA, Integer initB, boolean rev, StateManager stateManager, SymbolManager symbolManager) {
		super(stateManager, symbolManager);
		initialize(initA, initB);
		reversed_inclusion = rev;
	}

	
	protected void initialize(Integer initA, Integer initB) {
		SetOfStates set_qI = new SetOfStates();
		set_qI.add(initB);
		updates.add(initA, set_qI, new Word());
		content.add(initA, set_qI, new Word());
	}
	
	
	protected SetOfStates post (SetOfStates current, int symbol) {
		SetOfStates postB = new SetOfStates();
		for (Integer fromB : current) {
			SetOfStates succ = stateManager.getSuccessors(fromB, symbol);
			if (succ != null)
				postB.addAll(succ);
		}
		return postB;
	}
	
	
	protected boolean addExtreme(Integer stateA, SetOfStates postB, Word word) {
		if (reversed_inclusion) return content.addIfMax(stateA, postB, word);
		return content.addIfMin(stateA, postB, word);
	}
}



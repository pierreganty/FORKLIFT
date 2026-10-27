package engine;

import automata.*;
import tuples.*;
import java.util.*;


/**
 * State relation optimized for vectorized subset operations.
 * Maintains BitSet representations for efficient smaller_than checks.
 */
public final class StateRelation
extends HashMap<State, SetOfStates>
implements QuasiOrder<StateRelation>, Iterable<Pair<State, State>> {

	// Track all state IDs for BitSet optimization
	private int maxStateId = -1;

	public void add (State initB, State stateB) {
		SetOfStates set = computeIfAbsent(initB, k -> new SetOfStates());
		set.add(stateB);
		maxStateId = Math.max(maxStateId, Math.max(initB.getId(), stateB.getId()));
	}
	
	public boolean addAll (State initB, Collection<State> stateB) {
		SetOfStates set = computeIfAbsent(initB, k -> new SetOfStates());
		boolean modified = set.addAll(stateB);
		for (State s : stateB) {
			maxStateId = Math.max(maxStateId, Math.max(initB.getId(), s.getId()));
		}
		return modified;
	}
	
	public boolean contains (State initB, State stateB) {
		Set<State> set = get(initB);
		if (set == null) return false;
		return set.contains(stateB);
	}

	/**
	 * Optimized subset check using BitSet-based SetOfStates.
	 * This is the hot path in FORKLIFT's fix-point algorithms.
	 */
	@Override
	public boolean smaller_than (StateRelation rel) {
		for (State fromB : keySet()) {
			SetOfStates otherSet = rel.get(fromB);
			if (otherSet == null) return false;
			
			SetOfStates thisSet = get(fromB);
			if (!thisSet.smaller_than(otherSet)) return false;
		}
		return true;
	}

	/**
	 * Vector API implementation for relation subset check.
	 * Requires Java 16+ with --add-modules jdk.incubator.vector
	 * 
	 * This demonstrates how to use Vector API for parallel comparison
	 * of multiple state relations.
	 */
	/*
	import jdk.incubator.vector.*;
	
	public boolean smaller_than_vectorized(StateRelation rel) {
		// For each state in this relation, check if its successors are a subset
		// of the corresponding successors in rel
		
		// Get all states from both relations
		Set<State> allStates = new HashSet<>(keySet());
		allStates.addAll(rel.keySet());
		
		// For each state, get the BitSet of successors
		Map<State, BitSet> thisSuccessors = new HashMap<>();
		Map<State, BitSet> otherSuccessors = new HashMap<>();
		
		for (State state : allStates) {
			SetOfStates thisSet = get(state);
			SetOfStates otherSet = rel.get(state);
			
			if (thisSet != null) {
				thisSuccessors.put(state, thisSet.getBitSet());
			}
			if (otherSet != null) {
				otherSuccessors.put(state, otherSet.getBitSet());
			} else if (thisSet != null) {
				// rel doesn't have this state, so it can't be a superset
				return false;
			}
		}
		
		// Convert BitSets to long arrays for vector processing
		List<State> statesList = new ArrayList<>(allStates);
		long[][] thisChunks = new long[statesList.size()][];
		long[][] otherChunks = new long[statesList.size()][];
		
		for (int i = 0; i < statesList.size(); i++) {
			State state = statesList.get(i);
			BitSet thisBits = thisSuccessors.getOrDefault(state, new BitSet());
			BitSet otherBits = otherSuccessors.getOrDefault(state, new BitSet());
			
			thisChunks[i] = thisBits.toLongArray();
			otherChunks[i] = otherBits.toLongArray();
		}
		
		// Use Vector API to check all states in parallel
		VectorSpecies<Long> species = LongVector.SPECIES_PREFERRED;
		int vectorLength = species.length();
		
		// Process each state's BitSet with vectors
		for (int stateIdx = 0; stateIdx < statesList.size(); stateIdx++) {
			long[] thisChunk = thisChunks[stateIdx];
			long[] otherChunk = otherChunks[stateIdx];
			
			// Skip if this state has no successors (empty set is subset of anything)
			if (thisChunk.length == 0) continue;
			
			// Check if other has this state
			if (otherChunk.length == 0 && thisChunk.length > 0) {
				return false; // this has successors but other doesn't
			}
			
			for (int chunkIdx = 0; chunkIdx < thisChunk.length; chunkIdx += vectorLength) {
				if (chunkIdx + vectorLength > thisChunk.length) {
					// Scalar fallback for remainder
					for (int j = chunkIdx; j < thisChunk.length; j++) {
						long otherVal = (j < otherChunk.length) ? otherChunk[j] : 0L;
						if ((thisChunk[j] & ~otherVal) != 0L) {
							return false;
						}
					}
					break;
				}
				
				// Load vectors
				LongVector thisVec = LongVector.fromArray(species, thisChunk, chunkIdx);
				LongVector otherVec = LongVector.fromArray(species, otherChunk, chunkIdx);
				
				// Check: (this & ~other) should be zero
				if (thisVec.and(otherVec.not()).anyNonZero()) {
					return false;
				}
			}
		}
		
		return true;
	}
	*/

	@Override
	public String toString() {
		StringJoiner outersj = new StringJoiner(";", "{", "}" );
		for (Pair<State, State> pair : this) {
			StringJoiner sj = new StringJoiner(",", "(", ")" );
			sj.add(pair.first().toString());
			sj.add(pair.second().toString());
			outersj.add(sj.toString());
		}
		return outersj.toString();
	}


	@Override
	public Iterator<Pair<State, State>> iterator() {
		return new StateRelationIterator(this);
	}
	
	/**
	 * Get the maximum state ID tracked by this relation.
	 */
	public int getMaxStateId() {
		return maxStateId;
	}
}

	
	
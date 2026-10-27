package engine;

import automata.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Set of states optimized for vectorized subset operations.
 * Uses BitSet internally for efficient containsAll/smaller_than checks.
 * 
 * This implementation maintains compatibility with the Set interface while
 * providing optimized subset operations that can leverage the Vector API.
 */
public class SetOfStates extends HashSet<State> implements QuasiOrder<SetOfStates> {
	
	// Track maximum state ID for BitSet optimization
	private int maxStateId = -1;
	
	// BitSet representation for vectorized operations
	// Each bit represents whether a state with that ID is in the set
	private final BitSet bits = new BitSet();
	
	@Override
	public boolean add(State state) {
		boolean modified = super.add(state);
		if (modified) {
			bits.set(state.getId());
			maxStateId = Math.max(maxStateId, state.getId());
		}
		return modified;
	}
	
	@Override
	public boolean remove(Object o) {
		if (o instanceof State) {
			State state = (State) o;
			bits.clear(state.getId());
		}
		return super.remove(o);
	}
	
	@Override
	public void clear() {
		super.clear();
		bits.clear();
		maxStateId = -1;
	}
	
	/**
	 * Optimized subset check using BitSet.
	 * This is the hot path in FORKLIFT's fix-point algorithms.
	 * 
	 * Uses BitSet's native optimizations (bitwise operations) which are
	 * already highly efficient. For even better performance with large sets,
	 * this could be enhanced with Vector API (see commented code below).
	 */
	@Override
	public boolean smaller_than(SetOfStates set) {
		// Use BitSet for fast subset check
		// If maxStateId is -1, we're empty, so we're a subset of anything
		if (maxStateId < 0) return true;
		if (set.maxStateId < 0) return false; // set is empty but we're not
		
		// Get the relevant portion of both BitSets
		BitSet thisBits = bits.get(0, maxStateId + 1);
		BitSet setBits = set.bits.get(0, Math.max(maxStateId, set.maxStateId) + 1);
		
		// Check: thisBits is subset of setBits
		// (thisBits & ~setBits) should be empty
		BitSet diff = (BitSet) thisBits.clone();
		diff.andNot(setBits);
		return diff.isEmpty();
	}
	
	/**
	 * Vector API implementation for subset check.
	 * Requires Java 16+ with --add-modules jdk.incubator.vector
	 * 
	 * This demonstrates how to use Vector API for parallel bit comparison.
	 * Currently commented out as Vector API is incubator status.
	 */
	/*
	import jdk.incubator.vector.*;
	
	public boolean smaller_than_vectorized(SetOfStates set) {
		if (maxStateId < 0) return true;
		if (set.maxStateId < 0) return false;
		
		// Convert BitSet to long array (64 bits per long)
		long[] thisChunks = bits.toLongArray();
		long[] setChunks = set.bits.toLongArray();
		
		int chunkCount = Math.min(thisChunks.length, setChunks.length);
		
		// Use Vector API to compare chunks in parallel
		VectorSpecies<Long> species = LongVector.SPECIES_PREFERRED;
		int vectorLength = species.length();
		
		for (int i = 0; i < chunkCount; i += vectorLength) {
			if (i + vectorLength > chunkCount) {
				// Handle remainder with scalar operations
				for (int j = i; j < chunkCount; j++) {
					if ((thisChunks[j] & ~setChunks[j]) != 0L) {
						return false;
					}
				}
				break;
			}
			
			// Load vectors
			LongVector thisVec = LongVector.fromArray(species, thisChunks, i);
			LongVector setVec = LongVector.fromArray(species, setChunks, i);
			
			// Check: (this & ~set) should be zero for all lanes
			LongVector diff = thisVec.and(setVec.not());
			
			// If any lane has a non-zero value, this is not a subset
			if (diff.anyNonZero()) {
				return false;
			}
		}
		
		// Check remaining chunks if any
		if (thisChunks.length > setChunks.length) {
			for (int j = chunkCount; j < thisChunks.length; j++) {
				if (thisChunks[j] != 0L) {
					return false; // this has bits set beyond set's range
				}
			}
		}
		
		return true;
	}
	*/
	
	@Override
	public String toString() {
		return this.stream().map(State::toString).collect(Collectors.joining(";", "{", "}"));
	}
	
	/**
	 * Get the BitSet representation for advanced operations.
	 */
	public BitSet getBitSet() {
		return (BitSet) bits.clone();
	}
	
	/**
	 * Get maximum state ID in this set.
	 */
	public int getMaxStateId() {
		return maxStateId;
	}
}


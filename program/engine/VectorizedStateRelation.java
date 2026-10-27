package engine;

import automata.*;
import tuples.*;
import java.util.*;

/**
 * Vector API demonstration: StateRelation with vectorized subset checks.
 * 
 * This class demonstrates how the Vector API could accelerate the smaller_than
 * method which is called frequently in FORKLIFT's fix-point computations.
 * 
 * The key insight: StateRelation is essentially a Map<State, SetOfStates> where
 * smaller_than checks if this relation is a subset of another.
 * 
 * For large state spaces, this can be vectorized by:
 * 1. Representing states as integer IDs
 * 2. Using BitSet for each state's successors
 * 3. Using Vector API to compare multiple state mappings in parallel
 */
public class VectorizedStateRelation extends HashMap<State, VectorizedSetOfStates> 
    implements QuasiOrder<VectorizedStateRelation>, Iterable<Pair<State, State>> {

    public void add(State initB, State stateB) {
        VectorizedSetOfStates set = computeIfAbsent(initB, k -> new VectorizedSetOfStates());
        set.add(stateB);
    }

    public boolean addAll(State initB, Collection<State> stateB) {
        VectorizedSetOfStates set = computeIfAbsent(initB, k -> new VectorizedSetOfStates());
        return set.addAll(stateB);
    }

    public boolean contains(State initB, State stateB) {
        VectorizedSetOfStates set = get(initB);
        if (set == null) return false;
        return set.contains(stateB);
    }

    /**
     * Vector-optimized subset check.
     * 
     * This is the hot path in FORKLIFT's algorithm. The current implementation
     * iterates through each state and checks if its successors are a subset.
     * 
     * With Vector API, we could:
     * 1. Convert the entry sets to bit masks
     * 2. Use vector operations to compare multiple state relations in parallel
     * 3. Use SIMD instructions for bulk comparison
     */
    @Override
    public boolean smaller_than(VectorizedStateRelation rel) {
        for (State fromB : keySet()) {
            VectorizedSetOfStates otherSet = rel.get(fromB);
            if (otherSet == null) return false;
            
            VectorizedSetOfStates thisSet = get(fromB);
            if (!thisSet.smaller_than(otherSet)) return false;
        }
        return true;
    }

    /**
     * Vector API implementation (conceptual).
     * 
     * This would require:
     * 1. Mapping states to integer IDs
     * 2. Representing each state's successors as a BitSet
     * 3. Using Vector API to compare multiple BitSets in parallel
     */
    /*
    import jdk.incubator.vector.*;
    
    public boolean smaller_than_vectorized(VectorizedStateRelation rel) {
        // For each state, we need to check: this.stateSet ⊆ rel.stateSet
        // This can be vectorized if we represent sets as bits
        
        // Get all state IDs
        int[] stateIds = keySet().stream().mapToInt(State::getId).toArray();
        
        // For each state, get the BitSet of successors
        BitSet[] thisSuccessors = new BitSet[stateIds.length];
        BitSet[] otherSuccessors = new BitSet[stateIds.length];
        
        for (int i = 0; i < stateIds.length; i++) {
            State state = getStateById(stateIds[i]);
            thisSuccessors[i] = get(state);
            otherSuccessors[i] = rel.get(state);
            
            if (otherSuccessors[i] == null) return false;
        }
        
        // Convert BitSets to long arrays for vector processing
        long[][] thisChunks = new long[stateIds.length][];
        long[][] otherChunks = new long[stateIds.length][];
        
        for (int i = 0; i < stateIds.length; i++) {
            thisChunks[i] = thisSuccessors[i].toLongArray();
            otherChunks[i] = otherSuccessors[i].toLongArray();
        }
        
        // Use Vector API to check all states in parallel
        VectorSpecies<Long> species = LongVector.SPECIES_PREFERRED;
        int vectorLength = species.length();
        
        // Process each BitSet chunk with vectors
        for (int stateIdx = 0; stateIdx < stateIds.length; stateIdx++) {
            long[] thisChunk = thisChunks[stateIdx];
            long[] otherChunk = otherChunks[stateIdx];
            
            for (int chunkIdx = 0; chunkIdx < thisChunk.length; chunkIdx += vectorLength) {
                if (chunkIdx + vectorLength > thisChunk.length) {
                    // Scalar fallback
                    for (int j = chunkIdx; j < thisChunk.length; j++) {
                        if ((thisChunk[j] & ~otherChunk[j]) != 0) return false;
                    }
                    break;
                }
                
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
}

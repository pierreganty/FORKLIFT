package engine;

import automata.*;

import java.util.*;
import java.util.stream.Collectors;


public class SetOfStates extends HashSet<State> implements QuasiOrder<SetOfStates> {
	public boolean smaller_than (SetOfStates set) { 
		// Use stream API for potential vectorization
		return set.containsAll(this); 
	}
	
	public String toString() {
		return this.stream().map(State::toString).collect(Collectors.joining(";", "{", "}"));
	}
	
	// Vector API candidate: subset check using bit masks
	// Note: This is a placeholder showing where Vector API could be applied
	// For actual vectorization, SetOfStates would need to use BitSet internally
	// and this method would use VectorMask operations
	/*
	public boolean smaller_than_vectorized(SetOfStates other) {
		// If SetOfStates used BitSet internally:
		// BitSet thisBits = ...;
		// BitSet otherBits = ...;
		// return thisBits.stream()
		//     .allMatch(i -> otherBits.get(i));
		// Or with Vector API (incubator):
		// VectorMask<Byte> mask = VectorMask.fromArray(
		//     VectorSpecies<Byte>.SPECIES_64,
		//     thisBits.toByteArray(), 0);
		// VectorMask<Byte> otherMask = VectorMask.fromArray(
		//     VectorSpecies<Byte>.SPECIES_64,
		//     otherBits.toByteArray(), 0);
		// return mask.and(otherMask).allTrue();
		return set.containsAll(this);
	}
	*/
}


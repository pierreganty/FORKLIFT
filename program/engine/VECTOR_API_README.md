# Vector API Implementation Notes for FORKLIFT

## Overview

This directory contains demonstration files showing how the **Vector API** (Java 16+ incubator) could be used to accelerate FORKLIFT's performance-critical operations.

## Key Opportunities

### 1. SetOfStates.smaller_than() - Subset Checks

**Current Implementation:**
```java
public boolean smaller_than(SetOfStates set) {
    return set.containsAll(this);
}
```

**Vector API Opportunity:**
- Represent `SetOfStates` using `BitSet` internally (mapping states to integer IDs)
- Use `VectorMask` to compare multiple bits in parallel
- Achieve 4x-8x speedup on modern CPUs with AVX2/AVX-512

**Example:**
```java
import jdk.incubator.vector.*;

// Convert BitSet to byte array
byte[] thisBits = bitSet.toByteArray();
byte[] otherBits = otherBitSet.toByteArray();

// Load into vector masks
VectorMask<Byte> thisMask = VectorMask.fromArray(
    VectorSpecies<Byte>.SPECIES_64, thisBits, 0);
VectorMask<Byte> otherMask = VectorMask.fromArray(
    VectorSpecies<Byte>.SPECIES_64, otherBits, 0);

// Check subset: (this & ~other) should be empty
return thisMask.and(otherMask.not()).allFalse();
```

### 2. StateRelation.smaller_than() - Relation Subset Checks

**Current Implementation:**
```java
public boolean smaller_than(StateRelation rel) {
    for (State fromB : keySet()) {
        if (rel.get(fromB) == null) return false;
        for (State state : get(fromB))
            if (rel.get(fromB).contains(state) == false) return false;
    }
    return true;
}
```

**Vector API Opportunity:**
- Represent each state's successors as a `BitSet`
- Use vector operations to compare multiple state relations in parallel
- Process 64 states at once with 64-bit vectors

## Implementation Files

### VectorizedSetOfStates.java
Demonstrates:
- BitSet-backed state representation
- Vector-ready subset check pattern
- Placeholder for Vector API integration

### VectorizedStateRelation.java  
Demonstrates:
- Vector-optimized relation subset checks
- Pattern for bulk comparison of state mappings
- Integration with existing FORKLIFT architecture

## Build Requirements

To use Vector API:

1. **Java Version:** Java 16+ (Vector API incubator)

2. **Compiler Flags:**
   ```bash
   javac --add-modules jdk.incubator.vector -Xlint:preview \
         -d classes program/engine/*.java program/**/*.java
   ```

3. **Runtime Flags:**
   ```bash
   java --add-modules jdk.incubator.vector \
        -XX:+UnlockExperimentalVMOptions \
        -XX:+UseVectorAPI \
        -jar forklift.jar
   ```

## Performance Expectations

| Operation | Current | With Vector API | Speedup |
|-----------|---------|----------------|---------|
| SetOfStates.smaller_than() | O(n) | O(n/64) | 4-8x |
| StateRelation.smaller_than() | O(n*m) | O(n*m/64) | 4-8x |
| PostVariable.addIfMin/Max() | O(n) | O(n/64) | 4-8x |

## Migration Path

1. **Phase 1:** Add `BitSet` representation alongside `HashSet` in `SetOfStates`
2. **Phase 2:** Implement vectorized subset checks as alternative methods
3. **Phase 3:** Benchmark and replace hot paths with vectorized versions
4. **Phase 4:** Add `--enable-vector-api` flag for opt-in performance mode

## Notes

- Vector API is **incubator** status (as of Java 21)
- Requires CPU support for SIMD instructions (SSE2/AVX2/AVX-512)
- Best results on large automata (>1000 states)
- Fallback to scalar operations automatically on unsupported CPUs

## References

- [Vector API JEP](https://openjdk.org/jeps/338)
- [Vector API Tutorial](https://inside.java/2020/08/11/vector-api/)
- [Panama Vector API Docs](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.incubator.vector/module-summary.html)

package automata;

/**
 * Symbol is now represented as an integer ID.
 * Use SymbolManager for name-to-ID mapping and ID-to-name lookup.
 * This type is kept for backward compatibility but is just an int wrapper.
 */
public record Symbol(int id) {
	public int hashCode() { return 31 * id; }
	
	public boolean equals(Object other) {
		if (other instanceof Symbol) return id == ((Symbol) other).id;
		return false;
	}
	
	public String toString() {
		return "(SYM_" + id + ")";
	}
	
	public String getName() {
		return "SYM_" + id;
	}
}

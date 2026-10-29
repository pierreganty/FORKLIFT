package automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for symbol name to ID mapping.
 * Can be shared across automata to ensure consistent symbol IDs.
 * Provides O(1) lookup from int ID to string name.
 */
public class SymbolManager {
	private final List<String> symbolNames = new ArrayList<>();
	
	/**
	 * Add a new symbol name and return its ID.
	 * If the name already exists, returns the existing ID.
	 */
	public int addSymbol(String name) {
		// Check if already exists
		for (int i = 0; i < symbolNames.size(); i++) {
			if (symbolNames.get(i).equals(name)) {
				return i;
			}
		}
		// Add new symbol
		symbolNames.add(name);
		return symbolNames.size() - 1;
	}
	
	/**
	 * Get the name for a symbol ID.
	 */
	public String getName(int symbolId) {
		return symbolNames.get(symbolId);
	}
	
	/**
	 * Get the number of symbols registered.
	 */
	public int size() {
		return symbolNames.size();
	}
	
	/**
	 * Get a string representation of a symbol ID.
	 */
	public String toString(int symbolId) {
		return "(" + getName(symbolId) + ")";
	}
}

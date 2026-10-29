package automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Global manager for symbol name to ID mapping.
 * Provides O(1) lookup from int ID to string name.
 */
public class SymbolManager {
	private static List<String> symbolNames = new ArrayList<>();
	
	/**
	 * Add a new symbol name and return its ID.
	 * If the name already exists, returns the existing ID.
	 */
	public static int addSymbol(String name) {
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
	public static String getName(int symbolId) {
		return symbolNames.get(symbolId);
	}
	
	/**
	 * Get the number of symbols registered.
	 */
	public static int size() {
		return symbolNames.size();
	}
	
	/**
	 * Clear all symbols (for testing purposes).
	 */
	public static void clear() {
		symbolNames.clear();
	}
	
	/**
	 * Get a string representation of a symbol ID.
	 */
	public static String toString(int symbolId) {
		return "(" + getName(symbolId) + ")";
	}
}

package automata;


public record Word(int[] array, int length) {
	
	public Word () {
		this(new int[0], 0);
	}
	
	public Word (int symbol) {
		this(new int[]{symbol}, 1);
	}
	
	public Word (Word word, int symbol) {
		this(
			java.util.Arrays.copyOf(word.array, word.length + 1),
			word.length + 1
		);
		this.array[word.length] = symbol;
	}
	
	public int getLength () {
		return length;
	}
	
	public int getIndex (int i) {
		return array[i];
	}
 	

	public String toString() {
		String string = "";
		for (int i = 0; i < length; i++)
			string = string + SymbolManager.toString(array[i]);
		return string;
	}
}

package automata;


public record Word(Symbol[] array, int length) {
	
	public Word () {
		this(new Symbol[0], 0);
	}
	
	public Word (Symbol symbol) {
		this(new Symbol[]{symbol}, 1);
	}
	
	public Word (Word word, Symbol symbol) {
		this(
			java.util.Arrays.copyOf(word.array, word.length + 1),
			word.length + 1
		);
		this.array[word.length] = symbol;
	}
	
	public int getLength () {
		return length;
	}
	
	public Symbol getIndex (int i) {
		return array[i];
	}
 	

	public String toString() {
		String string = "";
		for (int i = 0; i < length; i++)
			string = string + array[i].toString();
		return string;
	}
}

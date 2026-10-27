package automata;



public record Symbol(String name, int id) {
	//+++++++++++++++ Getters +++++++++++++++
	public String getName() {return name;}
	// +++++++++++++++ Getters +++++++++++++++


    public String toString() {return "("+name+")";}
	
	
	public int hashCode() {return 31 * id;}
	
	
	public boolean equals (Object other) {
		if (other instanceof Symbol) return id == ((Symbol) other).id;
		return false;
	}
}

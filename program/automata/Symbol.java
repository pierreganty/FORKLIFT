package automata;



public record Symbol(String name, int id) {
	//+++++++++++++++ Getters +++++++++++++++
	public String getName() {return name;}
	// +++++++++++++++ Getters +++++++++++++++


    @Override
    public String toString() {return "("+name+")";}
}

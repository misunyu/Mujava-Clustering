package mujava.gen;

public enum GenerationType {
	/**
	 * 
	 */
	NONE("NONE"),
	/**
	 * 
	 */
	WS("WS(Weak&Strong)"),
	/**
	 * 
	 */
	SC("SC(Seperate Compilation)"),
	/**
	 * 
	 */

	SCC("SCC(Seperate Compilation Clustering)"),
	/**
	 * 
	 */
	
	MSG("MSG(mutant schemata generation)"),
	/**
	 * 
	 */
	REACH("REACH(Reachability Analysis)");

	private String title;

	GenerationType(String title) {
		this.title = title;
	}

	public String getLabel() {
		return title;
	}
}

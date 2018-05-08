package mujava;

public enum TestCaseType {
	/**
	 * 
	 */
	NONE("NONE"),
	/**
	 * 
	 */
	MJ("MuJava"),
	/**
	 * 
	 */
	JU4("JUnit4"),
	/**
	 * 
	 */
	JU3("JUnit3");
	

	private String title;

	TestCaseType(String title) {
		this.title = title;
	}

	public String getLabel() {
		return title;
	}
}

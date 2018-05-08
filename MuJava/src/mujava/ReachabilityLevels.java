package mujava;

public enum ReachabilityLevels
{
	/**
	 * 
	 */
	NONE("NONE"),
	/**
	 * 
	 */
	CLASS("Class"),
	/**
	 * 
	 */
	METHOD("Method"),
	/**
	 * 
	 */
	PATH("Path");

	private String title;

	ReachabilityLevels(String title)
	{
		this.title = title;
	}

	public String getLabel()
	{
		return title;
	}
}

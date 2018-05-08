package mujava.plugin.wizards.generation;

public class VariableState {
	private String name = "";
	private String type = "";
	private boolean isFinal = false;
	private boolean isPreState = false;
	private boolean isPostState = false;

	public String getVariableName() {
		return name;
	}

	public String getType() {
		return this.type;
	}

	public void setVariableName(String name) {
		if (name != null)
			this.name = name;
	}

	public void setType(String type) {
		if (type != null)
			this.type = type;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setPreState(boolean isPreState) {
		this.isPreState = isPreState;
	}

	public boolean isPreState() {
		return isPreState;
	}

	public boolean isPostState() {
		return isPostState;
	}

	public void setPostState(boolean isPostState) {
		this.isPostState = isPostState;
	}
}

package mujava.plugin.wizards.generation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class ClassState {
	Hashtable<String, MethodState> methodStates = new Hashtable<String, MethodState>();
	private Hashtable<String, VariableState> constructorVariables = new Hashtable<String, VariableState>();

	private String name = "";

	public String getClassName() {
		return name;
	}

	public void setFullName(String fullName) {
		this.name = fullName;
	}

	public void addMethodState(MethodState state) {
		String methodName = state.getMethodName();
		if (methodName.isEmpty())
			return;
		if (methodStates.get(methodName) != null)
			return;

		String retType = state.getReturnType();
		if (retType == null || retType.isEmpty())
			return;

		methodStates.put(state.toHashKeyString(), state);
	}

	public MethodState getMethodState(String methodSignature) {
		return methodStates.get(methodSignature);
	}

	public List<VariableState> getStubConstructorVariableList() {
		List<VariableState> list = new ArrayList<VariableState>();
		list.addAll(constructorVariables.values());
		return list;
	}

	public void addConstructorVariableState(VariableState state) {
		String name = state.getVariableName();
		if (name.isEmpty())
			return;
		if (constructorVariables.get(name) != null)
			return;

		constructorVariables.put(name, state);
	}
}

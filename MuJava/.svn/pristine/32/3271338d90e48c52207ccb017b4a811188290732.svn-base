package mujava.plugin.wizards.generation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import openjava.ptree.MethodDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;

public class MethodState {
	public static String makeHashKeyString(MethodDeclaration p) {
		StringBuffer sb = new StringBuffer();

		sb.append(p.getName());
		sb.append("(");
		ParameterList pList = p.getParameters();
		for (int i = 0; i < pList.size(); i++) {
			Parameter param = pList.get(i);
			sb.append(param.getTypeSpecifier());
			sb.append(",");
		}
		sb.append("):");
		sb.append(p.getReturnType());

		return sb.toString();
	}

	private String name = "";

	private Hashtable<Integer, ParamState> params = new Hashtable<Integer, ParamState>();

	private String type = "";

	private Hashtable<String, VariableState> variables = new Hashtable<String, VariableState>();

	public void addParamState(ParamState state) {
		int index = state.getIndex();
		if (index < 0)
			return;
		if (params.get(index) != null)
			return;

		params.put(index, state);
	}

	public void addVariableState(VariableState state) {
		String name = state.getVariableName();
		if (name.isEmpty())
			return;
		if (variables.get(name) != null)
			return;

		variables.put(name, state);
	}

	public String getMethodName() {
		return name;
	}

	public List<ParamState> getParamStates() {
		List<ParamState> list = new ArrayList<ParamState>();
		int size = params.size();
		for (int i = 0; i < size; i++) {
			ParamState param = params.get(i);
			list.add(param);
		}
		return list;
	}

	public String getReturnType() {
		return this.type;
	}

	public List<VariableState> getVariableStateList() {
		List<VariableState> list = new ArrayList<VariableState>();
		list.addAll(variables.values());
		return list;
	}

	public boolean hasVariables() {
		return !variables.isEmpty();
	}

	public void setMethodName(String methodName) {
		this.name = methodName;
	}

	/**
	 * 
	 * @param returnType
	 *            not empty string
	 */
	public void setReturnType(String returnType) {
		this.type = returnType;
	}

	public String toHashKeyString() {
		StringBuffer sb = new StringBuffer();

		sb.append(name);
		sb.append("(");
		for (ParamState param : getParamStates()) {
			sb.append(param.getType());
			sb.append(",");
		}
		sb.append("):");
		sb.append(type);

		return sb.toString();
	}
}

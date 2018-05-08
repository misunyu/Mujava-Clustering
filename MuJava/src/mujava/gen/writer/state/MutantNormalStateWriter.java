package mujava.gen.writer.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;

/**
 * Normal Writer makes a mutant source file corresponding a change point.
 * Because it is possible to make a set of mutants for a change point, it should
 * have remember the number of generated files. Variable sizesOfIteration has
 * the iteration number of each change point, and isFixedIteration indicates
 * whether each iteration number is same or not. If isFixedIteration is true,
 * fixedSizeOfIteration has the valid iteration number.
 * 
 * @author swkim
 * 
 */
public abstract class MutantNormalStateWriter extends MutantWriter {

	/**
	 * The fixed number of mutant candidates generated from a change point
	 */
	protected int fixedSizeOfIteration;

	/**
	 * indicates whether each iteration number is same or not.
	 */
	private boolean isFixedIteration = false;

	/**
	 * The number of mutant candidates generated from eacf change point
	 */
	protected Map<ChangePoint, Integer> sizesOfIteration = null;

	protected List<String> generatedMutantID = null;

	protected Hashtable<Integer, HashSet<String>> assignedVarSet = new Hashtable<Integer, HashSet<String>>();
	protected Hashtable<Integer, Hashtable<String, Boolean>> blockLocalVarSet = new Hashtable<Integer, Hashtable<String, Boolean>>();
	protected Hashtable<Integer, Hashtable<String, Boolean>> methodParSet = new Hashtable<Integer, Hashtable<String, Boolean>>();
	protected Hashtable<Integer, Hashtable<String, Boolean>> classFieldSet = new Hashtable<Integer, Hashtable<String, Boolean>>();
	protected Hashtable<Integer, HashSet<String>> classFieldAssignSet = new Hashtable<Integer, HashSet<String>>();
//	protected final HashSet<String> primitiveTypes = new HashSet<String>();


	protected boolean isInConstructor = false;
	//protected boolean written = true;
	protected boolean needStateSavingCode = false;
	protected MuJavaMutantInfo curMutant = null;
	public static String stateDirectory = "";
	
	protected MutantNormalStateWriter(Environment env, String name,
			boolean fixedIteration) throws IOException {
		super(env, name, MutantOperator.GEN_STATE);
		sizesOfIteration = new HashMap<ChangePoint, Integer>();
		generatedMutantID = new ArrayList<String>();
		setFixedIteration(fixedIteration);
		needStateSavingCode = false;
		
		//setPrimitiveTypes();
		
	}
	
	/*
	protected void setPrimitiveTypes()
	{
		primitiveTypes.add("byte");
		primitiveTypes.add("short");
		primitiveTypes.add("int");
		primitiveTypes.add("long");
		primitiveTypes.add("float");
		primitiveTypes.add("double");
		primitiveTypes.add("char");
		primitiveTypes.add("boolean");
	}
	*/
	
	/*
	protected boolean isPrimitiveType(String type)
	{
		return primitiveTypes.contains(type);
	}
	*/
	
	protected boolean isJavaIOPackage(String type)
	{
		return type.startsWith("java.io.");
	}

	public void emptyVarInfo() {
		//System.out.println("emptyVarInfo");
		//written = true;
		needStateSavingCode = false;
		assignedVarSet.clear();
		blockLocalVarSet.clear();
		curMutant = null;
	}
	
	protected void addMethodParList(int methodID, ParameterList plist)
	{
		//System.out.println("start addMethodParList");
		if(plist == null)
			return;
		
		Hashtable<String, Boolean> parSet = new Hashtable<String, Boolean>();
		Enumeration pListEnum = plist.elements();
		while(pListEnum.hasMoreElements()) {
			Parameter param = (Parameter)pListEnum.nextElement();
			boolean isJavaIOPackage = isJavaIOPackage(param.getTypeSpecifier().getName());
			//20170216 YSMA
			//parSet.put(param.getVariable(), !isJavaIOPackage);
			parSet.put(param.getVariable(), isJavaIOPackage);

		}
		if(parSet.size() > 0)
			methodParSet.put(methodID, parSet);
		//System.out.println("end addMethodParList parSet = " + parSet);
	}
	
	public void removeMethodParList(int methodID)
	{
		//System.out.println("removeMethodParList");
		Hashtable<String, Boolean> set = methodParSet.get(methodID);
		if(set == null) {
			return;
		}
		
		methodParSet.remove(methodID);
		//System.out.println("end removeMethodParList parSet = " + set);

	}

	public HashSet addAssignedVar(int methodID, String varName)
	{
		HashSet set = (HashSet)assignedVarSet.get(methodID);

		//System.out.println("start addAssignedVar var: " + varName + " id: " + methodID);
		if(varName.contains("(")) {
		//	System.out.println("No Assignment Var: " + varName);
			return set;
		}
		
		if(NumberUtils.isNumber(varName)){
		//	System.out.println("No Assignment Number: " + varName);
			return set;
		}
		
		if(varName.contains("!")) {
			//name = varName.replace("!", "");
			return set;
		}
		
		String name = varName;
		if(varName.contains("++")) {
			name = varName.replace("++", "");
		} else if (varName.contains("--")) {
			name = varName.replace("--", "");
		}
		
		
		if(set == null) {
			set = new HashSet();
		}
		set.add(name);
		assignedVarSet.put(methodID, set);
		return set;
		
	//	System.out.println("end addAssignedVar: " + assignedVarSet);
	}
	
	public void removeAssignedVar(int methodID, String varName)
	{
	//	System.out.println("removeAssignedVar id: " +  methodID + " varName: " + varName);

		HashSet set = (HashSet)assignedVarSet.get(methodID);
		if(set == null || varName == null) {
			return;
		}
		set.remove(varName);

		//System.out.println("addAssignedVar methodName =  " + methodName + " set = " +  set);
	}
	
	
	public void addClassField(int classID, FieldDeclaration p)
	{
		String name = p.getVariable();

		Hashtable<String, Boolean> set = classFieldSet.get(classID);
		if(set == null) {
			set = new Hashtable <String, Boolean>();
		}
		
		boolean isJavaIOPackage = isJavaIOPackage(p.getTypeSpecifier().getName());
		set.put(name, isJavaIOPackage);
		classFieldSet.put(classID, set);
		
		if(p.getInitializer() != null) {
			addClassFieldAssignSet(classID, name);
		}
			
		
		//System.out.println("addAssignedVar methodName =  " + methodName + " set = " +  set);
	}

	public void addClassFieldAssignSet(int classID, String name)
	{
		
		HashSet<String> set = classFieldAssignSet.get(classID);
		if(set == null) {
			set = new HashSet <String>();
		}
		
		set.add(name);
		classFieldAssignSet.put(classID, set);
		
		//System.out.println("addAssignedVar methodName =  " + methodName + " set = " +  set);
	}

	
	public void addBlockLocalVar(int methodID, String name, boolean isJavaIOPackage)
	{
		//System.out.println("start addBlockLocalVar var: " + name + " id: " + methodID);
	
		Hashtable<String, Boolean> set = blockLocalVarSet.get(methodID);
		if(set == null) {
			set = new Hashtable<String, Boolean>();
		}
		
		set.put(name, isJavaIOPackage);
		blockLocalVarSet.put(methodID, set);
		
		//System.out.println("end addBlockLocalVar methodName =  " + name + " set = " +  set);
	}
	
	public void removeBlockLocalVar(int methodID, String varNames)
	{
	//	System.out.println("removeBlockLocalVar id: " + methodID + " name: " + varNames);
		
		Hashtable<String, Boolean> set = blockLocalVarSet.get(methodID);
		if(set == null || varNames == null) {
			return;
		}
		set.remove(varNames);
		//System.out.println("addAssignedVar methodName =  " + methodName + " set = " +  set);
	}

	protected void setSizeOfIteration(Map<ChangePoint, Integer> iterationTable) {
		sizesOfIteration.clear();
		sizesOfIteration.putAll(iterationTable);
	}

	private void setFixedIteration(boolean isFixedIteration) {
		this.isFixedIteration = isFixedIteration;
	}

	public int getSizeOfIteration(ChangePoint point) {
		assert (point != null);

		if (isFixedIteration)
			return fixedSizeOfIteration;

		return sizesOfIteration.get(point);
	}
}
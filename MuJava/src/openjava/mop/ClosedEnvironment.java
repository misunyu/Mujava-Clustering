/*
 * ClosedEnvironment.java
 *
 * comments here.
 *
 * @author   Michiaki Tatsubori
 * @version  %VERSION% %DATE%
 * @see      java.lang.Object
 *
 * COPYRIGHT 1998 by Michiaki Tatsubori, ALL RIGHTS RESERVED.
 */
package openjava.mop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import openjava.ptree.ModifierList;
import openjava.tools.DebugOut;

/**
 * The class <code>ClosedEnvironment</code>
 * <p>
 * For example
 * 
 * <pre>
 * </pre>
 * 
 * <p>
 * 
 * @author Michiaki Tatsubori
 * @version 1.0
 * @since $Id: ClosedEnvironment.java,v 1.1 2009-10-30 09:05:11 swkim Exp $
 * @see java.lang.Object
 */
public class ClosedEnvironment extends Environment {
	protected List<String> initTable = new ArrayList<String>();
	protected Hashtable<String, ModifierList> modifierTable = new Hashtable<String, ModifierList>();
	protected Hashtable<String, OJClass> symbol_table = new Hashtable<String, OJClass>();
	protected Hashtable<String, OJClass> table = new Hashtable<String, OJClass>();
	protected List<String> paramTable = new ArrayList<String>();;

	/**
	 * 
	 * 
	 * @param
	 * @return
	 * @exception
	 * @see java.lang.Object
	 */
	public ClosedEnvironment(Environment env) {
		parent = env;
	}

	public void bindModifier(String name, ModifierList list) {
		modifierTable.put(name, list);
	}

	/**
	 * binds a name to the class type.
	 * 
	 * @param name
	 *            the fully-qualified name of the class
	 * @param clazz
	 *            the class object associated with that name
	 */
	public void bindVariable(String name, OJClass clazz) {
		symbol_table.put(name, clazz);
	}

	public void copyInitVaribleToParent() {
		if (initTable.isEmpty())
			return;

		if (parent != null && parent instanceof ClosedEnvironment) {
			ClosedEnvironment pEnv = (ClosedEnvironment) parent;
			for (String name : initTable) {
				if (!pEnv.initTable.contains(name)) {
					pEnv.initTable.add(name);
				}
			}

		}
	}

	/**
	 * -------------------------------------------- EDIT This is added by ysma &
	 * swkim --------------------------------------------
	 */
	public java.util.List<String> getAccessibleVariables() {
		List<String> v = new ArrayList<String>();

		Enumeration<?> e = symbol_table.keys();
		while (e.hasMoreElements()) {
			v.add((String) (e.nextElement()));
		}
		if (parent != null && parent instanceof ClosedEnvironment) {
			List<String> list = ((ClosedEnvironment) parent)
					.getAccessibleVariables();
			for (String str : list) {
				if (!v.contains(str))
					v.add(str);
			}
		}

		return v;
	}

	public boolean isFinal(String name) {
		ModifierList list = modifierTable.get(name);
		if (list != null)
			return list.contains(ModifierList.FINAL);
		if (parent != null && parent instanceof ClosedEnvironment) {
			ClosedEnvironment pEnv = (ClosedEnvironment) parent;
			return pEnv.isFinal(name);
		}
		return true;
	}

	public boolean isInitialized(String name) {
		if (initTable.contains(name))
			return true;

		if (parent != null && parent instanceof ClosedEnvironment)
			return ((ClosedEnvironment) parent).isInitialized(name);
		return false;
	}

	public OJClass lookupBind(String name) {
		OJClass type = symbol_table.get(name);
		if (type != null)
			return type;
		if (parent == null)
			return null;
		return parent.lookupBind(name);
	}

	public OJClass lookupClass(String name) {
		OJClass result = table.get(name);
		if (result != null)
			return result;
		return parent.lookupClass(name);
	}

	public boolean isParameter(String name) {
		if (paramTable.contains(name))
			return true;

		if (parent != null && parent instanceof ClosedEnvironment)
			return ((ClosedEnvironment) parent).isParameter(name);

		return false;
	}

	public void record(String name, OJClass clazz) {
		DebugOut.println("ClosedEnvironment#record() : " + name + " "
				+ clazz.getName());
		Object result = table.put(name, clazz);
		if (result != null) {
			System.err.println(name + " is already binded on "
					+ result.toString());
		}
	}

	public void registerInitVar(String name) {
		if (!initTable.contains(name))
			initTable.add(name);
	}

	/*
	 * public String toQualifiedName(String name) { if (name.indexOf('.') == -1) {
	 * java.util.Enumeration ite = table.keys(); while (ite.hasMoreElements()) {
	 * String qname = (String) ite.nextElement(); if (qname.endsWith("." +
	 * name)) return qname; } } return parent.toQualifiedName(name); }
	 */
	public String toQualifiedName(String name) {
		Enumeration<?> ite = table.keys();
		while (ite.hasMoreElements()) {
			String qname = (String) ite.nextElement();
			if (qname.equals(name))
				return qname;
		}
		return parent.toQualifiedName(name);
	}

	public String toString() {
		StringWriter str_writer = new StringWriter();
		PrintWriter out = new PrintWriter(str_writer);

		out.println("ClosedEnvironment");
		out.println("class object table : " + table);
		out.println("binding table : " + symbol_table);
		out.println("parent env : " + parent);

		out.flush();
		return str_writer.toString();
	}

	public void registerParameter(String name) {
		if (!paramTable.contains(name))
			paramTable.add(name);
	}
}

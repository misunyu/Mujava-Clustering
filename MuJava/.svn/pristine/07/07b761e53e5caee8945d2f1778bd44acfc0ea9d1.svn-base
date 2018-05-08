package mujava.op.util;

import openjava.ptree.MethodDeclaration;
import openjava.ptree.ParseTree;

public class ChangePoint {
	static int count = 0;

	/**
	 * Reset the static variable count to newCount to identify the change point
	 * in a class.
	 * 
	 * @param newCount
	 */
	public static void resetCount(int newCount) {
		count = newCount;
	}

	private ParseTree treeElement;

	private MethodDeclaration methodDeclaration;

	int id;

	private Object data;

	/**
	 * Create a ChangePoint Object. It is called only in Mutantor Class such as
	 * ROR and AOIU
	 * 
	 * @param e
	 * @param m
	 * @see TraditionalMutator.getMutantWriter();
	 */
	public ChangePoint(ParseTree e, MethodDeclaration m) {
		this.treeElement = e;
		this.methodDeclaration = m;
		this.id = ++count;
	}

	protected void setTreeElement(ParseTree expression) {
		this.treeElement = expression;
	}

	protected void setM(MethodDeclaration m) {
		this.methodDeclaration = m;
	}

	protected void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public ParseTree getTreeElement() {
		return treeElement;
	}

	public String getID() {
		return Integer.toString(id);
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public boolean isSamePoint(ParseTree exp, MethodDeclaration md) {
		if (isSameParseTreeElement(exp, this.treeElement))
			if (isSameParseTreeElement(this.methodDeclaration, md))
				return true;

		return false;
	}

	private boolean isSameParseTreeElement(ParseTree p, ParseTree q) {
		if (p == null && q == null)
			return true;
		if (p == null || q == null)
			return false;
		return (p.getObjectID() == q.getObjectID());
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

}

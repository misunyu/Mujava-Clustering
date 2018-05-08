////////////////////////////////////////////////////////////////////////////
// Module : AORS.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.ForStatement;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.UnaryExpression;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edited by swkim
 * @version 1.0
 */

public class AORSChangePointSeeker extends AbstractChangePointSeeker {

	Map<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();

	public Map<ChangePoint, Integer> getIterationTable() {
		return iterTable;
	}

	private List<ChangePoint> emptyPoints = new ArrayList<ChangePoint>();

	public AORSChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "AORS");
	}

	boolean isForStmts = false;

	// private List<ChangePoint> inForStmts = new ArrayList<ChangePoint>();
	//
	// public List<ChangePoint> getPrePostEQs() {
	// return inForStmts;
	// }

	public void visit(AssignmentExpression p) throws ParseTreeException {
		// Expression lexpr = p.getLeft();
		// lexpr.accept( this );
		Expression rexp = p.getRight();
		rexp.accept(this);
	}

	// Copy from the VariableBinder Class (super super class)
	public void visit(ForStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		boolean temp = isForStmts;
		isForStmts = true;
		ParseTree tree = p.getInitDeclType();
		if (tree != null)
			tree.accept(this);
		tree = p.getInit();
		if (tree != null) {
			Environment tempEnv = getEnvironment();
			pop();
			tree.accept(this);
			push(tempEnv);
		}
		tree = p.getCondition();
		if (tree != null)
			tree.accept(this);
		isForStmts = temp;

		tree = p.getStatements();
		if (tree != null)
			tree.accept(this);

		temp = isForStmts;
		isForStmts = true;
		tree = p.getIncrement();
		if (tree != null)
			tree.accept(this);
		isForStmts = temp;

		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		if ((op == UnaryExpression.POST_DECREMENT)
				|| (op == UnaryExpression.POST_INCREMENT)
				|| (op == UnaryExpression.PRE_DECREMENT)
				|| (op == UnaryExpression.PRE_INCREMENT)) {

			int size = 3;
			ChangePoint cPoint = new ChangePoint(p, currentMethod);

			if (!isForStmts) { // Inside FOR stmts
				// inForStmts.add(cPoint);
				// size = 1;
				if (isStatement(p)) {
					emptyPoints.add(cPoint);
					size = 1;
				}

				addChangePoint(cPoint);
				iterTable.put(cPoint, size);
			}
		}
	}

	public List<ChangePoint> getEmptyPoints() {
		return emptyPoints;
	}
}

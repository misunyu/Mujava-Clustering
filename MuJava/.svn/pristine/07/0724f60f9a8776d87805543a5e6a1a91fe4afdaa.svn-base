////////////////////////////////////////////////////////////////////////////
// Module : AODS.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import java.util.ArrayList;
import java.util.List;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.ptree.ForStatement;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.UnaryExpression;

import org.eclipse.jdt.core.ICompilationUnit;
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

public class AODSChangePointSeeker extends AbstractChangePointSeeker {
	private List<ChangePoint> emptyPoints = new ArrayList<ChangePoint>();
	boolean isForStmts = false;

	public AODSChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "AODS");
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		if ((op == UnaryExpression.POST_DECREMENT)
				|| (op == UnaryExpression.POST_INCREMENT)
				|| (op == UnaryExpression.PRE_DECREMENT)
				|| (op == UnaryExpression.PRE_INCREMENT)) {
			ChangePoint cPoint = new ChangePoint(p, currentMethod);
			if (!isForStmts) { // Inside FOR stmts
				if (isStatement(p))
					emptyPoints.add(cPoint);
				addChangePoint(cPoint);
			}
		}
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

	public List<ChangePoint> getEmptyPoints() {
		return emptyPoints;
	}
}

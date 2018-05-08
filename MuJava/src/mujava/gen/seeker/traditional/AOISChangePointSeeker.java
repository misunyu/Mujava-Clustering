////////////////////////////////////////////////////////////////////////////
// Module : AOIS.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ForStatement;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

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

public class AOISChangePointSeeker extends Arithmetic_OP {

	Map<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();

	/**
	 * Change Point list whose change point is a statement, not an expression.
	 */
	private List<ChangePoint> statementChangePoints = new ArrayList<ChangePoint>();

	public AOISChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "AOIS");
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {

		Expression rexp = p.getRight();
		rexp.accept(this);
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		OJClass type = getType(p);
		if (type == OJSystem.INT || type == OJSystem.DOUBLE
				|| type == OJSystem.FLOAT || type == OJSystem.LONG) {

			if (!isFinalVariable(p)) {
				ChangePoint cPoint = new ChangePoint(p, currentMethod);

				int numOfMutants = 4;
				// register change point if it is a whose statement
				if (isStatement(p)) {
					statementChangePoints.add(cPoint);
					numOfMutants = 2;
				}
				iterTable.put(cPoint, numOfMutants);

				addChangePoint(cPoint);
			}
		}
	}

	public List<ChangePoint> getStatementChangePoints() {
		return statementChangePoints;
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		// NO OPERATION
	}

	@Override
	public void visit(ForStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}

		StatementList list = p.getStatements();
		if (list != null)
			list.accept(this);

		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(Variable p) throws ParseTreeException {
		OJClass type = getType(p);
		if (type == OJSystem.INT || type == OJSystem.DOUBLE
				|| type == OJSystem.FLOAT || type == OJSystem.LONG) {
			if (!isFinalVariable(p)) {

				ChangePoint cPoint = new ChangePoint(p, currentMethod);

				int numOfMutants = 4;

				// register change point if it is a whose statement
				if (isStatement(p)) {
					statementChangePoints.add(cPoint);
					numOfMutants = 2;
				}
				iterTable.put(cPoint, numOfMutants);

				addChangePoint(cPoint);
			}
		}
	}

	public Map<ChangePoint, Integer> getIterationTable() {
		return iterTable;
	}
}

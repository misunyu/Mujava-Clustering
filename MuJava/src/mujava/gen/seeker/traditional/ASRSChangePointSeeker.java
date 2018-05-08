////////////////////////////////////////////////////////////////////////////
// Module : ASRS.java
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
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.ParseTreeException;

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
 * @editor swkim
 * @version 1.1
 */

public class ASRSChangePointSeeker extends AbstractChangePointSeeker {
	Map<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();

	/**
	 * Change Point list whose change point is a statement, not an expression.
	 */
	private List<ChangePoint> statementChangePoints = new ArrayList<ChangePoint>();

	public ASRSChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "ASRS");
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		int op = p.getOperator();

		OJClass clz = getType(p.getLeft());
		if (clz != null && clz.isPrimitive()) {

			if ((op == AssignmentExpression.ADD)
					|| (op == AssignmentExpression.SUB)
					|| (op == AssignmentExpression.MULT)
					|| (op == AssignmentExpression.DIVIDE)
					|| (op == AssignmentExpression.MOD)) {
				ChangePoint point = new ChangePoint(p, currentMethod);
				addChangePoint(point);
				if (isStatement(p))
					statementChangePoints.add(point);
				iterTable.put(point, 4);
			} else if ((op == AssignmentExpression.AND)
					|| (op == AssignmentExpression.OR)
					|| (op == AssignmentExpression.XOR)) {
				ChangePoint point = new ChangePoint(p, currentMethod);
				addChangePoint(point);
				if (isStatement(p))
					statementChangePoints.add(point);
				iterTable.put(point, 2);
			} else if ((op == AssignmentExpression.SHIFT_L)
					|| (op == AssignmentExpression.SHIFT_R)
					|| (op == AssignmentExpression.SHIFT_RR)) {
				ChangePoint point = new ChangePoint(p, currentMethod);
				addChangePoint(point);
				if (isStatement(p))
					statementChangePoints.add(point);
				iterTable.put(point, 2);
			}
		}

		super.visit(p);
	}

	public List<ChangePoint> getStatement() {
		return statementChangePoints;
	}

	public Map<ChangePoint, Integer> getIterationTable() {
		return iterTable;
	}

}

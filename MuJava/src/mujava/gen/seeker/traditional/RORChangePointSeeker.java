////////////////////////////////////////////////////////////////////////////
// Module : ROR.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import java.util.HashMap;
import java.util.Map;

import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.BinaryExpression;
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
 * @edited by swkim
 * @version 1.1
 */

public class RORChangePointSeeker extends Arithmetic_OP {
	Map<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();

	public RORChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "ROR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		int op_type = p.getOperator();

		if (isArithmeticType(p.getLeft()) && isArithmeticType(p.getRight())) {
			if ((op_type == BinaryExpression.GREATER)
					|| (op_type == BinaryExpression.LESS)
					|| (op_type == BinaryExpression.GREATEREQUAL)
					|| (op_type == BinaryExpression.LESSEQUAL)
					|| (op_type == BinaryExpression.EQUAL)
					|| (op_type == BinaryExpression.NOTEQUAL)) {
				ChangePoint point = new ChangePoint(p, currentMethod);
				addChangePoint(point);
				//iterTable.put(point, 5);
				iterTable.put(point, 3);
			}
		} else if (!isArithmeticType(p.getLeft())
				&& !isArithmeticType(p.getRight())) {
			if (op_type == BinaryExpression.EQUAL
					|| op_type == BinaryExpression.NOTEQUAL) {
				ChangePoint point = new ChangePoint(p, currentMethod);
				addChangePoint(point);
				//iterTable.put(point, 1);
				iterTable.put(point, 2);
			}
		}

		super.visit(p);
	}

	public Map<ChangePoint, Integer> getIterationTable() {
		return iterTable;
	}

}

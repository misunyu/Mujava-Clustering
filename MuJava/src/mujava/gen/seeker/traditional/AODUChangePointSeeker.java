////////////////////////////////////////////////////////////////////////////
// Module : AODU.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.CaseGroup;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

import org.eclipse.jdt.core.ICompilationUnit;

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

public class AODUChangePointSeeker extends Arithmetic_OP {

	public AODUChangePointSeeker(ICompilationUnit sourceJavaElement, FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "AODU");
	}

	public void visit(CaseGroup p) throws ParseTreeException {
		// Case 의 Label가 변경되는 경우, 기존 Case Label과의 구분에서 오류가 발생할 수 있으므로 변경 대상에
		// 포함하지 않는다.

		super.visit(p.getStatements());
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		// [ Example ]
		// int a=0;int b=2;int c=4;
		// Right Expression : a = b = -c;
		// Wrong Expression : a = -b = c;
		// Ignore left expression
		Expression rexp = p.getRight();
		rexp.accept(this);
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		if (isArithmeticType(p)) {
			int op = p.getOperator();
			if (op == UnaryExpression.MINUS) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
	}
}

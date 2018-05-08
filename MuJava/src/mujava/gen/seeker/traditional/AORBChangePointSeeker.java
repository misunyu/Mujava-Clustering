////////////////////////////////////////////////////////////////////////////
// Module : AORB.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
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
 * @version 1.0
 */

public class AORBChangePointSeeker extends Arithmetic_OP
{

	public AORBChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env)
	{
		super(file_env, sourceJavaElement, "AORB");
	}

	public void visit(BinaryExpression p) throws ParseTreeException
	{
		int op = p.getOperator();
		if (isArithmeticType(p)
				&& (op == BinaryExpression.TIMES
						|| op == BinaryExpression.DIVIDE
						|| op == BinaryExpression.MOD
						|| op == BinaryExpression.PLUS || op == BinaryExpression.MINUS))
		{
			addChangePoint(new ChangePoint(p, currentMethod));
		}

		super.visit(p);
	}
}

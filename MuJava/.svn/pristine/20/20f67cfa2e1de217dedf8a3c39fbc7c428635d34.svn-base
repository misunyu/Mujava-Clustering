//Module : Arithmetic_OP.java
//Author : Ma, Yu-Seung
//COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.

package mujava.gen.seeker.traditional;

import mujava.gen.seeker.AbstractChangePointSeeker;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
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
 * @version 1.1
 */

public class Arithmetic_OP extends AbstractChangePointSeeker {

	public Arithmetic_OP(FileEnvironment file_env, ICompilationUnit src,
			String OpName) {
		super(file_env, src, OpName);
	}

	public boolean isArithmeticType(Expression p) throws ParseTreeException {
		OJClass type = getType(p);
		if (type == OJSystem.INT || type == OJSystem.DOUBLE
				|| type == OJSystem.FLOAT || type == OJSystem.LONG
				|| type == OJSystem.SHORT || type == OJSystem.CHAR
				|| type == OJSystem.BYTE) {
			return true;
		}
		return false;
	}

	public boolean hasBinaryArithmeticOp(BinaryExpression p)
			throws ParseTreeException {
		int op_type = p.getOperator();
		if ((op_type == BinaryExpression.TIMES)
				|| (op_type == BinaryExpression.DIVIDE)
				|| (op_type == BinaryExpression.MOD)
				|| (op_type == BinaryExpression.PLUS)
				|| (op_type == BinaryExpression.MINUS))
			return true;
		else
			return false;
	}
}

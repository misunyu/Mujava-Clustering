package mujava.gen.seeker.classical;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJMethod;
import openjava.mop.OJSystem;
import openjava.mop.Toolbox;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Variable;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edit by swkim
 * @version 1.1
 */

public class EOC extends AbstractChangePointSeeker {

	public EOC(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "EOC");
	}

	@Override
	public void visit(BinaryExpression p) throws ParseTreeException {
		Expression lexp = p.getLeft();
		int operator = p.getOperator();

		if (operator == BinaryExpression.EQUAL) {
			Expression rexp = p.getRight();
			if (rexp instanceof Variable || rexp instanceof FieldAccess) {
				OJClass type = getType(lexp);
				if (!type.isPrimitive()) {
					OJMethod[] methods = type.getAllMethods();
					OJMethod method = Toolbox.pickupMethod(methods, "equals",
							new OJClass[] { OJSystem.OBJECT });
					if (method != null)
						addChangePoint(new ChangePoint(p, currentMethod));
				}
			}
		} else
			super.visit(p);
	}
}

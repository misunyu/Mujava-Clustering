package mujava.gen.seeker.classical;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJMethod;
import openjava.mop.OJSystem;
import openjava.mop.Toolbox;
import openjava.ptree.AssignmentExpression;
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

public class EOA extends AbstractChangePointSeeker {

	public EOA(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "EOA");
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression lexp = p.getLeft();
		Expression rexp = p.getRight();
		int operator = p.getOperator();

		if (operator == AssignmentExpression.EQUALS) {
			if ((lexp instanceof Variable || lexp instanceof FieldAccess)
					&& (rexp instanceof Variable || rexp instanceof FieldAccess)) {
				OJClass lType = getType(lexp);
				OJClass rType = getType(rexp);
				if (!lType.isPrimitive() && !lType.equals(OJSystem.NULLTYPE)
						&& !rType.isPrimitive()
						&& !rType.equals(OJSystem.NULLTYPE)) {
					try {
						// check the clone method is defined in the class type
						// of right exprssion
						OJClass situtation = OJClass.forName(getEnvironment()
								.currentClassName());
						OJMethod[] methods = rType.getMethods(situtation);
						OJMethod method = Toolbox.pickupMethod(methods,
								"clone", new OJClass[] {});

						// If the clone method in the class of right expression
						// could raise exception, it can not be mutated to call
						// the method because the mutated code should be in a
						// try-catch block
						if (method != null) {
							OJClass[] exceptions = method.getExceptionTypes();
							if (exceptions == null || exceptions.length == 0)
								addChangePoint(new ChangePoint(p, currentMethod));
						}
					} catch (OJClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}

		super.visit(p);
	}
}

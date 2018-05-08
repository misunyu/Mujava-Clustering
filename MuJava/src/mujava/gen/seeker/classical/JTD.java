package mujava.gen.seeker.classical;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.SelfAccess;
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
 * @edit by swkim
 * @version 1.1
 */

public class JTD extends AbstractChangePointSeeker {

	public JTD(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "JTD");
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression left = p.getLeft();

		if (p.getOperator() == AssignmentExpression.EQUALS) {
			// In case of Def Node
			if (left instanceof FieldAccess) {
				FieldAccess fa = (FieldAccess) left;
				Expression exp = fa.getReferenceExpr();

				if (exp != null && exp instanceof SelfAccess) {
					if (!((SelfAccess) exp).isSuperAccess()) {
						FieldAccess temp = (FieldAccess) fa.makeRecursiveCopy();
						temp.setReferenceExpr(null);
						// MuJavaLogger.getLogger().debug(temp.toFlattenString());
						if (isParamterVariable(temp)) {
							OJClass oriClz = getType(fa);
							OJClass mutClz = getType(new Variable(fa.getName()));
							if (oriClz.equals(mutClz))
								addChangePoint(new ChangePoint(p, currentMethod));
						}
					}
				}
			}

		} else
			super.visit(p);
	}

	/**
	 * In case of Use Node, the instance variable is replaced with parameter
	 * variable
	 */
	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		Expression exp = p.getReferenceExpr();

		if (exp != null) {
			if (exp instanceof SelfAccess) {
				if (!((SelfAccess) exp).isSuperAccess()) {
					FieldAccess temp = (FieldAccess) p.makeRecursiveCopy();
					temp.setReferenceExpr(null);
					// MuJavaLogger.getLogger().debug(temp.toFlattenString());
					if (isParamterVariable(temp)) {
						OJClass oriClz = getType(p);
						OJClass mutClz = getType(new Variable(p.getName()));
						if (oriClz.equals(mutClz))
							addChangePoint(new ChangePoint(p, currentMethod));
					}
				}
			}
		} else
			super.visit(p);
	}
}

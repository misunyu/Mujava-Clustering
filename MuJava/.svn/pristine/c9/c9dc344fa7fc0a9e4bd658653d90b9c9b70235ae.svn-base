
package mujava.gen.seeker.traditional;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.ParseTreeException;
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

public class BODChangePointSeeker extends AbstractChangePointSeeker {
	public BODChangePointSeeker(ICompilationUnit sourceJavaElement, FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "BOD");
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		if (op == UnaryExpression.BIT_NOT) {
			addChangePoint(new ChangePoint(p, currentMethod));
		}
	}
}

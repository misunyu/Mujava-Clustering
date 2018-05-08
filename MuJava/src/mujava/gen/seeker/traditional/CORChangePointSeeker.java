////////////////////////////////////////////////////////////////////////////
// Module : COR.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJSystem;
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
 * @version 1.0
 */

public class CORChangePointSeeker extends AbstractChangePointSeeker {
	public CORChangePointSeeker(ICompilationUnit sourceJavaElement, FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "COR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		if ((getType(p.getLeft()) == OJSystem.BOOLEAN)
				&& (getType(p.getRight()) == OJSystem.BOOLEAN)) {
			int op_type = p.getOperator();
			if ((op_type == BinaryExpression.LOGICAL_AND)
					|| (op_type == BinaryExpression.LOGICAL_OR))  {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
		
		super.visit(p);
	}
}

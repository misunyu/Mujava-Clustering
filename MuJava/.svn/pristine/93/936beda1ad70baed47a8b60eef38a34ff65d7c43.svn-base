////////////////////////////////////////////////////////////////////////////
// Module : LOD.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
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
 * @version 1.0
 */

public class LODChangePointSeeker extends AbstractChangePointSeeker {
	public LODChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "LOD");
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		if (op == UnaryExpression.NOT) {
			addChangePoint(new ChangePoint(p, currentMethod));
		}
	}

}

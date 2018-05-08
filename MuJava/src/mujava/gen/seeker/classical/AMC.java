////////////////////////////////////////////////////////////////////////////
// Module : JSI.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.classical;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.MethodDeclaration;
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
 * @edit by swkim
 * @version 1.0
 */

public class AMC extends AbstractChangePointSeeker {

	public AMC(IJavaElement  originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "AMC");
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		ChangePoint c = new ChangePoint(p, currentMethod);
		addChangePoint(c);
	}

	public void visit(MethodDeclaration p) throws ParseTreeException {
		ChangePoint c = new ChangePoint(p, currentMethod);
		addChangePoint(c);
	}
}

////////////////////////////////////////////////////////////////////////////
// Module : COI.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.traditional;

import kaist.selab.util.MuJavaLogger;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.BinaryExpression;
import openjava.ptree.CaseGroup;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ParseTreeObject;
import openjava.ptree.UnaryExpression;
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
 * @version 1.0
 */

public class COIChangePointSeeker extends AbstractChangePointSeeker {
	public COIChangePointSeeker(ICompilationUnit sourceJavaElement,
			FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "COI");
	}

	boolean redundant = false;
	public void visit(AssignmentExpression p) throws ParseTreeException {
		// [ Example ]
		// int a=0;int b=2;int c=4;
		// Right Expression : a = b = -c;
		// Wrong Expression : a = -b = c;
		// Ignore left expression
		Expression rexp = p.getRight();
		rexp.accept(this);
	}

	public void visit(Variable p) throws ParseTreeException {
		if (isStatement(p))
			return;

		ParseTreeObject exp = p.getParent().getParent();
		if (exp instanceof CaseGroup)
			return;

		if (getType(p) == OJSystem.BOOLEAN) {
			if(!redundant){
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int operator = p.getOperator();
		if (UnaryExpression.POST_DECREMENT == operator
				|| UnaryExpression.POST_INCREMENT == operator
				|| UnaryExpression.PRE_DECREMENT == operator
				|| UnaryExpression.PRE_INCREMENT == operator) {
			// do nothing
		} else {
			super.visit(p.getExpression());
		}

	}
	
	public void visit(BinaryExpression p) throws ParseTreeException {
		 if( (p.getOperator()==BinaryExpression.LOGICAL_AND) || (p.getOperator()==BinaryExpression.LOGICAL_OR) ){
			 redundant = true;
			 super.visit(p);
			 redundant = false;
		 }
	}


	public void visit(FieldAccess p) throws ParseTreeException {
		if (isStatement(p))
			return;
		if(!redundant){
			if (getType(p) == OJSystem.BOOLEAN) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
	}

	public void visit(MethodCall p) throws ParseTreeException {
		if (!isStatement(p)) {
			OJClass retType = getType(p);

			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"MethodCall_Error in COI Mutator: "
								+ p.toFlattenString());
				return;
			} else if (getType(p) == OJSystem.BOOLEAN) {
				if(!redundant)
					addChangePoint(new ChangePoint(p, currentMethod));
			}
		}

		// for argument expressions
		p.getArguments().accept(this);
	}

	// public void visit(BinaryExpression p) throws ParseTreeException {
	// if (getType(p) == OJSystem.BOOLEAN) {
	// addChangePoint(new ChangePoint(p, currentMethod));
	// }
	// }
}

//Module : AOIU.java
//Author : Ma, Yu-Seung
//COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.

package mujava.gen.seeker.traditional;

import kaist.selab.util.MuJavaLogger;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.CaseGroup;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ParseTreeObject;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

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
 * @version 1.1
 */

public class AOIUChangePointSeeker extends Arithmetic_OP {

	public AOIUChangePointSeeker(ICompilationUnit originalSourceFile,
			FileEnvironment file_env) {
		super(file_env, originalSourceFile, "AOIU");
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		// [ Example ]
		// int a=0;int b=2;int c=4;
		// Right Expression : a = b = -c;
		// Wrong Expression : a = -b = c;
		// Ignore left expression
		Expression rexp = p.getRight();
		rexp.accept(this);
	}

	public void visit(CaseGroup p) throws ParseTreeException {
		// Case 의 Label가 변경되는 경우, 기존 Case Label과의 구분에서 오류가 발생할 수 있으므로 변경 대상에
		// 포함하지 않는다.

		super.visit(p.getStatements());
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (isStatement(p))
			return;

		if (isArithmeticType(p)) {
			OJClass retType = getType(p);
			if (retType.isPrimitive()
					&& !(retType.getName().equals("void")
							|| retType.getName().equals("byte")
							|| retType.getName().equals("boolean")
							|| retType.getName().equals("char") || retType
							.getName().equals("short"))) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
	}

	public void visit(MethodCall p) throws ParseTreeException {
		if (!isStatement(p)) {
			OJClass retType = getType(p);

			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"MethodCall_Error in AOIU Mutator: "
								+ p.toFlattenString());
				return;
			} else if (retType.isPrimitive()
					&& !(retType.getName().equals("void")
							|| retType.getName().equals("byte")
							|| retType.getName().equals("boolean")
							|| retType.getName().equals("char") || retType
							.getName().equals("short"))) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}

		// for argument expressions
		p.getArguments().accept(this);
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		if (op == UnaryExpression.MINUS || op == UnaryExpression.PLUS) {
			if (isStatement(p))
				return;
		}

		// for inner expression
		// p.getExpression().accept(this);
	}

	/**
	 * If Variable p is in the some UnaryExpression, the expression will be
	 * ignored by the upper visit(UnaryExpression) method. In case of case
	 * statement, it ignore the variable
	 */
	public void visit(Variable p) throws ParseTreeException {
		if (isStatement(p))
			return;

		ParseTreeObject exp = p.getParent().getParent();
		if (exp instanceof CaseGroup)
			return;

		if (isArithmeticType(p)) {
			OJClass retType = getType(p);
			if (retType.isPrimitive()
					&& !(retType.getName().equals("void")
							|| retType.getName().equals("byte")
							|| retType.getName().equals("boolean")
							|| retType.getName().equals("char") || retType
							.getName().equals("short"))) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
	}
}

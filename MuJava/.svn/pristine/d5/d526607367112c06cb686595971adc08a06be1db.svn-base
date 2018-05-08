package mujava.gen.seeker.classical;

import java.util.HashMap;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJField;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParseTreeException;
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
 * @edit by swkim
 * @version 1.1
 */

public class JTI extends AbstractChangePointSeeker {

	private Map<String, String> parameterList = new HashMap<String, String>();

	public JTI(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "JTI");
	}

	// Do not consider left-side of assignment, that is, we do not check the
	// variable to be definded
	public void visit(AssignmentExpression p) throws ParseTreeException {

		Expression leftExp = p.getLeft();
		Expression rightExp = p.getRight();

		if (hasTargetVariable(leftExp)) {
			MuJavaLogger.getLogger().debug(
					"Case [Left Exp has JTI target exp] has occured.");
		} else
			visit(rightExp);
	}

	// Clear the parameter list
	@Override
	public void visit(ConstructorDeclaration p) throws ParseTreeException {
		parameterList.clear();
		super.visit(p);
	}

	// Do not replace the variable which is used to call this constructor
	@Override
	public void visit(ConstructorInvocation p) throws ParseTreeException {
		// do nothing
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		// do nothing
	}

	// Clear the parameter list
	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		parameterList.clear();
		super.visit(p);
	}

	// register the parameter to the list
	@Override
	public void visit(Parameter p) throws ParseTreeException {
		super.visit(p);
		parameterList.put(p.getVariable(), p.getTypeSpecifier().toString());
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		if (isTargetVariable(p)) {
			addChangePoint(new ChangePoint(p, currentMethod));
		} else
			super.visit(p);
	}

	// // Limit the operator.
	// @Override
	// public void visit(UnaryExpression p) throws ParseTreeException {
	// int operator = p.getOperator();
	// if (operator == UnaryExpression.POST_DECREMENT
	// || operator == UnaryExpression.POST_INCREMENT
	// || operator == UnaryExpression.PRE_DECREMENT
	// || operator == UnaryExpression.PRE_INCREMENT)
	//
	// super.visit(p);
	// }

	private boolean hasTargetVariable(Expression exp) {
		if (exp instanceof Variable) {
			return isTargetVariable((Variable) exp);
			// }
			// else if (exp instanceof FieldAccess) {
			// FieldAccess fa = (FieldAccess) exp;
			// Expression expr = fa.getReferenceExpr();
			// TypeName type = fa.getReferenceType();
			// System.out
			// .println("Case [Field Write has JTI target exp] has occured.");
			// if (expr == null)
			// return false;
			// return hasTargetVariable(expr);
			// return false;
			// } else if (exp instanceof ArrayAccess) {
			// ArrayAccess aa = (ArrayAccess) exp;
			// Expression aexp = aa.getReferenceExpr();
			// Expression iexp = aa.getIndexExpr();
			// if (hasTargetVariable(aexp) || hasTargetVariable(iexp))
			// return true;
			// } else if (exp instanceof Literal) {
			// return false;
		} else if (exp instanceof UnaryExpression) {
			return hasTargetVariable(((UnaryExpression) exp).getExpression());
		}
		// else {
		// System.out.println("BBBB");
		// }
		return false;
	}

	private boolean isTargetVariable(Variable p) {
		String name = p.toString();
		String type = parameterList.get(name);

		if (type != null && isSelfClassVariable(p.toString())) {
			try {
				OJClass clz1 = getType(p);
				OJClass clz2 = OJClass.forName(env.currentClassName());
				OJField field = clz2.getField(name, clz2);
				if (field != null && field.getType().isAssignableFrom(clz1))
					return true;
			} catch (ParseTreeException e) {
				e.printStackTrace();
			} catch (OJClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMemberException e) {
			}
		}

		return false;
	}
}

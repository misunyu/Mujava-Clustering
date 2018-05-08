package mujava.gen.seeker.classical;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.SelfAccess;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * Description: super있는 곳은 빼고, 없는 곳에는 삽입한다. 단, 해당 Variable이나 method가
 * overriding일때 (부모와 자식에서 동시에 정의되는 경우.)
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edit by swkim
 * @version 1.1
 */

public class ISK extends AbstractChangePointSeeker {

	public ISK(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "ISK");
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		Expression exp = p.getReferenceExpr();

		if (exp != null && exp instanceof SelfAccess) {
			SelfAccess self = (SelfAccess) exp;
			if (self.isSuperAccess()) {
				String childClz = getEnvironment().currentClassName();
				OJClass clz = null;
				try {
					clz = OJClass.forName(childClz);
				} catch (OJClassNotFoundException e) {
					e.printStackTrace();
				}

				if (clz != null && isOverRidingField(p.getName(), clz))
					addChangePoint(new ChangePoint(p, currentMethod));
			}
		} else
			super.visit(p);
	}

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		Expression exp = p.getReferenceExpr();

		if (exp != null && exp instanceof SelfAccess) {
			SelfAccess self = (SelfAccess) exp;
			if (self.isSuperAccess()) {
				String childClz = getEnvironment().currentClassName();
				OJClass clz = null;
				try {
					clz = OJClass.forName(childClz);

					ExpressionList list = p.getArguments();
					OJClass[] args = new OJClass[list.size()];

					for (int i = 0; i < list.size(); i++) {
						Expression param = list.get(i);
						args[i] = getType(param);
					}

					if (clz != null
							&& isOverRidingMethod(p.getName(), args, clz))
						addChangePoint(new ChangePoint(p, currentMethod));
				} catch (OJClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else
			super.visit(p);
	}
}

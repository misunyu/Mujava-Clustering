package mujava.gen.seeker.classical;

import java.util.ArrayList;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJConstructor;
import openjava.ptree.AllocationExpression;
import openjava.ptree.ExpressionList;
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
 * @version 1.1
 */

public class PNC extends AbstractChangePointSeeker {

	public PNC(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "PNC");
	}

	@Override
	public void visit(AllocationExpression p) throws ParseTreeException {
		String type = p.getClassType().getName();

		OJClass declareType = null;
		OJClass situation = null;
		try {
			situation = OJClass.forName(getEnvironment().currentClassName());
			declareType = OJClass.forName(type);
		} catch (OJClassNotFoundException e) {
			e.printStackTrace();
		}

		if (declareType != null && !declareType.isPrimitive()) {
			OJClass[] clzs = declareType.getInheritableClasses(situation);
			if (clzs != null && clzs.length > 0) {
				ExpressionList param = p.getArguments();
				ArrayList<OJClass> paramList = new ArrayList<OJClass>();
				for (int i = 0; i < param.size(); i++) {
					OJClass pType = getType(param.get(i));
					paramList.add(pType);
				}
				OJClass[] paramArray = paramList.toArray(new OJClass[paramList
						.size()]);
				for (OJClass clz : clzs) {
					try {
						OJConstructor con = clz.getConstructor(paramArray,
								situation);
						ChangePoint cPoint = new ChangePoint(p, currentMethod);
						cPoint.setData(con);
						addChangePoint(cPoint);
					} catch (NoSuchMemberException e) {
					}
				}
			}
		}

		super.visit(p);
	}
}

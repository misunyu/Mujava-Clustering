package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.List;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ReturnStatement;
import openjava.ptree.SelfAccess;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edit by swkim
 * @version 1.1
 */

public class IOP extends AbstractChangePointSeeker {

	public IOP(IJavaElement originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "IOP");
	}

	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		StatementList list = p.getBody();
		if (list != null && list.size() > 1) {
			boolean hasReturn = false;
			String methodName = p.getName();
			if (list.get(list.size() - 1) instanceof ReturnStatement)
				hasReturn = true;

			for (int i = 0; i < list.size(); i++) {
				Statement stmt = list.get(i);
				if (stmt instanceof ExpressionStatement) {
					Expression exp = ((ExpressionStatement) stmt)
							.getExpression();
					if (exp instanceof MethodCall) {
						MethodCall mc = (MethodCall) exp;
						Expression expr = mc.getReferenceExpr();
						if (expr != null && expr instanceof SelfAccess) {
							if (((SelfAccess) expr).isSuperAccess()
									&& mc.getName().equals(methodName)) {

								List<Integer> indexList = new ArrayList<Integer>();
								int lastIndex = list.size() - 1;
								if (hasReturn)
									lastIndex--;
								int index = 0;
								// To Start Point
								if (i != index)
									indexList.add(index);
								// To End Point
								index = lastIndex;
								if (i != index && index >= 0)
									if (!indexList.contains(index))
										indexList.add(index);
								// Up Point
								index = i - 1;
								if (index >= 0) {
									if (!indexList.contains(index))
										indexList.add(index);
								}
								// Down Point
								index = i + 1;
								if (index <= lastIndex) {
									if (!indexList.contains(index))
										indexList.add(index);
								}

								ChangePoint cPoint = new ChangePoint(p,
										currentMethod);
								cPoint.setData(new Object[] { i, indexList });
								addChangePoint(cPoint);
							}
						}
					}
				}
			}
		}
	}
}

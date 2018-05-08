package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.List;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJMethod;
import openjava.mop.Toolbox;
import openjava.ptree.Expression;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

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

public class EMM extends AbstractChangePointSeeker {

	public EMM(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "EMM");
	}

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		// IF this method call is a set method
		String methodName = p.getName();
		if (methodName.startsWith("set") && p.getArguments().size() == 1) {

			OJClass situation = null;
			OJClass paramType = null;
			try {
				situation = OJClass
						.forName(getEnvironment().currentClassName());
				paramType = getType(p.getArguments().get(0));
			} catch (OJClassNotFoundException e1) {
				e1.printStackTrace();
			}

			// obtain the declaring class
			Expression exp = p.getReferenceExpr();
			TypeName type = p.getReferenceType();
			OJClass clz = null;
			try {
				if (exp == null && type == null) {
					clz = situation;
				} else if (exp != null) {
					clz = getType(exp);
				} else if (type != null) {
					clz = OJClass.forName(type.getName());
				}
			} catch (OJClassNotFoundException e) {
			}

			if (clz != null && situation != null && paramType != null) {
				OJMethod[] methods = clz.getMethods(situation);
				OJMethod[] selectedMethods = Toolbox
						.pickupAcceptableMethodsByParameterTypes(methods,
								new OJClass[] { paramType });
				List<String> alternativeMethods = new ArrayList<String>();
				for (OJMethod method : selectedMethods) {
					String name = method.getName();
					if (name.startsWith("set") && !name.equals(methodName)) {
						alternativeMethods.add(method.getName());
					}
				}

				if (!alternativeMethods.isEmpty()) {
					ChangePoint cPoint = new ChangePoint(p, currentMethod);
					cPoint.setData(alternativeMethods);
					addChangePoint(cPoint);
				}
			}

		} else
			super.visit(p);
	}
}

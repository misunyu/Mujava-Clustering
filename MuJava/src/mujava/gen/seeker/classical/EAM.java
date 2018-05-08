package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
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

public class EAM extends AbstractChangePointSeeker {

	public EAM(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "EAM");
	}

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		// IF this method call is a get method
		String methodName = p.getName();
		if (methodName.startsWith("get") && p.getArguments().isEmpty()) {
			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
			} else {

				OJClass situation = null;
				try {
					situation = OJClass.forName(getEnvironment()
							.currentClassName());
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
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + type.getName());
				}

				if (exp != null && exp.toString().equals("super"))
					clz = null;

				if (clz != null && situation != null) {
					OJMethod[] methods = clz.getMethods(situation);
					OJMethod[] selectedMethods = Toolbox
							.pickupAcceptableMethodsByParameterTypes(methods,
									new OJClass[] {});

					List<String> alternativeMethods = new ArrayList<String>();

					// search exact same method
					OJMethod curMethod = null;
					for (OJMethod method : selectedMethods) {
						if (method.getName().equalsIgnoreCase(methodName)
								&& method.getReturnType().equals(retType)) {
							curMethod = method;
							if (!curMethod.getModifiers().isPublic())
								return;

							break;
						}
					}

					if (curMethod != null) {

						// search another method
						for (OJMethod method : selectedMethods) {
							String name = method.getName();
							if (method.getModifiers().isPublic()) {
								if (name.startsWith("get")
										&& !name.equals(methodName)
										&& method.getReturnType().equals(
												retType)) {
									alternativeMethods.add(name);
								}
							}
						}

						if (!alternativeMethods.isEmpty()) {
							ChangePoint cPoint = new ChangePoint(p,
									currentMethod);
							cPoint.setData(alternativeMethods);
							addChangePoint(cPoint);

							return;
						}
					}
				}
			}
		}

		super.visit(p);
	}
}

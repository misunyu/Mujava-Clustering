package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.Helper;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJMethod;
import openjava.mop.Toolbox;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
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

public class OMR extends AbstractChangePointSeeker {

	public OMR(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "OMR");
	}

	// public static
	List<OJClass> getParameterTypes(List<OJClass> args, int depth, int index) {
		List<OJClass> list = new ArrayList<OJClass>();
		List<OJClass> listRest = new ArrayList<OJClass>();

		if (args.isEmpty() || depth == 0) {
			;
		} else {
			int interval = Helper.permutation(args.size(), depth) / args.size();
			int indexArray = index / interval;
			for (int i = 0; i < args.size(); i++) {
				if (i == indexArray)
					list.add(args.get(i));
				else
					listRest.add(args.get(i));
			}

			List<OJClass> retList = getParameterTypes(listRest, depth - 1,
					(index) % interval);
			list.addAll(retList);
		}

		return list;
	}

	// public static void main(String[] args) {
	// List<OJClass> ts = new ArrayList<OJClass>();
	// try {
	// ts.add(OJClass.forName("java.lang.Object"));
	// ts.add(OJClass.forName("java.lang.Boolean"));
	// ts.add(OJClass.forName("java.lang.Integer"));
	// // ts.add(OJClass.forName("java.lang.String"));
	// // ts.add(OJClass.forName("java.lang.Object"));
	// } catch (OJClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// for (int index = 0; index < ts.size(); index++) {
	// int max = OMR.permutation(ts.size(), index);
	// for (int j = 0; j < max; j++) {
	// List<OJClass> argList = OMR.getParameterTypes(ts, index, j);
	// System.out.println(j + sigmaPermutation(ts.size(), index)
	// + " : ");
	// for (int k = 0; k < argList.size(); k++)
	// System.out.println(argList.get(k).getName() + ", ");
	// }
	// }
	// }

	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		// super.evaluateDown(p);

		StatementList body = p.getBody();
		if (body != null && body.size() > 0) {

			String name = p.getName();
			ParameterList params = p.getParameters();

			OJClass clz = getEnvironment().lookupClass(
					getEnvironment().currentClassName());
			OJMethod[] methods = clz.getDeclaredMethods();
			OJMethod[] overloadingMethods = Toolbox.pickupMethodsByName(
					methods, name);

			if (overloadingMethods != null && overloadingMethods.length > 1
					&& params != null && params.size() > 0) {
				ArrayList<OJClass> paramList = new ArrayList<OJClass>();
				ArrayList<String> expList = new ArrayList<String>();

				for (int i = 0; i < params.size(); i++) {
					Parameter param = params.get(i);
					OJClass pClz = getEnvironment().lookupClass(
							param.getTypeSpecifier().toString());
					paramList.add(pClz);
					expList.add(param.getVariable());
				}

				int paramSize = paramList.size();
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				for (int index = 0; index < paramSize; index++) {
					int max = Helper.permutation(paramSize, index);
					for (int j = 0; j < max; j++) {
						List<OJClass> argList = getParameterTypes(paramList,
								index, j);
						OJClass[] args = argList.toArray(new OJClass[argList
								.size()]);
						OJMethod selectedMethod = Toolbox
								.pickupMethodByParameterTypes(
										overloadingMethods, args);
						int globalIndex = j
								+ Helper.sigmaPermutation(paramSize, index);
						if (selectedMethod != null) {
							indexList.add(globalIndex);
						}
					}
				}
				if (!indexList.isEmpty()) {
					ChangePoint cPoint = new ChangePoint(p, currentMethod);
					cPoint.setData(indexList);
					addChangePoint(cPoint);
				}
			}
		}

		// super.evaluateUp(p);
	}
}

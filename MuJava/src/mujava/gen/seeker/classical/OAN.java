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
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
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

public class OAN extends AbstractChangePointSeeker {

	public OAN(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "OAN");
	}

	// public static void main(String[] args) {
	// OAN oan = new OAN(null, null);
	// List<OJClass> ts = new ArrayList<OJClass>();
	// try {
	// ts.add(OJClass.forName("java.lang.Object"));
	// ts.add(OJClass.forName("java.lang.Boolean"));
	// ts.add(OJClass.forName("java.lang.Integer"));
	// ts.add(OJClass.forName("java.lang.String"));
	// // ts.add(OJClass.forName("java.lang.Object"));
	// } catch (OJClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// int paramSize = ts.size();
	// for (int index = 0; index < paramSize; index++) {
	// int max = oan.permutation(paramSize, index);
	// for (int j = 0; j < max; j++) {
	// List<OJClass> argList = oan.getParameterTypes(ts, index, j);
	// System.out.println(j + oan.sigmaPermutation(paramSize, index)
	// + " : ");
	// for (int k = 0; k < argList.size(); k++)
	// System.out.println(argList.get(k).getName() + ", ");
	// }
	// }
	// }

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

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		// Only if at least two arguments are exist
		ExpressionList list = p.getArguments();
		if (list.size() >= 1) {
			Expression exp = p.getReferenceExpr();
			TypeName type = p.getReferenceType();

			OJClass clz = null;
			// search the class where the method is defined to be called by p
			if (exp == null) {
				String childClz = null;
				if (type == null)
					childClz = getEnvironment().currentClassName();
				else
					childClz = type.toString();

				clz = getEnvironment().lookupClass(childClz);
			} else {
				clz = getType(exp);
			}

			if (clz != null) {
				// prepare the arguments type
				List<OJClass> args = new ArrayList<OJClass>();
				for (int i = 0; i < list.size(); i++) {
					Expression param = list.get(i);
					args.add(getType(param));
				}

				int paramSize = args.size();
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				OJMethod[] methods = clz.getAllMethods();
				if (methods != null) {
					for (int index = 0; index < paramSize; index++) {
						int max = Helper.permutation(paramSize, index);
						for (int j = 0; j < max; j++) {
							List<OJClass> argList = getParameterTypes(args,
									index, j);
							OJClass[] argsArray = argList
									.toArray(new OJClass[argList.size()]);
							OJMethod childMethod = Toolbox.pickupMethod(
									methods, p.getName(), argsArray);
							if (childMethod != null) {
								indexList.add(j
										+ Helper.sigmaPermutation(paramSize,
												index));
							}
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

		// keep going on searching change points on the arguments
		super.visit(list);
	}
}

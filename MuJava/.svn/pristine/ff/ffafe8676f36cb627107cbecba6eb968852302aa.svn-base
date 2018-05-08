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

public class OAO extends AbstractChangePointSeeker {

	public OAO(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "OAO");
	}

	// public static
	List<OJClass> getParameterTypes(List<OJClass> args, int depth, int index) {
		List<OJClass> list = new ArrayList<OJClass>();
		List<OJClass> listRest = new ArrayList<OJClass>();
		if (args.isEmpty()) {
			;
		} else if (args.size() == 1) {
			list.addAll(args);
		} else {
			int interval = permutation(depth - 1);
			int indexArray = index / interval;
			for (int i = 0; i < args.size(); i++) {
				if (i == indexArray)
					list.add(args.get(i));
				else
					listRest.add(args.get(i));
			}

			List<OJClass> retList = getParameterTypes(listRest, depth - 1,
					index % interval);
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
	// ts.add(OJClass.forName("java.lang.String"));
	// ts.add(OJClass.forName("java.lang.Object"));
	// } catch (OJClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// for (int i = 0; i < OAO.permutation(ts.size()); i++) {
	// List<OJClass> clzs = OAO.getParameterTypes(ts, ts.size(), i);
	// System.out.println(i + " : ");
	// for (int j = 0; j < ts.size(); j++)
	// System.out.println(clzs.get(j).getName() + ", ");
	// }
	//
	// }

	// public static
	int permutation(int size) {
		int mut = 1;
		for (int i = 1; i <= size; i++)
			mut = mut * i;

		return mut;
	}

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		// Only if at least two arguments are exist
		ExpressionList list = p.getArguments();
		if (list.size() >= 2) {
			Expression exp = p.getReferenceExpr();
			TypeName type = p.getReferenceType();

			OJClass clz = null;
			try {
				// search the class where the method is defined to be called by
				// p
				if (exp == null) {
					String childClz = null;
					if (type == null)
						childClz = getEnvironment().currentClassName();
					else
						childClz = type.getName();

					clz = OJClass.forName(childClz);
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

					ArrayList<Integer> indexList = new ArrayList<Integer>();
					OJMethod[] methods = clz.getAllMethods();
					if (methods != null) {
						for (int i = 1; i < permutation(args.size()); i++) {
							List<OJClass> argList = getParameterTypes(args,
									args.size(), i);
							OJClass[] argsArray = argList
									.toArray(new OJClass[argList.size()]);
							OJMethod childMethod = Toolbox.pickupMethod(
									methods, p.getName(), argsArray);
							if (childMethod != null) {
								indexList.add(i);
							}
						}
					}

					ChangePoint cPoint = new ChangePoint(p, currentMethod);
					cPoint.setData(indexList);
					addChangePoint(cPoint);
				}
			} catch (OJClassNotFoundException e) {
				e.printStackTrace();
				System.err.println("OAO");
			}
		}

		// keep going on searching change points on the arguments
		super.visit(list);
	}
}

package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.Helper;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.OMR;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author ysma
 * @edited swkim
 * @version 1.1
 */

public class OMR_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(OMR mutator)
			throws IOException {
		OMR_NormalStateWriter mutantWriter = new OMR_NormalStateWriter(mutator
				.getFileEnvironment());

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		HashMap<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();
		for (ChangePoint point : list) {
			List<?> indexList = (List<?>) point.getData();
			if (indexList == null)
				iterTable.put(point, 0);
			iterTable.put(point, indexList.size());
		}
		mutantWriter.setSizeOfIteration(iterTable);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public OMR_NormalStateWriter(Environment env) throws IOException {
		super(env, "OMR", false);
	}


	@SuppressWarnings("unchecked")
	public void visit(MethodDeclaration p) throws ParseTreeException {
		assert (super.currentChangePoint != null);
		// evaluateDown(p);
		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			Object obj = currentChangePoint.getData();
			if (obj != null && obj instanceof List) {
				int index = getNextMutantVariable(mutantID,
						(List<Integer>) obj, generatedMutantID);
				mutantID = mutantID + "_" + Integer.toString(index);

				/**
				 * Create MuJavaMutantInfo object to support mutant information
				 */
				// initial setting for normal way
				MuJavaMutantInfo m = new MuJavaMutantInfo();
				m.setChangeLocation(getLineNumber());
				m.setSizeOfSubID(1);
				m.setSubTypeOperator(index);
				m.setGenerationWay(MutantOperator.GEN_STATE);
				m.setMutationOperatorName(this.getMutationOperatorName());
				m.setMutantID(mutantID);

				// prepare domain list for expression strings
				ParameterList params = p.getParameters();
				List<String> exps = new ArrayList<String>();
				for (int i = 0; i < params.size(); i++) {
					Parameter param = params.get(i);
					exps.add(param.getVariable());
				}

				// make result list for expression strings
				int paramSize = exps.size();
				List<String> expList = new ArrayList<String>();
				if (index != 0) {
					int depth = getDepth(paramSize, index);
					int localIndex = index
							- Helper.sigmaPermutation(paramSize, depth);
					expList = getExpressionList(exps, depth, localIndex);
				}

				// writes mutated code
				OJClass retType = getEnvironment().lookupClass(
						p.getReturnType().toString());

				super.evaluateDown(p);

				writeTab();

				/* ModifierList */
				ModifierList modifs = p.getModifiers();
				if (modifs != null) {
					modifs.accept(this);
					// if (!modifs.isEmptyAsRegular())
					// out.print(" ");
				}
				out.print(" ");

				TypeName ts = p.getReturnType();
				ts.accept(this);

				out.print(" ");

				String name = p.getName();
				out.print(name);

				out.print("(");
				if (!params.isEmpty()) {
					out.print(" ");
					params.accept(this);
					out.print(" ");
				} else {
					params.accept(this);
				}
				out.print(")");

				TypeName[] tnl = p.getThrows();
				if (tnl.length != 0) {
					out.println();
					increaseLineNumber();
					writeTab();
					writeTab();
					out.print("throws ");
					tnl[0].accept(this);
					for (int i = 1; i < tnl.length; ++i) {
						out.print(", ");
						tnl[i].accept(this);
					}
				}

				out.println();
				increaseLineNumber();
				writeTab();
				out.print("{");
				out.println();
				increaseLineNumber();
				pushNest();

				// update change location
				m.setChangeLocation(getLineNumber());

				writeTab();
				if (retType != null && !retType.equals(OJSystem.VOID)) {
					out.print("return ");
				}

				out.print("this." + name + "(");
				if (!expList.isEmpty()) {
					out.print(expList.get(0));
					if (expList.size() > 1) {
						for (String exp : expList.subList(1, expList.size())) {
							out.print(", " + exp);
						}
					}
				}
				out.println(");");
				increaseLineNumber();

				popNest();
				writeTab();
				out.print("}");
				out.println();
				increaseLineNumber();

				super.evaluateUp(p);

				// generates log content
				m.setChangeLog(removeNewline("real body  => " + "this." + name
						+ "()"));

				// registers its mutation information
				super.generatedMutantID.add(mutantID);
				super.mujavaMutantInfos.add(m);
				increaseSizeOfChangedPoints();
				return;
			}
		}
		// evaluateUp(p);
		super.visit(p);
	}

	// public static void main(String[] args) throws IOException {
	// int paramSize = 4;
	// ArrayList<String> exps = new ArrayList<String>();
	// exps.add("1");
	// exps.add("A");
	// exps.add("\"a\"");
	// exps.add("5");
	// for (int index = 0; index < 41; index++) {
	// int depth = new OMR_NormalStateWriter(null).getDepth(paramSize, index);
	// int localIndex = index - Helper.sigmaPermutation(paramSize, depth);
	// List<String> expList = new OMR_NormalStateWriter(null)
	// .getExpressionList(exps, depth, localIndex);
	//
	// System.out.println(index + " : ");
	// for (String str : expList)
	// System.out.print(str + ", ");
	// System.out.println();
	// }
	//
	// }

	private int getDepth(int n, int index) {
		int var = 0;
		int per = 1;
		if (index == 0)
			return 0;

		do {
			per = Helper.sigmaPermutation(n, ++var);
		} while (index >= per);

		return var - 1;
	}

	private List<String> getExpressionList(List<String> exps, int depth,
			int index) {
		List<String> list = new ArrayList<String>();
		List<String> listRest = new ArrayList<String>();

		if (exps.isEmpty() || depth == 0) {
			;
		} else {
			int interval = Helper.permutation(exps.size(), depth) / exps.size();
			int indexArray = index / interval;
			for (int i = 0; i < exps.size(); i++) {
				if (i == indexArray)
					list.add(exps.get(i));
				else
					listRest.add(exps.get(i));
			}

			List<String> retList = getExpressionList(listRest, depth - 1, index
					% interval);
			list.addAll(retList);
		}

		return list;
	}

	private int getNextMutantVariable(String mutantID, List<Integer> indexList,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		if (indexList != null)
			for (int i = 0; i < indexList.size(); i++) {
				String id = newID + indexList.get(i);
				if (!generatedMutantID.contains(id))
					return indexList.get(i);
			}

		return 0;
	}
}

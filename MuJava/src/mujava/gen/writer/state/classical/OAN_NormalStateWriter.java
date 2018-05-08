package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.Helper;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.OAN;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

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

public class OAN_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(OAN mutator)
			throws IOException {
		OAN_NormalStateWriter mutantWriter = new OAN_NormalStateWriter(mutator
				.getFileEnvironment());

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		// if (!list.isEmpty())
		// MuJavaLogger.getLogger().debug(list.size());

		HashMap<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();
		for (ChangePoint point : list) {
			List<?> indexList = (List<?>) point.getData();
			if (indexList == null)
				iterTable.put(point, 0);
			iterTable.put(point, indexList.size());
			// MuJavaLogger.getLogger().debug("S " + indexList.size());
		}
		mutantWriter.setSizeOfIteration(iterTable);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public OAN_NormalStateWriter(Environment env) throws IOException {
		super(env, "OAN", false);
	}


	@SuppressWarnings("unchecked")
	public void visit(MethodCall p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

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

				// writes mutated code
				MethodCall p1 = (MethodCall) p.makeRecursiveCopy();
				ExpressionList args = p.getArguments();
				List<Expression> exps = new ArrayList<Expression>();
				for (int i = 0; i < args.size(); i++) {
					exps.add(args.get(i));
				}

				int paramSize = exps.size();
				List<Expression> expList = new ArrayList<Expression>();
				if (index != 0) {
					int depth = getDepth(paramSize, index);
					int localIndex = index
							- Helper.sigmaPermutation(paramSize, depth);
					expList = getExpressionList(exps, depth, localIndex);
				}
				ExpressionList newArgs = new ExpressionList();
				for (int i = 0; i < expList.size(); i++) {
					newArgs.add(expList.get(i));
				}
				p1.setArguments(newArgs);
				super.visit(p1);

				// generates log content
				m.setChangeLog(removeNewline(p.toFlattenString() + ";  =>  "
						+ p1.toFlattenString()));

				// registers its mutation information
				super.generatedMutantID.add(mutantID);
				super.mujavaMutantInfos.add(m);
				increaseSizeOfChangedPoints();
				return;
			}
		}

		super.visit(p);
	}

	// public static void main(String[] args) throws IOException {
	// OAN_NormalStateWriter wr = new OAN_NormalStateWriter(null);
	// for (int i = 0; i < 10; i++) {
	// int depth = wr.getDepth(i);
	// int localIndex = i - wr.sigmaPermutation(depth + 1);
	// System.out.println(localIndex);
	//
	// }
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

	private List<Expression> getExpressionList(List<Expression> exps,
			int depth, int index) {
		List<Expression> list = new ArrayList<Expression>();
		List<Expression> listRest = new ArrayList<Expression>();

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

			List<Expression> retList = getExpressionList(listRest, depth - 1,
					index % interval);
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

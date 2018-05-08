package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mujava.gen.seeker.classical.PNC;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.AllocationExpression;
import openjava.ptree.AssignmentExpression;
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

public class PNC_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	private Map<ChangePoint, List<String>> variableTable;

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(PNC mutator)
			throws IOException {
		PNC_NormalStateWriter mutantWriter = new PNC_NormalStateWriter(mutator
				.getFileEnvironment());
		Map<ChangePoint, List<String>> table = new HashMap<ChangePoint, List<String>>();

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// for (ChangePoint point : list) {
		// table.put(point, mutator.getVariableTable().get(
		// point.getTreeElement()));
		// }

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		mutantWriter.setVariableTable(table);
		return mutantWriter;
	}

	public PNC_NormalStateWriter(Environment env) throws IOException {
		super(env, "PNC", false);
	}

	public void visit(AllocationExpression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			// Expression lexpr = p.getLeft();
			//
			// int index = getNextMutantVariable(mutantID, p,
			// generatedMutantID);
			// mutantID = mutantID + "_" + Integer.toString(index);
			//
			// /**
			// * Create MuJavaMutantInfo object to support mutant information
			// */
			// // initial setting for normal way
			// MuJavaMutantInfo m = new MuJavaMutantInfo();
			// m.setChangeLocation(getLineNumber());
			// m.setSizeOfSubID(1);
			// m.setSubTypeOperator(index);
			// m.setGenerationWay(MutantOperator.GEN_NORMAL);
			// m.setMutantOperator(this.getMutantOperatorName());
			// m.setMutantID(mutantID);
			//
			// // writes mutated code
			// super.visit(p.getLeft());
			// out.print(" " + p.operatorString() + " ");
			// out.print(" " + getPRVCode(index) + " ");
			//
			// // generates log content
			// m.setChangeLog(removeNewline(p.toFlattenString() + "; => "
			// + lexpr.toString() + " = " + getPRVCode(index) + ";"));
			//
			// // registers its mutation information
			// super.generatedMutantID.add(mutantID);
			// super.mujavaMutantInfos.add(m);
			// increaseSizeOfChangedPoints();
		} else
			super.visit(p);
	}

	private String getPNCCode(int index) {
		List<String> vars = this.variableTable.get(currentChangePoint);
		assert (vars != null);
		assert (vars.size() > index);

		return vars.get(index);
	}

	private int getNextMutantVariable(String mutantID, AssignmentExpression p,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		List<String> vars = this.variableTable.get(currentChangePoint);
		assert (vars != null);

		for (int i = 0; i < vars.size(); i++) {
			String id = newID + Integer.toString(i);
			if (!generatedMutantID.contains(id))
				return i;
		}

		return 0;
	}

	public void setVariableTable(Map<ChangePoint, List<String>> variableTable) {
		assert (variableTable != null);
		this.variableTable = variableTable;

		// # of mutants which could be generated from a change point
		for (Iterator<ChangePoint> iter = variableTable.keySet().iterator(); iter
				.hasNext();) {
			ChangePoint point = iter.next();
			sizesOfIteration.put(point, variableTable.get(point).size());
		}
	}

}

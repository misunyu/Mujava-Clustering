package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JTI;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Variable;

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

public class JTI_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(JTI mutator)
			throws IOException {
		JTI_NormalStateWriter mutantWriter = new JTI_NormalStateWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		// mutantWriter.setVariableTable(table);
		return mutantWriter;
	}

	public JTI_NormalStateWriter(Environment env) throws IOException {
		super(env, "JTI", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			// pre mutant ID
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			// Type checking
			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				super.visit(p);
			}

			// Full-size mutant ID
			mutantID = mutantID + "_" + "0";

			// writes mutated code
			String newStr = "this." + p.toString();
			out.print(newStr);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(0);
			m.setMutantID(mutantID);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_STATE);
			String log_str = p.toString() + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			return;
		}

		super.visit(p);
	}

	// public void visit(AssignmentExpression p) throws ParseTreeException {
	// assert (super.currentChangePoint != null);
	//
	// if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
	// String mutantID = super.getMutantOperatorName() + "_"
	// + super.getTargetFileHashCode() + "_"
	// + currentChangePoint.getID();
	//
	// Expression lexpr = p.getLeft();
	//
	// int index = getNextMutantVariable(mutantID, p, generatedMutantID);
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
	// } else
	// super.visit(p);
	// }
	//
	// private String getPRVCode(int index) {
	// List<String> vars = this.variableTable.get(currentChangePoint);
	// assert (vars != null);
	// assert (vars.size() > index);
	//
	// return vars.get(index);
	// }
	//
	// private int getNextMutantVariable(String mutantID, AssignmentExpression
	// p,
	// List<String> generatedMutantID) {
	// String newID = mutantID + "_";
	//
	// List<String> vars = this.variableTable.get(currentChangePoint);
	// assert (vars != null);
	//
	// for (int i = 0; i < vars.size(); i++) {
	// String id = newID + Integer.toString(i);
	// if (!generatedMutantID.contains(id))
	// return i;
	// }
	//
	// return 0;
	// }
	//
	// public void setVariableTable(Map<ChangePoint, List<String>>
	// variableTable) {
	// assert (variableTable != null);
	// this.variableTable = variableTable;
	//
	// // # of mutants which could be generated from a change point
	// for (Iterator<ChangePoint> iter = variableTable.keySet().iterator(); iter
	// .hasNext();) {
	// ChangePoint point = iter.next();
	// sizesOfIteration.put(point, variableTable.get(point).size());
	// }
	// }

}

package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JTD;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
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

public class JTD_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(JTD mutator)
			throws IOException {
		JTD_NormalStateWriter mutantWriter = new JTD_NormalStateWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public JTD_NormalStateWriter(Environment env) throws IOException {
		super(env, "JTD", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
	}

	/**
	 * 대상으로 하는 SelfAccess는 반드시 FieldAccess만을 상위 객체로 가질수 있기 때문에 상위 객체에서
	 * ReferenceExpr을 구해 해당 changePoint인 경우에 이를 생략한다.
	 */
	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		Expression exp = p.getReferenceExpr();
		if (exp != null && super.currentChangePoint != null
				&& super.currentChangePoint.isSamePoint(p, currentMethod)) {

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
			String newStr = p.getName();
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
			String log_str = "this." + newStr + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
		} else
			super.visit(p);
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {

		if (super.currentChangePoint != null
				&& super.currentChangePoint.isSamePoint(p, currentMethod)) {

			Expression left = p.getLeft();
			Expression right = p.getRight();
			FieldAccess fa = (FieldAccess) left;
			String newName = fa.getName();

			// pre mutant ID
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			// // Type checking
			// OJClass retType = getType(p);
			// if (retType == null) {
			// MuJavaLogger.getLogger().error(
			// "Unhandled type object : " + p.toFlattenString());
			// super.visit(p);
			// }

			// Full-size mutant ID
			mutantID = mutantID + "_" + "0";

			// writes mutated code
			String newStr = newName;
			out.print(newStr + " = ");
			super.visit(right);

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
			String log_str = "this." + newStr + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
		} else
			super.visit(p);
	}
}

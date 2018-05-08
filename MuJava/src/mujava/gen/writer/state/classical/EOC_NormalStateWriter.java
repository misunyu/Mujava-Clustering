package mujava.gen.writer.state.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.EOC;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
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

public class EOC_NormalStateWriter extends ClassicalMutantNormalStateWriter {

	public static ClassicalMutantNormalStateWriter getMutantCodeWriter(EOC mutator)
			throws IOException {
		EOC_NormalStateWriter mutantWriter = new EOC_NormalStateWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		if (!list.isEmpty())
			MuJavaLogger.getLogger().debug(
					"ChangePoint for EOC : " + list.size());

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public EOC_NormalStateWriter(Environment env) throws IOException {
		super(env, "EOC", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
	}

	@Override
	public void visit(BinaryExpression p) throws ParseTreeException {
		if (super.currentChangePoint != null
				&& super.currentChangePoint.isSamePoint(p, currentMethod)) {

			// pre mutant ID
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			// Type checking
			OJClass leftType = getType(p.getLeft());
			if (leftType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				super.visit(p);
				return;
			}

			// Full-size mutant ID
			mutantID = mutantID + "_" + "0";

			// writes mutated code
			Expression left = p.getLeft();
			Expression right = p.getRight();

			super.visit(left);
			out.print(".equals(");
			super.visit(right);
			out.print(")");

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
			String log_str = left.toString() + "==" + right.toString() + " => "
					+ left.toString() + ".equals(" + right.toString() + ")";
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
		} else
			super.visit(p);
	}
}

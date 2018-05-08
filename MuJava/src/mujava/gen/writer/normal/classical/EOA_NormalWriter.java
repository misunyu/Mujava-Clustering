package mujava.gen.writer.normal.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.EOA;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
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

public class EOA_NormalWriter extends ClassicalMutantNormalWriter {

	public static ClassicalMutantNormalWriter getMutantCodeWriter(EOA mutator)
			throws IOException {
		EOA_NormalWriter mutantWriter = new EOA_NormalWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		// if (!list.isEmpty())
		// MuJavaLogger.getLogger().debug(
		// "ChangePoint for EOA : " + list.size());

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public EOA_NormalWriter(Environment env) throws IOException {
		super(env, "EOA", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {

		if (super.currentChangePoint != null
				&& super.currentChangePoint.isSamePoint(p, currentMethod)) {

			// pre mutant ID
			MutantID mutantID = MutantID.generateMutantID(super
					.getMutationOperatorName(), this.getTargetFileHashCode(),
					currentChangePoint.getID());

			// Type checking
			OJClass leftType = getType(p.getLeft());
			if (leftType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				super.visit(p);
				return;
			}

			super.evaluateDown(p);

			// Full-size mutant ID
			mutantID.setLastIndex(0);

			// writes mutated code
			Expression lexpr = p.getLeft();
			if (lexpr instanceof AssignmentExpression) {
				writeParenthesis(lexpr);
			} else {
				lexpr.accept(this);
			}

			String operator = p.operatorString();
			out.print(" " + operator + " ");
			out.print("(");
			out.print(leftType.getName());
			out.print(")");
			Expression rexp = p.getRight();
			rexp.accept(this);
			out.print(".clone()");

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(0);
			m.setMutantID(mutantID.toString());
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			String log_str = rexp.toFlattenString() + " => "
					+ rexp.toFlattenString() + ".clone()";
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID.toString());
			increaseSizeOfChangedPoints();

			super.evaluateUp(p);

		} else
			super.visit(p);
	}
}

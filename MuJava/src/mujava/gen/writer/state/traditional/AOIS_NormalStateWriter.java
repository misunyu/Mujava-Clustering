package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AOISChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edited by swkim
 * @version 1.1
 */

public class AOIS_NormalStateWriter extends TraditionalMutantNormalStateWriter {

	public static MutantWriter getMutantCodeWriter(AOISChangePointSeeker mutator)
			throws IOException {
		AOIS_NormalStateWriter mutantWriter = new AOIS_NormalStateWriter(mutator
				.getFileEnvironment());

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		List<ChangePoint> statementChangePoints = mutator.getChangePoints();
		mutantWriter.setStatementChangePoints(statementChangePoints);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file

		int hashCode = mutator.getTargetFileID();

		mutantWriter.setTargetFileHashCode(hashCode);
		mutantWriter.setSizeOfIteration(mutator.getIterationTable());
		return mutantWriter;
	}

	private List<ChangePoint> statements = new ArrayList<ChangePoint>();

	public AOIS_NormalStateWriter(Environment env) throws IOException {
		super(env, "AOIS", false);

		// # of mutants which could be generated from a change point
		// super.fixedSizeOfIteration = 4;
	}

	private void setStatementChangePoints(List<ChangePoint> list) {
		statements.clear();

		if (list != null) {
			statements.addAll(list);
		}
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				return;
			}

			/*
			 * Make a mutant expression. To generate a different mutant whenever
			 * this writer is executed, for a given a same change point, it
			 * record all mutants generated from the same change point and then
			 * figure out new mutant expression from the history
			 */

			// boolean isStatement = false;
			// for (ChangePoint point : statements) {
			// if (point.isSamePoint(p, currentMethod)) {
			// isStatement = true;
			// break;
			// }
			// }

			int size = 4;
			Integer value = sizesOfIteration.get(currentChangePoint);
			if (value != null) {
				size = value;
			}

			boolean isStatement = false;
			if (size != 4) {
				isStatement = true;
			}

			int newOperator = getNextMutantOperator(mutantID, size != 4,
					generatedMutantID);

			// full-size mutant id
			mutantID = mutantID + "_" + newOperator;

			// writes mutated code
			String exp = p.toString();
			String newStr = getAOISCode(newOperator, exp);
			out.print(" " + newStr + " ");

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(newOperator);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_STATE);
			m.setMutantID(mutantID);
			String log_str = p.toString() + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));
			
			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			setStateSavingMutantInfo(m);

			return;
		}

		super.visit(p);
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				return;
			}

			/*
			 * Make a mutant expression. To generate a different mutant whenever
			 * this writer is executed, for a given a same change point, it
			 * record all mutants generated from the same change point and then
			 * figure out new mutant expression from the history
			 */

			boolean isStatement = false;
			for (ChangePoint point : statements) {
				if (point.isSamePoint(p, currentMethod)) {
					isStatement = true;
					break;
				}
			}

			int newOperator = getNextMutantOperator(mutantID, isStatement,
					generatedMutantID);

			// full-size mutant id
			mutantID = mutantID + "_" + newOperator;

			// writes mutated code
			String exp = p.toFlattenString();
			String newStr = getAOISCode(newOperator, exp);
			out.print(" " + newStr + " ");

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(newOperator);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_STATE);
			m.setMutantID(mutantID);
			String log_str = p.toString() + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));

			needStateSavingCode = true;
			curMutant = m;
			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
			
			setStateSavingMutantInfo(m);
			
			return;
		}

		super.visit(p);
	}

	/**
	 * Create possible AOIS mutant Identifier. Then generated mutant identifier
	 * is added to generated mutant ID list. If mutant list contains the
	 * generated identifier, the other possible mutant identifier is created.
	 * Especially, only one identifier for each short-cut decreasing and
	 * increasing operator.
	 * 
	 * 
	 * @param mutantID
	 * @param generatedMutantID
	 * @return
	 */
	private int getNextMutantOperator(String mutantID, boolean isStatement,
			List<String> generatedMutantID) {

		String newID = mutantID + "_";

		String id = newID + UnaryExpression.POST_DECREMENT;
		if (!generatedMutantID.contains(id)) {
			return UnaryExpression.POST_DECREMENT;
		}
		id = newID + UnaryExpression.POST_INCREMENT;
		if (!generatedMutantID.contains(id)) {
			return UnaryExpression.POST_INCREMENT;
		}

		id = newID + UnaryExpression.PRE_DECREMENT;
		if (!generatedMutantID.contains(id)) {
			return UnaryExpression.PRE_DECREMENT;
		}
		id = newID + UnaryExpression.PRE_INCREMENT;
		if (!generatedMutantID.contains(id)) {
			return UnaryExpression.PRE_INCREMENT;
		}

		return 0;
	}

	private String getAOISCode(int i, String text) {
		switch (i) {
		case UnaryExpression.POST_DECREMENT:
			return text + "--";
		case UnaryExpression.POST_INCREMENT:
			return text + "++";
		case UnaryExpression.PRE_DECREMENT:
			return "--" + text;
		case UnaryExpression.PRE_INCREMENT:
			return "++" + text;
		}

		assert (false);
		return new String();
	}
}

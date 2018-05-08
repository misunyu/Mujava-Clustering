package mujava.gen.writer.normal.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AORBChangePointSeeker;
import mujava.gen.writer.MutantWriter;
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
 * 
 * @author swkim
 * @version 1.1
 */

public class AORB_NormalWriter extends TraditionalMutantNormalWriter {
	public static MutantWriter getMutantCodeWriter(AORBChangePointSeeker mutator)
			throws IOException {
		AORB_NormalWriter mutantWriter = new AORB_NormalWriter(mutator
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

	public AORB_NormalWriter(Environment env) throws IOException {
		super(env, "AORB", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 4;
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());

				super.visit(p);
				return;
			}

			/*
			 * Make a mutant expression. To generate a different mutant whenever
			 * this writer is executed, for a given a same change point, it
			 * record all mutants generated from the same change point and then
			 * figure out new mutant expression from the history
			 * 
			 */
			int operator = p.getOperator();

			int newOperator = getNextMutantOperator(mutantID, operator,
					generatedMutantID);
			// full-size mutant id
			mutantID = mutantID + "_" + newOperator;

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			// initial setting for normal way
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(newOperator);
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setMutantID(mutantID);
			m.setChangeLog(removeNewline(p.toString() + " => "
					+ p.getLeft().toFlattenString() + getAORBCode(newOperator)
					+ p.getRight().toFlattenString()));

			// writes mutated code
			Expression leftExp = p.getLeft();
			Expression rightExp = p.getRight();

			//writeParenthesis(leftExp);
			leftExp.accept(this);

			out.print(" " + getAORBCode(newOperator) + " ");

			//writeParenthesis(rightExp);
			rightExp.accept(this);

			// registers its mutation information
			if (!mujavaMutantInfos.contains(m)) {
				super.mujavaMutantInfos.add(m);
				super.generatedMutantID.add(mutantID);
				increaseSizeOfChangedPoints();
			}

		} else
			super.visit(p);

	}

	private int getNextMutantOperator(String mutantID, int operator,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		if (operator != BinaryExpression.TIMES) {
			String id = newID + BinaryExpression.TIMES;
			if (!generatedMutantID.contains(id)) {
				return BinaryExpression.TIMES;
			}
		}
		if (operator != BinaryExpression.DIVIDE) {
			String id = newID + BinaryExpression.DIVIDE;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.DIVIDE;
		}
		if (operator != BinaryExpression.MOD) {
			String id = newID + BinaryExpression.MOD;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.MOD;
		}
		if (operator != BinaryExpression.PLUS) {
			String id = newID + BinaryExpression.PLUS;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.PLUS;
		}
		if (operator != BinaryExpression.MINUS) {
			String id = newID + BinaryExpression.MINUS;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.MINUS;
		}

		assert (false);
		return 0;
	}

	private String getAORBCode(int i) {
		switch (i) {
		case BinaryExpression.TIMES:
			return "*";
		case BinaryExpression.DIVIDE:
			return "/";
		case BinaryExpression.MOD:
			return "%";
		case BinaryExpression.PLUS:
			return "+";
		case BinaryExpression.MINUS:
			return "-";
		}

		assert (false);
		return new String();
	}
}

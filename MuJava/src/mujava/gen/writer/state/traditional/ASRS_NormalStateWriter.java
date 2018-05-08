package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.ASRSChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.ParseTreeException;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class ASRS_NormalStateWriter extends TraditionalMutantNormalStateWriter {
	public static TraditionalMutantNormalStateWriter getMutantCodeWriter(
			ASRSChangePointSeeker mutator) throws IOException {
		ASRS_NormalStateWriter mutantWriter = new ASRS_NormalStateWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		mutantWriter.setSizeOfIteration(mutator.getIterationTable());
		return mutantWriter;
	}

	public ASRS_NormalStateWriter(Environment env) throws IOException {
		super(env, "ASRS", false);

		// # of mutants which could be generated from a change point - 1
		// super.fixedSizeOfIteration = 10;
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
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
			 */
			int operator = p.getOperator();

			int newOperator = getNextMutantOperator(mutantID, operator,
					generatedMutantID);
			mutantID = mutantID + "_" + newOperator;

			// writes mutated code
			Expression leftExp = p.getLeft();
			Expression rightExp = p.getRight();

			if (isOperatorNeededLeftPar(operator, leftExp)) {
				writeParenthesis(leftExp);
			} else {
				super.visit(leftExp);
			}

			out.print("/**here**/ " + getASRSCode(newOperator) + " ");

			if (isOperatorNeededLeftPar(operator, rightExp)) {
				writeParenthesis(rightExp);
			} else {
				super.visit(rightExp);
			}
			
			
			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			// initial setting for normal way
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(newOperator);
			m.setGenerationWay(MutantOperator.GEN_STATE);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setMutantID(mutantID);
			m.setChangeLog(removeNewline(p.toString() + " => "
					+ p.getLeft().toFlattenString() + getASRSCode(newOperator)
					+ p.getRight().toFlattenString()));

			
			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			setStateSavingMutantInfo(m);

			//System.out.println("Assignment p : " + p);
			if((p.getParent() instanceof ExpressionStatement)) {
				addAssignedVar(currentMethod.getObjectID(), leftExp.toString());
				out.println(";");
				this.increaseLineNumber();
				writeStateSavingCode();
			}
			
		} else
			super.visit(p);

	}

	private int getNextMutantOperator(String mutantID, int operator,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		if (isArithematicGroup(operator)
				&& operator != AssignmentExpression.ADD) {
			String id = newID + AssignmentExpression.ADD;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.ADD;
			}
		}
		if (isArithematicGroup(operator)
				&& operator != AssignmentExpression.SUB) {
			String id = newID + AssignmentExpression.SUB;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.SUB;
			}
		}
		if (isArithematicGroup(operator)
				&& operator != AssignmentExpression.MULT) {
			String id = newID + AssignmentExpression.MULT;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.MULT;
			}
		}
		if (isArithematicGroup(operator)
				&& operator != AssignmentExpression.DIVIDE) {
			String id = newID + AssignmentExpression.DIVIDE;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.DIVIDE;
			}
		}
		if (isArithematicGroup(operator)
				&& operator != AssignmentExpression.MOD) {
			String id = newID + AssignmentExpression.MOD;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.MOD;
			}
		}

		if (isShirtGroup(operator) && operator != AssignmentExpression.SHIFT_L) {
			String id = newID + AssignmentExpression.SHIFT_L;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.SHIFT_L;
			}
		}
		if (isShirtGroup(operator) && operator != AssignmentExpression.SHIFT_R) {
			String id = newID + AssignmentExpression.SHIFT_R;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.SHIFT_R;
			}
		}
		if (isShirtGroup(operator) && operator != AssignmentExpression.SHIFT_RR) {
			String id = newID + AssignmentExpression.SHIFT_RR;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.SHIFT_RR;
			}
		}

		if (isConditionGroup(operator) && operator != AssignmentExpression.OR) {
			String id = newID + AssignmentExpression.OR;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.OR;
			}
		}
		if (isConditionGroup(operator) && operator != AssignmentExpression.XOR) {
			String id = newID + AssignmentExpression.XOR;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.XOR;
			}
		}
		if (isConditionGroup(operator) && operator != AssignmentExpression.AND) {
			String id = newID + AssignmentExpression.AND;
			if (!generatedMutantID.contains(id)) {
				return AssignmentExpression.AND;
			}
		}

		assert (false);
		return 0;
	}

	private boolean isConditionGroup(int operator) {
		if (operator == AssignmentExpression.OR
				|| operator == AssignmentExpression.XOR
				|| operator == AssignmentExpression.AND)
			return true;

		return false;
	}

	private boolean isShirtGroup(int operator) {
		if (operator == AssignmentExpression.SHIFT_L
				|| operator == AssignmentExpression.SHIFT_R
				|| operator == AssignmentExpression.SHIFT_RR)
			return true;

		return false;
	}

	private boolean isArithematicGroup(int operator) {
		if (operator == AssignmentExpression.ADD
				|| operator == AssignmentExpression.DIVIDE
				|| operator == AssignmentExpression.MOD
				|| operator == AssignmentExpression.MULT
				|| operator == AssignmentExpression.SUB)
			return true;

		return false;
	}

	private String getASRSCode(int i) {
		switch (i) {
		case AssignmentExpression.ADD:
			return "+=";
		case AssignmentExpression.AND:
			return "&=";
		case AssignmentExpression.DIVIDE:
			return "/=";
		case AssignmentExpression.MOD:
			return "%=";
		case AssignmentExpression.MULT:
			return "*=";
		case AssignmentExpression.SHIFT_L:
			return "<<=";
		case AssignmentExpression.SHIFT_R:
			return ">>=";
		case AssignmentExpression.SHIFT_RR:
			return ">>=";
		case AssignmentExpression.SUB:
			return "-=";
		case AssignmentExpression.XOR:
			return "^=";
		case AssignmentExpression.OR:
			return "|=";
		}
		assert (false);
		return new String();
	}
}

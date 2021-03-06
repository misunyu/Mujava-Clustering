package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.BORChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ArrayAccess;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.IfStatement;
import openjava.ptree.Leaf;
import openjava.ptree.MethodCall;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class BOR_NormalStateWriter extends TraditionalMutantNormalStateWriter {
	public static TraditionalMutantNormalStateWriter getMutantCodeWriter(BORChangePointSeeker mutator)
			throws IOException {
		BOR_NormalStateWriter mutantWriter = new BOR_NormalStateWriter(mutator
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

	public BOR_NormalStateWriter(Environment env) throws IOException {
		super(env, "BOR", true);
		
		//written = true;
		needStateSavingCode = false;

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 3 - 1;
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

			//System.out.println("curMethod = " + this.currentMethod.getName());
			
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
			mutantID = mutantID + "_" + newOperator;

			// writes mutated code
			Expression leftExp = p.getLeft();
			Expression rightExp = p.getRight();

			if (isOperatorNeededLeftPar(operator, leftExp)) {
				writeParenthesis(leftExp);
			} else {
				super.visit(leftExp);
			}

			//System.out.println("BOR written FALSE leftExp: "  + leftExp + " rightExp: " + rightExp);
			//written = false;
			needStateSavingCode = true;

			out.print("/**here**/ " + getBORCode(newOperator) + " ");

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
					+ p.getLeft().toFlattenString() + getBORCode(newOperator)
					+ p.getRight().toFlattenString()));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			setStateSavingMutantInfo(m);

		} else
			super.visit(p);

	}

	private int getNextMutantOperator(String mutantID, int operator,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		if (operator != BinaryExpression.BITAND) {
			String id = newID + BinaryExpression.BITAND;
			if (!generatedMutantID.contains(id)) {
				return BinaryExpression.BITAND;
			}
		}
		if (operator != BinaryExpression.BITOR) {
			String id = newID + BinaryExpression.BITOR;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.BITOR;
		}
		if (operator != BinaryExpression.XOR) {
			String id = newID + BinaryExpression.XOR;
			if (!generatedMutantID.contains(id))
				return BinaryExpression.XOR;
		}

		assert (false);
		return 0;
	}

	private String getBORCode(int i) {
		switch (i) {
		case BinaryExpression.BITAND:
			return "&";
		case BinaryExpression.BITOR:
			return "|";
		case BinaryExpression.XOR:
			return "^";
		}

		assert (false);
		return new String();
	}
}

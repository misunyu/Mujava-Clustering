package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.LORChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.IfStatement;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class LOR_NormalStateWriter extends TraditionalMutantNormalStateWriter {
	public static TraditionalMutantNormalStateWriter getMutantCodeWriter(
			LORChangePointSeeker mutator) throws IOException {
		LOR_NormalStateWriter mutantWriter = new LOR_NormalStateWriter(mutator
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

	public LOR_NormalStateWriter(Environment env) throws IOException {
		super(env, "LOR", true);

		//written = true;
		needStateSavingCode = false;

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
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

			//written = false;
			needStateSavingCode = true;

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
			
			out.print("(");
			 if (isOperatorNeededLeftPar(operator, leftExp)) {
				 writeParenthesis(leftExp);
			 } else {  
				 super.visit(leftExp); 
			 }

			out.print("/**here**/ " + getLORCode(newOperator) + " ");

			 if (isOperatorNeededLeftPar(operator, rightExp)) {
				 writeParenthesis(rightExp);
			 } else {  
				 super.visit(rightExp);
			 }
			out.print(")");
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
					+ p.getLeft().toFlattenString() + getLORCode(newOperator)
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

		if (operator != BinaryExpression.LOGICAL_AND) {
			String id = newID + BinaryExpression.LOGICAL_AND;
			if (!generatedMutantID.contains(id)) {
				return BinaryExpression.LOGICAL_AND;
			}
		}
		if (operator != BinaryExpression.LOGICAL_OR) {
			String id = newID + BinaryExpression.LOGICAL_OR;
			if (!generatedMutantID.contains(id)) {
				return BinaryExpression.LOGICAL_OR;
			}
		}
		assert (false);
		return 0;
	}

	private String getLORCode(int i) {
		switch (i) {
		case BinaryExpression.LOGICAL_AND:
			return "&&";
		case BinaryExpression.LOGICAL_OR:
			return "||";
		}

		assert (false);
		return new String();
	}
}

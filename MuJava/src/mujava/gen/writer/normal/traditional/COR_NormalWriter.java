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
import mujava.gen.seeker.traditional.CORChangePointSeeker;
import mujava.op.traditional.COR;
import mujava.op.traditional.ROR;
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

public class COR_NormalWriter extends TraditionalMutantNormalWriter {
	public static TraditionalMutantNormalWriter getMutantCodeWriter(
			CORChangePointSeeker mutator) throws IOException {
		COR_NormalWriter mutantWriter = new COR_NormalWriter(mutator
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

	public COR_NormalWriter(Environment env) throws IOException {
		super(env, "COR", true);

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

			int	newOperator = COR.getNonRedundantCOROp(operator, extractMutantOpArray(mutantID, generatedMutantID)) ;
			
			mutantID = mutantID + "_" + newOperator;

			String mutatedExp = "";
			if(newOperator==COR.TRUE){
					out.print("true");
					mutatedExp = "true";
			}else if(newOperator==COR.FALSE){
					out.print("false");	
					mutatedExp = "false";
			}else{

				// writes mutated code
				Expression leftExp = p.getLeft();
				Expression rightExp = p.getRight();
				
				if(newOperator==COR.LHS){
					
					 if (isOperatorNeededLeftPar(operator, leftExp)) {
						 writeParenthesis(leftExp);
					 } else {  
						 super.visit(leftExp); 
					 }
					mutatedExp = "("+leftExp.toString()+")";
					
				}else if(newOperator==COR.RHS){
					
					 if (isOperatorNeededLeftPar(operator, rightExp)) {
						 writeParenthesis(rightExp);
					 } else {  
						 super.visit(rightExp); 
					 }
					mutatedExp = "("+rightExp.toString()+")";
					
				}else{					
					
				
					 if (isOperatorNeededLeftPar(operator, leftExp)) {
						 writeParenthesis(leftExp);
					 } else {  
					   super.visit(leftExp); 
					 }
		
					out.print(" " + COR.getCORCode(newOperator) + " ");
		
					 if (isOperatorNeededLeftPar(operator, rightExp)) {
						 writeParenthesis(rightExp);
					 } else {  
					   super.visit(rightExp);
					 }
					
					mutatedExp = "("+ leftExp.toFlattenString() + COR.getCORCode(newOperator) + rightExp.toFlattenString()+")";
				}
			}
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
			m.setChangeLog(removeNewline(p.toString() + " => "+ mutatedExp));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

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


}

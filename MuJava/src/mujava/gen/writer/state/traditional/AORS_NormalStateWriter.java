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
import mujava.gen.seeker.traditional.AORSChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ArrayAccess;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.UnaryExpression;
import openjava.ptree.WhileStatement;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class AORS_NormalStateWriter extends TraditionalMutantNormalStateWriter {
	
	private boolean writtenPassed = false;
	
	public static TraditionalMutantNormalStateWriter getMutantCodeWriter(
			AORSChangePointSeeker mutator) throws IOException {
		AORS_NormalStateWriter mutantWriter = new AORS_NormalStateWriter(mutator
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

	// private List<ChangePoint> inForStmts;
	//
	// private void setPrePostEQs(List<ChangePoint> list) {
	// this.inForStmts = list;
	// }

	public AORS_NormalStateWriter(Environment env) throws IOException {
		super(env, "AORS", false);

		// # of mutants which could be generated from a change point - 1
		// super.fixedSizeOfIteration = 3;
	}


	public void visit(AssignmentExpression p) throws ParseTreeException {
	//	System.out.println("AORB_NormalWriter visit(AssignmentExpression p) " + p.toString());
		
		super.evaluateDown(p);

		Expression lexpr = p.getLeft();

		if (lexpr instanceof AssignmentExpression) {
			writeParenthesis(lexpr);

		} else {
			
			addAssignedVar(currentMethod.getObjectID(), lexpr.toString());
			lexpr.accept(this);
		}
		

		String operator = p.operatorString();
		out.print(" " + operator + " ");

		Expression rexp = p.getRight();
		rexp.accept(this);
		
			
		//if(!written) {
		if(needStateSavingCode){
			//System.out.println("AORS parent = " + p.getParent().getClass().toString());
			//System.out.println("AORS parent = " + p.getParent().getParent().getClass().toString());
			
			if(writtenPassed || p.getParent().getParent() instanceof StatementList) {
			
				out.println(";");
				this.increaseLineNumber();
				
				writeStateSavingCode();
				//written = true;
				needStateSavingCode = false;
			}
			
			writtenPassed = true;
		}
		
		super.evaluateUp(p);
		
	}
	public void visit(UnaryExpression p) throws ParseTreeException {
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
			
			addAssignedVar(currentMethod.getObjectID(), p.toString());
					
			//written = false;
			needStateSavingCode = true;
			writtenPassed = false;
			
			/*
			 * Make a mutant expression. To generate a different mutant whenever
			 * this writer is executed, for a given a same change point, it
			 * record all mutants generated from the same change point and then
			 * figure out new mutant expression from the history
			 */

			int size = 3;
			Integer value = sizesOfIteration.get(currentChangePoint);
			if (value != null) {
				size = value;
			}

			boolean isStatement = false;
			if (size != 3) {
				isStatement = true;
			}

			int operator = p.getOperator();
			int newOperator = getNextMutantOperator(mutantID, operator,
					size != 3, generatedMutantID, false/* inForStatement */);

			// full-size mutant id
			mutantID = mutantID + "_" + newOperator;

			// writes mutated code
			Expression exp = p.getExpression();
			String newStr = getAORSCode(newOperator, exp.toFlattenString());
			out.print("/**here**/ " + newStr + " ");

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
			m.setChangeLog(removeNewline(p.toString() + " => " + newStr));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			setStateSavingMutantInfo(m);

		} else {
			super.visit(p);
		}
	}

	private int getNextMutantOperator(String mutantID, int operator,
			boolean isStatement, List<String> generatedMutantID,
			boolean inForStmts) {
		String newID = mutantID + "_";

		if (isStatement) {
			// a-- || --a ==> only A++
			if (operator == UnaryExpression.POST_DECREMENT
					|| operator == UnaryExpression.PRE_DECREMENT) {
				String id = newID + UnaryExpression.POST_INCREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.POST_INCREMENT;
				}
			}
			// a++ || ++a ==> only A--;
			if (operator == UnaryExpression.POST_INCREMENT
					|| operator == UnaryExpression.PRE_INCREMENT) {
				String id = newID + UnaryExpression.POST_DECREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.POST_DECREMENT;
				}
			}
		} else {
			if (operator != UnaryExpression.POST_DECREMENT) {
				String id = newID + UnaryExpression.POST_DECREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.POST_DECREMENT;
				}
			}
			if (operator != UnaryExpression.POST_INCREMENT) {
				String id = newID + UnaryExpression.POST_INCREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.POST_INCREMENT;
				}
			}
			if (operator != UnaryExpression.PRE_DECREMENT) {
				String id = newID + UnaryExpression.PRE_DECREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.PRE_DECREMENT;
				}
			}
			if (operator != UnaryExpression.PRE_INCREMENT) {
				String id = newID + UnaryExpression.PRE_INCREMENT;
				if (!generatedMutantID.contains(id)) {
					return UnaryExpression.PRE_INCREMENT;
				}
			}
		}

		assert (false);
		return 0;
	}

	private String getAORSCode(int i, String text) {
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

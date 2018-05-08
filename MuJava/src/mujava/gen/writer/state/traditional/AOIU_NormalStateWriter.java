package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AOIUChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
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

public class AOIU_NormalStateWriter extends TraditionalMutantNormalStateWriter {

	public static MutantWriter getMutantCodeWriter(AOIUChangePointSeeker mutator)
			throws IOException {
		AOIU_NormalStateWriter mutantWriter = new AOIU_NormalStateWriter(mutator
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

	public AOIU_NormalStateWriter(Environment env) throws IOException {
		super(env, "AOIU", true);

		// # of mutants which could be generated from a change point
		super.fixedSizeOfIteration = 1;
	}

	private boolean writeCode(Expression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				return true;
			}

			// generate full-size mutant id;
			mutantID = mutantID + "_" + UnaryExpression.MINUS;

			// write mutant code
			String newStr = "-(" + p.toString() + ")";
			out.print(newStr);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(UnaryExpression.MINUS);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_STATE);
			m.setMutantID(mutantID);
			m.setChangeLog(removeNewline(p.toString() + " => " + newStr));

			// registers its mutation information
			super.generatedMutantID.add(mutantID);
			super.mujavaMutantInfos.add(m);
			increaseSizeOfChangedPoints();
			
			setStateSavingMutantInfo(m);

			return false;
		}
		return true;
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}

	public void visit(MethodCall mc) throws ParseTreeException {

		if (super.currentChangePoint.isSamePoint(mc, currentMethod)) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ this.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			OJClass retType = getType(mc);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + mc.toFlattenString());
				super.visit(mc);
			}

			// generate full-size mutant id;
			mutantID = mutantID + "_" + UnaryExpression.MINUS;

			// writes mutated code
			String newStr = "-(" + mc.toString() + ")";
			out.print(newStr);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(UnaryExpression.MINUS);
			m.setMutantID(mutantID);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_STATE);
			m.setChangeLog(removeNewline(mc.toString() + " => " + newStr));

			// registers its mutation information
			super.generatedMutantID.add(mutantID);
			super.mujavaMutantInfos.add(m);
			increaseSizeOfChangedPoints();
			
			setStateSavingMutantInfo(m);
			
			return;
		}

		super.visit(mc);
	}

	public void visit(Variable p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}
}

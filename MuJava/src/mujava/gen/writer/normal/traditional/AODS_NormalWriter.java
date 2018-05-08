package mujava.gen.writer.normal.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AODSChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

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

public class AODS_NormalWriter extends TraditionalMutantNormalWriter {

	public static MutantWriter getMutantCodeWriter(AODSChangePointSeeker mutator)
			throws IOException {
		AODS_NormalWriter mutantWriter = new AODS_NormalWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		list = mutator.getEmptyPoints();
		mutantWriter.setEmptyPoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	private List<ChangePoint> emptyPoints;

	private void setEmptyPoints(List<ChangePoint> list) {
		this.emptyPoints = list;
	}

	public AODS_NormalWriter(Environment env) throws IOException {
		super(env, "AODS", true);

		// # of mutants which could be generated from a change point - 1
		super.fixedSizeOfIteration = 1;
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			MutantID mutantID = MutantID.generateMutantID(super
					.getMutationOperatorName(), super.getTargetFileHashCode(),
					currentChangePoint.getID());

			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				return;
			}

			mutantID.setLastIndex(p.getOperator());

			// writes mutated code
			String newStr = p.getExpression().toString();

			for (ChangePoint point : emptyPoints) {
				if (point.isSamePoint(p, currentMethod)) {
					newStr = "//" + p.toString();
					break;
				}
			}
			out.print(newStr);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(p.getOperator());
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			m.setMutantID(mutantID.toString());
			String log_str = p.toString() + " => " + newStr;
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID.toString());
			increaseSizeOfChangedPoints();
			return;
		}

		super.visit(p);
	}
}

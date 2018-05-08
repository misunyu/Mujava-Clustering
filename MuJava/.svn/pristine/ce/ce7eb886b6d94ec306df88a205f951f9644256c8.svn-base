////////////////////////////////////////////////////////////////////////////
// Module : JSI_Writer.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.writer.normal.classical;

import java.io.IOException;
import java.util.List;

import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JSI;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class JSI_NormalWriter extends ClassicalMutantNormalWriter {

	boolean isMutantTarget = false;

	public JSI_NormalWriter(Environment env) throws IOException {
		super(env, "JSI", true);

		// # of mutants which could be generated from a change point => 1
		super.fixedSizeOfIteration = 1;
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, null)) {
			isMutantTarget = true;
			super.visit(p);
			isMutantTarget = false;
		} else {
			super.visit(p);
		}
	}

	public void visit(ModifierList p) throws ParseTreeException {
		if (isMutantTarget) {
			ModifierList temp = (ModifierList) p.makeRecursiveCopy();
			temp.add(ModifierList.STATIC);
			super.visit(temp);

			// -------------------------------------------------------------
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID() + "_" + "0";

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			// initial setting for normal way
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(0);
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setMutantID(mutantID);
			m.setChangeLog(removeNewline("static is inserted"));
			m.setMutantedClassName(env.currentClassName());

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
		} else {
			super.visit(p);
		}
	}

	public static ClassicalMutantNormalWriter getMutantCodeWriter(JSI mutator)
			throws IOException {
		JSI_NormalWriter mutantWriter = new JSI_NormalWriter(mutator
				.getFileEnvironment());

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		// mutantWriter.setVariableTable(table);
		return mutantWriter;
	}
}

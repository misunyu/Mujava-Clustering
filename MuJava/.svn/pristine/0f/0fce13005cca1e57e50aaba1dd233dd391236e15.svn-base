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
import mujava.gen.seeker.classical.AMC;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.MethodDeclaration;
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

public class AMC_NormalWriter extends ClassicalMutantNormalWriter {

	public static ClassicalMutantNormalWriter getMutantCodeWriter(AMC mutator)
			throws IOException {
		AMC_NormalWriter mutantWriter = new AMC_NormalWriter(mutator
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

	private boolean isMutantTarget = false;

	public AMC_NormalWriter(Environment env) throws IOException {
		super(env, "AMC", true);

		// # of mutants which could be generated from a change point => 1
		super.fixedSizeOfIteration = 3;
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			isMutantTarget = true;
			super.visit(p);
			isMutantTarget = false;
		} else {
			super.visit(p);
		}
	}

	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (super.currentChangePoint.isSamePoint(p, currentMethod)) {
			isMutantTarget = true;
			super.visit(p);
			isMutantTarget = false;
		} else {
			super.visit(p);
		}
	}

	public void visit(ModifierList p) throws ParseTreeException {
		if (isMutantTarget) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			int type = getNextMutantVariable(mutantID, p, generatedMutantID);
			mutantID = mutantID + "_" + Integer.toString(type);

			ModifierList temp = (ModifierList) p.makeRecursiveCopy();

			// remove all accessability modifier
			temp.setRegular(temp.getRegular() & ~ModifierList.PRIVATE
					& ~ModifierList.PROTECTED & ~ModifierList.PUBLIC);
			// reset another modifier
			if (type != ModifierList.EMPTY)
				temp.setRegular(type);

			super.visit(temp);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			// initial setting for normal way
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(type);
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setMutantID(mutantID);
			m.setChangeLog(removeNewline(p.toFlattenString() + "-> "
					+ temp.toFlattenString()));
			m.setMutantedClassName(env.currentClassName());

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();
		} else {
			super.visit(p);
		}
	}

	private int getNextMutantVariable(String mutantID, ModifierList p,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		if (p == null) {
			String id = newID + ModifierList.PRIVATE;
			if (!generatedMutantID.contains(id))
				return ModifierList.PRIVATE;
			id = newID + ModifierList.PROTECTED;
			if (!generatedMutantID.contains(id))
				return ModifierList.PROTECTED;
			id = newID + ModifierList.PUBLIC;
			if (!generatedMutantID.contains(id))
				return ModifierList.PUBLIC;
		} else if (p.contains(ModifierList.PRIVATE)) {
			String id = newID + ModifierList.EMPTY;
			if (!generatedMutantID.contains(id))
				return ModifierList.EMPTY;
			id = newID + ModifierList.PROTECTED;
			if (!generatedMutantID.contains(id))
				return ModifierList.PROTECTED;
			id = newID + ModifierList.PUBLIC;
			if (!generatedMutantID.contains(id))
				return ModifierList.PUBLIC;
		} else if (p.contains(ModifierList.PROTECTED)) {
			String id = newID + ModifierList.EMPTY;
			if (!generatedMutantID.contains(id))
				return ModifierList.EMPTY;
			id = newID + ModifierList.PRIVATE;
			if (!generatedMutantID.contains(id))
				return ModifierList.PRIVATE;
			id = newID + ModifierList.PUBLIC;
			if (!generatedMutantID.contains(id))
				return ModifierList.PUBLIC;
		} else if (p.contains(ModifierList.PUBLIC)) {
			String id = newID + ModifierList.EMPTY;
			if (!generatedMutantID.contains(id))
				return ModifierList.EMPTY;
			id = newID + ModifierList.PROTECTED;
			if (!generatedMutantID.contains(id))
				return ModifierList.PROTECTED;
			id = newID + ModifierList.PRIVATE;
			if (!generatedMutantID.contains(id))
				return ModifierList.PRIVATE;
		}

		return 0;
	}
}

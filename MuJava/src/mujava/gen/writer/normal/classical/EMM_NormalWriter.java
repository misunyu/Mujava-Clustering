package mujava.gen.writer.normal.classical;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.EMM;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author ysma
 * @edited swkim
 * @version 1.1
 */

public class EMM_NormalWriter extends ClassicalMutantNormalWriter {
	private Map<ChangePoint, List<String>> methodTable = null;

	@SuppressWarnings("unchecked")
	public static ClassicalMutantNormalWriter getMutantCodeWriter(EMM mutator)
			throws IOException {
		EMM_NormalWriter mutantWriter = new EMM_NormalWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		if (!list.isEmpty())
			MuJavaLogger.getLogger().debug(
					"ChangePoint for EAM : " + list.size());

		HashMap<ChangePoint, Integer> iterTable = new HashMap<ChangePoint, Integer>();
		HashMap<ChangePoint, List<String>> methodTable = new HashMap<ChangePoint, List<String>>();
		for (ChangePoint point : list) {
			List<String> nameList = (List<String>) point.getData();
			if (nameList == null)
				iterTable.put(point, 0);
			iterTable.put(point, nameList.size());
			methodTable.put(point, nameList);
		}
		mutantWriter.setSizeOfIteration(iterTable);
		mutantWriter.setMethodTable(methodTable);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	private void setMethodTable(HashMap<ChangePoint, List<String>> methodTable) {
		this.methodTable = methodTable;
	}

	public EMM_NormalWriter(Environment env) throws IOException {
		super(env, "EMM", false);
	}

	@Override
	public void visit(MethodCall p) throws ParseTreeException {

		if (super.currentChangePoint != null
				&& super.currentChangePoint.isSamePoint(p, currentMethod)) {

			// pre mutant ID
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_"
					+ currentChangePoint.getID();

			// Type checking
			OJClass retType = getType(p);
			if (retType == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());
				super.visit(p);
				return;
			}

			// Full-size mutant ID
			int index = getNextMutantVariable(mutantID, p, generatedMutantID);
			mutantID = mutantID + "_" + Integer.toString(index);

			// writes mutated code
			MethodCall p1 = (MethodCall) p.makeRecursiveCopy();
			p1.setName(getEMMCode(index));
			super.visit(p1);

			/**
			 * Create MuJavaMutantInfo object to support mutant information
			 */
			MuJavaMutantInfo m = new MuJavaMutantInfo();
			m.setChangeLocation(getLineNumber());
			m.setSizeOfSubID(1);
			m.setSubTypeOperator(0);
			m.setMutantID(mutantID);
			m.setMutationOperatorName(this.getMutationOperatorName());
			m.setGenerationWay(MutantOperator.GEN_NORMAL);
			String log_str = p.toFlattenString() + " => "
					+ p1.toFlattenString();
			m.setChangeLog(removeNewline(log_str));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			super.generatedMutantID.add(mutantID);
			increaseSizeOfChangedPoints();

			return;
		}

		super.visit(p);
	}

	private String getEMMCode(int index) {
		List<String> vars = this.methodTable.get(currentChangePoint);
		assert (vars != null);
		assert (vars.size() > index);

		return vars.get(index);
	}

	private int getNextMutantVariable(String mutantID, MethodCall p,
			List<String> generatedMutantID) {
		String newID = mutantID + "_";

		List<String> vars = this.methodTable.get(currentChangePoint);
		assert (vars != null);

		for (int i = 0; i < vars.size(); i++) {
			String id = newID + Integer.toString(i);
			if (!generatedMutantID.contains(id))
				return i;
		}

		return 0;
	}

}

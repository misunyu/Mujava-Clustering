package mujava.gen.writer.MSG.classical;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.PRV;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.ParseTreeException;

/**
 * 
 * @author swkim
 * @version 1.1
 */

public class PRV_MSGWriter extends ClassicalMutantMSGWriter {
	private Map<ChangePoint, List<String>> variableTable = new HashMap<ChangePoint, List<String>>();

	public static MutantWriter getMutantCodeWriter(PRV mutator)
			throws IOException {
		PRV_MSGWriter mutantWriter = new PRV_MSGWriter(mutator
				.getFileEnvironment());
		Map<ChangePoint, List<String>> table = new HashMap<ChangePoint, List<String>>();

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		for (ChangePoint point : list) {
			table.put(point, mutator.getVariableTable().get(
					point.getTreeElement()));
		}
		mutantWriter.setVariableTable(table);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public void setVariableTable(Map<ChangePoint, List<String>> variableTable) {
		assert (variableTable != null);
		this.variableTable.clear();
		this.variableTable.putAll(variableTable);

		// # of mutants which could be generated from a change point
		// for (Iterator<ChangePoint> iter = variableTable.keySet().iterator();
		// iter
		// .hasNext();) {
		// ChangePoint point = iter.next();
		// sizesOfIteration.put(point, variableTable.get(point).size());
		// }
	}

	public PRV_MSGWriter(Environment env) throws IOException {
		super(env, "PRV");
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				// 본 Change Point에서 대체 가능한 Variable들을 구한다.
				List<String> vars = this.variableTable.get(cPoint);
				// 대체될 Variable들이 없는 경우에는 굳이 아무런 일을 하지 않는다.
				if (vars.size() == 0) {
					super.visit(p);
					return;
				}

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				// In case of Assignment, some logic is required to be executed
				// in the super.evaluateDown()
				evaluateDown(p);

				// writes mutated code
				super.visit(p.getLeft());
				out.print(" " + p.operatorString() + " "); // "="
				out.print("(" + retType.getName()
						+ " )(MSG.MSGHelper.isGivenID(\"" + mutantID + "\","
						+ cPoint.getID()
						+ ") ? MSG.classical.PRVMetaMutant.PRV(new "
						+ retType.getName() + "[] {");

				phase = ORIGINAL;
				StringBuffer buf = new StringBuffer();
				for (String var : vars) {
					buf.append(var + ",");
				}
				buf.deleteCharAt(buf.length() - 1);
				out.print(buf.toString());
				out.print(" }");
				out.print(", ");
				out.print("\"" + mutantID + "\"");
				out.print(") : ");

				phase = IMPLEMENTED;

				super.visit(p.getRight());
				out.print(")");

				// generates log content
				String log = removeNewline(p.toFlattenString() + ";  =>  "
						+ p.getLeft().toString() + " = PRVGen(List[...])");

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";

				/**
				 * Create MuJavaMutantInfo objects to support mutant information
				 */
				for (int i = 0; i < vars.size(); i++) {
					mutantID.setLastIndex(i);
					// initial setting for normal way
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_MSG, vars.size(), i, i,
							getLineNumber(), log);

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}

				// Writer에서 생성되었음을 알수 있는 counter
				increaseSizeOfChangedPoints();
				return;
			}
		}
		super.visit(p);
	}
}

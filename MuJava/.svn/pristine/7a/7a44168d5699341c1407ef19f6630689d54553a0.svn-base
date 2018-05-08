package mujava.gen.writer.nujava.classical;

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

public class PRVNuJavaWriter extends ClassicalNuJavaWriter {

	protected PRVNuJavaWriter(Environment env) throws IOException {
		super(env, "PRV");

	}

	private Map<ChangePoint, List<String>> variableTable = new HashMap<ChangePoint, List<String>>();

	public static MutantWriter getMutantCodeWriter(PRV mutator)
			throws IOException {
		PRVNuJavaWriter mutantWriter = new PRVNuJavaWriter(mutator
				.getFileEnvironment());
		Map<ChangePoint, List<String>> table = new HashMap<ChangePoint, List<String>>();

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		for (ChangePoint point : list) {
			table.put(point, mutator.getVariableTable().get(
					point.getTreeElement()));
		}

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		mutantWriter.setVariableTable(table);
		return mutantWriter;
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				// 본 Change Point에서 대체 가능한 Variable들을 구한다.
				List<String> vars = this.variableTable.get(cPoint);
				// 대체될 Variable들이 없는 경우에는 굳이 아무런 일을 하지 않는다.
				if (vars.size() == 0) {
					super.visit(p);
					return;
				}

				OJClass type = getType(p.getLeft());
				if (type == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				String mutantID = super.getMutationOperatorName() + "_"
						+ super.getTargetFileHashCode() + "_" + cPoint.getID();

				// In case of Assignment, some logic is required to be executed
				// in the super.evaluateDown()
				evaluateDown(p);

				// writes mutated code
				super.visit(p.getLeft());

				out.print(" " + p.operatorString() + " "); // "="
				out.print("(" + type.getName()
						+ " )(nujava.NuJavaHelper.isGivenID(\"" + mutantID
						+ "\", " + MutantID.getChangePointID(mutantID)
						+ ") ? nujava.classical.PRVMetaMutant.PRVGen(new "
						+ type.getName() + "[] {");

				phase = ORIGINAL;
				StringBuffer buf = new StringBuffer();
				buf.append(p.getRight().toFlattenString() + ",");
				for (String var : vars) {
					buf.append(var + ",");
				}
				buf.deleteCharAt(buf.length() - 1);
				out.print(buf.toString());
				out.print(" }");
				out.print(", ");
				super.visit(p.getRight());
				out.print(", ");
				out.print("\"" + mutantID + "\"");
				out.print(") : ");

				phase = IMPLEMENTED;

				super.visit(p.getRight());
				out.print(")");

				// generates log content
				String log = removeNewline(p.toFlattenString() + ";  =>  "
						+ p.getLeft().toString() + " = PRVGen(List[...])");

				/**
				 * Create MuJavaMutantInfo objects to support mutant information
				 */
				for (int i = 0; i < vars.size(); i++) {
					// initial setting for normal way
					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID + "_"
							+ i, getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_NUJAVA, vars.size(), 0, i + 1,
							getLineNumber(), removeNewline(log));

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}

				increaseSizeOfChangedPoints();
				return;
			}
		}

		super.visit(p);
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
}

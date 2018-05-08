package mujava.gen.writer.nujava.classical;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.EAM;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.Expression;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

public class EAMNuJavaWriter extends ClassicalNuJavaWriter {

	protected EAMNuJavaWriter(Environment env) throws IOException {
		super(env, "EAM");
	}

	private Map<ChangePoint, List<String>> alternativeMethods = new HashMap<ChangePoint, List<String>>();

	@SuppressWarnings("unchecked")
	public static MutantWriter getMutantCodeWriter(EAM mutator)
			throws IOException {
		EAMNuJavaWriter mutantWriter = new EAMNuJavaWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		HashMap<ChangePoint, List<String>> methodTable = new HashMap<ChangePoint, List<String>>();
		for (ChangePoint point : list) {
			List<String> nameList = (List<String>) point.getData();
			methodTable.put(point, nameList);
		}
		mutantWriter.setAlternativeMethodTable(methodTable);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		return mutantWriter;
	}

	private void setAlternativeMethodTable(
			HashMap<ChangePoint, List<String>> methodTable) {
		this.alternativeMethods.clear();
		this.alternativeMethods.putAll(methodTable);
	}

	public void visit(MethodCall mc) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(mc);
			if (cPoint != null) {

				// 본 Change Point에서 대체 가능한 Method구한다.
				List<String> methods = this.alternativeMethods.get(cPoint);
				// 대체될 Variable들이 없는 경우에는 굳이 아무런 일을 하지 않는다.
				if (methods.size() == 0) {
					super.visit(mc);
					return;
				}

				OJClass type = getType(mc);
				if (type == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + mc.toFlattenString());

					super.visit(mc);
					return;
				}

				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				// writes mutated code
				if (type != null) {
					out.print("(" + type.getName() + " )");
					// out.print("(nujava.NuJavaHelper.isGivenID(\"" + mutantID
					// + "\", " + MutantID.getChangePointID(IDstr)+") ?
					// nujava.classical.EAMMetaMutant.EAMGen(new "
					// + type.getName() + "[] {");
					Expression mcExp = mc.getReferenceExpr();
					TypeName mcType = mc.getReferenceType();

					String expStr = "";
					String typeStr = "null";

					if (mcExp == null && mcType == null) {
						expStr = "this";
					} else if (mcExp != null) {
						if (mcExp.equals("super"))
							typeStr = "super.getClass()";
						expStr = mcExp.toFlattenString();
					} else if (mcType != null) {
						typeStr = mcType.getName() + ".class";
					}

					out.print("(nujava.NuJavaHelper.isGivenID(\"" + mutantID
							+ "\", " + cPoint.getID()
							+ ") ? nujava.classical.EAMMetaMutant.EAMGen("
							+ expStr + ", " + typeStr + ", new String [] {");

					phase = ORIGINAL;
					StringBuffer buf = new StringBuffer();
					buf.append("\"" + mc.getName() + "\",");
					for (String var : methods) {
						buf.append("\"" + var + "\",");
					}
					buf.deleteCharAt(buf.length() - 1);
					out.print(buf.toString());
					out.print(" }");
					out.print(", \"" + mc.getName() + "\"");
					out.print(", ");
					out.print("\"" + mutantID + "\", ");
					if (type.isPrimitive())
						out.print("(" + type.toString() + ")");
					else
						out.print("null");

					out.print(") : ");

					phase = IMPLEMENTED;

					super.visit(mc);
					out.print(")");

					// generates log content
					String log = removeNewline(mc.toFlattenString() + "  =>  "
							+ " EAMGen(List[...])");

					/**
					 * Create MuJavaMutantInfo objects to support mutant
					 * information
					 */
					for (int i = 0; i < methods.size(); i++) {
						mutantID.setLastIndex(i);
						// initial setting for normal way
						String parentMethod = (currentMethod != null) ? currentMethod
								.getName()
								: "";
						MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
								+ "_0", getMutationOperatorName(), env
								.currentClassName(), parentMethod,
								MutantOperator.GEN_NUJAVA, methods.size(), 0,
								i, getLineNumber(), removeNewline(log));

						// registers its mutation information
						super.mujavaMutantInfos.add(m);
					}

					increaseSizeOfChangedPoints();

					return;
				}
			}
		}

		super.visit(mc);
	}
}

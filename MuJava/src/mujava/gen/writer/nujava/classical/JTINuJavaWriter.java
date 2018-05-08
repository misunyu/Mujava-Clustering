package mujava.gen.writer.nujava.classical;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JTI;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Variable;

public class JTINuJavaWriter extends ClassicalNuJavaWriter {

	private Map<String, String> parameterList = new HashMap<String, String>();

	protected JTINuJavaWriter(Environment env) throws IOException {
		super(env, "JTI");

	}

	// private Map<ChangePoint, List<String>> variableTable = new
	// HashMap<ChangePoint, List<String>>();

	public static MutantWriter getMutantCodeWriter(JTI mutator)
			throws IOException {
		JTINuJavaWriter mutantWriter = new JTINuJavaWriter(mutator
				.getFileEnvironment());
		Map<ChangePoint, List<String>> table = new HashMap<ChangePoint, List<String>>();

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		return mutantWriter;
	}

	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		parameterList.clear();
		super.visit(p);
	}

	@Override
	public void visit(Parameter p) throws ParseTreeException {
		super.visit(p);

		String type = p.getTypeSpecifier().toString();
		String name = p.getVariable();
		parameterList.put(name, type);
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression left = p.getLeft();
		Expression right = p.getRight();

		ChangePoint cPoint = getChangePoint(p);
		if (cPoint != null) {
			String mutantID = super.getMutationOperatorName() + "_"
					+ super.getTargetFileHashCode() + "_" + cPoint.getID();

			OJClass type = getType(p.getLeft());
			if (type == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());

				super.visit(p);
				return;
			}

			String name = "";// getTargetVariable(left);

			out.print("if(nujava.NuJavaHelper.isGivenID(mutantID, "
					+ MutantID.getChangePointID(mutantID) + ")) {");
			out.print("if(!MutantMonitor.getInstance().isExpWeakMode()) {");
			out.print("this.");
			out.print(name);
			out.print(p.operatorString());

			int tempFlag = phase;
			phase = ORIGINAL;
			super.visit(right);
			phase = tempFlag;
			out.print("; ");
			out.print("} else {");
			out.print("x ");
			out.print(p.operatorString());
			out.print("nujava.classical.JTIMetaMutant.JTIGenDef(");
			out.print(name);
			out.print(", ");
			out.print("this.");
			out.print(name);
			out.print(", ");
			tempFlag = phase;
			phase = ORIGINAL;
			super.visit(right);
			phase = tempFlag;
			out.print(", ");
			out.print("\"" + mutantID + "\"");
			out.print(");");
			out.print("}");
			out.print("} else {");
			out.print(name);
			out.print(p.operatorString());
			tempFlag = phase;
			phase = IMPLEMENTED;
			super.visit(right);
			phase = tempFlag;
			out.print("}");

			String parentMethod = (currentMethod != null) ? currentMethod
					.getName() : "";
			MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID + "_1",
					getMutationOperatorName(), env.currentClassName(),
					parentMethod, MutantOperator.GEN_NUJAVA, 1, 0, 1,
					getLineNumber(), removeNewline(name + "-> " + "this."
							+ name));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			increaseSizeOfChangedPoints();
			return;
		}

	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		// TODO Auto-generated method stub
		super.visit(p);
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		// TODO Auto-generated method stub
		super.visit(p);
	}

	// public void setVariableTable(Map<ChangePoint, List<String>>
	// variableTable) {
	// assert (variableTable != null);
	// this.variableTable.clear();
	// this.variableTable.putAll(variableTable);
	//
	// // # of mutants which could be generated from a change point
	// // for (Iterator<ChangePoint> iter = variableTable.keySet().iterator();
	// // iter
	// // .hasNext();) {
	// // ChangePoint point = iter.next();
	// // sizesOfIteration.put(point, variableTable.get(point).size());
	// // }
	// }
}

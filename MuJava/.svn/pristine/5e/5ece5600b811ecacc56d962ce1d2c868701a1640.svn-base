package mujava.gen.writer.nujava.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JTD;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;

public class JTDNuJavaWriter extends ClassicalNuJavaWriter {

	protected JTDNuJavaWriter(Environment env) throws IOException {
		super(env, "JTD");

	}

	// private Map<ChangePoint, List<String>> variableTable = new
	// HashMap<ChangePoint, List<String>>();

	public static MutantWriter getMutantCodeWriter(JTD mutator)
			throws IOException {
		JTDNuJavaWriter mutantWriter = new JTDNuJavaWriter(mutator
				.getFileEnvironment());
		// Map<ChangePoint, List<String>> table = new HashMap<ChangePoint,
		// List<String>>();

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		return mutantWriter;
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression left = p.getLeft();
		Expression right = p.getRight();

		ChangePoint cPoint = getChangePoint(p);
		if (cPoint != null) {
			MutantID mutantID = MutantID.generateMutantID(super
					.getMutationOperatorName(), this.getTargetFileHashCode(),
					cPoint.getID());

			OJClass type = getType(p.getLeft());
			if (type == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());

				super.visit(p);
				return;
			}

			int index = startOutputCopy();
			FieldAccess fa = (FieldAccess) left;
			super.visit(left);

			out.print(" = ");
			out.print("(nujava.NuJavaHelper.isGivenID(\"" + mutantID.toString()
					+ "\", " + cPoint.getID() + ") ? ");
			out.print("nujava.classical.JTDMetaMutant.JTDGen(");
			out.print(fa.toString());
			out.print(", ");

			phase = ORIGINAL;
			super.visit(right);
			out.print(", ");

			out.print(fa.getName());
			out.print("= (!nujava.MutantMonitor.getInstance().isExpWeak() ? ");
			// int rIndex = startOutputCopy();
			super.visit(right);
			// String rhs = getCopiedString(rIndex);
			out.print(" : ");
			out.print(fa.getName());
			out.print(" ), ");
			// out.print("(!nujava.MutantMonitor.getInstance().isExpWeak() ? ");
			// out.print(fa.toString());
			// out.print(" : ");
			// out.print(rhs);
			// out.print(")");
			out.print("\"" + mutantID.toString() + "\"");
			out.print(", true) : ");
			phase = IMPLEMENTED;
			super.visit(right);
			out.print(")");

			String logStr = fa.toString() + " => " + getCopiedString(index);

			String parentMethod = (currentMethod != null) ? currentMethod
					.getName() : "";
			mutantID.setLastIndex(0);
			MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
					getMutationOperatorName(), env.currentClassName(),
					parentMethod, MutantOperator.GEN_NUJAVA, 1, 0, 0,
					getLineNumber(), removeNewline(logStr));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			increaseSizeOfChangedPoints();
			return;
		}

		super.visit(p);
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		Expression exp = p.getReferenceExpr();
		ChangePoint cPoint = getChangePoint(p);
		if (exp != null && cPoint != null) {
			MutantID mutantID = MutantID.generateMutantID(super
					.getMutationOperatorName(), this.getTargetFileHashCode(),
					cPoint.getID());

			OJClass type = getType(p);
			if (type == null) {
				MuJavaLogger.getLogger().error(
						"Unhandled type object : " + p.toFlattenString());

				super.visit(p);
				return;
			}

			int index = startOutputCopy();
			out.print("(nujava.NuJavaHelper.isGivenID(\"" + mutantID.toString()
					+ "\", " + cPoint.getID() + ") ? ");
			out.print("nujava.classical.JTDMetaMutant.JTDGen(");
			out.print(p.toString());
			out.print(", ");
			out.print(p.toString());
			out.print(", ");
			out.print(p.getName());
			out.print(", ");
			out.print("\"" + mutantID + "\"");
			out.print(", false) : ");
			out.print(p.toString());
			out.print(")");
			String logStr = p.toString() + " => " + getCopiedString(index);

			String parentMethod = (currentMethod != null) ? currentMethod
					.getName() : "";
			mutantID.setLastIndex(0);
			MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
					getMutationOperatorName(), env.currentClassName(),
					parentMethod, MutantOperator.GEN_NUJAVA, 1, 0, 0,
					getLineNumber(), removeNewline(logStr));

			// registers its mutation information
			super.mujavaMutantInfos.add(m);
			increaseSizeOfChangedPoints();
			return;
		}

		super.visit(p);
	}

}

package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.BOIChangePointSeeker;
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
 * @author swkim
 * @version 1.1
 */

public class BOINuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(BOIChangePointSeeker mutator)
			throws IOException {
		BOINuJavaWriter mutantWriter = new BOINuJavaWriter(mutator
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

	public BOINuJavaWriter(Environment env) throws IOException {
		super(env, "BOI");
	}

	private boolean writeCode(Expression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(
						getMutationOperatorName(), getTargetFileHashCode(),
						cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					return true;
				}

				String log_str = p.toString() + " => BOI()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.BOIDMetaMutant.BOIDGen(");

				phase = ORIGINAL;
				super.visit(p);
				
				out.print(", " + UnaryExpression.BIT_NOT);
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");
				
				phase = ORIGINAL;
				super.visit(p);
				
				out.print(" )");
				phase = IMPLEMENTED;

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.BIT_NOT);
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1, -1,
						UnaryExpression.BIT_NOT, getLineNumber(),
						removeNewline(log_str));

				// registers its mutation information
				if (!mujavaMutantInfos.contains(m)) {
					super.mujavaMutantInfos.add(m);
					increaseSizeOfChangedPoints();
				}

				return false;
			}
		}
		return true;
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}

	public void visit(MethodCall mc) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(mc);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(
						getMutationOperatorName(), getTargetFileHashCode(),
						cPoint.getID());

				OJClass retType = getType(mc);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + mc.toFlattenString());
					super.visit(mc);
				}

				String log_str = mc.toString() + " => BOI()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint[");
				out.print(cPoint.getID());
				out.print("] ? nujava.traditional.BOIDMetaMutant.BOIDGen(");

				phase = ORIGINAL;
				writeMethodHeader(mc);
				phase = IMPLEMENTED;
				writeArguments(mc.getArguments());
				
				out.print(", " + UnaryExpression.BIT_NOT );
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");
				
				phase = ORIGINAL;
				writeMethodHeader(mc);
				phase = IMPLEMENTED;
				writeArguments(mc.getArguments());
				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.BIT_NOT);
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1, -1,
						UnaryExpression.BIT_NOT, getLineNumber(),
						removeNewline(log_str));

				// registers its mutation information
				if (!mujavaMutantInfos.contains(m)) {
					super.mujavaMutantInfos.add(m);
					increaseSizeOfChangedPoints();
				}
				return;
			}
		}

		super.visit(mc);
	}

	public void visit(Variable p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}
}
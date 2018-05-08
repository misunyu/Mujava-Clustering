package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.BODChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

/**
 * @author swkim
 * @version 1.1
 */

public class BODNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(BODChangePointSeeker mutator)
			throws IOException {
		BODNuJavaWriter mutantWriter = new BODNuJavaWriter(mutator
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

	public BODNuJavaWriter(Environment env) throws IOException {
		super(env, "BOD");
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
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
					return;
				}

				String log_str = p.toString() + " => BOD()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.BOIDMetaMutant.BOIDGen(");

				phase = ORIGINAL;
				super.visit(p);
				out.print(", " + UnaryExpression.BIT_NOT);
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");

				super.visit(p);

				phase = IMPLEMENTED;

				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.BIT_NOT);
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1,
						UnaryExpression.BIT_NOT, UnaryExpression.BIT_NOT,
						getLineNumber(), removeNewline(log_str));

				// registers its mutation information only if it is new mutant
				if (!mujavaMutantInfos.contains(m)) {
					super.mujavaMutantInfos.add(m);
					increaseSizeOfChangedPoints();
				}

				return;
			}
		}

		super.visit(p);
	}
}
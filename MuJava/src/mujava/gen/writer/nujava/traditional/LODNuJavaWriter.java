package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.LODChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

/**
 * @author swkim
 * 
 *         IsGiven method -> __mujava__ExecuteChangePoint array
 * @version 1.2
 */

public class LODNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(LODChangePointSeeker mutator)
			throws IOException {
		LODNuJavaWriter mutantWriter = new LODNuJavaWriter(mutator
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

	public LODNuJavaWriter(Environment env) throws IOException {
		super(env, "LOD");
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

				String log_str = p.toString() + " => LOD()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.LOIDMetaMutant.LOIDGen(");
				phase = ORIGINAL;
				super.visit(p);
				out.print(", " + UnaryExpression.NOT );
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");

				super.visit(p);

				phase = IMPLEMENTED;

				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.NOT);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1,
						UnaryExpression.NOT, UnaryExpression.NOT,
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
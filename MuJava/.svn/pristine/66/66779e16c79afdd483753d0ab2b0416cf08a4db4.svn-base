package mujava.gen.writer.reachability.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AODUChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * 
 * @edited by swkim
 * @version 1.1
 */

public class AODU_ReachableMutantWriter extends
		TraditionalReachableMutantWriter {

	public static MutantWriter getMutantCodeWriter(AODUChangePointSeeker mutator)
			throws IOException {
		AODU_ReachableMutantWriter mutantWriter = new AODU_ReachableMutantWriter(
				mutator.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	public AODU_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "AODU");

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

				String log_str = p.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();
				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");
				phase = ORIGINAL;
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.PLUS);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_EXP_REACH, 1,
						UnaryExpression.PLUS, UnaryExpression.PLUS,
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

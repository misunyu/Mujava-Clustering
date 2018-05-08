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
import mujava.gen.seeker.traditional.AODSChangePointSeeker;
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
 * 
 * @edited by swkim
 * @version 1.1
 */

public class AODS_ReachableMutantWriter extends
		TraditionalReachableMutantWriter {

	public static MutantWriter getMutantCodeWriter(AODSChangePointSeeker mutator)
			throws IOException {

		AODS_ReachableMutantWriter mutantWriter = new AODS_ReachableMutantWriter(
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

	public AODS_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "AODS");
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					StringBuffer sb = new StringBuffer();
					sb.append("Unhandled type object : ");
					sb.append(p.toFlattenString());
					MuJavaLogger.getLogger().error(sb.toString());
					sb.setLength(0);
					return;
				}

				int op = p.getOperator();

				String log_str = p.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();
				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");
				phase = ORIGINAL;
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");

				mutantID.setLastIndex(p.getOperator());
				String parentClass = env.currentClassName();
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";

				/**
				 * Create MuJavaMutantInfo object to support mutant information
				 */
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), parentClass, parentMethod,
						MutantOperator.GEN_EXP_REACH, 1, op, op,
						getLineNumber(), log_str);

				// registers its mutation information
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

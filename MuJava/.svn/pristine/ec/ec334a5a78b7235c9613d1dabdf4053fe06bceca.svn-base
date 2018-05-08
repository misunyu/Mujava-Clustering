package mujava.gen.writer.reachability.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AOISChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

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

public class AOIS_ReachableMutantWriter extends
		TraditionalReachableMutantWriter {

	public static MutantWriter getMutantCodeWriter(AOISChangePointSeeker mutator)
			throws IOException {
		AOIS_ReachableMutantWriter mutantWriter = new AOIS_ReachableMutantWriter(
				mutator.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		// mutantWriter.setSizeOfIteration(mutator.getIterationTable());
		return mutantWriter;
	}

	public AOIS_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "AOIS");
	}

	private int getAOISOperator(int operator, ArrayList<Integer> ops) {
		if (operator != UnaryExpression.PRE_INCREMENT
				&& !ops.contains(UnaryExpression.PRE_INCREMENT)) {
			ops.add(UnaryExpression.PRE_INCREMENT);
			return UnaryExpression.PRE_INCREMENT;
		}
		if (operator != UnaryExpression.PRE_DECREMENT
				&& !ops.contains(UnaryExpression.PRE_DECREMENT)) {
			ops.add(UnaryExpression.PRE_DECREMENT);
			return UnaryExpression.PRE_DECREMENT;
		}
		if (operator != UnaryExpression.POST_INCREMENT
				&& !ops.contains(UnaryExpression.POST_INCREMENT)) {
			ops.add(UnaryExpression.POST_INCREMENT);
			return UnaryExpression.POST_INCREMENT;
		}
		if (operator != UnaryExpression.POST_DECREMENT
				&& !ops.contains(UnaryExpression.POST_DECREMENT)) {
			ops.add(UnaryExpression.POST_DECREMENT);
			return UnaryExpression.POST_DECREMENT;
		}

		return 0;
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

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

				ArrayList<Integer> ops = new ArrayList<Integer>();
				for (int i = 0; i < 4; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAOISOperator(-1, ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_EXP_REACH, 4, -1, newOp,
							getLineNumber(), removeNewline(log_str));

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}

				increaseSizeOfChangedPoints();
				return;
			}
		}
		super.visit(p);
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

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

				ArrayList<Integer> ops = new ArrayList<Integer>();
				for (int i = 0; i < 4; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAOISOperator(-1, ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_EXP_REACH, 4, -1, newOp,
							getLineNumber(), removeNewline(log_str));

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}

				increaseSizeOfChangedPoints();
				return;
			}
		}
		super.visit(p);
	}
}

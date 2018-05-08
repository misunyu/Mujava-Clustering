package mujava.gen.writer.reachability.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AORSChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class AORS_ReachableMutantWriter extends
		TraditionalReachableMutantWriter {
	public static TraditionalReachableMutantWriter getMutantCodeWriter(
			AORSChangePointSeeker mutator) throws IOException {
		AORS_ReachableMutantWriter mutantWriter = new AORS_ReachableMutantWriter(
				mutator.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		mutantWriter.setSizeOfIteration(mutator.getIterationTable());

		return mutantWriter;
	}

	Map<ChangePoint, Integer> sizesOfIteration = new HashMap<ChangePoint, Integer>();

	private void setSizeOfIteration(Map<ChangePoint, Integer> iterationTable) {
		sizesOfIteration.clear();
		sizesOfIteration.putAll(iterationTable);
	}

	public AORS_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "AORS");
	}

	@Override
	public void visit(UnaryExpression p) throws ParseTreeException {
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

				int size = 3;
				Integer value = sizesOfIteration.get(cPoint);
				if (value != null) {
					size = value;
				}

				boolean isStatement = false;
				if (size != 3) {
					isStatement = true;
				}

				int op = p.getOperator();
				String log_str = p.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();

				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");
				phase = ORIGINAL;
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAORSOperator(op, isStatement, ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_EXP_REACH, size, op, newOp,
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

	private int getAORSOperator(int operator, boolean isStatement,
			ArrayList<Integer> ops) {
		if (isStatement) {
			// a-- || --a ==> only A++
			if (operator == UnaryExpression.POST_DECREMENT
					|| operator == UnaryExpression.PRE_DECREMENT) {

				if (!ops.contains(UnaryExpression.POST_INCREMENT)) {
					ops.add(UnaryExpression.POST_INCREMENT);
					return UnaryExpression.POST_INCREMENT;
				}
			}
			// a++ || ++a ==> only A--;
			if (operator == UnaryExpression.POST_INCREMENT
					|| operator == UnaryExpression.PRE_INCREMENT) {

				if (!ops.contains(UnaryExpression.POST_DECREMENT)) {
					ops.add(UnaryExpression.POST_DECREMENT);
					return UnaryExpression.POST_DECREMENT;
				}
			}
		} else {
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
		}

		return 0;
	}
}

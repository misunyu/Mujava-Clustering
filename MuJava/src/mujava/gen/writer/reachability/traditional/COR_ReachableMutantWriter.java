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
import mujava.gen.seeker.traditional.CORChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.BinaryExpression;
import openjava.ptree.ParseTreeException;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author swkim
 * @version 1.1
 */

public class COR_ReachableMutantWriter extends TraditionalReachableMutantWriter {
	public static TraditionalReachableMutantWriter getMutantCodeWriter(
			CORChangePointSeeker mutator) throws IOException {
		COR_ReachableMutantWriter mutantWriter = new COR_ReachableMutantWriter(
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

	public COR_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "COR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
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

					super.visit(p);
					return;
				}

				int operator = p.getOperator();

				String log_str = p.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();
				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");
				writeParenthesis(p.getLeft());
				out.print(" " + p.operatorString() + " ");
				writeParenthesis(p.getRight());
				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				int size = 2;
				boolean isValid = false;
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getCOROperator(operator, ops);
					if(newOp==-1) continue;
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_EXP_REACH, size, operator,
							newOp, getLineNumber(), removeNewline(log_str));

					// registers its mutation information
					if (!mujavaMutantInfos.contains(m)) {
						isValid = true;
						super.mujavaMutantInfos.add(m);
					}
				}

				if (isValid)
					increaseSizeOfChangedPoints();
				return;
			}
		}
		super.visit(p);
	}

	private int getCOROperator(int operator, ArrayList<Integer> ops) {
		if (operator != BinaryExpression.LOGICAL_AND
				&& !ops.contains(BinaryExpression.LOGICAL_AND)) {
			ops.add(BinaryExpression.LOGICAL_AND);
			return BinaryExpression.LOGICAL_AND;
		}
		if (operator != BinaryExpression.LOGICAL_OR
				&& !ops.contains(BinaryExpression.LOGICAL_OR)) {
			ops.add(BinaryExpression.LOGICAL_OR);
			return BinaryExpression.LOGICAL_OR;
		}

		return -1;
	}
}

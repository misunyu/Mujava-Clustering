package mujava.gen.writer.MSG.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import MSG.MSGConstraints;
import MSG.MutantMonitor;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.CORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.traditional.COR;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;

/**
 * 
 * @author swkim
 * @version 1.1
 */

public class COR_MSGWriter extends TraditionalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(CORChangePointSeeker mutator)
			throws IOException {
		COR_MSGWriter mutantWriter = new COR_MSGWriter(mutator
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

	public COR_MSGWriter(Environment env) throws IOException {
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

				phase = ORIGINAL;
				out.print("( MSG.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID() + "] ? (");
				super.visit(p.getLeft());
				out
						.print("? ( MSG.MutantMonitor.subID == MSG.MSGConstraints.B_LOGICAL_OR "
								+ "? true : MSG.traditional.CORMetaMutant.COR(true, ");
				super.visit(p.getRight());
				out.print(", MSG.MutantMonitor.subID, ");
				out.print("\"" + mutantID.toString() + "\"))");
				out
						.print(": ( MSG.MutantMonitor.subID == MSG.MSGConstraints.B_LOGICAL_AND "
								+ "? false : MSG.traditional.CORMetaMutant.COR(false, ");
				super.visit(p.getRight());
				out.print(", MSG.MutantMonitor.subID, ");
				out.print("\"" + mutantID.toString() + "\"))) ");

				out.print(" : (");

				phase = IMPLEMENTED;
				Expression lexpr = p.getLeft();
				writeParenthesis(lexpr);
				out.print(" " + p.operatorString() + " ");
				Expression rexpr = p.getRight();
				writeParenthesis(rexpr);
				out.print(" ))");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				String parentClass = env.currentClassName();
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				String logStr = removeNewline(p.toString() + " => COR()");
				int size = 4;
				boolean isValid = false;
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = COR.getNonRedundantCOROp(operator, ops);
					if(newOp==-1) continue;
					mutantID.setLastIndex(newOp);

					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(),
							parentClass, parentMethod, MutantOperator.GEN_MSG,
							size, operator, newOp, getLineNumber(),
							removeNewline(logStr));

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

package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.BORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
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

public class BORNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(BORChangePointSeeker mutator)
			throws IOException {
		BORNuJavaWriter mutantWriter = new BORNuJavaWriter(mutator
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

	public BORNuJavaWriter(Environment env) throws IOException {
		super(env, "BOR");
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

				String log_str = p.toString() + " => BOR()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.BORMetaMutant.BORGen(");

				super.visit(p.getLeft());
				out.print(", ");
				super.visit(p.getRight());
				out.print(", " + operator );
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");

				out.print(" : ");

				Expression lexpr = p.getLeft();
				if (isOperatorNeededLeftPar(p.getOperator(), lexpr)) {
					writeParenthesis(lexpr);
				} else {
					lexpr.accept(this);
				}
				out.print(" " + p.operatorString() + " ");
				Expression rexpr = p.getRight();
				if (isOperatorNeededLeftPar(p.getOperator(), rexpr)) {
					writeParenthesis(rexpr);
				} else {
					rexpr.accept(this);
				}
				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				int size = 2;
				boolean isReached = false;
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getBOROperator(operator, ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_NUJAVA, size, operator, newOp,
							getLineNumber(), removeNewline(log_str));

					// registers its mutation information
					if (!mujavaMutantInfos.contains(m)) {
						super.mujavaMutantInfos.add(m);
						isReached = true;
					}
				}

				if (isReached) {
					// Writer에서 생성되었음을 알수 있는 counter
					increaseSizeOfChangedPoints();
				}
				return;
			}
		}
		super.visit(p);
	}

	private int getBOROperator(int operator, ArrayList<Integer> ops) {
		if (operator != BinaryExpression.BITAND
				&& !ops.contains(BinaryExpression.BITAND)) {
			ops.add(BinaryExpression.BITAND);
			return BinaryExpression.BITAND;
		}
		if (operator != BinaryExpression.BITOR
				&& !ops.contains(BinaryExpression.BITOR)) {
			ops.add(BinaryExpression.BITOR);
			return BinaryExpression.BITOR;
		}
		if (operator != BinaryExpression.XOR
				&& !ops.contains(BinaryExpression.XOR)) {
			ops.add(BinaryExpression.XOR);
			return BinaryExpression.XOR;
		}

		return 0;
	}
}

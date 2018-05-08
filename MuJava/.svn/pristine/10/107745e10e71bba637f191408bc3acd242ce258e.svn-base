package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AORBChangePointSeeker;
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

public class AORBNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(AORBChangePointSeeker mutator)
			throws IOException {
		AORBNuJavaWriter mutantWriter = new AORBNuJavaWriter(mutator
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

	public AORBNuJavaWriter(Environment env) throws IOException {
		super(env, "AORB");
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

				String log_str = p.toString() + " => AORB()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint[");
				out.print(cPoint.getID());
				out.print("] ? nujava.traditional.AORBMetaMutant.AORBGen(");

				super.visit(p.getLeft());
				out.print(", ");
				super.visit(p.getRight());

				out.print(", " + operator );
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");

				out.print(" : ");

				Expression lexpr = p.getLeft();
				writeParenthesis(lexpr);
				out.print(" " + p.operatorString() + " ");
				Expression rexpr = p.getRight();
				writeParenthesis(rexpr);

				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				int size = 4;
				boolean isReached = false;
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAORBOperator(operator, ops);
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

	private int getAORBOperator(int operator, ArrayList<Integer> ops) {
		if (operator != BinaryExpression.PLUS
				&& !ops.contains(BinaryExpression.PLUS)) {
			ops.add(BinaryExpression.PLUS);
			return BinaryExpression.PLUS;
		}
		if (operator != BinaryExpression.MINUS
				&& !ops.contains(BinaryExpression.MINUS)) {
			ops.add(BinaryExpression.MINUS);
			return BinaryExpression.MINUS;
		}
		if (operator != BinaryExpression.TIMES
				&& !ops.contains(BinaryExpression.TIMES)) {
			ops.add(BinaryExpression.TIMES);
			return BinaryExpression.TIMES;
		}
		if (operator != BinaryExpression.DIVIDE
				&& !ops.contains(BinaryExpression.DIVIDE)) {
			ops.add(BinaryExpression.DIVIDE);
			return BinaryExpression.DIVIDE;
		}
		if (operator != BinaryExpression.MOD
				&& !ops.contains(BinaryExpression.MOD)) {
			ops.add(BinaryExpression.MOD);
			return BinaryExpression.MOD;
		}

		return 0;
	}
}

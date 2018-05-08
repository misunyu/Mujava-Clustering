package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class CORNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(CORChangePointSeeker mutator)
			throws IOException {
		CORNuJavaWriter mutantWriter = new CORNuJavaWriter(
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

	public CORNuJavaWriter(Environment env) throws IOException {
		super(env, "COR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(
						super.getMutationOperatorName(),
						this.getTargetFileHashCode(), cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				int operator = p.getOperator();

				String log_str = p.toString() + " => COR()";
				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID() + "] ");

				out.print("? ( nujava.traditional.CORMetaMutant.preCOR(");
				super.visit(p.getLeft());
				out.print(", " + operator);
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print("? ( nujava.traditional.CORMetaMutant.isHalfEvaluation(true, "
						+ operator
						+ ", "
						+ cPoint.getID()
						+ ") "
						+ "? true : nujava.traditional.CORMetaMutant.CORGen(true, ");
				super.visit(p.getRight());
				out.print(", " + operator);
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\"))");
				out.print(": ( nujava.traditional.CORMetaMutant.isHalfEvaluation(false, "
						+ operator
						+ ", "
						+ cPoint.getID()
						+ ") "
						+ "? false : nujava.traditional.CORMetaMutant.CORGen(false, ");
				super.visit(p.getRight());
				out.print(", " + operator);
				out.print(", " + cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\"))) ");

				out.print(": ");
				Expression lexpr = p.getLeft();
				writeParenthesis(lexpr);
				out.print(" " + p.operatorString() + " ");
				Expression rexpr = p.getRight();
				writeParenthesis(rexpr);
				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				int size = 4;
				boolean isValid = false;
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = COR.getNonRedundantCOROp(operator, ops);
					if(newOp==-1) continue;
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName() : "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(
							mutantID.toString(), getMutationOperatorName(),
							env.currentClassName(), parentMethod,
							MutantOperator.GEN_NUJAVA, size, operator, newOp,
							getLineNumber(), removeNewline(log_str));

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

}

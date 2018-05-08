package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author swkim
 * @version 1.1
 */

public class AOISNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(AOISChangePointSeeker mutator)
			throws IOException {
		AOISNuJavaWriter mutantWriter = new AOISNuJavaWriter(mutator
				.getFileEnvironment());
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

	public AOISNuJavaWriter(Environment env) throws IOException {
		super(env, "AOIS");
	}

	private int getAOISOperator(List<Integer> ops) {
		if (!ops.contains(UnaryExpression.POST_INCREMENT)) {
			ops.add(UnaryExpression.POST_INCREMENT);
			return UnaryExpression.POST_INCREMENT;
		}
		if (!ops.contains(UnaryExpression.POST_DECREMENT)) {
			ops.add(UnaryExpression.POST_DECREMENT);
			return UnaryExpression.POST_DECREMENT;
		}
		if (!ops.contains(UnaryExpression.PRE_INCREMENT)) {
			ops.add(UnaryExpression.PRE_INCREMENT);
			return UnaryExpression.PRE_INCREMENT;
		}
		if (!ops.contains(UnaryExpression.PRE_DECREMENT)) {
			ops.add(UnaryExpression.PRE_DECREMENT);
			return UnaryExpression.PRE_DECREMENT;
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

				int index = startOutputCopy();
				out.print("(");
				out.print("nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.AOISMetaMutant.AOISGen(");
				phase = ORIGINAL;
				super.visit(p);
				out.print("=nujava.traditional.AOISMetaMutant.AOIS(");
				super.visit(p);
				out.print("), ");
				out.print(cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");
				String log_str = p.toString() + " => " + getCopiedString(index);

				int size = 4;
				Integer value = sizesOfIteration.get(cPoint);
				if (value != null) {
					size = value;
				}

				boolean isStatement = false;
				if (size != 4) {
					isStatement = true;
				}

				ArrayList<Integer> ops = new ArrayList<Integer>();
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAOISOperator(ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_NUJAVA, size, -1, newOp,
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

				String log_str = p.toString() + " => AOIS()";

				out.print("(");
				out.print("nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.AOISMetaMutant.AOISGen(");
				phase = ORIGINAL;
				super.visit(p);
				out.print("=nujava.traditional.AOISMetaMutant.AOIS(");
				super.visit(p);
				out.print("), ");
				out.print(cPoint.getID() + ",");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : ");
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");

				int size = 4;
				Integer value = sizesOfIteration.get(cPoint);
				if (value != null) {
					size = value;
				}

				boolean isStatement = false;
				if (size != 4) {
					isStatement = true;
				}

				ArrayList<Integer> ops = new ArrayList<Integer>();
				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getAOISOperator(ops);
					mutantID.setLastIndex(newOp);

					String parentMethod = (currentMethod != null) ? currentMethod
							.getName()
							: "";
					MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID
							.toString(), getMutationOperatorName(), env
							.currentClassName(), parentMethod,
							MutantOperator.GEN_NUJAVA, size, -1, newOp,
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
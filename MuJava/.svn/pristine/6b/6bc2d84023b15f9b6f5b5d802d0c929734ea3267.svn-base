package mujava.gen.writer.MSG.traditional;

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
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AOISChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

/**
 * @author swkim
 * @version 1.1
 */

public class AOIS_MSGWriter extends TraditionalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(AOISChangePointSeeker mutator)
			throws IOException {
		AOIS_MSGWriter mutantWriter = new AOIS_MSGWriter(mutator
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

	public AOIS_MSGWriter(Environment env) throws IOException {
		super(env, "AOIS");
	}

	private boolean writeCode(Expression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				String mutantID = super.getMutationOperatorName() + "_"
						+ this.getTargetFileHashCode() + "_" + cPoint.getID();

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					return true;
				}

				int index = startOutputCopy();
				out.print("( MSG.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? MSG.traditional.AOISMetaMutant.AOIS(");

				phase = ORIGINAL;
				super.visit(p);
				out.print("=MSG.traditional.AOISMetaMutant.AOISValue(");
				super.visit(p);
				out.print(")");
				out.print(")");
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
					MuJavaMutantInfo m = new MuJavaMutantInfo();
					m.setChangeLocation(getLineNumber());
					m.setSizeOfSubID(size);
					int newOp = getAOISOperator(ops);
					m.setSubTypeOperator(newOp);
					m.setGenerationWay(MutantOperator.GEN_MSG);
					m.setMutationOperatorName(this.getMutationOperatorName());
					m.setChangeLog(removeNewline(log_str));
					m.setMutantID(mutantID + "_" + newOp);

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}
				increaseSizeOfChangedPoints();
				return false;
			}
		}
		return true;
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

	public void visit(FieldAccess p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}

	public void visit(Variable p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}
}
package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.List;

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
 * @author swkim
 * @version 1.1
 */

public class AODSNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(AODSChangePointSeeker mutator)
			throws IOException {

		AODSNuJavaWriter mutantWriter = new AODSNuJavaWriter(mutator
				.getFileEnvironment());
		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);
		list = mutator.getEmptyPoints();
		mutantWriter.setEmptyPoint(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);

		return mutantWriter;
	}

	private List<ChangePoint> emptyPoints = null;

	private void setEmptyPoint(List<ChangePoint> list) {
		emptyPoints = list;
	}

	public AODSNuJavaWriter(Environment env) throws IOException {
		super(env, "AODS");
	}

	@Override
	public void visit(UnaryExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				StringBuffer sb = new StringBuffer();

				OJClass retType = getType(p);
				if (retType == null) {
					sb.append("Unhandled type object : ");
					sb.append(p.toFlattenString());
					MuJavaLogger.getLogger().error(sb.toString());
					sb.setLength(0);
					return;
				}

				int op = p.getOperator();

				sb.setLength(0);
				sb.append(p.toString());
				sb.append(" => AODS()");
				String log_str = removeNewline(sb.toString());

				boolean isStatement = false;
				for (ChangePoint point : emptyPoints) {
					if (point.isSamePoint(p, currentMethod)) {
						isStatement = true;
						break;
					}
				}

				String IDstr = mutantID.toString();
				if (isStatement) {
					/**
					 * statement이므로 if 문장으로 변경한다.
					 */
					out.print("if (nujava.MutantMonitor.ExecuteChangePoint["
							+ cPoint.getID() + "]) { ");
					out.print(" nujava.traditional.AODSMetaMutant.AODSGen(");
					phase = ORIGINAL;
					super.visit(p.getExpression());
					out.print("= nujava.traditional.AODSMetaMutant.AODS(");
					super.visit(p.getExpression());
					out.print(", " + op + ") ");
					out.print(", " + op);
					out.print(", " + cPoint.getID() + ",");
					out.print("\"" + IDstr + "\")");
					out.print("; } else ");
					super.visit(p);
					phase = IMPLEMENTED;
				} else {
					phase = ORIGINAL;
					out.print("(");
					out.print("nujava.MutantMonitor.ExecuteChangePoint[");
					out.print(cPoint.getID());
					out.print("] ? ");
					out.print(" nujava.traditional.AODSMetaMutant.AODSGen(");
					super.visit(p.getExpression());
					out.print("= nujava.traditional.AODSMetaMutant.AODS(");
					super.visit(p.getExpression());
					out.print(", ");
					out.print(op);
					out.print(") ");
					out.print(", ");
					out.print(op);
					out.print(", " + cPoint.getID());
					out.print(", ");
					out.print("\"");
					out.print(IDstr);
					out.print("\")");
					out.print(" : ");
					super.visit(p);
					phase = IMPLEMENTED;
					out.print(" )");
				}

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(op);
				String parentClass = env.currentClassName();
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), parentClass, parentMethod,
						MutantOperator.GEN_NUJAVA, 1, op, op, getLineNumber(),
						log_str);

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
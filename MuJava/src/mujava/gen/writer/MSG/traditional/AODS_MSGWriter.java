package mujava.gen.writer.MSG.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

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

public class AODS_MSGWriter extends TraditionalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(AODSChangePointSeeker mutator)
			throws IOException {
		AODS_MSGWriter mutantWriter = new AODS_MSGWriter(mutator
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

	public AODS_MSGWriter(Environment env) throws IOException {
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
				sb.setLength(0);

				boolean isStatement = false;
				for (ChangePoint point : emptyPoints) {
					if (point.isSamePoint(p, currentMethod)) {
						isStatement = true;
						break;
					}
				}

				if (!isStatement) {
					out.print("( MSG.MutantMonitor.ExecuteChangePoint["
							+ cPoint.getID() + "] ? ");
					phase = ORIGINAL;
					super.visit(p.getExpression());
					out.print(" : ");
					super.visit(p);
					phase = IMPLEMENTED;
					out.print(" )");
				} else {
					out.print("if ( MSG.MutantMonitor.ExecuteChangePoint["
							+ cPoint.getID() + "] ) {");
					out.print("; } else {");
					phase = ORIGINAL;
					super.visit(p);
					out.print(";}");
					phase = IMPLEMENTED;
				}

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(op);
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_MSG, 1, op, op,
						getLineNumber(), log_str);

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
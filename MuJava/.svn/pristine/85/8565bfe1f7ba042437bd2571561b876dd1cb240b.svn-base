package mujava.gen.writer.reachability.traditional;

import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.omg.PortableInterceptor.INACTIVE;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.AOIUChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * 
 * @edited by swkim
 * @version 1.1
 */

public class AOIU_ReachableMutantWriter extends
		TraditionalReachableMutantWriter {

	public static MutantWriter getMutantCodeWriter(AOIUChangePointSeeker mutator)
			throws IOException {
		AOIU_ReachableMutantWriter mutantWriter = new AOIU_ReachableMutantWriter(
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

	public AOIU_ReachableMutantWriter(Environment env) throws IOException {
		super(env, "AOIU");

	}

	private boolean writeCode(Expression p) throws ParseTreeException {
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

					return true;
				}

				String log_str = p.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();
				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");
				phase = ORIGINAL;
				super.visit(p);
				phase = IMPLEMENTED;
				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.MINUS);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_EXP_REACH, 1,
						UnaryExpression.MINUS, UnaryExpression.MINUS,
						getLineNumber(), removeNewline(log_str));

				// registers its mutation information
				if (!mujavaMutantInfos.contains(m)) {
					super.mujavaMutantInfos.add(m);
					increaseSizeOfChangedPoints();
				}
				return false;
			}
		}
		return true;
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}

	public void visit(MethodCall mc) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(mc);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				OJClass retType = getType(mc);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + mc.toFlattenString());
					super.visit(mc);
				}

				String log_str = mc.toString() + " => ReachMonitor.reach()";
				String IDstr = mutantID.toString();

				out.print(" mujava.ReachMonitor.reach(\"" + IDstr + "\", ");

				phase = ORIGINAL;
				writeMethodHeader(mc);
				phase = IMPLEMENTED;
				writeArguments(mc.getArguments());
				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.MINUS);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_EXP_REACH, 1,
						UnaryExpression.MINUS, UnaryExpression.MINUS,
						getLineNumber(), removeNewline(log_str));

				// registers its mutation information
				if (!mujavaMutantInfos.contains(m)) {
					super.mujavaMutantInfos.add(m);
					increaseSizeOfChangedPoints();
				}
				return;
			}
		}

		super.visit(mc);
	}

	public void visit(Variable p) throws ParseTreeException {
		if (writeCode(p))
			super.visit(p);
	}
}

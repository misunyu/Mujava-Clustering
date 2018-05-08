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
import mujava.gen.seeker.traditional.LODChangePointSeeker;
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

public class LOD_MSGWriter extends TraditionalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(LODChangePointSeeker mutator)
			throws IOException {
		LOD_MSGWriter mutantWriter = new LOD_MSGWriter(mutator
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

	public LOD_MSGWriter(Environment env) throws IOException {
		super(env, "LOD");

	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(
						getMutationOperatorName(), getTargetFileHashCode(),
						cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());
					return;
				}

				String log_str = p.toString() + " => LOD()";
				out.print("( MSG.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID() + "] ? ");
				phase = ORIGINAL;
				super.visit(p.getExpression());
				out.print(" : ");

				super.visit(p);

				phase = IMPLEMENTED;

				out.print(" )");

				// create MuJavaMutantInfo object to support mutant information
				mutantID.setLastIndex(UnaryExpression.NOT);
				MuJavaMutantInfo m = new MuJavaMutantInfo();
				m.setChangeLocation(getLineNumber());
				m.setSizeOfSubID(1);
				m.setSubTypeOperator(UnaryExpression.NOT);
				m.setGenerationWay(MutantOperator.GEN_MSG);
				m.setMutationOperatorName(this.getMutationOperatorName());
				m.setChangeLog(removeNewline(log_str));
				m.setMutantID(mutantID.toString());

				// registers its mutation information only if it is new mutant
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
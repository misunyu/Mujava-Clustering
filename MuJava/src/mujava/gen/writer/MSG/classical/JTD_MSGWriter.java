package mujava.gen.writer.MSG.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JTD;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;

/**
 * 
 * @author swkim
 * @version 1.1
 */

public class JTD_MSGWriter extends ClassicalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(JTD mutator)
			throws IOException {
		JTD_MSGWriter mutantWriter = new JTD_MSGWriter(mutator
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

	public JTD_MSGWriter(Environment env) throws IOException {
		super(env, "JTD");
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this.targetFileHashCode,
						cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				evaluateDown(p);

				Expression left = p.getLeft();
				Expression right = p.getRight();

				FieldAccess fa = (FieldAccess) left;

				String log_str = p.toString()
						+ " => ( if (MSGHelper.isGivenID()) " + fa.getName()
						+ " = right-hand-side else " + fa.toString()
						+ " = right-hand-side" + " : " + p.toString() + ")";

				out.print("if (MSG.MSGHelper.isGivenID(\""
						+ mutantID.toString() + "\", " + cPoint.getID() + ")) "
						+ fa.getName() + " = ");
				phase = ORIGINAL;
				// startOutputCopy();
				super.visit(right);
				// String rhs = getCopiedString();
				out.print("; else " + fa.toString() + " = ");
				super.visit(right);
				phase = IMPLEMENTED;

				// Full-size mutant ID
				mutantID.setLastIndex(0);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";

				// create MuJavaMutantInfo object to support mutant information
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_MSG, 1, 0, 0,
						getLineNumber(), log_str);

				// registers its mutation information
				super.mujavaMutantInfos.add(m);

				// notify to the its writer for creating a new mutant
				increaseSizeOfChangedPoints();
				return;
			}

		}
		super.visit(p);
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this.targetFileHashCode,
						cPoint.getID());

				OJClass retType = getType(p);
				if (retType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				String log_str = p.toString()
						+ " => ( MSG.MSGHelper.isGivenID() ? " + p.getName()
						+ " : " + p.toString() + ")";

				out.print("( MSG.MSGHelper.isGivenID(\"" + mutantID.toString()
						+ "\"," + cPoint.getID() + ") ? " + p.getName());
				out.print(" : " + p.toString());
				phase = IMPLEMENTED;
				out.print(" )");

				// Full-size mutant ID
				mutantID.setLastIndex(0);

				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";

				// create MuJavaMutantInfo object to support mutant information
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_MSG, 1, 0, 0,
						getLineNumber(), log_str);

				// registers its mutation information
				super.mujavaMutantInfos.add(m);

				// notify to the its writer for creating a new mutant
				increaseSizeOfChangedPoints();
				return;
			}
		}

		super.visit(p);
	}
}

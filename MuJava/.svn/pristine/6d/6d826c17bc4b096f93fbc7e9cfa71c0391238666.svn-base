package mujava.gen.writer.nujava.classical;

import java.io.IOException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.EOA;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;

public class EOANuJavaWriter extends ClassicalNuJavaWriter {

	protected EOANuJavaWriter(Environment env) throws IOException {
		super(env, "EOA");

	}

	public static MutantWriter getMutantCodeWriter(EOA mutator)
			throws IOException {
		EOANuJavaWriter mutantWriter = new EOANuJavaWriter(mutator
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

	public void visit(AssignmentExpression p) throws ParseTreeException {
		assert (super.currentChangePoint != null);

		if (phase == IMPLEMENTED) {

			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {

				OJClass type = getType(p.getLeft());
				if (type == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());

					super.visit(p);
					return;
				}

				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				// In case of Assignment, some logic is required to be executed
				// in the super.evaluateDown()
				evaluateDown(p);

				// writes mutated code
				Expression lexpr = p.getLeft();
				if (lexpr instanceof AssignmentExpression) {
					writeParenthesis(lexpr);
				} else {
					lexpr.accept(this);
				}

				out.print(" " + p.operatorString() + " "); // "="
				out.print("(" + type.getName()
						+ " )(nujava.NuJavaHelper.isGivenID(\""
						+ mutantID.toString() + "\", " + cPoint.getID()
						+ ") ? nujava.classical.EOAMetaMutant.EOAGen( ");

				phase = ORIGINAL;
				super.visit(p.getRight());
				out.print(", ");
				out.print("\"" + mutantID + "\"");
				out.print(") : ");

				phase = IMPLEMENTED;
				super.visit(p.getRight());
				out.print(")");

				String logStr = p.getRight().toFlattenString() + " => "
						+ p.getRight().toFlattenString() + ".clone()";

				/**
				 * Create MuJavaMutantInfo objects to support mutant information
				 */
				// Full-size mutant ID
				mutantID.setLastIndex(1);
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString(),
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1, 0, 1,
						getLineNumber(), removeNewline(logStr));

				// registers its mutation information
				super.mujavaMutantInfos.add(m);
				increaseSizeOfChangedPoints();

				return;
			}
		}

		super.visit(p);
	}
}

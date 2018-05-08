package mujava.gen.writer.MSG.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

import MSG.MutantMonitor;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.ASRSChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;

/**
 * @author swkim
 * @version 1.1
 */

public class ASRS_MSGWriter extends TraditionalMutantMSGWriter {
	public static MutantWriter getMutantCodeWriter(ASRSChangePointSeeker mutator)
			throws IOException {
		ASRS_MSGWriter mutantWriter = new ASRS_MSGWriter(mutator
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

	public ASRS_MSGWriter(Environment env) throws IOException {
		super(env, "ASRS");
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {
		if (phase == IMPLEMENTED) {
			ChangePoint cPoint = getChangePoint(p);
			if (cPoint != null) {
				MutantID mutantID = MutantID.generateMutantID(super
						.getMutationOperatorName(), this
						.getTargetFileHashCode(), cPoint.getID());

				OJClass retType = getType(p);
				OJClass rightType = getType(p.getRight());
				if (retType == null || rightType == null) {
					MuJavaLogger.getLogger().error(
							"Unhandled type object : " + p.toFlattenString());
					return;
				}
				OJClass paramType = getBiggerClass(retType, rightType);
				String paramTypeStr = (paramType == null) ? retType.toString()
						: paramType.toString();
				int op = p.getOperator();

				phase = ORIGINAL;
				Expression lexpr = p.getLeft();
				if (lexpr instanceof AssignmentExpression) {
					writeParenthesis(lexpr);
				} else {
					lexpr.accept(this);
				}
				out.print("=");
				out.print("(" + retType.toString() + ")");
				out.print("( MSG.MutantMonitor.ExecuteChangePoint[" + cPoint.getID()
						+ "] ? ");
				out.print("(" + retType.toString()
						+ ")MSG.traditional.ASRSMetaMutant.ASRS(");
				out.print("(" + paramTypeStr + ")(");
				super.visit(p.getLeft());
				out.print("), (" + paramTypeStr + ")(");
				super.visit(p.getRight());
				out.print("), MSG.MutantMonitor.subID, ");
				out.print("\"" + mutantID.toString() + "\")");
				out.print(" : (" + retType.toString() + ")(");
				super.visit(p.getLeft());
				out.print(getModifiedOperatorString(op));
				phase = IMPLEMENTED;
				out.print("(");
				super.visit(p.getRight());
				out.print(")");
				out.print(") )");

				ArrayList<Integer> ops = new ArrayList<Integer>();

				int size = 2;
				if (op == AssignmentExpression.ADD
						|| op == AssignmentExpression.SUB
						|| op == AssignmentExpression.MULT
						|| op == AssignmentExpression.DIVIDE
						|| op == AssignmentExpression.MOD)
					size = 4;

				for (int i = 0; i < size; i++) {
					// create MuJavaMutantInfo object to support mutant
					// information
					int newOp = getASRSOperator(op, ops);
					mutantID.setLastIndex(newOp);

					MuJavaMutantInfo m = new MuJavaMutantInfo();
					m.setChangeLocation(getLineNumber());
					m.setSizeOfSubID(size);
					m.setSubTypeOperator(newOp);
					m.setGenerationWay(MutantOperator.GEN_MSG);
					m.setMutationOperatorName(this.getMutationOperatorName());
					m.setChangeLog(removeNewline(p.toString() + " => ASRS()"));
					m.setMutantID(mutantID.toString());

					// registers its mutation information
					super.mujavaMutantInfos.add(m);
				}

				increaseSizeOfChangedPoints();
				return;
			}
		}
		super.visit(p);
	}

	private String getModifiedOperatorString(int op) {
		if (op == AssignmentExpression.ADD)
			return "+";
		if (op == AssignmentExpression.SUB)
			return "-";
		if (op == AssignmentExpression.MULT)
			return "*";
		if (op == AssignmentExpression.DIVIDE)
			return "/";
		if (op == AssignmentExpression.MOD)
			return "%";
		if (op == AssignmentExpression.AND)
			return "&";
		if (op == AssignmentExpression.OR)
			return "|";
		if (op == AssignmentExpression.XOR)
			return "^";
		if (op == AssignmentExpression.SHIFT_L)
			return "<<";
		if (op == AssignmentExpression.SHIFT_R)
			return ">>";
		if (op == AssignmentExpression.SHIFT_RR)
			return ">>>";
		assert (true);
		return null;
	}

	private int getASRSOperator(int operator, ArrayList<Integer> ops) {
		if (operator == AssignmentExpression.ADD
				|| operator == AssignmentExpression.SUB
				|| operator == AssignmentExpression.MULT
				|| operator == AssignmentExpression.DIVIDE
				|| operator == AssignmentExpression.MOD) {
			if (operator != AssignmentExpression.ADD
					&& !ops.contains(AssignmentExpression.ADD)) {
				ops.add(AssignmentExpression.ADD);
				return AssignmentExpression.ADD;
			}
			if (operator != AssignmentExpression.SUB
					&& !ops.contains(AssignmentExpression.SUB)) {
				ops.add(AssignmentExpression.SUB);
				return AssignmentExpression.SUB;
			}
			if (operator != AssignmentExpression.MULT
					&& !ops.contains(AssignmentExpression.MULT)) {
				ops.add(AssignmentExpression.MULT);
				return AssignmentExpression.MULT;
			}
			if (operator != AssignmentExpression.DIVIDE
					&& !ops.contains(AssignmentExpression.DIVIDE)) {
				ops.add(AssignmentExpression.DIVIDE);
				return AssignmentExpression.DIVIDE;
			}
			if (operator != AssignmentExpression.MOD
					&& !ops.contains(AssignmentExpression.MOD)) {
				ops.add(AssignmentExpression.MOD);
				return AssignmentExpression.MOD;
			}
		}

		if (operator == AssignmentExpression.XOR
				|| operator == AssignmentExpression.AND
				|| operator == AssignmentExpression.OR) {
			if (operator != AssignmentExpression.XOR
					&& !ops.contains(AssignmentExpression.XOR)) {
				ops.add(AssignmentExpression.XOR);
				return AssignmentExpression.XOR;
			}
			if (operator != AssignmentExpression.AND
					&& !ops.contains(AssignmentExpression.AND)) {
				ops.add(AssignmentExpression.AND);
				return AssignmentExpression.AND;
			}
			if (operator != AssignmentExpression.OR
					&& !ops.contains(AssignmentExpression.OR)) {
				ops.add(AssignmentExpression.OR);
				return AssignmentExpression.OR;
			}
		}

		if (operator == AssignmentExpression.SHIFT_L
				|| operator == AssignmentExpression.SHIFT_R
				|| operator == AssignmentExpression.SHIFT_RR) {
			if (operator != AssignmentExpression.SHIFT_L
					&& !ops.contains(AssignmentExpression.SHIFT_L)) {
				ops.add(AssignmentExpression.SHIFT_L);
				return AssignmentExpression.SHIFT_L;
			}
			if (operator != AssignmentExpression.SHIFT_R
					&& !ops.contains(AssignmentExpression.SHIFT_R)) {
				ops.add(AssignmentExpression.SHIFT_R);
				return AssignmentExpression.SHIFT_R;
			}
			if (operator != AssignmentExpression.SHIFT_RR
					&& !ops.contains(AssignmentExpression.SHIFT_RR)) {
				ops.add(AssignmentExpression.SHIFT_RR);
				return AssignmentExpression.SHIFT_RR;
			}
		}

		return 0;
	}

}
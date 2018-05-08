package mujava.gen.writer.nujava.traditional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.traditional.RORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Expression;
import openjava.ptree.ParseTreeException;
import mujava.op.traditional.ROR;

/**
 * 
 * @author swkim
 * @version 1.1
 */

public class RORNuJavaWriter extends TraditionalMutantNuJavaWriter {
	public static MutantWriter getMutantCodeWriter(RORChangePointSeeker mutator)
			throws IOException {
		RORNuJavaWriter mutantWriter = new RORNuJavaWriter(mutator
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

	public RORNuJavaWriter(Environment env) throws IOException {
		super(env, "ROR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
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

					super.visit(p);
					return;
				}
				
				int operator = p.getOperator();

				out.print("( nujava.MutantMonitor.ExecuteChangePoint["
						+ cPoint.getID()
						+ "] ? nujava.traditional.RORMetaMutant.RORGen(");

				super.visit(p.getLeft());
				out.print(", ");
				super.visit(p.getRight());
				out.print(", " + operator);
				out.print(", " + cPoint.getID());
				out.print(", \"" + mutantID.toString() + "\")");

				out.print(" : ");

				Expression lexpr = p.getLeft();
				String leftExp,rightExp;
				if (isOperatorNeededLeftPar(p.getOperator(), lexpr)) {
					writeParenthesis(lexpr);
					leftExp = "("+lexpr.toFlattenString()+")";
				} else {
					lexpr.accept(this);
					leftExp = lexpr.toFlattenString();
				}
				out.print(" " + p.operatorString() + " ");
				Expression rexpr = p.getRight();
				if (isOperatorNeededLeftPar(p.getOperator(), rexpr)) {
					writeParenthesis(rexpr);
					rightExp = "("+rexpr.toFlattenString()+")";
				} else {
					rexpr.accept(this);
					rightExp = rexpr.toFlattenString();
				}
				out.print(" )");

				ArrayList<Integer> ops = new ArrayList<Integer>();
				boolean isReached = false;
				OJClass clz = getType(p.getLeft());
				// 객체 형태일때는 !=, == 만 수행한다.
				if(clz == OJSystem.BOOLEAN){
					for (int i = 0; i < 2; i++) {
						// create MuJavaMutantInfo object to support mutant
						// information
						int newOp = ROR.getROROperatorForBoolean(operator, ops);
						if(newOp==0) continue;
						String log_str = p.toString() + " -> " + ROR.getMutatedExp(leftExp,rightExp,newOp);
						
						mutantID.setLastIndex(newOp);
	
						String parentMethod = (currentMethod != null) ? currentMethod
								.getName()
								: "";
						MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString()
								, getMutationOperatorName(), env
								.currentClassName(), parentMethod,
								MutantOperator.GEN_NUJAVA, 2, operator, newOp,
								getLineNumber(), removeNewline(log_str));
						
						// registers its mutation information
						if (!mujavaMutantInfos.contains(m)) {
							super.mujavaMutantInfos.add(m);
							isReached = true;
						}
					}					
				}else if (clz.isPrimitive()){			
					for (int i = 0; i < 7; i++) {
						// create MuJavaMutantInfo object to support mutant
						// information
						int newOp = ROR.getNonRedundantROROp(operator, ops);
						if(newOp==0) continue;
						String log_str = p.toString() + " -> " + ROR.getMutatedExp(leftExp,rightExp,newOp);
						
						mutantID.setLastIndex(newOp);
	
						String parentMethod = (currentMethod != null) ? currentMethod
								.getName()
								: "";
						MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString()
								, getMutationOperatorName(), env
								.currentClassName(), parentMethod,
								MutantOperator.GEN_NUJAVA, 7, operator, newOp,
								getLineNumber(), removeNewline(log_str));
						
						// registers its mutation information
						if (!mujavaMutantInfos.contains(m)) {
							super.mujavaMutantInfos.add(m);
							isReached = true;
						}
					}
				}else{
					int newOp = ROR.getROROperatorForObject(operator, ops);
					if(newOp!=0){
						String log_str = p.toString() + " -> " + ROR.getMutatedExp(leftExp,rightExp,newOp);
						
						mutantID.setLastIndex(newOp);
	
						String parentMethod = (currentMethod != null) ? currentMethod
								.getName()
								: "";
						MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID.toString()
								, getMutationOperatorName(), env
								.currentClassName(), parentMethod,
								MutantOperator.GEN_NUJAVA, 1, operator, newOp,
								getLineNumber(), removeNewline(log_str));
						
						// registers its mutation information
						if (!mujavaMutantInfos.contains(m)) {
							super.mujavaMutantInfos.add(m);
							isReached = true;
						}		
					}
					
				}

				if (isReached) {
					// Writer에서 생성되었음을 알수 있는 counter
					increaseSizeOfChangedPoints();
				}
				return;
			}
		}
		super.visit(p);
	}

/*	private int getROROperator(int operator, ArrayList<Integer> ops, int size) {
		if (size == 5 && operator != BinaryExpression.GREATER
				&& !ops.contains(BinaryExpression.GREATER)) {
			ops.add(BinaryExpression.GREATER);
			return BinaryExpression.GREATER;
		}
		if (size == 5 && operator != BinaryExpression.GREATEREQUAL
				&& !ops.contains(BinaryExpression.GREATEREQUAL)) {
			ops.add(BinaryExpression.GREATEREQUAL);
			return BinaryExpression.GREATEREQUAL;
		}
		if (size == 5 && operator != BinaryExpression.LESS
				&& !ops.contains(BinaryExpression.LESS)) {
			ops.add(BinaryExpression.LESS);
			return BinaryExpression.LESS;
		}
		if (size == 5 && operator != BinaryExpression.LESSEQUAL
				&& !ops.contains(BinaryExpression.LESSEQUAL)) {
			ops.add(BinaryExpression.LESSEQUAL);
			return BinaryExpression.LESSEQUAL;
		}
		if (operator != BinaryExpression.EQUAL
				&& !ops.contains(BinaryExpression.EQUAL)) {
			ops.add(BinaryExpression.EQUAL);
			return BinaryExpression.EQUAL;
		}
		if (operator != BinaryExpression.NOTEQUAL
				&& !ops.contains(BinaryExpression.NOTEQUAL)) {
			ops.add(BinaryExpression.NOTEQUAL);
			return BinaryExpression.NOTEQUAL;
		}

		return 0;
	}*/
}

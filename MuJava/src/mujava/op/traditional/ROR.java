package mujava.op.traditional;

import java.io.IOException;
import java.util.ArrayList;

import openjava.ptree.BinaryExpression;
import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.RORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.ROR_MSGWriter;
import mujava.gen.writer.normal.traditional.ROR_NormalWriter;
import mujava.gen.writer.nujava.traditional.RORNuJavaWriter;
import mujava.gen.writer.reachability.traditional.ROR_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.ROR_NormalStateWriter;

public class ROR extends TradionalMutantOperator {
	
	public static int TRUE = 20;
	public static int FALSE = 21;

	public ROR() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "ROR";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return ROR_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.RORChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return ROR_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.RORChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return RORNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.RORChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return ROR_ReachableMutantWriter
						.getMutantCodeWriter((RORChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return ROR_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.RORChangePointSeeker) mutator);	
			}
		}
		return null;
	}
	
	public static String getMutatedExp(String left,String right, int operator){
		switch(operator){
			case BinaryExpression.GREATER:
				return left + " "+ ">" + right;
			case BinaryExpression.GREATEREQUAL:
				return left + " "+ ">=" +right;
			case BinaryExpression.LESS:
				return left + " "+ "<" +right;
			case BinaryExpression.LESSEQUAL:
				return left + " "+ "<=" +right;
			case BinaryExpression.EQUAL:
				return left + " "+ "==" +right;
			case BinaryExpression.NOTEQUAL:
				return left + " "+ "!=" +right;
			case 20:
				return "true";
			case 21:
				return "false";
		}
		return null;
	}
	
	public static int getNonRedundantROROp(int operator, ArrayList<Integer> ops) {
		
		if( operator == BinaryExpression.GREATER){
			if(!ops.contains(FALSE)){
				ops.add(FALSE);	return FALSE;
			}
			if(!ops.contains(BinaryExpression.GREATEREQUAL)){
				ops.add(BinaryExpression.GREATEREQUAL);	return BinaryExpression.GREATEREQUAL;
			}
			if(!ops.contains(BinaryExpression.NOTEQUAL)){
				ops.add(BinaryExpression.NOTEQUAL);	return BinaryExpression.NOTEQUAL;
			}			
		}
		
		if( operator == BinaryExpression.LESS){
			if(!ops.contains(FALSE)){
				ops.add(FALSE);	return FALSE;
			}
			if(!ops.contains(BinaryExpression.LESSEQUAL)){
				ops.add(BinaryExpression.LESSEQUAL);	return BinaryExpression.LESSEQUAL;
			}
			if(!ops.contains(BinaryExpression.NOTEQUAL)){
				ops.add(BinaryExpression.NOTEQUAL);	return BinaryExpression.NOTEQUAL;
			}
		}
		
		if(operator == BinaryExpression.EQUAL){
			if(!ops.contains(FALSE)){
				ops.add(FALSE);	return FALSE;
			}
			if(!ops.contains(BinaryExpression.LESSEQUAL)){
				ops.add(BinaryExpression.LESSEQUAL);	return BinaryExpression.LESSEQUAL;
			}
			if(!ops.contains(BinaryExpression.GREATEREQUAL)){
				ops.add(BinaryExpression.GREATEREQUAL);	return BinaryExpression.GREATEREQUAL;
			}
		}
		
		if(operator == BinaryExpression.GREATEREQUAL){
			if(!ops.contains(TRUE)){
				ops.add(TRUE);	return TRUE;
			}
			if(!ops.contains(BinaryExpression.GREATER)){
				ops.add(BinaryExpression.GREATER);	return BinaryExpression.GREATER;
			}
			if(!ops.contains(BinaryExpression.EQUAL)){
				ops.add(BinaryExpression.EQUAL);	return BinaryExpression.EQUAL;
			}
		}
		
		if(operator == BinaryExpression.LESSEQUAL){
			if(!ops.contains(TRUE)){
				ops.add(TRUE);	return TRUE;
			}
			if(!ops.contains(BinaryExpression.LESS)){
				ops.add(BinaryExpression.LESS);	return BinaryExpression.LESS;
			}
			if(!ops.contains(BinaryExpression.EQUAL)){
				ops.add(BinaryExpression.EQUAL);	return BinaryExpression.EQUAL;
			}
		}
		
		if(operator == BinaryExpression.NOTEQUAL){
			if(!ops.contains(TRUE)){
				ops.add(TRUE);	return TRUE;
			}
			if(!ops.contains(BinaryExpression.LESS)){
				ops.add(BinaryExpression.LESS);	return BinaryExpression.LESS;
			}
			if(!ops.contains(BinaryExpression.GREATER)){
				ops.add(BinaryExpression.GREATER);	return BinaryExpression.GREATER;
			}
		}
	
		return 0;
	}	


	public static int getROROperatorForBoolean(int operator, ArrayList<Integer> ops) {
			if (operator == BinaryExpression.EQUAL){
					if(!ops.contains(BinaryExpression.NOTEQUAL)) {
						ops.add(BinaryExpression.NOTEQUAL);
						return BinaryExpression.NOTEQUAL;
					}
					if(!ops.contains(TRUE)) {
						ops.add(TRUE);
						return TRUE;
					}
			}
			if (operator == BinaryExpression.NOTEQUAL) {
				if(!ops.contains(BinaryExpression.EQUAL)) {
					ops.add(BinaryExpression.EQUAL);
					return BinaryExpression.EQUAL;
				}
				if(!ops.contains(FALSE)) {
					ops.add(FALSE);
					return FALSE;
				}
			}
			return 0;
	}	
	
	public static int getROROperatorForObject(int operator, ArrayList<Integer> ops) {
		
		if(false){
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
		}
		return 0;
	}	

}

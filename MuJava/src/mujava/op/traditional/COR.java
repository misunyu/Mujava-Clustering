package mujava.op.traditional;

import java.io.IOException;
import java.util.ArrayList;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.CORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.COR_MSGWriter;
import mujava.gen.writer.normal.traditional.COR_NormalWriter;
import mujava.gen.writer.nujava.traditional.CORNuJavaWriter;
import mujava.gen.writer.reachability.traditional.COR_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.COR_NormalStateWriter;
import mujava.gen.writer.state.traditional.ROR_NormalStateWriter;
import openjava.ptree.BinaryExpression;

public class COR extends TradionalMutantOperator {

	public static int TRUE = 20;
	public static int FALSE = 21;
	public static int LHS = 22;
	public static int RHS = 23;


	public COR() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH);
		super.name = "COR";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return COR_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CORChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return COR_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CORChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return CORNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CORChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return COR_ReachableMutantWriter
						.getMutantCodeWriter((CORChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return COR_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CORChangePointSeeker) mutator);	
			}
		}
		return null;
	}
	
	public static int getNonRedundantCOROp(int operator, ArrayList<Integer> ops) {
		
		if (operator == BinaryExpression.LOGICAL_AND){
			if(!ops.contains(FALSE)){
				ops.add(FALSE);
				return FALSE;				
			}
			if(!ops.contains(LHS)){
				ops.add(LHS);
				return LHS;				
			}
			if(!ops.contains(RHS)){
				ops.add(RHS);
				return RHS;				
			}
			if(!ops.contains(BinaryExpression.EQUAL)){
				ops.add(BinaryExpression.EQUAL);
				return BinaryExpression.EQUAL;				
			}
		}

		if (operator == BinaryExpression.LOGICAL_OR){
			if(!ops.contains(BinaryExpression.NOTEQUAL)){
				ops.add(BinaryExpression.NOTEQUAL);
				return BinaryExpression.NOTEQUAL;				
			}
			if(!ops.contains(RHS)){
				ops.add(RHS);
				return RHS;				
			}
			if(!ops.contains(LHS)){
				ops.add(LHS);
				return LHS;				
			}
			if(!ops.contains(TRUE)){
				ops.add(TRUE);
				return TRUE;				
			}
		}
		return 0;
	}
	
	public static String getCORCode(int i) {
		switch (i) {
		case BinaryExpression.LOGICAL_AND:
			return "&&";
		case BinaryExpression.LOGICAL_OR:
			return "||";
		case BinaryExpression.NOTEQUAL:
			return "!=";
		case BinaryExpression.EQUAL:
			return "==";
		}

		assert (false);
		return new String();
	}
}

package mujava.op.traditional;

import java.io.IOException;
import java.util.ArrayList;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.CODChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.COD_MSGWriter;
import mujava.gen.writer.normal.traditional.COD_NormalWriter;
import mujava.gen.writer.nujava.traditional.CODNuJavaWriter;
import mujava.gen.writer.reachability.traditional.COD_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.COD_NormalStateWriter;
import mujava.gen.writer.state.traditional.COR_NormalStateWriter;
import openjava.ptree.BinaryExpression;

public class COD extends TradionalMutantOperator {
	

	public COD() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH);
		super.name = "COD";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return COD_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CODChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return COD_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CODChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return CODNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CODChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return COD_ReachableMutantWriter
						.getMutantCodeWriter((CODChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return COD_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.CODChangePointSeeker) mutator);	
			}
		}
		return null;
	}
	

	
	
}

package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.COIChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.COI_MSGWriter;
import mujava.gen.writer.normal.traditional.COI_NormalWriter;
import mujava.gen.writer.nujava.traditional.COINuJavaWriter;
import mujava.gen.writer.reachability.traditional.COI_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.COI_NormalStateWriter;
import mujava.gen.writer.state.traditional.COR_NormalStateWriter;

public class COI extends TradionalMutantOperator {

	public COI() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH);
		super.name = "COI";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return COI_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.COIChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return COI_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.COIChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return COINuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.COIChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return COI_ReachableMutantWriter
						.getMutantCodeWriter((COIChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return COI_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.COIChangePointSeeker) mutator);	
			}
		}
		return null;
	}
}

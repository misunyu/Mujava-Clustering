package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AOIU_MSGWriter;
import mujava.gen.writer.normal.traditional.AOIU_NormalWriter;
import mujava.gen.writer.nujava.traditional.AOIUNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AOIU_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AOIU_NormalStateWriter;

public class AOIU extends TradionalMutantOperator {

	public AOIU() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AOIU";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AOIU_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOIUChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AOIU_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOIUChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AOIUNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOIUChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AOIU_ReachableMutantWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOIUChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AOIU_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOIUChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

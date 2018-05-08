package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.LORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.LOR_MSGWriter;
import mujava.gen.writer.normal.traditional.LOR_NormalWriter;
import mujava.gen.writer.nujava.traditional.LORNuJavaWriter;
import mujava.gen.writer.reachability.traditional.LOR_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.LOR_NormalStateWriter;

public class LOR extends TradionalMutantOperator {

	public LOR() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "LOR";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return LOR_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LORChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return LOR_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LORChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return LORNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LORChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return LOR_ReachableMutantWriter
						.getMutantCodeWriter((LORChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return LOR_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LORChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

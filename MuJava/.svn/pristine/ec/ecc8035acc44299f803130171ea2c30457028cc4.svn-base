package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.BORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.BOR_MSGWriter;
import mujava.gen.writer.normal.traditional.BOR_NormalWriter;
import mujava.gen.writer.nujava.traditional.BORNuJavaWriter;
import mujava.gen.writer.reachability.traditional.BOR_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.BOR_NormalStateWriter;

public class BOR extends TradionalMutantOperator {

	public BOR() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "BOR";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return BOR_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BORChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return BOR_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BORChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return BORNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BORChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return BOR_ReachableMutantWriter
						.getMutantCodeWriter((BORChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return BOR_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BORChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

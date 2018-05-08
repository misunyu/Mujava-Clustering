package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.BOIChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.BOI_MSGWriter;
import mujava.gen.writer.normal.traditional.BOI_NormalWriter;
import mujava.gen.writer.nujava.traditional.BOINuJavaWriter;
import mujava.gen.writer.reachability.traditional.BOI_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.BOI_NormalStateWriter;

public class BOI extends TradionalMutantOperator {

	public BOI() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "BOI";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return BOI_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BOIChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return BOI_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BOIChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return BOINuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BOIChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return BOI_ReachableMutantWriter
						.getMutantCodeWriter((BOIChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return BOI_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BOIChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

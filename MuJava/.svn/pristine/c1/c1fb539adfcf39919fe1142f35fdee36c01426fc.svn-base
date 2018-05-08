package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.BODChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.BOD_MSGWriter;
import mujava.gen.writer.normal.traditional.BOD_NormalWriter;
import mujava.gen.writer.nujava.traditional.BODNuJavaWriter;
import mujava.gen.writer.reachability.traditional.BOD_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.BOD_NormalStateWriter;

public class BOD extends TradionalMutantOperator {

	public BOD() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "BOD";
	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return BOD_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BODChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return BOD_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BODChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return BODNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BODChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return BOD_ReachableMutantWriter
						.getMutantCodeWriter((BODChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return BOD_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.BODChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

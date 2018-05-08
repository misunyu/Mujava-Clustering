package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AOIS_MSGWriter;
import mujava.gen.writer.normal.traditional.AOIS_NormalWriter;
import mujava.gen.writer.nujava.traditional.AOISNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AOIS_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AOIS_NormalStateWriter;

public class AOIS extends TradionalMutantOperator {

	public AOIS() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AOIS";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AOIS_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOISChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AOIS_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOISChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AOISNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOISChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AOIS_ReachableMutantWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOISChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AOIS_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AOISChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

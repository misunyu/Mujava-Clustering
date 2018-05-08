package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AODS_MSGWriter;
import mujava.gen.writer.normal.traditional.AODS_NormalWriter;
import mujava.gen.writer.nujava.traditional.AODSNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AODS_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AODS_NormalStateWriter;

public class AODS extends TradionalMutantOperator {

	public AODS() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AODS";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AODS_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODSChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AODS_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODSChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AODSNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODSChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AODS_ReachableMutantWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODSChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AODS_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODSChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

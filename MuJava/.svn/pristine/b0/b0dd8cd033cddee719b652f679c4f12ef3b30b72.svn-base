package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.ASRSChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.ASRS_MSGWriter;
import mujava.gen.writer.normal.traditional.ASRS_NormalWriter;
import mujava.gen.writer.nujava.traditional.ASRSNuJavaWriter;
import mujava.gen.writer.reachability.traditional.ASRS_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.ASRS_NormalStateWriter;

public class ASRS extends TradionalMutantOperator {

	public ASRS() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "ASRS";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return ASRS_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.ASRSChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return ASRS_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.ASRSChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return ASRSNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.ASRSChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return ASRS_ReachableMutantWriter
						.getMutantCodeWriter((ASRSChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return ASRS_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.ASRSChangePointSeeker) mutator);

			}
		}
		return null;
	}

}

package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.AORSChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AORS_MSGWriter;
import mujava.gen.writer.normal.traditional.AORS_NormalWriter;
import mujava.gen.writer.nujava.traditional.AORSNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AORS_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AORS_NormalStateWriter;

public class AORS extends TradionalMutantOperator {

	public AORS() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AORS";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AORS_NormalWriter
						.getMutantCodeWriter((AORSChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AORS_MSGWriter
						.getMutantCodeWriter((AORSChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AORSNuJavaWriter
						.getMutantCodeWriter((AORSChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AORS_ReachableMutantWriter
						.getMutantCodeWriter((AORSChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AORS_NormalStateWriter
						.getMutantCodeWriter((AORSChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

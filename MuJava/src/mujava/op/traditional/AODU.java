package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AODU_MSGWriter;
import mujava.gen.writer.normal.traditional.AODU_NormalWriter;
import mujava.gen.writer.nujava.traditional.AODUNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AODU_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AODU_NormalStateWriter;

public class AODU extends TradionalMutantOperator {

	public AODU() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AODU";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AODU_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODUChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AODU_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODUChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AODUNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODUChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AODU_ReachableMutantWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODUChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AODU_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AODUChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

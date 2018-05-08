package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.AORB_MSGWriter;
import mujava.gen.writer.normal.traditional.AORB_NormalWriter;
import mujava.gen.writer.nujava.traditional.AORBNuJavaWriter;
import mujava.gen.writer.reachability.traditional.AORB_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.AORB_NormalStateWriter;

public class AORB extends TradionalMutantOperator {

	public AORB() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "AORB";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return AORB_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AORBChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return AORB_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AORBChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return AORBNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AORBChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return AORB_ReachableMutantWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AORBChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return AORB_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.AORBChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

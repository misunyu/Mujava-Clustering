package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.LOIChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.LOI_MSGWriter;
import mujava.gen.writer.normal.traditional.LOI_NormalWriter;
import mujava.gen.writer.nujava.traditional.LOINuJavaWriter;
import mujava.gen.writer.reachability.traditional.LOI_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.LOI_NormalStateWriter;

public class LOI extends TradionalMutantOperator {

	public LOI() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "LOI";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return LOI_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LOIChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return LOI_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LOIChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return LOINuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LOIChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return LOI_ReachableMutantWriter
						.getMutantCodeWriter((LOIChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return LOI_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LOIChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

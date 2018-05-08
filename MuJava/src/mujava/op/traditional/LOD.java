package mujava.op.traditional;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.traditional.LODChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.traditional.LOD_MSGWriter;
import mujava.gen.writer.normal.traditional.LOD_NormalWriter;
import mujava.gen.writer.nujava.traditional.LODNuJavaWriter;
import mujava.gen.writer.reachability.traditional.LOD_ReachableMutantWriter;
import mujava.gen.writer.state.traditional.LOD_NormalStateWriter;

public class LOD extends TradionalMutantOperator {

	public LOD() {
		super(MutantOperator.TRADITIONAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA
				| MutantOperator.GEN_EXP_REACH | MutantOperator.GEN_STATE);
		super.name = "LOD";

	}

	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return LOD_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LODChangePointSeeker) mutator);
			case MutantOperator.GEN_MSG:
				return LOD_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LODChangePointSeeker) mutator);
			case MutantOperator.GEN_NUJAVA:
				return LODNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LODChangePointSeeker) mutator);
			case MutantOperator.GEN_EXP_REACH:
				return LOD_ReachableMutantWriter
						.getMutantCodeWriter((LODChangePointSeeker) mutator);
			case MutantOperator.GEN_STATE:
				return LOD_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.traditional.LODChangePointSeeker) mutator);

			}
		}
		return null;
	}
}

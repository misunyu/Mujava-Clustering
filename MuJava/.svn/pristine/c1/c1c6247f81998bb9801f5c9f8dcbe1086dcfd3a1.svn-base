package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.classical.PRV_MSGWriter;
import mujava.gen.writer.normal.classical.PRV_NormalWriter;
import mujava.gen.writer.nujava.classical.PRVNuJavaWriter;
import mujava.gen.writer.state.classical.PRV_NormalStateWriter;

public class PRV extends MutantOperator {

	public PRV() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA | MutantOperator.GEN_STATE);
		super.name = "PRV";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
				return PRV_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.PRV) mutator);
			case MutantOperator.GEN_MSG:
				return PRV_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.PRV) mutator);
			case MutantOperator.GEN_NUJAVA:
				return PRVNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.PRV) mutator);
			case MutantOperator.GEN_STATE:
				return PRV_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.PRV) mutator);

			}
		}
		return null;
	}

}

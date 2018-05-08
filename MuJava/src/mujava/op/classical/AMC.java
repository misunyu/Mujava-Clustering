package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.AMC_NormalWriter;

public class AMC extends MutantOperator {

	public AMC() {
		super(MutantOperator.CLASSICAL /*
										 * | MutantOperator.GEN_NORMAL /* |
										 * MutantOperator.GEN_MSG |
										 * MutantOperator.GEN_NEW
										 */);
		super.name = "AMC";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator,
			int mode) throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
			case MutantOperator.GEN_STATE:
			case MutantOperator.GEN_MSG:
			case MutantOperator.GEN_NUJAVA:
				return AMC_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.AMC) mutator);
				// case MutantOperator.GEN_NEW:
				// return JSI_WMSGWriter
				// .getMutantCodeWriter((mujava.gen.mutator.classical.JSI)
				// mutator);
			}
		}
		return null;
	}
}

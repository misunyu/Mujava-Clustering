package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.EMM_NormalWriter;

public class EMM extends MutantOperator {

	public EMM() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL | MutantOperator.GEN_STATE);
		// | MutantOperator.GEN_MSG | MutantOperator.GEN_NEW);
		super.name = "EMM";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
			case MutantOperator.GEN_MSG:
			case MutantOperator.GEN_NUJAVA:
			case MutantOperator.GEN_STATE:
				return EMM_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.EMM) mutator);
				// case MutantOperator.GEN_NEW:
				// return JTI_WMSGWriter
				// .getMutantCodeWriter((mujava.gen.mutator.classical.JTI)
				// mutator);
			}
		}
		return null;
	}

}

package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.JSI_NormalWriter;

public class JSI extends MutantOperator {

	public JSI() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL | MutantOperator.GEN_STATE
		/* | MutantOperator.GEN_MSG | MutantOperator.GEN_NEW */);
		super.name = "JSI";
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
				return JSI_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JSI) mutator);
				// case MutantOperator.GEN_NEW:
				// return JSI_WMSGWriter
				// .getMutantCodeWriter((mujava.gen.mutator.classical.JSI)
				// mutator);
			}
		}
		return null;
	}
}

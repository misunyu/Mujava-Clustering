package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.EOA_NormalWriter;
import mujava.gen.writer.nujava.classical.EOANuJavaWriter;

public class EOA extends MutantOperator {

	public EOA() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA | MutantOperator.GEN_STATE);
		super.name = "EOA";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
			case MutantOperator.GEN_STATE:

			case MutantOperator.GEN_MSG:
				return EOA_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.EOA) mutator);
			case MutantOperator.GEN_NUJAVA:
				return EOANuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.EOA) mutator);
			}
		}
		return null;
	}

}

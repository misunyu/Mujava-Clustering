package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.EAM_NormalWriter;
import mujava.gen.writer.nujava.classical.EAMNuJavaWriter;

public class EAM extends MutantOperator {

	public EAM() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA | MutantOperator.GEN_STATE);
		super.name = "EAM";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
			case MutantOperator.GEN_STATE:				
			case MutantOperator.GEN_MSG:
				return EAM_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.EAM) mutator);
			case MutantOperator.GEN_NUJAVA:
				return EAMNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.EAM) mutator);
			}
		}
		return null;
	}

}

package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.classical.JTI_NormalWriter;
import mujava.gen.writer.nujava.classical.JTINuJavaWriter;

public class JTI extends MutantOperator {

	public JTI() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL
		/* | MutantOperator.GEN_MSG */| MutantOperator.GEN_NUJAVA | MutantOperator.GEN_STATE);
		super.name = "JTI";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {
			case MutantOperator.GEN_NORMAL:
			case MutantOperator.GEN_MSG:
			case MutantOperator.GEN_STATE:				
				return JTI_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTI) mutator);
			case MutantOperator.GEN_NUJAVA:
				return JTINuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTI) mutator);
			}
		}
		return null;
	}

}

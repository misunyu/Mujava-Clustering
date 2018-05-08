package mujava.op.classical;

import java.io.IOException;

import mujava.MutantOperator;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.MSG.classical.JTD_MSGWriter;
import mujava.gen.writer.normal.classical.JTD_NormalWriter;
import mujava.gen.writer.nujava.classical.JTDNuJavaWriter;
import mujava.gen.writer.state.classical.JTD_NormalStateWriter;

public class JTD extends MutantOperator {

	public JTD() {
		super(MutantOperator.CLASSICAL | MutantOperator.GEN_NORMAL
				| MutantOperator.GEN_MSG | MutantOperator.GEN_NUJAVA | MutantOperator.GEN_STATE);
		super.name = "JTD";
	}

	@Override
	public MutantWriter getMutantCodeWriter(AbstractChangePointSeeker mutator, int mode)
			throws IOException {
		if (name.equalsIgnoreCase(mutator.getOperatorName())) {
			switch (mode) {

			case MutantOperator.GEN_NORMAL:
				return JTD_NormalWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTD) mutator);
			case MutantOperator.GEN_MSG:
				return JTD_MSGWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTD) mutator);
			case MutantOperator.GEN_NUJAVA:
				return JTDNuJavaWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTD) mutator);
			case MutantOperator.GEN_STATE:
				return JTD_NormalStateWriter
						.getMutantCodeWriter((mujava.gen.seeker.classical.JTD) mutator);

			}
		}
		return null;
	}
}

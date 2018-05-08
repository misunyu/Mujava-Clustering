package mujava.gen.writer.reachability.traditional;

import java.io.IOException;
import java.util.List;

import mujava.MutantID;
import mujava.gen.writer.reachability.ReachableMutantWriter;
import openjava.mop.Environment;

public class TraditionalReachableMutantWriter extends ReachableMutantWriter {
	protected static final int IMPLEMENTED = 3;
	protected static final int ORIGINAL = 0;

	// internal flags
	protected int phase = IMPLEMENTED;

	protected TraditionalReachableMutantWriter(Environment env, String name)
			throws IOException {
		super(env, name);
	}

	protected int containMutantID(MutantID mutantID, int operator,
			List<String> generatedMutantID, int comparedOpType) {

		if (operator != comparedOpType) {
			mutantID.setLastIndex(comparedOpType);
			String id = mutantID.toString();
			if (!generatedMutantID.contains(id)) {
				return comparedOpType;
			}
		}

		return -1;
	}
}

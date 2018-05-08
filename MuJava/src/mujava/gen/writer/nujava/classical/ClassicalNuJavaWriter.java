package mujava.gen.writer.nujava.classical;

import java.io.IOException;

import mujava.gen.writer.nujava.MutantNuJavaWriter;
import openjava.mop.Environment;

public class ClassicalNuJavaWriter extends MutantNuJavaWriter {
	protected static final int IMPLEMENTED = 3;
	protected static final int ORIGINAL = 0;

	// internal flags
	protected int phase = IMPLEMENTED;

	protected ClassicalNuJavaWriter(Environment env, String name)
			throws IOException {
		super(env, name);
	}

}

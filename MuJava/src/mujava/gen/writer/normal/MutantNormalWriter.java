package mujava.gen.writer.normal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;

/**
 * Normal Writer makes a mutant source file corresponding a change point.
 * Because it is possible to make a set of mutants for a change point, it should
 * have remember the number of generated files. Variable sizesOfIteration has
 * the iteration number of each change point, and isFixedIteration indicates
 * whether each iteration number is same or not. If isFixedIteration is true,
 * fixedSizeOfIteration has the valid iteration number.
 * 
 * @author swkim
 * 
 */
public abstract class MutantNormalWriter extends MutantWriter {

	/**
	 * The fixed number of mutant candidates generated from a change point
	 */
	protected int fixedSizeOfIteration;

	/**
	 * indicates whether each iteration number is same or not.
	 */
	private boolean isFixedIteration = false;

	/**
	 * The number of mutant candidates generated from eacf change point
	 */
	protected Map<ChangePoint, Integer> sizesOfIteration = null;

	protected List<String> generatedMutantID = null;

	protected MutantNormalWriter(Environment env, String name,
			boolean fixedIteration) throws IOException {
		super(env, name, MutantOperator.GEN_NORMAL);
		sizesOfIteration = new HashMap<ChangePoint, Integer>();
		generatedMutantID = new ArrayList<String>();
		setFixedIteration(fixedIteration);
	}

	protected void setSizeOfIteration(Map<ChangePoint, Integer> iterationTable) {
		sizesOfIteration.clear();
		sizesOfIteration.putAll(iterationTable);
	}

	private void setFixedIteration(boolean isFixedIteration) {
		this.isFixedIteration = isFixedIteration;
	}

	public int getSizeOfIteration(ChangePoint point) {
		assert (point != null);

		if (isFixedIteration)
			return fixedSizeOfIteration;

		return sizesOfIteration.get(point);
	}
}
package mujava.plugin.editor.mujavaproject;

import java.util.List;

import mujava.MutantOperator;

import org.eclipse.swt.widgets.Composite;

public class TraditionalMutantComposite extends MutantViewerComposite {
	private static List<String> traditionalMutationOperators = MutantOperator
			.getAllTraditionalOperators();

	public TraditionalMutantComposite(Composite parent, int style) {
		super(parent, style);

		// Collections.sort(traditionalMutationOperators);
	}

	List<String> getMutationOperators() {
		return traditionalMutationOperators;
	}
}

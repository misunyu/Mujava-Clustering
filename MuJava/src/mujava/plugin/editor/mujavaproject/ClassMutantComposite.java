package mujava.plugin.editor.mujavaproject;

import java.util.List;

import mujava.MutantOperator;

import org.eclipse.swt.widgets.Composite;

public class ClassMutantComposite extends MutantViewerComposite {
	private static List<String> classicalMutationOperators = MutantOperator
			.getAllClassicalOperators();

	public ClassMutantComposite(Composite parent, int style) {
		super(parent, style);
	}

	List<String> getMutationOperators() {
		return classicalMutationOperators;
	}
}

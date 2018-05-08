package mujava.plugin.editor.mutantreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TimeAndCostResultContentProvider implements
		IStructuredContentProvider {

	private List<TestResultForOperator> results = new ArrayList<TestResultForOperator>();

	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.results.clear();

		if (newInput != null && newInput instanceof List) {
			this.results.addAll((List<TestResultForOperator>) newInput);
			Collections.sort(results);
		}
	}

	public Object[] getElements(Object inputElement) {
		assert (results != null);
		return (Object[]) results.toArray(new Object[results.size()]);
	}

}

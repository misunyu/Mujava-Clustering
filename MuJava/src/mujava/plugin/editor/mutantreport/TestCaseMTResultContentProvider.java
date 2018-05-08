package mujava.plugin.editor.mutantreport;

import java.util.List;

import mujava.ResultTable;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TestCaseMTResultContentProvider implements ITreeContentProvider {
	String ROOT = new String("All Test Cases");
	String NONEROOT = new String("No Test Cases");

	ResultTable rTable = null;

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null)
			return;

		this.rTable = (ResultTable) newInput;
	}

	public Object[] getChildren(Object parentElement) {
		List<TestCase> cases = rTable.getOverallTestCases();

		return (Object[]) cases.toArray(new Object[cases.size()]);
	}

	public Object getParent(Object element) {
		if (element instanceof TestCase)
			return ROOT;

		return null;
	}

	public boolean hasChildren(Object element) {
		if (rTable == null)
			return false;

		if (element instanceof String) {
			if (ROOT.equals(element))
				return true;
			else if (NONEROOT.equals(element))
				return false;
		}

		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (rTable != null && rTable.getOverallTestCases().size() > 0) {
			return new Object[] { ROOT };
		}

		return new Object[] { NONEROOT };
	}
}

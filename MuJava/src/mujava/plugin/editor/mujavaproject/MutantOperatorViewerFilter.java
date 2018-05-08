package mujava.plugin.editor.mujavaproject;

import java.util.ArrayList;
import java.util.List;

import mujava.MutantID;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

class MutantOperatorViewerFilter extends ViewerFilter {
	String name = null;
	List<String> list = new ArrayList<String>();

	public MutantOperatorViewerFilter() {
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IJavaElement)
			return true;

		if (element instanceof String) {
			// MuJavaMutantInfo info = (MuJavaMutantInfo) element;
			// String op = info.getMutantOperator();
			String mutantID = (String) element;
			String mutationOperator = MutantID.getMutationOperator(mutantID);

			if (list.contains(mutationOperator))
				return true;
		}

		return false;
	}

	/**
	 * 중복되지 않는 mutation operator에 대해 filter에 추가한다. 중복되는 mutation operator에 대해서는
	 * 아무런 동작을 하지 않는다.
	 * 
	 * @param str
	 *            operator name
	 */
	public void addMutantOperator(String str) {
		if (!list.contains(str))
			list.add(str);
	}

	public void clear() {
		list.clear();
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void deleteMutantOperator(String str) {
		this.list.remove(str);
	}
}

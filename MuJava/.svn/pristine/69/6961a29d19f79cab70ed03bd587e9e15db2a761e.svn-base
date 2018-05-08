package mujava.plugin.editor.mutantreport;

import mujava.MutantID;
import mujava.MutantResult;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class MutantResultSorter extends ViewerSorter {
	public MutantResultSorter() {
		super();
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		// if(e1 instanceof MuJavaMutantInfo && e2 instanceof MuJavaMutantInfo)
		// {
		// MuJavaMutantInfo p = (MuJavaMutantInfo)e1;
		// MuJavaMutantInfo q = (MuJavaMutantInfo)e2;
		// return p.compareTo(q);
		// }

		if (e1 instanceof TestResultForOperator
				&& e2 instanceof TestResultForOperator) {
			TestResultForOperator p = (TestResultForOperator) e1;
			TestResultForOperator q = (TestResultForOperator) e2;
			return p.getMutantOperator().compareTo(q.getMutantOperator());
		}
		if (e1 instanceof MutantScoreProvider.TestResultForFile
				&& e2 instanceof MutantScoreProvider.TestResultForFile) {
			MutantScoreProvider.TestResultForFile p = (MutantScoreProvider.TestResultForFile) e1;
			MutantScoreProvider.TestResultForFile q = (MutantScoreProvider.TestResultForFile) e2;
			return p.element.getElementName().compareTo(
					q.element.getElementName());
		} else if (e1 instanceof MutantResult && e2 instanceof MutantResult) {
			MutantResult p = (MutantResult) e1;
			MutantResult q = (MutantResult) e2;

			String mutantID1 = p.getMutantID();
			String mutantID2 = q.getMutantID();
			return new MutantID(mutantID1).compareTo(new MutantID(mutantID2));
		}

		return super.compare(viewer, e1, e2);
	}
}

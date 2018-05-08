package mujava.plugin.editor.mutantreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.MutantID;
import mujava.MutantResult;
import mujava.ResultTable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MutantScoreProvider implements ITreeContentProvider {
	public class TestResultForFile {
		public String mutationOperator;
		public IJavaElement element;
		private List<MutantResult> list = new ArrayList<MutantResult>();

		public void addMutantResult(MutantResult result) {
			list.add(result);
		}

		public List<MutantResult> getMutantResults() {
			return list;
		}
	}

	Map<String, List<TestResultForFile>> maps = new HashMap<String, List<TestResultForFile>>();

	private ResultTable table;

	private boolean isKilled;

	public MutantScoreProvider(boolean isKilled) {
		this.isKilled = isKilled;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null)
			return;

		table = (ResultTable) newInput;
		maps.clear();
	}

	public Object[] getElements(Object inputElement) {
		List<TestResultForOperator> results = new ArrayList<TestResultForOperator>();

		List<TestResultForOperator> tempList = table
				.getAllMutantOperatorResults();
		for (TestResultForOperator moResult : tempList) {
			if(isKilled && moResult.getKilledScore() > 0) {
				results.add(moResult);
			}
			else if( !isKilled && moResult.getKilledScore() < 1) {
				results.add(moResult);
			}
		}

		return (Object[]) results.toArray(new Object[results.size()]);
	}

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof TestResultForOperator) {
			TestResultForOperator moResult = (TestResultForOperator) parentElement;
			String op = moResult.getMutantOperator();
			List<MutantResult> tempList = this.table
					.getMutantResultByOperator(op);

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			List<String> files = table.getFilesByOperator(op);
			Map<Integer, IJavaElement> map = new HashMap<Integer, IJavaElement>();
			for (String fileName : files) {
				IResource res = root.findMember(new Path(fileName));
				IJavaElement element = JavaCore.create(res, JavaCore.create(res
						.getProject()));
				int code = element.getPath().hashCode();
				map.put(code, element);
			}

			List<TestResultForFile> list = new ArrayList<TestResultForFile>();
			for (MutantResult result : tempList) {
				if (isKilled == result.isKilled()) {
					int hashCode = MutantID.getHashCode(result.getMutantID());
					IJavaElement element = map.get(hashCode);
					if (element != null) {
						int foundIndex = -1;
						TestResultForFile currentTR = null;
						for (TestResultForFile tr : list) {
							if (element.equals(tr.element)) {
								foundIndex = list.indexOf(tr);
								currentTR = tr;
							}
						}
						if (currentTR == null) {
							currentTR = new TestResultForFile();
							currentTR.element = element;
							currentTR.mutationOperator = op;
						}
						currentTR.addMutantResult(result);

						if (foundIndex >= 0) {
							list.set(foundIndex, currentTR);
						} else
							list.add(currentTR);
					}
				}
			}
			maps.put(op, list);

			return list.toArray(new Object[list.size()]);

		} else if (parentElement instanceof TestResultForFile) {
			TestResultForFile tr = (TestResultForFile) parentElement;
			List<MutantResult> results = tr.getMutantResults();

			return (results != null) ? results.toArray(new Object[results
					.size()]) : null;
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		Object[] objs = this.getChildren(element);

		if (objs != null && objs.length > 0)
			return true;

		return false;
	}

	public Object getParent(Object element) {

		if (element instanceof TestResultForFile) {
			TestResultForFile tr = (TestResultForFile) element;
			TestResultForOperator moResult = this.table
					.getMutantOperatorResult(tr.mutationOperator);
			return moResult;
		} else if (element instanceof MutantResult) {
			MutantResult result = (MutantResult) element;
			int hashCode = MutantID.getHashCode(result.getMutantID());
			String op = result.getMutantOperator();
			List<TestResultForFile> list = maps.get(op);
			if (list != null) {
				for (TestResultForFile tr : list) {
					if (tr.element.hashCode() == hashCode)
						return tr;
				}
			}
		}

		return null;
	}
}

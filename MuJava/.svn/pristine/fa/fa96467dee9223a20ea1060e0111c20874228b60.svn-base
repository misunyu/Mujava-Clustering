package mujava.plugin.editor.mujavaproject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MutantCostContentProvider implements ITreeContentProvider {

	List<MutantCostReport> gResults = new ArrayList<MutantCostReport>();
	MutantCostReport top;

	public void dispose() {
		gResults.clear();
		top = null;
	}

	public Object[] getElements(Object inputElement) {
		Object[] ret = new Object[] { top };
		return ret;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof MutantCostReportTable) {

			MutantCostReportTable table = (MutantCostReportTable) newInput;
			this.gResults.clear();
			this.gResults.addAll(table.getResults());

			top = MutantCostReport.makeCollectedGenerationResult(gResults);
			long startBuildTime = -1;
			long lastBuildTime = -1;

			for (MutantCostReport gResult : gResults) {
				long currentBuildTime = gResult.getBuildTime();
				if (startBuildTime == -1)
					startBuildTime = currentBuildTime;
				if (lastBuildTime == -1)
					lastBuildTime = currentBuildTime;
				if (startBuildTime < currentBuildTime)
					startBuildTime = currentBuildTime;
				if (lastBuildTime > currentBuildTime)
					lastBuildTime = currentBuildTime;

			}
			top.setStartTime(startBuildTime);
			top.setSecondBuildTime(lastBuildTime);
		}
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement == top) {
			Object[] vals = gResults.toArray(new Object[gResults.size()]);
			return vals;
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof MutantCostReport) {
			MutantCostReport ret = (MutantCostReport) element;
			return (!ret.isCollected()) ? top : null;
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof MutantCostReport) {
			MutantCostReport ret = (MutantCostReport) element;
			if (ret.isCollected() && ret.getSizeOfMutants() > 0)
				return true;
		}

		return false;
	}

}

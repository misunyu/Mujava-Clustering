package mujava.plugin.editor.mutantreport;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TimeAndCostResultLabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		TestResultForOperator result = (TestResultForOperator) element;
		String text = new String();
		switch (columnIndex) {
		case 0:
			text = result.getMutantOperator();
			break;
		case 1:
			long totalTime = result.getComparingTime()
					+ result.getExecutingTime() + result.getLoadingTime()
					+ result.getPreparingTime() + result.getAnalysisTime();
			text = Long.toString(totalTime);
			break;
		case 2:
			text = Long.toString(result.getLoadingTime());
			break;
		case 3:
			text = Long.toString(result.getPreparingTime());
			break;
		case 4:
			text = Long.toString(result.getAnalysisTime());
			break;
		case 5:
			text = Long.toString(result.getExecutingTime());
			break;
		case 6:
			text = Long.toString(result.getExecutingTime(false));
			break;
		case 7:
			text = Long.toString(result.getComparingTime());
			break;
		case 8:
			text = Long.toString(result.getEtcTime());
			break;
		case 9:
			text = Long.toString(result.getTotalSize());
		}
		return text;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}

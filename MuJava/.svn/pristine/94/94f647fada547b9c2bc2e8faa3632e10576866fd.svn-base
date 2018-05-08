package mujava.plugin.editor.mujavaproject;

import kaist.selab.util.Helper;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class OperatorCostReportLabelProvider implements
		ITableLabelProvider {
	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String str = "";

		if (element instanceof CostReportForOperator) {
			CostReportForOperator gResult = (CostReportForOperator) element;
			if(gResult.isIncluded()) {
				switch(columnIndex) {
				case 0:
					str = gResult.getMutantOperator();
					break;
				}
			} else {
				switch(columnIndex) {
				case 0: 
					str = gResult.getMutantOperator();
					break;
				case 2:
					str = Helper.long2TimeString(gResult.getTime());
					break;
				case 3:
					str = Helper.long2SpaceString(gResult.getDiskCost());
					break;
				case 4:
					str = Helper.long2SpaceString(gResult.getMemoryCost());
					break;
				case 5:
					str = Integer.toString(gResult.getSizeOfMutants());
					break;
				}
			}
		}
		return str;
	}
}

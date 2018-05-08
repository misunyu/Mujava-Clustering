package mujava.plugin.editor.mujavaproject;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class MutantCostReportLableProvider implements ITableLabelProvider {

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
		if( element instanceof MutantCostReport) {
			MutantCostReport gResult = (MutantCostReport) element;
			switch(columnIndex) {
			case 0: 
				if(gResult.isCollected()) {
					int size  = gResult.getSizeOfMutants();
					if(size>0) str = "All Reports";
					else str = "No Generation Result";
					break;
				} else {
					long buildTime = gResult.getBuildTime();
					str = Long.toString(buildTime);
				}
				break;
			case 1:
				break;
			}
		} 
		
		return str;
	}

}

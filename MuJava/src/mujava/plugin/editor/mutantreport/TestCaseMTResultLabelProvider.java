package mujava.plugin.editor.mutantreport;

import mujava.ResultTable;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TestCaseMTResultLabelProvider implements ITableLabelProvider {
	ResultTable rTable = null;

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public String getColumnText(Object element, int columnIndex) {
		String text = "";
		if (element instanceof String && columnIndex == 0)
			return (String) element;
		else if (element instanceof TestCase) {
			TestCase tc = (TestCase) element;
			switch (columnIndex) {
			case 0:
				text = tc.toSimpleString();
				break;
			case 1:
				text = "";
				if (rTable != null) {
					if (rTable.isEffectiveTestCase(tc))
						text = "Effective";
					else if (rTable.isExecutedTestCase(tc))
						text = "Executed";
				}
				break;
			case 2:
				text = tc.getFileLoactions();
				break;
			}
		}
		return text;
	}

	public void updateResultTable(ResultTable resultTable) {
		this.rTable = resultTable;
	}
}

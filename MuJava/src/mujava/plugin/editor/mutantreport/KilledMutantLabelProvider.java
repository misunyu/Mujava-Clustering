package mujava.plugin.editor.mutantreport;

import mujava.MutantResult;
import mujava.plugin.editor.mutantreport.MutantScoreProvider.TestResultForFile;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class KilledMutantLabelProvider implements ITableLabelProvider {
	ILabelProvider javaProvider = new JavaElementLabelProvider(
			JavaElementLabelProvider.SHOW_DEFAULT
					| JavaElementLabelProvider.SHOW_QUALIFIED);

	Image mutantIcon = AbstractUIPlugin.imageDescriptorFromPlugin(
			"MuJavaEclipsePlugIn", "icons/brkpi_obj.gif").createImage();

	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof TestResultForFile && columnIndex == 0)
			return javaProvider.getImage(((TestResultForFile) element).element);
		else if (element instanceof MutantResult && columnIndex == 0)
			return mutantIcon;

		return null;
	}

	/**
	 * Gets the text for the specified column
	 * 
	 * @param arg0
	 *            the result
	 * @param arg1
	 *            the column
	 * @return String
	 */
	public String getColumnText(Object arg0, int arg1) {

		String text = "";

		if (arg0 instanceof TestResultForOperator) {
			TestResultForOperator result = (TestResultForOperator) arg0;
			switch (arg1) {
			case MutantResult.COLUMN_ID:
				text = result.getMutantOperator();
				break;
			// case MutantResult.COLUMN_ETC:
			// text = result.getKilledScoreString();
			// break;
			}
		} else if (arg0 instanceof TestResultForFile) {
			TestResultForFile result = (TestResultForFile) arg0;
			if (arg1 == 0) {
				text = result.element.getElementName();
			}
			if (arg1 == 4) {
				text = String.valueOf(((TestResultForFile) arg0)
						.getMutantResults().size());
			}
		} else if (arg0 instanceof MutantResult) {
			MutantResult result = (MutantResult) arg0;
			switch (arg1) {
			case MutantResult.COLUMN_ID:
				text = result.getMutantID();
				break;
			case MutantResult.COLUMN_TESTCASE:
				TestCase tc= result.getTestCase();
				text = (tc != null) ? tc.toString(): "";
				break;
			case MutantResult.COLUMN_ORIGINALRESULT:
				text = result.getOriginalResult();
				break;
			case MutantResult.COLUMN_MUTANTRESULT:
				text = result.getMutantResult();
				break;
			case MutantResult.COLUMN_ETC:
				break;
			}
		}
		return text;
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	public void dispose() {
		// finalize any resources
		javaProvider.dispose();
		mutantIcon.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}

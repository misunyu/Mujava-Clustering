package mujava.plugin.editor.mutantreport;

import mujava.ResultTable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 주어진 ResultTable에 사용된 TestCase를 보여주는 Composite, Tree로 각 method를 분류해서 보여준다.
 * 
 * @author swkim
 */
public class TestCaseViewerComposite extends Composite {

	class TestCaseSorter extends ViewerSorter {
		public TestCaseSorter() {
			super();
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof TestCase && e2 instanceof TestCase) {
				TestCase p = (TestCase) e1;
				TestCase q = (TestCase) e2;
				return p.toSimpleString().compareTo(q.toSimpleString());
			}
			return super.compare(viewer, e1, e2);
		}
	}

	private TreeViewer testCaseViewer;
	private TestCaseMTResultLabelProvider labelProvider = new TestCaseMTResultLabelProvider();

	public TestCaseViewerComposite(Composite parent, int none) {
		super(parent, none);

		// set components & its color
		Color whiteColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.setBackground(whiteColor);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setLayout(new GridLayout(1, false));

		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;

		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayout(new GridLayout(1, false));
		topComposite.setLayoutData(gridData1);

		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;

		CLabel titleLabel = new CLabel(topComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Test Cases");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 30, 45 });
		titleLabel.setLayoutData(gridData1);

		testCaseViewer = new TreeViewer(topComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		Tree tempTable = testCaseViewer.getTree();
		tempTable.setLayoutData(gridData2);
		tempTable.setLinesVisible(true);
		tempTable.setHeaderVisible(true);
		testCaseViewer
				.setContentProvider(new TestCaseMTResultContentProvider());
		testCaseViewer.setLabelProvider(labelProvider);
		testCaseViewer.setAutoExpandLevel(2);
		testCaseViewer.setSorter(new TestCaseSorter());
		TreeColumn column = new TreeColumn(tempTable, SWT.LEFT);
		column.setText("Test Cases");
		column.setWidth(150);
		column = new TreeColumn(tempTable, SWT.LEFT);
		column.setText("Effective");
		column.setWidth(150);
		column = new TreeColumn(tempTable, SWT.LEFT);
		column.setText("Loc");
		column.setWidth(200);

		Button btn = new Button(topComposite, SWT.NONE);
		btn.setText("View source");

		GridData gd3 = new GridData();
		gd3.horizontalAlignment = SWT.RIGHT;
		btn.setLayoutData(gd3);
		btn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * It has a sub-task (1 worked)
	 * 
	 * @param resultTable
	 * @param monitor
	 */
	public void update(ResultTable resultTable, IProgressMonitor monitor) {
		monitor
				.subTask("Update TestCaseComposite with the given ResultTable Object");

		if (resultTable != null) {
			labelProvider.updateResultTable(resultTable);
			testCaseViewer.setInput(resultTable);
			this.update();
		}

		monitor.worked(1);
	}
}

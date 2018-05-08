package mujava.plugin.editor.mutantreport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import mujava.MutantOperator;
import mujava.ResultTable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TimeCostViewerComposite extends Composite {

	private static final String TRADITIONAL_TABLE_TAB_TEXT = "Traditional Mutation Operators";
	private Label totalTimeCost;
	private Label totalLoadingTimeCost;
	private Label totalAnalysisTimeCost;
	private Label avgTimeCost;
	private Label avgLoadingTimeCost;
	private Label avgPreparingTimeCost;
	private Label totalPreparingTimeCost;
	private Label avgExecutingTimeCost;
	private Label totalExecutingTimeCost;
	private Label avgComparingTimeCost;
	private Label totalComparingTimeCost;
	private Label totalETCTimeCost;
	private Label avgETCTimeCost;
	private Label actualExecutionTime;
	private Label originalExecutionTime;
	private TableViewer tMutantTimeViewer;
	private TableViewer cMutantTimeViewer;
	private TabFolder tabFolder;

	public TimeCostViewerComposite(Composite container, int none) {
		super(container, none);

		// set components & its color
		Color whiteColor = container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE);
		this.setBackground(whiteColor);
		this.setBackgroundMode(SWT.INHERIT_FORCE);

		this.setLayout(new GridLayout(1, false));

		// make composites as initial area
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		GridData gridData3 = new GridData(GridData.FILL_BOTH);
		gridData3.horizontalSpan = 2;

		GridLayout topLayout = new GridLayout(4, false);
		GridLayout bottomLayout = new GridLayout(2, false);

		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayoutData(gridData1);
		topComposite.setLayout(topLayout);

		// For topComposite
		CLabel titleLabel = new CLabel(topComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Whole Time Cost");
		titleLabel
				.setBackground(
						new Color[] {
								container.getDisplay().getSystemColor(
										SWT.COLOR_TITLE_BACKGROUND),
								container.getDisplay().getSystemColor(
										SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
								container.getDisplay().getSystemColor(
										SWT.COLOR_WHITE) },
						new int[] { 60, 80 });
		titleLabel.setLayoutData(gridData);

		Label label = new Label(topComposite, SWT.NONE);
		label.setText("Actual whole time :");
		actualExecutionTime = new Label(topComposite, SWT.RIGHT);
		actualExecutionTime.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Original Execution Time :");
		originalExecutionTime = new Label(topComposite, SWT.RIGHT);
		originalExecutionTime.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Total Mutant Execution Time :");
		totalTimeCost = new Label(topComposite, SWT.RIGHT);
		totalTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Average Mutant Execution Time :");
		avgTimeCost = new Label(topComposite, SWT.RIGHT);
		avgTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("  Total Mutant Loading Time :");
		totalLoadingTimeCost = new Label(topComposite, SWT.RIGHT);
		totalLoadingTimeCost.setLayoutData(gridData1);
		label = new Label(topComposite, SWT.NONE);
		label.setText("  Average Mutant Loading Time :");
		avgLoadingTimeCost = new Label(topComposite, SWT.RIGHT);
		avgLoadingTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("  Total Mutant Preparing Time :");
		totalPreparingTimeCost = new Label(topComposite, SWT.RIGHT);
		totalPreparingTimeCost.setLayoutData(gridData1);
		label = new Label(topComposite, SWT.NONE);
		label.setText("  Average Mutant Preparing Time :");
		avgPreparingTimeCost = new Label(topComposite, SWT.RIGHT);
		avgPreparingTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("  Total Mutant Invoking Time :");
		totalExecutingTimeCost = new Label(topComposite, SWT.RIGHT);
		totalExecutingTimeCost.setLayoutData(gridData1);
		label = new Label(topComposite, SWT.NONE);
		label.setText("  Average Mutant Invoking Time :");
		avgExecutingTimeCost = new Label(topComposite, SWT.RIGHT);
		avgExecutingTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("  Total Mutant Comparing Time :");
		totalComparingTimeCost = new Label(topComposite, SWT.RIGHT);
		totalComparingTimeCost.setLayoutData(gridData1);
		label = new Label(topComposite, SWT.NONE);
		label.setText("  Average Mutant Comparing Time :");
		avgComparingTimeCost = new Label(topComposite, SWT.RIGHT);
		avgComparingTimeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("  Total Analysis Time :");
		totalAnalysisTimeCost = new Label(topComposite, SWT.RIGHT);
		totalAnalysisTimeCost.setLayoutData(gridData1);
		label = new Label(topComposite, SWT.NONE);
		label.setText("  Average etc Time :");
		avgETCTimeCost = new Label(topComposite, SWT.RIGHT);
		avgETCTimeCost.setLayoutData(gridData1);

		// label = new Label(topComposite, SWT.NONE);
		// label.setText("Total Size of Executed Mutants :");
		// sizeOfExecutedMutant = new Label(topComposite, SWT.RIGHT);
		// sizeOfExecutedMutant.setLayoutData(gridData1);

		// ///////////////////////////////////////////
		Composite bottomComposite = new Composite(this, SWT.None);
		bottomComposite.setLayoutData(gridData2);
		bottomComposite.setLayout(bottomLayout);
		// bottomComposite.setLayout(new FormLayout());

		// FormData upperFromData = new FormData();
		// upperFromData.top = new FormAttachment(0, 0);
		// upperFromData.bottom = new FormAttachment(50, 0);
		// upperFromData.left = new FormAttachment(0, 0);
		// upperFromData.right = new FormAttachment(100, 0);
		//
		// Composite tComposite = new Composite(bottomComposite, SWT.None);
		// tComposite.setLayoutData(upperFromData);
		// tComposite.setLayout(bottomLayout);
		//
		// FormData lowerFromData = new FormData();
		// lowerFromData.top = new FormAttachment(50, 0);
		// lowerFromData.bottom = new FormAttachment(100, 0);
		// lowerFromData.left = new FormAttachment(0, 0);
		// lowerFromData.right = new FormAttachment(100, 0);

		// Composite cComposite = new Composite(bottomComposite, SWT.None);
		// cComposite.setLayout(bottomLayout);
		// cComposite.setLayoutData(lowerFromData);

		titleLabel = new CLabel(bottomComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Time Cost per Mutation Operators");
		titleLabel
				.setBackground(
						new Color[] {
								container.getDisplay().getSystemColor(
										SWT.COLOR_TITLE_BACKGROUND),
								container.getDisplay().getSystemColor(
										SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
								container.getDisplay().getSystemColor(
										SWT.COLOR_WHITE) },
						new int[] { 60, 80 });
		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		titleLabel.setLayoutData(gridData1);

		Button clipboardBtn = new Button(bottomComposite, SWT.PUSH);
		clipboardBtn.setText("To Clipboard");
		clipboardBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = getTargetTable();
				String str = makeCopyText(table);

				Clipboard clipboard = new Clipboard(getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { str },
						new Transfer[] { textTransfer });
				clipboard.dispose();
			}

			private String makeCopyText(Table table) {

				StringBuffer sb = new StringBuffer();

				// print header
				int columnCounter = table.getColumnCount();
				int rowCounter = table.getItemCount();
				for (int j = 0; j < columnCounter; j++) {
					if (j == 2 || j == 6 || j == 7 || j == 8 || j == 9) {
						continue;
					}
					sb.append(table.getColumn(j).getText());
					sb.append("\t");
				}
				sb.append("\n");

				for (int i = 0; i < rowCounter; i++) {
					for (int j = 0; j < columnCounter; j++) {
						if (j == 2 || j == 6 || j == 7 || j == 8 || j == 9) {
							continue;
						}
						TableItem item = table.getItem(i);
						sb.append(item.getText(j));
						sb.append("\t");
					}
					sb.append("\n");
				}
				return sb.toString();
			}

			private Table getTargetTable() throws NoSuchElementException {
				int index = tabFolder.getSelectionIndex();
				if (index == -1) {
					throw new NoSuchElementException();
				}

				TableViewer viewer = cMutantTimeViewer;
				TabItem item = tabFolder.getItem(index);
				if (item.getText().equals(TRADITIONAL_TABLE_TAB_TEXT)) {
					viewer = tMutantTimeViewer;
				}

				if (viewer == null) {
					throw new NoSuchElementException();
				}
				return viewer.getTable();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		tabFolder = new TabFolder(bottomComposite, SWT.TOP);
		tabFolder.setLayoutData(gridData3);

		TabItem item1 = new TabItem(tabFolder, SWT.None);
		item1.setText(TRADITIONAL_TABLE_TAB_TEXT);

		TabItem item2 = new TabItem(tabFolder, SWT.None);
		item2.setText("Class Mutation Operators");

		// ----------------------------------
		//
		// MutantOperator|TimeCost|PreparingTime|LoadingTime|ExecutionTime|PureExecution|CompareTime|#
		// of mutants|Score|

		tMutantTimeViewer = new TableViewer(tabFolder, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);

		gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		Table tempTable = tMutantTimeViewer.getTable();
		tempTable.setLayoutData(gridData2);
		tempTable.setLinesVisible(true);
		tempTable.setHeaderVisible(true);
		tMutantTimeViewer
				.setContentProvider(new TimeAndCostResultContentProvider());
		tMutantTimeViewer
				.setLabelProvider(new TimeAndCostResultLabelProvider());
		// tMutantTimeViewer.setInput(null);
		TableColumn column = new TableColumn(tempTable, SWT.LEFT);
		column.setText("MutantOperator");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.LEFT);
		column.setText("T_Total(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Loading(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Preparing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Analysis(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Executing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_PureExecuting");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Comparing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_ETC(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("# of Mutant");
		column.setWidth(100);
		item1.setControl(tempTable);

		cMutantTimeViewer = new TableViewer(tabFolder, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		tempTable = cMutantTimeViewer.getTable();
		tempTable.setLayoutData(gridData2);
		tempTable.setLinesVisible(true);
		tempTable.setHeaderVisible(true);
		cMutantTimeViewer
				.setContentProvider(new TimeAndCostResultContentProvider());
		cMutantTimeViewer
				.setLabelProvider(new TimeAndCostResultLabelProvider());
		cMutantTimeViewer.setInput(null);
		column = new TableColumn(tempTable, SWT.LEFT);
		column.setText("MutantOperator");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.LEFT);
		column.setText("T_Total(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Loading(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Preparing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Executing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_PureExecuting");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_Comparing(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("T_ETC(AVG)");
		column.setWidth(100);
		column = new TableColumn(tempTable, SWT.CENTER);
		column.setText("# of Mutant");
		column.setWidth(100);
		item2.setControl(tempTable);
	}

	/**
	 * It has a sub-task (1 worked)
	 * 
	 * @param resultTable
	 * @param monitor
	 */
	public void update(ResultTable resultTable, IProgressMonitor monitor) {
		monitor.subTask("Update TimeCost with the given ResultTable Object");

		// recalculate all time inforamtion
		int size = 0;
		int totalSize = 0;
		long analysisTime = 0;
		long preparingTime = 0;
		long loadingTime = 0;
		long executingTime = 0;
		long pureExecutingTime = 0;
		long timeOutExecutingTime = 0;
		long comparingTime = 0;
		long etcTime = 0;
		// long totalTime = 0;

		List<TestResultForOperator> tMutant = new ArrayList<TestResultForOperator>();
		List<TestResultForOperator> cMutant = new ArrayList<TestResultForOperator>();

		List<TestResultForOperator> moResults = resultTable
				.getAllMutantOperatorResults();
		for (TestResultForOperator moResult : moResults) {
			size += moResult.getKilledMutantSize();
			totalSize += moResult.getTotalSize();
			analysisTime += moResult.getAnalysisTime();
			preparingTime += moResult.getPreparingTime();
			loadingTime += moResult.getLoadingTime();
			executingTime += moResult.getExecutingTime();
			pureExecutingTime += moResult.getExecutingTime(false);
			timeOutExecutingTime += moResult.getExecutingTime(true);
			comparingTime += moResult.getComparingTime();
			// totalTime += moResult.getTotalTime();
			etcTime += moResult.getEtcTime();

			boolean flag = false;
			for (String tOp : MutantOperator
					.getAllTraditionalOperators()) {
				if (tOp.equals(moResult.getMutantOperator())) {
					tMutant.add(moResult);
					flag = true;
					break;
				}
			}
			if (!flag)
				for (String cOp : MutantOperator.getAllClassicalOperators()) {
					if (cOp.equals(moResult.getMutantOperator())) {
						cMutant.add(moResult);
						break;
					}
				}
		}

		// show whore time cost
		long totalTime = comparingTime + executingTime + loadingTime
				+ analysisTime + preparingTime;
		totalTimeCost.setText(Long.toString(totalTime) + "   ");
		totalAnalysisTimeCost.setText(Long.toString(analysisTime) + "   ");
		totalPreparingTimeCost.setText(Long.toString(preparingTime) + "   ");
		totalLoadingTimeCost.setText(Long.toString(loadingTime) + "   ");
		totalExecutingTimeCost.setText(Long.toString(executingTime) + "   ");
		totalComparingTimeCost.setText(Long.toString(comparingTime) + "   ");
		// totalETCTimeCost.setText(Long.toString(etcTime) + "   ");

		// sizeOfExecutedMutant.setText(Long.toString(size)+" ");
		actualExecutionTime
				.setText(Long.toString(resultTable.getActualTime() / 1000000)
						+ "   ");
		originalExecutionTime.setText(Long.toString(resultTable
				.getOriginalTimeCost()) + "   ");

		if (size == 0)
			size = 1;

		DecimalFormat format = new DecimalFormat("#0.##   ");
		avgTimeCost.setText(format.format(1.0 * totalTime / totalSize));
		avgPreparingTimeCost.setText(format.format(1.0 * preparingTime
				/ totalSize));
		avgLoadingTimeCost
				.setText(format.format(1.0 * loadingTime / totalSize));
		avgExecutingTimeCost.setText(format.format(1.0 * executingTime
				/ totalSize));
		avgComparingTimeCost.setText(format.format(1.0 * comparingTime
				/ totalSize));
		avgETCTimeCost.setText(format.format(1.0 * etcTime / totalSize));

		tMutantTimeViewer.setInput(tMutant);
		cMutantTimeViewer.setInput(cMutant);
		monitor.worked(1);
	}

}

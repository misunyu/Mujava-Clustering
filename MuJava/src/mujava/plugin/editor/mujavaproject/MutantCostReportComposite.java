package mujava.plugin.editor.mujavaproject;

import java.text.DecimalFormat;

import kaist.selab.util.Helper;
import mujava.gen.PerformanceElement;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class MutantCostReportComposite extends Composite {

	DecimalFormat format = new DecimalFormat("####0.00");

	private TreeViewer generationResultTreeViewer;

	private TreeViewer generatedMutantTreeViewer;

	private Label sizeOfMutants;

	private Label time;

	private Label avgTime;

	private Label diskSpace;

	private Label avgDiskSpace;

	private Label memorySpace;

	private Label avgMemorySpace;

	private Label firstDate;

	private Label lastDate;

	private GridData topElementGridData1;

	private GridData topElementGridData3;

	private Label flabel;

	private Label llabel;

	private Label sizeOfTargets;

	private Label analysisTime;

	private Label analysisDisk;

	private Label analysisMem;

	private Label avgAnalysisTime;

	private Label avgAnalysisDisk;

	private Label avgAnalysisMem;

	private Label prepareTime;

	private Label averPreparingTime;

	public MutantCostReportComposite(Composite parent, int style) {
		super(parent, style);

		// set components & its color
		Color whiteColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.setBackground(whiteColor);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setLayout(new FormLayout());

		FormData upperFromData = new FormData();
		upperFromData.top = new FormAttachment(0, 10);
		upperFromData.left = new FormAttachment(0, 10);
		upperFromData.bottom = new FormAttachment(50, -5);
		upperFromData.right = new FormAttachment(100, -5);

		FormData lowerFormData = new FormData();
		lowerFormData.top = new FormAttachment(50, 10);
		lowerFormData.left = new FormAttachment(0, 10);
		lowerFormData.bottom = new FormAttachment(100, -5);
		lowerFormData.right = new FormAttachment(100, -5);

		GridData rightGridData = new GridData(GridData.FILL_HORIZONTAL);
		rightGridData.verticalAlignment = SWT.FILL;
		GridData leftGridData = new GridData();
		leftGridData.verticalAlignment = SWT.FILL;
		leftGridData.grabExcessVerticalSpace = true;
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;

		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(upperFromData);

		Composite leftComposite = new Composite(topComposite, SWT.NONE);
		leftComposite.setLayout(new GridLayout(1, false));
		leftComposite.setLayoutData(leftGridData);
		final Composite rightComposite = new Composite(topComposite, SWT.NONE);
		rightComposite.setLayout(new GridLayout(4, false));
		rightComposite.setLayoutData(rightGridData);

		Composite bottomComposite = new Composite(this, SWT.NONE);
		bottomComposite.setLayoutData(lowerFormData);
		bottomComposite.setLayout(new GridLayout(1, false));

		CLabel titleLabel = new CLabel(leftComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Generation Results");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 60, 80 });
		titleLabel.setLayoutData(gridData1);

		generationResultTreeViewer = new TreeViewer(leftComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);

		GridData tableGridData = new GridData(GridData.FILL_HORIZONTAL);
		tableGridData.horizontalSpan = 1;
		tableGridData.verticalAlignment = GridData.FILL;
		tableGridData.grabExcessVerticalSpace = true;

		Tree tempTree = generationResultTreeViewer.getTree();
		tempTree.setLayoutData(tableGridData);
		tempTree.setHeaderVisible(true);
		tempTree.setLinesVisible(true);

		TreeColumn column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Build Date");
		column.setWidth(120);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Portion");
		column.setWidth(50);
		generationResultTreeViewer
				.setContentProvider(new MutantCostContentProvider());
		generationResultTreeViewer
				.setLabelProvider(new MutantCostReportLableProvider());
		generationResultTreeViewer.setAutoExpandLevel(2);

		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		gridData3.horizontalSpan = 3;
		GridData gridData4 = new GridData(GridData.FILL_HORIZONTAL);
		gridData4.horizontalSpan = 4;
		titleLabel = new CLabel(rightComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Selected Result");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 30, 45 });
		titleLabel.setLayoutData(gridData4);

		topElementGridData1 = new GridData();
		topElementGridData1.exclude = true;
		topElementGridData3 = new GridData();
		topElementGridData3.exclude = true;
		topElementGridData3.horizontalSpan = 3;

		flabel = new Label(rightComposite, SWT.LEFT);
		flabel.setText("First Generated Date :");
		flabel.setLayoutData(topElementGridData1);
		firstDate = new Label(rightComposite, SWT.LEFT);
		firstDate.setLayoutData(topElementGridData3);

		llabel = new Label(rightComposite, SWT.LEFT);
		llabel.setText("Last Generated Date :");
		llabel.setLayoutData(topElementGridData1);
		lastDate = new Label(rightComposite, SWT.LEFT);
		lastDate.setLayoutData(topElementGridData3);

		Label label = new Label(rightComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(gridData4);

		label = new Label(rightComposite, SWT.LEFT);
		label.setText("# of Target source files :");
		sizeOfTargets = new Label(rightComposite, SWT.LEFT);
		sizeOfTargets.setLayoutData(gridData3);

		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Prepare Time :");
		prepareTime = new Label(rightComposite, SWT.LEFT);
		prepareTime.setLayoutData(gridData1);

		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Aveg. Prepare Time :");
		averPreparingTime = new Label(rightComposite, SWT.LEFT);
		averPreparingTime.setLayoutData(gridData1);

		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Analysis Time :");
		analysisTime = new Label(rightComposite, SWT.LEFT);
		analysisTime.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. Analysis Time :");
		avgAnalysisTime = new Label(rightComposite, SWT.LEFT);
		avgAnalysisTime.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Analysis Disk :");
		analysisDisk = new Label(rightComposite, SWT.LEFT);
		analysisDisk.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. analysis Disk :");
		avgAnalysisDisk = new Label(rightComposite, SWT.LEFT);
		avgAnalysisDisk.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Analysis Mem :");
		analysisMem = new Label(rightComposite, SWT.LEFT);
		analysisMem.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. Analysis Mem :");
		avgAnalysisMem = new Label(rightComposite, SWT.LEFT);
		avgAnalysisMem.setLayoutData(gridData1);

		label = new Label(rightComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(gridData4);

		label = new Label(rightComposite, SWT.LEFT);
		label.setText("# of Generated Mutants :");
		sizeOfMutants = new Label(rightComposite, SWT.LEFT);
		sizeOfMutants.setLayoutData(gridData3);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Generation Time :");
		time = new Label(rightComposite, SWT.LEFT);
		time.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. Generation Time :");
		avgTime = new Label(rightComposite, SWT.LEFT);
		avgTime.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Used Disk Space :");
		diskSpace = new Label(rightComposite, SWT.LEFT);
		diskSpace.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. Used Disk :");
		avgDiskSpace = new Label(rightComposite, SWT.LEFT);
		avgDiskSpace.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Used Memory Space :");
		memorySpace = new Label(rightComposite, SWT.LEFT);
		memorySpace.setLayoutData(gridData1);
		label = new Label(rightComposite, SWT.LEFT);
		label.setText("Avg. Used Memory :");
		avgMemorySpace = new Label(rightComposite, SWT.LEFT);
		avgMemorySpace.setLayoutData(gridData1);

		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;

		titleLabel = new CLabel(bottomComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Generated Result for each Mutant Operators");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 30, 45 });
		titleLabel.setLayoutData(gridData1);

		generatedMutantTreeViewer = new TreeViewer(bottomComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		generatedMutantTreeViewer
				.setContentProvider(new OperatorCostReportContentProvider());
		generatedMutantTreeViewer
				.setLabelProvider(new OperatorCostReportLabelProvider());
		generatedMutantTreeViewer.setAutoExpandLevel(2);
		tempTree = generatedMutantTreeViewer.getTree();
		tempTree.setLayoutData(gridData2);
		tempTree.setHeaderVisible(true);
		tempTree.setLinesVisible(true);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Mutant Operator");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Generated Type");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Time ");
		column.setWidth(80);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Disk Space");
		column.setWidth(80);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Memory Space");
		column.setWidth(80);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("# of Generated Mutants");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.CENTER);
		column.setText("Portion");
		column.setWidth(80);

		generationResultTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						assert (event.getSource() instanceof TreeViewer);
						TreeViewer viewer = (TreeViewer) event.getSource();
						ITreeSelection sec = (ITreeSelection) viewer
								.getSelection();
						Object obj = sec.getFirstElement();
						if (obj instanceof MutantCostReport) {
							MutantCostReport gResult = (MutantCostReport) obj;
							PerformanceElement temp = gResult.getActualCost();
							int mSize = gResult.getSizeOfMutants();
							int tSize = gResult.getSizeOfTargets();
							updateResultInfo(mSize, tSize, temp, gResult
									.getAnalysisCost(), gResult
									.getCalculatedCost(), gResult
									.getPreparingCost());

							if (gResult.isCollected()) {
								topElementGridData1.exclude = false;
								topElementGridData3.exclude = false;
								if (gResult.getSizeOfMutants() == 0) {
									firstDate.setText("-");
									lastDate.setText("-");
								} else {
									firstDate.setText(Helper
											.long2DateString(gResult
													.getBuildTime()));
									lastDate.setText(Helper
											.long2DateString(gResult
													.getSecondBuildTime()));
								}
							} else {
								topElementGridData1.exclude = true;
								topElementGridData3.exclude = true;
							}
							flabel.setVisible(!topElementGridData1.exclude);
							llabel.setVisible(!topElementGridData1.exclude);
							firstDate.setVisible(!topElementGridData3.exclude);
							lastDate.setVisible(!topElementGridData3.exclude);
							rightComposite.layout();

							generatedMutantTreeViewer.setInput(gResult);
							generatedMutantTreeViewer.setAutoExpandLevel(2);
						}
					}
				});
	}

	private void updateResultInfo(int mSize, int tSize,
			PerformanceElement actual, PerformanceElement analysis,
			PerformanceElement calculated, PerformanceElement preparing) {
		sizeOfMutants.setText(Integer.toString(mSize));
		sizeOfTargets.setText(Integer.toString(tSize));

		prepareTime.setText(Helper.long2TimeString(preparing.getTimeCost()));

		time.setText(Helper.long2TimeString(actual.getTimeCost()));
		diskSpace.setText(Helper.long2SpaceString(actual.getDiskCost()));
		memorySpace.setText(Helper.long2SpaceString(actual.getMemoryCost()));

		analysisTime.setText(Helper.long2TimeString(analysis.getTimeCost()));
		analysisDisk.setText(Helper.long2SpaceString(analysis.getDiskCost()));
		analysisMem.setText(Helper.long2SpaceString(analysis.getMemoryCost()));

		if (tSize == 0)
			tSize = 1;
		if (mSize == 0)
			mSize = 1;

		averPreparingTime.setText(Helper.double2TimeString(1.0
				* preparing.getTimeCost() / tSize));
		avgAnalysisTime.setText(Helper.double2TimeString(1.0
				* analysis.getTimeCost() / tSize));
		avgAnalysisDisk.setText(Helper.double2SpaceString(1.0
				* analysis.getDiskCost() / tSize));
		avgAnalysisMem.setText(Helper.double2SpaceString(1.0
				* analysis.getMemoryCost() / tSize));

		avgTime.setText(Helper.double2TimeString(1.0 * actual.getTimeCost()
				/ mSize));
		avgDiskSpace.setText(Helper.double2SpaceString(1.0
				* actual.getDiskCost() / mSize));
		avgMemorySpace.setText(Helper.double2SpaceString(1.0
				* actual.getMemoryCost() / mSize));

	}

	public void updateTable(MutantCostReportTable gTable) {
		generationResultTreeViewer.setInput(gTable);
		generationResultTreeViewer.setAutoExpandLevel(2);

		update();
	}
}

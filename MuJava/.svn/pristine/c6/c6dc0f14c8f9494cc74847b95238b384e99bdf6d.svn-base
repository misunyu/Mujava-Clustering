package mujava.plugin.editor.mutantreport;

import java.text.DecimalFormat;
import java.util.List;

import kaist.selab.util.Helper;
import mujava.MuJavaProject;
import mujava.MutantResult;
import mujava.ResultTable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class GeneralReportInfoComposite extends Composite {

	private Label buildDate;
	private Label score;
	private Label timeCost;
	private Label sizeOfMutants;
	private Label usedMemorySpace;
	private DecimalFormat scoreFormat = new DecimalFormat("####0.00");
	private Label sizeOfOverallTestCases;
	private Label sizeOfExecutedTestCases;
	private Label usedDiskSpace;
	private Label projectFileName;
	private Label reachTableFileName;
	private Label cmMode;

	public GeneralReportInfoComposite(Composite parent, int style) {
		super(parent, style);

		// set components & its color
		Color whiteColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.setBackground(whiteColor);
		this.setBackgroundMode(SWT.INHERIT_FORCE);

		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;

		this.setLayout(new GridLayout(1, false));
		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(gridData1);

		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;

		CLabel titleLabel = new CLabel(topComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Report Information");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 30, 45 });
		titleLabel.setLayoutData(gridData2);

		Label label = new Label(topComposite, SWT.NONE);
		label.setText("Build Date :");
		buildDate = new Label(topComposite, SWT.BORDER);
		buildDate.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Mutant Score :");
		score = new Label(topComposite, SWT.BORDER);
		score.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Total Executing Time :");
		timeCost = new Label(topComposite, SWT.BORDER);
		timeCost.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("# of Executed Mutants :");
		sizeOfMutants = new Label(topComposite, SWT.BORDER);
		sizeOfMutants.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("# of Executed TestCases :");
		sizeOfOverallTestCases = new Label(topComposite, SWT.BORDER);
		sizeOfOverallTestCases.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("# of Executed TestCases :");
		sizeOfExecutedTestCases = new Label(topComposite, SWT.BORDER);
		sizeOfExecutedTestCases.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Used Disk spaces (Byte) :");
		usedDiskSpace = new Label(topComposite, SWT.BORDER);
		usedDiskSpace.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Used memeory spaces (Byte) :");
		usedMemorySpace = new Label(topComposite, SWT.BORDER);
		usedMemorySpace.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Used MuJava Project :");
		projectFileName = new Label(topComposite, SWT.BORDER);
		projectFileName.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("Used Reach Table:");
		reachTableFileName = new Label(topComposite, SWT.BORDER);
		reachTableFileName.setLayoutData(gridData1);

		label = new Label(topComposite, SWT.NONE);
		label.setText("CM Mode:");
		cmMode = new Label(topComposite, SWT.BORDER);
		cmMode.setLayoutData(gridData1);
	}

	public void update(ResultTable resultTable, IProgressMonitor monitor) {
		if (monitor != null)
			monitor
					.subTask("Update TestCaseComposite with the given ResultTable Object");

		long startTime = resultTable.getBuildDate();
		buildDate.setText(Helper.long2DateString(startTime));

		List<String> ops = resultTable.getAllMutantOperators();

		int total = 0;
		int killed = 0;
		for (String op : ops) {
			List<MutantResult> results = resultTable
					.getMutantResultByOperator(op);
			for (MutantResult result : results) {
				total++;
				if (result.isKilled())
					killed++;
			}
		}
		double value = (total == 0) ? 1.0 * killed : 1.0 * killed / total;
		score.setText(scoreFormat.format(value * 100) + " ( " + killed + "/"
				+ total + ")");

		long time = resultTable.getActualTime();
		timeCost.setText(Helper.long2TimeString(time));

		sizeOfMutants.setText(Integer.toString(total));

		int tSize = resultTable.getOverallTestCases().size();
		sizeOfOverallTestCases.setText(Integer.toString(tSize));
		tSize = resultTable.getExecutedTestCases().size();
		sizeOfExecutedTestCases.setText(Integer.toString(tSize));

		usedDiskSpace.setText(Helper.long2SpaceString(resultTable
				.getUsedDiskSpace()));
		usedMemorySpace.setText(Helper.long2SpaceString(resultTable
				.getUsedMemorySpace()));
		MuJavaProject prj = resultTable.getMuJavaProject();
		projectFileName.setText(prj.getFileName());
		reachTableFileName.setText(resultTable.getUsedReachTableName());

		cmMode.setText(Boolean.toString(resultTable.isCMMode()));

		if (monitor != null)
			monitor.worked(1);
	}
}

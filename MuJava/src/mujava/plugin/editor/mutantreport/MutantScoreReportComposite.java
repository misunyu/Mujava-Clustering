package mujava.plugin.editor.mutantreport;

import java.text.DecimalFormat;
import java.util.List;

import mujava.MutantResult;
import mujava.ResultTable;
import mujava.plugin.editor.mujavaproject.LiveMutantLabelProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class MutantScoreReportComposite extends Composite {

	private Label mutantScore;
	private Label executedMutant;
	private Label killedMutant;
	private TreeViewer killedMutantTreeViewer;
	private TreeViewer liveMutantTreeViewer;

	public MutantScoreReportComposite(Composite container, int style) {
		super(container, style);

		// set components & its color
		Color whiteColor = container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE);
		this.setBackground(whiteColor);
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		this.setLayout(new GridLayout(1, false));

		// make composites as initial area
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;

		Composite topComposite = new Composite(this, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(gridData1);

		CLabel titleLabel = new CLabel(topComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Mutant Score");
		titleLabel.setBackground(new Color[] {
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND),
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				container.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 30, 45 });
		titleLabel.setLayoutData(gridData);

		Label label = new Label(topComposite, SWT.LEFT);
		label.setText("Score : ");
		mutantScore = new Label(topComposite, SWT.RIGHT);
		label = new Label(topComposite, SWT.LEFT);
		label.setText("# of Killed Mutants : ");
		killedMutant = new Label(topComposite, SWT.RIGHT);

		label = new Label(topComposite, SWT.LEFT);
		label.setText("# of Executed Mutants : ");
		executedMutant = new Label(topComposite, SWT.RIGHT);

		// ///////////////////////////////////
		Composite bottomComposite = new Composite(this, SWT.None);
		bottomComposite.setLayoutData(gridData2);
		bottomComposite.setLayout(new FormLayout());

		FormData upperFromData = new FormData();
		upperFromData.top = new FormAttachment(0, 0);
		upperFromData.left = new FormAttachment(0, 0);
		upperFromData.bottom = new FormAttachment(50, 0);
		upperFromData.right = new FormAttachment(100, 0);

		Composite kComposite = new Composite(bottomComposite, SWT.NONE);
		kComposite.setLayout(new GridLayout(1, false));
		kComposite.setLayoutData(upperFromData);

		FormData lowerFormData = new FormData();
		lowerFormData.top = new FormAttachment(50, 0);
		lowerFormData.left = new FormAttachment(0, 0);
		lowerFormData.bottom = new FormAttachment(100, 0);
		lowerFormData.right = new FormAttachment(100, 0);

		Composite lComposite = new Composite(bottomComposite, SWT.NONE);
		lComposite.setLayout(new GridLayout(1, false));
		lComposite.setLayoutData(lowerFormData);

		titleLabel = new CLabel(kComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Killed Mutants");
		titleLabel.setBackground(new Color[] {
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND),
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				container.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 60, 80 });
		gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		titleLabel.setLayoutData(gridData1);

		gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		killedMutantTreeViewer = new TreeViewer(kComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		Tree tempTree = killedMutantTreeViewer.getTree();
		tempTree.setLayoutData(gridData2);
		tempTree.setHeaderVisible(true);
		tempTree.setLinesVisible(true);
		TreeColumn column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Mutant");
		column.setWidth(160);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("TestCase");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("OriginalResult");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("MutantResult");
		column.setWidth(100);
		column = new TreeColumn(tempTree, SWT.CENTER);
		column.setText("Killed/Total");
		column.setWidth(80);
		tempTree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				Tree tree = (Tree) event.widget;
				GC gc = event.gc;
				Color foreground = gc.getForeground();
				Color background = gc.getBackground();

				TreeItem item = (TreeItem) event.item;
				TreeItem parentItem = item.getParentItem();
				if (parentItem == null && event.index == 4) {
					TreeColumn scoreColumn = tree.getColumn(4);
					TestResultForOperator moResult = (TestResultForOperator) item
							.getData();

					if (moResult.getKilledScore() == 1) {
						gc.setForeground(tree.getDisplay().getSystemColor(
								SWT.COLOR_DARK_GREEN));
						gc.setBackground(tree.getDisplay().getSystemColor(
								SWT.COLOR_GREEN));
					} else {
						gc.setForeground(tree.getDisplay().getSystemColor(
								SWT.COLOR_RED));
						gc.setBackground(tree.getDisplay().getSystemColor(
								SWT.COLOR_YELLOW));
					}

					int width = (int) ((scoreColumn.getWidth() - 1) * moResult
							.getKilledScore());
					gc.fillGradientRectangle(event.x, event.y, width,
							event.height, true);
					Rectangle rect2 = new Rectangle(event.x, event.y,
							width - 1, event.height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(tree.getDisplay().getSystemColor(
							SWT.COLOR_LIST_FOREGROUND));
					String text = moResult.getKilledScoreString();
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (event.height - size.y) / 2);

					int x = Math.max(5, (scoreColumn.getWidth() - size.x) / 2);
					gc.drawText(text, event.x + x, event.y + offset, true);

				}

				gc.setForeground(background);
				gc.setBackground(foreground);
			}
		});

		killedMutantTreeViewer.setAutoExpandLevel(3);
		killedMutantTreeViewer
				.setContentProvider(new MutantScoreProvider(true));
		killedMutantTreeViewer
				.setLabelProvider(new KilledMutantLabelProvider());
		killedMutantTreeViewer.setSorter(new MutantResultSorter());

		titleLabel = new CLabel(lComposite, SWT.LEFT | SWT.SMOOTH);
		titleLabel.setForeground(container.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("Live Mutants");
		titleLabel.setBackground(new Color[] {
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND),
				container.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				container.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 60, 80 });
		titleLabel.setLayoutData(gridData1);
		liveMutantTreeViewer = new TreeViewer(lComposite, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		liveMutantTreeViewer.setSorter(new MutantResultSorter());
		tempTree = liveMutantTreeViewer.getTree();
		tempTree.setLayoutData(gridData2);
		tempTree.setHeaderVisible(true);
		tempTree.setLinesVisible(true);
		column = new TreeColumn(tempTree, SWT.LEFT);
		column.setText("Mutant");
		column.setWidth(160);
		column = new TreeColumn(tempTree, SWT.CENTER);
		column.setText("Lived/Total");
		column.setWidth(80);
		tempTree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				Tree tree = (Tree) event.widget;
				GC gc = event.gc;
				Color foreground = gc.getForeground();
				Color background = gc.getBackground();

				TreeItem item = (TreeItem) event.item;
				TreeItem parentItem = item.getParentItem();
				if (parentItem == null && event.index == 1) {
					TreeColumn scoreColumn = tree.getColumn(1);
					TestResultForOperator moResult = (TestResultForOperator) item
							.getData();

					if (moResult.getLiveScore() == 1) {
						gc.setForeground(tree.getDisplay().getSystemColor(
								SWT.COLOR_DARK_RED));
						gc.setBackground(tree.getDisplay().getSystemColor(
								SWT.COLOR_RED));
					} else {
						gc.setForeground(tree.getDisplay().getSystemColor(
								SWT.COLOR_RED));
						gc.setBackground(tree.getDisplay().getSystemColor(
								SWT.COLOR_YELLOW));
					}

					int width = (int) ((scoreColumn.getWidth() - 1) * moResult
							.getLiveScore());
					gc.fillGradientRectangle(event.x, event.y, width,
							event.height, true);
					Rectangle rect2 = new Rectangle(event.x, event.y,
							width - 1, event.height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(tree.getDisplay().getSystemColor(
							SWT.COLOR_LIST_FOREGROUND));
					String text = moResult.getLiveScoreString();
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (event.height - size.y) / 2);

					int x = Math.max(5, (scoreColumn.getWidth() - size.x) / 2);
					gc.drawText(text, event.x + x, event.y + offset, true);

				}

				gc.setForeground(background);
				gc.setBackground(foreground);
			}
		});
		liveMutantTreeViewer.setAutoExpandLevel(3);
		liveMutantTreeViewer.setContentProvider(new MutantScoreProvider(false));
		liveMutantTreeViewer.setLabelProvider(new LiveMutantLabelProvider());
		// liveMutantTreeViewer.setSorter(new MutantResultSorter());

	}

	public void setMutantScore(int total, int killed) {
		assert (killed <= total && total >= 0 && killed >= 0);

		double score = (total != 0) ? 1.0 * killed / total * 100 : 0;
		DecimalFormat format = new DecimalFormat("####0.00");
		DecimalFormat intFormat = new DecimalFormat("#######0");
		mutantScore.setText(format.format(score));
		killedMutant.setText(intFormat.format(killed));
		executedMutant.setText(intFormat.format(total));
	}

	/**
	 * It has a sub-task (1 worked)
	 * 
	 * @param rTable
	 * @param monitor
	 */
	public void update(ResultTable rTable, IProgressMonitor monitor) {
		monitor
				.subTask("Update ResultComposite with the given ResultTable Object");

		List<String> ops = rTable.getAllMutantOperators();

		int total = 0;
		int killed = 0;
		for (String op : ops) {
			List<MutantResult> results = rTable.getMutantResultByOperator(op);
			for (MutantResult result : results) {
				if (!result.getMutantID().isEmpty()) {
					total++;
					if (result.isKilled())
						killed++;
				}
			}
		}

		this.setMutantScore(total, killed);
		killedMutantTreeViewer.setInput(rTable);
		liveMutantTreeViewer.setInput(rTable);
		monitor.worked(1);
	}
}

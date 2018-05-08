package mujava.plugin.editor.mujavaproject;

import java.util.List;

import mujava.MuJavaMutantInfo;
import mujava.MutantTable;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public abstract class MutantViewerComposite extends Composite {

	// 좌측의 생성에 사용된 MutantOperator들을 보여주는 swt Table UI
	private Table mutantOperatorTable;
	// 우측 상단의 생성된 Mutant들을 나열하는 List
	// protected ListViewer mutantListViewer;
	TreeViewer mutantTreeViewer;
	// MutantList에 보여질 List를 관리하는 ContentProvider
	protected MutantListContentProvider contentProvider = new MutantListContentProvider();
	// MutantList의 내용에 filtering을 지원할 Filter
	final protected MutantOperatorViewerFilter filter = new MutantOperatorViewerFilter();
	// 화면에 표시될 총 mutant의 갯수.
	private Text numMutant;
	// 우측 하단의 선택된 mutant의 정보가 표시되는 UI
	private MutantDescriptionComposite mutantDescriptor;

	MutantTable mutantTable = null;

	// 상속된 자식들이 직접 사용하는 MutationOperator를 반환한다.
	// 되도록 soft한 순서로 반환한다.
	abstract List<String> getMutationOperators();

	public MutantViewerComposite(Composite parent, int style) {
		super(parent, style);

		this.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		FormLayout layout = new FormLayout();
		this.setLayout(layout);

		GridLayout leftSideLayout = new GridLayout();
		leftSideLayout.numColumns = 2;
		GridLayout rightSideLayout = new GridLayout();

		FormData formLeft = new FormData();
		formLeft.top = new FormAttachment(0, 10);
		formLeft.left = new FormAttachment(0, 10);
		formLeft.bottom = new FormAttachment(100, -10);
		formLeft.right = new FormAttachment(30, -10);

		Composite leftSide = new Composite(this, SWT.NONE);
		leftSide.setLayoutData(formLeft);
		leftSide.setLayout(leftSideLayout);
		leftSide.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		FormData formRight = new FormData();
		formRight.top = new FormAttachment(0, 10);
		formRight.left = new FormAttachment(leftSide, 10);
		formRight.bottom = new FormAttachment(60, -10);
		formRight.right = new FormAttachment(100, -10);

		Composite rightSide = new Composite(this, SWT.NONE);
		rightSide.setLayout(rightSideLayout);
		rightSide.setLayoutData(formRight);
		rightSide.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		CLabel titleLabel = new CLabel(leftSide, SWT.LEFT);
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setText("# of Mutants");
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 75, 100 });
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		titleLabel.setLayoutData(gridData);

		Label label = new Label(leftSide, SWT.NONE);
		label.setText("Total : ");
		label.setBackground(this.getBackground());
		numMutant = new Text(leftSide, SWT.NONE);
		numMutant.setEditable(false);
		numMutant.setBackground(this.getBackground());
		numMutant.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mutantOperatorTable = new Table(leftSide, SWT.CHECK
				| SWT.HIDE_SELECTION | SWT.BORDER);
		mutantOperatorTable.setHeaderVisible(true);
		mutantOperatorTable.setLinesVisible(true);
		GridData tableGridData = new GridData(GridData.FILL_BOTH);
		tableGridData.horizontalSpan = 2;
		mutantOperatorTable.setLayoutData(tableGridData);
		mutantOperatorTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = ((TableItem) e.item);
				boolean status = item.getChecked();
				String op = item.getText(1);

				if (status == true) {
					filter.addMutantOperator(op);
				} else {
					filter.deleteMutantOperator(op);
				}

				// mutantListViewer.refresh(true);
				mutantTreeViewer.refresh(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		TableColumn column = new TableColumn(mutantOperatorTable, SWT.LEFT);
		column.setText(" ");
		column.setWidth(15);
		column = new TableColumn(mutantOperatorTable, SWT.CENTER);
		column.setText("Operator");
		column.setWidth(80);
		column = new TableColumn(mutantOperatorTable, SWT.CENTER);
		column.setText("Nums");
		column.setWidth(50);

		// Table의 content를 초기화 한다.
		for (String op : getMutationOperators()) {
			TableItem item = new TableItem(mutantOperatorTable, SWT.NONE);
			item.setText(new String[] { "", op, "0" });
			item.setChecked(true);
		}

		titleLabel = new CLabel(rightSide, SWT.LEFT);
		titleLabel.setText("Generated Mutants");
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 75, 100 });
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mutantTreeViewer = new TreeViewer(rightSide, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		Tree tempTree = mutantTreeViewer.getTree();
		tempTree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tempTree.setLinesVisible(true);

		mutantTreeViewer.setAutoExpandLevel(2);
		mutantTreeViewer.setSorter(new MutantSorter());
		mutantTreeViewer.setContentProvider(contentProvider);
		mutantTreeViewer.setLabelProvider(new ILabelProvider() {
			ILabelProvider javaProvider = new JavaElementLabelProvider(
					JavaElementLabelProvider.SHOW_DEFAULT
							| JavaElementLabelProvider.SHOW_QUALIFIED);
			Image mutantIcon = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/brkpi_obj.gif").createImage();

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
				mutantIcon.dispose();
				javaProvider.dispose();
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

			// File이름에서는 다른 그림을 그려주고, Mutant자체에서는 기본 아이콘을 그려준다.
			public Image getImage(Object element) {
				if (element instanceof String) {
					return mutantIcon;
				}
				return javaProvider.getImage(element);
			}

			// File이름을 보여줄때만 뒤에 현재 갯수를 보여준다.
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				if (element instanceof IJavaElement) {
					IJavaElement javaElement = (IJavaElement) element;

					int count = 0;
					Object[] IDs = contentProvider.getChildren(element);
					for (Object obj : IDs) {
						if (filter.select(mutantTreeViewer, element, obj))
							count++;
					}
					
					//  changed by ysma at 2013/10/18
					// 패키지가 다르지만 파일이름이 같은 경우 구분하기 위해 추가
					if(javaElement.getParent().getElementName().length()>0){		
						return javaElement.getParent().getElementName()+"."+javaElement.getElementName() + " (" + count
								+ " mutants)";
					} else{
						return javaElement.getElementName() + " (" + count
						+ " mutants)";			
					}
					
				}
				assert (false);

				return "";
			}
		});

		mutantTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) event
								.getSelection();

						MuJavaMutantInfo info = null;

						Object selectedItem = selection.getFirstElement();
						if (selectedItem instanceof String) {
							String mutantID = (String) selectedItem;
							if (mutantTable != null)
								info = mutantTable.getMutantInfo(mutantID);
						}

						// show selected mutant information
						mutantDescriptor.setMutantInfo(info);
					}
				});
		mutantTreeViewer.addFilter(filter);

		FormData formInfo = new FormData();
		formInfo.top = new FormAttachment(rightSide, 10);
		formInfo.left = new FormAttachment(leftSide, 10);
		formInfo.bottom = new FormAttachment(100, -10);
		formInfo.right = new FormAttachment(100, -10);

		Composite bottomSide = new Composite(this, SWT.NONE);
		bottomSide.setLayout(new GridLayout());
		bottomSide.setLayoutData(formInfo);
		bottomSide.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		titleLabel = new CLabel(bottomSide, SWT.LEFT);
		titleLabel.setText("Mutant Information");
		titleLabel.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		titleLabel.setBackground(new Color[] {
				parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				parent.getDisplay().getSystemColor(
						SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
				parent.getDisplay().getSystemColor(SWT.COLOR_WHITE) },
				new int[] { 75, 100 });
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		mutantDescriptor = new MutantDescriptionComposite(bottomSide,
				SWT.BORDER);
		mutantDescriptor.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setTotalCount(int total) {
		this.numMutant.setText(String.valueOf(total));
	}

	// project의 내용 일부가 제거되거나 변경되었을때, 다시 읽어들인다.
	// TODO 해당 mutanttable에 영향이 있는 경우에만 다시 읽어들이도록 변경해야..
	public void update(MutantTable mutantTable) {
		if (mutantTable == null)
			return;

		// List<MuJavaMutantInfo> list = new ArrayList<MuJavaMutantInfo>();
		int totalNumOfMutant = 0;

		// table에 등록된 mutation operator를 대해서 순차적으로 Mutant의 정보를 update한다.
		for (int i = 0; i < mutantOperatorTable.getItemCount(); i++) {

			TableItem item = mutantOperatorTable.getItem(i);
			String operator = item.getText(1);
			// 해당 Mutaion Operator에 해당하는 mutant의 생성갯수를 찾는다.
			int numOfMutants = mutantTable.getMutantCount(operator);

			// table의 row에 생성된 Mutant의 갯수를 지정한다.
			item.setText(2, String.valueOf(numOfMutants));

			// 동시에 전체 갯수를 구하기 위해 누적한다.
			totalNumOfMutant += numOfMutants;

			// 해당 Mutant operator에 대해 생성된 mutant가 존재하는 경우 MutantList에 추가한다.
			// if (numOfMutants != 0) {
			// MuJavaMutantInfo[] infos = mutantTable.getMutantInfos(operator);
			// list.addAll(Arrays.asList(infos));
			// }
			// 가능한 operator에 대해서 MutantList에 등록한다.
			filter.addMutantOperator(operator);
		}

		setTotalCount(totalNumOfMutant);

		mutantTreeViewer.setInput(mutantTable);
		this.mutantTable = mutantTable;

		update();
	}
}

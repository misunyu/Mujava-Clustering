package mujava.plugin.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.MuJavaProject;
import mujava.MutantOperator;
import mujava.gen.GenerationType;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MutantOperatorPage extends WizardPage {
	private MuJavaProject muProject;
	CheckboxTableViewer traditionalOperatorViewer;
	CheckboxTableViewer classOperatorViewer;
	List<String> selectedTraditonalOpeator;
	List<String> selectedClassOpeator;
	Map<String, Integer> occurence = new HashMap<String, Integer>();

	private IStructuredContentProvider classContentProvider = new IStructuredContentProvider() {
		Object[] ops = null;

		@Override
		public Object[] getElements(Object inputElement) {
			if (ops == null)
				return new Object[0];

			return ops;
		}

		@Override
		public void dispose() {
			ops = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof GenerationType) {
				GenerationType genType = (GenerationType) newInput;
				List<String> names = MutantOperator
						.getListClassicalOperators(genType);
				Collections.sort(names);
				ops = new Object[names.size()];
				for (int i = 0; i < names.size(); i++) {
					ops[i] = new Object[] { names.get(i), "0" };
				}
			} else if (newInput instanceof Map) {
				Map map = (Map) newInput;
				List<String> names = MutantOperator.getAllClassicalOperators();
				Collections.sort(names);
				ops = new Object[names.size()];
				for (int i = 0; i < names.size(); i++) {
					Object key = names.get(i);
					Object result = map.get(key);
					if (result != null)
						ops[i] = new Object[] { key, result.toString() };
					else
						ops[i] = new Object[] { key, "0" };
				}
			}
		}

	};
	private IContentProvider traditionalContentProvider = new IStructuredContentProvider() {
		Object[] ops = null;

		@Override
		public Object[] getElements(Object inputElement) {
			if (ops == null)
				return new String[0];

			return ops;
		}

		@Override
		public void dispose() {
			ops = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			if (newInput instanceof GenerationType) {
				GenerationType genType = (GenerationType) newInput;

				List<String> names = MutantOperator
						.getListTraditionalOperators(genType);

				ops = new Object[names.size()];
				for (int i = 0; i < names.size(); i++) {
					ops[i] = new Object[] { names.get(i), "0" };
				}
			} else if (newInput instanceof Map) {
				Map map = (Map) newInput;
				List<String> names = MutantOperator
						.getAllTraditionalOperators();

				ops = new Object[names.size()];
				for (int i = 0; i < names.size(); i++) {
					Object key = names.get(i);
					Object result = map.get(key);
					if (result != null)
						ops[i] = new Object[] { key, result.toString() };
					else
						ops[i] = new Object[] { key, "0" };
				}
			}
		}
	};;
	private ITableLabelProvider labelProvider = new ITableLabelProvider() {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Object[]) {
				Object[] objs = (Object[]) element;
				switch (columnIndex) {
				case 0:
					return objs[0].toString();
				case 1:
					return objs[1].toString();
				}
			}
			return "";
		}

		@Override
		public void addListener(ILabelProviderListener listener) {

		}

		@Override
		public void dispose() {

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	};
	private SelectionListener defaultSelectionListener = new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if ((e.detail & SWT.CHECK) == SWT.CHECK) {
				dialogChanged();
			}
		}
	};

	public MutantOperatorPage(ISelection selection) {
		super("wizardPage");
		setTitle("Select mutant opeators");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		GridData layoutGD = new GridData(GridData.FILL_BOTH);
		layoutGD.grabExcessVerticalSpace = true;
		Composite left = new Composite(container, SWT.NULL);
		left.setLayout(new GridLayout(2, true));
		left.setLayoutData(layoutGD);
		Composite right = new Composite(container, SWT.NULL);
		right.setLayout(new GridLayout(2, false));
		right.setLayoutData(layoutGD);

		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData tableGD = new GridData(GridData.FILL_BOTH);
		tableGD.horizontalSpan = 2;
		tableGD.grabExcessVerticalSpace = true;

		Label label = new Label(left, SWT.NONE);
		label.setLayoutData(gd2);
		label.setText("Traditional Mutant Operators");

		Table tradTable = makeTable(left);
		tradTable.setLayoutData(tableGD);
		tradTable.addSelectionListener(defaultSelectionListener);
		traditionalOperatorViewer = new CheckboxTableViewer(tradTable);
		traditionalOperatorViewer
				.setContentProvider(traditionalContentProvider);
		traditionalOperatorViewer.setLabelProvider(labelProvider);
		traditionalOperatorViewer.setInput(new Integer(MutantOperator.ALL));

		initButton(left, traditionalOperatorViewer);

		label = new Label(right, SWT.NONE);
		label.setLayoutData(gd2);
		label.setText("Class Mutant Operators");

		Table classTable = makeTable(right);
		classTable.setLayoutData(tableGD);
		classTable.addSelectionListener(defaultSelectionListener);
		classOperatorViewer = new CheckboxTableViewer(classTable);
		classOperatorViewer.setContentProvider(classContentProvider);
		classOperatorViewer.setLabelProvider(labelProvider);
		classOperatorViewer.setInput(new Integer(MutantOperator.ALL));

		initButton(right, classOperatorViewer);

		dialogChanged();
		setControl(container);
	}

	private void initButton(Composite parent, final CheckboxTableViewer viewer) {

		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 1;

		Button btnA = new Button(parent, SWT.PUSH);
		btnA.setLayoutData(gd1);
		btnA.setText("All");
		Button btnN = new Button(parent, SWT.PUSH);
		btnN.setText("None");
		btnN.setLayoutData(gd1);

		btnA.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnN.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private Table makeTable(Composite parent) {
		Table table = new Table(parent, SWT.CHECK | SWT.HIDE_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Setup common columns
		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setText("Operator");
		column.setWidth(95);

		column = new TableColumn(table, SWT.CENTER);
		column.setText("Occurence");
		column.setWidth(50);

		return table;
	}

	protected void dialogChanged() {
		selectedTraditonalOpeator = getSelectedTraditonalOpeator();
		selectedClassOpeator = getSelectedClassOpeator();

		if (selectedTraditonalOpeator != null
				&& selectedTraditonalOpeator.size() != 0) {
			updateStatus(null);
			return;
		}
		if (selectedClassOpeator != null && selectedClassOpeator.size() != 0) {
			updateStatus(null);
			return;
		}
		updateStatus("Select at least one mutant operator.");
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public List<String> getSelectedClassOpeator() {
		TableItem[] items = classOperatorViewer.getTable().getItems();
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked())
				v.add((String) items[i].getText());
		}
		return v;
	}

	public List<String> getSelectedTraditonalOpeator() {
		TableItem[] items = traditionalOperatorViewer.getTable().getItems();
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked())
				v.add((String) items[i].getText());
		}

		return v;
	}

	public void setMutantData(Map<String, Integer> tradMap,
			Map<String, Integer> clzMap) {
		traditionalOperatorViewer.setInput(tradMap);
		classOperatorViewer.setInput(clzMap);
		dialogChanged();
	}

	public MuJavaProject getMuJavaProject() {
		return this.muProject;
	}

	public void setMujavaProject(MuJavaProject muProject) {
		this.muProject = muProject;
	}

	public void setGenerationWay(GenerationType type) {
		if (traditionalOperatorViewer != null)
			traditionalOperatorViewer.setInput(type);

		if (classOperatorViewer != null)
			classOperatorViewer.setInput(type);
	}
}

package mujava.plugin.wizards.generation;

import java.util.ArrayList;


import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SourcePage extends WizardPage {
	IProject project = null;

	CheckboxTreeViewer viewer;

	ArrayList<IJavaElement> selectedSources = null;

	SourceCodeContentProvider contentProvider;

	public SourcePage(ISelection selection) {
		super("wizardPage");
		setTitle("Choose source codes to mutate");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;

		Composite btnComposite = new Composite(container, SWT.NULL);
		btnComposite.setLayoutData(gridData);
		btnComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		Button allBtn = new Button(btnComposite, SWT.PUSH);
		allBtn.setText("Select All");
		allBtn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				selectAll(true);
			}
		});

		Button nallBtn = new Button(btnComposite, SWT.PUSH);
		nallBtn.setText("Unselect All");
		nallBtn.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				selectAll(false);
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		viewer = new CheckboxTreeViewer(container);
		viewer.getTree().setLayoutData(gridData);
		contentProvider = new SourceCodeContentProvider();
		viewer.setContentProvider(contentProvider);
		// There are more flags defined in class JavaElementLabelProvider
		ILabelProvider labelProvider = new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_DEFAULT
						| JavaElementLabelProvider.SHOW_QUALIFIED);
		viewer.setLabelProvider(labelProvider);
		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean flag = event.getChecked();
				IJavaElement element = (IJavaElement) event.getElement();
				if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
					IPackageFragment frag = (IPackageFragment) element;
					try {
						IJavaElement[] temp = frag.getChildren();
						ArrayList<IJavaElement> list = contentProvider
								.getSourceCode(temp);
						for (IJavaElement item : list) {
							viewer.setChecked(item, flag);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				} else if (element.getElementType() == IJavaElement.COMPILATION_UNIT) {
					if (!flag) {
						while (true) {
							IJavaElement parent = element.getParent();
							if (parent == null)
								break;
							viewer.setChecked(parent, flag);
							element = parent;
						}
					}
				}

				dialogChanged();
			}
		});

		dialogChanged();
		setControl(container);
	}

	private void setChild(IPackageFragment element, boolean flag) {
		Object[] temp = contentProvider.getChildren(element);
		for (int i = 0; i < temp.length; i++) {
			viewer.setChecked(temp[i], flag);
			if (temp[i] instanceof IPackageFragment)
				setChild((IPackageFragment) temp[i], flag);
		}
	}

	protected void selectAll(boolean b) {
		Object[] objs = contentProvider.getElements(JavaCore.create(project));
		for (Object obj : objs) {
			viewer.setChecked(obj, b);
			if (obj instanceof IPackageFragment)
				setChild((IPackageFragment) obj, b);
		}
		dialogChanged();
	}

	private void dialogChanged() {
		selectedSources = new ArrayList<IJavaElement>();

		Object[] results = viewer.getCheckedElements();
		for (int i = 0; i < results.length; i++) {
			assert (results[i] instanceof IJavaElement);
			IJavaElement element = (IJavaElement) results[i];
			if (element.getElementType() == IJavaElement.COMPILATION_UNIT)
				selectedSources.add(element);
		}

		if (selectedSources.size() == 0) {
			updateStatus("Select Source Files more than one.");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Called when only previous page call nextPage()
	 * 
	 * @param project
	 */
	public void setProject(IProject project) {
		this.project = project;
		viewer.setInput(JavaCore.create(project));
	}

	public ArrayList<IJavaElement> getSelectedSources() {
		return selectedSources;
	}
}

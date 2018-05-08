package mujava.plugin.wizards;

import java.util.ArrayList;
import java.util.List;

import mujava.MutantOperator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ValidMuJavaProjectPage extends WizardPage {

	private IProject selectedProject;

	private IResource selectedMuJavaProject;

	private Text selectedProjectText;

	private Text selectedMuJavaProjectText;

	private boolean hasGen;

	private List<IDoubleClickListener> listeners = new ArrayList<IDoubleClickListener>();

	public ValidMuJavaProjectPage(ISelection selection, boolean hasGenerationWay) {
		super("wizardPage");
		setTitle("Select project and mujava project");
		setDescription("This wizard creates a new mutant file with *.mutant and *.original extension that can be opened by a mugamma mutant editor.");
		this.hasGen = hasGenerationWay;
	}

	public void createControl(Composite parent) {
		// setup UI
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		TreeViewer viewer = new TreeViewer(container);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		viewer.getTree().setLayoutData(gridData);
		Label label = new Label(container, SWT.NONE);
		label.setText("Selected Project : ");
		selectedProjectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		selectedProjectText.setEditable(false);
		selectedProjectText
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(container, SWT.NONE);
		label.setText("Selected MuJavaProject : ");
		selectedMuJavaProjectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		selectedMuJavaProjectText.setEditable(false);
		selectedMuJavaProjectText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		// get the project information
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		// setup the list model and view
		viewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof IProject) {
					IResource[] muJavaProjectFiles = getMuJavaProjectFiles((IProject) parentElement);
					return muJavaProjectFiles;
				}
				if (parentElement instanceof IResource) {
					return new Object[] {};
				}
				return new Object[] {};
			}

			public Object getParent(Object element) {
				if (element instanceof IProject)
					return null;
				if (element instanceof IResource)
					return ((IResource) element).getProject();

				return null;
			}

			public boolean hasChildren(Object element) {
				if (element instanceof IProject) {
					IResource[] projects = getMuJavaProjectFiles((IProject) element);
					if (projects != null && projects.length != 0)
						return true;
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				return root.getProjects();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		viewer.setInput(projects);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				Object item = sSelection.getFirstElement();
				if (item instanceof IProject) {
					selectProject((IProject) item);
					selectMuJavaProject(null);
				} else if (item instanceof IResource) {
					IResource resource = (IResource) item;
					selectProject(resource.getProject());
					selectMuJavaProject(resource);
				}
				dialogChanged();
				if (canFlipToNextPage())
					fireDoubleClick(event);
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				Object item = sSelection.getFirstElement();
				if (item instanceof IProject) {
					selectProject((IProject) item);
					selectMuJavaProject(null);
				} else if (item instanceof IResource) {
					IResource resource = (IResource) item;
					selectProject(resource.getProject());
					selectMuJavaProject(resource);
				}
				dialogChanged();
			}
		});
		viewer.setLabelProvider(new ILabelProvider() {
			Image project = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/projects.gif").createImage();

			Image muJavaProject = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/sample.gif").createImage();

			public Image getImage(Object element) {
				if (element instanceof IProject)
					return project;
				if (element instanceof IResource)
					return muJavaProject;
				return null;
			}

			public String getText(Object element) {
				if (element instanceof IProject)
					return ((IProject) element).getName();
				if (element instanceof IResource)
					return ((IResource) element).getName();
				return "";
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
				project.dispose();
				muJavaProject.dispose();
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
		});
		viewer.expandAll();

		dialogChanged();
		setControl(container);
	}

	protected void fireDoubleClick(DoubleClickEvent event) {
		for (IDoubleClickListener l : listeners) {
			l.doubleClick(event);
		}
	}

	public void addListener(IDoubleClickListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}

	public void removeListener(IDoubleClickListener l) {
		listeners.remove(l);
	}

	private IResource[] getMuJavaProjectFiles(IProject prj) {
		ArrayList<IResource> ary = new ArrayList<IResource>();
		try {
			IResource[] res = prj.members(false);
			for (int i = 0; i < res.length; i++) {
				String ext = res[i].getFileExtension();
				if ("mjp".equalsIgnoreCase(ext)) {
					ary.add(res[i]);
				}
			}

			// IMarker[] markers = prj.findMarkers("MuJava.mujavaMarker", false,
			// IResource.DEPTH_ONE);
			// for (int i = 0; i < markers.length; i++) {
			// Object obj = markers[i].getAttribute("Type");
			// if(obj != null && obj instanceof String) {
			// String value = (String)obj;
			// if(value.equals("MuJavaProject"))
			// ary.add(markers[i].getResource());
			// }
			// }
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return (IResource[]) ary.toArray(new IResource[ary.size()]);
	}

	private void selectProject(IProject project) {
		String name = (project == null) ? "" : project.getName();
		selectedProject = project;
		selectedProjectText.setText(name);
	}

	private void selectMuJavaProject(IResource res) {
		String name = (res == null) ? "" : res.getName();
		this.selectedMuJavaProject = res;
		this.selectedMuJavaProjectText.setText(name);
	}

	public IResource getSelectedMuJavaProject() {
		return selectedMuJavaProject;
	}

	public IProject getSelectedProject() {
		return selectedProject;
	}

	private void dialogChanged() {
		if (selectedProject == null) {
			updateStatus("Choose one project");
			return;
		}
		if (selectedMuJavaProject == null) {
			updateStatus("Select MuJava project");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

}

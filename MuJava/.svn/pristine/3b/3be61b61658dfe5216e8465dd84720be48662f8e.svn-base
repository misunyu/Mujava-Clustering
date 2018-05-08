package mujava.plugin.wizards.execution;

import java.io.File;
import java.util.ArrayList;

import mujava.MuJavaProject;
import mujava.gen.GenerationType;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author Swkim
 * 
 */
public class TestOptionPage extends WizardPage
{

	private int TIMEOUT = 3000;
	private int REPEAT = 1;

	private Text dependencyFileNameText = null;
	private Text equivalentFileNameText = null;
	private Text hashCodeFileNameText = null;

	private Text timeOut;
	private Button dependecyBtn;
	private Button equButton;
	//private Button targetButton;
	private Button cmBtn;
	private Text repeatText;
	private Button reportReachExcelTableBtn;

	private IProject selectedProject = null;
	private Button mutantCountTableBtn;
	private Button executionCountTableBtn;
	private Group occurenceTypeGroup;
	private TreeViewer dependTreeViewer;
	private TreeViewer equivalentTreeViewer;
	//private TreeViewer targetTreeViewer;
	private boolean isReachabilityAnalysisMode = false;
	private Text reachLogFileNameText;
	private Text reachTableFileNameText;
	private Text pathReachTableFileNameText;
	private Text classReachTableFileNameText;
	private Text methodReachTableFileNameText;
	private Button reachLogAppendBtn;
	private Group reachGroup;
	private Group logFileTypeGroup;
	private Button reachLogOverwriteBtn;
	private Button reuseReachLogBtn;
	private boolean isReachLogReused = false;
	private Group reachOutputGroup;
	private Button reachTableOverwriteBtn;

	protected TestOptionPage(ISelection selection)
	{
		super("wizardPage");
		setTitle("Notify any test case execution options");
	}

	private void btnSelect()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText("Specify TestCase Dependency File");
		dialog.setFilterExtensions(new String[] { "*.depend", "*.*" });
		String fileName = dialog.open();

		if (fileName.isEmpty())
			return;

		dependencyFileNameText.setText(fileName);
	}

	public boolean canGenerateExcelReport()
	{
		return this.reportReachExcelTableBtn.getSelection();
	}

	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createGroup(container);
		createDependGroup(container);
		createEquivalentGroup(container);
		//createTargetGroup(container);
		createReachGroup(container);
		createReachOutputGroup(container);

		dialogChanged();
		setControl(container);
	}

	/**
	 * This method initializes dependGroup
	 * 
	 */
	private void createDependGroup(Composite parent)
	{
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		gridData2.heightHint = 150;

		Group dependGroup = new Group(parent, SWT.NONE);
		dependGroup.setText("TestCase Reach Table");
		dependGroup.setLayout(new GridLayout(3, false));
		dependGroup.setLayoutData(gridData2);

		Label label = new Label(dependGroup, SWT.NONE);
		label.setText("File Name :");
		dependencyFileNameText = new Text(dependGroup, SWT.BORDER);
		dependencyFileNameText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		// dependencyFileNameText.addModifyListener(new ModifyListener() {
		//
		// @Override
		// public void modifyText(ModifyEvent e) {
		// if (levelCombo != null) {
		// levelCombo.setEnabled(!(dependencyFileNameText.getText()
		// .isEmpty()));
		// }
		//
		// }
		// });

		dependecyBtn = new Button(dependGroup, SWT.NONE);
		dependecyBtn.setText("Search");
		dependecyBtn.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				btnSelect();
			}
		});

		// get the project information
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		dependTreeViewer = new TreeViewer(dependGroup);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		dependTreeViewer.getTree().setLayoutData(gridData);

		// setup the list model and view
		dependTreeViewer.setContentProvider(new ITreeContentProvider()
		{
			public void dispose()
			{
			}

			public Object[] getChildren(Object parentElement)
			{
				if (parentElement instanceof IProject)
				{
					IResource[] muJavaProjectFiles = getDependencyFiles((IProject) parentElement);
					return muJavaProjectFiles;
				}
				if (parentElement instanceof IResource)
				{
					return new Object[] {};
				}
				return new Object[] {};
			}

			public Object[] getElements(Object inputElement)
			{
				if (selectedProject != null)
					return new Object[] { selectedProject };

				return ResourcesPlugin.getWorkspace().getRoot().getProjects();
			}

			public Object getParent(Object element)
			{
				if (element instanceof IProject)
					return null;
				if (element instanceof IResource)
					return ((IResource) element).getProject();

				return null;
			}

			public boolean hasChildren(Object element)
			{
				if (element instanceof IProject)
				{
					IResource[] projects = getDependencyFiles((IProject) element);
					if (projects != null && projects.length != 0)
						return true;
				}
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput)
			{
			}
		});
		dependTreeViewer.setInput(projects);
		dependTreeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection selection = event.getSelection();
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				Object item = sSelection.getFirstElement();
				if (item instanceof IProject)
				{
					selectDependencyFile(null);
				}
				else if (item instanceof IResource)
				{
					selectDependencyFile((IResource) item);
				}
				dialogChanged();
			}
		});

		dependTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						ISelection selection = event.getSelection();
						IStructuredSelection sSelection = (IStructuredSelection) selection;
						Object item = sSelection.getFirstElement();
						if (!(item instanceof IProject)
								&& item instanceof IResource)
						{
							IResource resource = (IResource) item;
							selectDependencyFile(resource);
						}
						else
							selectDependencyFile(null);
						dialogChanged();
					}
				});
		dependTreeViewer.setLabelProvider(new ILabelProvider()
		{
			Image projectImage = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/projects.gif").createImage();

			Image muJavaProjectImage = AbstractUIPlugin
					.imageDescriptorFromPlugin("MuJavaEclipsePlugIn",
							"icons/sample.gif").createImage();

			public void addListener(ILabelProviderListener listener)
			{
			}

			public void dispose()
			{
				projectImage.dispose();
				muJavaProjectImage.dispose();
			}

			public Image getImage(Object element)
			{
				if (element instanceof IProject)
					return projectImage;
				if (element instanceof IResource)
					return muJavaProjectImage;
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof IProject)
					return ((IProject) element).getName();
				if (element instanceof IResource)
					return ((IResource) element).getName();
				return "";
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void removeListener(ILabelProviderListener listener)
			{
			}
		});
		dependTreeViewer.expandAll();
	}

	private void createEquivalentGroup(Composite parent)
	{
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		gridData2.heightHint = 150;

		Group equGroup = new Group(parent, SWT.NONE);
		equGroup.setText("Equivalent Mutant Table");
		equGroup.setLayout(new GridLayout(3, false));
		equGroup.setLayoutData(gridData2);

		Label label = new Label(equGroup, SWT.NONE);
		label.setText("File Name :");
		equivalentFileNameText = new Text(equGroup, SWT.BORDER);
		equivalentFileNameText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		equButton = new Button(equGroup, SWT.NONE);
		equButton.setText("Search");
		equButton.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				equBtnSelect();
			}
		});

		equivalentTreeViewer = new TreeViewer(equGroup);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		equivalentTreeViewer.getTree().setLayoutData(gridData);

		// get the project information
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		// setup the list model and view
		equivalentTreeViewer.setContentProvider(new ITreeContentProvider()
		{
			public void dispose()
			{
			}

			public Object[] getChildren(Object parentElement)
			{
				if (parentElement instanceof IProject)
				{
					IResource[] muJavaProjectFiles = getEquFiles((IProject) parentElement);
					return muJavaProjectFiles;
				}
				if (parentElement instanceof IResource)
				{
					return new Object[] {};
				}
				return new Object[] {};
			}

			public Object[] getElements(Object inputElement)
			{

				if (selectedProject != null && inputElement != selectedProject)
				{
					return new IProject[] { selectedProject };
				}

				return ResourcesPlugin.getWorkspace().getRoot().getProjects();
			}

			public Object getParent(Object element)
			{
				if (element instanceof IProject)
					return null;
				if (element instanceof IResource)
					return ((IResource) element).getProject();

				return null;
			}

			public boolean hasChildren(Object element)
			{
				if (element instanceof IProject)
				{
					IResource[] eqFiles = getEquFiles((IProject) element);
					if (eqFiles != null && eqFiles.length != 0)
						return true;
				}
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput)
			{
			}
		});
		equivalentTreeViewer.setInput(projects);
		equivalentTreeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection selection = event.getSelection();
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				Object item = sSelection.getFirstElement();
				if (item instanceof IProject)
				{
					selectEquivalentFile(null);
				}
				else if (item instanceof IResource)
				{
					selectEquivalentFile((IResource) item);
				}
				dialogChanged();
			}
		});
		equivalentTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						ISelection selection = event.getSelection();
						IStructuredSelection sSelection = (IStructuredSelection) selection;
						Object item = sSelection.getFirstElement();
						if (!(item instanceof IProject)
								&& item instanceof IResource)
						{
							IResource resource = (IResource) item;
							selectEquivalentFile(resource);
						}
						else
							selectEquivalentFile(null);
						dialogChanged();
					}
				});
		equivalentTreeViewer.setLabelProvider(new ILabelProvider()
		{
			Image projectImage = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/projects.gif").createImage();

			Image muJavaProjectImage = AbstractUIPlugin
					.imageDescriptorFromPlugin("MuJavaEclipsePlugIn",
							"icons/sample.gif").createImage();

			public void addListener(ILabelProviderListener listener)
			{
			}

			public void dispose()
			{
				projectImage.dispose();
				muJavaProjectImage.dispose();
			}

			public Image getImage(Object element)
			{
				if (element instanceof IProject)
					return projectImage;
				if (element instanceof IResource)
					return muJavaProjectImage;
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof IProject)
					return ((IProject) element).getName();
				if (element instanceof IResource)
					return ((IResource) element).getName();
				return "";
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void removeListener(ILabelProviderListener listener)
			{
			}
		});
		equivalentTreeViewer.expandAll();
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup(Composite parent)
	{
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

		Group group = new Group(parent, SWT.NONE);
		group.setText("Options");
		group.setLayoutData(gridData2);
		group.setLayout(new GridLayout(2, false));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		GridData twoSpanGridData = new GridData(GridData.FILL_HORIZONTAL);
		twoSpanGridData.horizontalSpan = 2;

		Label timeOutLabel = new Label(group, SWT.NONE);
		timeOutLabel.setText("Time Out (ms) : ");
		timeOut = new Text(group, SWT.BORDER);
		timeOut.setLayoutData(gridData);
		timeOut.setText(Integer.toString(TIMEOUT));

		Label l = new Label(group, SWT.NONE);
		l.setText("Iteration : ");
		repeatText = new Text(group, SWT.BORDER);
		repeatText.setText(Integer.toString(REPEAT));
		repeatText.setLayoutData(gridData);

		reportReachExcelTableBtn = new Button(group, SWT.CHECK);
		reportReachExcelTableBtn.setText("Generates a Statistic-Table(*.xlsx)");
		reportReachExcelTableBtn.setLayoutData(twoSpanGridData);
		reportReachExcelTableBtn.setSelection(false);
		reportReachExcelTableBtn.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{

			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{

				boolean isSelected = reportReachExcelTableBtn.getSelection();
				mutantCountTableBtn.setEnabled(isSelected);
				executionCountTableBtn.setEnabled(isSelected);
			}
		});

		GridData groupGridData = new GridData(GridData.FILL_BOTH);
		groupGridData.horizontalSpan = 2;

		occurenceTypeGroup = new Group(group, SWT.NONE);
		occurenceTypeGroup.setLayoutData(groupGridData);
		occurenceTypeGroup.setLayout(new RowLayout(SWT.VERTICAL));

		mutantCountTableBtn = new Button(occurenceTypeGroup, SWT.RADIO);
		mutantCountTableBtn
				.setText("Count one as all executions for the same mutant");
		mutantCountTableBtn.setSelection(true);
		mutantCountTableBtn.setEnabled(false);

		executionCountTableBtn = new Button(occurenceTypeGroup, SWT.RADIO);
		executionCountTableBtn
				.setText("Count one as an execution for a mutant");
		executionCountTableBtn.setEnabled(false);

		cmBtn = new Button(group, SWT.CHECK);
		cmBtn.setText("Uses Conditional-Mutual Mutants");
		cmBtn.setLayoutData(twoSpanGridData);
		cmBtn.setSelection(false);

	}

	private void createReachGroup(Composite parent)
	{
		reachGroup = new Group(parent, SWT.NONE);
		{
			reachGroup.setText("Reachabilty Anaysis Log Options");
			reachGroup.setLayout(new GridLayout(2, false));

			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
			gridData2.exclude = true;
			reachGroup.setLayoutData(gridData2);
		}

		{
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

			Label l = new Label(reachGroup, SWT.None);
			l.setText("Log Filename : ");

			reachLogFileNameText = new Text(reachGroup, SWT.BORDER);
			reachLogFileNameText.setLayoutData(gridData);
			reachLogFileNameText.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					dialogChanged();
				}
			});
		}

		{
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;

			reuseReachLogBtn = new Button(reachGroup, SWT.CHECK);
			reuseReachLogBtn.setSelection(isReachLogReused);
			reuseReachLogBtn
					.setText("Reuse existing reachability-analysis results without executing mutants");
			reuseReachLogBtn.setLayoutData(gridData);
			reuseReachLogBtn.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					// DO NOTHING
				}

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					boolean selected = reuseReachLogBtn.getSelection();
					isReachLogReused = selected;

					updateResultNameGroup(isReachLogReused);

					dialogChanged();
				}
			});
		}

		{
			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
			gridData2.horizontalSpan = 2;

			RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
			rowLayout.spacing = 10;

			logFileTypeGroup = new Group(reachGroup, SWT.NONE);
			logFileTypeGroup.setLayoutData(gridData2);
			logFileTypeGroup.setLayout(rowLayout);

			reachLogAppendBtn = new Button(logFileTypeGroup, SWT.RADIO);
			reachLogAppendBtn.setSelection(false);
			reachLogAppendBtn
					.setText("Appends the result of reachabilty analysis");

			reachLogOverwriteBtn = new Button(logFileTypeGroup, SWT.RADIO);
			reachLogOverwriteBtn.setSelection(true);
			reachLogOverwriteBtn
					.setText("Overwrite the result of reachabilty analysis");
		}

	}

	private void createReachOutputGroup(Composite parent)
	{
		reachOutputGroup = new Group(parent, SWT.NONE);
		{
			reachOutputGroup.setText("Reachabilty Anaysis Output Option");
			reachOutputGroup.setLayout(new GridLayout(2, false));

			GridData gridData2 = new GridData(GridData.FILL_BOTH);
			gridData2.exclude = true;
			reachOutputGroup.setLayoutData(gridData2);
		}

		{
			Label l = new Label(reachOutputGroup, SWT.None);
			l.setText("Reach Table Filename : ");

			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

			reachTableFileNameText = new Text(reachOutputGroup, SWT.BORDER);
			reachTableFileNameText.setLayoutData(gridData2);
			reachTableFileNameText.addModifyListener(new ModifyListener()
			{

				@Override
				public void modifyText(ModifyEvent e)
				{
					String text = reachTableFileNameText.getText();
					String methodName = text + "_method.table";
					methodReachTableFileNameText.setText(methodName);
					String className = text + "_class.table";
					classReachTableFileNameText.setText(className);
					String pathName = text + "_path.table";
					pathReachTableFileNameText.setText(pathName);

					dialogChanged();
				}
			});
		}

		{
			Label l = new Label(reachOutputGroup, SWT.None);
			l.setText("Class-Level Reach Table : ");

			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

			classReachTableFileNameText = new Text(reachOutputGroup, SWT.BORDER);
			classReachTableFileNameText.setEditable(false);
			classReachTableFileNameText.setLayoutData(gridData2);
		}

		{
			Label l = new Label(reachOutputGroup, SWT.None);
			l.setText("Method-Level Reach Table : ");

			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

			methodReachTableFileNameText = new Text(reachOutputGroup,
					SWT.BORDER);
			methodReachTableFileNameText.setEditable(false);
			methodReachTableFileNameText.setLayoutData(gridData2);
		}
		{
			Label l = new Label(reachOutputGroup, SWT.None);
			l.setText("Path-Level Reach Table : ");

			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

			pathReachTableFileNameText = new Text(reachOutputGroup, SWT.BORDER);
			pathReachTableFileNameText.setEditable(false);
			pathReachTableFileNameText.setLayoutData(gridData2);
		}

		{
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 2;

			reachTableOverwriteBtn = new Button(reachOutputGroup, SWT.CHECK);
			reachTableOverwriteBtn.setSelection(true);
			reachTableOverwriteBtn
					.setText("Overwrite reach table into those files if those files already exist");
			reachTableOverwriteBtn.setLayoutData(gridData);
		}
	}

/*	private void createTargetGroup(Composite parent)
	{
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		gridData2.heightHint = 150;

		Group targetGroup = new Group(parent, SWT.NONE);
		targetGroup.setText("HashCode Table");
		targetGroup.setLayout(new GridLayout(3, false));
		targetGroup.setLayoutData(gridData2);

		Label label = new Label(targetGroup, SWT.NONE);
		label.setText("File Name :");
		hashCodeFileNameText = new Text(targetGroup, SWT.BORDER);
		hashCodeFileNameText.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		targetButton = new Button(targetGroup, SWT.NONE);
		targetButton.setText("Search");
		targetButton.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				hashCodeBtnSelect();
			}
		});

		targetTreeViewer = new TreeViewer(targetGroup);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		targetTreeViewer.getTree().setLayoutData(gridData);

		// get the project information
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		// setup the list model and view
		targetTreeViewer.setContentProvider(new ITreeContentProvider()
		{
			public void dispose()
			{
			}

			public Object[] getChildren(Object parentElement)
			{
				if (parentElement instanceof IProject)
				{
					IResource[] muJavaProjectFiles = getHashCodeFiles((IProject) parentElement);
					return muJavaProjectFiles;
				}
				if (parentElement instanceof IResource)
				{
					return new Object[] {};
				}
				return new Object[] {};
			}

			public Object[] getElements(Object inputElement)
			{
				if (selectedProject != null)
					return new Object[] { selectedProject };

				return ResourcesPlugin.getWorkspace().getRoot().getProjects();
			}

			public Object getParent(Object element)
			{
				if (element instanceof IProject)
					return null;
				if (element instanceof IResource)
					return ((IResource) element).getProject();

				return null;
			}

			public boolean hasChildren(Object element)
			{
				if (element instanceof IProject)
				{
					IResource[] projects = getHashCodeFiles((IProject) element);
					if (projects != null && projects.length != 0)
						return true;
				}
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput)
			{
			}
		});
		targetTreeViewer.setInput(projects);
		targetTreeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection selection = event.getSelection();
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				Object item = sSelection.getFirstElement();
				if (item instanceof IProject)
				{
					selectHashCodeFile(null);
				}
				else if (item instanceof IResource)
				{
					selectHashCodeFile((IResource) item);
				}
				dialogChanged();
			}
		});

		targetTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						ISelection selection = event.getSelection();
						IStructuredSelection sSelection = (IStructuredSelection) selection;
						Object item = sSelection.getFirstElement();
						if (!(item instanceof IProject)
								&& item instanceof IResource)
						{
							IResource resource = (IResource) item;
							selectHashCodeFile(resource);
						}
						else
							selectHashCodeFile(null);
						dialogChanged();
					}
				});
		targetTreeViewer.setLabelProvider(new ILabelProvider()
		{
			Image projectImage = AbstractUIPlugin.imageDescriptorFromPlugin(
					"MuJavaEclipsePlugIn", "icons/projects.gif").createImage();

			Image muJavaProjectImage = AbstractUIPlugin
					.imageDescriptorFromPlugin("MuJavaEclipsePlugIn",
							"icons/sample.gif").createImage();

			public void addListener(ILabelProviderListener listener)
			{
			}

			public void dispose()
			{
				projectImage.dispose();
				muJavaProjectImage.dispose();
			}

			public Image getImage(Object element)
			{
				if (element instanceof IProject)
					return projectImage;
				if (element instanceof IResource)
					return muJavaProjectImage;
				return null;
			}

			public String getText(Object element)
			{
				if (element instanceof IProject)
					return ((IProject) element).getName();
				if (element instanceof IResource)
					return ((IResource) element).getName();
				return "";
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void removeListener(ILabelProviderListener listener)
			{
			}
		});
		targetTreeViewer.expandAll();

	}
*/
	private void dialogChanged()
	{

		String text = timeOut.getText();
		try
		{
			Integer.getInteger(text);
		}
		catch (Exception e)
		{
			updateStatus("Time out is not a valid integer format.");
			return;
		}

		if (isReachabilityAnalysisMode)
		{
			String fileName = reachLogFileNameText.getText();
			fileName = fileName.trim();
			if (fileName.isEmpty())
			{
				updateStatus("A Reachability Analysis Result FileName is required.");
				return;
			}

			fileName = getReachLogFileName();
			if (isReachLogReused && !(new File(fileName).exists()))
			{
				updateStatus("Selected R-A Result File should be exist");
				return;
			}
		}
		else
		{
			// 입력 안하는것은 괜찮지만, 있으면 파일이 존재해야한다.
			String fileName = dependencyFileNameText.getText();
			if (!fileName.isEmpty())
			{
				if (!(new File(fileName).exists()))
				{
					updateStatus("Selected Depend File should be exist");
					return;
				}
			}

			/*
			fileName = equivalentFileNameText.getText();
			if (!fileName.isEmpty())
			{
				if (!(new File(fileName).exists()))
				{
					updateStatus("Selected Equivalent File should be exist");
					return;
				}

				fileName = hashCodeFileNameText.getText();
				if (fileName.isEmpty())
				{
					updateStatus("Equivalent File needs HashCode Table");
					return;
				}
				else
				{
					if (!(new File(fileName).exists()))
					{
						updateStatus("Selected HashCode File should be exist");
						return;
					}
				}
			}*/
		}

		updateStatus(null);
	}

	protected void equBtnSelect()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText("Specify Equivalent Mutant File");
		dialog.setFilterExtensions(new String[] { "*.eq", "*.*" });
		String fileName = dialog.open();

		if (fileName.isEmpty())
			return;

		equivalentFileNameText.setText(fileName);
	}

	public String getClassReachFileName()
	{
		return getProjectRelativePath(classReachTableFileNameText);
	}

	private IResource[] getDependencyFiles(IProject prj)
	{
		ArrayList<IResource> ary = new ArrayList<IResource>();
		try
		{
			IResource[] res = prj.members(false);
			for (int i = 0; i < res.length; i++)
			{
				String ext = res[i].getFileExtension();
				if ("table".equalsIgnoreCase(ext))
				{
					ary.add(res[i]);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return (IResource[]) ary.toArray(new IResource[ary.size()]);
	}

	/**
	 * 
	 * @return string object, never return null
	 */
	public String getEquFileName()
	{
		if (equivalentFileNameText.getText() == null)
			return "";
		return equivalentFileNameText.getText();
	}

	protected IResource[] getEquFiles(IProject parentElement)
	{
		ArrayList<IResource> ary = new ArrayList<IResource>();
		try
		{
			IResource[] res = parentElement.members(false);
			for (int i = 0; i < res.length; i++)
			{
				String ext = res[i].getFileExtension();
				if ("eq".equalsIgnoreCase(ext))
				{
					ary.add(res[i]);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return (IResource[]) ary.toArray(new IResource[ary.size()]);
	}

	/**
	 * 
	 * @return string object, never return null
	 */
	public String getFileName()
	{
		if (dependencyFileNameText.getText() == null)
			return "";
		return dependencyFileNameText.getText();
	}

	/**
	 * 
	 * @return string object, never return null
	 */
	public String getHashCodeFileName()
	{
		if (hashCodeFileNameText.getText() == null)
			return "";
		return hashCodeFileNameText.getText();
	}

	protected IResource[] getHashCodeFiles(IProject parentElement)
	{
		ArrayList<IResource> ary = new ArrayList<IResource>();
		try
		{
			IResource[] res = parentElement.members(false);
			for (int i = 0; i < res.length; i++)
			{
				String ext = res[i].getFileExtension();
				if ("hash".equalsIgnoreCase(ext))
				{
					ary.add(res[i]);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return (IResource[]) ary.toArray(new IResource[ary.size()]);
	}

	public int getIteration()
	{
		String text = repeatText.getText();
		text = text.trim();

		if (text.isEmpty())
		{
			return REPEAT;
		}

		try
		{
			int num = Integer.parseInt(text);
			if (num < 1)
			{
				return REPEAT;
			}

			return num;
		}
		catch (NumberFormatException e)
		{
			return REPEAT;
		}

	}

	public String getMethodReachFileName()
	{
		return getProjectRelativePath(methodReachTableFileNameText);
	}

	public int getOccurenceType()
	{
		if (canGenerateExcelReport())
		{
			if (executionCountTableBtn.getSelection())
			{
				return 2;
			}
			if (mutantCountTableBtn.getSelection())
			{
				return 1;
			}
		}

		return 0;
	}

	public String getPathReachFileName()
	{
		return getProjectRelativePath(pathReachTableFileNameText);
	}

	public IProject getProject()
	{
		return this.selectedProject;
	}

	private String getProjectRelativePath(Text textBox)
	{
		if (this.selectedProject == null || textBox == null)
		{
			return "";
		}

		String fileName = textBox.getText();

		IFile file = selectedProject.getFile(fileName);

		return file.getLocation().toOSString();
	}

	public String getReachLogFileName()
	{
		return getProjectRelativePath(reachLogFileNameText);
	}

	public int getTimeout()
	{
		String txt = timeOut.getText();
		try
		{
			int val = Integer.parseInt(txt);
			return val;
		}
		catch (Exception e)
		{
		}

		return TIMEOUT;
	}

	protected void hashCodeBtnSelect()
	{
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText("Specify Target Hashcode File");
		dialog.setFilterExtensions(new String[] { "*.hash", "*.*" });
		String fileName = dialog.open();

		if (fileName.isEmpty())
			return;

		hashCodeFileNameText.setText(fileName);
	}

	public boolean isCMMode()
	{
		return cmBtn.getSelection();
	}

	public boolean isReachLogOverwritten()
	{
		return reachLogOverwriteBtn.getSelection();
	}

	public boolean isReachLogReused()
	{
		return isReachLogReused;
	}

	public boolean isReachTableOverwritten()
	{
		return reachTableOverwriteBtn.getSelection();
	}

	private void selectDependencyFile(IResource res)
	{
		String name = (res == null) ? "" : res.getLocation().toOSString();
		this.dependencyFileNameText.setText(name);
	}

	protected void selectEquivalentFile(IResource resource)
	{
		String name = (resource == null) ? "" : resource.getLocation()
				.toOSString();
		this.equivalentFileNameText.setText(name);

	}

	protected void selectHashCodeFile(IResource resource)
	{
		String name = (resource == null) ? "" : resource.getLocation()
				.toOSString();
		this.hashCodeFileNameText.setText(name);

	}

	public void setProject(IResource mujavaProjectRes, IProject project)
	{
		this.selectedProject = project;

		MuJavaProject muPrj = MuJavaProject.getMuJavaProject(mujavaProjectRes,
				null);
		boolean isReachAnalysisMode = muPrj.getGenerationWay() == GenerationType.REACH;
		updateReachUI(isReachAnalysisMode);

		if (!isReachAnalysisMode)
		{
			// update content providers
			dependTreeViewer.setInput(this);
			dependTreeViewer.expandAll();
			equivalentTreeViewer.setInput(this);
			equivalentTreeViewer.expandAll();
			//targetTreeViewer.setInput(this);
			//targetTreeViewer.expandAll();
		}

		dialogChanged();
	}

	private void updateReachUI(boolean isReachAnalysis)
	{
		isReachabilityAnalysisMode = isReachAnalysis;

		repeatText.setEnabled(!isReachAnalysis);
		cmBtn.setVisible(!isReachAnalysis);
		reportReachExcelTableBtn.setVisible(!isReachAnalysis);

		Group dGroup = (Group) dependTreeViewer.getTree().getParent();
		GridData gd = (GridData) dGroup.getLayoutData();
		gd.exclude = isReachAnalysis;
		dGroup.setVisible(!isReachAnalysis);

		/*Group tGroup = (Group) targetTreeViewer.getTree().getParent();
		gd = (GridData) tGroup.getLayoutData();
		gd.exclude = isReachAnalysis;
		tGroup.setVisible(!isReachAnalysis);*/

		Group eGroup = (Group) equivalentTreeViewer.getTree().getParent();
		gd = (GridData) eGroup.getLayoutData();
		gd.exclude = isReachAnalysis;
		eGroup.setVisible(!isReachAnalysis);

		gd = (GridData) cmBtn.getLayoutData();
		gd.exclude = isReachAnalysis;

		gd = (GridData) occurenceTypeGroup.getLayoutData();
		gd.exclude = isReachAnalysis;
		occurenceTypeGroup.setVisible(!isReachAnalysis);

		gd = (GridData) reachGroup.getLayoutData();
		gd.exclude = !isReachAnalysis;
		reachGroup.setVisible(isReachAnalysis);

		gd = (GridData) reachOutputGroup.getLayoutData();
		gd.exclude = !isReachAnalysis;
		reachOutputGroup.setVisible(isReachAnalysis);

		String prjName = selectedProject.getProject().getName();
		String logFileName = prjName + "_reach.log";
		reachLogFileNameText.setText(logFileName);

		String tableFileName = prjName + "_reach";
		reachTableFileNameText.setText(tableFileName);

		this.getShell().layout(true, true);
	}

	protected void updateResultNameGroup(boolean isReachLogReused)
	{
		reachLogAppendBtn.setEnabled(!isReachLogReused);
		reachLogOverwriteBtn.setEnabled(!isReachLogReused);
	}

	private void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}

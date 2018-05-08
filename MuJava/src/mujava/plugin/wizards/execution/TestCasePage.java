package mujava.plugin.wizards.execution;

import java.util.ArrayList;
import java.util.List;

import mujava.MuJavaProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.junit.runner.JUnitCore;

/**
 * A wizard page for selecting test cases to be executed.
 * 
 * @author swkim
 * 
 */
public class TestCasePage extends WizardPage
{

	public static final String TITLE = "Select Test Cases";;

	/**
	 * primary information of this wizard
	 */
	ArrayList<IMethod> selectedTests = new ArrayList<IMethod>();

	ArrayList<IFile> selectedTestClasses = new ArrayList<IFile>();

	/**
	 * NOT USED
	 */
	IProject project = null;

	/**
	 * tree viewer for test cases
	 */
	CheckboxTreeViewer testCaseTreeViewer;

	/**
	 * ContentProvider for test case tree
	 */
	TestCaseContentProvider testCaseProvider = new TestCaseContentProvider();

	/**
	 * ContentProvider for JUnit test case tree
	 */
	JUnitCaseContentProvider unitCaseProvider = new JUnitCaseContentProvider();

	private boolean isJunitTest;

	/**
	 * Creates a test case wizard page with the given selected item.
	 * 
	 * @param selection
	 */
	public TestCasePage(ISelection selection)
	{
		super(TITLE);
		setTitle(TITLE);
	}

	/**
	 * Creates the top level control for test case wizard page under the given
	 * parent composite.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public void createControl(Composite parent)
	{

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		Composite btnComposite = new Composite(container, SWT.NULL);
		btnComposite.setLayoutData(gridData);
		btnComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Button selectAllBtn = new Button(btnComposite, SWT.PUSH);
		selectAllBtn.setText("Select All");
		selectAllBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				selectAll(true);
			}
		});

		Button deselectAllBtn = new Button(btnComposite, SWT.PUSH);
		deselectAllBtn.setText("De-select All");
		deselectAllBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				selectAll(false);
			}
		});
		
		Button selectTCBtn = new Button(btnComposite, SWT.PUSH);
		selectTCBtn.setText("(De)Select Specified TC");
		selectTCBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				selectSpeTC(false);
			}
		});		

		testCaseTreeViewer = new CheckboxTreeViewer(container);
		testCaseTreeViewer.getTree().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		testCaseTreeViewer.setContentProvider(unitCaseProvider);

		// viewer.setSorter(sorter);

		ILabelProvider labelProvider = new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_DEFAULT
						| JavaElementLabelProvider.SHOW_QUALIFIED);
		testCaseTreeViewer.setLabelProvider(labelProvider);
		testCaseTreeViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				boolean flag = event.getChecked();
				Object element = event.getElement();

				ITreeContentProvider provider = (ITreeContentProvider) testCaseTreeViewer
						.getContentProvider();

				if (isJunitTest)
				{
					if (element instanceof IMethod)
					{
						if (!flag) // OFF 할때면
						{
							Object currentElement = element;

							while (true)
							{
								Object parent = provider
										.getParent(currentElement);

								if (parent == null)
									break;

								testCaseTreeViewer.setChecked(parent, flag);
								currentElement = parent;
							}
						}
					}
					else if (element instanceof IType)
					{
						setChild(element, flag);
					}
				}
				else
				{
					if (element instanceof IFolder)
					{
						setChild(element, flag);
					}
					else if (element instanceof IFile)
					{
						if (!flag)
						{
							Object currentElement = event.getElement();
							while (true)
							{
								Object parent = testCaseProvider
										.getParent(currentElement);

								if (parent == null)
									break;

								testCaseTreeViewer.setChecked(parent, flag);
								currentElement = parent;
							}
						}
					}
				}
				dialogChanged();
			}

		});

		//
		dialogChanged();

		// notify that configuring UI is done to parent wizard
		setControl(container);
	}

	/**
	 * 
	 */
	void dialogChanged()
	{
		Object[] results = testCaseTreeViewer.getCheckedElements();

		if (isJunitTest)
		{
			selectedTests.clear();

			for (Object object : results)
			{
				if (object instanceof IMethod)
				{
					selectedTests.add((IMethod) object);
				}
			}

			if (selectedTests.size() == 0)
			{
				updateStatus("Select one test at least.");
				return;
			}
		}
		else
		{
			selectedTestClasses.clear();

			for (Object object : results)
			{
				if (object instanceof IFile)
				{
					selectedTestClasses.add((IFile) object);
				}
			}

			if (selectedTestClasses.size() == 0)
			{
				updateStatus("Select one test at least.");
				return;
			}
		}

		updateStatus(null);
	}

	public List<IFile> getSelectedTestCaseClasses()
	{
		return selectedTestClasses;
	}

	/**
	 * Returns a set of test cases to be executed.
	 * 
	 * @return
	 */
	public List<IMethod> getSelectedTestCases()
	{
		return selectedTests;
	}

	void selectSpeTC(boolean selected)
	{
		ITreeContentProvider provider = (ITreeContentProvider) testCaseTreeViewer
				.getContentProvider();

		Object[] objs = provider.getElements(null);
		for (Object obj : objs)
		{
			if(obj.toString().equals("test class")){
				testCaseTreeViewer.setChecked(obj, selected);
				
				Object[] child = provider.getChildren(obj);
			
				// 자식에 대한 처리 수행해라
				for (int i = 0; i < child.length; i++)
				{
					if(child[i].toString().equals("test method")){
						testCaseTreeViewer.setChecked(child[i], selected);
				
						if (testCaseTreeViewer.isExpandable(child[i]))
						{
							// 자식에 대해서도 전부 set한다.
							setChild(child[i], selected);
						}
					}
				}
			}
					
			
		}
		dialogChanged();
	}
	
	void selectAll(boolean selected)
	{
		ITreeContentProvider provider = (ITreeContentProvider) testCaseTreeViewer
				.getContentProvider();

		Object[] objs = provider.getElements(null);
		for (Object obj : objs)
		{
			testCaseTreeViewer.setChecked(obj, selected);
			setChild(obj, selected);
		}
		dialogChanged();
	}

	void setChild(Object element, boolean flag)
	{

		ITreeContentProvider provider = (ITreeContentProvider) testCaseTreeViewer
				.getContentProvider();
		Object[] child = provider.getChildren(element);

		for (int i = 0; i < child.length; i++)
		{
			testCaseTreeViewer.setChecked(child[i], flag);

			if (testCaseTreeViewer.isExpandable(child[i]))
			{
				// 자식에 대해서도 전부 set한다.
				setChild(child[i], flag);
			}
		}
	}

	/**
	 * Prepares test cases for the given mujava project. All test cases are
	 * scanned from eclipse project , and then collected.
	 * <p>
	 * It should be called when only previous page call <code>nextPage()</code>
	 * of Wizard.
	 * </p>
	 */
	public void setupTestCase(MuJavaProject muProject)
	{
		switch (muProject.getTestCaseType())
		{
		case MJ:
		{
			IProject eclipseProject = muProject.getResource().getProject();

			IFolder testRoot = eclipseProject.getFolder(muProject
					.getTestDirectory());

			testCaseTreeViewer.setContentProvider(testCaseProvider);
			testCaseTreeViewer.setInput(testRoot);
			isJunitTest = false;
		}
			break;

		case JU4:
		{
			IProject eclipseProject = muProject.getResource().getProject();

			try
			{
				IJavaProject javaProject = JavaCore.create(eclipseProject);
				
				final IType ifaceType = javaProject.findType("junit.framework",
						"TestCase");
				ITypeHierarchy typeHier = ifaceType.newTypeHierarchy(
						javaProject, null);
				IType[] testClsses = typeHier.getAllSubtypes(ifaceType);

				if (testClsses != null)
				{
					ArrayList<IType> classes = new ArrayList<IType>();

					for (IType type : testClsses)
					{
						IPackageFragment packageFragment = type
								.getPackageFragment();
						if (packageFragment == null)
						{
							continue;
						}

						String packageName = packageFragment.getElementName();
						if (!packageName.startsWith("junit"))
						{
							classes.add(type);
						}
					}

					testClsses = classes.toArray(new IType[classes.size()]);
				}

				testCaseTreeViewer.setContentProvider(unitCaseProvider);
				testCaseTreeViewer.setInput(testClsses);
				isJunitTest = true;

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
			break;
		
/*
		case JU4:
		{
			IProject eclipseProject = muProject.getResource().getProject();

			try
			{
				IJavaProject javaProject = JavaCore.create(eclipseProject);
				final IType ifaceType = javaProject.findType("junit.framework",
						"TestCase");
				ITypeHierarchy typeHier = ifaceType.newTypeHierarchy(
						javaProject, null);
				IType[] testClsses = typeHier.getAllSubtypes(ifaceType);

				if (testClsses != null)
				{
					ArrayList<IType> classes = new ArrayList<IType>();

					for (IType type : testClsses)
					{
						IPackageFragment packageFragment = type
								.getPackageFragment();
						if (packageFragment == null)
						{
							continue;
						}

						String packageName = packageFragment.getElementName();
						if (!packageName.startsWith("junit"))
						{
							classes.add(type);
						}
					}

					testClsses = classes.toArray(new IType[classes.size()]);
				}

				testCaseTreeViewer.setContentProvider(unitCaseProvider);
				testCaseTreeViewer.setInput(testClsses);
				isJunitTest = true;

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		break;
*/
			
		case JU3:
		{
			IProject eclipseProject = muProject.getResource().getProject();

			try
			{
				IJavaProject javaProject = JavaCore.create(eclipseProject);
				final IType ifaceType = javaProject.findType("junit.framework",
						"TestCase");
				ITypeHierarchy typeHier = ifaceType.newTypeHierarchy(
						javaProject, null);
				IType[] testClsses = typeHier.getAllSubtypes(ifaceType);

				if (testClsses != null)
				{
					ArrayList<IType> classes = new ArrayList<IType>();

					for (IType type : testClsses)
					{
						IPackageFragment packageFragment = type
								.getPackageFragment();
						if (packageFragment == null)
						{
							continue;
						}

						String packageName = packageFragment.getElementName();
						if (!packageName.startsWith("junit"))
						{
							classes.add(type);
						}
					}

					testClsses = classes.toArray(new IType[classes.size()]);
				}

				testCaseTreeViewer.setContentProvider(unitCaseProvider);
				testCaseTreeViewer.setInput(testClsses);
				isJunitTest = true;

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
			break;
		default:
			testCaseTreeViewer.setContentProvider(unitCaseProvider);
			isJunitTest = true;
			break;
		}
	}

	/**
	 * 
	 * @param message
	 */
	void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}

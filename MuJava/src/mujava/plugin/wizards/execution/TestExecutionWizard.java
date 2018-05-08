package mujava.plugin.wizards.execution;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaProject;
import mujava.MutantOperator;
import mujava.MutantTable;
import mujava.gen.GenerationType;
import mujava.plugin.wizards.MutantOperatorPage;
import mujava.plugin.wizards.ValidMuJavaProjectPage;
import mujava.util.ReachTable;
import mujava.util.ReachTableGenerator.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

public class TestExecutionWizard extends Wizard implements INewWizard,
		IDoubleClickListener
{
	public static final String TRADITIONAL_LITERAL = "TR";

	public static final String CLASS_LITERAL = "CL";

	private static File makeOutputFile(String fileName, boolean isOverwritten)
	{
		String ext = Path.fromOSString(fileName).getFileExtension();
		if (ext == null)
		{
			ext = "";
		}

		String header = fileName;
		if (!ext.isEmpty())
		{
			int index = fileName.lastIndexOf(ext);
			header = fileName.substring(0, index - 1);
		}

		StringBuffer sb = new StringBuffer(header);
		int counter = 0;
		while (true)
		{
			sb.append(".");
			sb.append(ext);

			File file = new File(sb.toString());

			if (isOverwritten || !file.exists())
			{
				return file;
			}

			counter++;
			sb.setLength(header.length());
			sb.append(counter);
		}
	}

	private ValidMuJavaProjectPage projectSelectionPage;

	private MutantOperatorPage mutantOperatorPage;

	private TestCasePage testCasePage;

	private TestOptionPage testCaseOptionPage;

	private ISelection selection;

	private IResource selectedProjectResource = null;

	public TestExecutionWizard()
	{
		super();
		setNeedsProgressMonitor(true);
	}

	public TestExecutionWizard(ISelection selection)
	{
		this();

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object item = sSelection.getFirstElement();
			if (item instanceof IResource)
			{
				if (((IResource) item).getFileExtension().equals("mjp"))
				{
					selectedProjectResource = (IResource) item;
				}
			}
		}
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages()
	{
		// create each page to be added to this wizard
		if (selectedProjectResource == null)
		{
			projectSelectionPage = new ValidMuJavaProjectPage(selection, true);
			projectSelectionPage.addListener(this);
		}
		mutantOperatorPage = new MutantOperatorPage(selection);
		testCasePage = new TestCasePage(selection);
		testCaseOptionPage = new TestOptionPage(selection);

		// adding the pages
		if (projectSelectionPage != null)
		{
			addPage(projectSelectionPage);
		}
		addPage(mutantOperatorPage);
		addPage(testCasePage);
		addPage(testCaseOptionPage);
	}

	/*
	 * check whether we obtain needed information from this wizard or not
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish()
	{
		if (testCaseOptionPage != null && testCasePage != null)
		{
			if (testCaseOptionPage.isPageComplete()
					&& testCasePage.isPageComplete())
				return true;
		}
		return false;
	}

	public void createPageControls(Composite pageContainer)
	{
		super.createPageControls(pageContainer);

		// Project File에서 Popup menu에서 Wizard가 수행되는 경우
		if (selectedProjectResource != null)
		{

			IRunnableWithProgress op = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException
				{
					try
					{
						settingMutantOperatorPage(null, null, monitor);
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
					finally
					{
						monitor.done();
					}
				}
			};

			try
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.run(false, false, op);
			}
			catch (InterruptedException e)
			{
				return;
			}
			catch (InvocationTargetException e)
			{
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error",
						realException.getMessage());
				return;
			}
		}

	}

	private void createReachTable(File logFile, String classReachFileName,
			String methodReachFileName, String pathReachFileName,
			boolean isOverwritten)
	{

		MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				selectedProjectResource, null);

		MutantTable mutantTable = null;

		try
		{
			mutantTable = MutantTable.getMutantTable(muProject, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		ReachTable table = new ReachTable();
		table.appendLogFile(logFile);

		// Create XML file

		MuJavaLogger.getLogger().info("Reach Table is Creating...");
		File outputFile;
		/*
		 File outputFile = makeOutputFile(classReachFileName, isOverwritten);
		 

		MuJavaLogger.getLogger().info(
				"Reach Table for CLASS (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.CLASS);

		outputFile = makeOutputFile(methodReachFileName, isOverwritten);
		MuJavaLogger.getLogger().info(
				"Reach Table for METHOD (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.METHOD);
		 */
		outputFile = makeOutputFile(pathReachFileName, isOverwritten);
		MuJavaLogger.getLogger().info(
				"Reach Table for PATH (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.PATH);

		MuJavaLogger.getLogger().info("Reach Table is Created");

	}

	/**
	 * ValidMuJavaProjectPage에서 project를 double click 하면 강제로 다음 page로 넘겨준다. 이를
	 * 위해 IDoubleClickListener를 구현한다
	 */
	public void doubleClick(DoubleClickEvent event)
	{
		if (projectSelectionPage == null)
		{
			return;
		}

		if (!projectSelectionPage.canFlipToNextPage())
		{
			return;
		}

		IWizardPage nextPage = getNextPage(projectSelectionPage);
		if (nextPage == null)
		{
			return;
		}

		IWizardContainer container = getContainer();
		if (container == null)
		{
			return;
		}

		container.showPage(nextPage);
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page)
	{
		final IWizardPage nextPage = super.getNextPage(page);

		if (page instanceof ValidMuJavaProjectPage)
		{
			
			IRunnableWithProgress op = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException
				{
					selectedProjectResource = projectSelectionPage.getSelectedMuJavaProject();;			// added by ysma 2013/10/16
					try
					{
						settingMutantOperatorPage(nextPage, page, monitor);
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
					finally
					{
						monitor.done();
					}
				}
			};

			try
			{
				getContainer().run(false, false, op);
			}
			catch (InterruptedException e)
			{
				return null;
			}
			catch (InvocationTargetException e)
			{
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error",
						realException.getMessage());
				return null;
			}
		}
		else if (page instanceof MutantOperatorPage)
		{
			IRunnableWithProgress op1 = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException
				{
					settingTestCasePage(nextPage, page, monitor);
				}
			};

			try
			{
				getContainer().run(false, false, op1);
			}
			catch (InterruptedException ie)
			{
				return null;
			}
			catch (InvocationTargetException ite)
			{
				Throwable realException = ite.getTargetException();
				MessageDialog.openError(getShell(), "Error",
						realException.getMessage());
				return null;
			}
		}
		else if (page instanceof TestCasePage)
		{
			IProject project = selectedProjectResource.getProject();
			testCaseOptionPage.setProject(selectedProjectResource, project);
		}

		return nextPage;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.selection = selection;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 * However, an actual execution logic is in executeMutants method.
	 * 
	 * @see TestExecutionWizard#executeTestCases(List, List, List, int,
	 *      IProgressMonitor)
	 */
	public boolean performFinish()
	{
		// Check whether the task executes mutants for reachability analysis
		//IResource muRes = projectSelectionPage.getSelectedMuJavaProject();	// deleted by ysma 2013/10/16
		MuJavaProject muPrj = MuJavaProject.getMuJavaProject(selectedProjectResource, null);
		boolean isReachabilityAnaysis = (muPrj.getGenerationWay() == GenerationType.REACH);

		// Common Execution Option
		final List<String> tOps = mutantOperatorPage
				.getSelectedTraditonalOpeator();
		final List<String> cOps = mutantOperatorPage.getSelectedClassOpeator();
		final List<IMethod> testCases = testCasePage.getSelectedTestCases();
		final List<IFile> testCaseClasses = testCasePage
				.getSelectedTestCaseClasses();
		final int timeout = testCaseOptionPage.getTimeout();

		// Execute Reachability Analysis
		if (isReachabilityAnaysis)
		{
			String logFileName = testCaseOptionPage.getReachLogFileName();
			File logFile = new File(logFileName);

			boolean isNotExecuted = testCaseOptionPage.isReachLogReused();
			if (!isNotExecuted)
			{
				boolean isLogOverwritten = testCaseOptionPage
						.isReachLogOverwritten();

				// Reachability Analysis 를 고려한 별도 로그에 저장
				MuJavaLogger.getLogger().recordReachAnalysisLog(logFile,
						isLogOverwritten);

				performReachabilityAnalysis(tOps, cOps, testCases,
						testCaseClasses, timeout);

				MuJavaLogger.getLogger().stopReachAnalysisLog();

				MuJavaLogger.getLogger().clearReachAnalysisLog();
			}
			
			// Reachability Analysis 후작업 진행
			String classReachFileName = testCaseOptionPage
					.getClassReachFileName();
			String methodReachFileName = testCaseOptionPage
					.getMethodReachFileName();
			String pathReachFileName = testCaseOptionPage
					.getPathReachFileName();
			boolean isTableOverwritten = testCaseOptionPage
					.isReachTableOverwritten();
			createReachTable(logFile, classReachFileName, methodReachFileName,
					pathReachFileName, isTableOverwritten);

			return true;
		} else{

			// For executing mutants for mujava
			String reachTableFileName = testCaseOptionPage.getFileName();
			//String hashCodeFileName = testCaseOptionPage.getHashCodeFileName();
			String equivalentFileName = testCaseOptionPage.getEquFileName();
			int occurrenceType = testCaseOptionPage.getOccurenceType();
	
			boolean cmMode = testCaseOptionPage.isCMMode();
			int iteration = testCaseOptionPage.getIteration();
			boolean canExportExcel = testCaseOptionPage.canGenerateExcelReport();
	
	/*		return performMutationTesting(tOps, cOps, testCases, testCaseClasses,
					timeout, reachTableFileName, hashCodeFileName,
					equivalentFileName, occurrenceType, cmMode, iteration,
					canExportExcel); */
					return performMutationTesting(tOps, cOps, testCases, testCaseClasses,
									timeout, reachTableFileName, equivalentFileName, 
									occurrenceType, cmMode, iteration,canExportExcel); 
		}
	}

	private boolean performMutationTesting(List<String> tOps,
			List<String> cOps, List<IMethod> testCases,
			List<IFile> testCaseClasses, int timeout,
			String reachTableFileName, 
			String equivalentFileName, int occurrenceType, boolean cmMode,
			int iteration, boolean canExportExcel)
	{

		try
		{
			for (int i = 0; i < iteration; i++)
			{
				TestExecutionTask op = new TestExecutionTask(
						selectedProjectResource, tOps, cOps, testCases,
						testCaseClasses);

				op.setTimeout(timeout);
				op.setDisplay(getShell().getDisplay());

				op.setCMMode(cmMode);
				op.setOccurrenceType(occurrenceType);
				op.setExcelExport(canExportExcel);
				op.setExternalFiles(reachTableFileName, equivalentFileName);

				getContainer().run(true, false, op);
			}

		}
		catch (InterruptedException e)
		{
			return false;
		}
		catch (InvocationTargetException e)
		{
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());

			return false;
		}

		return true;
	}

	private void performReachabilityAnalysis(List<String> tOps,
			List<String> cOps, List<IMethod> testCases,
			List<IFile> testCaseClasses, int timeout)
	{
		try
		{
			TestExecutionTask op = new TestExecutionTask(
					selectedProjectResource, tOps, cOps, testCases,
					testCaseClasses);
			op.setDisplay(getShell().getDisplay());
			op.setTimeout(timeout);

			getContainer().run(true, false, op);

			MuJavaLogger.getLogger().getReachAnalaysisLog();
		}
		catch (InterruptedException e)
		{
			// DO NOTHING
		}
		catch (InvocationTargetException e)
		{
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
		}

	}

	/*
	 * Set the information to show in case it takes too long time to save the
	 * first page
	 */
	void settingMutantOperatorPage(IWizardPage page, IWizardPage oriPage,
			IProgressMonitor monitor) throws CoreException
	{

		monitor.beginTask("setting next page...", 2);
		if (selectedProjectResource == null
				|| (page != null && oriPage != null))
		{
			selectedProjectResource = ((ValidMuJavaProjectPage) oriPage)
					.getSelectedMuJavaProject();
		}
		MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				selectedProjectResource, monitor);
		mutantOperatorPage.setMujavaProject(muProject);

		monitor.worked(1);
		monitor.setTaskName("setting project information...");

		Map<String, Integer> tMap = new HashMap<String, Integer>();
		Map<String, Integer> cMap = new HashMap<String, Integer>();

		MutantTable mTable = null;
		try
		{
			mTable = MutantTable.getMutantTable(muProject, monitor);

			for (String operator : MutantOperator.getAllTraditionalOperators())
			{
				int size = mTable.getMutantCount(operator);
				tMap.put(operator, size);
			}

			for (String operator : MutantOperator.getAllClassicalOperators())
			{
				int size = mTable.getMutantCount(operator);
				cMap.put(operator, size);
			}

			mutantOperatorPage.setMutantData(tMap, cMap);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		monitor.worked(1);
	}

	private void settingTestCasePage(IWizardPage nextPage, IWizardPage page,
			IProgressMonitor monitor)
	{
		monitor.beginTask("setting next page...", 2);
		final MuJavaProject muProject = ((MutantOperatorPage) page)
				.getMuJavaProject();
		((TestCasePage) nextPage).setupTestCase(muProject);

		monitor.worked(1);
	}
}

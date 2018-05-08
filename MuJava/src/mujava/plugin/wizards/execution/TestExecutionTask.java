package mujava.plugin.wizards.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kaist.selab.util.MuJavaLogger;
import mujava.GeneratedCodeTable;
import mujava.MuJavaProject;
import mujava.MutantManager;
import mujava.MutantTable;
import mujava.ResultTable;
import mujava.executor.TestExecutor;
import mujava.gen.MuJavaMutationEngine;
import mujava.gen.PerformanceElement;
import mujava.util.EquivalentTable;
import mujava.util.ExecutionCountTable;
import mujava.util.KilledMutantTable;
import mujava.util.ReachTable;
import mujava.util.RecordStatistics;
import mujava.util.SourceTable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class TestExecutionTask implements IRunnableWithProgress
{
	MuJavaProject muProject = null;
	ReachTable reachTable = new ReachTable();
	SourceTable sourceTable = null;
	EquivalentTable eqTable = new EquivalentTable();
	TestExecutor executor = null;
	ResultTable rTable = null;
	RecordStatistics statistics = null;

	List<String> tOps = new ArrayList<String>();

	List<String> cOps = new ArrayList<String>();

	List<IMethod> testCases = new ArrayList<IMethod>();

	List<IFile> testCaseClasses = new ArrayList<IFile>();

	private int timeout;
	private int occurrenceType;
	private boolean canExportExcel = false;
	private boolean cmMode = false;
	private String reachTableFile;
	private String hashcodeFile;
	private String equivalenceFile;
	private IResource projectResource = null;
	private Display display;

	public TestExecutionTask(IResource projectResource, List<String> tOps,
			List<String> cOps, List<IMethod> testCases,
			List<IFile> testCaseClasses)
	{
		this.projectResource = projectResource;

		this.tOps.clear();
		this.tOps.addAll(tOps);

		this.cOps.clear();
		this.cOps.addAll(cOps);

		this.testCases.clear();
		this.testCases.addAll(testCases);

		this.testCaseClasses.clear();
		this.testCaseClasses.addAll(testCaseClasses);
	}

	private TestExecutor createTestExecutor(MuJavaProject muProject,
			IProgressMonitor monitor)
	{

		TestExecutor executor = new TestExecutor(muProject, monitor);
		executor.setTimeOut(timeout);
		executor.setConditionalMode(cmMode);

		return executor;
	}

	private void executeTestCases(IProgressMonitor monitor)
	{
		boolean FOR_PAPER = false;
		executor = createTestExecutor(muProject, monitor);
		executor.setStatisticHandler(statistics);

		switch (muProject.getTestCaseType())
		{
		case NONE:
			break;
		case JU4:
		case JU3:
			
			if(FOR_PAPER){
				executor.executeJUnitTestCasesForPaper(tOps, cOps, testCases, reachTable,
				eqTable);
			}else{
				executor.executeJUnitTestCases(tOps, cOps, testCases, reachTable,eqTable);
				
			}
					
					
			
			break;
		case MJ:
			executor.executeTestCases(tOps, cOps, testCaseClasses, reachTable,
					eqTable);
			break;
		}

		rTable = executor.getResultTable();
	}

	private void executeTests(IProgressMonitor monitor)
	{

		prepareMuJavaProject(monitor);

		prepareMutantManager(monitor);

		prepareTables(monitor);

		executeTestCases(monitor);
	}

	private void exportExcel(IProgressMonitor monitor)
	{

		if (statistics != null)
		{

			IResource res = rTable.getResource();
			IPath path = res.getLocation();
			path = path.removeFileExtension();
			String name = path.lastSegment();
			path = path.removeLastSegments(1);
			path = path.append(name + "-count");
			path = path.addFileExtension("xlsx");
			File file = path.toFile();

			try
			{
				statistics.writeExcel(file, sourceTable, eqTable, tOps);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

		}
	}

	private void finalizeTestResult(IProgressMonitor monitor)
	{
		if (rTable != null)
		{
			rTable.setCMMode(cmMode);

			try
			{
				rTable.save(monitor);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		exportExcel(monitor);

		monitor.worked(1);
	}

	private void prepareCodeSetTable(IProgressMonitor monitor)
	{

		MuJavaLogger.getLogger().debug("Make Concreate CodeSet Table");
		if (monitor != null)
		{
			monitor.subTask("Make Concreate CodeSet Table");
		}

		try
		{
			GeneratedCodeTable.getGeneratedCodeTable(muProject, monitor);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private void prepareEquivalentTable(final String equivalentFileName,
			IProgressMonitor monitor)
	{

		if (equivalentFileName != null && !equivalentFileName.isEmpty())
		{
			eqTable.parseTargetFile(new File(equivalentFileName), sourceTable);
		}
	}

	private void prepareMuJavaProject(IProgressMonitor monitor)
	{
		muProject = MuJavaProject.getMuJavaProject(projectResource, monitor);
	}

	private void prepareMutantManager(IProgressMonitor monitor)
	{
		monitor.subTask("Getting project");
		MuJavaLogger.getLogger().debug("Getting project");

		MutantManager manager = MutantManager.getMutantManager();

		if (monitor != null)
		{
			monitor.worked(1);
		}

		manager.setMuJavaProject(muProject);

		if (monitor != null)
		{
			monitor.worked(1);
		}

	}

	private void prepareMutantTable(IProgressMonitor monitor)
	{
		try
		{
			MutantTable.getMutantTable(muProject, monitor);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private void prepareReachTable(final String reachTableName)
	{
		// Reachability File Parsing
		if (reachTableName != null && !reachTableName.isEmpty())
		{
			reachTable = ReachTable.load(reachTableName);
			MuJavaLogger.getLogger().debug("Reach Table : " + reachTableName);
		}
	}

	private void prepareSourceTable(final String hashCodeFileName,
			IProgressMonitor monitor)
	{

		if (hashCodeFileName != null && !hashCodeFileName.isEmpty())
		{
			sourceTable = SourceTable.load(hashCodeFileName);
		}
	}

	private void prepareStatisticsTable(IProgressMonitor monitor)
	{

		if (canExportExcel)
		{
			switch (occurrenceType)
			{
			case 1:
				statistics = new KilledMutantTable();
				break;
			case 2:
				statistics = new ExecutionCountTable();
				break;
			}
		}

		if (statistics != null)
		{
			try
			{
				MutantTable mTable = MutantTable.getMutantTable(muProject,
						monitor);

				statistics.putMutants(mTable);

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}

			Set<String> testNames = new HashSet<String>();

			switch (muProject.getTestCaseType())
			{
			case MJ:
				for (IFile testCase : testCaseClasses)
				{
					if (testCase.getFileExtension() != null)
					{
						IPath path = testCase.getFullPath()
								.removeFileExtension();
						String name = path.lastSegment();
						testNames.add(name);
					}
				}
				break;
			case JU4:
				for (IMethod method : testCases)
				{
					testNames.add(method.getElementName());
				}
				break;
			case JU3:
				for (IMethod method : testCases)
				{
					testNames.add(method.getElementName());
				}
				break;
			default:
				break;
			}

			statistics.setTestCase(testNames);
		}
	}

	private void prepareTables(IProgressMonitor monitor)
	{

		prepareMutantTable(monitor);

		prepareCodeSetTable(monitor);

		prepareReachTable(reachTableFile);

		//prepareSourceTable(hashcodeFile, monitor);
		// ysma at 2013.11.18
		String hashFileName = muProject.getDirectory()+ File.separator + "Mutants" + File.separator + muProject.getName() + File.separator + "hash.table";
	
			
		prepareSourceTable(hashFileName, monitor);

		prepareEquivalentTable(equivalenceFile, monitor);

		prepareStatisticsTable(monitor);
	}

	private void recordPerformance(long buildTime, PerformanceElement diff,
			IProgressMonitor monitor)
	{

		if (executor != null)
		{
			rTable = executor.getResultTable();
			rTable.setActualTime(diff.getTimeCost());
			rTable.setBuildTime(buildTime);
			rTable.setUsedDiskSpace(diff.getDiskCost());
			rTable.setUsedMemorySpace(diff.getMemoryCost());
			rTable.setUsedReachTableName(reachTableFile);
		}

	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException
	{

		MuJavaLogger.getLogger().info("Start...");

		long buildTime = System.currentTimeMillis();

		PerformanceElement start = PerformanceElement.getCurrent();

		executeTests(monitor);

		// get the current time to measure whole process time
		PerformanceElement diff = PerformanceElement.getDifferentFromNow(start);

		recordPerformance(buildTime, diff, monitor);

		finalizeTestResult(monitor);

		showTestResult(monitor);

		// 20131210
		rTable.clearResultTable();
		
		MuJavaLogger.getLogger().info("Complete...");
		monitor.done();
	}

	public void setCMMode(boolean cmMode)
	{
		//this.cmMode = cmMode; //msyu
		cmMode = false;
	}

	public void setDisplay(Display display)
	{
		this.display = display;
	}

	public void setExcelExport(boolean canExportExcel)
	{
		this.canExportExcel = canExportExcel;
	}

	public void setExternalFiles(String dependFileName,
			String hashCodeFileName, String equivalentFileName)
	{
		this.reachTableFile = dependFileName;
		this.hashcodeFile = hashCodeFileName;
		this.equivalenceFile = equivalentFileName;
	}

	public void setExternalFiles(String dependFileName,
			String equivalentFileName)
	{
		this.reachTableFile = dependFileName;
		this.equivalenceFile = equivalentFileName;
	}

	public void setOccurrenceType(int occurrenceType)
	{
		this.occurrenceType = occurrenceType;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	private void showTestResult(IProgressMonitor monitor)
	{
		monitor.subTask("Opening Result Table viewer...");

		if (this.display != null)
		{
			this.display.asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					try
					{
						IDE.openEditor(page, (IFile) rTable.getResource(), true);
					}
					catch (PartInitException e)
					{
					}
				}
			});
		}

		monitor.worked(1);
	}
}

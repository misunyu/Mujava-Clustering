package mujava.executor;

import interstate.InterState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import kaist.selab.util.MuJavaLogger;
import mujava.CodeSet;
import mujava.GeneratedCodeTable;
import mujava.MuJavaMutantInfo;
import mujava.MuJavaProject;
import mujava.MutantID;
import mujava.MutantResult;
import mujava.MutantTable;
import mujava.ResultTable;
import mujava.TestCaseType;
import mujava.exception.NoTestMethodException;
import mujava.plugin.MuJavaPlugin;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.plugin.wizards.execution.TestExecutionWizard;
import mujava.util.EquivalentTable;
import mujava.util.ReachTable;
import mujava.util.RecordStatistics;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;


public class TestExecutor
{

	private Set<String> lastCommonClassPath = null;

	private Object lastProject;

	private Object lockObject = new Object();

	private IProgressMonitor monitor;

	private MuJavaProject muProject = null;

	private int TIMEOUT = 3000;

	ResultTable resultTable = null;
	MutantTable mutantTable = null;
	EquivalentTable eqTable = null;
	MutantResult currentMutantResult = null;
	Set<MutantID> sideEffectChangePoints = new HashSet<MutantID>();
	ITestExecutor executor = null;

	public static boolean WK_WRITE = true;
	
	private ReachTable reachTable = null;

	boolean forceToExecuteKilledMutant = false;


	ExecutorService pool = java.util.concurrent.Executors
			.newSingleThreadExecutor();

	ReachabilityAnalysisExecutor raExecutor = new ReachabilityAnalysisExecutor();

	MuJavaMutantExecutor muExecutor = new MuJavaMutantExecutor();

	NuJavaMutantExecutor nuExecutor = new NuJavaMutantExecutor();

	MuJavaMSGMutantExecutor msgExecutor = new MuJavaMSGMutantExecutor();

	MuJavaMutantStateExecutor muStateExecutor = new MuJavaMutantStateExecutor();

	public TestExecutor(MuJavaProject muProject, IProgressMonitor monitor)
	{
		super();

		this.monitor = monitor;
		this.muProject = muProject;

		try
		{
			resultTable = ResultTable.createResultTable(muProject, monitor);
		}
		catch (IOException e)
		{
			MuJavaLogger.getLogger().info("[Can not make result table !!!]");
		}

		decideExecutor(this);

	}

	/**
	 * Project Type에 따라 Executor를 배정한다.
	 * 
	 * @param testExecutor
	 */
	private void decideExecutor(TestExecutor testExecutor)
	{

		switch (muProject.getGenerationWay())
		{
		case WS:
			if(WK_WRITE) {
				nuExecutor.weaklyMutantsOutDir = muProject.getDirectory().concat(File.separator + "States" + File.separator + muProject.getName());
				//System.out.println("weaklyMutantsOutDir: " + nuExecutor.weaklyMutantsOutDir);
				muStateExecutor.testsDir = muProject.getDirectory().concat(File.separator + "Tests");
				String baseDir =  muProject.getDirectory().concat(File.separator + "States" + File.separator + muProject.getName());

				muStateExecutor.weaklyMutantsInDir = baseDir;
				muStateExecutor.stateDir =  baseDir;
			}
			executor = nuExecutor;
			break;
		case MSG:
			executor = msgExecutor;
			break;
		case REACH:
			executor = raExecutor;
			break;
		case SCC:
			//muStateExecutor.testsDir = muProject.getDirectory().concat(File.separator + "Tests");
			muStateExecutor.testsDir = muProject.getDirectory().concat(File.separator + "Tests");
			String baseDir =  muProject.getDirectory().concat(File.separator + "States" + File.separator + muProject.getName());
			//muStateExecutor.weaklyMutantsInDir = baseDir;
			//muStateExecutor.stateDir =  baseDir;
			muStateExecutor.weaklyMutantsInDir = baseDir;
			muStateExecutor.stateDir =  baseDir;

			executor = muStateExecutor;
			break;

		default:
			executor = muExecutor;
		}

		executor.setExecutor(this);

	}

	protected void addAnalysisMutantResult(MuJavaMutantInfo mutant, long time)
	{

		MutantResult mResult = new MutantResult();
		mResult.setMutantID(mutant.getMutantID());
		mResult.setMutantOperator(mutant.getMutationOperatorName());
		mResult.setMutantResult("");
		mResult.setOriginalResult("");

		mResult.setComparingTime(0);
		mResult.setExecutingTime(0, false);
		mResult.setExecutingTime(0, true);
		mResult.setExecutingTime(0);
		mResult.setAnalysisTime(time);
		mResult.setLoadingTime(0);
		mResult.setPreparingTime(0);
		// mResult.setTotalTime(time);

		resultTable.addMutantResult(mResult, mutant.getTargetFile());
	}

	/**
	 * gather all possible mutants to be executed at this time except for killed
	 * mutant or already inserted mutant
	 * 
	 * @param mutant
	 * 
	 */
	protected Set<String> addEmptyCodeSetResult(MuJavaMutantInfo mutant)
	{
		Set<String> mutants = new HashSet<String>();

		try
		{
			GeneratedCodeTable gTable = GeneratedCodeTable
					.getGeneratedCodeTable(muProject, monitor);

			String codeSetID = mutant.getCodeID();
			if (codeSetID != null && !codeSetID.isEmpty())
			{
				mutants.addAll(gTable.getMutantIDs(codeSetID));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		for (String mutantID : mutants)
		{
			if (!resultTable.hasMutantResultByID(mutantID))
			{
				addEmptyMutantResult(mutantID, mutant.getTargetFile());
			}
		}

		return mutants;
	}

	public void addEmptyMutantResult(String mutantID, String targetFile)
	{
		addEmptyMutantResult(mutantID, targetFile, 0);
	}

	private void addEmptyMutantResult(String mutantID, String targetFile,
			long executionTime)
	{

		MutantResult mResult = new MutantResult();
		mResult.setMutantID(mutantID);
		mResult.setComparingTime(0);

		if (executionTime == 0)
		{
			mResult.setExecutingTime(0, false);
			mResult.setExecutingTime(0, true);
		}
		else
		{
			mResult.setExecutingTime(executionTime);
		}

		mResult.setAnalysisTime(0);
		mResult.setLoadingTime(0);
		mResult.setMutantOperator(MutantID.getMutationOperator(mutantID));
		mResult.setMutantResult("");
		mResult.setOriginalResult("");
		mResult.setPreparingTime(0);

		resultTable.addMutantResult(mResult, targetFile);
	}

	protected void addMutantResult(MuJavaMutantInfo mutant, MutantResult mResult)
	{
		if (!forceToExecuteKilledMutant)
		{
			this.resultTable.addMutantResult(mResult, mutant.getTargetFile());
		}
	}

	public void addOriginalResult(MutantResult mResult)
	{
		this.resultTable.addOriginalResult(mResult);

	}

	/**
	 * Reachablity를 검사하고, reach하지 않는 경우는 bypass한다.단, mutant가 수행되었음을 기록하기 위해
	 * Empty Mutant Result를 호출한다.
	 * 
	 * @param tc
	 * @param mutant
	 * @return
	 */
	protected boolean checkReachability(TestCase tc, MuJavaMutantInfo mutant)
	{
		// 초기화 되지 않았거나, 호출이 정상적이지 않아서 reachTable이 정의되지 않은 경우는 모든 mutant를 수행할 수
		// 있도록 true 를 반환한다.
		if (reachTable == null || reachTable.isEmpty())
		{
			return true;
		}

		String mutantID = mutant.getMutantID();
		String testID = tc.toSimpleString();
		testID = testID.replace("()", "");

		// Result Table 에 test 와 mutant 관계가 존재하지 않은 경우는 시험을 진행하지 않음
		if (reachTable.isReachedMutant(testID, mutantID))
		{
			return true;
		}

		// MuJavaLogger.getLogger().reach("[NON-REACH] MUTANT (" + mutantID +
		// " )for "
		// + testID);

		if (!resultTable.hasMutantResultByID(mutantID))
		{
			// prepare default result
			addEmptyMutantResult(mutantID, mutant.getTargetFile());
		}

		return false;
	}

	protected boolean checkSimpleReachability(TestCase tc, String mutantID)
	{
		// 초기화 되지 않았거나, 호출이 정상적이지 않아서 reachTable이 정의되지 않은 경우는 모든 mutant를 수행할 수
		// 있도록 true 를 반환한다.
		if (reachTable == null || reachTable.isEmpty())
		{
			return true;
		}
		String testID = tc.toSimpleString();
		testID = testID.replace("()", "");


		// Result Table 에 test 와 mutant 관계가 존재하지 않은 경우는 시험을 진행하지 않음
		if (reachTable.isReachedMutant(testID, mutantID))
		{
			return true;
		}
		
		return false;

	}

	public boolean checkReachability(TestCase tc, String mutantID)
	{
		// 초기화 되지 않았거나, 호출이 정상적이지 않아서 reachTable이 정의되지 않은 경우는 모든 mutant를 수행할 수
		// 있도록 true 를 반환한다.
		if (reachTable == null || reachTable.isEmpty())
		{
			return true;
		}

		String testID = tc.toSimpleString();
		testID = testID.replace("()", "");

		// Result Table 에 test 와 mutant 관계가 존재하지 않은 경우는 시험을 진행하지 않음
		if (reachTable.isReachedMutant(testID, mutantID))
		{
			return true;
		}

		// MuJavaLogger.getLogger().reach("[NON-REACH] MUTANT (" + mutantID +
		// " )for "
		// + testID);

		if (!resultTable.hasMutantResultByID(mutantID))
		{
			// prepare default result
			//addEmptyMutantResult(mutantID, mutant.getTargetFile());
		}

		return false;
	}		
	Comparator<IFile> testClassComparator = new Comparator<IFile>()
	{

		private String getStringHeader(String str)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++)
			{
				char ch = str.charAt(i);
				if (Character.isDigit(ch))
				{
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}

		@Override
		public int compare(IFile file0, IFile file1)
		{

			String arg0 = file0.getName();
			if (arg0.indexOf(".") != -1)
			{
				arg0 = arg0.substring(0, arg0.indexOf("."));
			}
			String arg1 = file1.getName();
			if (arg1.indexOf(".") != -1)
			{
				arg1 = arg1.substring(0, arg1.indexOf("."));
			}
			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0)
			{
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try
				{
					value1 = Integer.parseInt(integerStr1);
				}
				catch (NumberFormatException e)
				{
				}

				int value2 = 0;
				try
				{
					value2 = Integer.parseInt(integerStr2);
				}
				catch (NumberFormatException e)
				{
				}

				return value1 - value2;
			}

			return diff;
		}
	};

	Comparator<Method> testMethodComparator = new Comparator<Method>()
	{

		private String getStringHeader(String str)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++)
			{
				char ch = str.charAt(i);
				if (Character.isDigit(ch))
				{
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}

		@Override
		public int compare(Method m0, Method m1)
		{

			String arg0 = m0.getDeclaringClass().getSimpleName();
			if (arg0.indexOf(".") != -1)
			{
				arg0 = arg0.substring(0, arg0.indexOf("."));
			}
			String arg1 = m1.getDeclaringClass().getSimpleName();
			if (arg1.indexOf(".") != -1)
			{
				arg1 = arg1.substring(0, arg1.indexOf("."));
			}
			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0)
			{
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try
				{
					value1 = Integer.parseInt(integerStr1);
				}
				catch (NumberFormatException e)
				{
				}

				int value2 = 0;
				try
				{
					value2 = Integer.parseInt(integerStr2);
				}
				catch (NumberFormatException e)
				{
				}

				return value1 - value2;
			}

			return diff;
		}
	};

	Comparator<IMethod> testJunitMethodComparator = new Comparator<IMethod>()
	{
		private String getStringHeader(String str)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++)
			{
				char ch = str.charAt(i);
				if (Character.isDigit(ch))
				{
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}

		@Override
		public int compare(IMethod m0, IMethod m1)
		{
			IType cls0 = (IType) m0.getParent();
			IType cls1 = (IType) m1.getParent();

			String arg0 = cls0.getElementName();
			String arg1 = cls1.getElementName();

			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0)
			{
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try
				{
					value1 = Integer.parseInt(integerStr1);
				}
				catch (NumberFormatException e)
				{
				}

				int value2 = 0;
				try
				{
					value2 = Integer.parseInt(integerStr2);
				}
				catch (NumberFormatException e)
				{
				}

				return value1 - value2;
			}

			return diff;
		}
	};

	/**
	 * TestExecutor로 하여금 Execution을 수행시키는 함수.( it has a sub-task (1 worked))
	 * 
	 * @param tOps
	 * @param cOps
	 * @param resources
	 * @param eqTable
	 * @param statistics
	 * @param table
	 * @param monitor
	 */
	public void executeTestCases(List<String> tOps, List<String> cOps,
			List<IFile> resources, ReachTable reachTable,
			EquivalentTable eqTable)
	{

		printPreLog();

		HashMap<Method, TestCase> testMethods = loadTestMethods(resources,
				monitor);

		registerTables(reachTable, eqTable);

		setupTables(reachTable, testMethods.values(), tOps);

		setupMonitorProgressInfo(tOps, cOps, testMethods.size());

		executor.beforeRunAllTestMethod();

		List<Method> sortedTests = new ArrayList<Method>(testMethods.keySet());
		Collections.sort(sortedTests, testMethodComparator);

		//msyu
		if(executor == muStateExecutor) {
			((MuJavaMutantStateExecutor)executor).loadWeaklyMutantIDs("weakly_killed");			
			muStateExecutor.initResultFiles(muStateExecutor.stateDir);
		}
		
		for (Method testMethod : sortedTests) {

			MuJavaLogger.getLogger().debug(
					testMethod.toString() + "is Executed");

			// Result of original, compared with those of mutants
			executor.beforeRunATestMethod();

			TestCase tc = testMethods.get(testMethod);

			// start for tOps
			for (String tOp : tOps)
			{
				runMutants(tOp, testMethod, tc, monitor);
			}
			
			//msyu			
			if (executor == muStateExecutor) {				
				InterState.moveStateFiles (muStateExecutor.stateDir  + File.separator + "ResultStates", tc.toSimpleString());
			}
			
			// // start for cOps
			// for (String cOp : cOps) {
			// runMutants(mTable, cOp, testMethod, tc, eqTable, monitor);
			// }
		}

		executor.afterRunAllTestMethod();
		
		if(executor == nuExecutor && WK_WRITE) {
			((NuJavaMutantExecutor)executor).writeWeaklyMutantIDs("weakly_killed_" + reachTable.hashCode() + ".table");
		} /*else if (executor == muStateExecutor) {
			//muStateExecutor.getStateManager().addURL(muStateExecutor.testsDir);	
			muStateExecutor.getStateManager().clusterMutantStates (muStateExecutor.stateDir, muStateExecutor.testsDir);
			//System.out.println("classpath = " + System.getProperty("java.class.path"));
		}*/

		if (executor == muStateExecutor) {		
			muStateExecutor.closeResultFiles();
		}
		
		unregisterTables();
	}
	
	


	/**
	 * TestExecutor로 하여금 Execution 을 수행시키는 함수.( it has a sub-task (1 worked))
	 * 
	 * @param tOps
	 * @param cOps
	 * @param resources
	 * @param eqTable
	 * @param statistics
	 * @param table
	 * @param monitor
	 */
	public void executeJUnitTestCases(List<String> tOps, List<String> cOps,
			List<IMethod> methods, ReachTable reachTable,
			EquivalentTable eqTable)
	{

		printPreLog();

		HashMap<IMethod, TestCase> testMethods = loadJunitTestMethods(methods,
				monitor);

		registerTables(reachTable, eqTable);

		setupTables(reachTable, testMethods.values(), tOps);

		setupMonitorProgressInfo(tOps, cOps, testMethods.size());

		executor.beforeRunAllTestMethod();

		List<IMethod> sortedTests = new ArrayList<IMethod>(testMethods.keySet());
		Collections.sort(sortedTests, testJunitMethodComparator);

		//msyu
		
		if(executor == muStateExecutor) {
			((MuJavaMutantStateExecutor)executor).loadWeaklyMutantIDs("weakly_killed");			
			muStateExecutor.initResultFiles(muStateExecutor.stateDir);
		}
		
		for (IMethod testMethod : sortedTests)
		{

			MuJavaLogger.getLogger().debug(
					testMethod.toString() + "is Executed");

			// Result of original, compared with those of mutants
			executor.beforeRunATestMethod();

			TestCase tc = testMethods.get(testMethod);


			
			// start for tOps
			for (String tOp : tOps)
			{
				runMutants(tOp, testMethod, tc, monitor);
			} 
			
			//msyu			
			if (executor == muStateExecutor) {				
				InterState.moveStateFiles (muStateExecutor.stateDir  + File.separator + "ResultStates", tc.toSimpleString());
			}

		}

		executor.afterRunAllTestMethod();
		
		if(executor == nuExecutor && WK_WRITE) {
			((NuJavaMutantExecutor)executor).writeWeaklyMutantIDs("weakly_killed_" + reachTable.hashCode() + ".table");
		} /*else if (executor == muStateExecutor) {
			//muStateExecutor.getStateManager().addURL(muStateExecutor.testsDir);	
			muStateExecutor.getStateManager().clusterMutantStates (muStateExecutor.stateDir, muStateExecutor.testsDir);
			//System.out.println("classpath = " + System.getProperty("java.class.path"));
		}*/


		if (executor == muStateExecutor) {		
			muStateExecutor.closeResultFiles();
		}
		unregisterTables();
	}
	
	private boolean isAlreadExecuted(TestCase tc){
		File f = new File(muStateExecutor.stateDir);
		if(f.isDirectory()){
			String[] strList = f.list();
			for(String str: strList){
				if(str.indexOf(tc.toSimpleString())>=0){
					System.out.println("   ----- " + tc.toSimpleString()+" is already executed.");
					return true;
				}
			}
			
		}
		return false;
	}
	
	
	public void executeJUnitTestCasesForPaper(List<String> tOps, List<String> cOps,
			List<IMethod> methods, ReachTable reachTable,
			EquivalentTable eqTable)
	{

		printPreLog();

		HashMap<IMethod, TestCase> testMethods = loadJunitTestMethods(methods,
				monitor);


		setupMonitorProgressInfo(tOps, cOps, testMethods.size());

		//executor.beforeRunAllTestMethod();

		List<IMethod> sortedTests = new ArrayList<IMethod>(testMethods.keySet());
		Collections.sort(sortedTests, testJunitMethodComparator);

		//msyu
		
		nuExecutor.weaklyMutantsOutDir = muProject.getDirectory().concat(File.separator + "States" + File.separator + muProject.getName());
		//System.out.println("weaklyMutantsOutDir: " + nuExecutor.weaklyMutantsOutDir);
		muStateExecutor.testsDir = muProject.getDirectory().concat(File.separator + "Tests");
		String rootDir =  muProject.getDirectory().concat(File.separator + "States"+File.separator);
		String baseDir =  rootDir + muProject.getName()+File.separator;

		muStateExecutor.weaklyMutantsInDir = baseDir;
		muStateExecutor.stateDir =  baseDir;
		
		File f = new File(rootDir);
		if(!f.exists()){
				f.mkdir();
		}
	
		f = new File(baseDir);
		if(!f.exists()){
			f.mkdir();
		}
		
		muStateExecutor.initResultFiles(muStateExecutor.stateDir);
		String reachFName;
		MuJavaProject originalProj = muProject;
		
		for (IMethod testMethod : sortedTests)
		{
			registerWeakTablesForPaper(reachTable, eqTable);
			setupTables(reachTable, testMethods.values(), tOps);

			MuJavaLogger.getLogger().debug(
					testMethod.toString() + "is Executed");
		
			TestCase tc = testMethods.get(testMethod);
			
			if(isAlreadExecuted(tc)){
				continue;
			}


			// weak mutation 실행
			//MuJavaProject.getMuJavaProject("Weak", monitor);
			executor = nuExecutor;
			executor.setExecutor(this);
			//nuExecutor.
			executor.beforeRunAllTestMethod();
			executor.beforeRunATestMethod();
			for (String tOp : tOps)
			{
				runMutants(tOp, testMethod, tc, monitor);
			} 
			executor.afterRunAllTestMethod();
			reachFName = "wk_" + tc.toSimpleString() + ".table";
			nuExecutor.writeWeaklyMutantIDs(reachFName);
			nuExecutor.removeWeaklyMutantID();
			
			//----------------------------------------------------------------
			muProject = originalProj;
			registerTables(reachTable, eqTable);
			setupTables(reachTable, testMethods.values(), tOps);

			muStateExecutor.loadWeaklyMutantIDs(reachFName);			
			// clustering 실행
			executor = muStateExecutor;
			executor.setExecutor(this);
			executor.beforeRunAllTestMethod();
			executor.beforeRunATestMethod();
			for (String tOp : tOps)
			{
				runMutants(tOp, testMethod, tc, monitor);
			} 
			InterState.moveStateFiles (muStateExecutor.stateDir  + File.separator + "ResultStates", tc.toSimpleString());
			executor.afterRunAllTestMethod();			
		}
		
		muStateExecutor.closeResultFiles();
		unregisterTables();
	}

	private void setupMonitorProgressInfo(List<String> tOps, List<String> cOps,
			int testCaseCount)
	{

		// calculate all works
		int mutantSize = getMutantSize(tOps, cOps);
		int totalMonitorWork = 1 + testCaseCount * (1 + mutantSize);

		if (monitor != null)
		{
			monitor.beginTask("Executing Test Cases - ", totalMonitorWork);
		}
	}

	private void setupTables(ReachTable reachTable,
			Collection<TestCase> testMethods, List<String> tOps)
	{
		// register all used test cases
		resultTable.setUsedTestCase(testMethods);
		resultTable.setMutationOperators(tOps);
	}

	private void unregisterTables()
	{
		this.reachTable = null;
		this.eqTable = null;
		this.mutantTable = null;
	}

	private void registerTables(ReachTable reachTable, EquivalentTable eqTable)
	{

		// prepare mutants and their codesets
		try
		{
			mutantTable = MutantTable.getMutantTable(muProject, monitor);
			GeneratedCodeTable.getGeneratedCodeTable(muProject, monitor);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (CoreException e1)
		{
			e1.printStackTrace();
		}

		// register reach table
		this.reachTable = reachTable;
		this.eqTable = eqTable;
	}
	
	private void registerWeakTablesForPaper(ReachTable reachTable, EquivalentTable eqTable)
	{

		String projDir = muProject.getDirectory();
		projDir = projDir.replace("\\","/" );
		String tempDir;
		int targetProjId = -1;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projs = root.getProjects();
		for(int i=0;i<projs.length;i++){
			tempDir = projs[i].getLocation().toString();
			if(tempDir.equals(projDir)){
				targetProjId = i;
				break;
			}
		}
		
		if(targetProjId>=0){
			IFile targetPath = projs[targetProjId].getFile("WeakProj.mjp");
			muProject = MuJavaProject.getMuJavaProject(targetPath, null);
			
		//root.getFile(IPath.).getFile(path);
		/*rt.getFile(path)
		IFile file = new File(weakProjName);
		mujavaProject = MuJavaProject.getMuJavaProject(file, null);*/
		// prepare mutants and their codesets
			try
			{
				//IFile ff = muProjec
				mutantTable = MutantTable.getMutantTable(muProject, monitor);
				GeneratedCodeTable.getGeneratedCodeTable(muProject, monitor);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			catch (CoreException e1)
			{
				e1.printStackTrace();
			}
	
			// register reach table
			this.reachTable = reachTable;
			this.eqTable = eqTable;
		}
	}

	private void printPreLog()
	{
		String text = "Executing each Mutant with selected test cases...";
		if (monitor != null)
		{
			monitor.subTask(text);
		}

		MuJavaLogger.getLogger().debug(text);
	}

	private HashMap<Method, TestCase> loadTestMethods(List<IFile> resources,
			IProgressMonitor monitor)
	{

		// loaded the test class
		HashMap<Method, TestCase> testCases = new HashMap<Method, TestCase>();

		/**
		 * 기본 class loader 를 생성해서 TestCase에대해 reflection 으로 필요한 정보를 수집한다.
		 */
		ClassLoader mLoader = getClassLoader(monitor);

		// obtain only test methods which are defined in the test class
		for (IFile testcase : resources)
		{
			// get file name of test case
			//System.out.println("muProject.getTestDirectory(): " + muProject.getTestDirectory());

			String packageName = getTestCaseName(muProject.getTestDirectory(),
					testcase);
			//System.out.println("packageName: " + packageName);

				packageName = packageName.replace(IPath.SEPARATOR, '.');
			//	System.out.println("packageName: " + packageName);


			try
			{
				Class<?> testClass = mLoader.loadClass(packageName);

				try{
					for (Method testMethod : obtainTestMethods(testClass)) 
				
					{
						TestCase tc = new TestCase(testMethod);
						tc.setFileLocation(testcase);
						testCases.put(testMethod, tc);
					}
				}
				catch (NoTestMethodException e)
				{
					// due to obtainTestMethod
					e.printStackTrace();
					MuJavaLogger.getLogger().debug(
							"No test method was found " + testClass.getName() + " class");
				}

			}
			catch (ClassNotFoundException e)
			{
				// due to loadClass
				e.printStackTrace();
			}
		}
		return testCases;
	}

	private HashMap<IMethod, TestCase> loadJunitTestMethods(
			List<IMethod> methods, IProgressMonitor monitor)
	{
		// loaded the test class
		HashMap<IMethod, TestCase> testCases = new HashMap<IMethod, TestCase>();

		// obtain only test methods which are defined in the test class
		for (IMethod testMethod : methods)
		{
			TestCase tc = new TestCase(testMethod);
			tc.setFileLocation((IFile) testMethod.getResource());
			testCases.put(testMethod, tc);
		}

		return testCases;
	}

	private int getMutantSize(List<String> tOps, List<String> cOps)
	{

		if (mutantTable == null)
		{
			return 0;
		}

		int mutantSize = 0;
		for (String tOp : tOps)
		{
			Set<String> IDs = mutantTable.getMutantIDs(tOp);
			mutantSize += IDs.size();
		}
		for (String cOp : cOps)
		{
			Set<String> IDs = mutantTable.getMutantIDs(cOp);
			mutantSize += IDs.size();
		}

		return mutantSize;
	}

	/**
	 * Target Application과 Test Case, 그리고 관련된 Library만이 loading되는 ClassLoader를
	 * 생성한다.
	 * 
	 * @param monitor
	 * @return
	 */
	private ClassLoader getClassLoader(IProgressMonitor monitor)
	{
		return getClassLoader("", monitor);
	}

	/**
	 * 특정 Mutant에 연관된 CodeSet을 loading할 수 있는 ClassLoader를 생성한다. 공통적으로 사용되는
	 * Library등도 같이 load할수 있도록 ClassLoader를 생성한다.
	 * 
	 * @param gTable
	 *            GeneratedCodeTable
	 * @param cID
	 *            current codeset's ID
	 * @param monitor
	 * @return
	 */
	private ClassLoader getClassLoader(String archiveNameStr,
			IProgressMonitor monitor)
	{

		// custom class loader to be return
		ClassLoader mLoader = null;

		List<URL> urls = new ArrayList<URL>();
		try
		{
			IProject eclipseProject = muProject.getResource().getProject();
			IJavaProject project = JavaCore.create(eclipseProject);

			// loading path for test case
			IFolder testRoot = eclipseProject.getFolder(muProject
					.getTestDirectory());
			String absoluteTestRootpath = getWorkspaceRelativeFile(testRoot
					.getFullPath());
			urls.add(new File(absoluteTestRootpath).toURI().toURL());

			if (archiveNameStr != null && !archiveNameStr.isEmpty())
			{
				urls.add(new File(archiveNameStr).toURI().toURL());
			}

			String absoluteOutputLocation = getWorkspaceRelativeFile(project
					.getOutputLocation());
			urls.add(new File(absoluteOutputLocation).toURI().toURL());

			// project libraries
			String requires = (String) MuJavaPlugin.getDefault().getBundle()
					.getHeaders().get(Constants.BUNDLE_CLASSPATH);
			try
			{
				ManifestElement[] elements = ManifestElement.parseHeader(
						Constants.BUNDLE_CLASSPATH, requires);
				for (int i = 0; i < elements.length; i++)
				{
					String name = elements[i].getValue();
					URL u = FileLocator.toFileURL(MuJavaPlugin.getDefault()
							.getBundle().getEntry(name));
					if (!urls.contains(u))
					{
						urls.add(u);
					}
				}
			}
			catch (BundleException e2)
			{
				e2.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			urls.add(FileLocator.toFileURL(FileLocator.find(MuJavaPlugin
					//.getDefault().getBundle(), new Path("/bin"), null)));
					.getDefault().getBundle(), new Path(File.separator + "bin"), null)));
			urls.add(FileLocator.toFileURL(FileLocator.find(MuJavaPlugin
					.getDefault().getBundle(), new Path(File.separator), null)));

			URL[] urlArray = (URL[]) urls.toArray(new URL[urls.size()]);
			mLoader = new URLClassLoader(urlArray);

		}
		catch (JavaModelException e)
		{
			MuJavaLogger.getLogger().error(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return mLoader;
	}

	ClassLoader getCodeSetClassloader(MuJavaMutantInfo mutant)
	{

		ClassLoader mLoader = null;

		try
		{
			GeneratedCodeTable gTable = GeneratedCodeTable
					.getGeneratedCodeTable(muProject, monitor);

			IPath codePath = gTable.getCodeSetDirectory();
			String operator = mutant.getMutationOperatorName();
			String codeSetID = mutant.getCodeID();
			if (!operator.isEmpty())
			{
				codePath = codePath.append(operator);
			}
			CodeSet codes = CodeSet.getCodeSet(codePath, codeSetID);

			String archiveNameStr = codes.getArchiveName();

			mLoader = getClassLoader(archiveNameStr, monitor);
		}
		catch (Exception e)
		{

		}

		return mLoader;
	}

	protected Set<String> getCommonClassPath()
	{
		if (lastProject != null && lastProject.equals(muProject)
				&& lastCommonClassPath != null)
		{

			return lastCommonClassPath;
		}

		Set<String> libLocs = new HashSet<String>();

		IProject eclipseProject = muProject.getResource().getProject();

		// loading path for test case
		IFolder testRoot = eclipseProject.getFolder(muProject
				.getTestDirectory());
		String absoluteTestRootpath = getWorkspaceRelativeFile(testRoot
				.getFullPath());
		libLocs.add(absoluteTestRootpath);

		try
		{
			IJavaProject project = JavaCore.create(eclipseProject);
			String absoluteOutputLocation = getWorkspaceRelativeFile(project
					.getOutputLocation());
			libLocs.add(absoluteOutputLocation);

			// Project's Class Path 추가
			IClasspathEntry[] entries = project.getResolvedClasspath(true);
			for (IClasspathEntry cEntry : entries)
			{
				if (cEntry.getContentKind() == IPackageFragmentRoot.K_BINARY)
				{
					IPath path = cEntry.getPath();
					if (path.getDevice() == null)
					{
						String absoluteLibraryLocation = getWorkspaceRelativeFile(path);
						libLocs.add(absoluteLibraryLocation);
					}
					else
					{
						libLocs.add(cEntry.getPath().toOSString());
					}
				}
			}

			// MuJava libraries
			String requires = (String) MuJavaPlugin.getDefault().getBundle()
					.getHeaders().get(Constants.BUNDLE_CLASSPATH);
			try
			{
				ManifestElement[] elements = ManifestElement.parseHeader(
						Constants.BUNDLE_CLASSPATH, requires);
				for (int i = 0; i < elements.length; i++)
				{
					String name = elements[i].getValue();
					URL u = FileLocator.toFileURL(MuJavaPlugin.getDefault()
							.getBundle().getEntry(name));
					String fileName = u.getFile();
					if (fileName != null && !fileName.isEmpty())
					{
						File f = new File(fileName);
						String absolutePath = f.getAbsolutePath();
						if (!libLocs.contains(absolutePath))
						{
							libLocs.add(absolutePath);
						}
					}
				}
			}
			catch (BundleException e2)
			{
				e2.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			libLocs.add(FileLocator.toFileURL(
					FileLocator.find(MuJavaPlugin.getDefault().getBundle(),
							new Path("/bin"), null)).getFile());
			libLocs.add(FileLocator.toFileURL(
					FileLocator.find(MuJavaPlugin.getDefault().getBundle(),
							new Path("/"), null)).getFile());
		}
		catch (JavaModelException e)
		{
			MuJavaLogger.getLogger().error(e);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		lastCommonClassPath = libLocs;
		lastProject = muProject;
		return libLocs;
	}

	protected StringBuffer getCommonClassPathStringBuffer()
	{
		StringBuffer buffer = new StringBuffer();

		List<String> urlStrings = new ArrayList<String>();
		urlStrings.addAll(getCommonClassPath());

		for (String path : urlStrings)
		{
			buffer.append(path);
			buffer.append(File.pathSeparatorChar);
		}

		return buffer;
	}

	// private List<URL> getCommonClassPathURLs() {
	// List<String> list = getCommonClassPath();
	// List<URL> urls = new ArrayList<URL>();
	//
	// for (String file : list) {
	// try {
	// urls.add(new File(file).toURI().toURL());
	// } catch (MalformedURLException e) {
	// MuJavaLogger.getLogger().error(
	// "Can't make URL from the given classpath");
	// }
	// }
	// return urls;
	// }

	/**
	 * Obtain mutants from the given mutants that are killed if killed parameter
	 * is true or live otherwise.
	 * 
	 * @param mutantIDs
	 * @param killed
	 *            true if killed mutant, false if live mutant
	 * @return subset of the given mutant IDs
	 */
	protected Set<String> getKilledOrLivedMutant(Set<String> mutantIDs,
			boolean killed)
	{
		Set<String> mList = new HashSet<String>();
		for (String mID : mutantIDs)
		{
			if (eqTable != null && eqTable.isEquivalent(mID))
			{
				continue;
			}
			String mutantOperator = MutantID.getMutationOperator(mID);
			// check whether the mutant, (mID), is killed or not
			if (resultTable.isKilled(mutantOperator, mID) == killed)
			{
				mList.add(mID);
			}
		}
		return mList;
	}

	protected Set<String> getMutants(String mutantOperator)
	{
		return mutantTable.getMutantIDs(mutantOperator);
	}

	protected int getMaxChangePoint(MuJavaMutantInfo mutant)
	{

		int max = 0;

		try
		{
			GeneratedCodeTable gTable = GeneratedCodeTable
					.getGeneratedCodeTable(muProject, monitor);

			IPath codePath = gTable.getCodeSetDirectory();
			String operator = mutant.getMutationOperatorName();
			String codeSetID = mutant.getCodeID();
			if (!operator.isEmpty())
			{
				codePath = codePath.append(operator);
			}

			List<String> ids = gTable.getMutantIDs(codeSetID);
			for (String id : ids)
			{
				int value = Integer.parseInt(MutantID.getChangePointID(id));
				if (max < value)
				{
					max = value;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		return max;
	}

	protected StringBuffer getMutantClassPathStringBuffer(MuJavaMutantInfo mInfo)
	{
		return getMutantClassPathStringBuffer(mInfo, false);
	}

	protected String getArchiveFileName(MuJavaMutantInfo mutant,
			boolean useSubCodeSet)
	{
		try
		{
			GeneratedCodeTable gTable = GeneratedCodeTable
					.getGeneratedCodeTable(muProject, monitor);

			IPath codePath = gTable.getCodeSetDirectory();
			String operator = mutant.getMutationOperatorName();
			String codeSetID = mutant.getCodeID();
			if (useSubCodeSet)
			{
				codeSetID = mutant.getSubCodeID();
			}

			if (!operator.isEmpty())
			{
				codePath = codePath.append(operator);
			}

			CodeSet codes = CodeSet.getCodeSet(codePath, codeSetID);

			return codes.getArchiveName();
		}
		catch (Exception e1)
		{
		}

		return "";
	}

	protected StringBuffer getMutantClassPathStringBuffer(
			MuJavaMutantInfo mutant, boolean useSubCodeSet)
	{

		StringBuffer buffer = new StringBuffer();

		List<String> urlStrings = new ArrayList<String>();
		urlStrings.addAll(getCommonClassPath());

		String archiveFileName = getArchiveFileName(mutant, useSubCodeSet);
		if (archiveFileName != null && !archiveFileName.isEmpty())
		{
			urlStrings.add(1, archiveFileName);
		}

		for (String path : urlStrings)
		{
			buffer.append(path);
			buffer.append(File.pathSeparatorChar);
		}

		return buffer;
	}

	protected MuJavaMutantInfo getMutantInfo(String mID)
	{
		return MuJavaMutantInfo.getMuJavaMutantInfo(this.muProject, mID);
	}

	public ResultTable getResultTable()
	{
		assert (resultTable != null);

		return resultTable;
	}

	private String getTestCaseName(String testDir, IFile resource)
	{
		IPath testDirPath = new Path(testDir);
		IPath path = resource.getProjectRelativePath();
		path = path.removeFirstSegments(testDirPath.segmentCount());
		path = path.removeFileExtension();
		String packageName = path.toString();
		return packageName;
	}

	String getWorkspaceRelativeFile(IPath relativePath)
	{
		return getWorkspaceRelativeFile(relativePath.toOSString());
	}

	String getWorkspaceRelativeFile(String relativePath)
	{
		String path = "";

		Location wsLoc = Platform.getInstanceLocation();
		if (wsLoc != null)
		{
			try
			{
				URL url = FileLocator.toFileURL(wsLoc.getURL());
				String file = url.getFile();
				if (!file.isEmpty())
				{
					IPath newPath = new Path(file);
					newPath = newPath.append(relativePath);
					path = newPath.toFile().getAbsolutePath();
				}
			}
			catch (IOException e)
			{
			}
		}

		return path;
	}

	protected boolean isKilled(String mutationOperator, String mutantID)
	{
		return resultTable.isKilled(mutationOperator, mutantID);
	}

	/**
	 * Load all possible test methods of the target test class and set its
	 * instance variable
	 * 
	 * @param testClassName
	 *            the name of a given class which has a few tests to be executed
	 * @return true if at least one test is loaded, false otherwise.
	 */
	List<Method> obtainTestMethods(Class<?> testDriverClass)
			throws NoTestMethodException
	{
		try
		{
			// read testcases from the test set class
			Method[] methods = testDriverClass.getDeclaredMethods();
			if (methods == null)
			{
				MuJavaLogger.getLogger().error(" No test case exist ");
				throw new NoTestMethodException();
			}

			// Filter test method(case)s whose name is not started with "test"
			List<Method> list = new ArrayList<Method>();
			for (int i = 0; i < methods.length; i++)
			{
				String testName = methods[i].getName();
				if (testName.startsWith("test"))
				{
					list.add(methods[i]);
				}
			}

			if (list.size() == 0)
				throw new NoTestMethodException();

			return list;

		}
		catch (NoTestMethodException nme)
		{
			throw new NoTestMethodException();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}

		return new ArrayList<Method>();
	}

	/**
	 * 주어진 OP에 해당하는 모든 Mutant를 TC로 수행시킨다.
	 * 
	 * @param mTable
	 * @param op
	 * @param originalResult
	 * @param testMethod
	 * @param tc
	 * @param eqTable
	 * @param dTable
	 * @param monitor
	 * @return
	 */
	private void runMutants(String mutation_op, Method testMethod, TestCase tc,
			IProgressMonitor monitor)
	{

		executor.preProcess();
		MuJavaLogger.getLogger().reach("TestCase:" + tc.toString());

		if (mutantTable == null)
		{
			return;
		}

		Set<String> mutantIDs = mutantTable.getMutantIDs(mutation_op);
		Set<String> eqMutants = new HashSet<String>();
		for (String mutantID : mutantIDs)
		{

			if (!checkReachability(tc, mutantID))
			{
				continue;
			}
			
			if (eqTable.isEquivalent(mutantID))
			{
				eqMutants.add(mutantID);
				monitor.worked(1);
				continue;
			}

			// check whether the mutant is killed or not
		//	if (resultTable.isKilled(mutation_op, mutantID)) //msyu
		//	{
		//		monitor.worked(1);
		//		continue;
		//	}

			MuJavaMutantInfo mutant_info = mutantTable.getMutantInfo(mutantID);

			executor.execute(tc, testMethod, mutant_info,
					muProject.getTestCaseType());

			monitor.worked(1);
		}

		executor.postProcess();

		// 수행 생략된 mutant에 대해 기록하기 위해 삽입된 MutantResult 중 Equivalent Mutant를 제거
		for (String mutantID : eqMutants)
		{
			resultTable.clearMutantResult(mutantID);
		}

	}

	/**
	 * JUnit 용 executor
	 * 
	 * @param op
	 * @param testMethod
	 * @param tc
	 * @param monitor
	 */
	private void runMutants(String op, IMethod testMethod, TestCase tc,
			IProgressMonitor monitor)
	{
		//System.out.println("runMutants1");
		
		executor.preProcess();
		MuJavaLogger.getLogger().reach("TestCase:" + tc.toSimpleString());

		if (mutantTable == null)
		{
			return;
		}

		Set<String> mutantIDs = mutantTable.getMutantIDs(op);
		Set<String> eqMutants = new HashSet<String>();
		for (String mutantID : mutantIDs)
		{

			if (eqTable.isEquivalent(mutantID))
			{
				eqMutants.add(mutantID);
				monitor.worked(1);
				continue;
			}

			// check whether the mutant is killed or not
			//if (resultTable.isKilled(op, mutantID)) //msyu
			//{
			//	monitor.worked(1);
			//	continue;
			//}

			MuJavaMutantInfo mutant = mutantTable.getMutantInfo(mutantID);

			executor.execute(tc, testMethod, mutant,
					muProject.getTestCaseType());

			monitor.worked(1);
		}

		executor.postProcess();

		// 수행 생략된 mutant에 대해 기록하기 위해 삽입된 MutantResult 중 Equivalent Mutant를 제거
		for (String mutantID : eqMutants)
		{
			resultTable.clearMutantResult(mutantID);
		}

	}

	/**
	 * 어느 형태의 Mutant를 상관하지 않고, 수행시켜주는 함수
	 * 
	 * @param cPath
	 * @param envVariables
	 * @param workDir
	 * @param measureCompareCost
	 * @return
	 */
	protected MutantResult runMutantThread(String cPath,
			Map<String, String> envVariables, File workDir,
			boolean measureCompareCost, TestCaseType tcType)
	{
		MutantExecutorThread thread = new MutantExecutorThread(lockObject, cPath,
				workDir, envVariables, tcType);

		MutantResult mResult = runInternalMutantThread(thread, envVariables,
				measureCompareCost);

		// Clear thread instance
		thread = null;

		return mResult;
	}

	/**
	 * 어느 형태의 Mutant를 상관하지 않고, 수행시켜주는 함수
	 * 
	 * @param cPath
	 * @param envVariables
	 * @param workDir
	 * @param measureCompareCost
	 * @return
	 */
	private MutantResult runInternalMutantThread(IExecutorThread thread,
			Map<String, String> envVariables, boolean measureCompareCost)
	{

		MutantResult mResult = new MutantResult();
		Object result = null;

		String returnFileName = envVariables.get(runner.Executor.ID_RESULT);

		// timeout 설정은 제외함
		// envVariables.put(runner.Executor.ID_TIME_OUT,
		// String.valueOf(TIMEOUT));

		// initializing variable for cost measuring
		long executionTimeWithOutTimeOut = 0;
		long executionTimeWithTimeOut = 0;
		long localStartTime = System.nanoTime();

		pool.execute(thread);

		// measuring the execution time
		long comparingTime = 0;

		try
		{
			synchronized (lockObject)
			{
				// Check out if a mutant is in infinite loop
				lockObject.wait(TIMEOUT);
			}

			// When executing a mutant is done or executing a mutant is kept
			// running after the given TIMEOUT
			if (!thread.isRunning())
			{
				// It is already terminated
				executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;
				result = getResultFromFile(returnFileName, mResult);

				// executionTimeWithOutTimeOut = mResult.getExecutingTime();
				mResult.setMutantResult(result);

				// measuring the comparing time
				if (measureCompareCost)
				{
					long beforeTime = System.nanoTime();
					mResult.isKilled(); // comparing results
					comparingTime += (System.nanoTime() - beforeTime) / 1000000;
				}
			}
			else
			{
				// Still executed
				thread.finish(); // force to terminate

				// measuring execution time
				executionTimeWithTimeOut += (System.nanoTime() - localStartTime) / 1000000;

				// make an output
				result = "time_out: more than " + TIMEOUT + " seconds";
				mResult.setMutantResult(result);

				if (measureCompareCost)
				{
					// measuring the comparing time
					long beforeTime = System.nanoTime();
					mResult.isKilled(); // comparing results
					comparingTime += (System.nanoTime() - beforeTime) / 1000000;
				}
			}
		}
		catch (Exception e)
		{
			// exception occurred -> abnormal execution

			MuJavaLogger.getLogger().debug("Exception");
			// measuring time
			executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;

			// make an output
			result = e.getCause().getClass().getName() + " : "
					+ e.getCause().getMessage();
			mResult.setMutantResult(result);

			// measuring the comparing time
			if (measureCompareCost)
			{
				long beforeTime = System.nanoTime();
				mResult.isKilled(); // comparing results
				comparingTime += (System.nanoTime() - beforeTime) / 1000000;
			}
		}

		/* clear the Thread */
		// 1. 종료되기 전이면 강제 종료
		if (!pool.isTerminated())
		{
			thread.finish();
		}

		// 결과 기록
		mResult.setExecutingTime(executionTimeWithOutTimeOut
				+ executionTimeWithTimeOut);
		mResult.setExecutingTime(executionTimeWithTimeOut, true);
		mResult.setExecutingTime(executionTimeWithOutTimeOut, false);
		mResult.setComparingTime(comparingTime);
		mResult.setLoadingTime(0);

		return mResult;
	}

	protected Object runJUnitSerialMutantThread(TestCaseType tcType, String classPath,
			Map<String, String> envVariables, File workDir,
			boolean measureCompareCost)
	{
		MutantExecutorThread executorThread = new MutantExecutorThread(
				lockObject, classPath, workDir, envVariables, tcType);

		//System.out.println("classPath = : " + classPath);
		//System.out.println("workDir = : " + workDir);
		//System.out.println("tcType = : " + tcType);

		Object obj = runInternalSerialMutantThread(executorThread,
				envVariables, measureCompareCost);
		
		try { //msyu
			executorThread.printProcessOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		executorThread = null;

		return obj;
	}

	protected Object runSerialMutantThread(String classPath,
			Map<String, String> envVariables, File workDir,
			boolean measureCompareCost)
	{
		MutantExecutorThread executorThread = new MutantExecutorThread(
				lockObject, classPath, workDir, envVariables, TestCaseType.MJ);

		Object obj = runInternalSerialMutantThread(executorThread,
				envVariables, measureCompareCost);

		executorThread = null;

		return obj;
	}

	private Object runInternalSerialMutantThread(
			IExecutorThread executorThread, Map<String, String> envVariables,
			boolean measureCompareCost)
	{
		MutantResult mResult = new MutantResult();

		Object result = null;
		String returnFileName = envVariables.get(runner.Executor.ID_RESULT);
		//System.out.println("env = " + envVariables);

		// initializing variable for cost measuring
		long executionTimeWithOutTimeOut = 0;
		long executionTimeWithTimeOut = 0;
		long localStartTime = System.nanoTime();

		pool.execute(executorThread);
		
		// measuring the execution time
		long comparingTime = 0;

		try
		{
			synchronized (lockObject)
			{
				lockObject.wait(TIMEOUT);
			}

			// When executing a mutant is done or executing a mutant is kept
			// running after the given TIMEOUT
			if (!executorThread.isRunning())
			{ // It is already terminated
				executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;

				result = getResultFromFile(returnFileName, mResult);
				mResult.setMutantResult(result);
				
				//System.out.println("returnFileName: " + returnFileName);
				//System.out.println("mutantID: " + mResult.getMutantID() + " result: " + result);

				// measuring the comparing time
				if (measureCompareCost)
				{
					long beforeTime = System.nanoTime();
					mResult.isKilled(); // comparing results
					comparingTime += (System.nanoTime() - beforeTime) / 1000000;
				}
				//System.out.println("isKilled: " +  mResult.isKilled());

			}
		}
		catch (Exception e)
		{ // exception occurred -> abnormal execution
			MuJavaLogger.getLogger().debug("Exception");
			// measuring time
			executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;

			// make an output
			result = e.getCause().getClass().getName() + " : "
					+ e.getCause().getMessage();
			mResult.setMutantResult(result);

			// measuring the comparing time
			if (measureCompareCost)
			{
				long beforeTime = System.nanoTime();
				mResult.isKilled(); // comparing results
				comparingTime += (System.nanoTime() - beforeTime) / 1000000;
			}
		}

		// clear the Thread
		if (!pool.isTerminated())
		{
			executorThread.finish();
		}

		mResult.setExecutingTime(executionTimeWithOutTimeOut
				+ executionTimeWithTimeOut);
		mResult.setExecutingTime(executionTimeWithTimeOut, true);
		mResult.setExecutingTime(executionTimeWithOutTimeOut, false);
		mResult.setComparingTime(comparingTime);
		mResult.setLoadingTime(0);

		return result;
	}

	private String getResultFromFile(String returnFileName, MutantResult mResult)
	{

		if (returnFileName == null || returnFileName.isEmpty())
		{
			return "IRREGULAR";
		}

		//System.out.println("getResultFromFile");
		try
		{
			FileInputStream fis = new FileInputStream(returnFileName);

			FileChannel fic = fis.getChannel();
			ByteBuffer buf = ByteBuffer.allocate((int) fic.size());
			//System.out.println("channel size: " + fic.size());

			buf.rewind();
			fic.read(buf);

			String result = new String(buf.array(), 0, (int) (fic.size()),
					"UTF-8");
			fic.close();
			fis.close();
			return result;
		}
		catch (Exception e)
		{
			return e.toString();
		}
	}

	protected boolean runReachThread(String cPath,
			Map<String, String> envVariables, File workDirectory,
			TestCaseType tcType)
	{

		MutantResult mResult = new MutantResult();

		// initializing variable for cost measuring
		long executionTimeWithOutTimeOut = 0;
		long executionTimeWithTimeOut = 0;
		long localStartTime = System.nanoTime();

		DefaultExecutorThread t = new DefaultExecutorThread(lockObject, cPath,
				envVariables, workDirectory, tcType);
		pool.execute(t);

		// initializing variable for cost measuring
		boolean isReached = false;

		try
		{
			synchronized (lockObject)
			{
				lockObject.wait(TIMEOUT);
			}

			if (!t.isRunning())
			{
				// It is already terminated
				// isReached = t.isReached();

				executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;
			}
			else
			{
				// Still executed
				t.finish(); // intentional termination

				executionTimeWithTimeOut += (System.nanoTime() - localStartTime) / 1000000;
			}
		}
		catch (Exception e)
		{ // exception occurred -> abnormal execution
			MuJavaLogger.getLogger().debug("Exception");

			executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;
		}

		// clear the Thread
		if (!pool.isTerminated())
		{
			t.finish();
		}
		t = null;

		mResult.setAnalysisTime(executionTimeWithOutTimeOut
				+ executionTimeWithTimeOut);
		mResult.setExecutingTime(0);
		mResult.setExecutingTime(0, true);
		mResult.setExecutingTime(0, false);
		mResult.setComparingTime(0);
		mResult.setLoadingTime(0);

		return isReached;
	}

	protected void setMonitorSubTask(String str)
	{
		if (monitor != null)
		{
			monitor.subTask(str);
		}
	}

	/**
	 * Set the time out limit
	 * 
	 * @param msecs
	 *            timeout millisecond
	 * @see TestExecutionWizard.performFinish
	 */
	public void setTimeOut(int msecs)
	{
		TIMEOUT = msecs;
	}

	public void setConditionalMode(boolean cmMode)
	{
		if (nuExecutor != null)
		{
			nuExecutor.setCMmode(cmMode);
		}
	}

	public void setStatisticHandler(RecordStatistics handler)
	{
		this.executor.setStatisticHandler(handler);
	}

	public Set<Integer> getSideEffectChangePoints(Set<String> liveMutants)
	{
		// 반환 SET 생성
		Set<Integer> chpts = new HashSet<Integer>();

		// Kill 되지 않은 mutant에 대해 작업
		for (String mID : liveMutants)
		{
			// deleted by ysma at 2013/11/20
			//MutantID id = new MutantID(mID);
			//id.setLastIndex(0);

			// 모든 COR mutant 를 sideeffectchangepoint로 가정
			// 향후 sideeffect 분석이 제공되면 변경할 로직
			// {
			// for (MutantID mutantID : sideEffectChangePoints)
			// {
			// // 주어진 Mutant ID에 해당하는 change point 만 수집
			// if (mutantID.compareTo(id) == 0)
			// {
			// chpts.add(id.getChangePoint());
			// }
			// }
			// }

			if ("LOR".equals(MutantID.getMutationOperator(mID)))
			{
				chpts.add(MutantID.getChangePoint(mID));
			}
		}
		return chpts;
	}

}

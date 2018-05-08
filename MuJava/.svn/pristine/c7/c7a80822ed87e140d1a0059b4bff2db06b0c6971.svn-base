package mujava.executor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantResult;
import mujava.TestCaseType;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.util.RecordStatistics;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;

public class MuJavaMutantExecutor implements ITestExecutor {

	protected Object originalResult = null;
	TestExecutor executor;
	private RecordStatistics statisticHandler = null;

	@Override
	public void preProcess() {
	}

	@Override
	public void setStatisticHandler(RecordStatistics handler) {
		this.statisticHandler = handler;
	}
	
	@Override
	public void execute(TestCase tc, Method testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType) 
	{

		String mutantID = mutant.getMutantID();

		if (!executor.checkReachability(tc, mutant)) {
			MuJavaLogger.getLogger().reach(
					"[NON-REACH] MUTANT (" + mutantID + " )for "
							+ tc.toSimpleString());
			return;
		}

		// reach 할때만 수행한다.

		// Execute original code with test case on measuring time
		if (originalResult == null) {
			MuJavaLogger.getLogger()
			.debug("Executing Original for Test Case for "
					+ tc.toString());

			//originalResult = executeOriginal(executor, tc, testMethod,mutant, testCaseType);
			originalResult = executeOriginal(executor, tc, testMethod,mutant.getMutationOperatorName(), testCaseType);
		}

		executor.setMonitorSubTask("Execute a mutant " + mutantID
				+ " with a test case : " + tc.toString());

		try {
			
			MuJavaLogger.getLogger().reach("RE:" + mutantID);
			if (statisticHandler != null) {
				String testCaseID = RecordStatistics.getTestCaseID(tc
						.toSimpleString());
				statisticHandler.reach(RecordStatistics.REACH, testCaseID,
						mutantID);
			}
			
			executeMutant(executor, tc, testMethod, mutant, testCaseType);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void execute(TestCase tc, IMethod testMethod, MuJavaMutantInfo mutant_info, TestCaseType testCaseType)
	{
		// Execute original code with test case on measuring time
		if (originalResult == null) {
			MuJavaLogger.getLogger().debug(
					"Executing Original for Test Case for " + tc.toString());

			// TODO MSG를 사용해서 Original수행하도록 변경
			//originalResult = executeOriginal(executor, tc, testMethod, mutant_info, testCaseType);
			//originalResult = executeOriginal(executor, tc, testMethod, mutant_info.getMutationOperatorName(), testCaseType);
			originalResult = executeOriginal(tc, testMethod, mutant_info.getMutationOperatorName(), testCaseType);
		}

		// 2014 02 03
		String mutantID = mutant_info.getMutantID();
		executor.setMonitorSubTask("Execute a mutant " + mutantID
				+ " with a test case : " + tc.toString());

		if (executor.checkReachability(tc, mutant_info)) {
			// reach 할때만 수행한다.

			try {
				this.executeMutant(tc, testMethod, mutant_info, testCaseType);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			MuJavaLogger.getLogger().reach(
					"[NON-REACH] MUTANT (" + mutantID + " )for "
							+ tc.toSimpleString());
		}
				// TODO 구현 요망
	}
	
	/**
	 * mResult에는 TestCase 필요 .............................................a.
	 * Time = Tprepare + execution (executionWithTimeOut +
	 * executionWithoutTimeOut)+ comparing
	 */
	private void executeMutant(TestExecutor executor, TestCase tc,
			Method testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType) throws IOException,
			CoreException {

		// measuring time
		long startTime = System.nanoTime();

		String returnFileName = "";
		try {
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();
		} catch (IOException e3) {
		}

		StringBuffer pathBuffer = executor
				.getMutantClassPathStringBuffer(mutant);
		String cPath = pathBuffer.append(".").toString();

		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, testMethod.getDeclaringClass()
				.getCanonicalName());
		env.put(runner.Executor.ID_METHOD_NAME, testMethod.getName());
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "TIMEOUT");

		if (returnFileName != null && !returnFileName.isEmpty()) {
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}

		// measuring the preparing time
		long prepareTime = (System.nanoTime() - startTime) / 1000000;

		MutantResult mResult = null;
		try {
			mResult = executor.runMutantThread(cPath, env, null, true, testCaseType);
			mResult.setMutantID(mutant.getMutantID());
			mResult.setMutantOperator(mutant.getMutationOperatorName());
			mResult.setOriginalResult(originalResult);
			mResult.setTestCase(tc);
			mResult.setPreparingTime(prepareTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mResult != null && mResult.isKilled()) {
			MuJavaLogger.getLogger().reach("SK:" + mutant.getMutantID());
		}

		executor.addMutantResult(mutant, mResult);

		deleteTemporaryFiles(returnFileName);
	}

	public Object executeOriginal(TestCase tc, IMethod testMethod,
			String mutation_op, TestCaseType testCaseType) {
		
		//IProject eclipseProject = muProject.getResource().getProject();
		//MutantManager.getMutantManager().getMuJavaProject();
		executor.setMonitorSubTask("Executing Original for testcase ");

		Object result = null;

		long startTime = System.nanoTime();

		String returnFileName = "";
		try {
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();
		} catch (IOException e3) {
		}


		String classPath = JUnitUtil.getJUnitClassPath2(executor,testMethod,testCaseType, false);
		// Get Test Working Directory
		File workDir = JUnitUtil.getWorkingDirectory(testMethod);

		String className = testMethod.getDeclaringType().getFullyQualifiedName();
		String methodName = testMethod.getElementName();

		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, className);
		env.put(runner.Executor.ID_METHOD_NAME, methodName);
		
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "NONE");
		if (returnFileName != null && !returnFileName.isEmpty()) {
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}

		
		executor.setMonitorSubTask("Execute a test case : " + className + " : "
				+ methodName);

		// check for time resource
		long preparingTime = (System.nanoTime() - startTime) / 1000000;

		// Create a thread to execute a mutant

		MutantResult mResult = executor.runMutantThread(classPath, env, workDir,true, testCaseType);
		
		result = mResult.getMutantResult();

		//mResult.setMutantID(mutant_info.getMutantID());
		mResult.setMutantID(methodName+"_original");
		mResult.setMutantOperator(mutation_op);
		mResult.setTestCase(tc);
		mResult.setPreparingTime(preparingTime);
		mResult.setMutantResult("");

		
		executor.addOriginalResult(mResult);
		deleteTemporaryFiles(returnFileName);
		
		originalResult = result;

		return result;
	}	
	/**
	 * It has a sub-task (1 + # of test case files). OriginalResult table에 결과를
	 * 생성한다. 다만, 이미 생성된 결과가 있는 경우에는 더이상 실행하지 않는다.
	 * 
	 * @param mResult
	 * 
	 * @param testCases
	 * @param monitor
	 */
	Object executeOriginal(TestExecutor executor, TestCase tc, Method testCase,
			String mutation_op, TestCaseType testCaseType) {

		executor.setMonitorSubTask("Executing Original for testcase ");

		Object result = null;

		long startTime = System.nanoTime();

		String returnFileName = "";
		try {
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();
		} catch (IOException e3) {
		}

		// prepare class loader which loads all test cases
		StringBuffer buffer = executor.getCommonClassPathStringBuffer();
		String cPath = buffer.append(".").toString();

		String className = testCase.getDeclaringClass().getCanonicalName();
		String methodName = testCase.getName();

		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, className);
		env.put(runner.Executor.ID_METHOD_NAME, methodName);
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "NONE");
		if (returnFileName != null && !returnFileName.isEmpty()) {
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}

		executor.setMonitorSubTask("Execute a test case : " + className + " : "
				+ methodName);

		// check for time resource
		long preparingTime = (System.nanoTime() - startTime) / 1000000;

		// Create a thread to execute a mutant

		MutantResult mResult = executor.runMutantThread(cPath, env, null, false, testCaseType);
		result = mResult.getMutantResult();

		//mResult.setMutantID(mutant_info.getMutantID());
		mResult.setMutantID(testCase.getName()+"_original");
		mResult.setMutantOperator(mutation_op);
		mResult.setTestCase(tc);
		mResult.setPreparingTime(preparingTime);
		mResult.setOriginalResult("");
		mResult.setMutantResult("");

		executor.addOriginalResult(mResult);
		deleteTemporaryFiles(returnFileName);

		return result;
	}
	
	protected void executeMutant(TestCase tc, IMethod testMethod,
			MuJavaMutantInfo mutant_info, TestCaseType testCaseType) throws IOException, CoreException {

		long startTime = System.nanoTime();

		String returnFileName  = "";
		try
		{
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();

		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}


		// prepare class loader which loads all test cases
		String classPath = JUnitUtil.getJUnitClassPath(executor,testCaseType, mutant_info, false);

		// set environment variable delivered to mutant executor
		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, testMethod.getDeclaringType()
				.getFullyQualifiedName());
		env.put(runner.Executor.ID_METHOD_NAME, testMethod.getElementName());
		env.put(runner.Executor.ID_MONITOR_NAME, "MSG.MutantMonitor");
		env.put(runner.Executor.ID_SIZE_CHANGEPOINT,
				Integer.toString(executor.getMaxChangePoint(mutant_info)));
		env.put(MSG.MSGConstraints.header_mutantID, mutant_info.getMutantID());
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "TIMEOUT");
		if (returnFileName != null && !returnFileName.isEmpty()) {
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}

		
		
		// Get Test Working Directory
		File workDir = JUnitUtil.getWorkingDirectory(testMethod);

		// check for time resource
		long prepareTime = (System.nanoTime() - startTime) / 1000000;

		// Create a thread to execute a mutant
		MutantResult mutantResult = null;
		try
		{
			mutantResult = executor.runMutantThread(classPath, env, workDir,
					true, testCaseType);
			mutantResult.setMutantID(mutant_info.getMutantID());
			mutantResult.setMutantOperator(mutant_info.getMutationOperatorName());
			mutantResult.setOriginalResult(originalResult);
			mutantResult.setTestCase(tc);
			mutantResult.setPreparingTime(prepareTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MuJavaLogger.getLogger().reach("WK:" + mutant_info.getMutantID());

		if (mutantResult != null && mutantResult.isKilled()) {
			MuJavaLogger.getLogger().reach("SK:" + mutant_info.getMutantID());
		}

		
		executor.addMutantResult(mutant_info, mutantResult);
		deleteTemporaryFiles(returnFileName);
		
	}	
	void deleteTemporaryFiles(String fileName) {

		if (fileName != null && !fileName.isEmpty()) {
			File f = new File(fileName);
			f.deleteOnExit();
		}
	}

	@Override
	public void postProcess() {

	}

	@Override
	public void beforeRunATestMethod() {
		originalResult = null;
	}

	@Override
	public void beforeRunAllTestMethod() {

	}

	@Override
	public void afterRunAllTestMethod() {

	}

	@Override
	public void setExecutor(TestExecutor executor) {
		this.executor = executor;
	}



}

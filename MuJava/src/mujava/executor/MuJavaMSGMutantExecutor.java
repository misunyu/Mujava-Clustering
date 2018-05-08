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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;

public class MuJavaMSGMutantExecutor extends MuJavaMutantExecutor {

	@Override
	public void preProcess() {
	}

	@Override
	public void execute(TestCase tc, Method testMethod, MuJavaMutantInfo mutant_info, TestCaseType testCaseType) {

		// Execute original code with test case on measuring time
		if (originalResult == null) {
			MuJavaLogger.getLogger().debug(
					"Executing Original for Test Case for " + tc.toString());

			// TODO MSG를 사용해서 Original수행하도록 변경
			//originalResult = executeOriginal(executor, tc, testMethod, mutant_info, testCaseType);
			originalResult = executeOriginal(executor, tc, testMethod, mutant_info.getMutationOperatorName(), testCaseType);
		}

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

	}

	/**
	 * mResult에는 TestCase 필요 .............................................a.
	 * Time = Tprepare + execution (executionWithTimeOut +
	 * executionWithoutTimeOut)+ comparing
	 */
	private void executeMutant(TestCase tc, Method testMethod,
			MuJavaMutantInfo mutant, TestCaseType testCaseType) throws IOException, CoreException {

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
		env.put(runner.Executor.ID_MONITOR_NAME, "MSG.MutantMonitor");
		env.put(runner.Executor.ID_SIZE_CHANGEPOINT,
				Integer.toString(executor.getMaxChangePoint(mutant)));
		env.put(MSG.MSGConstraints.header_mutantID, mutant.getMutantID());
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

		MuJavaLogger.getLogger().reach("WK:" + mutant.getMutantID());

		if (mResult != null && mResult.isKilled()) {
			MuJavaLogger.getLogger().reach("SK:" + mutant.getMutantID());
		}

		executor.addMutantResult(mutant, mResult);

		deleteTemporaryFiles(returnFileName);
	}

	@Override
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
		
		
	}

	/**
	 * mResult에는 TestCase 필요 .............................................a.
	 * Time = Tprepare + execution (executionWithTimeOut +
	 * executionWithoutTimeOut)+ comparing
	 */
	
	
}

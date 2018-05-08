package mujava.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantResult;
import mujava.TestCaseType;
import mujava.exception.NoTestMethodException;
import mujava.gen.MuJavaMutationEngine;
import mujava.plugin.MuJavaPlugin;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.util.RecordStatistics;
import nujava.NuJavaHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class NuJavaMutantExecutor implements ITestExecutor
{
	private boolean FOR_PAPER = false;
	private boolean DEEP_ANAL = false;
	
	private boolean CM_MUTANT_MODE = false;

	Object originalResult = null;

	// visited codeset list
	List<String> visitedCodeSet = new ArrayList<String>();

	List<String> tempFiles = new ArrayList<String>();

	Hashtable<String, String> fileNameMap = new Hashtable<String, String>();

	private TestExecutor executor = null;

	private RecordStatistics statisticHandler;

	private HashSet<String> weaklyMutanIDs = new HashSet();
	public static String weaklyMutantsOutDir = "";
	
	public void removeWeaklyMutantID(){
		weaklyMutanIDs.clear();		
	}
	
	@Override
	public void afterRunAllTestMethod()
	{
		List<String> temp = new ArrayList<String>();

		for (String tempFile : tempFiles)
		{
			File f = new File(tempFile);
			if (!f.delete())
			{
				temp.add(tempFile);
			}
		}
		tempFiles.clear();
		for (String tempFile : fileNameMap.values())
		{
			File f = new File(tempFile);
			if (!f.delete())
			{
				temp.add(tempFile);
			}
		}
		fileNameMap.clear();

		// 지워지지 않은 file을 다음에 지울수 있도록 기록으로 남겨둔다.
		tempFiles.addAll(temp);
	}

	@Override
	public void beforeRunAllTestMethod()
	{
		tempFiles.clear();
		fileNameMap.clear();
	}

	@Override
	public void beforeRunATestMethod()
	{
		originalResult = null;
	}

	private MutantResult conductCMStrongMutation(
			HashMap<String, MutantResult> executedRepresentativeMutants,
			TestCase tc, IMethod testMethod,
			MutantResult weaklyKilledMutantResult,
			MuJavaMutantInfo targetMutantInfo, String repMutantID, TestCaseType testCaseType)
	{
		String targetMutantID = targetMutantInfo.getMutantID();
		MutantResult repMutantResult = executedRepresentativeMutants
				.get(repMutantID);

		if (repMutantResult == null)
		{
			MutantResult mResult = conductJunitStrongMutation(testMethod,
					targetMutantInfo, testCaseType);
			mResult.setMutantID(weaklyKilledMutantResult.getMutantID());
			mResult.setMutantOperator(weaklyKilledMutantResult
					.getMutantOperator());
			mResult.setOriginalResult(weaklyKilledMutantResult
					.getOriginalResult());
			mResult.setTestCase(weaklyKilledMutantResult.getTestCase());

			executedRepresentativeMutants.put(repMutantID, mResult);

			return mResult;
		}

		String testCaseID = RecordStatistics.getTestCaseID(tc.toSimpleString());

		MuJavaLogger.getLogger().reach("GRP:" + targetMutantID);
		if (statisticHandler != null)
		{
			statisticHandler.reach(RecordStatistics.GROUP_IGNORED, testCaseID,
					targetMutantID);
		}

		weaklyKilledMutantResult.setMutantID(targetMutantID);
		weaklyKilledMutantResult.setMutantOperator(repMutantResult
				.getMutantOperator());
		weaklyKilledMutantResult.setTestCase(repMutantResult.getTestCase());
		weaklyKilledMutantResult.setMutantResult(repMutantResult
				.getMutantResult());

		if (weaklyKilledMutantResult.isKilled())
		{
			MuJavaLogger.getLogger().reach("GK:" + targetMutantID);
			if (statisticHandler != null)
			{
				statisticHandler.reach(RecordStatistics.GROUP_KILLED,
						testCaseID, targetMutantID);
			}
		}

		return weaklyKilledMutantResult;
	}

	private MutantResult conductCMStrongMutation(
			HashMap<String, MutantResult> executedRepresentativeMutants,
			TestCase tc, Method testMethod,
			MutantResult weaklyKilledMutantResult,
			MuJavaMutantInfo targetMutantInfo, String repMutantID, TestCaseType testCaseType)
	{

		String targetMutantID = targetMutantInfo.getMutantID();
		MutantResult repMutantResult = executedRepresentativeMutants
				.get(repMutantID);

		if (repMutantResult == null)
		{
			MutantResult mResult = conductStrongMutation(testMethod,
					targetMutantInfo, testCaseType);
			mResult.setMutantID(weaklyKilledMutantResult.getMutantID());
			mResult.setMutantOperator(weaklyKilledMutantResult
					.getMutantOperator());
			mResult.setOriginalResult(weaklyKilledMutantResult
					.getOriginalResult());
			mResult.setTestCase(weaklyKilledMutantResult.getTestCase());

			executedRepresentativeMutants.put(repMutantID, mResult);

			return mResult;
		}

		String testClassID = RecordStatistics.getTestClassID(tc.toSimpleString());

		MuJavaLogger.getLogger().reach("GRP:" + targetMutantID);
		if (statisticHandler != null)
		{
			statisticHandler.reach(RecordStatistics.GROUP_IGNORED, testClassID,
					targetMutantID);
		}

		weaklyKilledMutantResult.setMutantID(targetMutantID);
		weaklyKilledMutantResult.setMutantOperator(repMutantResult
				.getMutantOperator());
		weaklyKilledMutantResult.setTestCase(repMutantResult.getTestCase());
		weaklyKilledMutantResult.setMutantResult(repMutantResult
				.getMutantResult());

		if (weaklyKilledMutantResult.isKilled())
		{
			MuJavaLogger.getLogger().reach("GK:" + targetMutantID);
			if (statisticHandler != null)
			{
				statisticHandler.reach(RecordStatistics.GROUP_KILLED,
						testClassID, targetMutantID);
			}
		}

		return weaklyKilledMutantResult;
	}

	/**
	 * JUNIT Test 를 이용해서 Strong Mutation을 실행하고, 그 결과를 반환.
	 * 
	 * @param testMethod
	 * @param mInfo
	 * @param testCaseType 
	 * @return
	 */
	private MutantResult conductJunitStrongMutation(IMethod testMethod,
			MuJavaMutantInfo mInfo, TestCaseType testCaseType)
	{
		long startTime = System.nanoTime();

		// prepare class loader which loads all test cases
		String classPath = getJUnitClassPath(testCaseType, mInfo, true);

		// set environment variable delivered to mutant executor
		Map<String, String> env = setExecutionEnvironments(testMethod, mInfo);

		// Get Test Working Directory
		File workDir = getWorkingDirectory(testMethod);

		// check for time resource
		long prepareTime = (System.nanoTime() - startTime) / 1000000;

		// Create a thread to execute a mutant
		MutantResult mutantResult = null;
		try
		{
			mutantResult = executor.runMutantThread(classPath, env, workDir,
					true, testCaseType);
			mutantResult.setPreparingTime(prepareTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mutantResult;
	}

	private MutantResult conductStrongMutation(Method testMethod,
			MuJavaMutantInfo mInfo, TestCaseType testCaseType)
	{

		String mutantID = mInfo.getMutantID();
		long startTime = System.nanoTime();

		// prepare class loader which loads all test cases
		StringBuffer buffer = executor.getMutantClassPathStringBuffer(mInfo,
				true);
		String cPath = buffer.append(".").toString();

		// set environment variable delivered to mutant executor

		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, testMethod.getDeclaringClass()
				.getCanonicalName());
		env.put(runner.Executor.ID_METHOD_NAME, testMethod.getName());
		env.put(runner.Executor.ID_MONITOR_NAME, "MSG.MutantMonitor");
		env.put(runner.Executor.ID_SIZE_CHANGEPOINT,
				Integer.toString(executor.getMaxChangePoint(mInfo)));
		env.put(MSG.MSGConstraints.header_mutantID, mutantID);
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "TIMEOUT");

		try
		{
			File returnFile = File.createTempFile("result", null);
			String returnFileName = returnFile.getAbsolutePath();
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		// check for time resource
		long currentTime = System.nanoTime();
		long prepareTime = (currentTime - startTime) / 1000000;

		// Create a thread to execute a mutant
		MutantResult mutantResult = null;
		try
		{
			
			mutantResult = executor.runMutantThread(cPath, env, null, true, testCaseType);
			mutantResult.setPreparingTime(prepareTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return mutantResult;
	}

	void deleteTemporaryFiles(String fileName)
	{

		if (fileName != null && !fileName.isEmpty())
		{
			File f = new File(fileName);
			f.deleteOnExit();

		}
	}

	@Override
	public void execute(TestCase tc, IMethod testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType)
	{
		//System.out.println("execute1 mutant: " + mutant.getMutantID());
		if (!this.executor.checkReachability(tc, mutant))
		{
			return;
		}

		String cID = mutant.getCodeID();
		if (visitedCodeSet.contains(cID))
		{
			return;
		}
		visitedCodeSet.add(cID);

		// mutant와 동일 CodeSet에 존재하는 모든 Mutant는 empty result를 등록한다.
		Set<String> codeSetMutants = executor.addEmptyCodeSetResult(mutant);

		// Obtaining mutants to be executed & delivery to test method
		Set<String> liveMutants = this.executor.getKilledOrLivedMutant(
				codeSetMutants, false);
		
		if(FOR_PAPER){
			liveMutants = codeSetMutants;
		}

		if (liveMutants.isEmpty())
		{
			return;
		}

		// Side Effect Change Point를 수집한다.
		Set<Integer> sideEffectChangePoints = this.executor
				.getSideEffectChangePoints(liveMutants);

		for (String mutantID : liveMutants)
		{
			MuJavaLogger.getLogger().reach("RE:" + mutantID);
			if (statisticHandler != null)
			{
				if (this.executor.checkSimpleReachability(tc, mutantID))
				{
					String testCaseID = RecordStatistics.getTestCaseID(tc
							.toSimpleString());
					statisticHandler.reach(RecordStatistics.REACH, testCaseID,
							mutantID);
				}
			}
		}

		if (CM_MUTANT_MODE)
		{
			executeCMWay(tc, testMethod, mutant, liveMutants,
					sideEffectChangePoints, testCaseType);
		}
		else
		{
			//System.out.println("mutant: " + mutant.getMutantID());
			executeWSWay(tc, testMethod, testCaseType, mutant,
					liveMutants, sideEffectChangePoints);
		}
	}

	/**
	 * Execute original code with nujava code for the test case under measuring
	 * time cost
	 * 
	 * @param eqTable
	 */
	@Override
	public void execute(TestCase tc, Method testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType)
	{
		//System.out.println("execute2");

		if (!this.executor.checkReachability(tc, mutant))
		{
			return;
		}

		String cID = mutant.getCodeID();
		if (visitedCodeSet.contains(cID))
		{
			return;
		}
		visitedCodeSet.add(cID);

		// mutant와 동일 CodeSet에 존재하는 모든 Mutant는 empty result를 등록한다.
		Set<String> codeSetMutants = executor.addEmptyCodeSetResult(mutant);

		// Obtaining mutants to be executed & delivery to test method
		Set<String> liveMutants = this.executor.getKilledOrLivedMutant(
				codeSetMutants, false);

		if (liveMutants.isEmpty())
		{
			return;
		}

		// Side Effect Change Point를 수집한다.
		Set<Integer> sideEffectChangePoints = this.executor
				.getSideEffectChangePoints(liveMutants);
		
		for (String mutantID : liveMutants)
		{
			MuJavaLogger.getLogger().reach("RE:" + mutantID);
			if (statisticHandler != null)
			{
				
				if (this.executor.checkSimpleReachability(tc, mutantID))
				{
					String testCaseID = RecordStatistics.getTestClassID(tc
							.toSimpleString());
					statisticHandler.reach(RecordStatistics.REACH, testCaseID,
							mutantID);
				}
			}
		}

		if (CM_MUTANT_MODE)
		{
			executeCMWay(tc, testMethod, mutant, liveMutants,
					sideEffectChangePoints, testCaseType);
		}
		else
		{
			executeWSWay(tc, testMethod, mutant, liveMutants,
					sideEffectChangePoints, testCaseType);
		}
	}

	private void executeCMWay(TestCase tc, IMethod testMethod,
			MuJavaMutantInfo mutant, Set<String> liveMutants,
			Set<Integer> sideEffectChangePoints, TestCaseType testCaseType)
	{
		DisjointSet mutantRelationSet = new DisjointSet();
		HashMap<String, MutantResult> executedRepresentativeMutants = new HashMap<String, MutantResult>();

		//System.out.println("executeCMWay1");

		try
		{
			Map<String, MutantResult> weakMutantMap = executeWeakMutation(
					executor, mutant, testMethod, tc, testCaseType, liveMutants,
					mutantRelationSet, sideEffectChangePoints);

			String testCaseID = RecordStatistics.getTestCaseID(tc
					.toSimpleString());

			// execute mutant only for very weakly killed
			Collection<String> mutantIDs = weakMutantMap.keySet();
			for (String mutantID : mutantIDs)
			{

				MuJavaLogger.getLogger().reach("WK:" + mutantID);

				if (statisticHandler != null)
				{
					statisticHandler.reach(RecordStatistics.WEAKLY_KILLEd,
							testCaseID, mutantID);
				}

				MuJavaMutantInfo mInfo = this.executor.getMutantInfo(mutantID);

				MutantResult weaklyKilledMutantResult = weakMutantMap
						.get(mutantID);

				MutantResult result = null;
				String repMutantID = (String) mutantRelationSet
						.findSet(mutantID);
				if (repMutantID == null)
				{
					System.err.println("NOT HAPPENED");
					repMutantID = mutantID;
				}

				result = conductCMStrongMutation(executedRepresentativeMutants,
						tc, testMethod, weaklyKilledMutantResult, mInfo,
						repMutantID, testCaseType);

				executor.addMutantResult(mInfo, result);

				if (result.isKilled())
				{
					MuJavaLogger.getLogger().reach("SK:" + mutantID);
					if (statisticHandler != null)
					{
						statisticHandler.reach(
								RecordStatistics.STRONGLY_KILLED, testCaseID,
								mutantID);
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void executeCMWay(TestCase tc, Method testMethod,
			MuJavaMutantInfo mutant, Set<String> liveMutants,
			Set<Integer> sideEffectChangePoints, TestCaseType testCaseType)
	{

		DisjointSet mutantRelationSet = new DisjointSet();
		HashMap<String, MutantResult> executedRepresentativeMutants = new HashMap<String, MutantResult>();

		try
		{
			Map<String, MutantResult> weakMutantMap = executeWeakMutation(
					executor, mutant, testMethod, tc, liveMutants,
					mutantRelationSet, sideEffectChangePoints);

			String testClassID = RecordStatistics.getTestClassID(tc
					.toSimpleString());

			// execute mutant only for very weakly killed
			Collection<String> mutantIDs = weakMutantMap.keySet();
			for (String mutantID : mutantIDs)
			{

				MuJavaLogger.getLogger().reach("WK:" + mutantID);

				if (statisticHandler != null)
				{
					statisticHandler.reach(RecordStatistics.WEAKLY_KILLEd,
							testClassID, mutantID);
				}

				MuJavaMutantInfo mInfo = this.executor.getMutantInfo(mutantID);

				MutantResult weaklyKilledMutantResult = weakMutantMap
						.get(mutantID);

				MutantResult result = null;
				String repMutantID = (String) mutantRelationSet
						.findSet(mutantID);
				if (repMutantID == null)
				{
					System.err.println("NOT HAPPENED");
					repMutantID = mutantID;
				}

				result = conductCMStrongMutation(executedRepresentativeMutants,
						tc, testMethod, weaklyKilledMutantResult, mInfo,
						repMutantID, testCaseType);

				executor.addMutantResult(mInfo, result);

				if (result.isKilled())
				{
					MuJavaLogger.getLogger().reach("SK:" + mutantID);
					if (statisticHandler != null)
					{
						statisticHandler.reach(
								RecordStatistics.STRONGLY_KILLED, testClassID,
								mutantID);
					}
				}
			}

		}
		catch (NoTestMethodException e)
		{
			// do nothing
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Save the result to the field variable, list weaklyKilledMutants
	 * 
	 * @param testClassObject
	 * @param testMethod
	 * @param tc
	 * @param cID
	 * @param weaklyKilledMutant
	 * @return original result as string type
	 * @throws IOException
	 */
	private void executeExpWeakMutation(TestExecutor executor,
			Method testMethod, TestCase tc, MuJavaMutantInfo hostMutantInfo,
			Set<String> liveMutants, Map<String, MutantResult> map,
			DisjointSet mutantRelationSet, Set<Integer> sideEffectChangePoints)
	{

		String tempFile = writeLiveMutants(liveMutants, sideEffectChangePoints);

		String returnFileName = "";
		String mutantIDFileName = "";

		try
		{
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();
		}
		catch (IOException e3)
		{
			return;
		}

		try
		{
			File mutantlistFile = File.createTempFile("mutant", null);
			mutantIDFileName = mutantlistFile.getAbsolutePath();
		}
		catch (IOException e3)
		{
			return;
		}

		// make channels to receive the tested results from the dynamic
		// invocation
		String className = testMethod.getDeclaringClass().getCanonicalName();
		String methodName = testMethod.getName();

		StringBuffer buffer = executor
				.getMutantClassPathStringBuffer(hostMutantInfo);
		String cPath = buffer.toString();

		Map<String, String> env = new HashMap<String, String>();
		try
		{
			env = setWeakExecutionEnvironments(executor, hostMutantInfo,
					returnFileName, className, methodName, tempFile,
					mutantIDFileName);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}

		try
		{
			originalResult = executor.runSerialMutantThread(cPath, env, null,
					false);
		}
		catch (Exception e)
		{
			// execption occurred -> abnormal execution
			originalResult = e.getCause().getClass().getName() + " : "
					+ e.getCause().getMessage();
		}

		// Retrive weakly killed mutant IDs
		mutantRelationSet = getWeaklyKilledMutants(mutantIDFileName,
				mutantRelationSet);
		makeMutantResultMap(executor, tc, map, mutantRelationSet);

		// files delete
		deleteTemporaryFiles(tempFile);
		deleteTemporaryFiles(mutantIDFileName);
		deleteTemporaryFiles(returnFileName);
	}

	private Set<String> executeExpWeakMutation(TestExecutor executor,
			Method testMethod, TestCase tc, MuJavaMutantInfo hostMutantInfo,
			Set<String> liveMutants, Set<Integer> sideEffectChangePoints)
	{

		String tempFile = writeLiveMutants(liveMutants, sideEffectChangePoints);

		String returnFileName = "";
		String mutantIDFileName = "";

		try
		{
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();
		}
		catch (IOException e3)
		{
			return new HashSet<String>();
		}

		try
		{
			File mutantlistFile = File.createTempFile("mutant", null);
			mutantIDFileName = mutantlistFile.getAbsolutePath();
		}
		catch (IOException e3)
		{
			return new HashSet<String>();
		}

		// make channels to receive the tested results from the dynamic
		// invocation
		String className = testMethod.getDeclaringClass().getCanonicalName();
		String methodName = testMethod.getName();

		StringBuffer buffer = executor
				.getMutantClassPathStringBuffer(hostMutantInfo);
		String cPath = buffer.toString();

		Map<String, String> env = new HashMap<String, String>();
		try
		{
			env = setWeakExecutionEnvironments(executor, hostMutantInfo,
					returnFileName, className, methodName, tempFile,
					mutantIDFileName);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			originalResult = executor.runSerialMutantThread(cPath, env, null,
					false);
		}
		catch (Exception e)
		{
			// execption occurred -> abnormal execution
			originalResult = e.getCause().getClass().getName() + " : "
					+ e.getCause().getMessage();
		}

		// Retrive weakly killed mutant IDs
		Set<String> ids = getWeaklyKilledMutants(mutantIDFileName);

		// files delete
		deleteTemporaryFiles(tempFile);
		deleteTemporaryFiles(mutantIDFileName);
		deleteTemporaryFiles(returnFileName);

		return ids;
	}

	/**
	 * CM 방식 Weak Mutation 수행
	 * 
	 * @param executor
	 * @param testMethod
	 * @param tc
	 * @param mutant
	 * @param liveMutants
	 * @param sideEffectChangePoints
	 * @param weaklyKilledMutant
	 * @param mutantRelationSet
	 */
	private void executeJUnitExpWeakMutation(TestExecutor executor,
			IMethod testMethod, TestCase tc, TestCaseType tcType, MuJavaMutantInfo mutant,
			Set<String> liveMutants, Set<Integer> sideEffectChangePoints,
			Map<String, MutantResult> weaklyKilledMutant,
			DisjointSet mutantRelationSet)
	{
		
		//System.out.println("executeJUnitExpWeakMutation1");
		String tempFile = writeLiveMutants(liveMutants, sideEffectChangePoints);

		String returnFileName = "";
		String mutantIDFileName = "";

		Map<String, String> env = new HashMap<String, String>();

		try
		{
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();

			File mutantlistFile = File.createTempFile("mutant", null);
			mutantIDFileName = mutantlistFile.getAbsolutePath();

			System.out.println("mutantIDFileName: " + mutantIDFileName);
			
			Map<String, String> envs = setWeakExecutionEnvironments(executor,
					mutant, returnFileName, testMethod, tempFile,
					mutantIDFileName);

			if (envs != null)
			{
				env.putAll(envs);
			}
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}

		File workDir = getWorkingDirectory(testMethod);

		try
		{
			String classPath = getJUnitClassPath(tcType, mutant, false);
			originalResult = executor.runJUnitSerialMutantThread(tcType,
					classPath, env, workDir, false);
		}
		catch (Exception e)
		{
			// execption occurred -> abnormal execution
			originalResult = e.getCause().getClass().getName() + " : "
					+ e.getCause().getMessage();
		}

		// Retrive weakly killed mutant IDs
		mutantRelationSet = getWeaklyKilledMutants(mutantIDFileName,
				mutantRelationSet);
		makeMutantResultMap(executor, tc, weaklyKilledMutant, mutantRelationSet);

		// files delete
		deleteTemporaryFiles(tempFile);
		deleteTemporaryFiles(mutantIDFileName);
		deleteTemporaryFiles(returnFileName);
	}

	/**
	 * 
	 * @param executor
	 * @param testMethod
	 * @param tc
	 * @param hostMutantInfo
	 * @param liveMutants
	 * @param sideEffectChangePoints
	 * @return
	 */
	private Set<String> executeJUnitExpWeakMutation(TestExecutor executor,
			IMethod testMethod, TestCase tc, TestCaseType tcType, MuJavaMutantInfo hostMutantInfo, 
			Set<String> liveMutants, Set<Integer> sideEffectChangePoints)
	{
		//System.out.println("executeJUnitExpWeakMutation2 hostMutantID: " + hostMutantInfo.getMutantID());

		String liveMutantFile = writeLiveMutants(liveMutants,
				sideEffectChangePoints);

		// make channels to receive the tested results from the dynamic
		// invocation
		String returnFileName = "";
		String mutantIDFileName = "";

		Map<String, String> env = new HashMap<String, String>();

		try
		{
			File returnFile = File.createTempFile("result", null);
			returnFileName = returnFile.getAbsolutePath();

			File mutantlistFile = File.createTempFile("mutant", null);
			mutantIDFileName = mutantlistFile.getAbsolutePath();
			
			
			env = setWeakExecutionEnvironments(executor, hostMutantInfo,
					returnFileName, testMethod, liveMutantFile,
					mutantIDFileName);
		}
		catch (IOException e1)
		{
			return new HashSet<String>();
		}

		File workDir = getWorkingDirectory(testMethod);
		//System.out.println("workDir: " + workDir);

		try
		{
			String classPath = getJUnitClassPath(tcType, hostMutantInfo, false);  // Look this ysma
			originalResult = executor.runJUnitSerialMutantThread(tcType, classPath, env,
					workDir, false);
		}
		catch (Exception e1)
		{
			// execption occurred -> abnormal execution
			originalResult = e1.getCause().getClass().getName() + " : "
					+ e1.getCause().getMessage();
		}

		// Retrive weakly killed mutant IDs
		Set<String> ids = getWeaklyKilledMutants(mutantIDFileName);
	//	System.out.println("originalResult: " + originalResult + " mutantIDFileName: " + mutantIDFileName + " ids: " + ids);

	//	System.out.println("liveMutantFile: " + liveMutantFile);
	//	System.out.println("returnFileName: " + returnFileName);

		// files delete
		deleteTemporaryFiles(liveMutantFile);
		deleteTemporaryFiles(mutantIDFileName);
		deleteTemporaryFiles(returnFileName);

		return ids;
	}

	private Collection<String> executeJUnitWSWeakMutation(
			TestExecutor executor, MuJavaMutantInfo mutant, IMethod testMethod,
			TestCase tc,  TestCaseType tcType, Set<String> liveMutants,
			Set<Integer> sideEffectChangePoints)
	{
		executor.setMonitorSubTask("Execute metamutant as original with a JUNIT test case : "
				+ tc.toString());

		// execute original code and make weakly killed mutants and
		// then their results are gathered
		long startTime = System.nanoTime();

		//System.out.println("executeJUnitWSWeakMutation mutant: " + mutant.getMutantID());
		Set<String> weaklyKilledMutants = executeJUnitExpWeakMutation(executor,
				testMethod, tc, tcType, mutant, liveMutants, sideEffectChangePoints);

		// add SerialMutant Execution Time
		long diffTime = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mutant, diffTime);

		//System.out.println("weaklyKilledMutants: " + weaklyKilledMutants);

		return weaklyKilledMutants;
	}

	private Map<String, MutantResult> executeWeakMutation(
			TestExecutor executor, MuJavaMutantInfo mutant, IMethod testMethod,
			TestCase tc, TestCaseType tcType, Set<String> liveMutants,
			DisjointSet mutantRelationSet, Set<Integer> sideEffectChangePoints)
	{
		executor.setMonitorSubTask("Execute metamutant as original with a test case : "
				+ tc.toString());

		// execute original code and make weakly killed mutants and
		// then their results are gathered
		Map<String, MutantResult> weaklyKilledMutant = new HashMap<String, MutantResult>();

		long startTime = System.nanoTime();

		executeJUnitExpWeakMutation(executor, testMethod, tc, tcType, mutant,
				liveMutants, sideEffectChangePoints, weaklyKilledMutant,
				mutantRelationSet);

		// add SerialMutant Execution Time
		long diffTime = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mutant, diffTime);

		return weaklyKilledMutant;
	}

	private Map<String, MutantResult> executeWeakMutation(
			TestExecutor executor, MuJavaMutantInfo mInfo, Method testMethod,
			TestCase tc, Set<String> liveMutants,
			DisjointSet mutantRelationSet, Set<Integer> sideEffectChangePoints)
			throws NoTestMethodException
	{

		executor.setMonitorSubTask("Execute metamutant as original with a test case : "
				+ tc.toString());

		// execute original code and make weakly killed mutants and
		// then their results are gathered
		Map<String, MutantResult> weaklyKilledMutant = new HashMap<String, MutantResult>();

		long startTime = System.nanoTime();

		executeExpWeakMutation(executor, testMethod, tc, mInfo, liveMutants,
				weaklyKilledMutant, mutantRelationSet, sideEffectChangePoints);

		// add SerialMutant Execution Time
		long diffTime = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mInfo, diffTime);

		return weaklyKilledMutant;
	}

	private void executeWSWay(TestCase tc, IMethod testMethod,
			TestCaseType testCaseType, MuJavaMutantInfo mutant,
			Set<String> liveMutants, Set<Integer> sideEffectChangePoints)
	{
		try
		{
			Collection<String> mutantIDs = executeJUnitWSWeakMutation(executor,
					mutant, testMethod, tc, testCaseType, liveMutants, sideEffectChangePoints);
			//System.out.println("executeWSWay1 wmutants: " + mutantIDs);

			String testCaseID = RecordStatistics.getTestCaseID(tc
					.toSimpleString());

			if(TestExecutor.WK_WRITE) {
				weaklyMutanIDs.addAll(mutantIDs);
			}
			
			if(FOR_PAPER && !DEEP_ANAL){return;}
			// execute mutant only for very weakly killed
			for (String mutantID : mutantIDs)
			{

				MuJavaLogger.getLogger().reach("WK:" + mutantID);
				if (statisticHandler != null)
				{
					statisticHandler.reach(RecordStatistics.WEAKLY_KILLEd,
							testCaseID, mutantID);
				}

				MuJavaMutantInfo mInfo = this.executor.getMutantInfo(mutantID);

				MutantResult result = conductJunitStrongMutation(testMethod,
						mInfo, testCaseType);
				result.setMutantID(mutantID);
				result.setMutantOperator(mInfo.getMutationOperatorName());
				result.setTestCase(tc);
				result.setOriginalResult(originalResult);
				executor.addMutantResult(mInfo, result);
		
				if (result.isKilled())
				{
					MuJavaLogger.getLogger().reach("SK:" + mutantID);
					if (statisticHandler != null)
					{
						statisticHandler.reach(
								RecordStatistics.STRONGLY_KILLED, testCaseID,
								mutantID);
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void executeWSWay(TestCase tc, Method testMethod,
			MuJavaMutantInfo mutant, Set<String> liveMutants,
			Set<Integer> sideEffectChangePoints, TestCaseType testCaseType)
	{
		//System.out.println("executeWSWay2");
		try
		{
			Collection<String> mutantIDs = executeWSWeakMutation(executor,
					mutant, testMethod, tc, liveMutants, sideEffectChangePoints);

			String testCaseID = RecordStatistics.getTestClassID(tc
					.toSimpleString());

			if(TestExecutor.WK_WRITE) {
				weaklyMutanIDs.addAll(mutantIDs);
			}
			// execute mutant only for very weakly killed
			for (String mutantID : mutantIDs)
			{

				MuJavaLogger.getLogger().reach("WK:" + mutantID);
				if (statisticHandler != null)
				{
					statisticHandler.reach(RecordStatistics.WEAKLY_KILLEd,
							testCaseID, mutantID);
				}

				MuJavaMutantInfo mInfo = this.executor.getMutantInfo(mutantID);

				MutantResult result = conductStrongMutation(testMethod, mInfo, testCaseType);
				result.setMutantID(mutantID);
				result.setMutantOperator(mInfo.getMutationOperatorName());
				result.setTestCase(tc);
				result.setOriginalResult(originalResult);
				executor.addMutantResult(mInfo, result);

				if (result.isKilled())
				{
					MuJavaLogger.getLogger().reach("SK:" + mutantID);
					if (statisticHandler != null)
					{
						statisticHandler.reach(
								RecordStatistics.STRONGLY_KILLED, testCaseID,
								mutantID);
					}
				}
			}

		}
		catch (NoTestMethodException e)
		{
			// do nothing
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private Collection<String> executeWSWeakMutation(TestExecutor executor,
			MuJavaMutantInfo mInfo, Method testMethod, TestCase tc,
			Set<String> liveMutants, Set<Integer> sideEffectChangePoints)
			throws NoTestMethodException
	{

		executor.setMonitorSubTask("Execute metamutant as original with a test case : "
				+ tc.toString());

		// execute original code and make weakly killed mutants and
		// then their results are gathered
		long startTime = System.nanoTime();

		Set<String> weaklyKilledMutants = executeExpWeakMutation(executor,
				testMethod, tc, mInfo, liveMutants, sideEffectChangePoints);

		// add SerialMutant Execution Time
		long diffTime = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mInfo, diffTime);

		return weaklyKilledMutants;
	}

	private String getClassPathStringBuffer(MuJavaMutantInfo hostMutantInfo,
			boolean useSubCodeSet)
	{
		IProject eclipseProject = hostMutantInfo.getResource().getProject();
		IJavaProject project = JavaCore.create(eclipseProject);
		StringBuffer sb = new StringBuffer();

		// 1. Project Bin Path 추가
		String codesetFile = executor.getArchiveFileName(hostMutantInfo,
				useSubCodeSet);
		if (codesetFile != null && !codesetFile.isEmpty())
		{
			sb.append(codesetFile);
			sb.append(File.pathSeparator);
		}

		try
		{
			String absoluteOutputLocation = executor
					.getWorkspaceRelativeFile(project.getOutputLocation());

			sb.append(absoluteOutputLocation);
			sb.append(File.pathSeparator);
		}
		catch (JavaModelException e1)
		{
		}

		// 2. Project Class Path 추가
		try
		{
			IClasspathEntry[] entries = project.getResolvedClasspath(true);
			for (IClasspathEntry cEntry : entries)
			{
				if (cEntry.getContentKind() == IPackageFragmentRoot.K_BINARY)
				{
					IPath path = cEntry.getPath();
					if (path.getDevice() == null)
					{
						String absoluteLibraryLocation = executor
								.getWorkspaceRelativeFile(path);
						
						if(MuJavaMutationEngine.IS_WINDOWS) {
							sb.append(absoluteLibraryLocation);
						} else {
							sb.append(path.toString());
						}
						sb.append(File.pathSeparator);
					}
					else
					{
						sb.append(cEntry.getPath().toOSString());
						sb.append(File.pathSeparator);
					}
				}
			}
		}
		catch (JavaModelException e1)
		{
		}

		// 3. NuJava lib Path 추가
		try
		{
			String path = getMuJavaLibraryPath("msg.jar");
			sb.append(path);
			sb.append(File.pathSeparator);
			path = getMuJavaLibraryPath("nujava.jar");
			sb.append(path);
			sb.append(File.pathSeparator);
			path = getMuJavaLibraryPath("xstream-1.3.jar");
			sb.append(path);
			sb.append(File.pathSeparator);
			path = getMuJavaLibraryPath("xpp3_min-1.1.4c.jar");
			sb.append(path);
			sb.append(File.pathSeparator);
			path = getMuJavaLibraryPath("interstate.jar");
			sb.append(path);
			sb.append(File.pathSeparator);
		}
		catch (NullPointerException e1)
		{
		}

		return sb.toString();
	}

	private String getJUnitClassPath(TestCaseType tcType, MuJavaMutantInfo hostMutantInfo,
			boolean useSubCodeSet)
	{
		String classPath = getClassPathStringBuffer(hostMutantInfo,
				useSubCodeSet).toString();

		switch(tcType)
		{
		case JU4:
			try
			{
				String executorPath = getMuJavaLibraryPath("JUnit4Executor.jar");

				return (executorPath + File.pathSeparator + classPath);
			}
			catch (NullPointerException e1)
			{
			}
			break;
			
		case JU3:
			try
			{
				String executorPath = getMuJavaLibraryPath("JUnit3Executor.jar");

				return (executorPath + File.pathSeparator + classPath);
			}
			catch (NullPointerException e1)
			{
			}
		default:
			// DO NOTHING
		}
		
		return classPath;
	}

	/**
	 * Plugin 에 등록된 전체 Library 중 주어진 이름의 라이브러리를 확인하고, 해당 주소를 반환한다.
	 * 
	 * @param fileName
	 * @return
	 */
	private String getMuJavaLibraryPath(String fileName)
	{
		// Plugin 등록 라이브버리 확인
		Enumeration<String> paths = MuJavaPlugin.getDefault().getBundle()
				.getEntryPaths("lib");

		while (paths.hasMoreElements())
		{
			String libPath = paths.nextElement();

			try
			{
				URL url = FileLocator.toFileURL(MuJavaPlugin.getDefault()
						.getBundle().getEntry(libPath));
				String fileUrl = url.getFile();

				if (fileUrl != null && !fileUrl.isEmpty())
				{
					IPath libFullPath = new Path(fileUrl);

					if (fileName.equals(libFullPath.lastSegment()))
					{
						return libFullPath.toOSString();
					}
				}
			}
			catch (IOException e)
			{
				continue;
			}
		}

		return "";
	}

	private Set<String> getWeaklyKilledMutants(String mutantIDFileName)
	{

		HashSet<String> ids = new HashSet<String>();

		if (mutantIDFileName == null || mutantIDFileName.isEmpty())
		{
			return ids;
		}

		try
		{
			FileInputStream fis = new FileInputStream(mutantIDFileName);
			FileChannel fic = fis.getChannel();

			if (fic.size() == 0)
			{
				fic.close();
				fis.close();

				return ids;
			}

			ByteBuffer buf = ByteBuffer.allocateDirect((int) fic.size());
			fic.read(buf);
			buf.rewind();

			while (buf.hasRemaining())
			{
				// read header
				int header = buf.getInt();
				switch (header)
				{
				case 1234:
				{
					// read mutant ID
					String mID = readString(buf);
					ids.add(mID);
				}
					break;
				case 4321:
				{
				}
					break;
				}
			}

			fic.close();
			fis.close();
		}
		catch (Exception e)
		{

		}

		return ids;
	}

	private DisjointSet getWeaklyKilledMutants(String mutantIDFileName,
			DisjointSet mutantRelationSet)
	{

		if (mutantIDFileName == null || mutantIDFileName.isEmpty())
		{
			return mutantRelationSet;
		}

		MutantPartition mutantPartitions = new MutantPartition();
		try
		{
			FileInputStream fis = new FileInputStream(mutantIDFileName);
			FileChannel fic = fis.getChannel();

			if (fic.size() == 0)
			{
				fic.close();
				fis.close();
				return mutantRelationSet;
			}

			ByteBuffer buf = ByteBuffer.allocateDirect((int) fic.size());
			fic.read(buf);
			buf.rewind();

			while (buf.hasRemaining())
			{
				// read header
				int header = buf.getInt();
				switch (header)
				{
				case 1234:
				{
					// read mutant ID
					String mID = readString(buf);
					mutantPartitions.add(mID);
				}
					break;
				case 4321:
				{
					String mID = readString(buf);
					String mutantResult = readString(buf);
					long time = buf.getLong();

					mutantPartitions.add(mID, time, mutantResult);
				}
					break;
				}
			}

			fic.close();
			fis.close();
		}
		catch (Exception e)
		{
		}

		mutantPartitions.close();
		mutantPartitions.makeDisjointSet(mutantRelationSet);

		return mutantRelationSet;
	}

	/**
	 * JUnit Test 수행 폴더 위치를 제공 ( Project 폴더)
	 * 
	 * @param testMethod
	 * @return
	 */
	private File getWorkingDirectory(IMethod testMethod)
	{
		IJavaProject jProject = testMethod.getJavaProject();
		if (jProject == null)
		{
			return null;
		}

		IProject eProject = jProject.getProject();
		if (eProject == null)
		{
			return null;
		}

		return eProject.getLocation().toFile();
	}

	private void makeMutantResultMap(TestExecutor executor, TestCase tc,
			Map<String, MutantResult> map, Collection<String> killedMutants)
	{

		if (killedMutants == null || killedMutants.isEmpty())
		{
			return;
		}

		for (String mID : killedMutants)
		{

			// Skip executing mutants if the mutant is already killed
			String op = MutantID.getMutationOperator(mID);
			if (executor.isKilled(op, mID))
			{
				continue;
			}

			MutantResult subResult = new MutantResult();
			subResult.setMutantID(mID);
			subResult.setTestCase(tc);
			subResult.setOriginalResult(originalResult);
			subResult.setMutantOperator(op);

			map.put(mID, subResult);
		}
	}

	private void makeMutantResultMap(TestExecutor executor, TestCase tc,
			Map<String, MutantResult> map, DisjointSet mutantRelationSet)
	{

		List<String> killedMutantList = new ArrayList<String>();
		mutantRelationSet.toList(killedMutantList);
		makeMutantResultMap(executor, tc, map, killedMutantList);
	}

	@Override
	public void postProcess()
	{

	}

	@Override
	public void preProcess()
	{
		visitedCodeSet.clear();
	}

	private String readString(ByteBuffer buf)
	{
		int size = buf.getInt();
		char[] buff = new char[size];
		for (int i = 0; i < size; i++)
		{
			buff[i] = buf.getChar();
		}

		// groups.add(buff);
		return new String(buff);
	}

	public void setCMmode(boolean flag)
	{
		CM_MUTANT_MODE = flag;
	}

	private Map<String, String> setExecutionEnvironments(IMethod testMethod,
			MuJavaMutantInfo mInfo)
	{
		String mutantID = mInfo.getMutantID();

		Map<String, String> env = new HashMap<String, String>();
		env.put(runner.Executor.ID_CLZ_NAME, testMethod.getDeclaringType()
				.getFullyQualifiedName());
		env.put(runner.Executor.ID_METHOD_NAME, testMethod.getElementName());
		env.put(runner.Executor.ID_MONITOR_NAME, "MSG.MutantMonitor");
		env.put(runner.Executor.ID_SIZE_CHANGEPOINT,
				Integer.toString(executor.getMaxChangePoint(mInfo)));
		env.put(MSG.MSGConstraints.header_mutantID, mutantID);
		// env.put(runner.Executor.ID_TIME_TRACK_MODE, "TIMEOUT");

		try
		{
			File returnFile = File.createTempFile("result", null);
			String returnFileName = returnFile.getAbsolutePath();

			env.put(runner.Executor.ID_RESULT, returnFileName);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		return env;
	}

	public void setExecutor(TestExecutor testExecutor)
	{
		this.executor = testExecutor;
	}

	@Override
	public void setStatisticHandler(RecordStatistics handler)
	{
		this.statisticHandler = handler;
	}

	private Map<String, String> setWeakExecutionEnvironments(
			TestExecutor executor, MuJavaMutantInfo hostMutantInfo,
			String returnFileName, IMethod testMethod, String liveMutantFile,
			String mutantIDFileName) throws IOException
	{
		String className = testMethod.getDeclaringType()
				.getFullyQualifiedName();
		String methodName = testMethod.getElementName();

		return setWeakExecutionEnvironments(executor, hostMutantInfo,
				returnFileName, className, methodName, liveMutantFile,
				mutantIDFileName);
	}

	private Map<String, String> setWeakExecutionEnvironments(
			TestExecutor executor, MuJavaMutantInfo hostMutantInfo,
			String returnFileName, String className, String methodName,
			String liveMutantFile, String mutantIDFileName) throws IOException
	{
		Map<String, String> env = new HashMap<String, String>();

		env.put(runner.Executor.ID_CLZ_NAME, className);
		env.put(runner.Executor.ID_METHOD_NAME, methodName);
		env.put(runner.Executor.ID_MONITOR_NAME, "nujava.MutantMonitor");
		env.put(runner.Executor.ID_SIZE_CHANGEPOINT,
				Integer.toString(executor.getMaxChangePoint(hostMutantInfo)));
		if (returnFileName != null && !returnFileName.isEmpty())
		{
			env.put(runner.Executor.ID_RESULT, returnFileName);
		}

		// CM 사용 appproach의 경우를 표시, 만약 weak and strong mutation을 수행하기 위해서는
		// ExpressionWeakMutationMode로 변경한다.
		env.put(NuJavaHelper.header_mutationMode,
				Integer.toString(NuJavaHelper.ExpressionWeakMutationMode));

		if (liveMutantFile != null && !liveMutantFile.isEmpty())
		{
			env.put(NuJavaHelper.header_liveMutantFileName, liveMutantFile);
		}
		if (mutantIDFileName != null && !mutantIDFileName.isEmpty())
		{
			env.put(NuJavaHelper.header_killedMutantFileName, mutantIDFileName);
		}

		// CM 사용 appproach의 경우를 표시, 만약 weak and strong mutation을 수행하기 위해서는
		// ExpressionWeakMutationMode로 변경한다.
		int mode = (CM_MUTANT_MODE) ? NuJavaHelper.ConditonalMutualMutantMode
				: NuJavaHelper.ExpressionWeakMutationMode;
		env.put(NuJavaHelper.header_mutationMode, Integer.toString(mode));

		return env;
	}

	private String writeLiveMutants(Set<String> liveMutants,
			Set<Integer> sideEffectChangePoints)
	{

		// Initializing the property
		// deleteTemporaryFiles(tempFile);

		File file = null;

		try
		{
			file = File.createTempFile(
					String.valueOf(System.currentTimeMillis()), null);
			FileOutputStream fs = new FileOutputStream(file);
			FileChannel foc = fs.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE + 200
					* liveMutants.size());

			// Write Side Effect Change Points
			if (sideEffectChangePoints == null)
			{
				buf.putInt(0);
			}
			else
			{
				buf.putInt(sideEffectChangePoints.size());
			}
			for (int chpt : sideEffectChangePoints)
			{
				buf.putInt(chpt);
			}

			// Write live mutants
			buf.putInt(liveMutants.size());
			for (String mutant : liveMutants)
			{
				char[] content = mutant.toCharArray();
				buf.putInt(content.length);
				for (int i = 0; i < content.length; i++)
				{
					buf.putChar(content[i]);
				}
			}

			buf.flip();
			foc.write(buf);
			foc.close();
			fs.close();
			fs = null;

			return file.getAbsolutePath();
		}
		catch (IOException e1)
		{
		}

		return "";
	}

	public void writeWeaklyMutantIDs(String fName) {
		// TODO Auto-generated method stub
		   File dir = new File(weaklyMutantsOutDir);
	       if(!dir.exists()){
	           dir.mkdir();
	       }
		
	      ObjectOutputStream out;
		try {
			FileOutputStream file = new FileOutputStream(weaklyMutantsOutDir + File.separator + fName);
			out = new ObjectOutputStream(file);
		    out.writeObject(weaklyMutanIDs);		
		    out.flush();
		    out.close();
		    
			System.out.println("saving Weakly killed mutants = " + fName);
			System.out.println("HashSet Size = " + weaklyMutanIDs.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

}

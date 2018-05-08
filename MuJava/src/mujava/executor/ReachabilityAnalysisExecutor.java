package mujava.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantResult;
import mujava.TestCaseType;
import mujava.plugin.MuJavaPlugin;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.util.RecordStatistics;
import nujava.NuJavaHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class ReachabilityAnalysisExecutor implements ITestExecutor
{

	boolean onceReached = false;

	/**
	 * reachable mutant
	 */
	List<String> reachable = new ArrayList<String>();

	// visited codeset list
	List<String> visitedCodeSet = new ArrayList<String>();
	private TestExecutor executor;
	private RecordStatistics statisticHandler = null;

	@Override
	public void afterRunAllTestMethod()
	{
		// do nothing
	}

	@Override
	public void beforeRunAllTestMethod()
	{
		// do nothing
	}

	@Override
	public void beforeRunATestMethod()
	{
		// do nothing
	}

	private void deleteTemporaryFiles(String fileName)
	{

		if (fileName != null && !fileName.isEmpty())
		{
			File f = new File(fileName);
			f.deleteOnExit();
		}
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

	private String getJUnit4ClassPath(MuJavaMutantInfo hostMutantInfo)
	{
		String classPath = getClassPathStringBuffer(hostMutantInfo, false)
				.toString();

		try
		{
			String j4ExecutorPath = getMuJavaLibraryPath("JUnit4Executor.jar");

			classPath = j4ExecutorPath + File.pathSeparator + classPath;
		}
		catch (NullPointerException e1)
		{
		}

		return classPath;
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

						sb.append(absoluteLibraryLocation);
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
			String path = getMuJavaLibraryPath("nujava.jar");
			sb.append(path);
			sb.append(File.pathSeparator);

			path = getMuJavaLibraryPath("reach.jar");
			sb.append(path);
			sb.append(File.pathSeparator);

		}
		catch (NullPointerException e1)
		{
		}

		return sb.toString();
	}

	private void invoke(Set<String> mutantIDs, String reachMutantFileName,
			String cPath, Map<String, String> env, File workDirectory, TestCaseType tcType)
	{
		// Create a thread to execute a mutant
		try
		{
			executor.runReachThread(cPath, env, workDirectory, tcType);
		}
		catch (Exception e)
		{
		}

		// Retrive weakly killed mutant IDs
		List<String> reachedMutants = readWeakMutantID(reachMutantFileName);
		for (String prefix : reachedMutants)
		{

			for (String mutantID : mutantIDs)
			{

				if (mutantID.startsWith(prefix + "_"))
				{

					if (!reachable.contains(mutantID))
					{
						reachable.add(mutantID);
					}
				}
			}
		}

		Collections.sort(reachable);
	}

	public void execute(TestCase tc, Method testMethod,
			MuJavaMutantInfo mutant, TestCaseType testCaseType)
	{
		// 한번이상 수행되었을을 기록
		onceReached = true;

		// /
		String cID = mutant.getCodeID();
		if (visitedCodeSet.contains(cID))
		{
			return;
		}
		visitedCodeSet.add(cID);

		// /
		long startTime = System.nanoTime();

		Map<String, String> env = getEnvironmentVariables(testMethod);

		internalReach(mutant, env, null, testCaseType);

		// measure executing time
		long time = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mutant, time);
	}

	@Override
	public void execute(TestCase tc, IMethod testMethod,
			MuJavaMutantInfo mutant, TestCaseType testCaseType)
	{
		// 한번이상 수행되었을을 기록
		onceReached = true;

		// /
		String cID = mutant.getCodeID();
		if (visitedCodeSet.contains(cID))
		{
			return;
		}
		visitedCodeSet.add(cID);

		// /
		long startTime = System.nanoTime();

		Map<String, String> env = getEnvironmentVariables(testMethod);
		
		File workDir = getWorkingDirectory(testMethod);
		
		internalReach(mutant, env, workDir, testCaseType);

		// measure executing time
		long time = (System.nanoTime() - startTime) / 1000000;
		executor.addAnalysisMutantResult(mutant, time);
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
	
	private String getResultFile()
	{
		String reachMutantFileName = "";
		try
		{
			File reachMutantFile = File.createTempFile("result", null);
			reachMutantFileName = reachMutantFile.getAbsolutePath();
		}
		catch (IOException e3)
		{
		}

		return reachMutantFileName;
	}

	private void internalReach(MuJavaMutantInfo mutant,
			Map<String, String> env, File workDirectory, TestCaseType tcType)
	{
		String reachMutantFileName = getReachMutantFileName(env);

		StringBuffer buffer = executor.getMutantClassPathStringBuffer(mutant);
		String cPath = buffer.append(".").toString();

		if (tcType == TestCaseType.JU4)
		{
			cPath = getJUnit4ClassPath(mutant);
		}

		String op = mutant.getMutationOperatorName();
		Set<String> mutantIDs = executor.getMutants(op);

		// Mutant 를 수행함
		invoke(mutantIDs, reachMutantFileName, cPath, env, workDirectory, tcType);

		// cleanup corresponding resources
		deleteTemporaryFiles(reachMutantFileName);
	}

	private String getReachMutantFileName(Map<String, String> env)
	{
		return env.get(NuJavaHelper.header_reachedMutantFileName);
	}

	@Override
	public void postProcess()
	{

		// Reachablity anaysis를 통해 수집된 List를 출력한다.
		// 없는 경우도 표시하기 위해 Flag를 사용해서 한번이라도 Reachability Analysis가
		// 수행된 경우에만 출력한다.
		if (onceReached)
		{
			if (reachable.size() > 0)
			{
				for (String id : reachable)
				{
					MuJavaLogger.getLogger().reach("List:," + id);
				}
			}
			else
			{
				MuJavaLogger.getLogger().reach("NONE");
			}
		}

	}

	@Override
	public void preProcess()
	{
		onceReached = false;
		reachable.clear();
		visitedCodeSet.clear();
	}

	private Map<String, String> getEnvironmentVariables(Object testMethod)
	{
		Map<String, String> env = new HashMap<String, String>();

		// Monitor 설정
		env.put(runner.Executor.ID_MONITOR_NAME, "mujava.ReachMonitor");

		// Mutant 목록 파일 값 설정
		String reachMutantFileName = getResultFile();
		env.put(NuJavaHelper.header_reachedMutantFileName, reachMutantFileName);

		// Class 및 Method 설정
		if (testMethod instanceof IMethod)
		{
			IMethod method = (IMethod) testMethod;

			String className = method.getDeclaringType().getFullyQualifiedName();
			env.put(runner.Executor.ID_CLZ_NAME, className);
			
			env.put(runner.Executor.ID_METHOD_NAME, method.getElementName());
		}
		else if (testMethod instanceof Method)
		{
			Method method = (Method) testMethod;

			env.put(runner.Executor.ID_CLZ_NAME, method.getDeclaringClass()
					.getCanonicalName());
			env.put(runner.Executor.ID_METHOD_NAME, method.getName());
		}

		return env;
	}

	private List<String> readWeakMutantID(String fileName)
	{

		ArrayList<String> reachedMutants = new ArrayList<String>();

		if (fileName == null || fileName.isEmpty())
		{
			return reachedMutants;
		}

		try
		{
			FileInputStream fis = new FileInputStream(fileName);
			FileChannel fic = fis.getChannel();
			if (fic.size() == 0)
			{
				fic.close();
				fis.close();
				return reachedMutants;
			}

			ByteBuffer buf = ByteBuffer.allocateDirect((int) fic.size());
			fic.read(buf);
			buf.rewind();

			while (buf.hasRemaining())
			{
				// read header
				int header = buf.getInt();
				if (header == 1234)
				{
					// read mutant ID
					int size = buf.getInt();
					char[] buff = new char[size];
					for (int i = 0; i < size; i++)
					{
						buff[i] = buf.getChar();
					}

					String mID = new String(buff);
					reachedMutants.add(mID);
				}
			}

			fic.close();
			fis.close();
		}
		catch (Exception e)
		{
		}

		return reachedMutants;
	}

	@Override
	public void setExecutor(TestExecutor executor)
	{
		this.executor = executor;
	}

	@Override
	public void setStatisticHandler(RecordStatistics handler)
	{
		this.statisticHandler = handler;
	}

}

package mujava.executor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

import mujava.MuJavaMutantInfo;
import mujava.TestCaseType;
import mujava.gen.MuJavaMutationEngine;
import mujava.plugin.MuJavaPlugin;

public class JUnitUtil {

	private static String getMuJavaLibraryPath(String fileName)
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
	
	public  static File getWorkingDirectory(IMethod testMethod)
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
	
	public static Map<String, String> setExecutionEnvironments(TestExecutor executor,IMethod testMethod,
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

		return env;
	}	
	
	public static String getClassPathStringBuffer(TestExecutor executor,MuJavaMutantInfo hostMutantInfo,
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
						
						//sb.append(absoluteLibraryLocation);
						//sb.append(File.pathSeparator);
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
	
	public static String getClassPathStringBuffer2(TestExecutor executor,IMethod testMethod,boolean useSubCodeSet)
	{
		IProject eclipseProject =  testMethod.getJavaProject().getProject();
		IJavaProject project = JavaCore.create(eclipseProject);
		StringBuffer sb = new StringBuffer();

		// 1. Project Bin Path 추가
		/*String codesetFile = executor.getArchiveFileName(hostMutantInfo,
				useSubCodeSet);
		if (codesetFile != null && !codesetFile.isEmpty())
		{
			sb.append(codesetFile);
			sb.append(File.pathSeparator);
		}
*/
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
	
	public static String getJUnitClassPath(TestExecutor executor,TestCaseType tcType, MuJavaMutantInfo hostMutantInfo,
			boolean useSubCodeSet)
	{
		String classPath = getClassPathStringBuffer(executor,hostMutantInfo,
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
	
	public static String getJUnitClassPath2(TestExecutor executor, IMethod testMethod,TestCaseType tcType, boolean useSubCodeSet)
	{
		String classPath = getClassPathStringBuffer2(executor,testMethod,useSubCodeSet).toString();

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
}

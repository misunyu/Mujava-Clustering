package mujava.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.TestCaseType;

import kaist.selab.util.MuJavaLogger;

public class DefaultExecutorThread implements IExecutorThread
{
	private boolean running = false;
	
	private Object lockObject = null;
	
	Process executorProcess = null;
	
	private File workDir = null;
	
	private String classPath = "";
	
	private Map<String, String> env = new HashMap<String, String>();
	
	private TestCaseType testCaseType = TestCaseType.NONE;
	
	DefaultExecutorThread(Object lockObject, String classPath, Map<String, String> env, File workDirectory, TestCaseType tcType)
	{
		this.lockObject = lockObject;
		this.testCaseType = tcType;
		this.classPath = classPath;
		this.workDir = workDirectory;
		
		if (env != null)
		{
			this.env.putAll(env);
		}
	}
	
	@Override
	public final void run()
	{
		running = true;

		startExecutor();
		
		waitExecution();
		
		running = false;
		
		// Lock Object 동기화
		synchronize();
	}


	@Override
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void finish()
	{
		if (executorProcess != null)
		{
			executorProcess.destroy();
			executorProcess = null;
		}
		
		// Clear Lock Object
		lockObject = null;
	}

	void startExecutor()
	{
		List<String> arguments = getBuilderArguments(testCaseType, classPath);

		ProcessBuilder builder = new ProcessBuilder(arguments);
		
		try
		{
			if (env != null)
			{
				builder.environment().putAll(env);
			}
			
			if (workDir != null && workDir.exists())
			{
				builder.directory(workDir);
			}
			
			// builder.directory(targetRootPath.toFile());
			builder.redirectErrorStream(true);

			// run command
			executorProcess = builder.start();

			// grab related streams, including error output stream
			printProcessOutput();

		}
		catch (Exception e)
		{
		}
	}
	
	protected void waitExecution()
	{
		if (executorProcess == null)
		{
			return ;
		}
		
		try
		{
			executorProcess.waitFor();
		}
		catch (InterruptedException e)
		{
		}
	}

	protected void printProcessOutput() throws IOException
	{
		if(executorProcess == null)
		{
			return ;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				executorProcess.getInputStream()));

		String line = "";
		while ((line = reader.readLine()) != null)
		{
			MuJavaLogger.getLogger().execResult(line);
			//System.out.println(" YSMA: "+line);
		}

		reader.close();
	}

	/**
	 * "java -classpath \"" + classPath + "\" " + " runner.Executor"
	 * 
	 * @param testCaseType
	 * @param classPath
	 * @return
	 */
	protected List<String> getBuilderArguments(TestCaseType testCaseType,
			String classPath)
	{
		List<String> arguments = new ArrayList<String>();

		arguments.add("java");
		arguments.add("-classpath");
		arguments.add(classPath);

		switch (testCaseType)
		{
		case MJ:
			arguments.add("runner.Executor");
			break;
		case JU4:
			arguments.add("runner.J4Executor");
			break;
		case JU3:
			arguments.add("runner.J3Executor");
			break;

		default:
			break;
		}

		MuJavaLogger.getLogger().execCmd(arguments.toString());

		return arguments;
	}
	
	private void synchronize()
	{
		if (lockObject == null)
		{
			return;
		}

		synchronized (lockObject)
		{
			lockObject.notify();
		}
	}
}

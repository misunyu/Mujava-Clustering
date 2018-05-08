package mujava.executor;

import java.io.File;
import java.util.Map;

import mujava.TestCaseType;

public class MutantExecutorThread extends DefaultExecutorThread
{
	public MutantExecutorThread(Object lockObject, String path, File dir,
			Map<String, String> env, TestCaseType tcType)
	{
		super(lockObject, path, env, dir, tcType );
	}

}

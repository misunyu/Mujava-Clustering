package test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import nujava.NuJavaHelper;

import mujava.TestCaseType;
import mujava.executor.MutantExecutorThread;

public class ExecutorTest {
	Object lockObject;
	int TIMEOUT = 10000;

	public static void main(String[] args) {
		ExecutorTest test = new ExecutorTest();
		test.run();
	}

	public void run() {
		long executionTimeWithOutTimeOut = 0;
		long executionTimeWithTimeOut = 0;
		long comparingTime = 0;
		StringBuffer sb = new StringBuffer();
		lockObject = new Object();
		String path = "D:\\testEclipse/RE/Tests;D:\\testEclipse\\RE\\Mutants\\nujava\\1219674870316\\1219674870316.jar;D:\\testEclipse\\RE;D:\\JavaWorks\\MuJava_EclipsePlugin\\lib\\xstream-1.2.2.jar;D:\\JavaWorks\\MuJava_EclipsePlugin;D:\\JavaWorks\\MuJava_EclipsePlugin\\lib\\msg.jar;D:\\JavaWorks\\MuJava_EclipsePlugin\\lib\\nujava.jar;D:\\JavaWorks\\MuJava_EclipsePlugin\\lib\\original.jar;.";

		for (int i = 0; i < 10; i++) {
			long currentTime = System.nanoTime();
			long localStartTime = currentTime;

			Map<String, String> env = new HashMap<String, String>();
			env.put("mujava.original.class", "Test6");
			env.put("mujava.original.method", "test0_2");
			env.put("nujava.MSG.mutantID", "ASRS_-2142650463_1_5");
			env.put("nujava.mutationMode", "2");
			//env.put(runner.Executor.ID_TIME_OUT, String.valueOf(TIMEOUT));

			MutantExecutorThread t = new MutantExecutorThread(lockObject, path,
					null, env, TestCaseType.MJ);
			ExecutorService pool = Executors.newSingleThreadExecutor();
			pool.execute(t);

			try {
				synchronized (lockObject) {
					// Chek out if a mutant is in infinite loop
					lockObject.wait(TIMEOUT);
				}
				// when the mutant execution is finished or timeout is
				// occured
				if (!t.isRunning()) {
					currentTime = System.nanoTime();
					executionTimeWithOutTimeOut += (currentTime - localStartTime) / 1000000;

					// comparing results
					long beforeTime = currentTime;
					// mResult.isKilled();
					comparingTime += (System.nanoTime() - beforeTime) / 1000000;

				} else {
					// MuJavaLogger.getLogger().debug("NonFinishExecuting");
					t.finish(); // intential termination
					// MuJavaLogger.getLogger().debug("Interrupting");

					// measuring time
					currentTime = System.nanoTime();
					executionTimeWithTimeOut += (currentTime - localStartTime) / 1000000;

					// make an output
					sb.setLength(0);
					sb.append("time_out: more than ");
					sb.append(TIMEOUT);
					sb.append(" seconds");
					System.out.println(sb.toString());
					// mResult.setMutantResult(sb.toString());

					// measuring the comparing time
					long beforeTime = currentTime;
					// mResult.isKilled(); // comparing results
					comparingTime += (System.nanoTime() - beforeTime) / 100000;
				}
			} catch (Exception e) {
				// MuJavaLogger.getLogger().debug("Exception");
				System.out.println("Exception");

				// measuring time
				executionTimeWithOutTimeOut += (System.nanoTime() - localStartTime) / 1000000;

				// make an output

				Object result = (e.getCause() == null) ? "null" : e.getCause()
						.getClass().getName()
						+ " : " + e.getCause().toString();
				System.err.println(result);
				// mResult.setMutantResult(result);

				// measuring the comparing time
				long beforeTime = System.nanoTime();
				// mResult.isKilled(); // comparing results
				comparingTime += (System.nanoTime() - beforeTime) / 1000000;
			}

			// clear the Thread
			if (!pool.isTerminated()) {
				t.finish();
			}
			t = null;
		}

		System.out.println(executionTimeWithOutTimeOut / 10);
		System.out.println(executionTimeWithTimeOut / 10);
	}
}

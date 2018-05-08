package kaist.selab.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class MuJavaLogger
{

	/**
	 * Singleton Pattern variable
	 */
	static MuJavaLogger instance = null;

	/**
	 * MuJavaLogger 인스턴스를 제공하는 정적 함수. Singleton Pattern
	 * 
	 * @return an mujava logger instance
	 */
	public static MuJavaLogger getLogger()
	{
		if (instance == null)
		{
			instance = new MuJavaLogger();
		}
		return instance;
	}

	/**
	 * Log4J 생성을 위한 함수.
	 * 
	 * @return an Log4J.Logger instance
	 */
	static Logger getMyLogger()
	{
		Logger logger = Logger.getLogger(MuJavaLogger.class);

		String pattern = "[%d{yyyy-MM-dd HH:mm:ss}] %-5p - %m%n";
		PatternLayout layout = new PatternLayout(pattern);

		// Eclipse Runtime directory;
		IWorkspace workSpace = ResourcesPlugin.getWorkspace();
		IPath path = workSpace.getRoot().getLocation();
		path = path.append("mujava.log");
		String fileName = path.toOSString();
		String datePattern = ".yyyy-MM-dd";

		try
		{
			logger.removeAllAppenders();

			// 파일 저장용
			FileAppender appender = new DailyRollingFileAppender(layout,
					fileName, datePattern);
			logger.addAppender(appender);

			// 화면 출력용
			logger.addAppender(new ConsoleAppender(layout));
			// start header
			logger.info(" *******************[ MuJava Plug-in Started ]");

			// TODO uncomment
			// LoggerOutputStream stream = new LoggerOutputStream(
			// LoggerOutputStream.OUT);
			// LoggerOutputStream streamErr = new LoggerOutputStream(
			// LoggerOutputStream.ERR);
			// System.setOut(new PrintStream(stream));
			// System.setErr(new PrintStream(streamErr));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return logger;
	}

	/**
	 * Log4J 생성을 위한 함수.
	 * 
	 * @return an Log4J.Logger instance
	 */
	static Logger getReachLogger()
	{
		Logger logger = Logger.getLogger("mujava.log.reach");

		return logger;
	}

	Logger logger = null;
	Logger reachLogger = null;

	private boolean execCMD = false;
	private boolean efficiency = false;
	private boolean execResult = false;
	private boolean time = false;
	private boolean reachability = false;

	private boolean isReachabiltyAnalysis = false;
	private File reachLogFile = null;

	MuJavaLogger()
	{
		this.logger = getMyLogger();
		this.reachLogger = getReachLogger();

		execCMD = false;
		efficiency = false;
		time = false;
		execResult = false;
		reachability = false;
	}

	void clearReachLogAppenders()
	{
		// 기존 LogAppender를 정리
		Enumeration<?> appenders = reachLogger.getAllAppenders();
		while (appenders.hasMoreElements())
		{
			Appender appender = (Appender) appenders.nextElement();
			appender.close();
		}
		reachLogger.removeAllAppenders();
	}

	public File getReachAnalaysisLog()
	{
		return (isReachabiltyAnalysis) ? reachLogFile : null;
	}

	public void stopReachAnalysisLog()
	{
		isReachabiltyAnalysis = false;
	}

	public void recordReachAnalysisLog(File reachLogFile, boolean overwrite)
	{
		clearReachLogAppenders();

		this.reachLogFile = reachLogFile;

		MuJavaLogger.getLogger().debug(
				"Reachability Analysis Log File = "
						+ reachLogFile.getAbsolutePath());

		addReachLogAppender(reachLogFile, overwrite);

		isReachabiltyAnalysis = true;
	}

	private void addReachLogAppender(File file, boolean overwrite)
	{
		String fileName = file.getAbsolutePath();

		try
		{
			// 파일 저장용 Appender 추가
			String pattern = "[%d{yyyy-MM-dd HH:mm:ss}] %-5p - %m%n";
			PatternLayout layout = new PatternLayout(pattern);
			FileAppender appender = new FileAppender(layout, fileName,
					!overwrite);
			reachLogger.addAppender(appender);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public void debug(Object string)
	{
		this.logger.debug(string);
	}

	public void efficientMsg(String string)
	{
		if (efficiency)
			this.logger.debug("[EFFICIENCY] " + string);
	}

	public void error(Object string)
	{
		this.logger.error(string);
	}

	public void execCmd(String string)
	{
		if (execCMD)
			this.logger.debug("[EXEC_CMD] " + string);
	}

	public void execResult(String string)
	{
		if (execResult)
			this.logger.debug("[EXEC_RESULT] " + string);
	}

	public void info(Object string)
	{
		this.logger.info(string);
	}

	public void reach(String message)
	{
		if (reachability)
		{
			this.logger.debug("[REACH] " + message);
		}

		if (isReachabiltyAnalysis)
		{
			this.reachLogger.debug("[REACH] " + message);
		}
	}

	public void timeMsg(String string)
	{
		if (time)
			this.logger.debug("[TIME]" + string);
	}

	public void warn(Object string)
	{
		this.logger.warn(string);
	}

	public void clearReachAnalysisLog()
	{
		this.clearReachLogAppenders();
	}
}

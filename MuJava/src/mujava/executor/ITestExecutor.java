package mujava.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import mujava.MuJavaMutantInfo;
import mujava.TestCaseType;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.util.RecordStatistics;

import org.eclipse.jdt.core.IMethod;

public interface ITestExecutor {

	void preProcess();

	/**
	 * Java Reflection 기반의 함수를 시험 사례로 수행시킴
	 * 
	 * @param tc
	 * @param testMethod
	 * @param mutant
	 */
	void execute(TestCase tc, Method testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType);

	/**
	 * Eclipse JDT 기반의 함수를 시험 사례로 수행시킴
	 * 
	 * @param tc
	 * @param testMethod
	 * @param mutant
	 * @param testCaseType 
	 */
	void execute(TestCase tc, IMethod testMethod, MuJavaMutantInfo mutant, TestCaseType testCaseType);

	void postProcess();

	void beforeRunATestMethod();

	void beforeRunAllTestMethod();

	void afterRunAllTestMethod();

	void setExecutor(TestExecutor executor);

	void setStatisticHandler(RecordStatistics handler);
	
	//void initResultFiles(String dir);
	
	// void closeResultFiles();

	// void writeLineToOriginalReultFile(String line);
}

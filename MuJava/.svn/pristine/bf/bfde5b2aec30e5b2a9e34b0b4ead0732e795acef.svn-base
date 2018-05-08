package mujava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import kaist.selab.util.Helper;
import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;
import mujava.plugin.editor.mutantreport.TestCase;
import mujava.plugin.editor.mutantreport.TestResultForOperator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.almworks.sqlite4java.SQLiteConnection;

public class ResultTable extends PropertyFile {
	private static Hashtable<IResource, ResultTable> tables = new Hashtable<IResource, ResultTable>();

	/**
	 * Create a new Result Table whose name is determined to its creating time.
	 * It has a sub-task (1 work from getResultTable)
	 * 
	 * @param muProject
	 * @param monitor
	 * @return new ResultTable
	 * @throws IOException
	 */
	public static ResultTable createResultTable(MuJavaProject muProject,
			IProgressMonitor monitor) throws IOException {

		if (monitor != null)
			monitor.subTask("Create a Result Table...");

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// Make a name of the new result table
		long time = System.currentTimeMillis();
		IPath path = muProject.getResource().getFullPath();
		path = path.removeLastSegments(1);
		path = path.append(muProject.getResultDirectory());

		Date now = new Date(time);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH-mm-ss-SSS");
		String timeStr = "D"+dateFormatter.format(now) + "T"+timeFormatter.format(now);
		path = path.append(timeStr);

		path = path.addFileExtension("report");
		IResource res = root.getFile(path);

		// Create a new result table object with the given file name
		ResultTable table = ResultTable.getResultTable(res, monitor);

		// initialize the other properties of the result table
		table.setMuJavaProject(muProject);

		// return the new table object
		return table;
	}

	/**
	 * For obtaining the report from the existing file. If given file does not
	 * exist, it create the file. It has a sub-task (1 worked from
	 * ResultTable.open)
	 * 
	 * @param resultTable
	 * @param monitor
	 * @return
	 * @throws IOException
	 *             due to method call ResultTable.save(IProgressMonitor monitor)
	 */
	public static ResultTable getResultTable(IResource res,
			IProgressMonitor monitor) throws IOException {

		ResultTable table = tables.get(res);

		if (table == null) {
			// If there is not a table with the given file, it create new
			// object.
			if (monitor != null)
				monitor.subTask("Open Result Table " + res.getName());

			table = new ResultTable(res);
			try {
				table.open(monitor);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				table.save(monitor);
			}

			// registry new table object to the static hashtable
			tables.put(res, table);
		}

		return table;
	}

	private boolean dirty = false;

	private boolean cmMode = false;

	private long actualTime;

	private long originalTime;

	private long startTime;

	private long usedDiskSpace;

	private long usedMemorySpace;

	private String usedReachTable = "";

	// testCase에서 실행된 TestCase들의 집합.
	private List<TestCase> executedTestCases = new ArrayList<TestCase>();
	// testCase들 중에서도 한번이라도 Mutant를 Kill하는 testCase들의 집합.
	private Set<TestCase> effectiveTestCases = new HashSet<TestCase>();
	// ResultTable에서 사용된 모든 TestCase의 집합.
	private List<TestCase> overallTestCases = new ArrayList<TestCase>();

	private Hashtable<String, TestResultForOperator> opResultCache = new Hashtable<String, TestResultForOperator>();

	private Map<String, List<MutantResult>> resultMap = new HashMap<String, List<MutantResult>>();

	private Map<String, List<String>> fileMaps = new HashMap<String, List<String>>();

	MuJavaProject muProject = null;

	MutantResult originalResult = null;

	Set<String> emptyResultOperators = new HashSet<String>();

	private ResultTable(IResource res) {
		super();
		super.resource = res;
		super.setFileComment("Executed Mutant Result Table for MuJava");
	}

	private void addExecutedTestCases(TestCase nTestCase) {
		if (nTestCase == null)
			return;

		if (!executedTestCases.contains(nTestCase))
			executedTestCases.add(nTestCase);
	}

	/**
	 * There should be only one test case reference which has the same method
	 * signature. If a test case whose attributes are the same as the one of the
	 * system is added to the system, we overwrite the one into the given test
	 * case of the mutant result.
	 * 
	 * @param result
	 * @param fileName
	 */
	public void addMutantResult(MutantResult result, String fileName) {

		if (result == null) {
			return;
		}

		TestCase tc = result.getTestCase();
		addExecutedTestCases(tc);

		if (result.isKilled()) {
			this.effectiveTestCases.add(tc);
		}

		String op = result.getMutantOperator();
		List<MutantResult> results = this.resultMap.get(op);
		if (results == null)
			results = new ArrayList<MutantResult>();
		List<String> files = this.fileMaps.get(op);
		if (files == null)
			files = new ArrayList<String>();
		if (!files.contains(fileName))
			files.add(fileName);

		// overwrite new mutant result to old one always
		int index = -1;
		for (MutantResult resultMutant : results) {
			// 이미 주어진 mutant에 관련한 결과가 저장된 경우
			if (resultMutant.getMutantID().equals(result.getMutantID())) {
				index = results.indexOf(resultMutant);
				break;
			}
		}

		if (emptyResultOperators.contains(op) && !result.isEmptyResult()) {
			emptyResultOperators.remove(op);
		}

		if (index >= 0) {
			MutantResult r = results.get(index);
			r.addMutantResult(result);
			results.set(index, r);
		} else
			results.add(result);

		// store new relation between results and the mutant operator op
		this.resultMap.put(op, results);
		this.fileMaps.put(op, files);

		setDirty(true);
	}

	public void addOriginalResult(MutantResult result) {
		TestCase tc = result.getTestCase();
		addExecutedTestCases(tc);

		if (this.originalResult != null) {
			originalResult.addMutantResult(result);
		} else {
			originalResult = result;
		}

		setDirty(true);
	}
	
	public void clearResultTable(){
		tables.clear();		

		executedTestCases.clear();
		effectiveTestCases.clear();
		overallTestCases.clear();
		opResultCache.clear();
		resultMap.clear();
		fileMaps.clear();
	}


	public void clearMutantResult(String mutantID) {
		String op = MutantID.getMutationOperator(mutantID);

		List<MutantResult> results = this.resultMap.get(op);
		if (results != null) {
			int index = -1;
			for (MutantResult result : results) {
				if (result.getMutantID().equals(mutantID)) {
					index = results.indexOf(result);
				}
			}

			if (index != -1) {
				results.remove(index);
				this.resultMap.put(op, results);
			}
		}

		setDirty(true);
	}

	private void createTestResultForMutantOperator() {
		if (isDirty()) {
			List<String> ops = getAllMutantOperators();
			for (String operator : ops) {
				TestResultForOperator moResult = makeMutantOperatorResult(operator);

				opResultCache.put(operator, moResult);
			}
		}
		setDirty(false);
	}

	public long getActualTime() {
		return this.actualTime;
	}

	public List<TestResultForOperator> getAllMutantOperatorResults() {
		List<TestResultForOperator> list = new ArrayList<TestResultForOperator>();

		createTestResultForMutantOperator();

		List<String> ops = getAllMutantOperators();
		for (String op : ops) {
			TestResultForOperator moResult = getMutantOperatorResult(op);
			list.add(moResult);
		}
		return list;
	}

	public List<String> getAllMutantOperators() {
		return new ArrayList<String>(resultMap.keySet());
	}

	public long getBuildDate() {
		return this.startTime;
	}

	public Set<TestCase> getEffectiveTestCases() {
		return this.effectiveTestCases;
	}

	public List<TestCase> getExecutedTestCases() {
		return this.executedTestCases;
	}

	public List<String> getFilesByOperator(String op) {
		return fileMaps.get(op);
	}

	public MuJavaProject getMuJavaProject() {
		return this.muProject;
	}

	public TestResultForOperator getMutantOperatorResult(String op) {

		createTestResultForMutantOperator();

		// Because there is no cache about the mutant operator , (re)calculate a
		// mutant operator result for tabletreeviewer of ResultComposite.

		TestResultForOperator moResult = opResultCache.get(op);
		if (moResult == null) {
			moResult = makeMutantOperatorResult(op);

			opResultCache.put(op, moResult);
		}

		return moResult;
	}

	/**
	 * Never return null
	 * 
	 * @param op
	 * @return
	 */
	public List<MutantResult> getMutantResultByOperator(String op) {
		List<MutantResult> list;
		list = resultMap.get(op);
		if (list == null)
			list = new ArrayList<MutantResult>();
		return list;
	}

	public long getOriginalTimeCost() {
		return originalTime;
	}

	public List<TestCase> getOverallTestCases() {
		return this.overallTestCases;
	}

	public long getUsedDiskSpace() {
		return this.usedDiskSpace;
	}

	public long getUsedMemorySpace() {
		return usedMemorySpace;
	}

	public String getUsedReachTableName() {
		return usedReachTable;
	}

	public boolean hasMutantResultByID(String liveID) {
		int begin = liveID.indexOf("_");
		String op = liveID.substring(0, begin);
		List<MutantResult> results = getMutantResultByOperator(op);
		for (MutantResult result : results) {
			if (result.getMutantID().equals(liveID))
				return true;
		}

		return false;
	}

	private boolean isDirty() {
		return dirty;
	}

	public boolean isEffectiveTestCase(TestCase tc) {
		if (effectiveTestCases != null) {
			if (this.effectiveTestCases.contains(tc))
				return true;
		}

		return false;
	}

	public boolean isExecutedTestCase(TestCase tc) {
		if (executedTestCases != null) {
			if (this.executedTestCases.contains(tc))
				return true;
		}

		return false;
	}

	/**
	 * If op does not exist in this result table, it return false
	 * 
	 * @param op
	 * @param mutantID
	 * @return
	 */
	public boolean isKilled(String op, String mutantID) {
		List<MutantResult> list = getMutantResultByOperator(op);
		for (MutantResult result : list) {
			if (result.getMutantID().equals(mutantID)) {
				return result.isKilled();
			}
		}
		return false;
	}

	private TestResultForOperator makeMutantOperatorResult(String operator) {

		int total = 0;
		int killed = 0;
		long totalTime = 0;
		long prepareTime = 0;
		long loadTime = 0;
		long executeTime = 0;
		long compareTime = 0;
		long etcTime = 0;
		long pureExecuteTime = 0;
		long timeOutExecutionTime = 0;
		long analysisTime = 0;

		List<MutantResult> results = this.getMutantResultByOperator(operator);

		for (MutantResult result : results) {
			total++;

			// totalTime += result.getTotalTime();
			long time = result.getTotalTime();

			prepareTime += result.getPreparingTime();
			loadTime += result.getLoadingTime();
			analysisTime += result.getAnalysisTime();
			executeTime += result.getExecutingTime();
			pureExecuteTime += result.getExecutingTime(false);
			timeOutExecutionTime += result.getExecutingTime(true);
			compareTime += result.getComparingTime();
			etcTime = result.getEtcTime();

			if (result.isKilled()) {
				killed++;
			}
		}

		long originalExecutionTime = 0;
		long originalPureExecutionTime = 0;
		long originalTimeoutExecutionTime = 0;

		if (originalResult != null) {

			Set<String> set = this.resultMap.keySet();
			set.removeAll(emptyResultOperators);
			int count = set.size();
			if (count != 0) {
				originalExecutionTime = originalResult.getExecutingTime()
						/ count;
				originalPureExecutionTime = originalResult
						.getExecutingTime(false) / count;
				originalTimeoutExecutionTime = originalResult
						.getExecutingTime(true) / count;
			}
		}

		TestResultForOperator moResult = new TestResultForOperator();
		moResult.setMutantOperator(operator);
		moResult.setTotalSize(total);
		moResult.setKilledMutantSize(killed);
		moResult.setTotalTime(totalTime);
		moResult.setPreparingTime(prepareTime);
		moResult.setLoadingTime(loadTime);
		moResult.setAnalysisTime(analysisTime);
		moResult.setExecutingTime(executeTime + originalExecutionTime);
		moResult.setExecutingTime(pureExecuteTime + originalPureExecutionTime,
				false);
		moResult.setExecutingTime(timeOutExecutionTime
				+ originalTimeoutExecutionTime, true);
		moResult.setComparingTime(compareTime);
		moResult.setEtcTime(etcTime);

		return moResult;
	}

	SQLiteConnection connection = null;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (connection != null) {
			connection.dispose();
		}
	}

	@Override
	/*
	 * It has a sub-task (1 worked)
	 */
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {
		if (monitor != null) {
			monitor.subTask("Open Result Table...");
		}

		// ResultTable의 내용을 읽는다.
		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());

			// SQLiteConnection connection = new SQLiteConnection(new
			// File(getFileName()));
			// try {
			// connection.open(true);
			// } catch (SQLiteException e) {
			// e.printStackTrace();
			// }

			String str = getFileName();
			File file = new File(str);
			FileInputStream fis = new FileInputStream(file);
			super.prop.load(fis);

			fis.close();

		} else {
			MuJavaLogger.getLogger().info("[ResultTable's resource is null]");
		}

		// 정상적으로 읽어진 파일로 인해 모든 ResultTable의 값을 정리.
		resultMap.clear();
		overallTestCases.clear();
		executedTestCases.clear();
		effectiveTestCases.clear();
		emptyResultOperators.clear();
		setDirty(true);

		// 저장.
		MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				super.getValue("Project"), monitor);
		setMuJavaProject(muProject);
		setActualTime(super.getLongValue("ActualTime"));
		setOriginalTimeCost(super.getLongValue("OriginalTime"));
		setBuildTime(super.getLongValue("StartTime"));
		setUsedDiskSpace(super.getLongValue("UsedDiskSpace"));
		setUsedMemorySpace(super.getLongValue("UsedMemorySpace"));
		setUsedReachTableName(super.getValue("UsedReachTable"));
		setCMMode(super.getBooleanValue("CMMode"));

		/**
		 * TestCase의 Test-based 정보로 적절한 객체를 생성하고, Result와 TestCase와의 관계도 연결한다.
		 */
		// TestCase의 Key값과 TestCase 객체를 연결하기 위한 임시 Map
		Map<Long, TestCase> testCaseMap = new HashMap<Long, TestCase>();

		int tSize = super.getIntValue("TestCase_Size");
		for (int i = 0; i < tSize; i++) {
			StringBuffer keyBuf = new StringBuffer();
			keyBuf.append("TestCase_");
			keyBuf.append(i);
			String result = super.getValue(keyBuf.toString());

			keyBuf.setLength(0);
			keyBuf.append("TestCase_");
			keyBuf.append(i);
			keyBuf.append("_Executed");
			boolean isExecuted = super.getBooleanValue(keyBuf.toString());

			keyBuf.setLength(0);
			keyBuf.append("TestCase_");
			keyBuf.append(i);
			keyBuf.append("_ID");
			long id = super.getLongValue(keyBuf.toString());

			// text-based TestCase로부터 객체를 생성한다.
			ByteArrayInputStream sis = new ByteArrayInputStream(
					result.getBytes());
			Properties prop = new Properties();
			prop.loadFromXML(sis);
			TestCase tc = TestCase.getInstance(prop);

			// testCase 관련 기록에 저장한다.
			overallTestCases.add(tc);
			testCaseMap.put(id, tc);
			if (isExecuted) {
				executedTestCases.add(tc);
			}
		}

		// 실제 결과에 대해 읽는다.
		for (int i = 0; i < super.getIntValue("MutantOperatorSize"); i++) {
			String key = "OP_" + i;
			String op = super.getValue(key);

			emptyResultOperators.add(op);

			int size = super.getIntValue(key + "_SIZE");
			for (int j = 0; j < size; j++) {
				String resultKey = "Result_" + op + "_" + j;
				String result = getValue(resultKey);
				assert (result != null);

				ByteArrayInputStream sis = new ByteArrayInputStream(
						result.getBytes());
				Properties prop = new Properties();
				prop.load(sis);
				sis.close();

				long id = Helper.getLongValueFromProperties(prop, "TestCaseID");

				MutantResult mutantResult = new MutantResult();
				mutantResult.setProperties(prop);

				// 사용된 TestCase를 구한다.
				TestCase tc = testCaseMap.get(id);
				mutantResult.setTestCase(tc);

				MuJavaMutantInfo info = MuJavaMutantInfo.getMuJavaMutantInfo(
						muProject, mutantResult.getMutantID());

				this.addMutantResult(mutantResult, info.getTargetFile());
			}
		}

		String result = getValue("Result_ORIGINAL");
		if (!result.isEmpty()) {

			ByteArrayInputStream sis = new ByteArrayInputStream(
					result.getBytes());
			Properties prop = new Properties();
			prop.load(sis);
			sis.close();

			long id = Helper.getLongValueFromProperties(prop, "TestCaseID");

			MutantResult mutantResult = new MutantResult();
			mutantResult.setProperties(prop);

			// 사용된 TestCase를 구한다.
			TestCase tc = testCaseMap.get(id);
			mutantResult.setTestCase(tc);

			this.addOriginalResult(mutantResult);
		}

		tables.put(this.getResource(), this);

		if (monitor != null)
			monitor.worked(1);
	}

	@Override
	/*
	 * It has a sub-task (1 worked)
	 */
	public void save(IProgressMonitor monitor) throws IOException {
		if (monitor != null) {
			monitor.subTask("Save Result Table...");
		}

		if (connection != null) {

		}

		super.getProperties().clear();
		super.setValue("Project", (muProject == null) ? "" : muProject
				.getResource().getFullPath().toString());
		super.setValue("MutantOperatorSize", String.valueOf(resultMap.size()));
		super.setValue("ActualTime", Long.toString(getActualTime()));
		super.setValue("OriginalTime", Long.toString(getOriginalTimeCost()));
		super.setValue("StartTime", Long.toString(getBuildDate()));
		super.setValue("TestCase_Size",
				Integer.toString(this.overallTestCases.size()));
		super.setValue("UsedDiskSpace", Long.toString(getUsedDiskSpace()));
		super.setValue("UsedMemorySpace", Long.toString(getUsedMemorySpace()));
		super.setValue("UsedReachTable", getUsedReachTableName());
		super.setValue("CMMode", String.valueOf(cmMode));
		/**
		 * MutantResult와 사용된 TestCase가 객체로 mapping되어 있어 Text로 저장하는데 변경이 불가피하다.
		 * 따라서 저장할 때에 임시로 TestCase들에 대해 새로운 ID를 부여하고, 이를 기준으로 TestCase의 정보와,
		 * TestCase의 ID만을 가지는 MutantResult의 정보를 분리해서 저장한다.
		 */
		// 임시 ID로 정보를 저장할 Map
		HashMap<TestCase, Long> testCaseReverseMap = new HashMap<TestCase, Long>();
		// 가장 최근의 TestCase들을 정렬하기 위해 현재 시간값으로 Key를 생성.
		long tckey = System.currentTimeMillis();
		// 임시 map에 객체와 Text-based ID를 mapping한다.
		for (TestCase tc : overallTestCases) {
			testCaseReverseMap.put(tc, tckey);
			tckey++;
		}

		// 사용된 모든 TestCase의 객체들을 Text기반으로 저장한다.
		int testCaseCount = 0;
		ByteArrayOutputStream sos = new ByteArrayOutputStream();

		for (TestCase testCase : overallTestCases) {
			sos.reset();
			Properties prop = testCase.getProperties();
			prop.storeToXML(sos, "");

			// TestCase의 내용을 저장.
			StringBuffer keyBuf = new StringBuffer();
			keyBuf.append("TestCase_");
			keyBuf.append(testCaseCount);
			super.setValue(keyBuf.toString(), sos.toString());

			// Executed 여부 저장.
			keyBuf.setLength(0);
			keyBuf.append("TestCase_");
			keyBuf.append(testCaseCount);
			keyBuf.append("_Executed");
			super.setValue(keyBuf.toString(),
					Boolean.toString(executedTestCases.contains(testCase)));

			// TestCase의 ID를 저장.
			keyBuf.setLength(0);
			keyBuf.append("TestCase_");
			keyBuf.append(testCaseCount++);
			keyBuf.append("_ID");
			super.setValue(keyBuf.toString(),
					Long.toString(testCaseReverseMap.get(testCase)));
		}

		// 모든 MutantResult를 하나씩 전부 저장한다.
		int index = 0;
		for (String op : resultMap.keySet()) {
			int resultIndex = 0;
			String key = "OP_" + Integer.toString(index++);
			super.setValue(key, op);

			List<MutantResult> list = resultMap.get(op);
			int size = (list == null) ? 0 : list.size();
			super.setValue(key + "_SIZE", Integer.toString(size));

			for (MutantResult result : list) {
				TestCase tc = result.getTestCase();
				Properties prop = result.getProperties();
				long id = (tc == null) ? 0 : testCaseReverseMap.get(tc);

				sos.reset();
				prop.setProperty("TestCaseID", Long.toString(id)); // result의
				// 내용에
				// TestCaseID를
				// 추가한다.
				prop.store(sos, ""); // result의 내용을 string으로 저장한다.

				// String으로 변환된 Result는 다시금 ResultTable에 적절한 Key와 함께 저장된다.
				StringBuffer keyBuf = new StringBuffer();
				keyBuf.append("Result_");
				keyBuf.append(op);
				keyBuf.append("_");
				keyBuf.append(resultIndex++);
				super.setValue(keyBuf.toString(), sos.toString());
			}

			// p.save(monitor);
		}

		if (originalResult != null) {
			TestCase tc = originalResult.getTestCase();
			Properties prop = originalResult.getProperties();
			long id = (tc == null) ? 0 : testCaseReverseMap.get(tc);

			sos.reset();
			// result의 내용에 TestCaseID를 추가한다.
			prop.setProperty("TestCaseID", Long.toString(id));
			prop.store(sos, ""); // result의 내용을 string으로 저장한다.

			// String으로 변환된 Result는 다시금 ResultTable에 적절한 Key와 함께 저장된다.
			super.setValue("Result_ORIGINAL", sos.toString());
		}

		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			prop.store(bos, "");
			bos.flush();

			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());

			IFile file = (IFile) super.getResource();

			try {
				if (file.exists()) {
					file.setContents(bis, true, false, monitor);
				} else {
					IPath path = super.getResource().getFullPath();
					super.createParentFolder(path);
					file.create(bis, true, monitor);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			MuJavaLogger.getLogger().info("[ResultTable's resource is null ]");
		}

		if (monitor != null) {
			monitor.worked(1);
		}
	}

	public void setActualTime(long time) {
		actualTime = time;
	}

	public void setBuildTime(long l) {
		this.startTime = l;
	}

	private void setDirty(boolean flag) {
		this.dirty = flag;
	}

	public void setMuJavaProject(MuJavaProject muProject) {
		this.muProject = muProject;
	}

	public void setOriginalTimeCost(long l) {
		this.originalTime = l;
	}

	public void setUsedDiskSpace(long l) {
		this.usedDiskSpace = l;
	}

	public void setUsedMemorySpace(long l) {
		this.usedMemorySpace = l;
	}

	public void setUsedReachTableName(String name) {
		if (name != null) {
			this.usedReachTable = name;
		}
	}

	public void setUsedTestCase(Collection<TestCase> testCases) {
		this.overallTestCases.clear();
		this.overallTestCases.addAll(testCases);
	}

	public void setMutationOperators(List<String> operators) {
		this.emptyResultOperators.addAll(operators);
	}

	public void setCMMode(boolean cmMode) {
		this.cmMode = cmMode;
	}

	public boolean isCMMode() {
		return this.cmMode;
	}
}

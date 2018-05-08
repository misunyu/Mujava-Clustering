package mujava;

import java.util.Properties;

import kaist.selab.util.XMLValidator;
import mujava.inf.ITimeCostResult;
import mujava.plugin.editor.mutantreport.TestCase;

public class MutantResult implements ITimeCostResult {
	public static final int COLUMN_ID = 0;

	public static final int COLUMN_TESTCASE = 1;

	public static final int COLUMN_ORIGINALRESULT = 2;

	public static final int COLUMN_MUTANTRESULT = 3;

	public static final int COLUMN_ETC = 4;

	public static final int COLUMN_ANALYSIS = 5;

	private static final int KILLED = 1;

	private static final int NOTKILLED = 2;

	private static final int RESET = 0;

	private Properties prop = new Properties();

	private int type = 0;

	private TestCase testCase;

	private boolean isEmpty = true;

	public void addMutantResult(MutantResult r) {
		if (this.getMutantID().equals(r.getMutantID())) {
			this.setComparingTime(this.getComparingTime()
					+ r.getComparingTime());
			this.setExecutingTime(this.getExecutingTime()
					+ r.getExecutingTime());
			this.setExecutingTime(this.getExecutingTime(true)
					+ r.getExecutingTime(true), true);
			this.setExecutingTime(this.getExecutingTime(false)
					+ r.getExecutingTime(false), false);
			this.setLoadingTime(this.getLoadingTime() + r.getLoadingTime());
			this.setPreparingTime(this.getPreparingTime()
					+ r.getPreparingTime());
			this.setAnalysisTime(this.getAnalysisTime() + r.getAnalysisTime());
			this.setTotalTime(this.getTotalTime() + r.getTotalTime());
		}
		if (!this.isKilled()) {
			this.setTestCase(r.getTestCase());
			this.setMutantResult(r.getMutantResult());
			this.setOriginalResult(r.getOriginalResult());
		}
	}

	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public long getAnalysisTime() {
		String val = this.prop.getProperty("AnalysisTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	public long getComparingTime() {
		String val = this.prop.getProperty("ComparingTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	public long getEtcTime() {
		return getTotalTime()
				- (getPreparingTime() + getLoadingTime() + getExecutingTime() + getComparingTime());
	}

	public long getExecutingTime() {
		String val = this.prop.getProperty("ExecutingTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	public long getExecutingTime(boolean timeOut) {
		String key = (timeOut) ? "ExecutionTimeTimeOut"
				: "ExecutionTimeNoTimeOut";
		String value = this.prop.getProperty(key);
		if (value != null)
			return Long.parseLong(value);
		return 0;
	}

	public long getLoadingTime() {
		String val = this.prop.getProperty("LoadingTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	public String getMutantID() {
		return getValue("MutantID");
	}

	public String getMutantOperator() {
		return getValue("MutantOperator");
	}

	public String getMutantResult() {
		return getValue("MutantResult");
	}

	public String getOriginalResult() {
		return getValue("OriginalResult");
	}

	public String getOriginalState() {
		String val = this.prop.getProperty("OriginalState");
		return (val == null) ? "" : val;
	}

	public long getPreparingTime() {
		String val = this.prop.getProperty("PreparingTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	public Properties getProperties() {
		return this.prop;
	}

	public TestCase getTestCase() {
		return this.testCase;
	}

	public long getTotalTime() {
		String val = this.prop.getProperty("TotalTime");
		if (val != null)
			return Long.parseLong(val);

		return 0;
	}

	private String getValue(String key) {
		String val = this.prop.getProperty(key);
		if (val == null)
			return new String();
		return val;
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

	// 반드시 getOriginalResult가 null이 아니라는 가정
	public boolean isKilled() {
		if (type != 0) {
			if (type == KILLED)
				return true;
			else
				return false;
		}

		String oResult = getOriginalResult();
		String mResult = getMutantResult();

		if (oResult.equals(mResult)) {
			type = NOTKILLED;
			return false;
		}

		type = KILLED;
		return true;
	}

	public void setAnalysisTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("AnalysisTime", Long.toString(time));
	}

	public void setComparingTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("ComparingTime", Long.toString(time));
	}

	public void setExecutingTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("ExecutingTime", Long.toString(time));
	}

	/**
	 * 
	 * @param executionTime
	 * @param b
	 *            is true if the given time is execution time which have time
	 *            out conditions
	 */
	public void setExecutingTime(long executionTime, boolean timeOut) {
		if (executionTime > 0) {
			isEmpty = false;
		}

		String key = (timeOut) ? "ExecutionTimeTimeOut"
				: "ExecutionTimeNoTimeOut";
		this.prop.setProperty(key, Long.toString(executionTime));
	}

	public void setLoadingTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("LoadingTime", Long.toString(time));
	}

	public void setMutantID(String mutantID) {
		this.prop.setProperty("MutantID", mutantID);
	}

	public void setMutantOperator(String op) {
		this.prop.setProperty("MutantOperator", op);
	}

	public void setMutantResult(Object result) {
		String str = result.toString();
		str = XMLValidator.makeValidString(str);
		this.prop.setProperty("MutantResult", str);
		this.type = RESET;
	}

	public void setOriginalResult(Object original) {
		String str = (original == null) ? "" : original.toString();
		str = XMLValidator.makeValidString(str);

		this.prop.setProperty("OriginalResult", str);
		type = RESET;
	}

	public void setOriginalState(String originalStateString) {
		this.prop.setProperty("OriginalState", originalStateString);
	}

	public void setPreparingTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("PreparingTime", Long.toString(time));
	}

	public void setProperties(Properties prop) {
		this.prop = prop;

		this.isEmpty = true;
		if (getAnalysisTime() > 0 || getComparingTime() > 0 || getEtcTime() > 0
				|| getExecutingTime() > 0 || getExecutingTime(true) > 0
				|| getExecutingTime(false) > 0 || getLoadingTime() > 0
				|| getPreparingTime() > 0 || getTotalTime() > 0) {
			isEmpty = false;
		}
	}

	public void setTestCase(TestCase testcase) {
		this.testCase = testcase;
	}

	public void setTotalTime(long time) {
		if (time > 0) {
			isEmpty = false;
		}
		this.prop.setProperty("TotalTime", Long.toString(time));
	}

	public boolean isEmptyResult() {
		return isEmpty;
	}

	@Override
	public String toString() {
		return this.getMutantID();
	}
}

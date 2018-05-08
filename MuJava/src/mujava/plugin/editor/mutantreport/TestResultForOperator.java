package mujava.plugin.editor.mutantreport;

import mujava.inf.ITimeCostResult;

public class TestResultForOperator implements ITimeCostResult, Comparable {

	private String opName;
	private int total = 0;
	private int killed = 0;
	private long prepareTime;
	private long totalTime;
	private long compareTime;
	private long executeTime;
	private long loadTime;
	private long etcTime;
	private long pureExecutionTime;
	private long timeOutExecutingTime;
	private long analysisTime;

	public long getAnalysisTime() {
		return analysisTime;
	}

	public long getComparingTime() {
		return compareTime;
	}

	public long getEtcTime() {
		// return totalTime - (prepareTime + loadTime + executeTime +
		// compareTime);
		return this.etcTime;
	}

	public long getExecutingTime() {
		return executeTime;
	}

	@Override
	public long getExecutingTime(boolean timeOut) {
		if (timeOut)
			return timeOutExecutingTime;

		return pureExecutionTime;
	}

	public int getKilledMutantSize() {
		return this.killed;
	}

	public double getKilledScore() {
		return 1.0 * killed / total;
	}

	public String getKilledScoreString() {
		return new String(killed + "/" + total);
	}

	public double getLiveScore() {
		return 1.0 * (total - killed) / total;
	}

	public String getLiveScoreString() {
		return new String((total - killed) + "/" + total);
	}

	public long getLoadingTime() {
		return loadTime;
	}

	public String getMutantOperator() {
		return opName;
	}

	public long getPreparingTime() {
		return prepareTime;
	}

	public int getTotalSize() {
		return total;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setAnalysisTime(long time) {
		this.analysisTime = time;
	}

	public void setComparingTime(long l) {
		this.compareTime = l;
	}

	public void setEtcTime(long etcTime) {
		this.etcTime = etcTime;
	}

	public void setExecutingTime(long l) {
		this.executeTime = l;
	}

	@Override
	public void setExecutingTime(long l, boolean timeOut) {
		if (timeOut)
			timeOutExecutingTime = l;
		else
			pureExecutionTime = l;
	}

	public void setKilledMutantSize(int killed) {
		assert (killed <= total);
		this.killed = killed;
	}

	public void setLoadingTime(long l) {
		this.loadTime = l;
	}

	public void setMutantOperator(String op) {
		this.opName = op;
	}

	public void setPreparingTime(long l) {
		this.prepareTime = l;
	}

	public void setTotalSize(int total) {
		assert (total >= killed);
		this.total = total;
	}

	public void setTotalTime(long l) {
		this.totalTime = l;
	}

	@Override
	public int compareTo(Object o) {

		if (o instanceof TestResultForOperator) {
			TestResultForOperator target = (TestResultForOperator) o;
			return this.getMutantOperator().compareTo(
					target.getMutantOperator());
		}

		return -1;
	}
}
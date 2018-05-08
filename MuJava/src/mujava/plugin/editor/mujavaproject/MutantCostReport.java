package mujava.plugin.editor.mujavaproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import kaist.selab.util.Helper;
import mujava.gen.PerformanceElement;

public class MutantCostReport extends Properties {
	private static MutantCostReport result;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876036426110211162L;

	public static MutantCostReport getCurrentResult() {
		return result;
	}

	public synchronized static MutantCostReport loadXML(InputStream in)
			throws IOException, InvalidPropertiesFormatException {

		MutantCostReport result = new MutantCostReport(false);
		result.clear();
		result.subResults.clear();

		result.loadFromXML(in);

		result.actualTotalCost = new PerformanceElement();
		result.calculatedCost = new PerformanceElement();
		result.analysisCost = new PerformanceElement();

		result.startTime = Helper.getLongValueFromProperties(result,
				"BuildTime");
		String str = Helper.getValueFromProperties(result, "ActualCost");
		if (str != "")
			result.actualTotalCost.fromString(str);

		str = Helper.getValueFromProperties(result, "AnalysisCost");
		if (str != "")
			result.analysisCost.fromString(str);
		str = Helper.getValueFromProperties(result, "PreparingCost");
		if (str != "")
			result.preparingCost.fromString(str);

		// str = Helper.getValueFromProperties(result, "CalculatedCost");
		// if (str != "")
		// result.calculatedCost.fromString(str);

		int count = Helper.getIntValueFromProperties(result, "ResultSize");
		for (int i = 0; i < count; i++) {
			String content = result.getProperty("Result_" + i);
			if (content != null) {
				CostReportForOperator gResult = CostReportForOperator
						.fromString(content);
				result.subResults.add(gResult);
			}
		}

		result.recalculate();

		// result.mutants.clear();
		// count = Helper.getIntValueFromProperties(result, "MutantSize");
		// for (int i = 0; i < count; i++) {
		// String content = result.getProperty("Mutant_" + i);
		// if (content != null) {
		// result.mutants.add(content);
		// }
		// }
		//		
		// result.targets.clear();
		// count = Helper.getIntValueFromProperties(result, "TargetSize");
		// for (int i = 0; i < count; i++) {
		// String content = result.getProperty("Target_" + i);
		// if (content != null) {
		// result.targets.add(content);
		// }
		// }

		return result;
	}

	/**
	 * For UI
	 * 
	 * @param gResults
	 * @return
	 */
	public static MutantCostReport makeCollectedGenerationResult(
			List<MutantCostReport> gResults) {
		MutantCostReport result = new MutantCostReport(true);
		result.actualTotalCost.reset();
		result.analysisCost.reset();
		result.calculatedCost.reset();

		for (MutantCostReport gResult : gResults) {
			result.actualTotalCost.addTo(gResult.actualTotalCost);
			result.analysisCost.addTo(gResult.analysisCost);
			result.calculatedCost.addTo(gResult.calculatedCost);
			Helper.unionStringList(result.targets, gResult.targets);
			Helper.unionStringList(result.mutants, gResult.mutants);

			/**
			 * Ignore the time to execute the same mutant in a different
			 * MutantCostResult, So cumulate all sub cost information
			 */
			for (CostReportForOperator mResult : gResult.subResults) {
				result.cumulateSubResult(mResult);
			}
		}

		return result;
	}

	/**
	 * It generates a GenerationResult object with new unique id
	 * 
	 * @return
	 */
	public static MutantCostReport newGenerationResult() {
		MutantCostReport result = new MutantCostReport(false);
		result.setStartTime(System.currentTimeMillis());

		MutantCostReport.setCurrentResult(result);
		return result;
	}

	public static void setCurrentResult(MutantCostReport _result) {
		result = _result;
	}

	private PerformanceElement actualTotalCost;

	private PerformanceElement analysisCost = null;

	private long startTime;

	private PerformanceElement calculatedCost;

	private boolean isCollected;

	private List<String> mutants = new ArrayList<String>();

	private PerformanceElement preparingCost;

	private long secondBuildTime;

	private List<CostReportForOperator> subResults = new ArrayList<CostReportForOperator>();

	private List<String> targets = new ArrayList<String>();

	/**
	 * isIncluded is true only if this constructor is called content provider
	 * for UI
	 * 
	 * @param isIncluded
	 */
	private MutantCostReport(boolean isIncluded) {
		this.isCollected = isIncluded;
		this.actualTotalCost = new PerformanceElement();
		this.analysisCost = new PerformanceElement();
		this.calculatedCost = new PerformanceElement();
		this.preparingCost = new PerformanceElement();
	}

	public void addAnalysisCost(PerformanceElement cost) {
		if (this.analysisCost == null)
			this.analysisCost = cost;
		else {
			this.analysisCost.addTo(cost);
		}
	}

	/**
	 * For UI
	 * 
	 * @param result
	 */
	public void cumulateSubResult(CostReportForOperator result) {
		if (subResults.contains(result)) {
			int index = subResults.indexOf(result);
			CostReportForOperator mResult = subResults.get(index);
			mResult.merge(result);
			subResults.set(index, mResult);
		} else {
			subResults.add(result.copy());
		}
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof MutantCostReport))
			return false;

		MutantCostReport thisObj = (MutantCostReport) obj;
		if (thisObj.startTime == this.startTime)
			return true;

		return false;
	}

	public PerformanceElement getActualCost() {
		return this.actualTotalCost;
	}

	public PerformanceElement getAnalysisCost() {
		return this.analysisCost;
	}

	public long getBuildTime() {
		return startTime;
	}

	public PerformanceElement getCalculatedCost() {
		return this.calculatedCost;
	}

	public long getSecondBuildTime() {
		return secondBuildTime;
	}

	int getSizeOfMutants() {
		return mutants.size();
	}

	public int getSizeOfTargets() {
		return this.targets.size();
	}

	public List<CostReportForOperator> getSubResults() {
		return this.subResults;
	}

	public boolean isCollected() {
		return isCollected;
	}

	/**
	 * Check whether this result is valid, that is, there is at least one
	 * mutant.
	 * 
	 * @return
	 */
	public boolean isValid() {
		int size = getSizeOfMutants();

		return (size > 0) ? true : false;
	}

	/**
	 * called only when a GenerationResult is created and added to the
	 * generation result table in the wizard
	 */
	public void recalculate() {
		long time = 0;
		long disk = 0;
		long mem = 0;
		mutants.clear();
		targets.clear();

		for (CostReportForOperator gResult : subResults) {
			time += gResult.getTime();
			disk += gResult.getDiskCost();
			mem += gResult.getMemoryCost();
			Helper.unionStringList(this.mutants, gResult.getMutants());
			Helper.unionStringList(this.targets, gResult.getTargets());
		}

		PerformanceElement element = new PerformanceElement();
		element.setNanoTime(time);
		element.setDiskCost(disk);
		element.setMemoryCost(mem);
		this.calculatedCost = element;
	}

	public void setActualCost(PerformanceElement total) {
		this.actualTotalCost = total;
	}

	void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setPreparingCost(PerformanceElement preparingCost) {
		this.preparingCost = preparingCost;
	}

	public void setSecondBuildTime(long time) {
		this.secondBuildTime = time;
	}

	@Override
	public synchronized void storeToXML(OutputStream os, String comment)
			throws IOException {
		super.clear();

		super.setProperty("BuildTime", Long.toString(startTime));
		super.setProperty("ActualCost", actualTotalCost.toString());
		// super.setProperty("CalculatedCost", calculatedCost.toString());
		super.setProperty("PreparingCost", preparingCost.toString());
		super.setProperty("AnalysisCost", analysisCost.toString());

		super.setProperty("ResultSize", Integer.toString(subResults.size()));
		int count = 0;
		for (CostReportForOperator gResult : subResults) {
			super.setProperty("Result_" + count, gResult.getString());
			count++;
		}
		// super.setProperty("TargetSize", Integer.toString(targets.size()));
		// count = 0;
		// for (String target : targets) {
		// super.setProperty("Target_" + count, target);
		// count++;
		// }
		// super.setProperty("MutantSize", Integer.toString(mutants.size()));
		// count = 0;
		// for (String mutantID : mutants) {
		// super.setProperty("Mutant_" + count, mutantID);
		// count++;
		// }

		super.storeToXML(os, comment);
	}

	public PerformanceElement getPreparingCost() {
		return this.preparingCost;
	}

}

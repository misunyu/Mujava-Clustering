package mujava.plugin.editor.mujavaproject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import kaist.selab.util.Helper;
import mujava.gen.PerformanceElement;

public class CostReportForOperator {

	protected CostReportForOperator copy() {
		CostReportForOperator result = new CostReportForOperator(
				this.mutantOperator);
		result.isIncluded = this.isIncluded;
		result.cost = new PerformanceElement(this.cost);
		result.anaysisCost = new PerformanceElement(this.anaysisCost);
		result.mutants = new ArrayList<String>(this.mutants);
		result.targets = new ArrayList<String>(this.targets);
		return result;
	}

	public static CostReportForOperator fromString(String from) {
		CostReportForOperator result = new CostReportForOperator("", false);

		ByteArrayInputStream bis = new ByteArrayInputStream(from.getBytes());
		Properties prop = new Properties();
		try {
			prop.loadFromXML(bis);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result.mutantOperator = Helper.getValueFromProperties(prop,
				"MutantOperator");
		result.cost
				.setNanoTime(Helper.getLongValueFromProperties(prop, "Time"));
		result.cost
				.setDiskCost(Helper.getLongValueFromProperties(prop, "Disk"));
		result.cost.setMemoryCost(Helper.getLongValueFromProperties(prop,
				"Memory"));
		result.anaysisCost.setNanoTime(Helper.getLongValueFromProperties(prop,
				"MPGenTime"));
		result.anaysisCost.setDiskCost(Helper.getLongValueFromProperties(prop,
				"MPGenDisk"));
		result.anaysisCost.setMemoryCost(Helper.getLongValueFromProperties(
				prop, "MPGenMemory"));

		int mSize = Helper.getIntValueFromProperties(prop, "Mutants");
		int tSize = Helper.getIntValueFromProperties(prop, "Targets");

		for (int i = 0; i < mSize; i++) {
			String mutantID = Helper
					.getValueFromProperties(prop, "Mutant_" + i);
			result.addMutant(mutantID);
		}
		for (int i = 0; i < tSize; i++) {
			String target = Helper.getValueFromProperties(prop, "Target_" + i);
			result.addTarget(target);
		}

		return result;
	}

	/**
	 * For UI
	 * 
	 * @param list
	 * @return
	 */
	public static CostReportForOperator makeIncludedResult(
			List<CostReportForOperator> list) {
		CostReportForOperator result = new CostReportForOperator("", true);
		result.cost.reset();
		result.anaysisCost.reset();

		for (CostReportForOperator gResult : list) {
			result.merge(gResult);
		}

		return result;
	}

	private String mutantOperator;

	private PerformanceElement cost;

	private PerformanceElement anaysisCost;

	private boolean isIncluded;

	private List<String> targets = new ArrayList<String>();

	private List<String> mutants = new ArrayList<String>();

	public CostReportForOperator(String mutantOperator, boolean isIncluded) {
		this.mutantOperator = mutantOperator;
		this.isIncluded = isIncluded;
		cost = new PerformanceElement();
		anaysisCost = new PerformanceElement();
	}

	public CostReportForOperator(String operatorName) {
		this(operatorName, false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof CostReportForOperator))
			return false;

		if (this.mutantOperator
				.equals(((CostReportForOperator) obj).mutantOperator)) {
			return true;
		}
		return false;
	}

	public long getDiskCost() {
		return cost.getDiskCost();
	}

	public long getMemoryCost() {
		return cost.getMemoryCost();
	}

	public String getMutantOperator() {
		return mutantOperator;
	}

	public PerformanceElement getMutantPointGenerationCost() {
		return anaysisCost;
	}

	/**
	 * Only called by UI class.
	 * 
	 * @return
	 */
	public int getSizeOfMutants() {
		return mutants.size();
	}

	public int getSizeOfTargets() {
		return targets.size();
	}

	public long getTime() {
		return cost.getTimeCost();
	}

	/**
	 * Merge both cost and mutantPointGenerationCost and union both string
	 * lists, mutants and targets
	 * 
	 * @param result
	 */
	public void merge(CostReportForOperator result) {
		this.cost.addTo(result.cost);
		this.anaysisCost.addTo(result.anaysisCost);
		Helper.unionStringList(this.mutants, result.mutants);
		Helper.unionStringList(this.targets, result.targets);
	}

	public void setCost(PerformanceElement lastCost) {
		this.cost = lastCost;
	}

	public void setAnalysisCost(PerformanceElement cost) {
		this.anaysisCost = cost;
	}

	public String getString() {
		Properties prop = new Properties();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		prop.setProperty("MutantOperator", mutantOperator);
		prop.setProperty("Time", Long.toString(cost.getTimeCost()));
		prop.setProperty("Disk", Long.toString(cost.getDiskCost()));
		prop.setProperty("Memory", Long.toString(cost.getMemoryCost()));
		prop.setProperty("MPGenTime", Long.toString(anaysisCost.getTimeCost()));
		prop.setProperty("MPGenDisk", Long.toString(anaysisCost.getDiskCost()));
		prop.setProperty("MPGenMemory", Long.toString(anaysisCost
				.getMemoryCost()));
		prop.setProperty("Targets", Integer.toString(targets.size()));
		prop.setProperty("Mutants", Integer.toString(mutants.size()));

		for (int i = 0; i < mutants.size(); i++) {
			prop.setProperty("Mutant_" + i, mutants.get(i));
		}
		for (int i = 0; i < targets.size(); i++) {
			prop.setProperty("Target_" + i, targets.get(i));
		}

		try {
			prop.storeToXML(bos, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toString();
	}

	public void setMutantOperator(String str) {
		this.mutantOperator = str;
	}

	public boolean isIncluded() {
		return isIncluded;
	}

	public List<String> getTargets() {
		return targets;
	}

	public List<String> getMutants() {
		return mutants;
	}

	public void addTarget(String target) {
		if (!targets.contains(target))
			this.targets.add(target);
	}

	public void addMutant(String mutantID) {
		if (!mutants.contains(mutantID))
			this.mutants.add(mutantID);
	}
}

package mujava.plugin.editor.mujavaproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.MutantOperator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class OperatorCostReportContentProvider implements ITreeContentProvider {
	List<CostReportForOperator> tList = new ArrayList<CostReportForOperator>();

	List<CostReportForOperator> cList = new ArrayList<CostReportForOperator>();

	CostReportForOperator tResult;

	CostReportForOperator cResult;

	Comparator<CostReportForOperator> comparator = new Comparator<CostReportForOperator>() {
		@Override
		public int compare(CostReportForOperator o1, CostReportForOperator o2) {
			String str1 = o1.getMutantOperator();
			String str2 = o2.getMutantOperator();
			return str1.compareTo(str2);
		}
	};

	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		tList.clear();
		cList.clear();

		MutantCostReport result = (MutantCostReport) newInput;
		if (result != null) {
			List<CostReportForOperator> list = result.getSubResults();

			for (CostReportForOperator mResult : list) {
				boolean flag = false;
				for (String op : MutantOperator.getAllTraditionalOperators()) {
					if (op.equals(mResult.getMutantOperator())) {
						tList.add(mResult);
						flag = true;
						break;
					}
				}

				if (!flag)
					for (String op : MutantOperator.getAllClassicalOperators()) {
						if (op.equals(mResult.getMutantOperator())) {
							cList.add(mResult);
							flag = true;
							break;
						}
					}

				if (!flag)
					MuJavaLogger.getLogger().error("[Unknown error ..]");
			}
			Collections.sort(tList, comparator);
			Collections.sort(cList, comparator);

			tResult = CostReportForOperator.makeIncludedResult(tList);
			tResult.setMutantOperator("Traditional");

			cResult = CostReportForOperator.makeIncludedResult(cList);
			cResult.setMutantOperator("Classical");
		}
	}

	public Object[] getChildren(Object parentElement) {
		assert (parentElement instanceof CostReportForOperator);
		CostReportForOperator result = (CostReportForOperator) parentElement;
		if (result.isIncluded()) {
			if (result == tResult) {
				return tList.toArray(new Object[tList.size()]);
			}
			if (result == cResult) {
				return cList.toArray(new Object[cList.size()]);
			}
		}

		return null;
	}

	public Object getParent(Object element) {
		assert (element instanceof CostReportForOperator);
		CostReportForOperator result = (CostReportForOperator) element;
		if (!result.isIncluded()) {
			String op = result.getMutantOperator();
			MutantOperator mop = MutantOperator.getMutantOperator(op);
			if (mop.isTraditional())
				return tResult;
			if (mop.isClassical())
				return cResult;
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		assert (element instanceof CostReportForOperator);
		CostReportForOperator result = (CostReportForOperator) element;

		if (result.isIncluded() && result.getSizeOfMutants() > 0)
			return true;

		return false;
	}

	public Object[] getElements(Object inputElement) {

		if (tResult.getSizeOfMutants() > 0 && cResult.getSizeOfMutants() > 0)
			return new Object[] { tResult, cResult };

		if (tResult.getSizeOfMutants() > 0)
			return new Object[] { tResult };

		if (cResult.getSizeOfMutants() > 0)
			return new Object[] { cResult };

		return new Object[] {};
	}

}

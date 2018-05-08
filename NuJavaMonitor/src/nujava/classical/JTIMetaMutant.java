package nujava.classical;

import nujava.MutantMonitor;

import com.thoughtworks.xstream.XStream;

public class JTIMetaMutant {

	/**
	 * Use Node에 대해서는 본 함수를 사용가능하다.
	 * 
	 * @param <E>
	 * @param parameter
	 * @param instance
	 * @param mutantID
	 * @return
	 */
	public static <E> E JTIGenUse(E parameter, E instance, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			// int sID = MutantMonitor.getInstance().getSubID();
			return instance;
		}

		XStream xs = MutantMonitor.getInstance().getXstream();
		String ori = xs.toXML(parameter);
		String mut = xs.toXML(instance);
		if (!ori.equals(mut)) {
			// comparator.reportMutant(mutantID, ori, 0, mut);
		}

		return parameter;
	}

	public static <E> E JTIGenDef(E parameter, E instance, E rightExp,
			String mutantID) {

		XStream xs = MutantMonitor.getInstance().getXstream();

		String ori = xs.toXML(parameter);
		String mut = xs.toXML(instance);
		String right = xs.toXML(rightExp);
		String originalResult = right + ":" + mut;
		String mutantResult = ori + ":" + right;

		if (!originalResult.equals(mutantResult)) {
			// comparator.reportMutant(mutantID, originalResult, 0,
			// mutantResult);
		}
		return parameter;
	}
}

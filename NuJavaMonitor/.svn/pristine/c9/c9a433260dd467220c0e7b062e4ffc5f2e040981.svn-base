package nujava.traditional;

import nujava.MutantMonitor;

public class LOIDMetaMutant {

	public static boolean LOIDGen(boolean exp, int op, int changePoint,
			String mutantID) {

		if (exp != !exp) {
			String mID = mutantID + "_" + op;
			MutantMonitor.getInstance().updateSingleMutant(mID, changePoint);
		}

		return exp;
	}
}

package nujava.traditional;

import nujava.MutantMonitor;

public class COIDMetaMutant {

	public static boolean COIDGen(boolean exp, int op, int changePoint,
			String mutantID) {

		if (exp != !exp) {
			String mID = mutantID + "_" + op;
			MutantMonitor.getInstance().updateSingleMutant(mID, changePoint);
		}

		return exp;
	}
}

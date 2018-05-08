package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class BOIDMetaMutant {

	public static int BOIDGen(int exp, int op, int changePoint, String mutantID) {

		if (exp != ~exp) {
			String mID = mutantID + "_" + MSGConstraints.U_BIT_NOT;
			MutantMonitor.getInstance().updateSingleMutant(mID, changePoint);
		}
		return exp;
	}

	public static long BOIDGen(long exp, int op, int changePoint,
			String mutantID) {

		if (exp != ~exp) {
			String mID = mutantID + "_" + MSGConstraints.U_BIT_NOT;
			MutantMonitor.getInstance().updateSingleMutant(mID, changePoint);
		}

		return exp;
	}
}

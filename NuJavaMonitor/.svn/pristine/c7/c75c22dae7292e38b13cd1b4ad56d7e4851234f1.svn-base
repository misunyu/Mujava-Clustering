package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class AORSMetaMutant {

	/**
	 * Final 값을 계산하는 함수
	 * 
	 * @param original
	 * @param op
	 * @return
	 */
	public static byte AORS(byte original, int op) {
		// MSG나 WtS 모드는 주어진 ID에 해당하는 값을 생성한다.
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1) {
				op = sID;
			}
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static char AORS(char original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static double AORS(double original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static float AORS(float original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static int AORS(int original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static long AORS(long original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static short AORS(short original, int op) {
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				op = sID;
		}

		return MSG.traditional.AORSMetaMutant.AORSValue(original, op);
	}

	public static byte AORSGen(byte originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		byte originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static char AORSGen(char originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* boolean isForStmts */) {

		char originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static double AORSGen(double originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		double originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		// double mutantFinal = 0;
		// double mutantIntermediate = 0;

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static float AORSGen(float originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		float originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		// float mutantFinal = 0;
		// float mutantIntermediate = 0;

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static int AORSGen(int originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		int originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		// int mutantFinal = 0;
		// int mutantIntermediate = 0;

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static long AORSGen(long originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		long originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		// long mutantFinal = 0;
		// long mutantIntermediate = 0;

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	public static short AORSGen(short originalFinal, int op, int changePoint,
			String mutantID, boolean isStatement
	/* , boolean isForStmts */) {

		short originalIntermediate = MSG.traditional.AORSMetaMutant.AORS(
				originalFinal, op, mutantID);

		report(op, mutantID, changePoint, isStatement);

		return originalIntermediate;
	}

	private static void report(int op, String mutantID, int changePoint,
			boolean isStatement) {
		MutantMonitor monitor = MutantMonitor.getInstance();
		if (MSGConstraints.U_PRE_INCREMENT == op) {

			String mID = mutantID + "_" + MSGConstraints.U_POST_DECREMENT;
			monitor.updateNonSingleMutant(mID, changePoint,
					MSGConstraints.U_POST_DECREMENT);

			if (!isStatement) {
				mID = mutantID + "_" + MSGConstraints.U_PRE_DECREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_DECREMENT);
				mID = mutantID + "_" + MSGConstraints.U_POST_INCREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_POST_INCREMENT);
			}
		} else if (MSGConstraints.U_PRE_DECREMENT == op) {
			String mID = mutantID + "_" + MSGConstraints.U_POST_INCREMENT;
			monitor.updateNonSingleMutant(mID, changePoint,
					MSGConstraints.U_POST_INCREMENT);

			if (!isStatement) {
				mID = mutantID + "_" + MSGConstraints.U_PRE_INCREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_INCREMENT);
				mID = mutantID + "_" + MSGConstraints.U_POST_DECREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_POST_DECREMENT);
			}
		} else if (MSGConstraints.U_POST_INCREMENT == op) {

			String mID = mutantID + "_" + MSGConstraints.U_POST_DECREMENT;
			monitor.updateNonSingleMutant(mID, changePoint,
					MSGConstraints.U_POST_DECREMENT);

			if (!isStatement) {
				mID = mutantID + "_" + MSGConstraints.U_PRE_INCREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_INCREMENT);
				mID = mutantID + "_" + MSGConstraints.U_PRE_DECREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_DECREMENT);
			}
		} else if (MSGConstraints.U_POST_DECREMENT == op) {
			String mID = mutantID + "_" + MSGConstraints.U_POST_INCREMENT;
			monitor.updateNonSingleMutant(mID, changePoint,
					MSGConstraints.U_POST_INCREMENT);
			if (!isStatement) {
				mID = mutantID + "_" + MSGConstraints.U_PRE_INCREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_INCREMENT);
				mID = mutantID + "_" + MSGConstraints.U_PRE_DECREMENT;
				monitor.updateNonSingleMutant(mID, changePoint,
						MSGConstraints.U_PRE_DECREMENT);
			}
		}
	}
}

package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class BORMetaMutant {

	public static int BORGen(int left, int right, int op, int changePoint,
			String id) {

		int original = MSG.traditional.BORMetaMutant.BOR(left, right, op, id);

		long time = System.nanoTime();
		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			int mutant = left & right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			int mutant = left | right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {

			int type = MSGConstraints.B_XOR;

			int mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}

		String oriStr = String.valueOf(original);
		if (op != MSGConstraints.B_SHIFT_L) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_L;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_L, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_R) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = id + "_" + MSGConstraints.B_SHIFT_R;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_R, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_RR) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_RR;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_RR, time, mutStr);
			}
		}
		
		return original;
	}

	public static int BORGen(byte left, byte right, int op, int changePoint,
			String id) {
		int original = MSG.traditional.BORMetaMutant.BOR(left, right, op, id);

		long time = System.nanoTime();

		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			int mutant = left & right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			int mutant = left | right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {

			int type = MSGConstraints.B_XOR;

			int mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		String oriStr = String.valueOf(original);
		if (op != MSGConstraints.B_SHIFT_L) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_L;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_L, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_R) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = id + "_" + MSGConstraints.B_SHIFT_R;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_R, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_RR) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_RR;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_RR, time, mutStr);
			}
		}

		return original;
	}

	public static int BORGen(short left, short right, int op, int changePoint,
			String id) {

		int original = MSG.traditional.BORMetaMutant.BOR(left, right, op, id);

		long time = System.nanoTime();

		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			int mutant = left & right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			int mutant = left | right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {

			int type = MSGConstraints.B_XOR;

			int mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		String oriStr = String.valueOf(original);
		if (op != MSGConstraints.B_SHIFT_L) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_L;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_L, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_R) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = id + "_" + MSGConstraints.B_SHIFT_R;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_R, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_RR) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_RR;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_RR, time, mutStr);
			}
		}

		return original;
	}

	public static int BORGen(char left, char right, int op, int changePoint,
			String id) {

		int original = MSG.traditional.BORMetaMutant.BOR(left, right, op, id);

		long time = System.nanoTime();

		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			int mutant = left & right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			int mutant = left | right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {
			int type = MSGConstraints.B_XOR;

			int mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Integer.toString(mutant));
			}
		}
		String oriStr = String.valueOf(original);
		if (op != MSGConstraints.B_SHIFT_L) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_L;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_L, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_R) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = id + "_" + MSGConstraints.B_SHIFT_R;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_R, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_RR) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_RR;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_RR, time, mutStr);
			}
		}

		return original;
	}

	public static long BORGen(long left, long right, int op, int changePoint,
			String id) {

		long original = MSG.traditional.BORMetaMutant.BOR(left, right, op, id);

		long time = System.nanoTime();

		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			long mutant = left & right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Long.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			long mutant = left | right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Long.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {

			int type = MSGConstraints.B_XOR;

			long mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Long.toString(mutant));
			}
		}
		String oriStr = String.valueOf(original);
		if (op != MSGConstraints.B_SHIFT_L) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_L;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_L, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_R) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = id + "_" + MSGConstraints.B_SHIFT_R;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_R, time, mutStr);
			}
		}
		if (op != MSGConstraints.B_SHIFT_RR) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = id + "_" + MSGConstraints.B_SHIFT_RR;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_SHIFT_RR, time, mutStr);
			}
		}

		return original;
	}

	public static boolean BORGen(boolean left, boolean right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.BORMetaMutant.BOR(left, right, op,
				id);

		long time = System.nanoTime();

		if (op != MSGConstraints.B_BITAND) {

			int type = MSGConstraints.B_BITAND;

			boolean mutant = left & right;
			if (original != mutant) {

				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}

		if (op != MSGConstraints.B_BITOR) {

			int type = MSGConstraints.B_BITOR;

			boolean mutant = left | right;
			if (original != mutant) {

				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}
		if (op != MSGConstraints.B_XOR) {

			int type = MSGConstraints.B_XOR;

			boolean mutant = left ^ right;
			if (original != mutant) {
				String mID = id + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}


		return original;
	}
}

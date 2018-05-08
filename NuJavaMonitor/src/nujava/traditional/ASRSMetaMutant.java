package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class ASRSMetaMutant {

	public static boolean ASRSGen(boolean left, boolean right, int opType,
			int changePoint, String mutantID) {

		boolean original = left;

		if (opType == MSGConstraints.A_AND)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITAND, mutantID);
		else if (opType == MSGConstraints.A_OR)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITOR, mutantID);
		else if (opType == MSGConstraints.A_XOR)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_XOR, mutantID);
		else {
			assert (true);
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.A_AND) {

			int type = MSGConstraints.A_AND;

			boolean mutant = (left & right);

			if (original != mutant) {
				String mID = mutantID + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}
		if (opType != MSGConstraints.A_OR) {

			int type = MSGConstraints.A_OR;

			boolean mutant = (left | right);
			if (original != mutant) {

				String mID = mutantID + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}
		if (opType != MSGConstraints.A_XOR) {

			int type = MSGConstraints.A_XOR;

			boolean mutant = (left ^ right);
			if (original != mutant) {

				String mID = mutantID + "_" + type;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, type, time, Boolean.toString(mutant));
			}
		}

		return original;
	}

	public static byte ASRSGen(byte left, byte right, int op, int changePoint,
			String id) {

		byte original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = (byte) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = (byte) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = (byte) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = (byte) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = (byte) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_AND)
			original = (byte) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITAND, id);
		else if (op == MSGConstraints.A_OR)
			original = (byte) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITOR, id);
		else if (op == MSGConstraints.A_XOR)
			original = (byte) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_XOR, id);
		else if (op == MSGConstraints.A_SHIFT_L) {
			original = (byte) (left << right);
		} else if (op == MSGConstraints.A_SHIFT_R) {
			original = (byte) (left >> right);
		} else if (op == MSGConstraints.A_SHIFT_RR) {
			original = (byte) (left >>> right);
		} else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				byte mutant = (byte) (left + right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				byte mutant = (byte) (left - right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				byte mutant = (byte) (left * right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((byte) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((byte) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}
		if (op == MSGConstraints.A_AND || op == MSGConstraints.A_OR
				|| op == MSGConstraints.A_XOR) {
			if (op != MSGConstraints.A_AND) {

				int type = MSGConstraints.A_AND;

				byte mutant = (byte) (left & right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_OR) {

				int type = MSGConstraints.A_OR;

				byte mutant = (byte) (left | right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_XOR) {

				int type = MSGConstraints.A_XOR;

				byte mutant = (byte) (left ^ right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
		}
		if (op == MSGConstraints.A_SHIFT_L || op == MSGConstraints.A_SHIFT_R
				|| op == MSGConstraints.A_SHIFT_RR) {
			if (op != MSGConstraints.A_SHIFT_L) {

				int type = MSGConstraints.A_SHIFT_L;

				byte mutant = (byte) (left << right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_R) {

				int type = MSGConstraints.A_SHIFT_R;

				byte mutant = (byte) (left >> right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_RR) {

				int type = MSGConstraints.A_SHIFT_RR;

				byte mutant = (byte) (left >>> right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Byte.toString(mutant));
				}
			}
		}
		if (oriException != null)
			throw oriException;
		return original;
	}

	public static char ASRSGen(char left, char right, int op, int changePoint,
			String id) {

		char original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = (char) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = (char) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = (char) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = (char) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = (char) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_AND)
			original = (char) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITAND, id);
		else if (op == MSGConstraints.A_OR)
			original = (char) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITOR, id);
		else if (op == MSGConstraints.A_XOR)
			original = (char) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_XOR, id);
		else if (op == MSGConstraints.A_SHIFT_L) {
			original = (char) (left << right);
		} else if (op == MSGConstraints.A_SHIFT_R) {
			original = (char) (left >> right);
		} else if (op == MSGConstraints.A_SHIFT_RR) {
			original = (char) (left >>> right);
		} else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				char mutant = (char) (left + right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				char mutant = (char) (left - right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				char mutant = (char) (left * right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((char) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((char) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}
		if (op == MSGConstraints.A_AND || op == MSGConstraints.A_OR
				|| op == MSGConstraints.A_XOR) {
			if (op != MSGConstraints.A_AND) {

				int type = MSGConstraints.A_AND;

				char mutant = (char) (left & right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_OR) {

				int type = MSGConstraints.A_OR;

				char mutant = (char) (left | right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_XOR) {

				int type = MSGConstraints.A_XOR;

				char mutant = (char) (left ^ right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
		}
		if (op == MSGConstraints.A_SHIFT_L || op == MSGConstraints.A_SHIFT_R
				|| op == MSGConstraints.A_SHIFT_RR) {
			if (op != MSGConstraints.A_SHIFT_L) {

				int type = MSGConstraints.A_SHIFT_L;

				char mutant = (char) (left << right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_R) {

				int type = MSGConstraints.A_SHIFT_R;

				char mutant = (char) (left >> right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_RR) {

				int type = MSGConstraints.A_SHIFT_RR;

				char mutant = (char) (left >>> right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance()
							.updateNonSingleMutant(mID, changePoint, type,
									time, Character.toString(mutant));
				}
			}
		}

		if (oriException != null)
			throw oriException;
		return original;
	}

	public static short ASRSGen(short left, short right, int op,
			int changePoint, String id) {

		short original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = (short) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = (short) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = (short) MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = (short) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = (short) MSG.traditional.AORBMetaMutant.AORB(left,
						right, MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_AND)
			original = (short) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITAND, id);
		else if (op == MSGConstraints.A_OR)
			original = (short) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITOR, id);
		else if (op == MSGConstraints.A_XOR)
			original = (short) MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_XOR, id);
		else if (op == MSGConstraints.A_SHIFT_L) {
			original = (short) (left << right);
		} else if (op == MSGConstraints.A_SHIFT_R) {
			original = (short) (left >> right);
		} else if (op == MSGConstraints.A_SHIFT_RR) {
			original = (short) (left >>> right);
		} else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				short mutant = (short) (left + right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				short mutant = (short) (left - right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				short mutant = (short) (left * right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((short) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((short) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}
		if (op == MSGConstraints.A_AND || op == MSGConstraints.A_OR
				|| op == MSGConstraints.A_XOR) {
			if (op != MSGConstraints.A_AND) {

				int type = MSGConstraints.A_AND;

				short mutant = (short) (left & right);
				if (original != mutant) {
					String mID = id + "_" + type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_OR) {

				int type = MSGConstraints.A_OR;

				short mutant = (short) (left | right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_XOR) {

				int type = MSGConstraints.A_XOR;

				short mutant = (short) (left ^ right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
		}
		if (op == MSGConstraints.A_SHIFT_L || op == MSGConstraints.A_SHIFT_R
				|| op == MSGConstraints.A_SHIFT_RR) {
			if (op != MSGConstraints.A_SHIFT_L) {

				int type = MSGConstraints.A_SHIFT_L;

				short mutant = (short) (left << right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_R) {

				int type = MSGConstraints.A_SHIFT_R;

				short mutant = (short) (left >> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_RR) {

				int type = MSGConstraints.A_SHIFT_RR;

				short mutant = (short) (left >>> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Short.toString(mutant));
				}
			}
		}

		if (oriException != null)
			throw oriException;
		return original;
	}

	public static int ASRSGen(int left, int right, int op, int changePoint,
			String id) {

		int original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = left + right;
		else if (op == MSGConstraints.A_SUB)
			original = left - right;
		else if (op == MSGConstraints.A_MULT)
			original = left * right;
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = left / right;
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = left % right;
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_AND)
			original = left & right;
		else if (op == MSGConstraints.A_OR)
			original = left | right;
		else if (op == MSGConstraints.A_XOR)
			original = left ^ right;
		else if (op == MSGConstraints.A_SHIFT_L) {
			original = (left << right);
		} else if (op == MSGConstraints.A_SHIFT_R) {
			original = (left >> right);
		} else if (op == MSGConstraints.A_SHIFT_RR) {
			original = (left >>> right);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				int mutant = (left + right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				int mutant = (left - right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				int mutant = (left * right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((int) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((int) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}
		if (op == MSGConstraints.A_AND || op == MSGConstraints.A_OR
				|| op == MSGConstraints.A_XOR) {
			if (op != MSGConstraints.A_AND) {

				int type = MSGConstraints.A_AND;

				int mutant = (left & right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_OR) {

				int type = MSGConstraints.A_OR;

				int mutant = (left | right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_XOR) {

				int type = MSGConstraints.A_XOR;

				int mutant = (left ^ right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
		}
		if (op == MSGConstraints.A_SHIFT_L || op == MSGConstraints.A_SHIFT_R
				|| op == MSGConstraints.A_SHIFT_RR) {
			if (op != MSGConstraints.A_SHIFT_L) {

				int type = MSGConstraints.A_SHIFT_L;

				int mutant = (int) (left << right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_R) {

				int type = MSGConstraints.A_SHIFT_R;

				int mutant = (left >> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_RR) {

				int type = MSGConstraints.A_SHIFT_RR;

				int mutant = (left >>> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Integer.toString(mutant));
				}
			}
		}

		if (oriException != null)
			throw oriException;

		return original;
	}

	public static long ASRSGen(long left, long right, int op, int changePoint,
			String id) {

		long original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = MSG.traditional.AORBMetaMutant.AORB(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = MSG.traditional.AORBMetaMutant.AORB(left, right,
						MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = MSG.traditional.AORBMetaMutant.AORB(left, right,
						MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_AND)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITAND, id);
		else if (op == MSGConstraints.A_OR)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_BITOR, id);
		else if (op == MSGConstraints.A_XOR)
			original = MSG.traditional.BORMetaMutant.BOR(left, right,
					MSGConstraints.B_XOR, id);
		else if (op == MSGConstraints.A_SHIFT_L) {
			original = (left << right);
		} else if (op == MSGConstraints.A_SHIFT_R) {
			original = (left >> right);
		} else if (op == MSGConstraints.A_SHIFT_RR) {
			original = (left >>> right);
		} else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				long mutant = (left + right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				long mutant = (left - right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				long mutant = (left * right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((long) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((long) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}
		if (op == MSGConstraints.A_AND || op == MSGConstraints.A_OR
				|| op == MSGConstraints.A_XOR) {
			if (op != MSGConstraints.A_AND) {

				int type = MSGConstraints.A_AND;

				long mutant = (left & right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_OR) {

				int type = MSGConstraints.A_OR;

				long mutant = (left | right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_XOR) {

				int type = MSGConstraints.A_XOR;

				long mutant = (left ^ right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
		}
		if (op == MSGConstraints.A_SHIFT_L || op == MSGConstraints.A_SHIFT_R
				|| op == MSGConstraints.A_SHIFT_RR) {
			if (op != MSGConstraints.A_SHIFT_L) {

				int type = MSGConstraints.A_SHIFT_L;

				long mutant = (left << right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_R) {

				int type = MSGConstraints.A_SHIFT_R;

				long mutant = (left >> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SHIFT_RR) {

				int type = MSGConstraints.A_SHIFT_RR;

				long mutant = (left >>> right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Long.toString(mutant));
				}
			}
		}

		if (oriException != null)
			throw oriException;
		return original;
	}

	public static double ASRSGen(double left, double right, int op,
			int changePoint, String id) {

		double original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
						MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
						MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				double mutant = (left + right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Double.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				double mutant = (left - right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Double.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				double mutant = (left * right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Double.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((double) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((double) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}

		if (oriException != null)
			throw oriException;
		return original;
	}

	public static double ASRSGen(float left, float right, int op,
			int changePoint, String id) {

		float original = left;
		ArithmeticException oriException = null;

		if (op == MSGConstraints.A_ADD)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_PLUS, id);
		else if (op == MSGConstraints.A_SUB)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_MINUS, id);
		else if (op == MSGConstraints.A_MULT)
			original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
					MSGConstraints.B_TIMES, id);
		else if (op == MSGConstraints.A_DIVIDE)
			try {
				original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
						MSGConstraints.B_DIVIDE, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else if (op == MSGConstraints.A_MOD)
			try {
				original = MSG.traditional.ASRSMetaMutant.ASRS(left, right,
						MSGConstraints.B_MOD, id);
			} catch (ArithmeticException e) {
				oriException = e;
			}
		else {
			assert (true);
		}

		String oriStr = (oriException != null) ? "Exception" : String
				.valueOf(original);

		long time = System.nanoTime();

		if (op == MSGConstraints.A_ADD || op == MSGConstraints.A_SUB
				|| op == MSGConstraints.A_MULT || op == MSGConstraints.A_DIVIDE
				|| op == MSGConstraints.A_MOD) {
			if (op != MSGConstraints.A_ADD) {

				int type = MSGConstraints.A_ADD;

				float mutant = (float) (left + right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Float.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_SUB) {

				int type = MSGConstraints.A_SUB;

				float mutant = (left - right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Float.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_MULT) {

				int type = MSGConstraints.A_MULT;

				float mutant = (left * right);
				if (original != mutant) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, Float.toString(mutant));
				}
			}
			if (op != MSGConstraints.A_DIVIDE) {

				int type = MSGConstraints.A_DIVIDE;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((float) (left / right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
			if (op != MSGConstraints.A_MOD) {

				int type = MSGConstraints.A_MOD;

				String mutStr = (right == 0) ? "Exception" : String
						.valueOf((float) (left % right));
				if (!mutStr.equalsIgnoreCase(oriStr)) {
					String mID = id + "_" +type;
					MutantMonitor.getInstance().updateNonSingleMutant(mID,
							changePoint, type, time, mutStr);
				}
			}
		}

		if (oriException != null)
			throw oriException;
		return original;
	}
}

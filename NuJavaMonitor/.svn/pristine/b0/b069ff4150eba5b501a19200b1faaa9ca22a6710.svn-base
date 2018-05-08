package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class AODSMetaMutant {

	public static byte AODS(byte original, int operator) {

		byte returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (byte) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (byte) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (byte) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (byte) (original - 1);
		}

		return returnValue;
	}

	public static char AODS(char original, int operator) {

		char returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (char) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (char) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (char) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (char) (original - 1);
		}

		return returnValue;
	}

	public static short AODS(short original, int operator) {

		short returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (short) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (short) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (short) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (short) (original - 1);
		}

		return returnValue;
	}

	public static int AODS(int original, int operator) {

		int returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (int) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (int) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (int) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (int) (original - 1);
		}

		return returnValue;
	}

	public static float AODS(float original, int operator) {

		float returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (float) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (float) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (float) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (float) (original - 1);
		}

		return returnValue;
	}

	public static long AODS(long original, int operator) {

		long returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (long) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (long) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (long) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (long) (original - 1);
		}

		return returnValue;
	}

	public static double AODS(double original, int operator) {

		double returnValue = original;

		if (MSGConstraints.U_PRE_INCREMENT == operator) {
			returnValue = (double) (original + 1);
		} else if (MSGConstraints.U_PRE_DECREMENT == operator) {
			returnValue = (double) (original - 1);
		} else if (MSGConstraints.U_POST_INCREMENT == operator) {
			returnValue = (double) (original + 1);
		} else if (MSGConstraints.U_POST_DECREMENT == operator) {
			returnValue = (double) (original - 1);
		}

		return returnValue;
	}

	public static int AODSGen(int originalAfter, int op, int changePoint,
			String mutantID) {

		int returnValue = originalAfter;
		int mutantValue = originalAfter;

		if (MSGConstraints.U_PRE_INCREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_PRE_DECREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter + 1;
		} else if (MSGConstraints.U_POST_INCREMENT == op) {
			returnValue = originalAfter - 1;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_POST_DECREMENT == op) {
			returnValue = originalAfter + 1;
			mutantValue = originalAfter + 1;
		}

		if (returnValue != mutantValue || originalAfter != mutantValue) {

			String mID = mutantID + "_" + op;
			MutantMonitor.getInstance()
					.updateSingleMutant(mID, changePoint);
		}

		return returnValue;
	}

	public static float AODSGen(float originalAfter, int op, int changePoint,
			String mutantID) {

		float returnValue = originalAfter;
		float mutantValue = originalAfter;

		if (MSGConstraints.U_PRE_INCREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_PRE_DECREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter + 1;
		} else if (MSGConstraints.U_POST_INCREMENT == op) {
			returnValue = originalAfter - 1;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_POST_DECREMENT == op) {
			returnValue = originalAfter + 1;
			mutantValue = originalAfter + 1;
		}

		if (returnValue != mutantValue || originalAfter != mutantValue) {
			MutantMonitor.getInstance().updateSingleMutant(mutantID + "_" + op,
					changePoint);
		}

		return returnValue;
	}

	public static double AODSGen(double originalAfter, int op, int changePoint,
			String mutantID) {

		double returnValue = originalAfter;
		double mutantValue = originalAfter;

		if (MSGConstraints.U_PRE_INCREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_PRE_DECREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter + 1;
		} else if (MSGConstraints.U_POST_INCREMENT == op) {
			returnValue = originalAfter - 1;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_POST_DECREMENT == op) {
			returnValue = originalAfter + 1;
			mutantValue = originalAfter + 1;
		}

		if (returnValue != mutantValue || originalAfter != mutantValue) {
			MutantMonitor.getInstance().updateSingleMutant(mutantID + "_" + op,
					changePoint);
		}

		return returnValue;
	}

	public static long AODSGen(long originalAfter, int op, int changePoint,
			String mutantID) {

		long returnValue = originalAfter;
		long mutantValue = originalAfter;

		if (MSGConstraints.U_PRE_INCREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_PRE_DECREMENT == op) {
			returnValue = originalAfter;
			mutantValue = originalAfter + 1;
		} else if (MSGConstraints.U_POST_INCREMENT == op) {
			returnValue = originalAfter - 1;
			mutantValue = originalAfter - 1;
		} else if (MSGConstraints.U_POST_DECREMENT == op) {
			returnValue = originalAfter + 1;
			mutantValue = originalAfter + 1;
		}

		if (returnValue != mutantValue || originalAfter != mutantValue) {

			MutantMonitor.getInstance().updateSingleMutant(mutantID + "_" + op,
					changePoint);
		}

		return returnValue;
	}
}

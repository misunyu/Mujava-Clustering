package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class AORBMetaMutant {

	public static int AORB(byte left, byte right, int op) {
		int original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static int AORB(char left, char right, int op) {
		int original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static double AORB(double left, double right, int op) {
		double original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static float AORB(float left, float right, int op) {
		float original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static int AORB(int left, int right, int op) {
		int original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static long AORB(long left, long right, int op) {
		long original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static int AORB(short left, short right, int op) {
		int original = 0;

		if (op == MSGConstraints.B_PLUS)
			original = left + right;
		if (op == MSGConstraints.B_MINUS)
			original = left - right;
		if (op == MSGConstraints.B_TIMES)
			original = left * right;
		if (op == MSGConstraints.B_DIVIDE)
			original = left / right;
		if (op == MSGConstraints.B_MOD)
			original = left % right;

		return original;
	}

	public static int AORBGen(byte left, byte right, int opType,
			int changePoint, String mutantID) {

		int original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}

	public static int AORBGen(char left, char right, int opType,
			int changePoint, String mutantID) {

		String oriStr = "Exception";
		int original = left;
		ArithmeticException ex = null;

		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}

		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}
		if (ex != null)
			throw ex;

		return original;
	}

	public static double AORBGen(double left, double right, int opType,
			int changePoint, String mutantID) {

		double original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}

	public static float AORBGen(float left, float right, int opType,
			int changePoint, String mutantID) {

		float original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}

	public static int AORBGen(int left, int right, int opType, int changePoint,
			String mutantID) {
		int original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}

	public static long AORBGen(long left, long right, int opType,
			int changePoint, String mutantID) {

		long original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {
			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {
			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {
			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}

	public static int AORBGen(short left, short right, int opType,
			int changePoint, String mutantID) {

		int original = left;

		String oriStr = "Exception";
		ArithmeticException ex = null;
		try {
			if (opType == MSGConstraints.B_PLUS)
				original = left + right;
			if (opType == MSGConstraints.B_MINUS)
				original = left - right;
			if (opType == MSGConstraints.B_TIMES)
				original = left * right;
			if (opType == MSGConstraints.B_DIVIDE)
				original = left / right;
			if (opType == MSGConstraints.B_MOD)
				original = left % right;
			oriStr = String.valueOf(original);
		} catch (ArithmeticException e) {
			ex = e;
		}

		long time = System.nanoTime();

		if (opType != MSGConstraints.B_PLUS) {

			String mutStr = String.valueOf(left + right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_PLUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_PLUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MINUS) {

			String mutStr = String.valueOf(left - right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_MINUS;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MINUS, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_TIMES) {

			String mutStr = String.valueOf(left * right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_TIMES;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_TIMES, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_MOD) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					% right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {
				String mID = mutantID + "_" + MSGConstraints.B_MOD;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_MOD, time, mutStr);
			}
		}
		if (opType != MSGConstraints.B_DIVIDE) {

			String mutStr = (right == 0) ? "Exception" : String.valueOf(left
					/ right);
			if (!oriStr.equalsIgnoreCase(mutStr)) {

				String mID = mutantID + "_" + MSGConstraints.B_DIVIDE;
				MutantMonitor.getInstance().updateNonSingleMutant(mID,
						changePoint, MSGConstraints.B_DIVIDE, time, mutStr);
			}
		}

		if (ex != null)
			throw ex;

		return original;
	}
}

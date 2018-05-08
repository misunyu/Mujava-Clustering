package MSG.traditional;

import MSG.MSGConstraints;

public class AORBMetaMutant {
	public static int AORB(byte left, byte right, int op, String id) {
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
	public static int AORB(char left, char right, int op, String id) {
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
	public static int AORB(short left, short right, int op, String id) {
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
	
	public static double AORB(double left, double right, int op, String id) {
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

	public static float AORB(float left, float right, int op, String id) {
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

	public static int AORB(int left, int right, int op, String id) {
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

	public static long AORB(long left, long right, int op, String id) {
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
}

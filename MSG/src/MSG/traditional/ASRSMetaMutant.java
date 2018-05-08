package MSG.traditional;

import MSG.MSGConstraints;

public class ASRSMetaMutant {
	public static boolean ASRS(boolean left, boolean right, int op, String id) {
		if (op == MSGConstraints.A_AND)
			return left & right;
		if (op == MSGConstraints.A_OR)
			return left | right;
		if (op == MSGConstraints.A_XOR)
			return left ^ right;

		assert (false);

		return false;
	}

	public static int ASRS(int left, int right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return left + right;
		if (op == MSGConstraints.A_SUB)
			return left - right;
		if (op == MSGConstraints.A_MULT)
			return left * right;
		if (op == MSGConstraints.A_DIVIDE)
			return left / right;
		if (op == MSGConstraints.A_MOD)
			return left % right;
		if (op == MSGConstraints.A_AND)
			return left & right;
		if (op == MSGConstraints.A_OR)
			return left | right;
		if (op == MSGConstraints.A_XOR)
			return left ^ right;
		if (op == MSGConstraints.A_SHIFT_L)
			return (left << right);
		if (op == MSGConstraints.A_SHIFT_R)
			return (left >> right);
		if (op == MSGConstraints.A_SHIFT_RR)
			return (left >>> right);

		assert (false);

		return 0;
	}

	public static byte ASRS(byte left, byte right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (byte) (left + right);
		if (op == MSGConstraints.A_SUB)
			return (byte) (left - right);
		if (op == MSGConstraints.A_MULT)
			return (byte) (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (byte) (left / right);
		if (op == MSGConstraints.A_MOD)
			return (byte) (left % right);
		if (op == MSGConstraints.A_AND)
			return (byte) (left & right);
		if (op == MSGConstraints.A_OR)
			return (byte) (left | right);
		if (op == MSGConstraints.A_XOR)
			return (byte) (left ^ right);
		if (op == MSGConstraints.A_SHIFT_L)
			return (byte) (left << right);
		if (op == MSGConstraints.A_SHIFT_R)
			return (byte) (left >> right);
		if (op == MSGConstraints.A_SHIFT_RR)
			return (byte) (left >>> right);

		assert (false);

		return 0;
	}

	public static short ASRS(short left, short right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (short) (left + right);
		if (op == MSGConstraints.A_SUB)
			return (short) (left - right);
		if (op == MSGConstraints.A_MULT)
			return (short) (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (short) (left / right);
		if (op == MSGConstraints.A_MOD)
			return (short) (left % right);
		if (op == MSGConstraints.A_AND)
			return (short) (left & right);
		if (op == MSGConstraints.A_OR)
			return (short) (left | right);
		if (op == MSGConstraints.A_XOR)
			return (short) (left ^ right);
		if (op == MSGConstraints.A_SHIFT_L)
			return (short) (left << right);
		if (op == MSGConstraints.A_SHIFT_R)
			return (short) (left >> right);
		if (op == MSGConstraints.A_SHIFT_RR)
			return (short) (left >>> right);

		assert (false);

		return 0;
	}

	public static char ASRS(char left, char right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (char) (left + right);
		if (op == MSGConstraints.A_SUB)
			return (char) (left - right);
		if (op == MSGConstraints.A_MULT)
			return (char) (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (char) (left / right);
		if (op == MSGConstraints.A_MOD)
			return (char) (left % right);
		if (op == MSGConstraints.A_AND)
			return (char) (left & right);
		if (op == MSGConstraints.A_OR)
			return (char) (left | right);
		if (op == MSGConstraints.A_XOR)
			return (char) (left ^ right);
		if (op == MSGConstraints.A_SHIFT_L)
			return (char) (left << right);
		if (op == MSGConstraints.A_SHIFT_R)
			return (char) (left >> right);
		if (op == MSGConstraints.A_SHIFT_RR)
			return (char) (left >>> right);

		assert (false);

		return 0;
	}

	public static long ASRS(long left, long right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (left + right);
		if (op == MSGConstraints.A_SUB)
			return (left - right);
		if (op == MSGConstraints.A_MULT)
			return (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (left / right);
		if (op == MSGConstraints.A_MOD)
			return (left % right);
		if (op == MSGConstraints.A_AND)
			return (left & right);
		if (op == MSGConstraints.A_OR)
			return (left | right);
		if (op == MSGConstraints.A_XOR)
			return (left ^ right);
		if (op == MSGConstraints.A_SHIFT_L)
			return (left << right);
		if (op == MSGConstraints.A_SHIFT_R)
			return (left >> right);
		if (op == MSGConstraints.A_SHIFT_RR)
			return (left >>> right);

		assert (false);

		return 0;
	}

	public static float ASRS(float left, float right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (left + right);
		if (op == MSGConstraints.A_SUB)
			return (left - right);
		if (op == MSGConstraints.A_MULT)
			return (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (left / right);
		if (op == MSGConstraints.A_MOD)
			return (left % right);

		assert (false);

		return 0;
	}

	public static double ASRS(double left, double right, int op, String id) {
		if (op == MSGConstraints.A_ADD)
			return (left + right);
		if (op == MSGConstraints.A_SUB)
			return (left - right);
		if (op == MSGConstraints.A_MULT)
			return (left * right);
		if (op == MSGConstraints.A_DIVIDE)
			return (left / right);
		if (op == MSGConstraints.A_MOD)
			return (left % right);

		assert (false);

		return 0;
	}
}

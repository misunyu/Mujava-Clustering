package MSG.traditional;

import MSG.MSGConstraints;

public class RORMetaMutant {
	public static <E> boolean ROR(E left, E right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;

		return original;
	}

	public static boolean ROR(byte left, byte right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}
	
	
	public static boolean ROR(boolean left, boolean right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(char left, char right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(double left, double right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(float left, float right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(int left, int right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(long left, long right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}

	public static boolean ROR(short left, short right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_LESS)
			original = left < right;
		if (op == MSGConstraints.B_LESSEQUAL)
			original = left <= right;
		if (op == MSGConstraints.B_GREATER)
			original = left > right;
		if (op == MSGConstraints.B_GREATEREQUAL)
			original = left >= right;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;

		return original;
	}
}

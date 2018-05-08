package MSG.traditional;

import MSG.MSGConstraints;

public class AOIDUMetaMutant {
	public static int AOIDU(int exp, int op, String id) {
		if (exp == 0)
			return 0;

		if (op == MSGConstraints.U_MINUS)
			exp = -exp;

		return exp;
	}

	public static float AOIDU(float exp, int op, String id) {
		if (exp == 0)
			return 0;

		if (op == MSGConstraints.U_MINUS)
			exp = -exp;

		return exp;
	}

	public static long AOIDU(long exp, int op, String id) {
		if (exp == 0)
			return 0;

		if (op == MSGConstraints.U_MINUS)
			exp = -exp;

		return exp;
	}

	public static double AOIDU(double exp, int op, String id) {
		if (exp == 0)
			return 0;

		if (op == MSGConstraints.U_MINUS)
			exp = -exp;

		return exp;
	}

}

package MSG.traditional;

import MSG.MSGConstraints;

public class LOIDMetaMutant {
	public static boolean LOID(boolean exp, int op, String id) {
		boolean original = exp;

		if (op == MSGConstraints.U_NOT)
			original = !exp;

		return original;
	}
}

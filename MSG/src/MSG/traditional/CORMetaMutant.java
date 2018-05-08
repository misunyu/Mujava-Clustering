package MSG.traditional;

import MSG.MSGConstraints;

public class CORMetaMutant {
	public static <E> boolean COR(E left, E right, int op, String id) {
		boolean original = false;
		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		return original;
	}

	public static boolean COR(boolean left, boolean right, int op, String id) {
		boolean original = false;

		if (op == MSGConstraints.B_EQUAL)
			original = left == right;
		if (op == MSGConstraints.B_NOTEQUAL)
			original = left != right;
		if (op == MSGConstraints.TRUE)
			original = true;
		if (op == MSGConstraints.FALSE)
			original = false;
		if (op == MSGConstraints.LHS)
			original = left;
		if (op == MSGConstraints.RHS)
			original = right;
		if (op == MSGConstraints.B_LOGICAL_AND)
			original = left && right;
		if (op == MSGConstraints.B_LOGICAL_OR)
			original = left || right;

		return original;
	}
	
	// left, right expression???ÑÏöî?ÜÎäî Í≤ΩÏö∞Î•?Í≥®Îùº, ?åÎ†§Ï§?ã§.
	public static boolean isHalfEvaluation(boolean left, int operator) {
		// int operator = MutantMonitor.getInstance().getSubID();
		return (left && operator == MSGConstraints.B_LOGICAL_OR)
				|| (!left && operator == MSGConstraints.B_LOGICAL_AND);
	}


}

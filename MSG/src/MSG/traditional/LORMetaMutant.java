package MSG.traditional;

import MSG.MSGConstraints;

public class LORMetaMutant {

	public static boolean LOR(boolean left, boolean right, int op, String id) {
		boolean original = left;

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

package nujava.classical;

public class JSIMetaMutant {

	public static <E> E JSIGen(E mutant, E original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			return mutant;
		}

		if (!original.equals(mutant)) {
			// comparator.reportMutant(mutantID, String.valueOf(original), 0,
			// String
			// .valueOf(mutant));
		}
		return original;
	}

	public static int JSIGen(int mutant, int original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			return mutant;
		}

		if (original != mutant) {
			// comparator.reportMutant(mutantID, String.valueOf(original), 0,
			// String
			// .valueOf(mutant));
		}
		return original;
	}

	public static long JSIGen(long mutant, long original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			return mutant;
		}

		if (original != mutant) {
			// comparator.reportMutant(mutantID, String.valueOf(original), 0,
			// String
			// .valueOf(mutant));
		}
		return original;
	}

	public static double JSIGen(double mutant, double original, String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			return mutant;
		}

		if (original != mutant) {
			// comparator.reportMutant(mutantID, String.valueOf(original), 0,
			// String
			// .valueOf(mutant));
		}
		return original;
	}

	public static boolean JSIGen(boolean mutant, boolean original,
			String mutantID) {
		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			return mutant;
		}

		if (original != mutant) {
			// comparator.reportMutant(mutantID, String.valueOf(original), 0,
			// String
			// .valueOf(mutant));
		}
		return original;
	}
}

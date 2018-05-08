package nujava.classical;

import nujava.MutantMonitor;

import com.thoughtworks.xstream.XStream;

public class JTDMetaMutant {

	public static <E> E JTDGen(E withVar, E right, E woVar, String mutantID,
			boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		XStream xs = MutantMonitor.getInstance().getXstream();

		if (isDef) {
			String original = xs.toXML(withVar);
			String mutant = xs.toXML(woVar);
			String exp = xs.toXML(right);
			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			String original = xs.toXML(withVar);
			String mutant = xs.toXML(woVar);
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static char JTDGen(char withVar, char right, char woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);
		String original = Character.toString(withVar);
		String mutant = Character.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Character.toString(right);
			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static short JTDGen(short withVar, short right, short woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Short.toString(withVar);
		String mutant = Short.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Short.toString(right);
			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static boolean JTDGen(boolean withVar, boolean right, boolean woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Boolean.toString(withVar);
		String mutant = Boolean.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Boolean.toString(right);

			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static int JTDGen(int withVar, int right, int woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Integer.toString(withVar);
		String mutant = Integer.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Integer.toString(right);
			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static float JTDGen(float withVar, float right, float woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Float.toString(withVar);
		String mutant = Float.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Float.toString(right);

			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static double JTDGen(double withVar, double right, double woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Double.toString(withVar);
		String mutant = Double.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Double.toString(right);

			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static byte JTDGen(byte withVar, byte right, byte woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);
		String original = Byte.toString(withVar);
		String mutant = Byte.toString(woVar);

		if (isDef) {
			// String exp = xs.toXML(right);
			String exp = Byte.toString(right);

			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}

	public static long JTDGen(long withVar, long right, long woVar,
			String mutantID, boolean isDef) {

		if (!nujava.NuJavaHelper.isExpWeakMode()) {
			if (isDef)
				return withVar;
			else
				return woVar;
		}

		// XStream xs = comparator.getXstream();
		// String original = xs.toXML(withVar);
		// String mutant = xs.toXML(woVar);

		String original = Long.toString(withVar);
		String mutant = Long.toString(woVar);

		if (isDef) {
			// [this.var = right -> var = right ]
			// String exp = xs.toXML(right);
			String exp = Long.toString(right);

			if (!(original.equals(exp) && mutant.equals(exp))) {
				String originalResult = right + ":" + mutant;
				String mutantResult = original + ":" + right;
				// comparator.reportMutant(mutantID, originalResult, 0,
				// mutantResult);
			}
		} else {
			// [left = this.var -> left = var]
			if (!original.equals(mutant)) {
				// comparator.reportMutant(mutantID, original, 0, mutant);
			}
		}

		return right;
	}
}

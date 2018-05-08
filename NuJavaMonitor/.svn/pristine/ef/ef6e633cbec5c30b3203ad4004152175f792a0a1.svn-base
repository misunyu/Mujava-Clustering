package nujava.traditional;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class AOISMetaMutant {

	public static double AOIS(double original) {
		double returnValue = original;

		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				return MSG.traditional.AORSMetaMutant.AORSValue(original, sID);
		}

		return returnValue;
	}

	public static float AOIS(float original) {
		float returnValue = original;

		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				return MSG.traditional.AORSMetaMutant.AORSValue(original, sID);
		}

		return returnValue;
	}

	public static int AOIS(int original) {
		int returnValue = original;
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				return MSG.traditional.AORSMetaMutant.AORSValue(original, sID);
		}

		return returnValue;
	}

	public static long AOIS(long original) {
		long returnValue = original;
		if (!MutantMonitor.isExpWeakMode) {
			int sID = MutantMonitor.subID;
			if (sID != -1)
				return MSG.traditional.AORSMetaMutant.AORSValue(original, sID);
		}

		return returnValue;
	}

	public static int AOISGen(int originalValue, int changePoint,
			String mutantID) {

		if (!MutantMonitor.isExpWeakMode) {

			int returnValue = originalValue;
			int operator = MutantMonitor.subID;
			if (operator == -1)
				return originalValue;

			if (operator == MSGConstraints.U_PRE_INCREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_PRE_DECREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_POST_INCREMENT) {
				returnValue = originalValue - 1;
			} else if (operator == MSGConstraints.U_POST_DECREMENT) {
				returnValue = originalValue + 1;
			}
			return returnValue;
		}

		int mutantExpression = 0;
		int mutantVariable = 0;

		// PRE_INC, v -> ++v
		mutantExpression = originalValue + 1;
		mutantVariable = originalValue + 1;
		compInt(mutantID, MSGConstraints.U_PRE_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// PRE_DEC, v -> --v
		mutantExpression = originalValue - 1;
		mutantVariable = originalValue - 1;
		compInt(mutantID, MSGConstraints.U_PRE_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_INC, v -> v++
		mutantExpression = originalValue;
		mutantVariable = originalValue + 1;
		compInt(mutantID, MSGConstraints.U_POST_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_DEC, v -> v--
		mutantExpression = originalValue;
		mutantVariable = originalValue - 1;
		compInt(mutantID, MSGConstraints.U_POST_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);

		return originalValue;
	}

	public static double AOISGen(double originalValue, int changePoint,
			String mutantID) {

		if (!MutantMonitor.isExpWeakMode) {
			double returnValue = originalValue;
			int operator = MutantMonitor.subID;
			if (operator == -1)
				return originalValue;

			if (operator == MSGConstraints.U_PRE_INCREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_PRE_DECREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_POST_INCREMENT) {
				returnValue = originalValue - 1;
			} else if (operator == MSGConstraints.U_POST_DECREMENT) {
				returnValue = originalValue + 1;
			}
			return returnValue;
		}

		double mutantExpression = 0;
		double mutantVariable = 0;

		// PRE_INC, v -> ++v
		mutantExpression = originalValue + 1;
		mutantVariable = originalValue + 1;
		compDouble(mutantID, MSGConstraints.U_PRE_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// PRE_DEC, v -> --v
		mutantExpression = originalValue - 1;
		mutantVariable = originalValue - 1;
		compDouble(mutantID, MSGConstraints.U_PRE_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_INC, v -> v++
		mutantExpression = originalValue;
		mutantVariable = originalValue + 1;
		compDouble(mutantID, MSGConstraints.U_POST_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_DEC, v -> v--
		mutantExpression = originalValue;
		mutantVariable = originalValue - 1;
		compDouble(mutantID, MSGConstraints.U_POST_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);

		return originalValue;
	}

	public static float AOISGen(float originalValue, int changePoint,
			String mutantID) {

		if (!MutantMonitor.isExpWeakMode) {
			float returnValue = originalValue;
			int operator = MutantMonitor.subID;
			if (operator == -1)
				return originalValue;

			if (operator == MSGConstraints.U_PRE_INCREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_PRE_DECREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_POST_INCREMENT) {
				returnValue = originalValue - 1;
			} else if (operator == MSGConstraints.U_POST_DECREMENT) {
				returnValue = originalValue + 1;
			}
			return returnValue;
		}

		float mutantExpression = 0;
		float mutantVariable = 0;

		// PRE_INC, v -> ++v
		mutantExpression = originalValue + 1;
		mutantVariable = originalValue + 1;
		compFloat(mutantID, MSGConstraints.U_PRE_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// PRE_DEC, v -> --v
		mutantExpression = originalValue - 1;
		mutantVariable = originalValue - 1;
		compFloat(mutantID, MSGConstraints.U_PRE_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_INC, v -> v++
		mutantExpression = originalValue;
		mutantVariable = originalValue + 1;
		compFloat(mutantID, MSGConstraints.U_POST_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_DEC, v -> v--
		mutantExpression = originalValue;
		mutantVariable = originalValue - 1;
		compFloat(mutantID, MSGConstraints.U_POST_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);

		return originalValue;
	}

	public static long AOISGen(long originalValue, int changePoint,
			String mutantID) {
		if (!MutantMonitor.isExpWeakMode) {
			long returnValue = originalValue;
			int operator = MutantMonitor.subID;
			if (operator == -1)
				return originalValue;

			if (operator == MSGConstraints.U_PRE_INCREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_PRE_DECREMENT) {
				returnValue = originalValue;
			} else if (operator == MSGConstraints.U_POST_INCREMENT) {
				returnValue = originalValue - 1;
			} else if (operator == MSGConstraints.U_POST_DECREMENT) {
				returnValue = originalValue + 1;
			}
			return returnValue;
		}
		long mutantExpression = 0;
		long mutantVariable = 0;

		// PRE_INC, v -> ++v
		mutantExpression = originalValue + 1;
		mutantVariable = originalValue + 1;
		compLong(mutantID, MSGConstraints.U_PRE_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// PRE_DEC, v -> --v
		mutantExpression = originalValue - 1;
		mutantVariable = originalValue - 1;
		compLong(mutantID, MSGConstraints.U_PRE_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_INC, v -> v++
		mutantExpression = originalValue;
		mutantVariable = originalValue + 1;
		compLong(mutantID, MSGConstraints.U_POST_INCREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);
		// POST_DEC, v -> v--
		mutantExpression = originalValue;
		mutantVariable = originalValue - 1;
		compLong(mutantID, MSGConstraints.U_POST_DECREMENT, originalValue,
				mutantExpression, mutantVariable, changePoint);

		return originalValue;
	}

	private static void compDouble(String mutantID, int opType,
			double originalAfter, double returnValue, double mutantValue,
			int changePoint) {

		// MuJavaLogger.getLogger().reach(mutantID + "_" + opType);

		if (originalAfter != returnValue || originalAfter != mutantValue) {
			// XStream xs = comparator.getXstream();
			// StringBuffer sb = new StringBuffer();
			// sb.append(xs.toXML(originalAfter));
			// sb.append(":");
			// sb.append(xs.toXML(originalAfter));
			// String oResult = sb.toString();
			// sb.setLength(0);
			// sb.append(xs.toXML(returnValue));
			// sb.append(":");
			// sb.append(xs.toXML(mutantValue));
			// String mResult = sb.toString();
			// comparator.reportMutant(mutantID, oResult, opType, mResult);
			String mID = mutantID + "_" + opType;
			MutantMonitor.getInstance().updateNonSingleMutant(mID, changePoint,
					opType);
		}
	}

	private static void compFloat(String mutantID, int opType,
			float originalAfter, float returnValue, float mutantValue,
			int changePoint) {

		// MuJavaLogger.getLogger().reach(mutantID + "_" + opType);

		if (originalAfter != returnValue || originalAfter != mutantValue) {
			// XStream xs = comparator.getXstream();
			// StringBuffer sb = new StringBuffer();
			// sb.append(xs.toXML(originalAfter));
			// sb.append(":");
			// sb.append(xs.toXML(originalAfter));
			// String oResult = sb.toString();
			// sb.setLength(0);
			// sb.append(xs.toXML(returnValue));
			// sb.append(":");
			// sb.append(xs.toXML(mutantValue));
			// String mResult = sb.toString();
			// comparator.reportMutant(mutantID, oResult, opType, mResult);
			String mID = mutantID + "_" + opType;
			MutantMonitor.getInstance().updateNonSingleMutant(mID, changePoint,
					opType);
		}
	}

	private static void compInt(String mutantID, int opType, int originalAfter,
			int returnValue, int mutantValue, int changePoint) {

		// MuJavaLogger.getLogger().reach(mutantID + "_" + opType);

		if (originalAfter != returnValue || originalAfter != mutantValue) {
			// XStream xs = comparator.getXstream();
			// StringBuffer sb = new StringBuffer();
			// sb.append(xs.toXML(originalAfter));
			// sb.append(":");
			// sb.append(xs.toXML(originalAfter));
			// String oResult = sb.toString();
			// sb.setLength(0);
			// sb.append(xs.toXML(returnValue));
			// sb.append(":");
			// sb.append(xs.toXML(mutantValue));
			// String mResult = sb.toString();
			// comparator.reportMutant(mutantID, oResult, opType, mResult);
			String mID = mutantID + "_" + opType;
			MutantMonitor.getInstance().updateNonSingleMutant(mID, changePoint,
					opType);
		}
	}

	private static void compLong(String mutantID, int opType,
			long originalAfter, long returnValue, long mutantValue,
			int changePoint) {

		// MuJavaLogger.getLogger().reach(mutantID + "_" + opType);

		if (originalAfter != returnValue || originalAfter != mutantValue) {
			// XStream xs = comparator.getXstream();
			// StringBuffer sb = new StringBuffer();
			// sb.append(xs.toXML(originalAfter));
			// sb.append(":");
			// sb.append(xs.toXML(originalAfter));
			// String oResult = sb.toString();
			// sb.setLength(0);
			// sb.append(xs.toXML(returnValue));
			// sb.append(":");
			// sb.append(xs.toXML(mutantValue));
			// String mResult = sb.toString();
			// comparator.reportMutant(mutantID, oResult, opType, mResult);
			String mID = mutantID + "_" + opType;
			MutantMonitor.getInstance().updateNonSingleMutant(mID, changePoint,
					opType);
		}
	}
}

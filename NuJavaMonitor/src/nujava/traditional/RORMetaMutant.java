package nujava.traditional;

import java.util.ArrayList;
import java.util.List;

import nujava.MutantMonitor;
import MSG.MSGConstraints;

public class RORMetaMutant {

	static void compBoolean(byte left, byte right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}

	static void compBoolean(char left, char right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
				
			}
		}
	}

	static void compBoolean(double left, double right, int op,
			boolean original, String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}

	static void compBoolean(float left, float right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}

	static void compBoolean(int left, int right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
				
			//	System.out.println("compBoolean mutantID: " + is.mutantID + " result: " + is.result);


			}
		}
	}

	static void compBoolean(long left, long right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}

	static void compBoolean(short left, short right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}

	
	static void compBoolean(boolean left, boolean right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.RORMetaMutant.ROR(left, right,
					type, id);

			if (original != mutant) {
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, type, time, Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
	}
	/**
	 * 마지막 보고에 사용한 time을 기록하는 변수
	 */
	static private long previousTime = 0L;

	/**
	 * nanotime 정확도로 system의 시간을 획득한다. 다만, 매우 빠른 순간에 호출되어 동일한 시간을 반환하는 경우를 막기
	 * 위해서 항상 다른 값을 반환하도록 수정한다.
	 * 
	 * @return system의 nanotime
	 */
	private static long getUniqueTime() {
		long time = System.nanoTime();

		if (time == previousTime) {
			time++;
		}

		previousTime = time;

		return time;
	}

	static List<MutantMonitor.InternalStructure> mutantList = new ArrayList<MutantMonitor.InternalStructure>(
			6);

	public static boolean RORGen(byte left, byte right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);
		long time = getUniqueTime();

		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(char left, char right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);

		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(double left, double right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);

		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(float left, float right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);

		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(int left, int right, int op, int changePoint,
			String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);
		//System.out.println("RORGen1 original: " + original);

		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);
		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(long left, long right, int op,
			int changePoint, String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);

		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static boolean RORGen(short left, short right, int op,
			int changePoint, String id) {
		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);
		
		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESS, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_LESSEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATER, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_GREATEREQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_EQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.B_NOTEQUAL, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.TRUE, time);
		compBoolean(left, right, op, original, id, changePoint,
				MSGConstraints.FALSE, time);

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}
	
	public static boolean RORGen(boolean left, boolean right, int op,
			int changePoint, String id) {
		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);
		
		long time = getUniqueTime();
		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		if (op == MSGConstraints.B_EQUAL) {
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.B_NOTEQUAL, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.TRUE, time);
		}
		
		if (op == MSGConstraints.B_NOTEQUAL) {
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.B_EQUAL, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.FALSE, time);
		}

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}

	public static <E> boolean RORGen(E left, E right, int op, int changePoint,
			String id) {

		boolean original = MSG.traditional.RORMetaMutant.ROR(left, right, op,
				id);
	//	System.out.println("RORGen2 original: " + original);

		long time = getUniqueTime();

		if (!mutantList.isEmpty()) {
			mutantList.clear();
		}

		if (op != MSGConstraints.B_EQUAL) {

			boolean mutant = left == right;
			if (original != mutant) {
				int type = MSGConstraints.B_EQUAL;
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, MSGConstraints.B_EQUAL, time,
				// Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}
		if (op != MSGConstraints.B_NOTEQUAL) {

			boolean mutant = left != right;
			if (original != mutant) {
				int type = MSGConstraints.B_NOTEQUAL;
				String mID = id + "_" + type;
				// MutantMonitor.getInstance().updateNonSingleMutant(mID,
				// changePoint, MSGConstraints.B_NOTEQUAL, time,
				// Boolean.toString(mutant));
				MutantMonitor.InternalStructure is = MutantMonitor
						.getInstance().new InternalStructure();
				is.changePoint = changePoint;
				is.mutantID = mID;
				is.result = Boolean.toString(mutant);
				is.subID = type;
				mutantList.add(is);
			}
		}

		MutantMonitor.getInstance().updateMutants(mutantList, time);

		return original;
	}
}

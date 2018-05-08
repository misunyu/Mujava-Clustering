package nujava.traditional;

import nujava.MutantMonitor;
import nujava.MutantMonitor.InternalStructure;

import java.util.ArrayList;
import java.util.List;

import MSG.MSGConstraints;

public class CORMetaMutant
{
	/**
	 * right expression evaluation 이 필요없는 경우를 알려준다.
	 * 
	 * @param left
	 * @param op
	 * @param changePoint
	 * @return
	 */
	public static boolean isHalfEvaluation(boolean left, int op, int changePoint)
	{
		// Side Effect 가 없는 경우는 Right Expression 의 Evaluation 을 수행한다.
		if (!MutantMonitor.SideEffectChangePoint[changePoint])
		{
			return false;
		}

		// 특정 조건에 Right Expression 에 대해 Right Expression Evaluation 을 알림
		return (left && (op == MSGConstraints.B_LOGICAL_OR))
				|| (!left && (op == MSGConstraints.B_LOGICAL_AND));
	}

	/**
	 * Original Expression 에서 Left Expression evaluation 만 수행했음에도, Right
	 * Expression 에 대해 Evaluation 를 시도하는 경우는 강제로 killed mutant 로 판단
	 * 
	 * @param left
	 * @param op
	 * @param changePoint
	 * @param id
	 * @return
	 */
	public static boolean preCOR(boolean left, int op, int changePoint,
			String id)
	{
		// right expression 을 evaluation하는 과정 때문에 very weakly killed라고 판정.
		if (!MutantMonitor.SideEffectChangePoint[changePoint])
		{
			return left;
		}

		MutantMonitor monitor = MutantMonitor.getInstance();

		if (left && op == MSGConstraints.B_LOGICAL_OR)
		{
			monitor.updateNonSingleMutant(id + "_"
					+ MSGConstraints.B_LOGICAL_AND, changePoint,
					MSGConstraints.B_LOGICAL_AND);
		}
		else if (!left && op == MSGConstraints.B_LOGICAL_AND)
		{
			monitor.updateNonSingleMutant(id + "_"
					+ MSGConstraints.B_LOGICAL_OR, changePoint,
					MSGConstraints.B_LOGICAL_OR);
		}

		return left;
	}
	
	static List<MutantMonitor.InternalStructure> mutantList = new ArrayList<MutantMonitor.InternalStructure>(
			6);

	static void compBoolean(boolean left, boolean right, int op, boolean original,
			String id, int changePoint, int type, long time) {

		if (op != type) {

			boolean mutant = MSG.traditional.CORMetaMutant.COR(left, right,
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
	
	public static boolean CORGen(boolean left, boolean right, int op,
			int changePoint, String id)
	{
		// Original Expression Evaluation 수행
		boolean original = false;

		if (op == MSGConstraints.B_LOGICAL_AND)
			original = left && right;
		if (op == MSGConstraints.B_LOGICAL_OR)
			original = left || right;

		// Mutant Expression 수행 및 비교/보고

		MutantMonitor monitor = MutantMonitor.getInstance();

		long time = System.nanoTime();
		
		if (op == MSGConstraints.B_LOGICAL_AND) {
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.FALSE, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.LHS, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.RHS, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.B_EQUAL, time);
		}
		
		if (op == MSGConstraints.B_LOGICAL_OR) {
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.B_NOTEQUAL, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.RHS, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.LHS, time);
			compBoolean(left, right, op, original, id, changePoint,
					MSGConstraints.TRUE, time);
		}		
		
		MutantMonitor.getInstance().updateMutants(mutantList, time);
		return original;
	}
}

package MSG;

import java.util.StringTokenizer;

public class MutantMonitor {
	private static MutantMonitor monitor = null;;

	public static MutantMonitor getInstance() {
		if (monitor == null)
			monitor = new MutantMonitor();

		return monitor;
	}

	public static boolean[] ExecuteChangePoint = new boolean[0];

	/**
	 * Obtain a sub ID which means a sub ID evalutating one mutant of all
	 * possible MSG metamutant. It is called only if isMSG is true and mutant ID
	 * is 4th level ( opeartor + file_hashcode + changepoint + operator).
	 * 
	 * @return -1 if the given mutantID is not 4th level, or any integer value
	 *         otherwise
	 */
	public static int subID = -1;

	/**
	 * ChangePoint 갯수를 설정한다.
	 * <p>
	 * Change Point 갯수만큼의 배열을 생성하고, 각 배열의 위치에 metamutant 를 수행할 수 없도록 설정한다. 반드시
	 * Instance 가 생성된 이후에 호출되어 초기화시켜줘야 한다. 초기화되지 않은 경우, metamutant는 original 과
	 * 동일하게 수행된다.
	 * 
	 * @param numOfChangePoints
	 *            the number of change points
	 */
	public static void initArraySize(int numOfChangePoints) {
		ExecuteChangePoint = new boolean[numOfChangePoints + 1];

		for (int i = 0; i < numOfChangePoints + 1; i++) {
			ExecuteChangePoint[i] = false;
		}
	}

	public MutantMonitor() {

		// set MSG mode
		String ID = System.getenv(MSGConstraints.header_mutantID);
		if (ID == null) {
			ID = System.getProperty(MSGConstraints.header_mutantID);
		}

		int changePointID = -1;
		int index = -1;

		if (ID != null && !ID.isEmpty()) {
			StringTokenizer st = new StringTokenizer(ID, "_");
			if (st.hasMoreTokens()) {
				// mutationOperator = st.nextToken();
				st.nextToken();
			}
			if (st.hasMoreTokens()) {
				// hashCode = Integer.parseInt(st.nextToken());
				st.nextToken();
			}
			if (st.hasMoreTokens()) {
				changePointID = Integer.parseInt(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				index = Integer.parseInt(st.nextToken());
			}
		}

		if (changePointID > 0 && changePointID < ExecuteChangePoint.length) {
			ExecuteChangePoint[changePointID] = true;
		}

		if (index >= 0) {
			subID = index;
		}
	}
}

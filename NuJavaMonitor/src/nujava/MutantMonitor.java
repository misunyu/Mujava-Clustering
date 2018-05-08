package nujava;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDomDriver;

public class MutantMonitor {

	public class InternalStructure {
		public String mutantID;
		public int changePoint;
		public int subID;
		public String result;
	}

	/**
	 * SINGLETON Pattern 을 위한 static instance.
	 * 
	 * @see MutantMonitor.getInstance
	 */
	static MutantMonitor monitor;
	/**
	 * NuJava를 수행한 결과를 전달할 파일 이름을 기록하는 변수.
	 * <p>
	 * 초기화하고, VM을 생성할 때 HOST에서 assign 해준다.
	 * 
	 * @see MutationMonitor.writeWeakMutantID
	 */
	static String resultFileName = "";

	/**
	 * 결과 파일에서 mutant 들을 구분하는 헤더. 오직 ID만을 기록한다.
	 */
	static final int HEADER_ID_ONLY = 1234;
	/**
	 * 결과 파일에서 mutant 들을 구분하는 헤더. ID와 mutated value, 시간 등의 정보를 저장할 때 사용된다.
	 */
	static final int HEADER_REGULAR = 4321;

	public static boolean[] ExecuteChangePoint = new boolean[0];
	public static boolean[] SideEffectChangePoint = new boolean[0];
	public static int subID = -1;

	public static boolean isStrongMode = false;
	public static boolean isMethodWeakMode = false;
	public static boolean isExpWeakMode = false;
	public static boolean isConditionalMutualMutantMode = false;

	/**
	 * SINGLETON instance를 획득하는 함수.
	 * <p>
	 * 항상 하나의 instance를 제공한다.
	 * 
	 * @return an instance of MutantMonitor Class
	 */
	public static MutantMonitor getInstance() {
		if (monitor == null)
			monitor = new MutantMonitor();

		return monitor;
	}

	/**
	 * ChangePoint 갯수를 설정한다.
	 * <p>
	 * Change Point 갯수만큼의 배열을 생성하고, 각 배열의 위치에 serial-mutant 를 수행할 수 없도록 설정한다.
	 * 반드시 Instance 가 생성된 이후에 호출되어 초기화시켜줘야 한다. 초기화되지 않은 경우, serail-mutant는
	 * original 과 동일하게 수행된다.
	 * 
	 * @param numOfChangePoints
	 *            the number of change points
	 */
	public static void initArraySize(int numOfChangePoints) {
		ExecuteChangePoint = new boolean[numOfChangePoints + 1];
		SideEffectChangePoint = new boolean[numOfChangePoints + 1];

		for (int i = 0; i < numOfChangePoints + 1; i++) {
			ExecuteChangePoint[i] = false;
			SideEffectChangePoint[i] = false;
		}

		// System.err.println("<<" + ExecuteChangePoint.length + ">>");
	}

	/**
	 * Exp-level weak에서만 사용되는 변수. Host에서 전달받은 찾을 대상의 mutant들을 저장한다.
	 */
	Map<Integer, BitSet> mutantMap = new HashMap<Integer, BitSet>();

	String targetClassName = "";
	String mutationOperator = "";
	int hashCode = -1;

	/**
	 * 객체 상태를 기록하기 위한 라이브러리 인스턴스.
	 */
	protected XStream xstream = null;

	/**
	 * 객체 생성자
	 */
	MutantMonitor() {

		// define the mode to be executed
		initExecutionMode();

		// setup
		initClassVariables();

		if (isStrongMode) {
			initStrongMode();
		} else if (isMethodWeakMode) {
			initStrongMode();
		} else {
			/* Expression-level weak mutation */
			initExpressionWeakMode();
		}
	}

	private void disableChangePoint(int changePoint) {
		if (changePoint > 0 && changePoint < ExecuteChangePoint.length
				&& ExecuteChangePoint[changePoint]) {
			ExecuteChangePoint[changePoint] = false;
			// System.err.println("DISABLE CHANGE POINT " + changePoint);
		}
	}

	private void disableChangePoint(int changePoint, int subID) {
		// search mutant map
		BitSet subIDs = mutantMap.get(changePoint);
		if (subIDs == null) {
			// 이미 chnage point의 모든 sub mutant가 kill된 경우
			disableChangePoint(changePoint);

			return;
		}

		// change point를 update해서 중복된 change point에 대해서는 검사하지 않는다.
		subIDs.set(subID, false);
		if (subIDs.isEmpty()) {
			mutantMap.put(changePoint, null);

			disableChangePoint(changePoint);
		}
	}

	public XStream getXstream() {
		if (xstream == null)
			xstream = new XStream(new XppDomDriver());

		return xstream;
	}

	/**
	 * 객체 instance 를 초기화 하는 함수.
	 */
	private void initClassVariables() {
		subID = -1;
		mutationOperator = "";
		hashCode = -1;
	}

	/**
	 * 외부 변수에 따라 ExecutionMode를 결정한다.
	 * <p>
	 * 값에 따라 isStrongMode, isMethodWeakMode, isExpWeakMode 중 하나의 값만 true로 변경된다.
	 * 
	 */
	private void initExecutionMode() {

		int mode = nujava.NuJavaHelper.StrongMutationMode;

		String value = System.getenv(nujava.NuJavaHelper.header_mutationMode);
		if (value == null) {
			value = System.getProperty(nujava.NuJavaHelper.header_mutationMode);
		}

		try {

			if (value != null) {
				mode = Integer.parseInt(value);
			}

			// 지정된 mode가 아닌 경우는 강제로 Strong mutation mode로 사용한다.
			if (!(mode == nujava.NuJavaHelper.MethodWeakMutationMode
					|| mode == nujava.NuJavaHelper.ExpressionWeakMutationMode || mode == nujava.NuJavaHelper.ConditonalMutualMutantMode)) {

				mode = nujava.NuJavaHelper.StrongMutationMode;
			}
		} catch (NumberFormatException e) {
			// mode 기본값으로 Strong으로 생성되어 있으므로 아무런 작업을 수행하지 않음.
		}

		switch (mode) {
		case nujava.NuJavaHelper.StrongMutationMode:
			isStrongMode = true;
			// System.err.println("STRONG MODE");
			break;
		case nujava.NuJavaHelper.MethodWeakMutationMode:
			isMethodWeakMode = true;
			// System.err.println("METHOD-WEAK MODE");
			break;
		case nujava.NuJavaHelper.ExpressionWeakMutationMode:
			isExpWeakMode = true;
			// System.err.println("EXP-WEAK MODE");
			break;
		case nujava.NuJavaHelper.ConditonalMutualMutantMode:
			isExpWeakMode = true;
			isConditionalMutualMutantMode = true;
			// System.err.println("CM MODE");
			break;
		}
	}

	/**
	 * Serial-mutant와 연동하기 위한 최기화 함수.
	 * 
	 */
	private void initExpressionWeakMode() {

		// Obtain mutant IDs
		String fileName = System
				.getenv(nujava.NuJavaHelper.header_liveMutantFileName);
		if (fileName == null) {
			fileName = System
					.getProperty(nujava.NuJavaHelper.header_liveMutantFileName);
		}

		String killFileName = System
				.getenv(nujava.NuJavaHelper.header_killedMutantFileName);
		if (killFileName == null) {
			killFileName = System
					.getProperty(nujava.NuJavaHelper.header_killedMutantFileName);
		}

		MutantMonitor.resultFileName = killFileName;
		// System.err.println("<<WEAK MUTATION FILE SETTING>>" + killFileName);

		try {
			FileInputStream fis = new FileInputStream(fileName);
			FileChannel fic = fis.getChannel();
			ByteBuffer buf = ByteBuffer.allocateDirect((int) fic.size());
			fic.read(buf);
			buf.rewind();

			List<Integer> sideEffectChangePoints = new ArrayList<Integer>();
			List<String> liveMutantIDs = new ArrayList<String>();

			// HAS_SIDE_EFFECT_CHANGEPOINT +
			// SIZE(n1) + ( CHANGE POINT ) * n1
			// SIZE(n2) + (STR_SIZE(n3) + STR_DATA[n3] ) * n2
			int sizeOfSideEffectValues = buf.getInt();
			for (int i = 0; i < sizeOfSideEffectValues; i++) {
				int chpt = buf.getInt();
				sideEffectChangePoints.add(chpt);
			}

			int mutantIDsize = buf.getInt();
			for (int i = 0; i < mutantIDsize; i++) {

				int len = buf.getInt();
				char[] buff = new char[len];
				for (int j = 0; j < len; j++) {
					buff[j] = buf.getChar();
				}
				String id = new String(buff);
				int fromIndex = id.indexOf("_", id.indexOf("_") + 1) + 1;
				String newID = id.substring(fromIndex);
				liveMutantIDs.add(newID);
			}

			fic.close();
			fis.close();
			fis = null;

			for (String id : liveMutantIDs) {
				StringTokenizer st = new StringTokenizer(id, "_");
				if (st.countTokens() == 2) {
					int changePoint = Integer.parseInt(st.nextToken());
					BitSet ids = mutantMap.get(changePoint);
					if (ids == null) {
						ids = new BitSet();
						mutantMap.put(changePoint, ids);
					}

					int subID = Integer.parseInt(st.nextToken());
					ids.set(subID);

					// System.err.println("SET CHANGE POINT 1 [" + changePoint
					// + "," + ids.toString() + "]");
				}
			}

			// 시험 대상의 ChangePoint를 설정한다.
			for (int chpt : mutantMap.keySet()) {
				if (chpt > 0 && chpt < ExecuteChangePoint.length) {
					ExecuteChangePoint[chpt] = true;
					// System.err.println("SET CHANGE POINT 2: " + chpt);
				}
			}

			// Side-Effect ChangePoint를 설정한다.
			for (int chpt : sideEffectChangePoints) {
				if (chpt > 0 && chpt < SideEffectChangePoint.length) {
					SideEffectChangePoint[chpt] = true;
					// System.err.println("SET SIDE EFFECT CHANGE POINT 3: "
					// + chpt);
				}
			}

		} catch (Exception e) {

		}
	}

	/**
	 * Strong mutation을 수행하기 위한 초기화 함수.
	 */
	private void initStrongMode() {

		String mutantID = System
				.getenv(nujava.NuJavaHelper.header_MSG_mutantID);
		if (mutantID == null) {
			mutantID = System
					.getProperty(nujava.NuJavaHelper.header_MSG_mutantID);
		}

		int changePoint = -1;
		int index = -1;

		StringTokenizer st = new StringTokenizer(mutantID, "_");
		if (st.hasMoreTokens())
			this.mutationOperator = st.nextToken();
		if (st.hasMoreTokens())
			this.hashCode = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens()) {
			changePoint = Integer.parseInt(st.nextToken());
		}
		if (st.hasMoreTokens()) {
			index = Integer.parseInt(st.nextToken());
		}

		if (changePoint > 0 && changePoint < ExecuteChangePoint.length) {
			ExecuteChangePoint[changePoint] = true;
			// System.err.println("SET STRONG CHANGE POINT" + changePoint);
		}

		if (index >= 0) {
			subID = index;
			// System.err.println("SET STRONG SUB INDEX" + index);
		}
	}

	// JTD에서 사용.
	public boolean isExpWeak() {
		return isExpWeakMode;
	}

	protected boolean isMethodWeak() {
		return isMethodWeakMode;
	}

	protected boolean isStrong() {
		return isStrongMode;
	}

	private boolean isUpdatableMutant(boolean isSingleMutant, int changePoint,
			int subID) {

		if (isSingleMutant) {
			return true;
		}

		// search mutant map
		BitSet subIDs = mutantMap.get(changePoint);
		if (subIDs == null || subIDs.isEmpty()) {
			// 검사 대상 ID가 없는 경우는 더이상 수행하지 않는다.
			disableChangePoint(changePoint, subID);

			return false;
		}

		return subIDs.get(subID);
	}

	private void updateMutants(List<InternalStructure> results, long time,
			boolean shouldReportTime, boolean shouldEliminateMutant) {

		if (!MutantMonitor.isExpWeakMode) {
			return;
		}

		List<InternalStructure> reportedMutants = new ArrayList<InternalStructure>();

		for (InternalStructure mutant : results) {

			// verify invalid mutant identifier
			if (mutant.changePoint == -1 || mutant.subID == -1) {
				continue;
			}

			if (isUpdatableMutant(false, mutant.changePoint, mutant.subID)) {

				reportedMutants.add(mutant);

				// System.out.println("UPDATE CHANGE POINT : "
				// + mutant.changePoint);
				// System.out.println("UPDATE SUB ID : " + mutant.subID);

				if (shouldEliminateMutant) {
					// update change point flags
					disableChangePoint(mutant.changePoint, mutant.subID);
				}
			}
		}

		if (reportedMutants.isEmpty()) {
			return;
		}

		if (shouldReportTime) {
			// 시간 보고하는 weak mutation은 mutant끼리의 값을 확인하기 위해
			// change point를 update하지 않는다.
			writeWeakMutantID(reportedMutants, time);
		} else {
			// ID를 보고한다.
			writeWeakMutantID(reportedMutants);
		}

	}

	/**
	 * Update change point bit array
	 * 
	 * @param mID
	 * @param time
	 * @param mutantResult
	 * @param isSingleMutant
	 * @param shouldReportTime
	 * @param shouldEliminateMutant
	 */
	private void updateMutant(String mID, int changePoint, int subID,
			long time, String mutantResult, boolean isSingleMutant,
			boolean shouldReportTime, boolean shouldEliminateMutant) {

		if (!MutantMonitor.isExpWeakMode) {
			return;
		}

		// verify invalid mutant identifier
		if (changePoint == -1 || subID == -1) {
			return;
		}

		if (isUpdatableMutant(isSingleMutant, changePoint, subID)) {

			if (shouldReportTime) {
				// 시간 보고하는 weak mutation은 mutant끼리의 값을 확인하기 위해
				// change point를 update하지 않는다.
				writeWeakMutantID(mID, mutantResult, time);
			} else {
				// ID를 보고한다.
				writeWeakMutantID(mID);
			}

			if (shouldEliminateMutant) {
				if (isSingleMutant) {
					disableChangePoint(changePoint);
				} else {
					// update change point flags
					disableChangePoint(changePoint, subID);
				}
			}
		}

	}

	public void updateMutants(List<InternalStructure> results, long time) {
		updateMutants(results, time,
				MutantMonitor.isConditionalMutualMutantMode,
				!MutantMonitor.isConditionalMutualMutantMode);
	}

	/**
	 * <p>
	 * CM 모드는 weak mutant에 대해 제거하지 않고, WS모드에서만 weak mutant를 제거한다.
	 * <p>
	 * 단, change point가 하나는 아니다. LOR, AORS, AOIS
	 */
	public void updateNonSingleMutant(String mID, int changepoint, int subID) {
		updateMutant(mID, changepoint, subID, 0, "", false, false, true);
	}

	/**
	 * CM 모드는 weak mutant에 대해 제거하지 않고, WS모드에서만 weak mutant를 제거한다.
	 * 
	 * @param mID
	 * @param time
	 * @param mutantResult
	 */
	public void updateNonSingleMutant(String mID, int changepoint, int subID,
			long time, String mutantResult) {
		updateMutant(mID, changepoint, subID, time, mutantResult, false,
				MutantMonitor.isConditionalMutualMutantMode,
				!MutantMonitor.isConditionalMutualMutantMode);
	}

	/**
	 * CM/WS 모드 관계 없이 한번 보고하고, 중복 수행을 제지한다.
	 * 
	 * AODS, AOID, BOID, LOID
	 * 
	 * @param mID
	 */
	public void updateSingleMutant(String mID, int changepoint) {
		updateMutant(mID, changepoint, 0, 0, "", true, false, true);
	}

	/**
	 * Weakly Killed Mutant ID 만 기록.
	 */
	private void writeWeakMutantID(String mutantID) {

		// System.err.println("WRITE WEAKLY KILLED MUTANT " + mutantID);

		String fileName = MutantMonitor.resultFileName;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}

		try {
			// write
			FileOutputStream fos = new FileOutputStream(fileName, true);
			FileChannel foc = fos.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// write header
			buf.putInt(HEADER_ID_ONLY);

			// write length of MutantID string
			char[] content = mutantID.toCharArray();

			buf.putInt(content.length);
			for (int i = 0; i < content.length; i++) {
				buf.putChar(content[i]);
			}

			buf.flip();

			foc.write(buf);
			foc.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Weakly Killed Mutant ID 만 기록.
	 */
	private void writeWeakMutantID(List<InternalStructure> mutants) {

		// System.err.println("WRITE WEAKLY KILLED MUTANT " + mutantID);

		String fileName = MutantMonitor.resultFileName;
		if (fileName == null || fileName.isEmpty()) {
			return;
		}

		try {
			// write
			FileOutputStream fos = new FileOutputStream(fileName, true);
			FileChannel foc = fos.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);

			for (InternalStructure mutant : mutants) {
				// write header
				buf.putInt(HEADER_ID_ONLY);

				// write length of MutantID string
				char[] content = mutant.mutantID.toCharArray();

				buf.putInt(content.length);
				for (int i = 0; i < content.length; i++) {
					buf.putChar(content[i]);
				}

				buf.flip();
				foc.write(buf);

				buf.clear();
			}

			foc.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Weakly Killed Mutant ID 와 result, 생성 시간 을 기록
	 * 
	 * <p>
	 * NIO 사용.
	 * 
	 * @param mutantID
	 * @param time
	 * @param mutantResult
	 */
	private void writeWeakMutantID(String mutantID, String mutantResult,
			long time) {

		String fileName = MutantMonitor.resultFileName;

		if (fileName == null || fileName.isEmpty()) {
			return;
		}

		// System.err.println("WRITE WEAKLY KILLED MUTANT WITH TIME " +
		// mutantID);

		try {

			// write
			FileOutputStream fos = new FileOutputStream(fileName, true);
			FileChannel foc = fos.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// write header
			buf.putInt(HEADER_REGULAR);

			// write length of MutantID string
			char[] content = mutantID.toCharArray();
			buf.putInt(content.length);
			for (int i = 0; i < content.length; i++) {
				buf.putChar(content[i]);
			}

			// write length of mutated value
			content = mutantResult.toCharArray();
			buf.putInt(content.length);
			for (int i = 0; i < content.length; i++) {
				buf.putChar(content[i]);
			}

			// write time
			buf.putLong(time);

			buf.flip();
			foc.write(buf);
			foc.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeWeakMutantID(List<InternalStructure> mutants, long time) {

		String fileName = MutantMonitor.resultFileName;

		if (fileName == null || fileName.isEmpty()) {
			return;
		}

		// System.err.println("WRITE WEAKLY KILLED MUTANT WITH TIME " +
		// mutantID);

		try {

			// write
			FileOutputStream fos = new FileOutputStream(fileName, true);
			FileChannel foc = fos.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);

			for (InternalStructure mutant : mutants) {

				// write header
				buf.putInt(HEADER_REGULAR);

				// write length of MutantID string
				char[] content = mutant.mutantID.toCharArray();
				buf.putInt(content.length);
				for (int i = 0; i < content.length; i++) {
					buf.putChar(content[i]);
				}

				// write length of mutated value
				content = mutant.result.toCharArray();
				buf.putInt(content.length);
				for (int i = 0; i < content.length; i++) {
					buf.putChar(content[i]);
				}

				// write time
				buf.putLong(time);
			}

			buf.flip();
			foc.write(buf);

			foc.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

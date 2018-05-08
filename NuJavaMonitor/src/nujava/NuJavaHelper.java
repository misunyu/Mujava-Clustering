package nujava;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NuJavaHelper {
	public static final int MAX_STATE = 10;

	public static final int StrongMutationMode = 0;
	public static final int MethodWeakMutationMode = 1;
	public static final int ExpressionWeakMutationMode = 2;
	public static final int ConditonalMutualMutantMode = 3;

	public static String header_mutantID = "nujava.mutantID";
	public static String header_testResult = "nujava.result.testMethod";
	public static String header_MSG_mutantID = "nujava.MSG.mutantID";
	public static String header_MSG_TargetClass = "nujava.target.class";
	public static String header_className = "nujava.class.name";
	public static String header_methodName = "nujava.method.name";
	public static String header_mutationMode = "nujava.mutationMode";
	public static String header_liveMutantFileName = "nujava.file.liveMutants";
	public static String header_preStateFileName = "nujava.file.preState";
	public static String header_clientPreStateFileName = "nujava.file.client.preState";
	public static String header_killedMutantFileName = "nujava.file.killedMutants";
	public static String header_reachedMutantFileName = "nujava.file.reachedMutants";
	public static byte[] header_state = new byte[] { 0xF, 0xE, 0xd, 0xc };

	/**
	 * 주어진 ID의 mutant를 수행할지를 알려준다.
	 * 
	 * @param id
	 *            mutant id without its mutant type
	 * @return
	 */
	public static boolean isGivenID(String mutantID, int changePointID) {
		return MutantMonitor.ExecuteChangePoint[changePointID];
	}

	public static String makeValidString(String str) {
		String newStr = new String(str.getBytes());
		return newStr;
	}

	public static boolean isMethodWeakMode() {
		return MutantMonitor.getInstance().isMethodWeak();
	}

	public static boolean isExpWeakMode() {
		return MutantMonitor.getInstance().isExpWeak();
	}

	public static boolean isStrongMode() {
		return MutantMonitor.getInstance().isStrong();
	}

	public static void writeString(FileOutputStream fos, String str)
			throws IOException {
		byte[] buf = str.getBytes();
		writeInt(fos, buf.length);
		fos.write(buf, 0, buf.length);
	}

	public static String readString(FileInputStream fis) throws IOException {
		int size = readInt(fis);
		if (size <= 0) {
			return "";
		}

		byte[] buf = new byte[size];
		fis.read(buf);
		return new String(buf);
	}

	public static void writeInt(OutputStream os, int value) throws IOException {
		byte[] dword = new byte[4];
		dword[0] = (byte) ((value >> 24) & 0xFF);
		dword[1] = (byte) ((value >> 16) & 0xFF);
		dword[2] = (byte) ((value >> 8) & 0xFF);
		dword[3] = (byte) (value & 0xFF);
		os.write(dword, 0, 4);
	}

	public static int readInt(InputStream fis) throws IOException {
		byte[] buffer = new byte[4];

		fis.read(buffer, 0, 4);

		return (int) buffer[0] << 24 | (int) buffer[1] << 16
				| (int) buffer[2] << 8 | (int) buffer[3] & 0xff;
	}

	public static void writeBoolean(FileOutputStream fos, boolean value)
			throws IOException {
		if (value)
			fos.write(0x1);
		else
			fos.write(0x0);
	}

	public static boolean readBoolean(FileInputStream fis) throws IOException {
		int value = fis.read();

		if (value == 0)
			return false;
		else
			return true;
	}

	public static void writeLong(FileOutputStream fos, long time) {

	}
}

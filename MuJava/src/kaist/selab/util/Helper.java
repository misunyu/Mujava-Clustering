package kaist.selab.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import openjava.ptree.ClassDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;

/**
 * 
 * @author erlip
 * @version 1.0
 */

public class Helper {
	private static DecimalFormat spaceFormat = new DecimalFormat("####0.##");

	static Helper helper = null;

	public static String directorySeparator = File.separator;

	public static String newline = System.getProperty("line.separator");

	public final static MethodDeclaration OutOfMethod = new MethodDeclaration(
			new ModifierList(), null, "", null, null, new StatementList());

	public static final ClassDeclaration OutOfClass = new ClassDeclaration(
			new ModifierList(), new String(), new TypeName[] {},
			new TypeName[] {}, new MemberDeclarationList());
	public static String pathSeparate = System.getProperty("path.separator");

	public static void copyFile(File srcFile, String root, String packageName)
			throws IOException {
		File desFile = new File(root + packageName);
		MuJavaLogger.getLogger().info(
				"Copy " + srcFile.toString() + " to " + desFile.toString()
						+ ".");

		File parent = desFile.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}

		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(desFile);

		int b = fis.read();
		while (b >= 0) {
			fos.write(b);
			b = fis.read();
		}
		fos.flush();
		fos.close();
	}

	public static void copyFile(String srcFile, String des) throws IOException {
		File desFile = new File(des);

		desFile.mkdirs();

		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(desFile);

		int b = fis.read();
		while (b >= 0) {
			fos.write(b);
			b = fis.read();
		}
		fos.flush();
		fos.close();
	}

	public static String double2SpaceString(double space) {
		double value = space / (1024 * 1024);
		if (value < 1)
			return spaceFormat.format(1.0 * space / 1024) + " KB";
		value = space / (1024 * 1024 * 1024);
		if (value < 1)
			return spaceFormat.format(1.0 * space / (1024 * 1024)) + " MB";
		value = space / (1024 * 1024 * 1024 * 1024);
		if (value < 1)
			return spaceFormat.format(1.0 * space / (1024 * 1024 * 1024))
					+ " GB";

		return Double.toString(space) + " Byte";
	}

	public static String double2TimeString(double time) {
		StringBuffer buf = new StringBuffer();
		buf.append(" (ms) ");
		double value = time - 1000 * ((long) time / 1000);
		buf.insert(0, spaceFormat.format(value));
		time = (time - value) / 1000;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " sec ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " min ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " hour ");
		buf.insert(0, time % 24);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " day ");
		buf.insert(0, time % 365);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " month ");
		buf.insert(0, time % 365);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		buf.insert(0, " year ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time < 1)
			return buf.toString();

		return buf.toString();
	}

	public static Helper getHelper() {
		return helper;
	}

	public static int getIntValueFromProperties(Properties prop, String key) {
		String value = prop.getProperty(key);
		if (value == null || value.length() == 0)
			return 0;
		return Integer.parseInt(value);
	}

	public static long getLongValueFromProperties(Properties prop, String key) {
		String value = prop.getProperty(key);
		if (value == null || value.length() == 0)
			return 0;
		return Long.parseLong(value);
	}

	public static boolean getBooleanValueFromProperties(Properties prop,
			String key) {
		String value = prop.getProperty(key);
		if (value == null || value.length() == 0)
			return false;

		return Boolean.parseBoolean(value);
	}

	public static String getValueFromProperties(Properties prop, String key) {
		String value = prop.getProperty(key);
		if (value == null || value.length() == 0)
			return new String();
		return value;
	}

	public static String long2DateString(long startTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startTime);
		return DateFormat.getDateTimeInstance().format(c.getTime());
	}

	public static String long2SpaceString(long space) {
		double value = 1.0 * space / (1024 * 1024);
		if (value < 1)
			return spaceFormat.format(1.0 * space / 1024) + " KB";
		value = 1.0 * space / (1024 * 1024 * 1024);
		if (value < 1)
			return spaceFormat.format(1.0 * space / (1024 * 1024)) + " MB";
		value = 1.0 * space / (1024 * 1024 * 1024) / 1024;
		if (value < 1)
			return spaceFormat.format(1.0 * space / (1024 * 1024 * 1024))
					+ " GB";

		return Long.toString(space) + " Byte";
	}

	public static String long2TimeString(long time) {
		time = time / 1000000; // nano to milli
		StringBuffer buf = new StringBuffer();
		buf.append(" (ms) ");
		buf.insert(0, (int) time % 1000);
		time = time / 1000;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " sec ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " min ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " hour ");
		buf.insert(0, time % 24);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " day ");
		buf.insert(0, time % 365);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " month ");
		buf.insert(0, time % 365);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		buf.insert(0, " year ");
		buf.insert(0, time % 60);
		time = time / 60;
		if (time <= 0)
			return buf.toString();

		return buf.toString();
	}

	// public static void setCenterLocation(Component dlg) {
	// // Center the window
	// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	// Dimension size = dlg.getSize();
	// if (size.height > screenSize.height) {
	// size.height = screenSize.height;
	// }
	// if (size.width > screenSize.width) {
	// size.width = screenSize.width;
	// }
	// dlg.setLocation((screenSize.width - size.width) / 2,
	// (screenSize.height - size.height) / 2);
	// }
	public static void unionStringList(List<String> toList,
			List<String> fromList) {
		for (String mutant : fromList) {
			if (!toList.contains(mutant))
				toList.add(mutant);
		}
	}

	public static int factorial(int n) {
		int f = 1;
		if (n > 0)
			for (int i = 1; i <= n; i++)
				f = f * i;

		return f;
	}

	public static int permutation(int n, int r) {
		if (n == 0)
			return 1;
		long top = factorial(n);
		long bottom = factorial(n - r);
		return (int) (top / bottom);
	}

	/**
	 * Sum all permutation value (n,1) to (n, r).
	 * 
	 * @param n
	 * @param r
	 * @return
	 */
	public static int sigmaPermutation(int n, int r) {
		int sum = 0;
		for (int i = 0; i < r; i++)
			sum = sum + permutation(n, i);

		return sum;
	}
	// Frame frame = null;
	//	
	// public Helper(Frame frame) {
	// this.frame = frame;
	// helper = this;
	// }
	//
	// public JFileChooser getFileChooser() {
	// JFileChooser ch = new JFileChooser();
	//
	// // Center the window
	// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	// Dimension frameSize = ch.getSize();
	// if (frameSize.height > screenSize.height) {
	// frameSize.height = screenSize.height;
	// }
	// if (frameSize.width > screenSize.width) {
	// frameSize.width = screenSize.width;
	// }
	// ch.setLocation((screenSize.width - frameSize.width) / 2,
	// (screenSize.height - frameSize.height) / 2);
	//
	// return ch;
	// }

	// public Frame getMainFrame() {
	// return frame;
	// }
}

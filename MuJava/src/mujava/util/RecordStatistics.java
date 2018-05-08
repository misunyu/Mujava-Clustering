package mujava.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import mujava.MutantOperator;
import mujava.MutantTable;

public abstract class RecordStatistics {

	public static final int REACH = 1;
	public static final int WEAKLY_KILLEd = 2;
	public static final int STRONGLY_KILLED = 3;
	public static final int GROUP_IGNORED = 4;
	public static final int GROUP_KILLED = 5;

	public static String getTestCaseID_old(String testCaseSimpleName) {
		StringTokenizer st = new StringTokenizer(testCaseSimpleName, ".");
		if (st.countTokens() < 2) {
			throw new NullPointerException("TestCase Name is invalid");
		}

		String testCaseID = "";
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.endsWith("()")) {
				return testCaseID;
			} else {
				testCaseID = token;
			}
		}
		return null;
	}

	public static String getTestCaseID(String testCaseSimpleName) {
		StringTokenizer st = new StringTokenizer(testCaseSimpleName, ".");
		if (st.countTokens() < 2) {
			throw new NullPointerException("TestCase Name is invalid");
		}

		//String testCaseID = "";
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.endsWith("()")) {
				return token.substring(0,token.length()-"()".length());
			}// else {
			//	testCaseID = token;
			//}
		}
		return null;
	}
	
	public static String getTestClassID(String testCaseSimpleName) {
		StringTokenizer st = new StringTokenizer(testCaseSimpleName, ".");
		if (st.countTokens() < 2) {
			throw new NullPointerException("TestCase Name is invalid");
		}

		String previousStr = "";
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.endsWith("()")) {
				//return token.substring(0,token.length()-"()".length());
				return previousStr;
			} else {
				previousStr = token;
			}
		}
		return null;
	}
		
	List<String> testCases = new ArrayList<String>();

	Map<String, Set<String>> mutantMapByOperator = new HashMap<String, Set<String>>();
	Map<String, Set<String>> weakMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> reachMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> strongMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> groupMap = new HashMap<String, Set<String>>();

	Map<String, Set<String>> groupStrongMap = new HashMap<String, Set<String>>();

	protected CellStyle summaryStyle;

	protected CellStyle headerStyle;

	protected Comparator<String> testCaseIDComparator = new Comparator<String>() {

		@Override
		public int compare(String arg0, String arg1) {

			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0) {
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try {
					value1 = Integer.parseInt(integerStr1);
				} catch (NumberFormatException e) {
				}

				int value2 = 0;
				try {
					value2 = Integer.parseInt(integerStr2);
				} catch (NumberFormatException e) {
				}

				return value1 - value2;
			}

			return diff;
		}

		private String getStringHeader(String str) {
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++) {
				char ch = str.charAt(i);
				if (Character.isDigit(ch)) {
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}
	};

	protected Comparator<String> mutantIDComparator = new Comparator<String>() {

		@Override
		public int compare(String arg0, String arg1) {

			String[] argArray0 = arg0.split("_");
			String[] argArray1 = arg1.split("_");

			long diff = argArray0.length - argArray1.length;
			if (diff == 0) {
				for (int i = 0; i < argArray0.length; i++) {
					String element0 = argArray0[i];
					String element1 = argArray1[i];

					try {
						int value1 = Integer.parseInt(element0);
						int value2 = Integer.parseInt(element1);

						diff = value1 - value2;
					} catch (NumberFormatException e) {
						diff = element0.length() - element1.length();
						if (diff != 0) {
							return diff < 0 ? -1 : 1;
						}
						diff = element0.compareTo(element1);
					}

					if (diff != 0) {
						return diff < 0 ? -1 : 1;
					}
				}
			}

			return diff < 0 ? -1 : 1;
		}
	};

	protected void drawRangeBorder(Row row, int lastColumn, int columnSize) {
		drawRangeBorder(row, lastColumn, 1, columnSize);
	}

	protected void drawRangeBorder(Row lastRow, int lastColumn, int rowSize,
			int columnSize) {

		if (rowSize > 0) {
			Sheet sheet = lastRow.getSheet();

			int beginRowIndex = lastRow.getRowNum() - (rowSize - 1);
			int endRowIndex = lastRow.getRowNum();
			int beginColumnIndex = lastColumn - (columnSize - 1);
			int endColumnIndex = lastColumn;

			CellRangeAddress region = new CellRangeAddress(beginRowIndex,
					endRowIndex, beginColumnIndex, endColumnIndex);
			RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region, sheet,
					sheet.getWorkbook());
			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region, sheet,
					sheet.getWorkbook());
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region, sheet,
					sheet.getWorkbook());
			RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region, sheet,
					sheet.getWorkbook());
		}
	}

	protected List<String> getSortedIDs(Set<String> ids) {

		List<String> sortedIDs = new ArrayList<String>(ids);
		Collections.sort(sortedIDs, testCaseIDComparator);

		return sortedIDs;
	}

	protected void prepareCellStyle(Workbook wb) {

		summaryStyle = wb.createCellStyle();
		summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN
				.getIndex());
		summaryStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		headerStyle = wb.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE
				.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}

	void put(Map<String, Set<String>> map, String testCaseID, String mutantID) {

		Set<String> list = map.get(testCaseID);
		if (list == null) {
			list = new HashSet<String>();
			map.put(testCaseID, list);
		}

		list.add(mutantID);

		String op = mujava.MutantID.getMutationOperator(mutantID);
		Set<String> mutants = mutantMapByOperator.get(op);
		if (mutants == null) {
			mutants = new HashSet<String>();
			mutantMapByOperator.put(op, mutants);
		}
		mutants.add(mutantID);
	}

	public void putMutants(MutantTable mutantTable) {
		List<String> ops = MutantOperator.getAllTraditionalOperators();
		Collections.sort(ops);

		for (String op : ops) {
			Set<String> mutants = mutantMapByOperator.get(op);

			if (mutants == null) {
				mutants = new HashSet<String>();
			}

			Set<String> mutantIDs = mutantTable.getMutantIDs(op);
			mutants.addAll(mutantIDs);

			mutantMapByOperator.put(op, mutants);
		}
	}

	public void reach(int type, String testCaseID, String mutantID) {
		switch (type) {
		case REACH:
			put(reachMap, testCaseID, mutantID);
			break;
		case WEAKLY_KILLEd:
			put(weakMap, testCaseID, mutantID);
			break;
		case STRONGLY_KILLED:
			put(strongMap, testCaseID, mutantID);
			break;
		case GROUP_IGNORED:
			put(groupMap, testCaseID, mutantID);
			break;
		case GROUP_KILLED:
			put(groupStrongMap, testCaseID, mutantID);
			break;
		}
	}

	public void setTestCase(Collection<String> testCases) {
		this.testCases.clear();

		if (testCases != null && !testCases.isEmpty()) {
			this.testCases.addAll(testCases);
			Collections.sort(this.testCases, testCaseIDComparator);
		}
	}

	protected String toSimpleMutantID(String mutantID) {
		String subString = mutantID;
		int index = 0;
		for (int i = 0; i < 2; i++) {
			index = subString.indexOf("_");
			subString = subString.substring(index + 1);
		}

		return subString;
	}

	abstract public void writeExcel(File outputFile, SourceTable sourceTable,
			EquivalentTable equTable, List<String> mutationOperators)
			throws FileNotFoundException;
}

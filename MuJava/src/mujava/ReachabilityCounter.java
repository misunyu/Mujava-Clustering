package mujava;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import kaist.selab.util.Helper;

public class ReachabilityCounter {
	Hashtable<String, String> testMethodMap = new Hashtable<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 3) {
			System.err.println("ERROR - Not Executed");
			System.exit(-1);
		}

		String tableFileName = args[0];
		String logFileName = args[1];
		String outputCSVFileName = args[2];

		try {
			FileInputStream loggerStream = new FileInputStream(logFileName);
			FileOutputStream fos = new FileOutputStream(outputCSVFileName);

			ReachabilityCounter counter = new ReachabilityCounter(
					tableFileName, loggerStream, fos);

			counter.init();
			counter.count();
			counter.outputOverall();
			counter.outputPerFile();
			counter.close();

			fos.flush();
			fos.close();
			fos = null;

			loggerStream.close();
			loggerStream = null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String tableFileName;
	private BufferedReader reader;
	private Writer writer;
	private Hashtable<String, Set<String>> reachIDs = new Hashtable<String, Set<String>>();
	private ArrayList<String> totalMutantID = new ArrayList<String>();
	private Hashtable<Integer, Hashtable<String, Set<String>>> obtainedAllHashCodeMap = new Hashtable<Integer, Hashtable<String, Set<String>>>();
	private Hashtable<String, Hashtable<Integer, Hashtable<String, Set<String>>>> obtainedReachedHashCodeMap = new Hashtable<String, Hashtable<Integer, Hashtable<String, Set<String>>>>();

	public ReachabilityCounter(String tableFileName, InputStream loggerStream,
			OutputStream fos) {
		this.tableFileName = tableFileName;
		this.reader = new BufferedReader(new InputStreamReader(loggerStream));
		this.writer = new OutputStreamWriter(fos);
	}

	void count() {

		try {
			String line = "";
			String tcID = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}

				int index = line.indexOf("[REACH]");
				if (index == -1) {
					continue;
				} else {
					line = line.substring(index);
				}

				String testCaseTxt = "[REACH] TestCase:";
				String listTxt = "[REACH] List:";
				if (line.startsWith(testCaseTxt)) {
					String tempStr = line.substring(testCaseTxt.length());
					tcID = getTCID(tempStr);
				} else if (line.startsWith(listTxt)) {
					String tempStr = line.substring(listTxt.length());
					List<String> list = readList(tempStr);
					if (tcID != null && !tcID.isEmpty()) {
						addMutantIDs(tcID, list);
						tcID = "";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// reach한 mutant들에 대해 동일한 작업 수행
		for (String tcID : this.reachIDs.keySet()) {
			Set<String> mutantIDs = reachIDs.get(tcID);

			Hashtable<Integer, Hashtable<String, Set<String>>> tablePerTC = null;
			if (obtainedReachedHashCodeMap.containsKey(tcID)) {
				tablePerTC = obtainedReachedHashCodeMap.get(tcID);
			} else {
				tablePerTC = new Hashtable<Integer, Hashtable<String, Set<String>>>();
				obtainedReachedHashCodeMap.put(tcID, tablePerTC);
			}

			for (String mutantID : mutantIDs) {
				int hashCode = MutantID.getHashCode(mutantID);

				Hashtable<String, Set<String>> table = null;
				if (tablePerTC.containsKey(hashCode)) {
					table = tablePerTC.get(hashCode);
				} else {
					table = new Hashtable<String, Set<String>>();
					tablePerTC.put(hashCode, table);
				}

				String op = MutantID.getMutationOperator(mutantID);
				Set<String> set = table.get(op);
				if (set == null) {
					set = new HashSet<String>();
					table.put(op, set);
				}
				set.add(mutantID);
			}
		}

	}

	private void addMutantIDs(String tcID, List<String> list) {
		Set<String> retList = reachIDs.get(tcID);

		if (retList == null) {
			retList = new HashSet<String>();
			reachIDs.put(tcID, retList);
		}

		retList.addAll(list);
	}

	List<String> readList(String lineStr) {
		List<String> retList = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(lineStr, "[],");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = token.trim();
			if (!token.isEmpty())
				retList.add(token);
		}

		return retList;
	}

	private String getTCID(String testMethodName) {

		for (String tcID : testMethodMap.keySet()) {
			String name = testMethodMap.get(tcID);
			if (name.equalsIgnoreCase(testMethodName)) {
				return tcID;
			}
		}

		String newTcID = "TC" + (testMethodMap.size() + 1);
		testMethodMap.put(newTcID, testMethodName);

		return newTcID;
	}

	private void init() {
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(tableFileName);
			prop.loadFromXML(fis);
			fis.close();

			this.totalMutantID.clear();

			List<String> ops = MutantOperator
					.getAllTraditionalOperators();
			for (String op : ops) {
				totalMutantID.addAll(getTableMutantIDs(prop, op));
			}

			// 1단계 수집 : 파일별 mutant ID 수집
			obtainedAllHashCodeMap.clear();

			// 전체 mutant중 각각의 파일에 대해 OP별로 수집
			for (String mutantID : totalMutantID) {
				int hashCode = MutantID.getHashCode(mutantID);

				Hashtable<String, Set<String>> table = null;
				if (obtainedAllHashCodeMap.containsKey(hashCode)) {
					table = obtainedAllHashCodeMap.get(hashCode);
				} else {
					table = new Hashtable<String, Set<String>>();
					obtainedAllHashCodeMap.put(hashCode, table);
				}

				String op = MutantID.getMutationOperator(mutantID);
				Set<String> mutantIDs = table.get(op);
				if (mutantIDs == null) {
					mutantIDs = new HashSet<String>();
					table.put(op, mutantIDs);
				}

				mutantIDs.add(mutantID);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	List<String> getSortedTestCaseID() {
		List<String> keys = new ArrayList<String>(testMethodMap.keySet());
		Comparator<String> strComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int diff = o1.length() - o2.length();
				if (diff != 0)
					return diff;

				return o1.compareTo(o2);
			}

		};
		Collections.sort(keys, strComparator);

		return keys;
	}

	void outputPerFile() {
		// Printing header
		System.out.println("<< Per File >>");
		System.out.println("TC ,      , MutantID, ,      , Files");
		System.out.print("ID , name , ");

		Hashtable<Integer, String> hashCodeMap = new Hashtable<Integer, String>();
		int count = 0;
		for (Integer hashCode : obtainedAllHashCodeMap.keySet()) {
			String name = "FILE" + count++;
			System.out.print(name + ", , ,");
			hashCodeMap.put(hashCode, name);
		}
		System.out.println();

		List<String> tcIDs = getSortedTestCaseID();
		for (String tcID : tcIDs) {
			Hashtable<Integer, Hashtable<String, Set<String>>> reachList = obtainedReachedHashCodeMap
					.get(tcID);
			System.out.print(tcID + ", " + testMethodMap.get(tcID) + ", ");
			for (Integer hashCode : hashCodeMap.keySet()) {
				int reachCount = 0;
				Hashtable<String, Set<String>> opTable = reachList
						.get(hashCode);
				if (opTable == null) {
					reachCount += 0;
				} else {
					for (String op : opTable.keySet()) {
						Set<String> set = opTable.get(op);
						reachCount += set.size();
					}
				}

				System.out.print(reachCount + ", ");

				int allCount = 0;
				Hashtable<String, Set<String>> fileTable = obtainedAllHashCodeMap
						.get(hashCode);
				for (String op : fileTable.keySet()) {
					Set<String> set = fileTable.get(op);
					allCount += set.size();
				}

				System.out.print(allCount + ",  ,");
			}
			System.out.println();
		}
	}

	void outputOverall() {
		// Printing header
		System.out.println("<< Overall >>");
		System.out.println("TC ,      , MutantID,       ");
		System.out.println("ID , name , Reached , Total ");

		Set<String> allReachList = new HashSet<String>();

		List<String> tcIDs = getSortedTestCaseID();
		for (String tcID : tcIDs) {
			Set<String> reachList = reachIDs.get(tcID);

			System.out.println(tcID + ", " + testMethodMap.get(tcID) + ", "
					+ reachList.size() + ", " + this.totalMutantID.size());

			allReachList.addAll(reachList);
		}

		System.out.println("total,  , " + allReachList.size() + ", "
				+ totalMutantID.size());
	}

	private void close() {
		if (this.reader != null) {
			try {
				reader.close();
				reader = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.writer != null) {
			try {
				writer.flush();
				writer.close();
				writer = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Hashtable<String, String> getTestIdTables() {
		return this.testMethodMap;
	}

	public Set<String> getMutantIDs(String key) {
		return this.reachIDs.get(key);
	}

	private List<String> getTableMutantIDs(Properties prop, String operator) {
		ArrayList<String> list = new ArrayList<String>();

		int length = Helper.getIntValueFromProperties(prop, operator);
		for (int i = 0; i < length; i++) {
			String mutantID = Helper.getValueFromProperties(prop, operator
					+ String.valueOf(i + 1));
			if (!list.contains(mutantID))
				list.add(mutantID);
		}

		return list;
	}
}

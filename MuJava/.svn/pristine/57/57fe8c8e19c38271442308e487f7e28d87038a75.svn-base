package mujava.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import kaist.selab.util.MuJavaLogger;
import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantTable;
import mujava.util.ReachTableGenerator.Level;

public class ReachTable
{

	private static final int MAX_TEST_SIZE = 15;

	/**
	 * 저장된 ReachTable을 읽어들인다.
	 * 
	 * MuJava Plug-in 에서만 사용된다.
	 * 
	 * @param fileName
	 * @return
	 */
	public static ReachTable load(String fileName)
	{

		ReachTable table = new ReachTable();
		table.open(fileName);

		return table;
	}

	Map<String, Set<String>> reachedMutantMap = new HashMap<String, Set<String>>();
	Map<String, Set<String>> mutantMap = new HashMap<String, Set<String>>();

	private boolean isEmpty = true;

	Comparator<String> testCaseIDComparator = new Comparator<String>()
	{

		@Override
		public int compare(String arg0, String arg1)
		{

			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0)
			{
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try
				{
					value1 = Integer.parseInt(integerStr1);
				}
				catch (NumberFormatException e)
				{
				}

				int value2 = 0;
				try
				{
					value2 = Integer.parseInt(integerStr2);
				}
				catch (NumberFormatException e)
				{
				}

				return value1 - value2;
			}

			return diff;
		}

		private String getStringHeader(String str)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++)
			{
				char ch = str.charAt(i);
				if (Character.isDigit(ch))
				{
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}
	};

	Comparator<String> mutantIDComparator = new Comparator<String>()
	{

		@Override
		public int compare(String arg0, String arg1)
		{

			String[] argArray0 = arg0.split("_");
			String[] argArray1 = arg1.split("_");

			int diff = argArray0.length - argArray1.length;
			if (diff == 0)
			{
				for (int i = 0; i < argArray0.length; i++)
				{
					String element0 = argArray0[i];
					String element1 = argArray1[i];

					try
					{
						int value1 = Integer.parseInt(element0);
						int value2 = Integer.parseInt(element1);

						diff = value1 - value2;
					}
					catch (NumberFormatException e)
					{
						diff = element0.length() - element1.length();
						if (diff != 0)
						{
							return diff;
						}
						diff = element0.compareTo(element1);
					}

					if (diff != 0)
					{
						return diff;
					}
				}
			}

			return diff;
		}
	};

	public void appendLogFile(File logFile)
	{

		BufferedReader br = null;

		try
		{
			FileInputStream is = new FileInputStream(logFile);
			br = new BufferedReader(new InputStreamReader(is));
		}
		catch (FileNotFoundException e1)
		{
			System.out.println("Error on " + logFile.getAbsolutePath()
					+ "(NOT EXIST)");
			return;
		}

		int lineCounter = 0;
		String line = "";
		String testCaseID = "";

		try
		{
			while ((line = br.readLine()) != null)
			{
				// Line Count
				lineCounter++;

				// Clean a line string
				line = line.trim();
				if (line.isEmpty())
				{
					continue;
				}

				// Grab only string after "[Reach]" tag
				int index = line.indexOf("[REACH]");
				if (index == -1)
				{
					continue;
				}
				else
				{
					line = line.substring(index).trim();
				}

				String testCaseHeader = "[REACH] TestCase:";
				//String testCaseHeader = "[REACH] TestCase:java.lang.String";
				String listHeader1 = "[REACH] List:,";

				if (line.startsWith(testCaseHeader))
				{
					String tempStr = "";
					if(line.indexOf("java.lang.String")>0){
						tempStr = line.substring( "[REACH] TestCase:java.lang.String".length());
					}else{
						tempStr = line.substring(testCaseHeader.length());
						
					}
					//String tempStr = line.substring(testCaseHeader.length());
					testCaseID = getTestCaseID(tempStr.trim());

					Set<String> list = reachedMutantMap.get(testCaseID);
					if (list == null)
					{
						list = new HashSet<String>();
						reachedMutantMap.put(testCaseID, list);
					}
				}
				else if (line.startsWith(listHeader1))
				{
					String mutantID = line.substring(listHeader1.length())
							.trim();
					// System.out.println("TEMP : " + testCaseID + " : "
					// + mutantID);

					Set<String> list = reachedMutantMap.get(testCaseID);
					if (list == null)
					{
						throw new NullPointerException();
					}

					list.add(mutantID);
					reachedMutantMap.put(testCaseID, list);

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMap.get(op);
					if (mutants == null)
					{
						mutants = new HashSet<String>();
					}
					mutants.add(mutantID);
					mutantMap.put(op, mutants);
				}
			}

		}
		catch (IOException e)
		{
			System.out.println("Error on " + logFile + " (LINE " + lineCounter
					+ ")");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private Map<String, Set<String>> getAllLeveledMutants(
			MutantTable mutantTable, Level option)
	{

		// Key : ClassName or ClassName+MethodName
		Map<String, Set<String>> targetMutants = new HashMap<String, Set<String>>();

		if (option != Level.PATH)
		{

			for (String mutantID : mutantTable.getAllMutantID())
			{
				MuJavaMutantInfo mInfo = mutantTable.getMutantInfo(mutantID);

				String id = mInfo.getParentClassName() + ":"
						+ mInfo.getParentMethodName();

				if (option == Level.CLASS)
				{
					id = mInfo.getParentClassName() + ":";
				}

				Set<String> mutantIDs = targetMutants.get(id);
				if (mutantIDs == null)
				{
					mutantIDs = new HashSet<String>();
					targetMutants.put(id, mutantIDs);
				}

				mutantIDs.add(mutantID);
			}
		}

		return targetMutants;
	}

	/**
	 * 주어진 Method Signature에서 Test Caes ID를 생성
	 * 
	 * @param name
	 *            Class 및 함수 표시 이름
	 * @return
	 */
	private String getTestCaseID(String name)
	{
		// 문자열 정리
		name = name.trim();
		name = name.replace(" ", "");

		// 이름 형식 확인
		StringTokenizer st = new StringTokenizer(name, ".");
		if (st.countTokens() < 2)
		{
			throw new NullPointerException("TestCase Name is invalid");
		}

		// 함수 이름 중 괄호 제거
		name = name.replace("()", "");

		return name;
	}

	/**
	 * Indicate whether this reach table is not initialized.
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return isEmpty;
	}

	/**
	 * 
	 * @param testID
	 *            "T"로 시작하는 Index
	 * @param mutantID
	 * @return
	 */
	public boolean isReachedMutant(String testID, String mutantID)
	{
		Set<String> mutantIDs = reachedMutantMap.get(testID);

		if (mutantIDs != null)
		{
			return mutantIDs.contains(mutantID);
		}

		return false;
	}

	private void open(String fileName)
	{
		BufferedReader br = null;

		try
		{
			FileInputStream is = new FileInputStream(new java.io.File(fileName));
			br = new BufferedReader(new InputStreamReader(is));
		}
		catch (FileNotFoundException e1)
		{
			MuJavaLogger.getLogger().debug(
					"Can not load ReachTable " + fileName);
		}

		int lineCounter = 0;
		String line = "";

		try
		{
			while ((line = br.readLine()) != null)
			{
				// Line Count
				lineCounter++;

				// Clean a line string
				line = line.trim();
				if (line.isEmpty())
				{
					continue;
				}

				StringTokenizer st = new StringTokenizer(line, ":");
				if (st.countTokens() < 2)
				{
					continue;
				}

				String testCaseID = st.nextToken();
				String mutantID = st.nextToken();

				// TestCase별로 저장
				Set<String> mutants = reachedMutantMap.get(testCaseID);
				if (mutants == null)
				{
					mutants = new HashSet<String>();
					reachedMutantMap.put(testCaseID, mutants);
				}
				mutants.add(mutantID);

				// Operator별로 저장
				String op = mujava.MutantID.getMutationOperator(mutantID);
				mutants = mutantMap.get(op);
				if (mutants == null)
				{
					mutants = new HashSet<String>();
					mutantMap.put(op, mutants);
				}

				mutants.add(mutantID);
			}

			isEmpty = false;
		}
		catch (IOException e)
		{

			MuJavaLogger.getLogger().debug(
					"Error on " + fileName + " (LINE " + lineCounter + ")");
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private String toSimpleMutantID(String mutantID)
	{
		String subString = mutantID;
		int index = 0;
		for (int i = 0; i < 2; i++)
		{
			index = subString.indexOf("_");
			subString = subString.substring(index + 1);
		}

		return subString;
	}

	public void writeCSVFile(File outputFile)
	{
		BufferedWriter writer = null;

		try
		{
			FileOutputStream fos = new FileOutputStream(outputFile);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error on writing csv text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

		try
		{
			ArrayList<String> sortedKeys = new ArrayList<String>(
					reachedMutantMap.keySet());
			Collections.sort(sortedKeys, testCaseIDComparator);

			for (String mutationOperator : mutantMap.keySet())
			{
				writer.newLine();
				writer.append(mutationOperator);
				writer.append(",");

				for (String testCase : sortedKeys)
				{
					writer.append(testCase);
					writer.append(",");
					writer.append(",");
					writer.append(",");
					writer.append(",");
				}
				writer.newLine();

				writer.append(",");
				for (int i = 0; i < sortedKeys.size(); i++)
				{
					writer.append("R,");
					writer.append("W,");
					writer.append("S,");
					writer.append("G,");
				}
				writer.newLine();

				Set<String> mutants = mutantMap.get(mutationOperator);

				List<String> sortedMutantIDs = new ArrayList<String>(mutants);
				Collections.sort(sortedMutantIDs, mutantIDComparator);

				for (String mutantID : sortedMutantIDs)
				{
					String simpleMutantID = toSimpleMutantID(mutantID);
					writer.append(simpleMutantID);
					writer.append(",");

					for (String key : sortedKeys)
					{
						Set<String> reachMutants = reachedMutantMap.get(key);
						if (reachMutants.contains(mutantID))
						{
							writer.append(simpleMutantID);
							writer.append(",");
						}
						else
						{
							writer.append(",");
						}
						writer.append(",");
						writer.append(",");
						writer.append(",");
					}
					writer.newLine();

				}

			}

			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println("Error on writing text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

	}

	public void writeExcel(File outputFile)
	{

	}

	public void writeTextFile(File outputFile, MutantTable mutantTable,
			Level scopeLevel)
	{
		BufferedWriter writer = null;

		try
		{
			FileOutputStream fos = new FileOutputStream(outputFile, false);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Error on writing text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

		// 전체 Mutant 를 확보
		Map<String, Set<String>> targetMutants = getAllLeveledMutants(
				mutantTable, scopeLevel);

		// 목록 대상 별 key 정렬
		ArrayList<String> keys = new ArrayList<String>(
				reachedMutantMap.keySet());
		Collections.sort(keys, testCaseIDComparator);

		try
		{

			for (String key : keys)
			{

				Set<String> reachedMutantIDs = new HashSet<String>(
						reachedMutantMap.get(key));

				Set<String> leveledReachedMutantIDs = getLevelReachedMutants(
						reachedMutantIDs, scopeLevel, mutantTable,
						targetMutants);

				List<String> targetMutantIDs = new ArrayList<String>(
						leveledReachedMutantIDs);
				Collections.sort(targetMutantIDs, mutantIDComparator);

				// Write those mutants
				for (String targetMutantID : targetMutantIDs)
				{
					writer.append(key);
					writer.append(":");
					writer.append(targetMutantID);
					writer.newLine();
				}
			}

			writer.flush(); 
			writer.close();
		}
		catch (IOException e)
		{
			System.out.println("Error on writing text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}
	}

	private Set<String> getLevelReachedMutants(Set<String> givenMutantIDs,
			Level scopeLevel, MutantTable refMutantTable,
			Map<String, Set<String>> keyMappedMutantIDs)
	{
		Set<String> resultIDs = new HashSet<String>();

		for (String mutantID : givenMutantIDs)
		{

			MuJavaMutantInfo mInfo = refMutantTable.getMutantInfo(mutantID);

			String operator = MutantID.getMutationOperator(mutantID);

			String key = getReachTableKey(scopeLevel, mInfo);

			// 전체 Mutant 목록에서 동일 Key에서 생성된 mutant를 뽑는다.
			Set<String> mutants = getTargetMutants(keyMappedMutantIDs,
					mutantID, key, operator);

			resultIDs.addAll(mutants);
		}

		return resultIDs;
	}

	/**
	 * reach 된 mutant 들의 동일한 부모 함수, 또는 Class를 가지는 mutant 들을 반환한다.
	 * 
	 * @param scopeLevel
	 * @param mInfo
	 * @return PATH는 empty string 반환
	 */
	private String getReachTableKey(Level scopeLevel, MuJavaMutantInfo mInfo)
	{
		switch (scopeLevel)
		{
		case CLASS:
			return mInfo.getParentClassName() + ":";

		case METHOD:
			return mInfo.getParentClassName() + ":"
					+ mInfo.getParentMethodName();

		case PATH:
			// DO NOTHING
			break;
		}

		return "";
	}

	private Set<String> getTargetMutants(
			Map<String, Set<String>> keyMappedMutants, String givenMutantID,
			String key, String operator)
	{
		if (keyMappedMutants == null)
		{
			return Collections.<String> emptySet();
		}

		Set<String> targetMutants = new HashSet<String>();
		targetMutants.add(givenMutantID);

		if (key == null || key.isEmpty())
		{
			return targetMutants;
		}

		// 발생 불가 시험
		Set<String> mutants = keyMappedMutants.get(key);

		assert (mutants != null && !mutants.isEmpty());

		for (String mutantID : mutants)
		{
			// 주어진 Mutant Operator 와 다른 종류의 Mutant는 고려하지 않는다.
			String targetOperator = MutantID.getMutationOperator(mutantID);
			if (targetOperator == null || targetOperator.isEmpty())
			{
				continue;
			}

			// 서로 다른 mutation operator에서 생성된 mutant에 대해서는 고려 안함
			if (targetOperator.equals(operator))
			{
				targetMutants.add(mutantID);
			}
		}

		return targetMutants;
	}
}

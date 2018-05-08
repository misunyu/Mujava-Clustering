package mujava.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import mujava.MutantID;

public class YSourceTable {

	class InnerSource {
		public String label;
		public String fileName;
	}

	Map<Integer, InnerSource> table = new HashMap<Integer, InnerSource>();

	public void parseTargetFile(File file) {
		BufferedReader br = null;

		try {
			FileInputStream is = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(is));
		} catch (FileNotFoundException e1) {
			System.out.println("Error on " + file.getAbsolutePath()
					+ "(NOT EXIST)");
			return;
		}

		int lineCounter = 0;
		String line = "";

		try {
			while ((line = br.readLine()) != null) {
				// Line Count
				lineCounter++;

				// Clean a line string
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}

				StringTokenizer st = new StringTokenizer(line, " \t\n\r");
				if (st.countTokens() < 3) {
					throw new NullPointerException();
				}

				// Label
				InnerSource source = new InnerSource();
				source.label = "F"+lineCounter;
				source.fileName = st.nextToken();
				source.fileName = source.fileName.substring(0,source.fileName.length()-".java".length());
				while (st.hasMoreTokens()) {
					try {
						int value = Integer.parseInt(st.nextToken());
						table.put(value, source);
					} catch (NumberFormatException e) {
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error on " + file.getAbsolutePath() + " (LINE "
					+ lineCounter + ")");
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Set<String>> groupMutants(Set<String> mutants) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		// 모든 Source에 대한 사전 초기화 작업을 수행한다.
		Set<String> names = new HashSet<String>();
		for (Integer key : table.keySet()) {
			InnerSource source = table.get(key);
			names.add(source.label);
		}

		for (String name : names) {
			map.put(name, new HashSet<String>());
		}

		// mutant를 나누어 구분한다.
		for (String mutantID : mutants) {
			int hashCode = MutantID.getHashCode(mutantID);
			InnerSource source = table.get(hashCode);

			if (source != null) {
				Set<String> dividedMutants = map.get(source.label);
				if (dividedMutants == null) {
					dividedMutants = new HashSet<String>();
					map.put(source.label, dividedMutants);
				}

				dividedMutants.add(mutantID);
			}
		}

		return map;
	}

	public Set<Integer> getHashCodes(String fileID) {
		Set<Integer> hashcodes = new HashSet<Integer>();

		for (int hashcode : table.keySet()) {
			InnerSource source = table.get(hashcode);
			if (source.label.equals(fileID)) {
				hashcodes.add(hashcode);
			}
		}

		return hashcodes;
	}

	public static SourceTable load(String hashCodeFileName) {
		SourceTable sourceTable = new SourceTable();
		sourceTable.parseTargetFile(new File(hashCodeFileName));

		return sourceTable;
	}
}

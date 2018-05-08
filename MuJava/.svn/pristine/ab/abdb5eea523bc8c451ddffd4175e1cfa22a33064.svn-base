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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import mujava.CodeSet;
import mujava.MuJavaMutantInfo;
import mujava.MutantOperator;
import mujava.MutantTable;

public class TimeoutTable {

	Map<String, Set<String>> mutantMapByOperator = new HashMap<String, Set<String>>();
	Comparator<String> mutantIDComparator = new Comparator<String>() {

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

	public void writeCSVFile(File outputFile, MutantTable mutantTable,
			SourceTable sourceTable, String codeSetPath) {
		BufferedWriter writer = null;

		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (FileNotFoundException e) {
			System.out.println("Error on writing csv text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

		try {
			List<String> ops = MutantOperator.getAllTraditionalOperators();
			Collections.sort(ops);

			for (String mutationOperator : ops) {
				writer.newLine();

				writer.append(mutationOperator);
				writer.append(",");

				Set<String> mutants = mutantMapByOperator.get(mutationOperator);
				writer.append(Integer.toString(mutants.size()));
				writer.append(",");

				// mutant를 구분한다.
				List<String> sortedMutants = new ArrayList<String>(mutants);
				Collections.sort(sortedMutants, mutantIDComparator);

				IPath loc = new Path(codeSetPath);

				int index = 1;
				for (String mutantID : sortedMutants) {
					if (index != 1) {
						writer.append(",");
						writer.append(",");
					}

					writer.append("TO" + index++);
					writer.append(",");

					writer.append(mutantID);
					writer.append(",");

					MuJavaMutantInfo mInfo = mutantTable
							.getMutantInfo(mutantID);
					writer.append(Integer.toString(mInfo.getChangeLocation()));
					writer.append(",");

					writer.append(mInfo.getChangeLog());

					CodeSet codeSet = mujava.CodeSet.getCodeSet(loc,
							mInfo.getCodeID());
					for (String name : codeSet.getRelativePathNames()) {
						if (codeSet.getTargetFile().endsWith(name)) {

						}
					}
				}
				writer.newLine();
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Error on writing text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}
	}

	private String toSimpleMutantID(String mutantID) {
		String subString = mutantID;
		int index = 0;
		for (int i = 0; i < 2; i++) {
			index = subString.indexOf("_");
			subString = subString.substring(index + 1);
		}

		return subString;
	}

	public void putMutants(MutantTable mutantTable) {
		List<String> ops = MutantOperator.getAllTraditionalOperators();

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

	public void appendLogFile(String logFile) {
		BufferedReader br = null;

		try {
			FileInputStream is = new FileInputStream(logFile);
			br = new BufferedReader(new InputStreamReader(is));
		} catch (FileNotFoundException e1) {
			System.out.println("Error on " + logFile + "(NOT EXIST)");
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

				// Grab only string after "[Reach]" tag
				int index = line.indexOf("Timeout Happended");
				if (index == -1) {
					continue;
				} else {
					line = line.substring(index).trim();
				}

				String timeoutHeader = "Timeout Happended";

				if (line.startsWith(timeoutHeader)) {
					String tempStr = line.substring(timeoutHeader.length());
					String mutantID = tempStr.trim();

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMapByOperator.get(op);
					if (mutants == null) {
						mutants = new HashSet<String>();
						mutantMapByOperator.put(op, mutants);
					}
					mutants.add(mutantID);
				}
			}
		} catch (IOException e) {
			System.out.println("Error on " + logFile + " (LINE " + lineCounter
					+ ")");
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

}

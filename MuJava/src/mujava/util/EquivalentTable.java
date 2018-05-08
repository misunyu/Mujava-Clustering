package mujava.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

//import com.sun.org.apache.bcel.internal.classfile.SourceFile;

import mujava.MutantID;

//import sun.java2d.pipe.NullPipe;

public class EquivalentTable {

	Set<String> table = new HashSet<String>();

	public void parseTargetFile(File file, SourceTable sourceTable) {
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

				StringTokenizer st = new StringTokenizer(line, ": \t\n\r");
				if (st.countTokens() < 3) {
					throw new NullPointerException();
				}

				String fileID = st.nextToken();
				String operator = st.nextToken();
				String changePoint = st.nextToken();
				int typeID = -1;
				try {
					typeID = Integer.parseInt(st.nextToken());
				} catch (NumberFormatException e) {
				}

				Set<Integer> hashcodes = sourceTable.getHashCodes(fileID);
				for (Integer hashcode : hashcodes) {
					MutantID mutantID = MutantID.generateMutantID(operator,
							hashcode, changePoint);
					mutantID.setLastIndex(typeID);

					table.add(mutantID.toString());
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

	public boolean isEquivalent(String mutantID) {
		return table.contains(mutantID);
	}
}

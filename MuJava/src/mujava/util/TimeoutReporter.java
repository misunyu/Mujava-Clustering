package mujava.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mujava.MutantTable;

import org.eclipse.core.runtime.CoreException;

public class TimeoutReporter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Arguments are Missing!!!");
			System.exit(-1);
		}

		String logFile = args[0];
		String targetFile = args[1]; // hashcode table
		String tableFile = args[2];

		// Create Weak-ability Table
		TimeoutReporter generator = new TimeoutReporter();
		TimeoutTable toTable = generator.parseLogFile(logFile);

		// Target File 로딩
		SourceTable sourceTable = generator.parseTargetFile(targetFile);

		MutantTable mutantTable = null;
		try {
			mutantTable = MutantTable.getMutantTable(tableFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		String codeSetPath = new File(tableFile).getParent();

		// Create XML file
		File outputFile = makeOutputFile("TimeoutMutant", "csv");
		System.out.println("Timeout Report (" + outputFile.getAbsolutePath()
				+ ") is Creating...");
		toTable.writeCSVFile(outputFile, mutantTable, sourceTable, codeSetPath);
		System.out.println("Strong Table is Created");
	}

	private EquivalentTable parseEquFile(String file, SourceTable sourceTable) {

		EquivalentTable table = new EquivalentTable();
		table.parseTargetFile(new File(file), sourceTable);

		return table;
	}

	private SourceTable parseTargetFile(String targetFile) {

		SourceTable table = new SourceTable();
		table.parseTargetFile(new File(targetFile));

		return table;
	}

	private TimeoutTable parseLogFile(String logFile) {

		TimeoutTable table = new TimeoutTable();

		table.appendLogFile(logFile);

		return table;
	}

	private static File makeOutputFile(String header, String ext) {
		StringBuffer sb = new StringBuffer(header);

		int counter = 0;
		while (true) {
			sb.append(".");
			sb.append(ext);
			File file = new File(sb.toString());
			if (!file.exists()) {
				return file;
			}

			counter++;
			sb.setLength(header.length());
			sb.append(counter);
		}
	}

}

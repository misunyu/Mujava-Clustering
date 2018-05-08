package mujava.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mujava.MutantOperator;
import mujava.MutantTable;

import org.eclipse.core.runtime.CoreException;

public class KilledTableGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 3) {
			System.out.println("Arguments are Missing!!!");
			System.exit(-1);
		}

		String logFile = args[0];
		String targetFile = args[1];
		String tableFile = args[2];
		String eqTableFile = "";

		if (args.length > 3) {
			eqTableFile = args[3];
		}

		// Create Weak-ability Table
		KilledTableGenerator generator = new KilledTableGenerator();
		KilledMutantTable weakTable = generator
				.parseLogFile(logFile, tableFile);
		ExecutionCountTable countTable = generator.parseCountLogFile(logFile,
				tableFile);

		// Target File 로딩
		SourceTable sourceTable = generator.parseTargetFile(targetFile);
		EquivalentTable equTable = generator.parseEquFile(eqTableFile,
				sourceTable);

		// Create XML file
		// CSV 타입으로
		// File outputFile = makeOutputFile("ReachedLog", "csv");
		// weakTable.writeCSVFile(outputFile, sourceTable, equTable);

		File outputFile = makeOutputFile("ReachLog", "xlsx");
		System.out.println("Killed Mutant Table ("
				+ outputFile.getAbsolutePath() + ") is Creating...");
		// try {
		// weakTable.writeExcel(outputFile, sourceTable, equTable);
		// } catch (FileNotFoundException e) {
		// System.out.println("Error on writing csv text file ("
		// + outputFile.getAbsolutePath() + ")");
		// return;
		// }

		outputFile = makeOutputFile("CountLog", "xlsx");
		System.out.println("Reached Count Table ("
				+ outputFile.getAbsolutePath() + ") is Creating...");
		List<String> ops = MutantOperator.getAllTraditionalOperators();
		countTable.writeExcel(outputFile, sourceTable, equTable, ops);

		System.out.println("Strong Table is Created");
	}

	private EquivalentTable parseEquFile(String file, SourceTable sourceTable) {
		EquivalentTable table = new EquivalentTable();
		if (!file.isEmpty()) {
			table.parseTargetFile(new File(file), sourceTable);
		}

		return table;
	}

	private SourceTable parseTargetFile(String targetFile) {

		SourceTable table = new SourceTable();
		table.parseTargetFile(new File(targetFile));

		return table;
	}

	private KilledMutantTable parseLogFile(String logFile, String tableFile) {

		KilledMutantTable table = new KilledMutantTable();

		MutantTable mutantTable = null;
		try {
			mutantTable = MutantTable.getMutantTable(tableFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// mutant 정보로 삽입
		table.putMutants(mutantTable);

		table.appendLogFile(logFile);

		return table;
	}

	private ExecutionCountTable parseCountLogFile(String logFile,
			String tableFile) {

		ExecutionCountTable table = new ExecutionCountTable();

		MutantTable mutantTable = null;
		try {
			mutantTable = MutantTable.getMutantTable(tableFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// mutant 정보로 삽입
		table.putMutants(mutantTable);

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

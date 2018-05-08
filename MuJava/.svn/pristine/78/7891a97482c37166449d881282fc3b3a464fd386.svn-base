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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mujava.MutantOperator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class KilledMutantTable extends RecordStatistics {

	Map<String, Map<String, List<CellReference>>> summaryReferenceTables = new HashMap<String, Map<String, List<CellReference>>>();

	private int rowIndex;

	private CellStyle summaryStyle;

	private String operator;

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
		String testCaseID = "";

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
				int index = line.indexOf("[REACH]");
				if (index == -1) {
					continue;
				} else {
					line = line.substring(index).trim();
				}

				String testCaseHeader = "[REACH] TestCase:java.lang.String";
				String weakKilledHeader = "[REACH] WK:";
				String strongKilledHeader = "[REACH] SK:";
				String groupHeader = "[REACH] GRP:";
				String groupStrongHeader = "[REACH] GK:";

				if (line.startsWith(testCaseHeader)) {
					String tempStr = line.substring(testCaseHeader.length());
					testCaseID = getTestCaseID(tempStr.trim());

					Set<String> list = weakMap.get(testCaseID);
					if (list == null) {
						list = new HashSet<String>();
						weakMap.put(testCaseID, list);
					}

					list = strongMap.get(testCaseID);
					if (list == null) {
						list = new HashSet<String>();
						strongMap.put(testCaseID, list);
					}

					list = groupMap.get(testCaseID);
					if (list == null) {
						list = new HashSet<String>();
						groupMap.put(testCaseID, list);
					}

					list = groupStrongMap.get(testCaseID);
					if (list == null) {
						list = new HashSet<String>();
						groupStrongMap.put(testCaseID, list);
					}
				} else if (line.startsWith(weakKilledHeader)) {
					String mutantID = line.substring(weakKilledHeader.length())
							.trim();

					Set<String> list = weakMap.get(testCaseID);
					if (list == null) {
						throw new NullPointerException();
					}

					list.add(mutantID);
					weakMap.put(testCaseID, list);

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMapByOperator.get(op);
					if (mutants == null) {
						mutants = new HashSet<String>();
						mutantMapByOperator.put(op, mutants);
					}
					mutants.add(mutantID);
				} else if (line.startsWith(strongKilledHeader)) {
					String mutantID = line.substring(
							strongKilledHeader.length()).trim();

					Set<String> list = strongMap.get(testCaseID);
					if (list == null) {
						throw new NullPointerException();
					}

					list.add(mutantID);
					strongMap.put(testCaseID, list);

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMapByOperator.get(op);
					if (mutants == null) {
						mutants = new HashSet<String>();
						mutantMapByOperator.put(op, mutants);
					}
					mutants.add(mutantID);
				} else if (line.startsWith(groupHeader)) {
					String mutantID = line.substring(groupHeader.length())
							.trim();

					Set<String> list = groupMap.get(testCaseID);
					if (list == null) {
						throw new NullPointerException();
					}

					list.add(mutantID);
					groupMap.put(testCaseID, list);

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMapByOperator.get(op);
					if (mutants == null) {
						mutants = new HashSet<String>();
						mutantMapByOperator.put(op, mutants);
					}
					mutants.add(mutantID);
				} else if (line.startsWith(groupStrongHeader)) {
					String mutantID = line
							.substring(groupStrongHeader.length()).trim();

					Set<String> list = groupStrongMap.get(testCaseID);
					if (list == null) {
						throw new NullPointerException();
					}

					list.add(mutantID);
					groupStrongMap.put(testCaseID, list);

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

	private List<String> getMutantsBySource(String source,
			Map<String, Set<String>> sourceMap) {

		Set<String> mutants = sourceMap.get(source);
		List<String> sortedMutantIDs = new ArrayList<String>(mutants);
		Collections.sort(sortedMutantIDs, mutantIDComparator);

		return sortedMutantIDs;
	}

	private Map<String, Set<String>> getSourceMap(String mutationOperator,
			SourceTable sourceTable) {
		Set<String> mutants = mutantMapByOperator.get(mutationOperator);
		return sourceTable.groupMutants(mutants);
	}

	// private List<String> sortWeakMapKeys() {
	// ArrayList<String> sortedKeys = new ArrayList<String>(weakMap.keySet());
	//
	// Collections.sort(sortedKeys, testCaseIDComparator);
	//
	// return sortedKeys;
	// }

	public void writeCSVFile(File outputFile, SourceTable sourceTable,
			EquivalentTable equTable) {
		BufferedWriter writer = null;

		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (FileNotFoundException e) {
			System.out.println("Error on writing csv text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

		// List<String> sortedKeys = sortWeakMapKeys();

		List<String> ops = MutantOperator.getAllTraditionalOperators();
		Collections.sort(ops);

		try {

			for (String mutationOperator : ops) {
				writer.newLine();
				writer.append(mutationOperator);
				writer.append(",");
				writer.append(",");

				for (String testCaseID : testCases) {
					writer.append(testCaseID);
					writer.append(",");
					writer.append(",");
					writer.append(",");
					writer.append(",");
					writer.append(",");
				}
				writer.newLine();

				writer.append(",");
				writer.append(",");
				for (int i = 0; i < testCases.size(); i++) {
					writer.append("RE,");
					writer.append("WK,");
					writer.append("SK,");
					writer.append("GR,");
					writer.append("GK,");
				}
				writer.newLine();

				Set<String> mutants = mutantMapByOperator.get(mutationOperator);

				// source별로 mutant를 구분한다.
				Map<String, Set<String>> sourceMap = sourceTable
						.groupMutants(mutants);

				List<String> sources = new ArrayList<String>(sourceMap.keySet());
				Collections.sort(sources, testCaseIDComparator);

				for (String source : sources) {

					List<String> sortedMutantIDs = new ArrayList<String>(
							sourceMap.get(source));
					if (sortedMutantIDs.isEmpty()) {
						writer.newLine();
					}
					Collections.sort(sortedMutantIDs, mutantIDComparator);

					for (String mutantID : sortedMutantIDs) {
						String simpleMutantID = toSimpleMutantID(mutantID);
						writer.append(mutantID);
						writer.append(",");

						if (equTable.isEquivalent(mutantID)) {
							writer.append(simpleMutantID);
							writer.append(",");
							writer.newLine();
							continue;
						} else {
							writer.append(",");
						}

						for (String testCaseID : testCases) {

							// reach
							writer.append(",");

							// weak
							Set<String> weakMutants = weakMap.get(testCaseID);
							if (weakMutants != null
									&& weakMutants.contains(mutantID)) {
								writer.append(simpleMutantID);
								writer.append(",");
							} else {
								writer.append(",");
							}

							// strong
							Set<String> strongMutants = strongMap
									.get(testCaseID);
							if (strongMutants != null
									&& strongMutants.contains(mutantID)) {
								writer.append(simpleMutantID);
								writer.append(",");
							} else {
								writer.append(",");
							}

							// group
							Set<String> groupMutants = groupMap.get(testCaseID);
							if (groupMutants != null
									&& groupMutants.contains(mutantID)) {
								writer.append(simpleMutantID);
								writer.append(",");
							} else {
								writer.append(",");
							}

							// group killed
							Set<String> groupStrongMutants = groupStrongMap
									.get(testCaseID);
							if (groupStrongMutants != null
									&& groupStrongMutants.contains(mutantID)) {
								writer.append(simpleMutantID);
								writer.append(",");
							} else {
								writer.append(",");
							}
						}
						writer.newLine();
					}

					writer.append(source);
					writer.newLine();
				}
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Error on writing text file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}
	}

	private void writeEquivance(EquivalentTable equTable, String mutantID,
			int columnIndex, Row row) {

		if (equTable.isEquivalent(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);
			Cell eqCell = row.createCell(columnIndex);
			eqCell.setCellValue(simpleMutantID);
		}
	}

	public void writeExcel(File outputFile, SourceTable sourceTable,
			EquivalentTable equTable, List<String> mutationOperators)
			throws FileNotFoundException {

		FileOutputStream fos = new FileOutputStream(outputFile);

		Workbook wb = new SXSSFWorkbook(-1);
		prepareCellStyle(wb);

		try {
			Collections.sort(mutationOperators);

			for (String mutationOperator : mutationOperators) {
				operator = mutationOperator;
				writeMutationOperatorSheet(wb, sourceTable, equTable);
				Thread.sleep(100); // 메모리 오류를 없애기 위해서 삽입한 코드. 기다리는 동안 가비지 콜렉션이 일어나는 듯
			}

			writeSourceSummarySheet(wb);

			wb.write(fos);
			fos.close();
		} catch (IOException e) {
			System.out.println("Error on writing excel file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeSourceSummarySheet(Workbook wb) {

		rowIndex = 0;

		// make a sheet for each mutation operators
		Sheet sheet = wb.createSheet("Overall Files");
		wb.setSheetOrder(sheet.getSheetName(), 0);

		writeSourceSummarySheetHeader(sheet);
		int beginRow = rowIndex;
		writeSourceSummarySheetRow(sheet);
		writeSourceSummarySheetSummary(sheet, beginRow, rowIndex);
	}

	private void writeSourceSummarySheetRow(Sheet sheet) {

		List<String> sources = getSortedIDs(summaryReferenceTables.keySet());

		for (String source : sources) {
			Row row = sheet.createRow(rowIndex++);

			int columnIndex = 0;
			Cell cell = row.createCell(columnIndex++);
			cell.setCellValue(source);

			Map<String, List<CellReference>> referenceTable = summaryReferenceTables
					.get(source);
			if (referenceTable != null) {
				List<CellReference> refs = referenceTable.get("NUM");
				String exp = "SUM(" + getFomularParameters(refs) + ")";
				cell = row.createCell(columnIndex++);
				cell.setCellFormula(exp);

				refs = referenceTable.get("EQ");
				exp = "SUM(" + getFomularParameters(refs) + ")";
				cell = row.createCell(columnIndex++);
				cell.setCellFormula(exp);

				refs = referenceTable.get("OVERALL");
				for (int i = 0; i < 5; i++) {
					exp = "SUM(" + getFomularParameters(refs, i) + ")";
					cell = row.createCell(columnIndex++);
					cell.setCellFormula(exp);
				}
				drawRangeBorder(row, columnIndex - 1, 5);

				Set<String> keys = referenceTable.keySet();
				keys.remove("NUM");
				keys.remove("EQ");
				keys.remove("OVERALL");

				List<String> sortedTestCases = getSortedIDs(keys);

				for (String testCaseID : sortedTestCases) {
					refs = referenceTable.get(testCaseID);
					for (int i = 0; i < 5; i++) {
						exp = "SUM(" + getFomularParameters(refs, i) + ")";
						cell = row.createCell(columnIndex++);
						cell.setCellFormula(exp);
					}
				}

				if (sheet instanceof SXSSFSheet) {
					try {
						((SXSSFSheet) sheet).flushRows();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String getFomularParameters(List<CellReference> refs, int index) {
		String exp = "0";

		if (refs != null && refs.size() > 0) {

			StringBuffer sb = new StringBuffer();
			for (CellReference ref : refs) {
				if (index <= 0) {
					sb.append(ref.formatAsString());
				} else {
					CellReference newRef = new CellReference(
							ref.getSheetName(), ref.getRow(), ref.getCol()
									+ index, false, false);
					sb.append(newRef.formatAsString());
				}
				sb.append(",");
			}
			exp = sb.toString();
			if (exp.endsWith(",")) {
				exp = exp.substring(0, exp.length() - 1);
			}
		}

		return exp;
	}

	private String getFomularParameters(List<CellReference> refs) {
		return getFomularParameters(refs, 0);
	}

	private void writeSourceSummarySheetSummary(Sheet sheet, int beginRow,
			int endRow) {
		// for a Blank Line
		rowIndex++;

		// SUM ( values in this summary sheet)
		Row row = sheet.createRow(rowIndex++);
		int columnIndex = 0;

		Cell cell = row.createCell(columnIndex++);
		cell.setCellValue("TOTAL");

		if (beginRow < endRow) {

			// MUTANT
			cell = row.createCell(columnIndex++);
			String exp = getSimpleSumExpression(beginRow, endRow,
					cell.getColumnIndex());
			cell.setCellFormula(exp);
			cell.setCellStyle(headerStyle);
			drawRangeBorder(row, columnIndex - 1, 1);

			// EQ_MUTANT
			cell = row.createCell(columnIndex++);
			exp = getSimpleSumExpression(beginRow, endRow,
					cell.getColumnIndex());
			cell.setCellFormula(exp);
			cell.setCellStyle(headerStyle);
			drawRangeBorder(row, columnIndex - 1, 1);

			int sizeOfTestCases = testCases.size();
			for (int i = 0; i < sizeOfTestCases + 1; i++) { // (length + ALL) *
															// 5
				for (int j = 0; j < 5; j++) {
					cell = row.createCell(columnIndex++);
					exp = getSimpleSumExpression(beginRow, endRow,
							cell.getColumnIndex());
					cell.setCellFormula(exp);
					cell.setCellStyle(headerStyle);
				}
				drawRangeBorder(row, columnIndex - 1, 5);
			}
		}
	}

	private String getSimpleSumExpression(int beginRow, int endRow,
			int columnIndex) {

		CellReference fromCellRefer = new CellReference(beginRow, columnIndex);
		CellReference toCellRefer = new CellReference(endRow - 1, columnIndex);

		return "SUM(" + fromCellRefer.formatAsString() + ":"
				+ toCellRefer.formatAsString() + ")";
	}

	private void writeSourceSummarySheetHeader(Sheet sheet) {
		rowIndex++;

		// write fisrt row
		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		Cell cell = row.createCell(columnIndex++);
		cell.setCellValue("File");

		cell = row.createCell(columnIndex++);
		cell.setCellValue("Mutant");

		cell = row.createCell(columnIndex++);
		cell.setCellValue("EQ");

		int secondBeginColumn = columnIndex;
		cell = row.createCell(columnIndex);
		cell.setCellValue("ALL");
		columnIndex += 5;

		for (String testCaseID : testCases) {

			cell = row.createCell(columnIndex);
			cell.setCellValue(testCaseID);
			columnIndex += 5;
		}

		// write second row
		row = sheet.createRow(rowIndex++);
		columnIndex = secondBeginColumn;

		int sizeOfTestCases = testCases.size() + 1;
		for (int i = 0; i < sizeOfTestCases; i++) {
			columnIndex = secondBeginColumn + i * 5;
			writeTypeHeader(row, columnIndex);
		}
	}

	private void writeMutationOperatorSheet(Workbook wb,
			SourceTable sourceTable, EquivalentTable equTable) {

		rowIndex = 0;

		// make a sheet for each mutation operators
		Sheet sheet = wb.createSheet(operator);

		writeTestHeader(sheet);
		writeResultCategoryHeader(sheet);

		// source별로 mutant를 구분한다.
		Map<String, Set<String>> sourceMap = getSourceMap(operator, sourceTable);
		List<String> sources = getSortedIDs(sourceMap.keySet());
		List<Integer> rows = new ArrayList<Integer>();

		for (String source : sources) {
			List<String> mutants = getMutantsBySource(source, sourceMap);
			writeMutantsBySource(sheet, source, mutants, equTable);
			rows.add(rowIndex);
		}

		writeOverallSummary(sheet, rows);
	}

	private void writeGroupKilledMutant(Row row, int columnIndex,
			String mutantID, String testCaseID) {
		// group killed
		Set<String> groupStrongMutants = groupStrongMap.get(testCaseID);
		if (groupStrongMutants != null && groupStrongMutants.contains(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);

			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(simpleMutantID);
		}

	}

	private void writeGroupMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// group
		Set<String> groupMutants = groupMap.get(testCaseID);

		if (groupMutants != null && groupMutants.contains(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);

			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(simpleMutantID);
		}

	}

	private void writeMutantID(String mutantID, int columnIndex, Row row) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(mutantID);
	}

	private void writeMutantRow(Sheet sheet, String mutantID,
			EquivalentTable equTable) {

		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		writeMutantID(mutantID, columnIndex++, row);
		writeMutantID(toSimpleMutantID(mutantID), columnIndex++, row);
		writeEquivance(equTable, mutantID, columnIndex++, row);

		List<Integer> columns = new ArrayList<Integer>();
		for (String testCaseID : testCases) {
			columns.add(columnIndex);

			writeResultByTest(mutantID, testCaseID, columnIndex, row);
			columnIndex += 5;
		}

		writeMutantSummary(columns, columnIndex++, row);
	}

	private void writeMutantSummary(List<Integer> columns, int columnIndex,
			Row row) {

		for (int i = 0; i < 5; i++) {
			Cell cell = row.createCell(columnIndex++);

			StringBuffer sb = new StringBuffer();
			for (int resultColumnIndex : columns) {
				CellReference cellRef = new CellReference(row.getRowNum(),
						resultColumnIndex + i);
				sb.append(cellRef.formatAsString());
				sb.append(",");
			}
			String params = sb.toString();
			if (params.endsWith(",")) {
				params = params.substring(0, params.length() - 1);
			}

			cell.setCellFormula("IF(COUNTA(" + params + ")>0, 1, 0)");
		}
	}

	private void writeMutantsBySource(Sheet sheet, String source,
			List<String> mutants, EquivalentTable equTable) {

		int beginRow = rowIndex;
		for (String mutantID : mutants) {
			writeMutantRow(sheet, mutantID, equTable);
		}

		if (mutants.isEmpty()) {
			rowIndex++;
		}

		writeSourceSummaryRow(sheet, source, beginRow, mutants.size());

		if (sheet instanceof SXSSFSheet) {
			try {
				((SXSSFSheet) sheet).flushRows();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeOverallSummary(Sheet sheet, List<Integer> summaryRows) {

		rowIndex++;
		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		Cell nameCell = row.createCell(columnIndex++);
		nameCell.setCellValue("OVERALL");
		nameCell.setCellStyle(summaryStyle);

		writeColumnSummary(summaryRows, row, columnIndex++);
		drawSummaryBorder(row, columnIndex - 1, 1);
		writeColumnSummary(summaryRows, row, columnIndex++);
		drawSummaryBorder(row, columnIndex - 1, 1);

		int sizeOfTestCase = testCases.size();
		for (int i = 0; i <= sizeOfTestCase; i++) {
			for (int j = 0; j < 5; j++) {
				writeColumnSummary(summaryRows, row, columnIndex++);
			}
			drawSummaryBorder(row, columnIndex - 1, 5);
		}
	}

	private void writeColumnSummary(List<Integer> summaryRows, Row row,
			int columnIndex) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellStyle(summaryStyle);

		StringBuffer sb = new StringBuffer();
		for (int summaryRow : summaryRows) {
			CellReference cellRef = new CellReference(summaryRow - 1,
					cell.getColumnIndex());
			sb.append(cellRef.formatAsString());
			sb.append(",");
		}
		String params = sb.toString();
		if (params.endsWith(",")) {
			params = params.substring(0, params.length() - 1);
		}
		cell.setCellFormula("SUM(" + params + ")");
	}

	private void writeReachedMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {
		// reach
		Set<String> reachMutants = reachMap.get(testCaseID);

		if (reachMutants != null && reachMutants.contains(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);

			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(simpleMutantID);
		}
	}

	private void writeResultByTest(String mutantID, String testCaseID,
			int columnIndex, Row row) {

		writeReachedMutant(row, columnIndex, mutantID, testCaseID);

		writeWeakMutant(row, columnIndex + 1, mutantID, testCaseID);

		writeStrongMutant(row, columnIndex + 2, mutantID, testCaseID);

		writeGroupMutant(row, columnIndex + 3, mutantID, testCaseID);

		writeGroupKilledMutant(row, columnIndex + 4, mutantID, testCaseID);

	}

	private void writeResultCategoryHeader(Sheet sheet) {

		Row row = sheet.createRow(rowIndex++);

		int sizeOfTestCase = testCases.size();
		for (int i = 0; i <= sizeOfTestCase; i++) {

			int baseColumn = 3 + i * 5;
			writeTypeHeader(row, baseColumn);
		}

	}

	private void writeTypeHeader(Row row, int baseColumn) {
		Cell cell = row.createCell(baseColumn++);
		cell.setCellValue("RE");

		cell = row.createCell(baseColumn++);
		cell.setCellValue("WK");

		cell = row.createCell(baseColumn++);
		cell.setCellValue("SK");

		cell = row.createCell(baseColumn++);
		cell.setCellValue("GR");

		cell = row.createCell(baseColumn++);
		cell.setCellValue("GK");
	}

	private void writeSourceSummaryRow(Sheet sheet, String source,
			int beginRow, int size) {

		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		Cell cell = row.createCell(columnIndex++);
		cell.setCellValue(source);
		cell.setCellStyle(summaryStyle);

		cell = row.createCell(columnIndex++);
		cell.setCellValue(size);
		cell.setCellStyle(summaryStyle);
		setSummaryReferenceTable(source, "NUM", row.getRowNum(),
				columnIndex - 1);

		writeSummary(row, columnIndex++, beginRow, size);
		drawSummaryBorder(row, columnIndex - 1, 1);
		setSummaryReferenceTable(source, "EQ", row.getRowNum(), columnIndex - 1);

		for (String testCaseID : testCases) {
			setSummaryReferenceTable(source, testCaseID, row.getRowNum(),
					columnIndex);

			for (int j = 0; j < 5; j++) {
				writeSummary(row, columnIndex++, beginRow, size);
			}

			drawSummaryBorder(row, columnIndex - 1, 5);
		}

		setSummaryReferenceTable(source, "OVERALL", row.getRowNum(),
				columnIndex);
		for (int i = 0; i < 5; i++) {
			writeSumCell(row, columnIndex++, beginRow, size);
		}
		drawSummaryBorder(row, columnIndex - 1, 5);

	}

	private void writeSumCell(Row row, int columnIndex, int beginRow, int size) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellStyle(summaryStyle);
		if (size == 0) {
			cell.setCellValue(0);
		} else {
			String columnStr = CellReference.convertNumToColString(columnIndex);
			cell.setCellFormula("SUM(" + columnStr + (beginRow + 1) + ":"
					+ columnStr + (beginRow + size) + ")");
		}
	}

	private void setSummaryReferenceTable(String fileID, String key, int row,
			int column) {

		Map<String, List<CellReference>> keyMap = summaryReferenceTables
				.get(fileID);
		if (keyMap == null) {
			keyMap = new HashMap<String, List<CellReference>>();
			summaryReferenceTables.put(fileID, keyMap);
		}

		List<CellReference> values = keyMap.get(key);
		if (values == null) {
			values = new ArrayList<CellReference>();
			keyMap.put(key, values);
		}

		values.add(new CellReference(operator, row, column, false, false));
	}

	private void writeSummaryTotalSummary(Row row, int columnIndex,
			int beginRow, int size) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellStyle(summaryStyle);

		if (size == 0) {
			cell.setCellValue(0);
		} else {
			String exp = getSimpleSumExpression(beginRow, beginRow + size - 1,
					cell.getColumnIndex());
			cell.setCellFormula(exp);
		}

	}

	private void drawSummaryBorder(Row row, int column, int size) {
		int rowIndex = row.getRowNum();
		Sheet sheet = row.getSheet();
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex,
				column - (size - 1), column);
		RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region, sheet,
				sheet.getWorkbook());
		RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region, sheet,
				sheet.getWorkbook());
		RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region, sheet,
				sheet.getWorkbook());
		RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region, sheet,
				sheet.getWorkbook());
	}

	private void writeStrongMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// strong
		Set<String> strongMutants = strongMap.get(testCaseID);

		if (strongMutants != null && strongMutants.contains(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);

			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(simpleMutantID);
		}

	}

	private void writeSummary(Row row, int columnIndex, int beginRow, int size) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellStyle(summaryStyle);
		if (size == 0) {
			cell.setCellValue(0);
		} else {
			CellReference fromCellRefer = new CellReference(beginRow,
					cell.getColumnIndex());
			CellReference toCellRefer = new CellReference(beginRow + size - 1,
					cell.getColumnIndex());

			cell.setCellFormula("COUNTA(" + fromCellRefer.formatAsString()
					+ ":" + toCellRefer.formatAsString() + ")");
		}

	}

	private void writeTestHeader(Sheet sheet) {

		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		columnIndex++;

		Cell cell = row.createCell(columnIndex++);
		cell.setCellValue("Mutant");
		Cell eqCell = row.createCell(columnIndex++);
		eqCell.setCellValue("EQU?");

		for (String testCaseID : testCases) {

			cell = row.createCell(columnIndex);
			columnIndex += 5;

			cell.setCellValue(testCaseID);
		}

		cell = row.createCell(columnIndex);
		cell.setCellValue("TOTAL");
	}

	private void writeWeakMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// weak
		Set<String> weakMutants = weakMap.get(testCaseID);

		if (weakMutants != null && weakMutants.contains(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);

			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(simpleMutantID);
		}
	}
}

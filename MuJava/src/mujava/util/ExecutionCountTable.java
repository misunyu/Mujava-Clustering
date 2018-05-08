package mujava.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class ExecutionCountTable extends RecordStatistics {

	Map<String, Map<String, List<CellReference>>> summaryReferenceTables = new HashMap<String, Map<String, List<CellReference>>>();

	private int rowIndex;

	private String operator;
	
	private int total_re_a_mutant; 
	private int total_wk_a_mutant;  
	private int total_sk_a_mutant;  
	private int total_gr_a_mutant;  
	private int total_gk_a_mutant; 


	public void setTestCases(List<String> testCases) {
		this.testCases.clear();
		if (testCases != null && !testCases.isEmpty()) {
			this.testCases.addAll(testCases);
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
				String reachHeader = "[REACH] RE:";
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

					list = reachMap.get(testCaseID);
					if (list == null) {
						list = new HashSet<String>();

						reachMap.put(testCaseID, list);
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
				} else if (line.startsWith(reachHeader)) {
					String mutantID = line.substring(reachHeader.length())
							.trim();

					Set<String> list = reachMap.get(testCaseID);
					if (list == null) {
						throw new NullPointerException();
					}

					list.add(mutantID);
					reachMap.put(testCaseID, list);

					String op = mujava.MutantID.getMutationOperator(mutantID);
					Set<String> mutants = mutantMapByOperator.get(op);
					if (mutants == null) {
						mutants = new HashSet<String>();
						mutantMapByOperator.put(op, mutants);
					}
					mutants.add(mutantID);
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

	private void writeEquivance(EquivalentTable equTable, String mutantID,
			int columnIndex, Row row) {

		if (equTable.isEquivalent(mutantID)) {
			String simpleMutantID = toSimpleMutantID(mutantID);
			Cell eqCell = row.createCell(columnIndex);
			eqCell.setCellValue(simpleMutantID);
		}
	}

	public void writeExcel(File outputFile, SourceTable sourceTable,
			EquivalentTable equTable, List<String> mutationOperators) {

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error on writing Excel file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
		}

		// Workbook wb = new XSSFWorkbook();
		Workbook wb = new SXSSFWorkbook(-1);
		prepareCellStyle(wb);

		Collections.sort(mutationOperators);
		try {
			for (String mutationOperator : mutationOperators) {
				operator = mutationOperator;
				writeMutationOperatorSheet(wb, sourceTable, equTable);
			}

			writeSourceSummarySheet(wb);

			wb.write(fos);
			fos.close();
		} catch (IOException e) {
			System.out.println("Error on writing excel file ("
					+ outputFile.getAbsolutePath() + ")");
			return;
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
						exp = getSumFomularParameters(refs, i);
						cell = row.createCell(columnIndex++);
						cell.setCellFormula(exp);
					}

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

	private String getSumFomularParameters(List<CellReference> refs, int index) {
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
				sb.append("+");
			}
			exp = sb.toString();
			if (exp.endsWith("+")) {
				exp = exp.substring(0, exp.length() - 1);
			}
		}

		return exp;
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
		cell.setCellStyle(headerStyle);

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
		cell.setCellStyle(headerStyle);

		cell = row.createCell(columnIndex++);
		cell.setCellValue("Mutant");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(columnIndex++);
		cell.setCellValue("EQ");
		cell.setCellStyle(headerStyle);

		int secondBeginColumn = columnIndex;
		cell = row.createCell(columnIndex);
		cell.setCellValue("ALL");
		cell.setCellStyle(headerStyle);
		columnIndex += 5;

		/*for (String testCaseID : testCases) {
			cell = row.createCell(columnIndex);
			cell.setCellStyle(headerStyle);

			cell.setCellValue(testCaseID);
			cell.setCellStyle(headerStyle);
			columnIndex += 5;
		}*/

		// write second row
		row = sheet.createRow(rowIndex++);
		columnIndex = secondBeginColumn;

		writeTypeHeader(row, columnIndex);
		/*int sizeOfTestCase = testCases.size();
		for (int i = 0; i < sizeOfTestCase + 1; i++) {
			columnIndex = secondBeginColumn + i * 5;
			writeTypeHeader(row, columnIndex);
			drawRangeBorder(row, columnIndex + 5 - 1, 2, 5);
		}*/

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
			if(mutants.size()>0){
				writeMutantsBySource(sheet, source, mutants, equTable);
			rows.add(rowIndex);
			}
		}

		writeOverallSummary(sheet, rows);
	}

	private void writeGroupKilledMutant(Row row, int columnIndex,
			String mutantID, String testCaseID) {
		// group killed
		Set<String> groupStrongMutants = groupStrongMap.get(testCaseID);
		if (groupStrongMutants != null && groupStrongMutants.contains(mutantID)) {
			total_gk_a_mutant++;
			//Cell cell = row.createCell(columnIndex);
			//cell.setCellValue(1);
		}

	}

	private void writeGroupMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// group
		Set<String> groupMutants = groupMap.get(testCaseID);

		if (groupMutants != null && groupMutants.contains(mutantID)) {
			total_gr_a_mutant++;
			//Cell cell = row.createCell(columnIndex);
			//cell.setCellValue(1);
		}

	}

	private void writeMutantID(String mutantID, int columnIndex, Row row) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(mutantID);
	}

	private void writeMutantRow(Sheet sheet, String mutantID,
			EquivalentTable equTable) {

		total_re_a_mutant = 0; 
		total_wk_a_mutant = 0;   
		total_sk_a_mutant = 0;   
		total_gr_a_mutant = 0;  
		total_gk_a_mutant = 0;  

		Row row = sheet.createRow(rowIndex++);

		int columnIndex = 0;
		writeMutantID(mutantID, columnIndex++, row);
		writeMutantID(toSimpleMutantID(mutantID), columnIndex++, row);
		writeEquivance(equTable, mutantID, columnIndex++, row);

		List<Integer> columns = new ArrayList<Integer>();
		for (String testCaseID : testCases) {
			if(false)
				columns.add(columnIndex);

			writeResultByTest(mutantID, testCaseID, columnIndex, row);
			if(false)
				columnIndex += 5;
		}

		writeMutantSummary(columns, columnIndex++, row);
	}

	private void writeMutantSummary(List<Integer> columns, int columnIndex, Row row) {
		Cell cell;
		cell = row.createCell(columnIndex++);
		cell.setCellValue(total_re_a_mutant);
		cell = row.createCell(columnIndex++);
		cell.setCellValue(total_wk_a_mutant);
		cell = row.createCell(columnIndex++);
		cell.setCellValue(total_sk_a_mutant);
		cell = row.createCell(columnIndex++);
		cell.setCellValue(total_gr_a_mutant);
		cell = row.createCell(columnIndex++);
		cell.setCellValue(total_gk_a_mutant);
	}
	
		// 수식이 너무 길어 엑셀 파일에서 인식이 잘 안되서 삭제함
/*	private void writeMutantSummary(List<Integer> columns, int columnIndex,	Row row) {

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

			cell.setCellFormula("SUM(" + params + ")");

		}
	}
*/
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
		drawRangeBorder(row, columnIndex - 1, 1);
		writeColumnSummary(summaryRows, row, columnIndex++);
		drawRangeBorder(row, columnIndex - 1, 1);

		int sizeOfTestCase = testCases.size() + 1;
		for (int i = 0; i < sizeOfTestCase + 1; i++) {
			for (int j = 0; j < 5; j++) {
				writeColumnSummary(summaryRows, row, columnIndex++);
			}
			drawRangeBorder(row, columnIndex - 1, 5);
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

			if(false){
				Cell cell = row.createCell(columnIndex);
				cell.setCellValue(1);
			}
			total_re_a_mutant++;
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

		writeTypeHeader(row, 3);
		/*
		int sizeOfTestCase = testCases.size();
		for (int i = 0; i < sizeOfTestCase + 1; i++) {

			int baseColumn = 3 + i * 5;
			writeTypeHeader(row, baseColumn);
		}*/

	}

	private void writeTypeHeader(Row row, int baseColumn) {
		Cell cell = row.createCell(baseColumn++);
		cell.setCellValue("RE");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(baseColumn++);
		cell.setCellValue("WK");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(baseColumn++);
		cell.setCellValue("SK");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(baseColumn++);
		cell.setCellValue("GR");
		cell.setCellStyle(headerStyle);

		cell = row.createCell(baseColumn++);
		cell.setCellValue("GK");
		cell.setCellStyle(headerStyle);
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

		writeSumCell(row, columnIndex++, beginRow, size);
		drawRangeBorder(row, columnIndex - 1, 1);
		setSummaryReferenceTable(source, "EQ", row.getRowNum(), columnIndex - 1);

		/*for (String testCaseID : testCases) {

			setSummaryReferenceTable(source, testCaseID, row.getRowNum(),
					columnIndex);

			for (int j = 0; j < 5; j++) {
				writeSumCell(row, columnIndex++, beginRow, size);
			}
			drawRangeBorder(row, columnIndex - 1, 5);
		}*/

		setSummaryReferenceTable(source, "OVERALL", row.getRowNum(),
				columnIndex);
		for (int j = 0; j < 5; j++) {
			writeSumCell(row, columnIndex++, beginRow, size);
		}
		drawRangeBorder(row, columnIndex - 1, 5);

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

	private void writeStrongMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// strong
		Set<String> strongMutants = strongMap.get(testCaseID);

		if (strongMutants != null && strongMutants.contains(mutantID)) {
			total_sk_a_mutant++;
			//Cell cell = row.createCell(columnIndex);
			//cell.setCellValue(1);
		}

	}

	private void writeSumCell(Row row, int columnIndex, int beginRow, int size) {

		Cell cell = row.createCell(columnIndex);
		cell.setCellStyle(summaryStyle);
		if (size == 0) {
			cell.setCellValue(0);
		} else {
			// double sum = 0;
			// Sheet sheet = row.getSheet();
			// for (int i = beginRow; i < size; i++) {
			// Row newRow = sheet.getRow(beginRow);
			// Cell valueCell = newRow.getCell(columnIndex);
			// try {
			// if (valueCell != null) {
			// double value = valueCell.getNumericCellValue();
			// sum += value;
			// }
			// } catch (IllegalStateException e) {
			// } catch (NumberFormatException e) {
			// }
			// }
			// cell.setCellValue((long) sum);

			String columnStr = CellReference.convertNumToColString(columnIndex);
			cell.setCellFormula("SUM(" + columnStr + (beginRow + 1) + ":"
					+ columnStr + (beginRow + size) + ")");
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

		// 나중에 반영하자
		if(false){
			for (String testCaseID : testCases) {
	
				cell = row.createCell(columnIndex);
				columnIndex += 5;
	
				cell.setCellValue(testCaseID);
			}
		}

		cell = row.createCell(columnIndex);
		cell.setCellValue("TOTAL");
	}

	private void writeWeakMutant(Row row, int columnIndex, String mutantID,
			String testCaseID) {

		// weak
		Set<String> weakMutants = weakMap.get(testCaseID);

		if (weakMutants != null && weakMutants.contains(mutantID)) {
			total_wk_a_mutant++;
			//Cell cell = row.createCell(columnIndex);
			//cell.setCellValue(1);
		}
	}
}

package mujava.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mujava.MutantTable;

import org.eclipse.core.runtime.CoreException;

public class ReachTableGenerator
{

	public enum Level
	{
		PATH, METHOD, CLASS,
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		Level option = Level.PATH;

		List<String> cmds = Arrays.asList(args);
		// determine generation option
		// If both options are specified, method option is ignored.
		if (cmds.contains("-LEVEL=CLASS"))
		{
			option = Level.CLASS;
		}
		else if (cmds.contains("-LEVEL=METHOD"))
		{
			option = Level.METHOD;
		}

		String inputLogFileName = "";
		String inputTargetTableFileName = "";
		String inputMutantTableFileName = "";

		for (String cmd : cmds)
		{
			if (cmd.startsWith("-LOG="))
			{
				String fileName = cmd.substring(cmd.indexOf("-LOG=") + 5);
				if (!new File(fileName).exists())
				{
					System.err.println("No-Exist LOG File !!! : " + fileName);
					System.exit(-1);
				}

				inputLogFileName = fileName;
			}
			else if (cmd.startsWith("-TARGET="))
			{
				String fileName = cmd.substring(cmd.indexOf("-TARGET=") + 8);
				if (!new File(fileName).exists())
				{
					System.err
							.println("No-Exist TARGET File !!! : " + fileName);
					System.exit(-1);
				}

				inputTargetTableFileName = fileName;
			}
			else if (cmd.startsWith("-MUTANT="))
			{
				String fileName = cmd.substring(cmd.indexOf("-MUTANT=") + 8);
				if (!new File(fileName).exists())
				{
					System.err
							.println("No-Exist MUTANT File !!! : " + fileName);
					System.exit(-1);
				}

				inputMutantTableFileName = fileName;
			}
		}

		if (inputLogFileName.isEmpty())
		{
			System.err.println("Arguments are Missing!!!");
			System.exit(-1);
		}
		if (inputTargetTableFileName.isEmpty())
		{
			System.err.println("Arguments are Missing!!!");
			System.exit(-1);
		}

		long time = System.nanoTime();

		// Create Reach-ability Table
		ReachTableGenerator generator = new ReachTableGenerator();

		// Target File 로딩
		SourceTable sourceTable = generator
				.parseTargetFile(inputTargetTableFileName);

		MutantTable mutantTable = null;
		try
		{
			mutantTable = MutantTable.getMutantTable(inputMutantTableFileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		ReachTable reachTable = generator.parseLogFile(inputLogFileName);
		// ,mutantTable);

		// Create XML file
		File outputFile = makeOutputFile("Reach", "table");
		System.out.println("Reach Table (" + outputFile.getAbsolutePath()
				+ ") is Creating...");
		reachTable.writeTextFile(outputFile, mutantTable, option);

		System.out.println((System.nanoTime() - time) / 1000000);

		outputFile = makeOutputFile("CSV", "csv");
		reachTable.writeCSVFile(outputFile);

		outputFile = makeOutputFile("XLS", "xls");
		reachTable.writeExcel(outputFile);

		System.out.println("Reach Table is Created");
	}

	private SourceTable parseTargetFile(String inputTargetTableFileName)
	{
		SourceTable table = new SourceTable();
		table.parseTargetFile(new File(inputTargetTableFileName));

		return table;
	}

	private static File makeOutputFile(String header, String ext)
	{
		StringBuffer sb = new StringBuffer(header);

		int counter = 0;
		while (true)
		{
			sb.append(".");
			sb.append(ext);
			File file = new File(sb.toString());
			if (!file.exists())
			{
				return file;
			}

			counter++;
			sb.setLength(header.length());
			sb.append(counter);
		}
	}

	private ReachTable parseLogFile(String inputLogFileName)
	{

		ReachTable table = new ReachTable();
		table.appendLogFile(new File(inputLogFileName));

		return table;
	}

}

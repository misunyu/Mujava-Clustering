package mujava.plugin.actions;


import java.io.File;
import java.io.IOException;

import kaist.selab.util.MuJavaLogger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import mujava.MuJavaProject;
import mujava.MutantTable;
import mujava.plugin.wizards.generation.MutantGenerationWizard;
import mujava.util.ReachTable;
import mujava.util.ReachTableGenerator.Level;


public class GenerateReachTableActionDelegate implements
		IWorkbenchWindowActionDelegate {

	private ISelection selection;
	private IResource selectedProjectResource = null;

	@Override
	public void run(IAction action) {


		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object item = sSelection.getFirstElement();
			if (item instanceof IResource) {
				selectedProjectResource = (IResource) item;
			}
		}

		
		MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				selectedProjectResource, null);

		MutantTable mutantTable = null;

		try
		{
			mutantTable = MutantTable.getMutantTable(muProject, null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		ReachTable table = new ReachTable();
		File myF = new File("E:\\MuJava_2013_Data\\joda-time-2.3\\joda-time-2.3_reach.log");
		table.appendLogFile(myF);

		// Create XML file

		MuJavaLogger.getLogger().info("Reach Table is Creating...");
		File outputFile;
		/*
		 File outputFile = makeOutputFile(classReachFileName, isOverwritten);
		 

		MuJavaLogger.getLogger().info(
				"Reach Table for CLASS (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.CLASS);

		outputFile = makeOutputFile(methodReachFileName, isOverwritten);
		MuJavaLogger.getLogger().info(
				"Reach Table for METHOD (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.METHOD);
		 */
		outputFile = makeOutputFile("E:\\MuJava_2013_Data\\joda-time-2.3\\ysma_reach.table", true);
		MuJavaLogger.getLogger().info(
				"Reach Table for PATH (" + outputFile.getAbsolutePath()
						+ ") is Creating...");
		table.writeTextFile(outputFile, mutantTable, Level.PATH);

		MuJavaLogger.getLogger().info("Reach Table is Created");		
	}

	private static File makeOutputFile(String fileName, boolean isOverwritten)
	{
		String ext = Path.fromOSString(fileName).getFileExtension();
		if (ext == null)
		{
			ext = "";
		}

		String header = fileName;
		if (!ext.isEmpty())
		{
			int index = fileName.lastIndexOf(ext);
			header = fileName.substring(0, index - 1);
		}

		StringBuffer sb = new StringBuffer(header);
		int counter = 0;
		while (true)
		{
			sb.append(".");
			sb.append(ext);

			File file = new File(sb.toString());

			if (isOverwritten || !file.exists())
			{
				return file;
			}

			counter++;
			sb.setLength(header.length());
			sb.append(counter);
		}
	}
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}

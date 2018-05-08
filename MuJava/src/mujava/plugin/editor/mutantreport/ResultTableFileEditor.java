package mujava.plugin.editor.mutantreport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import kaist.selab.util.MuJavaLogger;
import mujava.ResultTable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class ResultTableFileEditor extends MultiPageEditorPart implements
		IResourceChangeListener
{

	private ResultTable resultTable;

	private MutantScoreReportComposite scorePage;

	private TimeCostViewerComposite timePage;

	private TestCaseViewerComposite testCasePage;

	private GeneralReportInfoComposite generalPage;

	private IFile resultTableFileName = null;

	public ResultTableFileEditor()
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	@Override
	/**
	 * This method is called after init()
	 */
	protected void createPages()
	{
		// creates pages
		generalPage = new GeneralReportInfoComposite(getContainer(), SWT.NONE);
		int index = addPage(generalPage);
		setPageText(index, "General");

		scorePage = new MutantScoreReportComposite(getContainer(), SWT.NONE);
		index = addPage(scorePage);
		setPageText(index, "Mutant Score");

		timePage = new TimeCostViewerComposite(getContainer(), SWT.NONE);
		index = addPage(timePage);
		setPageText(index, "Time");

		testCasePage = new TestCaseViewerComposite(getContainer(), SWT.NONE);
		index = addPage(testCasePage);
		setPageText(index, "Used Test Case");

		// loads update pages
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(Display
				.getDefault().getActiveShell());
		try
		{
			dlg.run(false, false, new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask("Update changed Result Table", 4);

					if (resultTable == null)
					{
						try
						{
							resultTable = ResultTable.getResultTable(
									resultTableFileName, monitor);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else
						monitor.worked(1);

					updatePages(resultTable, monitor);

					monitor.done();
				}
			});
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		dlg.close();
	}

	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	// this is just viewer, but not supporting editing
	public void doSave(IProgressMonitor monitor)
	{
	}

	// this is just viewer, but not supporting editing
	public void doSaveAs()
	{
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);

		IFileEditorInput input = (IFileEditorInput) editorInput;
		IFile file = input.getFile();
		this.setPartName(file.getName());
		this.resultTableFileName = file;
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	public void resourceChanged(IResourceChangeEvent event)
	{

		if (event.getType() == IResourceChangeEvent.POST_CHANGE)
		{
			IResourceDelta delta = event.getDelta();

			IResource res = this.resultTable.getResource();

			IResourceDelta resultDelta = delta.findMember(res.getFullPath());
			if (resultDelta != null)
			{
				try
				{
					Shell shell = Display.getDefault().getActiveShell();
					ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);

					dlg.run(false, false, new IRunnableWithProgress()
					{
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException
						{
							try
							{
								if (resultTableFileName != null)
								{
									monitor.beginTask(
											"Update changed Result Table", 4);

									resultTable = ResultTable.getResultTable(
											resultTableFileName, monitor);

									updatePages(resultTable, monitor);
									monitor.done();
								}
								else
									MuJavaLogger
											.getLogger()
											.error("[ERROR : Result Table file name is null");

							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					});

					dlg.close();
				}
				catch (SWTException e1)
				{

				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void setFocus()
	{

	}

	/**
	 * It has 3 sub-tasks(each has 1 workd)
	 * 
	 */
	protected void updatePages(ResultTable resultTable, IProgressMonitor monitor)
	{
		if (generalPage != null)
			generalPage.update(resultTable, monitor);
		if (scorePage != null)
			scorePage.update(resultTable, monitor);
		if (timePage != null)
			timePage.update(resultTable, monitor);
		if (testCasePage != null)
			testCasePage.update(resultTable, monitor);
	}

}

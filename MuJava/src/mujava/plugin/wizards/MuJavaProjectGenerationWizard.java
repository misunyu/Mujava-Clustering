package mujava.plugin.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import mujava.MuJavaProject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class MuJavaProjectGenerationWizard extends Wizard implements INewWizard {
	private NewMuJavaProjectWizardPage page;

	private ISelection selection;

	public MuJavaProjectGenerationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewMuJavaProjectWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {

		final String containerName = page.getContainerName();
		final MuJavaProject project = new MuJavaProject(null);

		// project.setMainClass(page.getMainClassText());
		project.setMutantDirectory(page.getMutantDirText());
		project.setTestDirectory(page.getTestDirText());
		project.setResultDirectory(page.getResultDirText());

		project.setName(page.getMuJavaProjectName());
		project.setFileName(page.getMuJavaProjectFileName());
		project.setGenerationWay(page.getGenerationWay());
		project.setTestCaseType(page.getTestCaseType());

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, project, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};

		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(String containerName, MuJavaProject project,
			IProgressMonitor monitor) throws CoreException {

		// create a mujava project file
		String projectFileName = project.getFileName();
		monitor.beginTask("Creating " + projectFileName, 3);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName
					+ "\" does not exist.");
		}

		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(projectFileName));
		project.setResource(file);

		try {
			project.save(monitor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Make a directory which contains a set of mutants. Thus, the directory
		 * should be regarded as source directory in Eclipse System
		 */
		IProject prj = container.getProject();
		IJavaProject jPrj = JavaCore.create(prj);

		try {
			IClasspathEntry[] entry = jPrj.getRawClasspath();

			// If there is a source entry
			IClasspathEntry srcEntry = null;
			for (int i = 0; i < entry.length; i++) {
				if (entry[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath[] excludingPaths = entry[i].getExclusionPatterns();
					IPath[] newExcludingPaths = new Path[excludingPaths.length + 3];
					System.arraycopy(excludingPaths, 0, newExcludingPaths, 0,
							excludingPaths.length);
					IPath newMutantDirectoryPath = new Path(
							project.getMutantDirectory()
									+ kaist.selab.util.Helper.directorySeparator);
					IPath newTestDirectoryPath = new Path(
							project.getTestDirectory()
									+ kaist.selab.util.Helper.directorySeparator);
					IPath newResultDirectoryPath = new Path(
							project.getTestDirectory()
									+ kaist.selab.util.Helper.directorySeparator);
					newExcludingPaths[newExcludingPaths.length - 3] = newResultDirectoryPath;
					newExcludingPaths[newExcludingPaths.length - 2] = newMutantDirectoryPath;
					newExcludingPaths[newExcludingPaths.length - 1] = newTestDirectoryPath;
					srcEntry = entry[i];
					IClasspathEntry newEntry = JavaCore.newSourceEntry(
							srcEntry.getPath(),
							srcEntry.getInclusionPatterns(), newExcludingPaths,
							srcEntry.getOutputLocation());
					entry[i] = newEntry;
					jPrj.setRawClasspath(entry, monitor);
					break;
				}
			}

			// If there is no source entry
			if (srcEntry == null) {
				IPath[] excludingPaths = new Path[] {
						new Path(project.getMutantDirectory()
								+ kaist.selab.util.Helper.directorySeparator),
						new Path(project.getTestDirectory()
								+ kaist.selab.util.Helper.directorySeparator),
						new Path(project.getResultDirectory()
								+ kaist.selab.util.Helper.directorySeparator) };

				IClasspathEntry[] newEntry = new IClasspathEntry[entry.length + 1];
				System.arraycopy(entry, 0, newEntry, 0, entry.length);

				newEntry[newEntry.length - 1] = JavaCore.newSourceEntry(jPrj
						.getResource().getParent().getFullPath(), null,
						excludingPaths, jPrj.getOutputLocation());
				jPrj.setRawClasspath(newEntry, monitor);
			}
		} catch (JavaModelException e) {
			// If there is not any classpath entry
			IPath[] excludingPaths = new Path[] {
					new Path(project.getMutantDirectory()
							+ kaist.selab.util.Helper.directorySeparator),
					new Path(project.getTestDirectory()
							+ kaist.selab.util.Helper.directorySeparator),
					new Path(project.getResultDirectory()
							+ kaist.selab.util.Helper.directorySeparator) };
			IClasspathEntry[] entry = new IClasspathEntry[] { JavaCore
					.newSourceEntry(jPrj.getResource().getParent()
							.getFullPath(), null, excludingPaths,
							jPrj.getOutputLocation()) };
			jPrj.setRawClasspath(entry, monitor);
		}

		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "Sample", IStatus.OK,
				message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
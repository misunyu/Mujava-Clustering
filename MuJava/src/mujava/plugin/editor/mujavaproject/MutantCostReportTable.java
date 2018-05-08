package mujava.plugin.editor.mujavaproject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import kaist.selab.util.Helper;
import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;
import mujava.MuJavaProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public class MutantCostReportTable extends PropertyFile {
	private static Hashtable<MuJavaProject, MutantCostReportTable> tables = new Hashtable<MuJavaProject, MutantCostReportTable>();
	private MuJavaProject mujavaProject;
	private List<MutantCostReport> gResults = new ArrayList<MutantCostReport>();

	public MutantCostReportTable(MuJavaProject mjProject) {
		this.mujavaProject = mjProject;
		this.setFileComment("Generation Report Table");
	}

	public static MutantCostReportTable getGenerationResultTable(
			MuJavaProject mjProject, IProgressMonitor monitor)
			throws IOException {
		MutantCostReportTable gTable = tables.get(mjProject);

		if (gTable == null) {
			gTable = new MutantCostReportTable(mjProject);

			IResource muJavaProjectFile = mjProject.getResource();
			IProject prj = muJavaProjectFile.getProject();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IPath path = prj.getFullPath();
			path = path.append(mjProject.getMutantDirectory());
			path = path.append(mjProject.getName());
			path = path.append(mjProject.getName());
			path = path.addFileExtension("gTable");

			IFile file = root.getFile(path);
			gTable.setResource(file);

			try {
				gTable.open(monitor);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				gTable.save(monitor);
			}

		}

		return gTable;
	}

	/**
	 * Open this table and put it into the cache table (static variable tables)
	 */
	@Override
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {
		if (monitor != null)
			monitor.subTask("Open Generation Result Table...");

		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();
		} else {
			MuJavaLogger.getLogger().debug(
					"[GenerationResultTable's resource is null]");
		}

		gResults.clear();
		int size = Helper.getIntValueFromProperties(super.getProperties(),
				"SizeOfGenerationResult");
		for (int i = 0; i < size; i++) {
			String content = Helper.getValueFromProperties(
					this.getProperties(), "Content_" + i);
			ByteArrayInputStream boi = new ByteArrayInputStream(content
					.getBytes());
			MutantCostReport gResult = MutantCostReport.loadXML(boi);
			gResults.add(gResult);
		}

		// registry new table object to the static hashtable
		tables.put(mujavaProject, this);

		if (monitor != null)
			monitor.worked(1);
	}

	@Override
	public void save(IProgressMonitor monitor) throws IOException {
		if (monitor != null)
			monitor.subTask("Save Result Table...");

		super.getProperties().clear();

		super.setValue("SizeOfGenerationResult", Integer.toString(gResults
				.size()));
		int count = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (MutantCostReport gResult : gResults) {
			baos.reset();
			gResult.storeToXML(baos, "");
			String str = baos.toString();
			super.setValue("Content_" + count, str);
			count++;
		}

		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			ByteArrayOutputStream bos = super.saveTemporary();
			ByteArrayInputStream bis = new ByteArrayInputStream(bos
					.toByteArray());
			IFile file = (IFile) super.getResource();

			try {
				if (file.exists()) {
					file.setContents(bis, true, false, monitor);
				} else {
					IPath path = super.getResource().getFullPath();
					super.createParentFolder(path);
					file.create(bis, true, monitor);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			MuJavaLogger.getLogger().debug("[ResultTable's resource is null ]");
		}

		if (monitor != null)
			monitor.worked(1);

	}

	public void addGenerationResult(MutantCostReport result) {
		if (!gResults.contains(result))
			gResults.add(result);
	}

	public Collection<? extends MutantCostReport> getResults() {

		return gResults;
	}

	public static void deleteCache(MuJavaProject project) {
		tables.remove(project);
	}
}

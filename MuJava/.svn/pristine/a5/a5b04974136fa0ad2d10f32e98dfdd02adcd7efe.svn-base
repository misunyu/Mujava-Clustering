package mujava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.StringTokenizer;

import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeneratedCodeTable extends PropertyFile {
	private static Hashtable<MuJavaProject, GeneratedCodeTable> tables = new Hashtable<MuJavaProject, GeneratedCodeTable>();

	private MuJavaProject mujavaProject;

	// private Properties packages = new Properties();
	private Hashtable<String, CodeSet> codeTable = new Hashtable<String, CodeSet>();

	private GeneratedCodeTable(MuJavaProject mjProject) {
		this.mujavaProject = mjProject;
		this.setFileComment("Generated Codes Table");
	}

	public static GeneratedCodeTable getGeneratedCodeTable(
			MuJavaProject mjProject, IProgressMonitor monitor)
			throws IOException, CoreException {

		GeneratedCodeTable table = tables.get(mjProject);
		if (table == null) {
			table = new GeneratedCodeTable(mjProject);
			IProject eclipseProject = mjProject.getResource().getProject();

			IPath path = eclipseProject.getFullPath();
			path = path.append(mjProject.getMutantDirectory());
			path = path.append(mjProject.getName());
			path = path.append(mjProject.getName());
			path = path.addFileExtension("code");

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = root.getFile(path);
			table.setResource(file);

			try {
				table.open(monitor);
			} catch (Exception e) {
				table.save(monitor);
			}

			tables.put(mjProject, table);
		}

		return table;
	}

	/**
	 * Never return null
	 * 
	 * @return
	 */
	public IPath getCodeSetDirectory() {
		IPath path = this.resource.getFullPath();
		path = path.removeLastSegments(1);
		// path = path.append(mujavaProject.getName());

		return path;
	}

	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {
		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();

			codeTable.clear();
			int size = super.getIntValue("SizeOfCodeSet");
			
			if (monitor != null) {
				monitor.beginTask("open codeset", size);
			}
			
			for (int i = 0; i < size; i++) {
				String value = super.getValue("CodeSet_" + i);
				StringTokenizer st = new StringTokenizer(value, "_");
				String operatorName = st.nextToken();
				String cID = st.nextToken();

				if (operatorName != null && cID != null) {

					IPath path = getCodeSetDirectory();
					if (!path.isEmpty()) {
						path = path.append(operatorName);
					}

					CodeSet codeSet = CodeSet.getCodeSet(path, cID);
					codeTable.put(cID, codeSet);

					if (monitor != null)
						monitor.worked(1);
				}
			}

			tables.put(mujavaProject, this);
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. opening without Resource variable in MutantTable");
		}
	}

	public void save(IProgressMonitor monitor) throws IOException {
		if (super.resource != null) {
			super.prop.clear();

			int size = codeTable.size();
			super.prop.setProperty("SizeOfCodeSet", String.valueOf(size));
			int count = 0;
			for (String cID : codeTable.keySet()) {
				CodeSet codeSet = codeTable.get(cID);
				super.prop.setProperty("CodeSet_" + (count++), codeSet
						.getMutantOperator()
						+ "_" + cID);
			}

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
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. saving without Resource variable in MutantTable");
		}
	}

	public void addCodeSet(CodeSet codeSet) {
		
		String cID = codeSet.getID();
		CodeSet oriCodeSet = codeTable.get(cID);
		if (oriCodeSet != null) {
			MuJavaLogger.getLogger().error(
					"An duplicated Codes are inputed. : " + cID);
		}

		codeTable.put(cID, codeSet);
	}

	public List<String> getMutantIDs(String cid) {
		List<String> codes = new ArrayList<String>();
		CodeSet code = codeTable.get(cid);
		if (code != null) {
			return code.IDs;
		}
		return codes;
	}
}

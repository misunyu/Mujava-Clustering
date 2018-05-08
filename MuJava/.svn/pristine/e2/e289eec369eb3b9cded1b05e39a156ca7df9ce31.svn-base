package mujava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class CodeSet extends PropertyFile {
	/**
	 * 
	 * @param loc
	 *            code set directory relative to the workspace
	 * @param cID
	 * @return
	 */
	public static CodeSet getCodeSet(IPath loc, String cID) {
		CodeSet c = new CodeSet();

		c.setID(cID);

		IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		IFile resource = rt.getFile(loc.append(cID).append(c.getFileName()));
		c.setResource(resource);

		try {
			c.open(null);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static CodeSet getCodeSet(String codeTablePath,
			String mutationOperator, String cID) {
		CodeSet c = new CodeSet();
		c.setID(cID);

		IPath path = new Path(codeTablePath);
		path = path.append(mutationOperator);
		path = path.append(cID);
		path = path.addFileExtension("prop");
		c.setFileName(path.toOSString());

		try {
			c.open(null);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static CodeSet getNewCodeSet(IPath loc, String opName) {
		CodeSet c = new CodeSet();
		c.setRootDirectory(loc.append(c.ID));
		c.setMutantOperator(opName);

		IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		IFile resource = rt.getFile(loc.append(c.getID()).append(
				c.getFileName()));
		c.setResource(resource);

		return c;
	}

	// Location string of the file in this code set
	String archiveName = new String();

	// Idefitifer of this Code Set
	String ID = new String();

	// A set of 4th level mutant ID
	List<String> IDs = new ArrayList<String>();

	int sizeOfChangePoint;

	String mutantOperator = new String();

	List<String> relativePathNames = new ArrayList<String>();

	IPath rootDirectory = new Path("");

	int startPoint = 0;

	String targetFile = new String();

	private CodeSet() {

		long uniqueKey = System.nanoTime();
		ID = String.valueOf(uniqueKey);

		super.setFileComment("CodeSet Inforamtion");
		super.setFileName("CodeSet.prop");
	}

	/**
	 * add the given 4th level mutant ID in this code set
	 * 
	 * @param globalMutantID
	 *            4th level, Exception is raised if it is not 4th level
	 */
	public void addMutantID(String globalMutantID) {
		if (!IDs.contains(globalMutantID)) {
			IDs.add(globalMutantID);
		}
	}

	public String getArchiveName() {
		return archiveName;
	}

	public String getID() {
		return ID;
	}

	public int getSizeOfChangePoint() {
		return sizeOfChangePoint;
	}

	public List<String> getRelativePathNames() {
		return relativePathNames;
	}

	/**
	 * 
	 * @return a classpath where mutants are located, which is relative from
	 *         eclipse workspace
	 */
	public IPath getRootDirectory() {
		return rootDirectory;
	}

	public boolean isValid() {
		return (this.sizeOfChangePoint > 0) ? true : false;
	}

	@Override
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {

		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();

			this.setRootDirectory(new Path(super.getValue("RootDirectory")));
			this.setArchiveName(super.getValue("ArchiveName"));
			this.setSizeOfChangePoint(super.getIntValue("ChangePointCount"));
			this.setStartPoint(super.getIntValue("StartPoint"));
			this.setMutantOperator(super.getValue("MutantOperator"));
			this.setTargetFile(super.getValue("TargetFile"));
			int sizeOfMutant = super.getIntValue("MutantCount");
			IDs.clear();
			for (int i = 0; i < sizeOfMutant; i++) {
				IDs.add(super.getValue("MutantID_" + i));
			}

			int size = super.getIntValue("FileSize");
			this.relativePathNames = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				String value = super.getValue("File" + Integer.toString(i));
				if (value != "")
					this.relativePathNames.add(value);
			}
		} else if (fileName != null && !fileName.isEmpty()) {
			super.openProperty();

			this.setRootDirectory(new Path(super.getValue("RootDirectory")));
			this.setArchiveName(super.getValue("ArchiveName"));
			this.setSizeOfChangePoint(super.getIntValue("ChangePointCount"));
			this.setStartPoint(super.getIntValue("StartPoint"));
			this.setMutantOperator(super.getValue("MutantOperator"));
			this.setTargetFile(super.getValue("TargetFile"));
			int sizeOfMutant = super.getIntValue("MutantCount");
			IDs.clear();
			for (int i = 0; i < sizeOfMutant; i++) {
				IDs.add(super.getValue("MutantID_" + i));
			}

			int size = super.getIntValue("FileSize");
			this.relativePathNames = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				String value = super.getValue("File" + Integer.toString(i));
				if (value != "")
					this.relativePathNames.add(value);
			}
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. opening without Resource variable in MutantTable");
		}
	}

	@Override
	public void save(IProgressMonitor monitor) throws IOException {
		super.prop.clear();

		if (super.resource != null) {
			super.setValue("RootDirectory", this.getRootDirectory().toString());
			super.setValue("ArchiveName", getArchiveName());
			super.setValue("ChangePointCount", String
					.valueOf(getSizeOfChangePoint()));
			super.setValue("StartPoint", String.valueOf(getStartPoint()));
			super.setValue("MutantOperator", getMutantOperator());
			super.setValue("TargetFile", getTargetFile());

			super.setValue("FileSize", Integer.toString(relativePathNames
					.size()));
			int index = 0;
			for (String name : relativePathNames) {
				super.setValue("File_" + Integer.toString(index++), name);
			}

			// assert (IDs.size() == getSizeOfChangePoint());
			super.setValue("MutantCount", Integer.toString(IDs.size()));
			for (String mutantID : IDs) {
				super.setValue("MutantID_" + IDs.indexOf(mutantID), mutantID);
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

	/**
	 * 
	 * @param archiveName
	 *            absolute path of the java archive file
	 */
	public void setArchiveName(String archiveName) {
		this.archiveName = archiveName;
	}

	public void setID(String id) {
		ID = id;
	}

	public void setSizeOfChangePoint(int sizeOfChangePoint) {
		this.sizeOfChangePoint = sizeOfChangePoint;
	}

	public void setRelativePathNames(List<String> relativePathNames) {
		this.relativePathNames = relativePathNames;
	}

	public void setRootDirectory(IPath rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	protected String getMutantOperator() {
		return mutantOperator;
	}

	protected int getStartPoint() {
		return startPoint;
	}

	protected void setMutantOperator(String mutantOperator) {
		this.mutantOperator = mutantOperator;
	}

	protected void setStartPoint(int startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * root relative path
	 * 
	 * @return
	 */
	public String getTargetFile() {
		return targetFile;
	}

	/**
	 * workspace-relative path
	 * 
	 * @param targetFile
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	public void remove() {
		if (archiveName != null && !archiveName.equals("")) {
			File file = new File(archiveName);
			file.delete();
		}

		IPath path = rootDirectory;
		IFolder res = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		try {
			res.delete(true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

}

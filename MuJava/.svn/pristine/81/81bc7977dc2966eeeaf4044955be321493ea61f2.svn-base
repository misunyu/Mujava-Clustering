package mujava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;
import mujava.gen.GenerationType;
import mujava.inf.IMuJavaProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class MuJavaProject extends PropertyFile implements IMuJavaProject {

	/**
	 * 
	 * @param mujavaPrj
	 *            path relative to the project root
	 * @param monitor
	 * @return
	 */
	public static MuJavaProject getMuJavaProject(IResource mujavaPrj,
			IProgressMonitor monitor) {
		MuJavaProject project = new MuJavaProject(mujavaPrj);

		try {
			project.open(monitor);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return project;
	}

	/**
	 * name은 MuJavaProject의 Root로부터 상대적인 이름을 지칭한다.
	 * 
	 * @param name
	 * @param monitor
	 * @return
	 */
	public static MuJavaProject getMuJavaProject(String name,
			IProgressMonitor monitor) {
		// IMarker[] markers = root.findMarkers("MuJava.mujavaMarker",
		// false,
		// IResource.DEPTH_ONE);
		// for (int i = 0; i < markers.length; i++) {
		// Object obj = markers[i].getAttribute("Type");
		// if (obj != null && obj instanceof String) {
		// String value = (String) obj;
		// if (value.equals("MuJavaProject")) {
		// IFile file = (IFile) markers[i].getResource();
		// if (file.getName().equals(name))
		// return getMuJavaProject(file, monitor);
		// }
		// }
		// }

		// String fileName = name + ".mjp";
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource res = root.getFile(new Path(name));

		return getMuJavaProject(res, monitor);
	}

	public MuJavaProject(IResource res) {
		super();
		super.resource = res;
		super.setFileComment("MuJava Project Info");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#delete()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#delete()
	 */
	public void delete() {
		super.setProperties(new Properties());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#equals(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MuJavaProject) {
			MuJavaProject project = (MuJavaProject) obj;
			if (this.getName().equals(project.getName())
					&& this.getFileName().equals(project.getFileName()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		String name = getName();
		if (name != null)
			return name.hashCode();

		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getMainClass()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#getMainClass()
	 */
	public String getMainClass() {
		return super.getValue("MainClass");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getMutantDirectory()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#getMutantDirectory()
	 */
	public String getMutantDirectory() {
		return super.getValue("MutantDirectory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#getMutantTableFileName()
	 */
	public String getMutantTableFileName() {
		return super.getValue("MutantTableFileName");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getName()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#getName()
	 */
	public String getName() {
		return super.getValue("MuJavaProjectName");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getNote()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#getNote()
	 */
	public String getNote() {
		return super.getValue("Note");
	}

	public String getTestDirectory() {
		return super.getValue("TestDirectory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#open()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mujava.IMuJavaProject#open(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {
		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();
		} else {
			MuJavaLogger
					.getLogger()
					.error("Unacceptable operation.. saving without Resource variable");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#save()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mujava.IMuJavaProject#save(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void save(IProgressMonitor monitor) throws IOException {
		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			ByteArrayOutputStream bos = super.saveTemporary();
			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());
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

			try {
				IMarker marker = file.createMarker("MuJava.mujavaMarker");
				marker.setAttribute("Type", "MuJavaProject");
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else {
			MuJavaLogger
					.getLogger()
					.error("Unacceptable operation.. saving without Resource variable");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setMainClass(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#setMainClass(java.lang.String)
	 */
	public void setMainClass(String mainClass) {
		super.setValue("MainClass", mainClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setMutantDirectory(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#setMutantDirectory(java.lang.String)
	 */
	public void setMutantDirectory(String mutantDirectory) {
		super.setValue("MutantDirectory", mutantDirectory);
	}

	// /* (non-Javadoc)
	// * @see mujava.IProject#makeFileSystem()
	// */
	// public void makeFileSystem() {
	// File f = new File(getMutantDirectory());
	// f.mkdirs();
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#setMutantTableFileName(java.lang.String)
	 */
	public void setMutantTableFileName(String name) {
		super.setValue("MutantTableFileName", name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#setName(java.lang.String)
	 */
	public void setName(String name) {
		super.setValue("MuJavaProjectName", name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setNote(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMuJavaProject#setNote(java.lang.String)
	 */
	public void setNote(String text) {
		super.setValue("Note", text);
	}

	public void setTestDirectory(String testDirText) {
		super.setValue("TestDirectory", testDirText);
	}

	public void setResultDirectory(String resultDirText) {
		super.setValue("ResultDirectory", resultDirText);
	}

	public String getResultDirectory() {
		return getValue("ResultDirectory");
	}

	public void setGenerationWay(mujava.gen.GenerationType type) {
		super.setValue("GenerationWay", type.toString());
	}

	public mujava.gen.GenerationType getGenerationWay() {
		mujava.gen.GenerationType genType = GenerationType.NONE;
		String value = getValue("GenerationWay");
		try {
			genType = GenerationType.valueOf(value);
		} catch (Exception e) {
		}

		return genType;
	}

	public void setTestCaseType(TestCaseType type) {
		super.setValue("TestCaseType", type.toString());
	}

	public TestCaseType getTestCaseType() {

		// 기본값 설정
		TestCaseType testType = TestCaseType.NONE;

		// Enumeration 문자열로 해당 Enum 값을 구함
		String value = getValue("TestCaseType");

		try {

			testType = TestCaseType.valueOf(value);
		} catch (Exception e) {
			// 기본값으로 재설정
			testType = TestCaseType.NONE;
		}

		return testType;
	}
	// public InputStream getInputStream() throws IOException {
	// ByteArrayOutputStream bos = super.saveTemporary();
	// ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	// bos.close();
	// return bis;
	// }

}

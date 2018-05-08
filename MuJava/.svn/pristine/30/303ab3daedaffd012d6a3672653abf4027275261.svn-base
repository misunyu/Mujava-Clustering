package mujava;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.StringTokenizer;

import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;
import mujava.inf.IMutantInfo;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public class MuJavaMutantInfo extends PropertyFile implements IMutantInfo,
		Comparable<MuJavaMutantInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5868259509778073018L;
	private int objID = -1;
	
	public static MuJavaMutantInfo getMuJavaMutantInfo(
			MuJavaProject mujavaProject, String mutantID) {

		IProject eclipseProject = mujavaProject.getResource().getProject();
		MuJavaMutantInfo info = new MuJavaMutantInfo(mujavaProject,
				eclipseProject.getName(), mutantID);
		IResource res = info.getResource();
		if (res.exists()) {
			try {
				info.open(null);
				return info;
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public int getObjID()
	{
		return objID;
	}
	
	public void setObjID(int id)
	{
		objID = id;
	}
	
	private List<File> files = null;

	public MuJavaMutantInfo() {
		// super.setFileName(FILENAME);
	}

	public MuJavaMutantInfo(MuJavaProject project, String eclipseProjectName, // IJavaElement
			// src,
			String mutantID) {

		String mutationOperatorName = MutantID.getMutationOperator(mutantID);

		this.setMuJavaProject(project);

		IPath mutantRoot = new Path(eclipseProjectName);
		mutantRoot = mutantRoot.append(project.getMutantDirectory());
		mutantRoot = mutantRoot.append(project.getName());
		mutantRoot = mutantRoot.append(mutationOperatorName);
		setMutantRootDirectory(mutantRoot.toOSString());

		// String fileName = this.getFileName();
		// ICompilationUnit unit = (ICompilationUnit) src;
		// IType type = unit.findPrimaryType();
		// String wrapperClass = type.getFullyQualifiedName();
		// this.setWrapperClass(wrapperClass);
		// this.setOriginalClass(wrapperClass + "_Original");
		// this.setMutantClass(wrapperClass + "_Mutant");
		//
		// String dir;
		//
		// if (type.getFullyQualifiedName().equals(type.getElementName())) {
		// dir = getMutantRootDirectory();
		// } else {
		// int lastIndex = type.getFullyQualifiedName().lastIndexOf('.');
		// String packageClassName = wrapperClass.substring(0, lastIndex);
		// dir = getMutantRootDirectory() + UIHelper.directorySeparator
		// + packageClassName.replace('.',
		// UIHelper.directorySeparator.charAt(0));
		// }
		//
		// setWrapperFileName(dir + UIHelper.directorySeparator
		// + type.getElementName() + ".java");
		//
		// this.files = getAllChildFiles(getMutantFileName());

		// setup for Property
		// super.setFileName(getMutantRootDirectory()
		// + UIHelper.directorySeparator + FILENAME);
		setMutantID(mutantID);
		setSizeOfSubID(1);

		IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		IPath mutantFile = mutantRoot.append(mutantID);
		mutantFile = mutantFile.addFileExtension("prop");
		IFile file = rt.getFile(mutantFile);

		this.setResource(file);
	}

	public MuJavaMutantInfo(String mutantID, String mutationOperatorName,
			String parentClass, String parentMethod, int GenWay,
			int sizeOfSubID, int originalSubType, int subType,
			int changeLocation, String changeLog) {

		this.setChangeLocation(changeLocation);
		this.setChangeLog(changeLog);
		this.setGenerationWay(GenWay);
		this.setMutationOperatorName(mutationOperatorName);
		this.setMutantID(mutantID);
		this.setParentMethodName(parentMethod);
		this.setParentClassName(parentClass);
		this.setSizeOfSubID(sizeOfSubID);
		this.setSubTypeOperator(subType);
		this.setOriginalSubTypeOperator(originalSubType);
	}

	/**
	 * combined string, 1-based index and mutant operator's name
	 * 
	 * @param mutantID
	 *            id string
	 */
	// public void setLocalMutantID(String mutantID) {
	// super.setValue("LocalMutantID", mutantID);
	// }
	// public String getLocalMutantID() {
	// return super.getValue("LocalMutantID");
	// }
	/**
	 * Compare this to another MuJavaMutantInfo object.
	 * 
	 * @param arg
	 *            another object
	 * @see Comparable
	 */
	public int compareTo(MuJavaMutantInfo arg) {
		String compID = arg.getMutantID();
		String origID = this.getMutantID();

		StringTokenizer originalST = new StringTokenizer(compID, "_");
		StringTokenizer comparedST = new StringTokenizer(origID, "_");

		int dif = originalST.countTokens() - comparedST.countTokens();
		if (dif != 0)
			return dif;

		String originalOP = originalST.nextToken();
		String comparedOP = comparedST.nextToken();
		dif = originalOP.compareTo(comparedOP);
		if (dif != 0)
			return dif;

		String originalTarget = originalST.nextToken();
		String comparedTarget = comparedST.nextToken();
		dif = originalTarget.compareTo(comparedTarget);
		if (dif != 0)
			return dif;

		String originalChangePoint = originalST.nextToken();
		int originalValue = Integer.parseInt(originalChangePoint);
		String comparedChangePoint = comparedST.nextToken();
		int comparedValue = Integer.parseInt(comparedChangePoint);
		dif = originalValue - comparedValue;
		if (dif != 0)
			return -dif;

		String originalMutantID = originalST.nextToken();
		originalValue = Integer.parseInt(originalMutantID);
		String comparedMutantID = comparedST.nextToken();
		comparedValue = Integer.parseInt(comparedMutantID);
		dif = originalValue - comparedValue;

		return -dif;
	}

	@Override
	public void delete() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MuJavaMutantInfo) {

			MuJavaMutantInfo mutant = (MuJavaMutantInfo) obj;

			// if(mutant.getChangeLocation() == this.getChangeLocation()
			// && mutant.getMutantID()
			// .equalsIgnoreCase(this.getMutantID()))

			if (mutant.getMutantID().equalsIgnoreCase(this.getMutantID())
					&& mutant.getGenerationWay() == this.getGenerationWay())
				return true;
		}
		return false;
	}

	private List<File> getAllChildFiles(File file) {
		List<File> list = new ArrayList<File>();
		if (file.isDirectory()) {
			File[] listDir = file.listFiles();
			for (int i = 0; i < listDir.length; i++) {
				list.addAll(getAllChildFiles(listDir[i]));
			}
		} else if (file.isFile()) {
			list.add(file);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getChangeLocation()
	 */
	public int getChangeLocation() {
		String str = super.getValue("MutatedLine");
		if (str.length() == 0)
			str = "-1";
		return Integer.valueOf(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getChangeLog()
	 */
	public String getChangeLog() {
		return super.getValue("Log");
	}

	public String getCodeID() {
		return super.getValue("CodeSet");
	}

	public int getFirstSubTypeOperator() {
		return super.getIntValue("FirstSubType");
	}

	public int getGenerationWay() {
		return super.getIntValue("GenerationWay");
	}

	public int getMaximumSubType() {
		return super.getIntValue("MaxSubType");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getOriginalFileName()
	 */
	// public String getOriginalFileName() {
	// String fileName = getWrapperFileName();
	// String name = fileName.substring(0, fileName.length() - 5);
	// return name + "_Original.java";
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getProject()
	 */
	public String getMuJavaProject() {
		return super.getValue("Project");
	}

	public String getMuJavaProjectName() {
		return super.getValue("ProjectName");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getMutantClass()
	 */
	// public String getMutantClass() {
	// String name = super.getValue("Wrapper");
	// return name + "_Mutant";
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getOriginalClass()
	 */
	// public String getOriginalClass() {
	// String name = super.getValue("Wrapper");
	// return name + "_Original";
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getMutantFileName()
	 */
	// public String getMutantFileName() {
	// String fileName = getWrapperFileName();
	// String name = fileName.substring(0, fileName.length() - 5);
	// return name + "_Mutant.java";
	// }
	/**
	 * Return its global mutant identifier
	 * 
	 * @see mujava.inf.IMutantInfo#getMutantID()
	 */
	public String getMutantID() {
		return super.getValue("MutantID");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getMutantRootDirectory()
	 */
	public String getMutantRootDirectory() {
		return super.getValue("MutantRootDir");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getType()
	 */
	public int getMutantType() {
		String value = super.getValue("MutantType");
		if (value.length() == 0) {
			return 0;
		}

		return Integer.parseInt(value);
	}

	/**
	 * Return the name of mutant operator used in generting this mutant
	 * 
	 * @see mujava.inf.IMutantInfo#getMutationOperatorName()
	 */
	public String getMutationOperatorName() {
		String value = super.getValue("MutationOperator");
		return (value == null) ? "" : value;
	}

	public int getOriginalOperator() {
		return 0;
	}

	public int getOriginalSubTypeOperator() {
		return super.getIntValue("OriginalSubType");
	}

	public String getParentClassName() {
		return super.getValue("ParentClassName");
	}

	public String getParentMethodName() {
		return super.getValue("ParentMethodName");
	}

	public String getSubCodeID() {
		return super.getValue("SubCodeSet");
	}

	public int getSubTypeOperator() {
		return super.getIntValue("CurrentSubType");
	}

	public String getTargetFile() {
		return super.getValue("TargetFile");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getWrapperClass()
	 */
	// public String getWrapperClass() {
	// return super.getValue("Wrapper");
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#getWrapperFileName()
	 */
	public String getWrapperFileName() {
		return super.getValue("MutantFileName");
	}

	@Override
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {
		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();
		} else if (!"".equals(super.getFileName())) {
			super.openProperty();
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. saving without Resource variable");
		}
	}

	public void refreshFiles() {
		File file = new File(getMutantRootDirectory());
		this.files = getAllChildFiles(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#remove()
	 */
	public void remove() {
		// delete all file and directory
	}

	@Override
	public void save(IProgressMonitor monitor) throws IOException {
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

			// try {
			// IMarker marker = file.createMarker("MuJava.mujavaMarker");
			// marker.setAttribute("Type", "MutantInfo");
			// } catch (CoreException e) {
			// e.printStackTrace();
			// }
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. saving without Resource variable");
		}

	}

	// public boolean isSameTarget(Object userObject) {
	// if (userObject instanceof MuJavaMutantInfo) {
	// MuJavaMutantInfo info = (MuJavaMutantInfo) userObject;
	// if (info.getOriginalClass().equals(this.getOriginalClass()))
	// return true;
	// }
	//
	// return false;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setChangeLocation(int)
	 */
	public void setChangeLocation(int line_num) {
		super.setValue("MutatedLine", String.valueOf(line_num));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setChangeLog(java.lang.String)
	 */
	public void setChangeLog(String log_str) {
		super.setValue("Log", log_str);
	}

	public void setCodeID(String id) {
		super.setValue("CodeSet", id);
	}

	public void setFirstSubTypeOperator(int i) {
		super.setValue("FirstSubType", Integer.toString(i));
	}

	public void setGenerationWay(int way) {
		super.setValue("GenerationWay", Integer.toString(way));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setProject(mujava.MuJavaProject)
	 */
	public void setMuJavaProject(MuJavaProject project) {
		super.setValue("Project", project.getResource().getFullPath()
				.toString());
		super.setValue("ProjectName", project.getName());
	}

	public void setMutantedClassName(String string) {
		super.setValue("MutatedClassName", string);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setMutantID(java.lang.String)
	 */
	public void setMutantID(String id) {
		super.setValue("MutantID", id);
	}

	/**
	 * Copy new values of each field to this object except for the fields
	 * related to eclipse resources and generated code set
	 * 
	 * @param newValues
	 */
	public void setMutantInfo(MuJavaMutantInfo newValues) {
		this.setChangeLocation(newValues.getChangeLocation());
		this.setChangeLog(newValues.getChangeLog());
		this.setFirstSubTypeOperator(newValues.getFirstSubTypeOperator());
		this.setGenerationWay(newValues.getGenerationWay());
		this.setMutationOperatorName(newValues.getMutationOperatorName());
		this.setMutantType(newValues.getMutantType());
		this.setSizeOfSubID(newValues.getMaximumSubType());
		this.setSubTypeOperator(newValues.getSubTypeOperator());
		this.setTargetFile(newValues.getTargetFile());
		this.setParentMethodName(newValues.getParentMethodName());
		this.setParentClassName(newValues.getParentClassName());

		// mutantInfo.setLocalMutantID(localID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setMutantRootDirectory(java.lang.String)
	 */
	private void setMutantRootDirectory(String string) {
		super.setValue("MutantRootDir", string);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setType(int)
	 */
	public void setMutantType(int type) {
		super.setValue("MutantType", String.valueOf(type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setMutantOperator(java.lang.String)
	 */
	public void setMutationOperatorName(String name) {
		super.setValue("MutationOperator", name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setOriginalClass(java.lang.String)
	 */
	public void setOriginalClass(String string) {
		super.setValue("Original", string);
	}

	private void setOriginalSubTypeOperator(int originalSubType) {
		super.setValue("OriginalSubType", Integer.toString(originalSubType));
	}

	public void setParentClassName(String name) {
		super.setValue("ParentClassName", name);
	}

	public void setParentMethodName(String name) {
		super.setValue("ParentMethodName", name);
	}

	public void setSizeOfSubID(int i) {
		super.setValue("MaxSubType", Integer.toString(i));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setState(int)
	 */
	public void setState(int state) {
		super.setValue("State", String.valueOf(state));
	}

	public void setSubCodeID(String id) {
		super.setValue("SubCodeSet", id);
	}

	public void setSubTypeOperator(int i) {
		super.setValue("CurrentSubType", Integer.toString(i));

	}

	public void setTargetFile(String targetFile) {
		super.setValue("TargetFile", targetFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setWrapperClass(java.lang.String)
	 */
	public void setWrapperClass(String packageClassName) {
		super.setValue("Wrapper", packageClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IMutantInfo#setWrapperFileName(java.lang.String)
	 */
	public void setWrapperFileName(String mutantFileName) {
		super.setValue("MutantFileName", mutantFileName);
	}
}

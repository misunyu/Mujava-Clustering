package mujava;

/**
 * We assume all eneries are not deleted. 
 * If an entry is inserted, it's ID will fixed permenantly.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Set;

import kaist.selab.util.MuJavaLogger;
import kaist.selab.util.PropertyFile;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.internal.ide.dialogs.IFileStoreFilter;

public class MutantTable extends PropertyFile {
	private static Hashtable<MuJavaProject, MutantTable> tables = new Hashtable<MuJavaProject, MutantTable>();

	/**
	 * MuJava Eclipse Plug-in 에서 사용하기 위한 Interface.
	 * 
	 * @param project
	 * @param monitor
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static MutantTable getMutantTable(MuJavaProject project,
			IProgressMonitor monitor) throws IOException, CoreException {

		MuJavaLogger.getLogger().debug("Make Mutant Table to be ready to use.");
		if (monitor != null) {
			monitor.subTask("Make Concreate Mutant Table");
		}

		MutantTable table = tables.get(project);

		if (table == null) {
			table = new MutantTable(project);

			IResource muJavaProjectFile = project.getResource();
			IProject prj = muJavaProjectFile.getProject();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IPath path = prj.getFullPath();
			path = path.append(project.getMutantDirectory());
			path = path.append(project.getName());
			path = path.append(project.getName());
			path = path.addFileExtension("table");

			IFile file = root.getFile(path);
			table.setResource(file);

			try {
				table.open(monitor);
			} catch (Exception e) {
				table.save(monitor);
			}
			tables.put(project, table);
		}

		return table;
	}

	/**
	 * MuJava 로 생성된 MutantTable을 외부에서 접근하기 위한 Interface.
	 * 
	 * 새로 생성하지 않고, 생성된 MutantTable만 읽어들인다. 하위 Mutant Table을 읽는 도중 문제가 발생하면 Null을
	 * 반환한다.
	 * 
	 * @param project
	 * @param monitor
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static MutantTable getMutantTable(String mutantTableFilePath)
			throws IOException, CoreException {

		MutantTable table = new MutantTable(null);
		table.setFileName(mutantTableFilePath);

		// 하위 Mutant Table을 읽는 도중 문제가 발생하면 Null을 반환한다.
		try {
			table.open(null);
		} catch (Exception e) {
			return null;
		}

		return table;
	}

	MuJavaProject mujavaProject = null;

	List<MuJavaMutantInfo> mutantsList = null;

	private Hashtable<String, MuJavaMutantInfo> mutants = new Hashtable<String, MuJavaMutantInfo>();

	protected MutantTable(MuJavaProject project) {
		this.mujavaProject = project;
		this.setFileComment("Mutant Report Table");
	}

	public void addMutant(String operatorName, String mutantID) {
		int count = getIntValue(operatorName);
		super.setValue(operatorName, String.valueOf(++count));
		super.setValue(operatorName + String.valueOf(count), mutantID);
	}

	@Override
	public void delete() {
	}

	public List<String> getAllMutantID() {
		ArrayList<String> list = new ArrayList<String>();
		List<String> ops = MutantOperator
				.getAllTraditionalOperators();
		for (String op : ops) {
			list.addAll(getMutantIDs(op));
		}

		ops = MutantOperator.getAllClassicalOperators();
		for (String op : ops) {
			list.addAll(getMutantIDs(op));
		}

		return list;
	}

	public Set<String> getMutantIDs(String operator) {
		Set<String> set = new HashSet<String>();

		for (int i = 0; i < super.getIntValue(operator); i++) {
			String mutantID = super.getValue(operator + String.valueOf(i + 1));
			set.add(mutantID);
		}

		return set;
	}

	/**
	 * 
	 * @param opName
	 *            mutant operator
	 * @return mutant ID, consists of mutant operator's name and the number of
	 *         mutants
	 */
	public String getLastMutantID(String opName) {
		int count = this.getIntValue(opName);
		if (count == 0) {
			return opName + "_0";
		}
		// String mutantID = this.getValue(opName+count);
		//		
		// assert(mutantID != null);
		// assert(mutantID != "");
		//		
		// int index = mutantID.lastIndexOf("_");
		// int newID = Integer.valueOf(mutantID.substring(index+1));
		return opName + "_" + String.valueOf(count);
		// IProject eclipseProject = this.getResource().getProject();
		// IPath filePath = this.getResource().getProjectRelativePath();
		// filePath = filePath.removeLastSegments(1);
		// filePath = filePath.append(this.mujavaProject.getName());
		//
		// int i = 0;
		// while (true) {
		// IPath tempPath = filePath.append(opName+ "_" + i);
		// IFolder f = eclipseProject.getFolder(tempPath);
		// if (f.exists()) {
		// i++;
		// continue;
		// } else {
		// return opName + "_" + i;
		// }
		// }
	}

	public int getMutantCount(String operator) {
		return super.getIntValue(operator);
	}

	// public List<MuJavaMutantInfo> getAllMutants() {
	// if (mutantsList == null) {
	// mutantsList = new ArrayList<MuJavaMutantInfo>();

	// String[] ops = IMutationSystem.tm_operators;
	// mutantsList.addAll(getMutantFileListByOperatorSet(ops));
	// ops = IMutationSystem.cm_operators;
	// mutantsList.addAll(getMutantFileListByOperatorSet(ops));
	// }
	// return mutantsList;
	// }

	// /**
	// * @param fileName
	// * full path string relative from the root
	// */
	// public List<String> getMutantIDListByTargetFile(String fileName) {
	// ArrayList<String> list = new ArrayList<String>();
	//
	// // 주어진 filename으로부터 hashcode를 구한다.
	// IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	// IResource res = root.findMember(new Path(fileName));
	// int givenHashCode = res.hashCode();
	//
	// // 모드 MutantID에 대해 주어진 FileName에 해당하는 것과 같은 hashcode를 가지는 mutantID를
	// // 추적한다.
	// List<String> mutantIDs = this.getAllMutantID();
	// for (String mutantID : mutantIDs) {
	// int hashCode = MutantID.getHashCode(mutantID);
	// if (hashCode == givenHashCode) {
	// list.add(mutantID);
	// }
	// }
	//
	// return list;
	// }

	// List<MuJavaMutantInfo> getMutantInfoList(String operator) {
	// ArrayList<MuJavaMutantInfo> list = new ArrayList<MuJavaMutantInfo>();
	// for (int i = 0; i < super.getIntValue(operator); i++) {
	// String mutantID = super.getValue(operator + String.valueOf(i + 1));
	//
	// // TODO mutant를 저장하는게 빠른지 아닌지 비교후, 제거해주어야 하는 부
	// MuJavaMutantInfo mutantInfo = mutants.get(mutantID);
	// if (mutantInfo == null) {
	// mutantInfo = MuJavaMutantInfo.getMuJavaMutantInfo(
	// this.mujavaProject, mutantID);
	// }
	// if (mutantInfo != null) {
	// mutants.put(mutantID, mutantInfo);
	// list.add(mutantInfo);
	// }
	//
	// }
	// return list;
	// }

	public MuJavaMutantInfo getMutantInfo(String mutantID) {
		// TODO mutant를 저장하는게 빠른지 아닌지 비교후, 제거해주어야 하는 부분.

		MuJavaMutantInfo mutantInfo = this.mutants.get(mutantID);
		if (mutantInfo == null) {
			if (mujavaProject != null) {
				mutantInfo = MuJavaMutantInfo.getMuJavaMutantInfo(
						mujavaProject, mutantID);
			} else {
				IPath path = new Path(this.getFileName());
				path = path.removeFileExtension();
				path = path.removeLastSegments(1);
				path = path.append(MutantID.getMutationOperator(mutantID));
				path = path.append(mutantID);
				path = path.addFileExtension("prop");

				mutantInfo = new MuJavaMutantInfo();
				mutantInfo.setFileName(path.toOSString());

				try {
					mutantInfo.open(null);
				} catch (InvalidPropertiesFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (mutantInfo != null) {
			this.mutants.put(mutantID, mutantInfo);
		}

		return mutantInfo;
	}

	// // 주어진 operator에 해당하는 MutantInfo들을 찾아준다.
	// public MuJavaMutantInfo[] getMutantInfos(String operator) {
	// ArrayList<MuJavaMutantInfo> list = new ArrayList<MuJavaMutantInfo>();
	// for (int i = 0; i < super.getIntValue(operator); i++) {
	// String mutantID = super.getValue(operator + String.valueOf(i + 1));
	//
	// // TODO mutant를 저장하는게 빠른지 아닌지 비교후, 제거해주어야 하는 부
	// MuJavaMutantInfo mutantInfo = mutants.get(mutantID);
	// if (mutantInfo == null) {
	// mutantInfo = MuJavaMutantInfo.getMuJavaMutantInfo(
	// this.mujavaProject, mutantID);
	// }
	// if (mutantInfo != null) {
	// mutants.put(mutantID, mutantInfo);
	// list.add(mutantInfo);
	// }
	//
	// }
	// return (MuJavaMutantInfo[]) list.toArray(new MuJavaMutantInfo[list
	// .size()]);
	// }

	@Override
	/*
	 * super.resource and this.mujavaProject fields must be initialized before
	 * the function calls
	 */
	public void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException {

		if (super.resource != null) {
			super.setFileName(super.resource.getLocation().toOSString());
			super.openProperty();

			tables.put(mujavaProject, this);
		} else if (!"".equals(super.getFileName())) {
			super.openProperty();
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. opening without Resource variable in MutantTable");
		}
	}

	// clear the relation between MuJavaMutantInfo and mutantID and
	// so if you call the getMutantInfos function,
	// the function will generate newly information from the local storage.
	// public void refresh() {
	// this.mutants.clear();
	// }

	/**
	 * resource field must be initialized before this function calls This is
	 * called only two cases, 1) after the class is created. 2) after some
	 * mutants are created.
	 */
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
			// set marker for mutantTable
			// try {
			// IMarker marker = file.createMarker("MuJava.mujavaMarker");
			// marker.setAttribute("Type", "MutantTable");
			// } catch (CoreException e) {
			// e.printStackTrace();
			// }
		} else {
			MuJavaLogger
					.getLogger()
					.error(
							"Unacceptable operation.. saving without Resource variable in MutantTable");
		}
	}

	public boolean isExistedMutantID(String mutantID) {
		String operator = MutantID.getMutationOperator(mutantID);

		Set<String> IDs = this.getMutantIDs(operator);
		if (IDs.contains(mutantID))
			return true;

		return false;
	}

	// public boolean isExist(MuJavaMutantInfo target) {
	// String op = target.getMutantOperator();
	// MuJavaMutantInfo[] infos = this.getMutantInfos(op);
	// for (MuJavaMutantInfo info : infos) {
	// if (info.getMutantID().equalsIgnoreCase(target.getMutantID()))
	// return true;
	// }
	//
	// return false;
	// }

	public static void deleteCache(MuJavaProject project) {
		tables.remove(project);
	}

}

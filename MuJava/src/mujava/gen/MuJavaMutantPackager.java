package mujava.gen;

import mujava.CodeSet;
import mujava.MutantPackager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jdt.ui.jarpackager.JarWriter3;

public class MuJavaMutantPackager extends MutantPackager {

	public void packageMutants(CodeSet codeSet) {
		//make jar file
		IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		IPath mutantRootPath = codeSet.getRootDirectory();
		IPath location = mutantRootPath.append(codeSet.getID() + ".jar");
		IFile ifile = rt.getFile(location);

		JarPackageData description = new JarPackageData();
		description.setJarLocation(ifile.getLocation());
		description.setSaveManifest(true);
		description.setOverwrite(true);

		//			IFile[] files = new IFile[] { (IFile)mujavaProject.getResource() };
		IFile[] files = new IFile[] { null };
		description.setElements(files);

		IFolder targetFolder = rt.getFolder(mutantRootPath);

		try {
			targetFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			JarWriter3 writer = description.createJarWriter3(null);
			writeRecursive(writer, targetFolder, mutantRootPath);
			writer.close();

			codeSet.setArchiveName(ifile.getRawLocation().toOSString());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	private IPath toRelativePath(IPath fullPath, IPath root) {
		if (fullPath.toOSString().startsWith(root.toOSString()))
			return fullPath.removeFirstSegments(root.segmentCount());

		return fullPath;
	}

	private void writeRecursive(JarWriter3 writer, IFolder targetFolder,
			IPath rootPath) throws CoreException {
		IResource[] res = targetFolder.members(true);
		for (IResource resource : res) {
			if (resource instanceof IFolder) {
				writeRecursive(writer, (IFolder) resource, rootPath);

			} else if (resource instanceof IFile) {
				if (!resource.getName().endsWith(".jar")) {
					IPath rootRelativePath = toRelativePath(resource
							.getFullPath(), rootPath);
					writer.write((IFile) resource, rootRelativePath);
				}
			}
		}
	}
}

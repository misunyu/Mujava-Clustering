package mujava.gen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.Helper;
import kaist.selab.util.MuJavaLogger;
import mujava.CodeSet;
import mujava.plugin.MuJavaPlugin;
import mujava.util.MutantCompiler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class MuJavaMutantCompiler extends MutantCompiler {

	private boolean searchFinish;
	private List<IResource> res = new ArrayList<IResource>();

	String getWorkspaceRelativeFile(IPath relativePath) {
		return getWorkspaceRelativeFile(relativePath.toOSString());
	}

	String getWorkspaceRelativeFile(String relativePath) {
		String path = "";

		Location wsLoc = Platform.getInstanceLocation();
		if (wsLoc != null) {
			try {
				URL url = FileLocator.toFileURL(wsLoc.getURL());
				String file = url.getFile();
				if (!file.isEmpty()) {
					IPath newPath = new Path(file);
					newPath = newPath.append(relativePath);
					path = newPath.toFile().getAbsolutePath();
				}
			} catch (IOException e) {
			}
		}

		return path;
	}

	private void makeLibEntries(IJavaProject javaProject,
			IClasspathEntry[] entries, List<String> libPaths)
			throws JavaModelException {

		for (IClasspathEntry entry : entries) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				IPath rawPath = entry.getPath();
				String name = getWorkspaceRelativeFile(rawPath);
				if (!name.isEmpty()) {
					libPaths.add(name);
				}
			} else if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				IPath rawPath = entry.getPath();
				IClasspathContainer container = JavaCore.getClasspathContainer(
						rawPath, javaProject);
				if (container.getKind() != IClasspathContainer.K_DEFAULT_SYSTEM) {
					makeLibEntries(javaProject,
							container.getClasspathEntries(), libPaths);
				}
			}
		}
	}
	@Override
	public boolean compileMutants(IProject eclipseProject,
			IJavaElement sourceElement, CodeSet codeSet,
			IProgressMonitor monitor) {
		IJavaProject javaProject = JavaCore.create(eclipseProject);

		// List for library paths
		List<String> libPaths = new ArrayList<String>();

		// add output directory (or bin directory) to classpath
		try {
			String path = getWorkspaceRelativeFile(javaProject
					.getOutputLocation());
			if (!path.isEmpty()) {
				libPaths.add(path);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// add project library path to classpath
		try {
			IClasspathEntry[] paths = javaProject.getRawClasspath();
			makeLibEntries(javaProject, paths, libPaths);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// MuJava에 관련된 Library들을 link한다.
		try {
			String requires = (String) MuJavaPlugin.getDefault().getBundle()
					.getHeaders().get(Constants.BUNDLE_CLASSPATH);
			ManifestElement[] elements = ManifestElement.parseHeader(
					Constants.BUNDLE_CLASSPATH, requires);
			for (int i = 0; i < elements.length; i++) {
				String name = elements[i].getValue();
				URL url = MuJavaPlugin.getDefault().getBundle().getEntry(name);
				if (url != null) {
					url = FileLocator.toFileURL(url);
					String urlFileName = url.getFile();
					if (urlFileName != null && !urlFileName.isEmpty()) {
						String path = new File(urlFileName).getAbsolutePath();
						if (!libPaths.contains(path)) {
							libPaths.add(path);
						}
					}
				}
			}
		} catch (BundleException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Make a string for compiler option, classpath
		StringBuffer buf = new StringBuffer();
		buf.append("-classpath ");
		for (String string : libPaths) {
			buf.append(string + Helper.pathSeparate);
		}
		buf.append(".");

		// Make a string listing all target source files from the code set
		StringBuffer sb = new StringBuffer();
		// IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		for (String file : codeSet.getRelativePathNames()) {
			String fullPath = getWorkspaceRelativeFile(file);
			if (!fullPath.isEmpty()) {
				sb.append(fullPath + " ");
			}
		}

		// Gather related original source files.
		// (In case of JSI, inserting static keyword to the mutant class, all
		// related java classes are should be recompiled because the structure
		// of the original is changed. If do not, IcomparatibleException is
		// raised when the mutants are executed.
		IJavaSearchScope scope = SearchEngine
				.createJavaSearchScope(new IJavaElement[] { sourceElement
						.getJavaProject() });

		final Object lockObject = new Object();
		SearchEngine engine = new SearchEngine();
		this.res.clear();

		try {
			IType[] types = ((ICompilationUnit) sourceElement).getTypes();

			for (IType type : types) {

				SearchPattern pattern = SearchPattern.createPattern(type,
						IJavaSearchConstants.REFERENCES);

				setSearchFinish(false);

				try {
					engine.search(pattern,
							new SearchParticipant[] { SearchEngine
									.getDefaultSearchParticipant() }, scope,
							new SearchRequestor() {
								@Override
								public void acceptSearchMatch(SearchMatch match)
										throws CoreException {
									IResource res = match.getResource();
									IPath path = res.getFullPath();
									if(!path.toString().contains("/test/")){
										if (!"TEST".equalsIgnoreCase(path
												.segment(1))) {
											addRelatedClasses(res);
										}
									}
								}

								@Override
								public void beginReporting() {
									super.beginReporting();

								}

								@Override
								public void endReporting() {
									setSearchFinish(true);
									synchronized (lockObject) {
										lockObject.notify();
									}

								}
							}, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}

				synchronized (lockObject) {
					if (!searchFinish)
						try {
							lockObject.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}

		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

		// Obtaining source file names from the the collected Resources
		for (IResource resource : res) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				// However, target source file is eliminated from the list
				if (file.equals(sourceElement.getResource()))
					continue;
				// Only java resource files are considered.
				if (file.getFileExtension().equalsIgnoreCase("java")) {
					sb.append(getWorkspaceRelativeFile(file.getFullPath())
							+ " ");
				}
			}
		}
		String targetFiles = sb.toString().trim();

		// Inform the debug information
		//MuJavaLogger.getLogger().debug("*** Compile Mutant codes ***");

		if (monitor != null) {
			monitor.subTask("Compiling generated codes : " + targetFiles);
		}

		// Prepare IO stream for compiling
		ByteArrayOutputStream errStream = new ByteArrayOutputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(outStream);
		PrintWriter err = new PrintWriter(errStream);

		/**
		 * Do not use System Java Compiler .. Since SE6.0, because we would //
		 * //not use the JDK. // JavaCompiler compiler =
		 * ToolProvider.getSystemJavaCompiler(); //
		 * DiagnosticCollector<JavaFileObject> diagnostics = new //
		 * DiagnosticCollector<JavaFileObject>(); // StandardJavaFileManager
		 * fileManager = // compiler.getStandardFileManager( // diagnostics,
		 * null, null); // Iterable<? extends JavaFileObject> compilationUnits =
		 * fileManager // .getJavaFileObjectsFromStrings(targets); //
		 * CompilationTask task = compiler.getTask(errWriter, fileManager, null,
		 * // Arrays.asList(buf.toString()), null, compilationUnits); // boolean
		 * result = task.call();
		 * 
		 */
		MuJavaLogger.getLogger().execCmd(
				"javac " + buf.toString() + " -source 1.8 -d "
						+ getWorkspaceRelativeFile(codeSet.getRootDirectory())
						+ " " + targetFiles);
		boolean result = Main.compile(buf.toString() + " -source 1.8 -d "
				+ getWorkspaceRelativeFile(codeSet.getRootDirectory()) + " "
				+ targetFiles, out, err);

		
		if (result) {
			//MuJavaLogger.getLogger().debug("Compile Done");
		} else {
			// When the compile is not executed, related files should be
			// deleted.
			MuJavaLogger.getLogger().error(outStream.toString());
			MuJavaLogger.getLogger().error(errStream.toString());
			MuJavaLogger.getLogger().error("*** Compile Fail ***");
		}

		return result;
	}

	protected void addRelatedClasses(IResource resource) {
		if (!res.contains(resource))
			this.res.add(resource);
	}

	protected void setSearchFinish(boolean b) {
		this.searchFinish = b;
	}
}

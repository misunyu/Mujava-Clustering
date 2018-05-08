package mujava.gen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import kaist.selab.util.MuJavaLogger;
import mujava.CodeSet;
import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

public class MuJavaMutantFileGenerator implements IMutantFileGenerator {
	private void createParentFolder(IPath path) {
		if (path.segmentCount() < 2)
			return;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 2; i < path.segmentCount(); i++) {
			IPath newPath = path.uptoSegment(i);
			IFolder folder = root.getFolder(newPath);
			try {
				if (!folder.exists())
					folder.create(true, false, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public CodeSet generate(IPath path, ICompilationUnit sourceCode,
			CompilationUnit parsedTarget, MutantWriter mutantWriter,
			IProgressMonitor monitor) throws IOException {

		List<String> pathNames = new ArrayList<String>();

		CodeSet codeSet = CodeSet.getNewCodeSet(path, mutantWriter
				.getMutationOperatorName());
		IPath root = codeSet.getRootDirectory();
		String packageName = parsedTarget.getPackage();
		IPath fileNamePath = root;

		String fileName = sourceCode.getElementName();

		if(packageName!=null){		// added by ysma at 2013/10/18
			StringTokenizer st = new StringTokenizer(packageName, ".");
			while (st.hasMoreElements()) {
				String temp = st.nextToken();
				fileNamePath = fileNamePath.append(temp);
			}
		}

		fileNamePath = fileNamePath.append(fileName);
		codeSet.setTargetFile(fileNamePath.toString());

		IWorkspaceRoot rt = ResourcesPlugin.getWorkspace().getRoot();
		IFile mutantFile = rt.getFile(fileNamePath);

		// initialize the state of mutant writer
		mutantWriter.resetSizeOfLastAppliedChangePoint();
		mutantWriter.setTargetFileHashCode(sourceCode.getPath().hashCode());
		//mutantWriter.setTargetFileHashCode(hashCode);
		// ICompilationUnit sourceCode.getPath().hashCode()

		// generate
		if (monitor != null)
			monitor.subTask(mutantWriter.getMutationOperatorName()
					+ "- Rewriting mutant code for :" + mutantFile.getName());

		try {
			parsedTarget.accept(mutantWriter);

			// check the size of generated mutants and do not generate codeset
			// if there is not any generated mutant
			int size = mutantWriter.getSizeOfLastAppliedChangePoint();
			if (size <= 0) {
				return null;
			}

			codeSet.setSizeOfChangePoint(size);

			// make concreate files
			ByteArrayInputStream is = new ByteArrayInputStream(mutantWriter
					.getByte());
			try {
				if (mutantFile.exists())
					mutantFile.setContents(is, true, false, null);
				else {
					createParentFolder(mutantFile.getFullPath());
					mutantFile.create(is, false, null);
				}

				pathNames.add(mutantFile.getFullPath().toOSString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			is.close();
			mutantWriter.resetWriter();

			codeSet.setRelativePathNames(pathNames);

		} catch (ParseTreeException e) {
			MuJavaLogger.getLogger().error(
					"errors during printing " + mutantFile.getName());
			e.printStackTrace();
		}

		return codeSet;
	}
}

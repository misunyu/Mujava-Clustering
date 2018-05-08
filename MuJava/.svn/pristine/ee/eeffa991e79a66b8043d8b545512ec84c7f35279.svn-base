package mujava.gen;

import java.io.IOException;

import mujava.CodeSet;
import mujava.gen.writer.MutantWriter;
import openjava.ptree.CompilationUnit;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

public interface IMutantFileGenerator {
	public CodeSet generate(IPath path, ICompilationUnit sourceFile,
			CompilationUnit targetClass, MutantWriter writer,
			IProgressMonitor monitor) throws IOException;
	// public void generate(CodeSet codeSet, CompilationUnit targetClass,
	// ClassicalMutantNormalWriter mutantWriter ) throws IOException;
}

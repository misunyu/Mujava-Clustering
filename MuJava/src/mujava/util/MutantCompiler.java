package mujava.util;

import mujava.CodeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;

public abstract class MutantCompiler {
	public abstract boolean compileMutants(IProject eclipseProject,
			IJavaElement sourceJavaElement, CodeSet codeSet,
			IProgressMonitor monitor);
}

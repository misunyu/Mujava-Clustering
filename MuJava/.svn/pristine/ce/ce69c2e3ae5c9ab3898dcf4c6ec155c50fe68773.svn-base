////////////////////////////////////////////////////////////////////////////
// Module : DeclAnalyzer.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.op.util;

import java.io.IOException;

import mujava.MuJavaProject;
import mujava.gen.GenericCodeWriter;
import mujava.inf.IMutantInfo;
import openjava.mop.OJClass;
import openjava.mop.OJField;
import openjava.ptree.CompilationUnit;

import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @version 1.0
 */

public abstract class DeclAnalyzer extends OJClass {
	private String operatorName = new String();
	private MuJavaProject mujavaProject = null;
	private IJavaElement originalSourceFile = null;
	protected CompilationUnit targetClass = null;

	public abstract void translateDefinition(CompilationUnit comp_unit)
	throws openjava.mop.MOPException;
	
	// Examine if OJField f1 and f2 are same.
	// It is used for hiding variable.
	public boolean equalFieldByNameAndType(OJField f1, OJField f2) {
		return (f1.getName().equals(f2.getName()))
				&& (f1.getType() == f2.getType());
	}

	public DeclAnalyzer(openjava.mop.Environment oj_param0,
			openjava.mop.OJClass oj_param1,
			openjava.ptree.ClassDeclaration oj_param2,
			MuJavaProject mujavaProject, org.eclipse.jdt.core.IJavaElement originalSourceFile, String opName) {
		super(oj_param0, oj_param1, oj_param2);
		this.mujavaProject  = mujavaProject;
		this.originalSourceFile = originalSourceFile;
		this.operatorName = opName;
	}

	protected String getOperatorName() {
		return operatorName;
	}

	protected IJavaElement getOriginalSourceFile() {
		return originalSourceFile;
	}

	protected abstract int getMutantType();

	protected abstract GenericCodeWriter getMutantCodeWriter(
			IMutantInfo mutantFile, Object[] args) throws IOException;

//	protected void outputToFile(FieldDeclaration original,
//			FieldDeclaration mutant) {
//		Object[] args = { original, mutant };
//		outputToFile(args);
//	}
//
//	protected void outputToFile(MethodDeclaration original) {
//		Object[] args = { original };
//		outputToFile(args);
//	}
//	protected void outputToFile(ConstructorDeclaration original) {
//		Object[] args = { original };
//		outputToFile(args);
//	}
//	protected void outputToFile(MethodDeclaration original, OJMethod method) {
//		Object[] args = { original, method };
//		outputToFile(args);
//	}
//	protected void outputToFile(MethodDeclaration original, String mutant) {
//		Object[] args = { original, mutant };
//		outputToFile(args);
//	}
//
//	protected void outputToFile(FieldDeclaration original) {
//		Object[] args = { original };
//		outputToFile(args);
//	}

//	private void outputToFile(Object[] args) {
//		
//		MutantManager mutantManager = MutantManager.getMutantManager();
//		MutantTable table = mutantManager.getMutantTable(this.mujavaProject);
//
//		assert (table == null) : "There is nothing about generated Mutant";
//
//		String mutantID = table.getMutantID(getOperatorName());
//		MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(this.mujavaProject,
//				this.originalSourceFile, mutantID);
//
//		try {
//			GenericCodeWriter mutantWriter = getMutantCodeWriter(mutantInfo,
//					args);
//			MutationSystem.getMutationSystem().getMutantWriter().generate(this.mujavaProject, 
//					targetClass, mutantWriter, mutantInfo);
//		} catch (IOException e1) {
//			System.err.println("fails to create "
//					+ mutantInfo.getMutantFileName());
//		}
//
//		// mutant properties are saved in the pre-defined file
//		mutantInfo.setMuJavaProject(this.mujavaProject.getName());
//		mutantInfo.setMutantID(mutantID);
//		mutantInfo.setMutantOperator(getOperatorName());
//		mutantInfo.setMutantType(getMutantType());
//		mutantInfo.refreshFiles();
//
//		try {
//			mutantInfo.save(mutantManager.getMonitor());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// compile generated mutant and original file with original class files
//		MutantCompiler compiler = MutationSystem.getMutationSystem()
//				.getMutantCompiler();
//		compiler.compileMutants(mutantInfo);
//
//		// store the generated mutant
//		// JarOutputStream jar = new JarOutputStream(new
//		// FileOutputStream("tmep.jar"));
//		// ZipEntry entry = new ZipEntry("asdf");
//		// entry.
//		// jar.putNextEntry(null);
//		// jar.closeEntry();
//
//		MutantManager.getMutantManager().addMutantFile(this.mujavaProject, mutantInfo);
//	}

	public void setTargetClass(CompilationUnit comp_unit) {
		this.targetClass = comp_unit;
	}

	public MuJavaProject getMujavaProject() {
		return this.mujavaProject;
	}

}

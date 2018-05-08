////////////////////////////////////////////////////////////////////////////
// Module : CallAnalyzer.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package openjava.ptree.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mujava.MuJavaMutantInfo;
import mujava.MuJavaProject;
import mujava.MutantManager;
import mujava.MutantTable;
import mujava.gen.GenericCodeWriter;
import mujava.inf.IMutantInfo;
import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJMethod;
import openjava.ptree.ArrayAccess;
import openjava.ptree.CompilationUnit;
import openjava.ptree.Expression;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * TO BE Eliminated
 * </P>
 * <p>
 * Description: File Analyzer for generating mutants
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @version 1.0
 */

public abstract class Mutator extends VariableBinder {
	// -------------------------------------
	private String operatorName = new String();

	private IJavaElement sourceFile = null;
	protected CompilationUnit targetClass = null;
	private Environment fileEnvironment;
	protected MethodDeclaration currentMethod;
	protected Hashtable<Expression, MethodDeclaration> targetExpressions = new Hashtable<Expression, MethodDeclaration>();

	public Mutator(Environment env, CompilationUnit targetClass,
			IJavaElement originalSourceFile, String opName) {
		super(env);
		this.fileEnvironment = env;
		this.sourceFile = originalSourceFile;
		this.operatorName = opName;
		this.targetClass = targetClass;
		assert (targetClass != null);
	}

	// TODO swkim
	// protected OJClass computeRefType(TypeName typename, Expression expr)
	// throws ParseTreeException {
	// if (typename != null)
	// return getType(typename);
	// if (expr != null)
	// return getType(expr);
	// return getSelfType();
	// }
	//
	// public String exclude(String a, String b) {
	// return a.substring(b.length() + 1, a.length());
	// }
	//
	// public String getClassName() {
	// Class cc = this.getClass();
	// return exclude(cc.getName(), cc.getPackage().getName());
	// }
	//
	// public String getMuantID() {
	// String str = getClassName() + "_" + this.num;
	// return str;
	// }

	protected void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	// protected int getCountGeneratedMutant() {
	// return num;
	// }

	protected abstract GenericCodeWriter getMutantCodeWriter(
			IMutantInfo mutantFile) throws IOException;

	protected abstract int getMutantType();

	public String getOperatorName() {
		return operatorName;
	}

	public IJavaElement getSourceJavaElement() {
		return sourceFile;
	}

	protected OJClass getSelfType() throws ParseTreeException {
		OJClass result;
		try {
			Environment env = getEnvironment();
			String selfname = env.currentClassName();
			result = env.lookupClass(selfname);
		} catch (Exception ex) {
			throw new ParseTreeException(ex);
		}
		return result;
	}

	// --------------

	protected OJClass getType(Expression p) throws ParseTreeException {
		OJClass result = null;

		if (p instanceof MethodCall) {
			// There are two cases in the MethodCall,
			// one is a general MethodCall located in normal class.
			// The other is a MethodCall located in anonymous class,
			// OpenJava does not support to find some methods from those yet.
			MethodCall m = (MethodCall) p;
			try {
				OJMethod method = getMethod(getEnvironment(), m, null);
				result = method.getReturnType();
			} catch (NoSuchMemberException e) {
				System.out.println(p.toFlattenString() + "not found.");
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				result = p.getType(getEnvironment());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (result == null) {
			System.err.println("cannot resolve the type of expression");
			System.err.println(p.getClass() + " : " + p);
			System.err.println(getEnvironment());
			/** ***DebugOut.println(getEnvironment().toString()); */
			if (p instanceof ArrayAccess) {
				ArrayAccess aaexpr = (ArrayAccess) p;
				Expression refexpr = aaexpr.getReferenceExpr();
				OJClass refexprtype = null;
				OJClass comptype = null;
				try {
					refexprtype = refexpr.getType(getEnvironment());
					comptype = refexprtype.getComponentType();
				} catch (Exception ex) {
				}
				System.err.println(refexpr + " : " + refexprtype + " : "
						+ comptype);
			}
		}
		return result;
	}

	protected OJClass getType(TypeName typename) throws ParseTreeException {
		OJClass result = null;
		try {
			Environment env = getEnvironment();
			String qname = env.toQualifiedName(typename.toString());
			result = env.lookupClass(qname);
		} catch (Exception ex) {
			throw new ParseTreeException(ex);
		}
		if (result == null) {
			System.err.println("unknown type for a type name : " + typename);
		}
		return result;
	}

	// protected void increaseGeneratedMutantCount() {
	// num++;
	// }

	private void outputToFile() {
		MutantManager manager = MutantManager.getMutantManager();

		MuJavaProject mujavaProject = manager.getMuJavaProject();
		MutantTable table = null;
		try {
			table = MutantTable.getMutantTable(mujavaProject, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		String mutantID = table.getLastMutantID(getOperatorName());
		MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(mujavaProject, "",
				mutantID);

		String mutantFileName = mutantInfo.getWrapperFileName();

		// for(Expression exp : targetExpressions.keySet()) {
		// if (exp instanceof CompilationUnit) {
		// try {
		// GenericCodeWriter mutantCodeWriter = getMutantCodeWriter(
		// mutantInfo);
		// manager.getMutantWriter().generate(null,
		// (CompilationUnit) exp, mutantCodeWriter);
		// } catch (IOException e1) {
		// System.err.println("fails to create "
		// + mutantInfo.getMutantFileName());
		// }
		// } else {
		// try {
		// GenericCodeWriter mutantCodeWriter = getMutantCodeWriter(
		// mutantInfo);
		// manager.getMutantWriter().generate(null,
		// targetClass, mutantCodeWriter);
		// } catch (IOException e1) {
		// System.err.println("fails to create "
		// + mutantInfo.getMutantFileName());
		// }
		// }
		// }

		// mutant properties are saved in the pre-defined file
		mutantInfo.setMuJavaProject(mujavaProject);
		mutantInfo.setMutantID(mutantID);
		mutantInfo.setMutationOperatorName(getOperatorName());
		mutantInfo.setMutantType(getMutantType());
		mutantInfo.refreshFiles();

		String fileName = mutantInfo.getFileName();

		IProject eclipseProject = mujavaProject.getResource().getProject();
		IFile file = eclipseProject.getFile(fileName);
		mutantInfo.setResource(file);
		try {
			mutantInfo.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// compile generated mutant and original file with original class files
		// store the generated mutant into one jar file
		List<String> files = new ArrayList<String>();
		files.add(mutantInfo.getWrapperFileName());

		// MutantCompiler compiler = manager.getMutantCompiler();
		// compiler.compileMutants(eclipseProject, files);
		// MutantPackager packager = manager.getMutantPackager();
		// packager.packageMutants(mutantInfo);

		table.addMutant(operatorName, mutantID);
	}

	// protected void outputToFile(Parameter p, Parameter mutant) {
	// Object[] args = { p, mutant };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(StatementList stmt_list, int index, int mod)
	// {
	// Object[] args = { stmt_list, new Integer(index), new Integer(mod) };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(ThrowStatement original) {
	// Object[] args = { original };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(TryStatement original_var) {
	// Object[] args = { original_var };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(UnaryExpression exp) {
	// Object[] args = { exp };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(UnaryExpression original, UnaryExpression
	// mutant) {
	// Object[] args = { original, mutant };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(Variable original_var) {
	// Object[] args = { original_var };
	// outputToFile(args);
	// }
	//
	// protected void outputToFile(Variable original_var, String mutant) {
	// Object[] args = { original_var, mutant };
	// outputToFile(args);
	// }

	public boolean sameParameterType(OJMethod m1, OJMethod m2) {
		OJClass[] c1 = m1.getParameterTypes();
		OJClass[] c2 = m2.getParameterTypes();

		if (c1.length == c2.length) {
			for (int i = 0; i < c1.length; i++) {
				if (!(c1[i].equals(c2[i])))
					return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean sameReturnType(OJMethod m1, OJMethod m2) {
		OJClass c1 = m1.getReturnType();
		OJClass c2 = m2.getReturnType();
		return (c1.equals(c2));
	}

	public void visit(MethodDeclaration p) throws ParseTreeException {
		if (!(p.getName().equals("main"))) {
			targetExpressions.clear();
			currentMethod = p;
			super.visit(p);
			outputToFile();
			currentMethod = null;
		}
	}

	public Environment getFileEnvironment() {
		return fileEnvironment;
	}
}

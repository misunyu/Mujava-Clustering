package openjava.ptree.util;

import java.io.IOException;
import java.util.Hashtable;

import mujava.gen.writer.MutantWriter;
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

import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * Description: File Analyzer for generating mutants
 * </p>
 * <p>
 * </p>
 * 
 * @version 1.0
 */

public abstract class MugammaTraditionalMutator extends VariableBinder {
	private Environment fileEnvironment;
	// -------------------------------------
	private String operatorName = new String();
	private IJavaElement sourceFile = null;
	protected MethodDeclaration currentMethod;
	protected CompilationUnit targetClass = null;
	protected Hashtable<Expression, MethodDeclaration> targetExpressions = new Hashtable<Expression, MethodDeclaration>();
	

	public MugammaTraditionalMutator(Environment env, CompilationUnit targetClass,
			IJavaElement originalSourceFile, String opName) {
		super(env);
		this.fileEnvironment = env;
		this.sourceFile = originalSourceFile;
		this.operatorName = opName;
		this.targetClass = targetClass;
		assert (targetClass != null);
	}

	public Environment getFileEnvironment() {
		return fileEnvironment;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public IJavaElement getSourceJavaElement() {
		return sourceFile;
	}

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
			currentMethod = p;
			super.visit(p);
			outputToFile();
			currentMethod = null;
		}
	}

	private void outputToFile() {
		
	}
	
	public abstract MutantWriter getMutantCodeWriter(
			) throws IOException;

	public abstract int getMutantType();

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

	protected OJClass getType(Expression p) throws ParseTreeException {
		OJClass result = null;
		
		if(p instanceof MethodCall) {
			// There are two cases in the MethodCall, 
			// one is a general MethodCall located in normal class.
			// The other is a MethodCall located in anonymous class, 
			// OpenJava does not support to find some methods from those yet.
			MethodCall m = (MethodCall)p;
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

	protected void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}

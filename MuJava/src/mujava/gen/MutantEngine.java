package mujava.gen;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import kaist.selab.util.MuJavaLogger;
import mujava.exception.OpenJavaException;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJSystem;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;
import openjava.ptree.util.MemberAccessCorrector;
import openjava.ptree.util.TypeNameQualifier;
import openjava.tools.parser.ParseException;
import openjava.tools.parser.Parser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

class ParseTreeWithFileEnv {
	protected CompilationUnit parseTree = null;
	protected FileEnvironment fileEnv = null;

	public ParseTreeWithFileEnv(CompilationUnit parseTree,
			FileEnvironment fileEnv) {
		this.parseTree = parseTree;
		this.fileEnv = fileEnv;
	}

	public CompilationUnit getParseTree() {
		return parseTree;
	}

	public void setParseTree(CompilationUnit parseTree) {
		this.parseTree = parseTree;
	}

	public FileEnvironment getFileEnv() {
		return fileEnv;
	}

	public void setFileEnv(FileEnvironment fileEnv) {
		this.fileEnv = fileEnv;
	}
}

public abstract class MutantEngine {

	private static CompilationUnit generateParseTree(IJavaElement jdtUnit,
			FileEnvironment fEnv) throws OpenJavaException {
		CompilationUnit parseTree = null;
		try {
			parseTree = parse(jdtUnit);
			fEnv.setCompilationUnit(parseTree);

			ClassDeclarationList typedecls = parseTree.getClassDeclarations();
			for (int j = 0; j < typedecls.size(); ++j) {
				ClassDeclaration class_decl = typedecls.get(j);
				OJClass c = makeOJClass(fEnv, class_decl);
				OJSystem.env.record(c.getName(), c);
				recordInnerClasses(c);
			}
		} catch (OpenJavaException e1) {
			throw e1;
		} catch (Exception e) {
			MuJavaLogger.getLogger().error("errors during parsing. " + e);
			e.printStackTrace();
		}

		return parseTree;
	}

	private static void initParseTree(CompilationUnit parseTree,
			FileEnvironment fEnv) throws OpenJavaException {
		try {
			/**
			 * use TypeNameQualifier for copying a ptree well
			 * 
			 * @see openjava.ptree.util.TypeNameQualifier;
			 */
			parseTree.accept(new TypeNameQualifier(fEnv));

			/**
			 * If we change the code of MemberAccessCorrector, we can change all
			 * public member variable to get/set functions with private member
			 * variable, But do nothing
			 */
			MemberAccessCorrector corrector = new MemberAccessCorrector(fEnv);
			parseTree.accept(corrector);
		} catch (ParseTreeException e) {
			throw new OpenJavaException("can't initialize parse tree");
		}
	}

	static private OJClass makeOJClass(Environment env, ClassDeclaration cdecl) {

		OJClass result = null;
		String qname = env.toQualifiedName(cdecl.getName());
		Class<?> meta = OJSystem.getMetabind(qname);
		try {
			Constructor<?> constr = meta.getConstructor(new Class[] {
					Environment.class, OJClass.class, ClassDeclaration.class });
			Object[] args = new Object[] { env, null, cdecl };
			result = (OJClass) constr.newInstance(args);
		} catch (Exception ex) {
			MuJavaLogger.getLogger().error(
					"errors during gererating a metaobject for " + qname);
			ex.printStackTrace();
			result = new OJClass(env, null, cdecl);
		}
		return result;
	}

	static private CompilationUnit parse(IJavaElement jdtUnit)
			throws OpenJavaException {
		Parser parser = null;
		InputStream is = null;

		try {
			IFile file = (IFile) jdtUnit.getResource();
			is = file.getContents();
			parser = new Parser(is);
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		try {
			CompilationUnit c = parser.CompilationUnit(OJSystem.env);
			is.close();
			return c;
		} catch (ParseException e) {
			throw new OpenJavaException(" can't generate parse tree");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		throw new OpenJavaException();
	}

	private static void recordInnerClasses(OJClass c) {
		OJClass[] inners = c.getDeclaredClasses();
		for (int i = 0; i < inners.length; ++i) {
			OJSystem.env.record(inners[i].getName(), inners[i]);
			recordInnerClasses(inners[i]);
		}
	}

	abstract protected void genMutants(ICompilationUnit jdtUnit,
			CompilationUnit parseTree, FileEnvironment fileEnv,
			List<String> operatorsT, List<String> operatorsC,
			IProgressMonitor monitor);

	public static CompilationUnit createParseTree(IJavaElement source,
			FileEnvironment fileEnv, IProgressMonitor monitor)
			throws OpenJavaException {

		MuJavaLogger.getLogger().debug("");
		MuJavaLogger.getLogger().debug(
				"------------------" + source.getElementName()
						+ "----------------");
		MuJavaLogger.getLogger().debug("* Generating parse tree.");

		CompilationUnit parseTree = generateParseTree(source, fileEnv);

		MuJavaLogger.getLogger().debug("* Initializing parse tree. ");
		initParseTree(parseTree, fileEnv);

		return parseTree;
	}
}
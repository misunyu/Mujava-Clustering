package openjava.ptree.util;

import java.io.IOException;

import mujava.gen.GenericCodeWriter;
import mujava.inf.IMutantInfo;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;

public class WrapperCodeWriter extends GenericCodeWriter {

	public WrapperCodeWriter(IMutantInfo mutantInfo) throws IOException {
		super(null);

//		MuJavaMutantInfo mutant = (MuJavaMutantInfo)super.mutantInfo;
//		File outfile = new File(mutant.getWrapperFileName());
//		FileWriter fout = new FileWriter(outfile);
//		PrintWriter out = new PrintWriter(fout);
//		setWriter(out);
	}
	public void visit(CompilationUnit p) throws ParseTreeException {
		out.println("// This is wrapper program.");
		out.println("// Author : swkim");
		out.println();

		/* package statement */
		String qn = p.getPackage();
		if (qn != null) {
			out.print("package " + qn + ";");
			out.println();

			out.println();
			out.println();
		}

		/* import statement list */
		String[] islst = p.getDeclaredImports();
		if (islst.length != 0) {
			for (int i = 0; i < islst.length; ++i) {
				out.println("import " + islst[i] + ";");
			}
			out.println();
			out.println();
		}
		out.println("import mugamma.monitor.*;");

		/* type declaration list */
		ClassDeclarationList tdlst = p.getClassDeclarations();
		tdlst.accept(this);
	}
	public void visit(ClassDeclaration p) throws ParseTreeException {

		writeTab();

		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}

		if (p.isInterface()) {
			out.print("interface ");
		} else {
			out.print("class ");
		}

		String name = p.getName();
		out.print(name);

		TypeName[] zuper = p.getBaseclasses();
		if (zuper.length != 0) {
			out.print(" extends ");
			zuper[0].accept(this);
			for (int i = 1; i < zuper.length; ++i) {
				out.print(", ");
				zuper[i].accept(this);
			}
		} else {

		}

		TypeName[] impl = p.getInterfaces();
		if (impl.length != 0) {
			out.print(" implements ");
			impl[0].accept(this);
			for (int i = 1; i < impl.length; ++i) {
				out.print(", ");
				impl[i].accept(this);
			}
		} else {

		}

		out.println();
		increaseLineNumber();

		MemberDeclarationList classbody = p.getBody();
		writeTab();
		out.println("{");
		increaseLineNumber();
		if (classbody.isEmpty()) {
			classbody.accept(this);
		} else {
			out.println();
			increaseLineNumber();
			pushNest();

			out.println("WrapperUtility util = WrapperUtility.getUtility();");

			out.println(name + "_Original original;");
			out.println(name + "_Mutant mutant;");

			classbody.accept(this);

			popNest();
			out.println();
			increaseLineNumber();
		}
		writeTab();
		out.print("}");
		out.println();
		increaseLineNumber();
		// }
	}

	public void visit(ConstructorDeclaration p) throws ParseTreeException {
		writeTab();

		/* ModifierList */
		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}

		/*
		 * if(class_name!=null){ out.print( class_name ); }else{ String name =
		 * p.getName(); out.print(name); }
		 */

		String name = p.getName();
		out.print(name);

		ParameterList params = p.getParameters();
		out.print("(");
		if (params.size() != 0) {
			out.print(" ");
			params.accept(this);
			out.print(" ");
		}
		out.print(")");

		TypeName[] tnl = p.getThrows();
		if (tnl.length != 0) {
			out.println();
			increaseLineNumber();
			writeTab();
			writeTab();
			out.print("throws ");
			tnl[0].accept(this);
			for (int i = 1; i < tnl.length; ++i) {
				out.print(", ");
				tnl[i].accept(this);
			}
		}

		ConstructorInvocation sc = p.getConstructorInvocation();
		StatementList body = p.getBody();
		if (body == null && sc == null) {
			out.println(";");
			increaseLineNumber();
		} else {
			out.println();
			increaseLineNumber();

			writeTab();
			out.println("{");
			increaseLineNumber();
			pushNest();

			// if (sc != null)
			// sc.accept(this);
			// if (body != null)
			// body.accept(this);

			out.println("original = new " + name + "_Original();");
			out.println("mutant = new " + name + "_Mutant();");
			out.println("util.recordCall(this, \"" + name + "()\");");
			out.println();
			out.println("if (!util.isKilled() && !original.equals(mutant)) {");
			out.println("// send a message to agent.");
			out.println("util.kill(this);");
			out.println("}");

			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		increaseLineNumber();
	}

	public void visit(MethodDeclaration p) throws ParseTreeException {

		writeTab();

		/* ModifierList */
		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}

		TypeName ts = p.getReturnType();
		ts.accept(this);

		out.print(" ");

		String name = p.getName();
		out.print(name);

		ParameterList params = p.getParameters();
		out.print("(");
		if (!params.isEmpty()) {
			out.print(" ");
			params.accept(this);
			out.print(" ");
		} else {
			params.accept(this);
		}
		out.print(")");

		TypeName[] tnl = p.getThrows();
		if (tnl.length != 0) {
			out.println();
			increaseLineNumber();
			writeTab();
			writeTab();
			out.print("throws ");
			tnl[0].accept(this);
			for (int i = 1; i < tnl.length; ++i) {
				out.print(", ");
				tnl[i].accept(this);
			}
		}

		StatementList bl = p.getBody();
		if (bl == null) {
			out.print(";");
		} else {
			out.println();
			increaseLineNumber();
			writeTab();
			out.print("{");
			out.println();
			increaseLineNumber();
			pushNest();

			String parameters = new String();
			if (params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					Parameter param = params.get(i);
					parameters += param.getVariable() + ",";
				}
				parameters = parameters.substring(0, parameters.length() - 1);
			}
			ts.accept(this);
			out.println(" ori = original." + name + "( " + parameters + " );");
			out.println("util.recordCall(this, \"" + name + "()\");");
			out.println();

			ts.accept(this);
			out.println(" mut = mutant." + name + "( " + parameters + " );");
			out.println("if (!util.isKilled() ) {");
			out.println("// send a message to agent.");
			out.println("util.kill(this);");
			out.println("}");
			out.println();
			out.println("return ori;");

			// bl.accept(this);

			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		increaseLineNumber();
	}
}

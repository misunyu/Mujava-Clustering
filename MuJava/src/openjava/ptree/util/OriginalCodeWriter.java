package openjava.ptree.util;

import java.io.IOException;

import mujava.MuJavaMutantInfo;
import mujava.gen.GenericCodeWriter;
import mujava.inf.IMutantInfo;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.ModifierList;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;

public class OriginalCodeWriter extends GenericCodeWriter {

	public OriginalCodeWriter(IMutantInfo mutantInfo) throws IOException {
		super(null);
		
//		MuJavaMutantInfo mutant = (MuJavaMutantInfo)mutantInfo;
//		File outfile = new File(mutant.getOriginalFileName());
//		FileWriter fout = new FileWriter(outfile);
//		PrintWriter out = new PrintWriter(fout);
//		setWriter(out);
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
		out.print(name+"_Original");

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
			classbody.accept(this);
			popNest();
			out.println();
			increaseLineNumber();
		}
		
		out.println("public boolean equals(" + name + "_Mutant  obj) {");
		out.println("  return true;");
		out.println("}");
		
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
		
		MuJavaMutantInfo mutantInfo = null; // (MuJavaMutantInfo)super.mutantInfo;
		String name = p.getName();
//		String mutantName = mutantInfo.getWrapperClass();
		String mutantName = name;
		int index = mutantName.lastIndexOf(".");
		if(index >0) mutantName = mutantName.substring(index+1);
		
		if (name.equals(mutantName))
			out.print(name+"_Original");
		else 
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

			if (sc != null)
				sc.accept(this);
			if (body != null)
				body.accept(this);

			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		increaseLineNumber();
	}
	
}

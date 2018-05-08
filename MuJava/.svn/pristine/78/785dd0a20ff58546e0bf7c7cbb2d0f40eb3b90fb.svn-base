package mujava.gen.writer.MSG.traditional;

import java.io.IOException;
import java.util.List;

import mujava.gen.writer.MSG.MutantMSGWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.ArrayAccess;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.Leaf;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;

public class TraditionalMutantMSGWriter extends MutantMSGWriter {

	protected static final int IMPLEMENTED = 3;
	protected static final int ORIGINAL = 0;

	// internal flags
	protected int phase = IMPLEMENTED;

	protected TraditionalMutantMSGWriter(Environment env, String name)
			throws IOException {
		super(env, name);
	}

	public void setChangePoints(List<ChangePoint> mutantPoints) {
		this.changePoints = mutantPoints;
	}

	public void setName(String string) {
		this.mutantOperatorName = string;
	}

	public void visit(CompilationUnit p) throws ParseTreeException {
		out.println("/** This is mutant program.");
		increaseLineNumber();
		out.println(" * @author swkim");
		increaseLineNumber();
		out.println(" */");
		increaseLineNumber();

		super.evaluateDown(p);

		/* package statement */
		String qn = p.getPackage();
		if (qn != null) {
			out.print("package " + qn + ";");
			out.println();
			increaseLineNumber();

			out.println();
			increaseLineNumber();
			out.println();
			increaseLineNumber();
		}

		/* import statement list */
		out.println("import MSG.traditional.*;");
		increaseLineNumber();
		// out.println("import MSG.classical.*;");
		// increaseLineNumber();

		String[] islst = p.getDeclaredImports();
		if (islst.length != 0) {
			for (int i = 0; i < islst.length; ++i) {
				out.println("import " + islst[i] + ";");
				increaseLineNumber();
			}
			out.println();
			increaseLineNumber();
			out.println();
			increaseLineNumber();
		}
		/* type declaration list */
		ClassDeclarationList tdlst = p.getClassDeclarations();
		tdlst.accept(this);

		super.evaluateUp(p);
	}

	protected void writeMethodHeader(MethodCall p) throws ParseTreeException {
		Expression expr = p.getReferenceExpr();
		TypeName typeName = p.getReferenceType();

		if (expr != null) {
			if (expr instanceof Leaf || expr instanceof ArrayAccess
					|| expr instanceof FieldAccess
					|| expr instanceof MethodCall || expr instanceof Variable) {
				expr.accept(this);
			} else {
				writeParenthesis(expr);
			}
			out.print(".");
		} else if (typeName != null) {
			typeName.accept(this);
			out.print(".");
		}
		String name = p.getName();
		out.print(name);
	}

	protected void writeMethodSignature(MethodDeclaration m)
			throws ParseTreeException {
		writeTab();
		ModifierList modifs = m.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}

		TypeName ts = m.getReturnType();
		ts.accept(this);

		out.print(" ");

		String name = m.getName();
		out.print(name);

		ParameterList params = m.getParameters();
		out.print("(");
		if (!params.isEmpty()) {
			out.print(" ");
			params.accept(this);
			out.print(" ");
		} else {
			params.accept(this);
		}
		out.print(")");

		TypeName[] tnl = m.getThrows();
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
		out.println();
		increaseLineNumber();
	}
}

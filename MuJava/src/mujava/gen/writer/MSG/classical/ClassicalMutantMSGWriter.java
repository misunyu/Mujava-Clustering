package mujava.gen.writer.MSG.classical;

import java.io.IOException;

import mujava.gen.writer.MSG.MutantMSGWriter;
import openjava.mop.Environment;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

public class ClassicalMutantMSGWriter extends MutantMSGWriter {

	protected static final int IMPLEMENTED = 3;
	protected static final int ORIGINAL = 0;

	// internal flags
	protected int phase = IMPLEMENTED;

	protected ClassicalMutantMSGWriter(Environment env, String name)
			throws IOException {
		super(env, name);
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
		// out.println("import MSG.MSGHelper;");
		// increaseLineNumber();
		// out.println("import MSG.traditional.*;");
		// increaseLineNumber();
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
}

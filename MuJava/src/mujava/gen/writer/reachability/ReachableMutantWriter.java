package mujava.gen.writer.reachability;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.ptree.ArrayAccess;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.Leaf;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;

public abstract class ReachableMutantWriter extends MutantWriter {

	protected ReachableMutantWriter(Environment env, String name)
			throws IOException {

		super(env, name, MutantOperator.GEN_EXP_REACH);
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
}
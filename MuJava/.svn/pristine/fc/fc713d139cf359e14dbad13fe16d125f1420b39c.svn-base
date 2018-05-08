package mujava.gen.writer.nujava.classical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.seeker.classical.JSI;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;

/**
 * For target non-static variable, declare generated static variable <a>
 * 
 * <pre>
 * int v;
 * int __v;
 * </pre>
 * 
 * For each target variable, define generated get/set functions
 * 
 * <pre>
 * public int __getV() {
 * 	return (cond) ? __v : v;
 * }
 * 
 * public void __setV(int _v) {
 * 	if (cond)
 * 		__v = _v;
 * 	else
 * 		v = _v;
 * }
 * </pre>
 * 
 * Finally, replace referenced variable to the generated get function and
 * assigned variable to the generated set function.
 * 
 * <pre>
 *  v = 5; -&gt; __setV(5);
 *  a = v + 5 -&gt; a = __getV() + 5;
 * </pre>
 * 
 * @author swkim
 * 
 */
public class JSINuJavaWriter extends ClassicalNuJavaWriter {
	private HashMap<ClassDeclaration, List<String>> map = new HashMap<ClassDeclaration, List<String>>();
	private boolean isDef = false;

	protected JSINuJavaWriter(Environment env) throws IOException {
		super(env, "JSI");
	}

	/**
	 * Declare static variable corresponding to the target non-static variable
	 * and define get/set function for the new static variable
	 */
	@Override
	public void visit(FieldDeclaration p) throws ParseTreeException {
		for (ChangePoint cPoint : this.changePoints) {
			if (cPoint.isSamePoint(p, null)) {
				// Mutant Info
				String mutantID = super.getMutationOperatorName() + "_"
						+ super.getTargetFileHashCode() + "_" + cPoint.getID();

				// Write original variable
				super.visit(p);

				// Insert Mutated Variable Declaration
				FieldDeclaration pCopy = (FieldDeclaration) p
						.makeRecursiveCopy();
				ModifierList list = pCopy.getModifiers();
				list.add(ModifierList.STATIC);
				pCopy.setModifiers(list);
				String name = pCopy.getVariable();
				pCopy.setVariable("__" + name);
				super.visit(pCopy);

				this.addVariable(name, pCopy.getTypeSpecifier());

				// Insert Get Method Declaration
				writeTab();
				// ModifierList //
				ModifierList modifs = p.getModifiers();
				if (modifs != null) {
					modifs.accept(this);
					// if (!modifs.isEmptyAsRegular())
					// out.print(" ");
				}
				out.print(" ");
				// Type
				TypeName ts = p.getTypeSpecifier();
				ts.accept(this);
				out.print(" ");

				// Get Method Name
				String getName = "__get_" + p.getName();
				out.print(getName);
				out.print("() {");
				out.println();
				increaseLineNumber();
				// Body
				pushNest();
				writeTab();
				out.print("return (");
				ts.accept(this);
				out.print(")(nujava.NuJavaHelper.isGivenID(\"" + mutantID
						+ "\", " + MutantID.getChangePointID(mutantID)
						+ ") ? nujava.classical.JSIMetaMutant.JSIGen(");
				out.print(pCopy.getVariable() + ", " + p.getVariable());
				out.print(", ");
				out.print("\"" + mutantID + "\"");
				out.print(") : " + p.getVariable() + ");");
				out.println();
				increaseLineNumber();
				popNest();
				// End }
				writeTab();
				out.print("}");
				out.println();
				increaseLineNumber();

				// Insert Set Method Declaration
				if (!modifs.contains(ModifierList.FINAL)) {
					writeTab();
					// ModifierList //
					if (modifs != null) {
						modifs.accept(this);
						// if (!modifs.isEmptyAsRegular())
						// out.print(" ");
					}
					out.print(" void ");
					// Get Method Name
					String setName = "__set_" + p.getName();
					out.print(setName);
					out.print("(");
					ts.accept(this);
					out.print(" _" + p.getVariable() + ") {");
					out.println();
					increaseLineNumber();
					// Body
					pushNest();
					writeTab();
					out.print("if (nujava.NuJavaHelper.isGivenID(\"" + mutantID
							+ "\")) ");
					out.println();
					increaseLineNumber();
					pushNest();
					writeTab();
					out.println(pCopy.getVariable() + "= _" + p.getVariable()
							+ ";");
					increaseLineNumber();
					popNest();
					writeTab();
					out.println("else");
					increaseLineNumber();
					pushNest();
					writeTab();
					out
							.println(p.getVariable() + "= _" + p.getVariable()
									+ ";");
					increaseLineNumber();
					popNest();
					popNest();
					// End }
					writeTab();
					out.print("}");
					out.println();
					increaseLineNumber();
				}

				// Post_Inc method
				// Post_Dec method
				// Pre_Inc method
				// Pre_Dec method
				String parentMethod = (currentMethod != null) ? currentMethod
						.getName() : "";
				MuJavaMutantInfo m = new MuJavaMutantInfo(mutantID + "_1",
						getMutationOperatorName(), env.currentClassName(),
						parentMethod, MutantOperator.GEN_NUJAVA, 1, 0, 1,
						getLineNumber(), removeNewline("static is inserted"));

				// registers its mutation information
				super.mujavaMutantInfos.add(m);
				increaseSizeOfChangedPoints();

				return;
			}
		}
		super.visit(p);
	}

	private void addVariable(String name, TypeName typeName) {
		List<String> list = map.get(currentClassDeclaration);
		if (list == null)
			list = new ArrayList<String>();
		String str = currentClassDeclaration.getName() + ":" + typeName + ":"
				+ name;
		if (!list.contains(str))
			list.add(str);
		map.put(currentClassDeclaration, list);
	}

	public static MutantWriter getMutantCodeWriter(JSI mutator)
			throws IOException {
		JSINuJavaWriter mutantWriter = new JSINuJavaWriter(mutator
				.getFileEnvironment());

		List<ChangePoint> list = mutator.getChangePoints();
		mutantWriter.setChangePoints(list);

		// Also, sets the hash code of target file, to distinguish every
		// same mutant whose ID is the same as the mutant generated from another
		// target file
		int hashCode = mutator.getTargetFileID();
		mutantWriter.setTargetFileHashCode(hashCode);
		return mutantWriter;
	}

	/**
	 * Only assignmentexpression has influence on the value of a variable.
	 * except for post/pre in(de)creasement. {@code isDef} variable is true if a
	 * variable is defined.
	 */
	public void visit(AssignmentExpression p) throws ParseTreeException {

		Expression lexpr = p.getLeft();
		Expression rexp = p.getRight();

		// Local Variable에 대해서는 아무런 변경을 하지 않아도 된다.
		if (isLocalVariable(lexpr))
			super.visit(p);
		else {
			String variableName = getVariableName(lexpr);
			if (isTarget(variableName)) {
				StringTokenizer st = new StringTokenizer(variableName, ":");
				st.nextToken();
				st.nextToken();
				String name = st.nextToken();
				out.print("__set_" + name + "(");
				isDef = false;
				rexp.accept(this);
				out.print(")");
			} else
				super.visit(p);
		}
	}

	private boolean isTarget(String variableName) {
		List<String> list = map.get(currentClassDeclaration);
		if (list != null) {
			if (list.contains(variableName))
				return true;
		}
		return false;
	}

	private String getVariableName(Expression lexpr) throws ParseTreeException {
		if (lexpr instanceof Variable) {
			OJClass clz = getType(lexpr);
			return currentClassDeclaration.getName() + ":" + clz.getName()
					+ ":" + ((Variable) lexpr).toString();
		} else if (lexpr instanceof FieldAccess) {
			String str = "";
			FieldAccess p = (FieldAccess) lexpr;
			TypeName tName = p.getReferenceType();
			Expression exp = p.getReferenceExpr();
			String sName = p.getName();
			OJClass type = getType(p);

			if (tName == null && exp == null)
				str = currentClassDeclaration.getName() + ":" + type.getName()
						+ ":" + p.toString();
			else if (tName != null && exp == null)
				str = tName.getName() + ":" + type.getName() + ":"
						+ p.toString();
			else if (tName == null && exp != null) {
				OJClass clz = getType(exp);

				str = clz.getName() + ":" + type.getName() + ":" + sName;
			}
			return str;
		}
		return "";
	}

	/**
	 * 모든 Use Node의 Variable에 대해서만 고려한다.
	 */
	public void visit(Variable p) throws ParseTreeException {
		String variableName = getVariableName(p);

		if (isLocalVariable(p))
			super.visit(p);
		else {
			if (isTargetVariable(p)) {
				StringTokenizer st = new StringTokenizer(variableName, ":");
				st.nextToken();
				st.nextToken();
				String name = st.nextToken();
				out.print("__get_" + name + "()");
			} else
				super.visit(p);
		}
	}

	/**
	 * Assignment와 마찬가지로, ++, --에 대해서 Def를 고려해서 변경한다.
	 */
	@Override
	public void visit(UnaryExpression p) throws ParseTreeException {
		int op = p.getOperator();
		Expression exp = p.getExpression();

		if (op == UnaryExpression.POST_INCREMENT) {
			// if(isTarget(exp)) {
			// //print pre expression
			// out.print("");
			// out.print("__post_inc_" + name + "()");
			// }
		}

		super.visit(p);
	}

	private boolean isTargetVariable(Variable p) throws ParseTreeException {
		String str = getVariableName(p);

		List<String> list = map.get(currentClassDeclaration);
		if (list == null)
			return false;
		if (list.contains(str))
			return true;
		return false;
	}

	private boolean isTarget(FieldAccess p) throws ParseTreeException {
		String str = getVariableName(p);

		List<String> list = map.get(currentClassDeclaration);
		if (list == null)
			return false;
		if (list.contains(str))
			return true;
		return false;
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		if (isTarget(p)) {
			String name = getVariableName(p);
			if (isDef) {
				System.out.println("BABO");
			} else {
				out.print("__get_" + name + "()");
			}
		} else
			super.visit(p);
	}
}

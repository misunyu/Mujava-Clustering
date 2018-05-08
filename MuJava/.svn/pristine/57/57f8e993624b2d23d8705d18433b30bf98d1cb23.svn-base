package openjava.ptree.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mujava.MuJavaMutantInfo;
import mujava.gen.GenericCodeWriter;
import mujava.op.util.ChangePoint;
import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJMethod;
import openjava.mop.OJSystem;
import openjava.ptree.ArrayAccess;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.FieldAccess;
import openjava.ptree.Leaf;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.SelfAccess;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclarator;
import openjava.ptree.VariableInitializer;

public class MugammaTraditionalMutantWriter extends GenericCodeWriter {
	protected static final int DEF = 4;
	protected static final int MUTANT = 1;
	protected static final int ORIGINAL = 0;
	protected static final int IMPLEMENTED = 3;
	protected static final int USE = 5;
	protected static final int WRAPPER = 2;

	private String mutantOperatorName;
	private Vector sideEffect = new Vector();

	protected boolean forUseNode = false;

	protected int phase = WRAPPER;
	protected MethodDeclaration currentMethod;

	int mutantReferenceNumber = 0;
	int originalReferenceNumber = 0;
	public List<MuJavaMutantInfo> mutantExpressions = new ArrayList<MuJavaMutantInfo>();
	protected List<ChangePoint> mutantPoints = null;

	protected MugammaTraditionalMutantWriter(Environment env)
			throws IOException {
		super(env);
		sideEffect.add("java.io.PrintStream/print");
		sideEffect.add("java.io.PrintStream/println");
	}

	public void setMutantPoints(List<ChangePoint> mutantPoints) {
		this.mutantPoints = mutantPoints;
	}

	public void setName(String string) {
		this.mutantOperatorName = string;
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {

		Expression left = p.getLeft();
		Expression right = p.getRight();
		boolean flag = isLocalVariable(left);
		assert (left instanceof Variable || left instanceof FieldAccess) : "assertion which all expression are vars or fieldaccess is failed";

		if (flag) {
			// if left-side expression has local variable,
			boolean temp = this.forUseNode;
			this.forUseNode = false;
			left.accept(this);
			out.print(p.operatorString());
			this.forUseNode = true;
			right.accept(this);
			this.forUseNode = temp;
		} else {
			OJClass retType = getType(left);

			if (phase == IMPLEMENTED) {
				boolean temp = this.forUseNode;
				this.forUseNode = false;
				super.visit(left);
				out.print(" = ");

				if (!retType.isPrimitive())
					out.print("(" + retType.getName() + ")");

				out.print("mugamma.Comparator.assign");
				writeTypeValue(retType);
				out.print("(\"" + mutantOperatorName + "\", "
						+ left.getObjectID());
				out.print(", ");

				if (!retType.isPrimitive())
					out.print("(Object)(");
				this.forUseNode = true;

				if (p.getOperator() == AssignmentExpression.EQUALS)
					super.visit(right);
				else {
					super.visit(left);
					out
							.print(" " + getBinaryOperator(p.operatorString())
									+ " ");
					super.visit(right);
				}
				if (!retType.isPrimitive())
					out.print(")");
				out.print(")");
				this.forUseNode = temp;
			} else if (phase == MUTANT) {
				boolean temp = this.forUseNode;
				out.print("mugamma.Comparator.compare");
				writeTypeValue(retType);
				out.print("(\"" + mutantOperatorName + "\", "
						+ left.getObjectID());
				out.print(", ");

				if (!retType.isPrimitive())
					out.print("(Object)(");

				this.forUseNode = true;
				if (p.getOperator() == AssignmentExpression.EQUALS)
					super.visit(right);
				else {
					super.visit(left);
					out
							.print(" " + getBinaryOperator(p.operatorString())
									+ " ");
					super.visit(right);
				}
				this.forUseNode = temp;
				if (!retType.isPrimitive())
					out.print(")");
				out.print(")");
			} else
				super.visit(p);
		}
	}

	public void visit(ClassDeclaration p) throws ParseTreeException {
		super.evaluateDown(p);

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
		// MuJavaMutantInfo mutantInfo = (MuJavaMutantInfo)super.mutantInfo;
		// String mutantName = mutantInfo.getWrapperClass();
		// int index = mutantName.lastIndexOf(".");
		// if(index >0) mutantName = mutantName.substring(index+1);

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

			// start - insertion
			writeTab();
			out.println("WrapperUtility __util = WrapperUtility.getUtility();");
			increaseLineNumber();

			out.println();
			increaseLineNumber();
			// end

			classbody.accept(this);
			popNest();
			out.println();
			increaseLineNumber();
		}
		writeTab();
		out.print("}");
		out.println();
		increaseLineNumber();

		super.evaluateUp(p);
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
		out.println("import mugamma.monitor.WrapperUtility;");
		increaseLineNumber();
		out.println("import mugamma.monitor.Monitor;");
		increaseLineNumber();

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

	public void visit(ConstructorDeclaration p) throws ParseTreeException {
		super.evaluateDown(p);

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
		// MuJavaMutantInfo mutantInfo = (MuJavaMutantInfo)super.mutantInfo;
		// String mutantName = mutantInfo.getWrapperClass();
		// int index = mutantName.lastIndexOf(".");
		// if(index >0) mutantName = mutantName.substring(index+1);

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

			// start insertion
			writeTab();
			out.println("__util.recordCall(this, \"" + name + "()\");");
			increaseLineNumber();
			// end

			if (body != null)
				body.accept(this);

			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		increaseLineNumber();

		super.evaluateUp(p);
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		if (phase != IMPLEMENTED && phase != MUTANT) {
			super.visit(p);
			return;
		}

		if (isLocalVariable(p)) {
			// do nothing
			super.visit(p);
			return;
		}

		String key = null;

		ParseTree pt = p.getReference();
		assert (pt != null);

		if (pt instanceof SelfAccess) {
			SelfAccess sa = (SelfAccess) pt;
			if (!sa.isSuperAccess()) {
				// this keyword
				String typeName = getEnvironment().currentClassName();
				key = typeName + "." + p.getName();
			} else {
				String typeName = getEnvironment().currentClassName();
				String varName = p.getName();
				key = typeName + "." + varName;
			}
		} else if (p.isTypeReference()) {
			TypeName type = (TypeName) pt;
			key = type.getName() + "." + p.getName();
		} else {
			try {
				Expression expr = (Expression) pt;
				OJClass clz = expr.getType(getEnvironment());
				assert (clz != null);

				key = clz.getName() + "." + p.getName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		OJClass retType = getType(p);

		if (phase == IMPLEMENTED) {
			if (this.forUseNode) {
				if (!retType.isPrimitive())
					out.print("((" + retType.getName() + ")");
				out.print("mugamma.Comparator.save");
				writeTypeValue(retType);
				out.print("(\"" + mutantOperatorName + "\", "
						+ (originalReferenceNumber++) + ", ");
				out.print("\"" + key + "\", " + p.toString() + ")");
				if (!retType.isPrimitive())
					out.print(")");
			} else
				super.visit(p);
		} else if (phase == MUTANT) {
			if (this.forUseNode) {
				if (!retType.isPrimitive())
					out.print("((" + retType.getName() + ")");
				out.print("mugamma.Comparator.get");
				writeTypeValue(retType);
				out.print("(\"" + mutantOperatorName + "\", "
						+ (mutantReferenceNumber++) + ", ");
				out.print("\"" + key + "\")");
				if (!retType.isPrimitive())
					out.print(")");
			} else
				super.visit(p);
		}
	}

	// public void setCurrentMethod(MethodDeclaration selectedMethod) {
	// this.selectedMethod = selectedMethod;
	// }

	@Override
	public void visit(MethodCall p) throws ParseTreeException {
		if (phase != IMPLEMENTED && phase != MUTANT) {
			super.visit(p);
			return;
		}

		/**
		 * if p is a recursive function, call getMethod with unmodified
		 * funcation name because we will change method name
		 */
		if (currentMethod.getName().equals("_" + p.getName() + "_Implemented")
				|| currentMethod.getName()
						.equals("_" + p.getName() + "_Mutant"))
			if (equalParameters(currentMethod, p, getEnvironment())) {
				String name = p.getName();
				p.setName(currentMethod.getName());
				super.evaluateDown(p);

				Expression expr = p.getReferenceExpr();
				TypeName reftype = p.getReferenceType();

				if (expr != null) {
					if (expr instanceof Leaf || expr instanceof ArrayAccess
							|| expr instanceof FieldAccess
							|| expr instanceof MethodCall
							|| expr instanceof Variable) {
						expr.accept(this);
					} else {
						writeParenthesis(expr);
					}
					out.print(".");
				} else if (reftype != null) {

					reftype.accept(this);
					out.print(".");
				}

				String mname = p.getName();
				out.print(mname);

				p.setName(name);
				ExpressionList args = p.getArguments();
				writeArguments(args);

				super.evaluateUp(p);

				p.setName(name);
				return;
			}

		OJMethod method = null;
		try {
			method = getMethod(getEnvironment(), p, null);
		} catch (NoSuchMemberException e) {
			super.visit(p);
			System.out.println(p.toFlattenString() + "not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assert (method != null);

		OJClass retType = method.getReturnType();

		boolean sideEffect = hasSideEffect(method);
		boolean flag = isVoidType(retType);

		if (!sideEffect) {
			// side effect free method call
			writeMethodHeader(p);
		} else {
			String paramName = "__param";

			out.print("new Object() { public ");
			out.print(retType.getName() + " ");
			out.print(p.getName() + "(");

			OJClass[] types = method.getParameterTypes();
			for (int i = 0; i < types.length; i++) {
				out.print(types[i].getName() + " " + "__param_" + i);
				if (types.length - 1 != i)
					out.print(", ");
			}
			out.print(") {");

			// body
			if (phase == IMPLEMENTED) {
				out.print("mugamma.Comparator.saveMethodCall(\"");
			} else if (phase == MUTANT) {
				out.print("mugamma.Comparator.compareMethodCall(\"");
			}

			writeMethodHeader(p);
			out.print("( ");
			if (types.length > 0)
				out.print("\" + " + paramName + "_0 + \"");

			for (int i = 1; i < types.length; i++) {
				out.print(", \" + " + paramName + "_" + i + " + \"");
			}
			out.print(")\", \"" + mutantOperatorName + "\"); ");

			if (!flag) {
				// not void type
				out.print("return ");
			}

			writeMethodHeader(p);
			out.print("(");
			if (types.length > 0)
				out.print("__param_" + 0);

			for (int i = 1; i < types.length; i++) {
				out.print(", ");
				out.print(paramName + "_" + i);
			}
			out.print("); } }.");
			out.print(p.getName());
		}

		boolean temp = this.forUseNode;
		this.forUseNode = true;
		ExpressionList args = p.getArguments();
		writeArguments(args);
		this.forUseNode = temp;
	}

	/**
	 * every method declaration is mutated according to the following algorithm
	 * in order to concentrate all mutants into a file
	 */
	public void visit(MethodDeclaration m) throws ParseTreeException {
		/**
		 * If there is no mutant point, normal source code will be written.
		 */
		if (mutantPoints == null) {
			super.visit(m);
			return;
		}

		/**
		 * If there is the method declaraiton whose code will be mutated, the
		 * method mutated
		 */
		if (containInMutantPoints(m)) {
			writeMethodSignature(m);

			currentMethod = m;
			phase = WRAPPER;

			writeTab();
			out.print("{");
			out.println();
			increaseLineNumber();
			pushNest();

			writeTab();
			out
					.println("String __mutantID = Monitor.getMonitor().getMutantID();");
			increaseLineNumber();

			List<ParseTree> list = new ArrayList<ParseTree>();
			for (ChangePoint mp : mutantPoints) {
				MethodDeclaration me = mp.getMethodDeclaration();
				if (me != null && isSameParseTreeElement(me, m)) {
					list.add(mp.getTreeElement());
				}
			}

			TypeName ts = m.getReturnType();
			String typeName = ts.getName();
			if (!typeName.equals("void")) {
				writeTab();
				out.print(ts.toString() + " original = ");
				if (ts.getDimension() != 0)
					out.println("null;");
				else if (typeName.equals("boolean"))
					out.println("false;");
				else if (typeName.equals("int") || typeName.equals("long")
						|| typeName.equals("double")
						|| typeName.equals("short") || typeName.equals("char")
						|| typeName.equals("float"))
					out.println("0;");
				else
					out.println("null;");
				increaseLineNumber();
			}

			String name = m.getName();
			writeTab();
			out.println("if(__mutantID == null) { }");
			increaseLineNumber();

			for (ParseTree treeElement : list) {
				writeTab();
				int index = getIndexOfTreeElement(treeElement);
				out.print("else if(__mutantID.equals(\""
						+ this.mutantOperatorName + "_"
						+ String.valueOf(index + 1) + "\")) ");
				if (!ts.getName().equals("void")) {
					out.print("original = ");
				}
				out.print("_" + name + "_Wrapper(");
				ParameterList params = m.getParameters();
				int size = params.size();
				for (int i = 0; i < size; i++) {
					Parameter p = params.get(i);
					if (i != 0)
						out.print(", ");
					out.print(p.getVariable());
				}
				out.println(");");
				increaseLineNumber();
			}
			writeTab();
			out.println("else");
			increaseLineNumber();
			pushNest();

			writeTab();
			if (!ts.getName().equals("void")) {

				out.print("original = ");
			}
			out.print("_" + name + "_Original(");
			ParameterList params = m.getParameters();
			int size = params.size();
			for (int i = 0; i < size; i++) {
				Parameter p = params.get(i);
				if (i != 0)
					out.print(", ");
				out.print(p.getVariable());
			}
			out.println(");");
			increaseLineNumber();
			popNest();

			if (!ts.getName().equals("void")) {
				writeTab();
				out.print("return original;");
				increaseLineNumber();
			}
			popNest();
			writeTab();
			out.println("} ");
			increaseLineNumber();

			// firstly, rename the original method and write its code
			this.phase = ORIGINAL;
			super.evaluateDown(m);
			m.setName(name);
			insertOriginalCode(m);
			super.evaluateUp(m);

			this.phase = WRAPPER;
			super.evaluateDown(m);
			m.setName(name);
			insertWrapperCode(m);
			super.evaluateUp(m);

			this.phase = IMPLEMENTED;
			super.evaluateDown(m);
			m.setName(name);
			insertImplementedCode(m);
			super.evaluateUp(m);

			this.phase = MUTANT;
			super.evaluateDown(m);
			m.setName(name);
			insertMutatedCode(m);
			m.setName(name);
			super.evaluateUp(m);

		} else {
			int temp = phase;
			phase = ORIGINAL;

			super.evaluateDown(m);

			writeTab();

			/* ModifierList */
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

			StatementList bl = m.getBody();
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

				// start insertion
				writeTab();
				ModifierList list = m.getModifiers();
				if (list.contains(ModifierList.STATIC)) {
					// for static method, we change the interaction
					// because it can not access the class variable
					out
							.println("WrapperUtility __util = WrapperUtility.getUtility();");
					increaseLineNumber();
					writeTab();
					out.println("__util.recordCall(null, \"" + name + "()\");");
					increaseLineNumber();
				} else {
					out.println("__util.recordCall(this, \"" + name + "()\");");
					increaseLineNumber();
				}
				// end

				bl.accept(this);
				popNest();
				writeTab();
				out.print("}");
			}

			out.println();
			increaseLineNumber();

			super.evaluateUp(m);
			phase = temp;
		}

	}

	private boolean containInMutantPoints(MethodDeclaration m) {
		for (int i = 0; i < mutantPoints.size(); i++) {
			ChangePoint mp = mutantPoints.get(i);
			if (isSameParseTreeElement(m, mp.getMethodDeclaration()))
				return true;
		}
		return false;
	}

	protected int getIndexOfTreeElement(ParseTree target) {
		for (int i = 0; i < mutantPoints.size(); i++) {
			ChangePoint mp = mutantPoints.get(i);
			if (isSameParseTreeElement(target, mp.getTreeElement()))
				if (isSameParseTreeElement(currentMethod, mp
						.getMethodDeclaration()))
					return i;
		}
		return -1;
	}

	@Override
	public void visit(Variable p) throws ParseTreeException {
		if (phase != IMPLEMENTED && phase != MUTANT) {
			super.visit(p);
			return;
		}

		if (isLocalVariable(p)) {
			// do nothing
			super.visit(p);
			return;
		}

		String name = p.toString();
		OJClass clz = getEnvironment().lookupBind(name);
		assert (clz != null);

		String key = name;
		if (clz.getName().equals(getEnvironment().currentClassName()))
			key = getEnvironment().currentClassName() + "." + name;

		OJClass retType = getType(p);

		// assert : forUseNode is always true
		assert (this.forUseNode == true);

		if (!this.forUseNode) {
			super.visit(p);
			return;
		}

		if (this.phase == IMPLEMENTED) {
			if (!retType.isPrimitive())
				out.print("((" + retType.getName() + ")");
			out.print("mugamma.Comparator.save");
			writeTypeValue(retType);
			out.print("(\"" + mutantOperatorName + "\", "
					+ (originalReferenceNumber++) + ", ");
			out.print("\"" + key + "\", " + name + ")");
			if (!retType.isPrimitive())
				out.print(")");
		} else if (phase == MUTANT) {
			if (!retType.isPrimitive())
				out.print("((" + retType.getName() + ")");
			out.print("mugamma.Comparator.get");
			writeTypeValue(retType);
			out.print("(\"" + mutantOperatorName + "\", "
					+ (mutantReferenceNumber++) + ", ");
			out.print("\"" + key + "\")");
			if (!retType.isPrimitive())
				out.print(")");
		} else
			super.visit(p);
	}

	/**
	 * this parameter p means a definition node(local)
	 */
	public void visit(VariableDeclarator p) throws ParseTreeException {
		// In case this parameter p is in one of the other methods.
		if (phase != IMPLEMENTED && phase != MUTANT) {
			super.visit(p);
			return;
		}

		super.evaluateDown(p);

		String declname = p.getVariable();
		out.print(declname);

		for (int i = 0; i < p.getDimension(); ++i) {
			out.print("[]");
		}

		VariableInitializer varinit = p.getInitializer();
		if (varinit != null) {
			out.print(" = ");

			boolean temp = this.forUseNode;
			this.forUseNode = true;
			varinit.accept(this);
			this.forUseNode = temp;
		}

		super.evaluateUp(p);
	}

	private String getBinaryOperator(String operator) {
		return operator.replaceAll("=", "");
	}

	private void writeMethodHeader(MethodCall p) throws ParseTreeException {
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

	/*
	 * @pre leftType != rightType
	 */
	protected OJClass getBiggerClass(OJClass leftType, OJClass rightType) {
		if (leftType.equals(OJSystem.DOUBLE)
				|| rightType.equals(OJSystem.DOUBLE))
			if (leftType.equals(OJSystem.FLOAT)
					|| rightType.equals(OJSystem.FLOAT)
					|| leftType.equals(OJSystem.INT)
					|| rightType.equals(OJSystem.INT))
				return OJSystem.DOUBLE;

		if (leftType.equals(OJSystem.LONG) || rightType.equals(OJSystem.LONG))
			if (leftType.equals(OJSystem.INT) || rightType.equals(OJSystem.INT)
					|| leftType.equals(OJSystem.SHORT)
					|| rightType.equals(OJSystem.SHORT))
				return OJSystem.LONG;

		if (!leftType.isPrimitive() || !rightType.isPrimitive())
			return null;

		assert (false) : ": " + leftType.toString() + ", "
				+ rightType.toString();
		return null;
	}

	protected OJClass getType(Expression p) throws ParseTreeException {
		OJClass result = null;

		if (p instanceof MethodCall) {
			// There are two cases in the MethodCall,
			// one is a general MethodCall located in normal class.
			// The other is a MethodCall located in anonymous class,
			// OpenJava does not support to find some methods from those yet.
			MethodCall m = (MethodCall) p;
			String oldName = m.getName();

			/**
			 * When MUTANT or IMPLEMENTED tag is turned on, the name of the
			 * current method in environment are changed using prefix "_" and
			 * postfix "_Mutant" or "_Implemented". To solve this, when those
			 * two flag is on, the name should change.
			 */

			if (phase != WRAPPER) {
				if (equalParameters(currentMethod, m, getEnvironment())) {
					if (phase == MUTANT
							&& currentMethod.getName().equals(
									"_" + m.getName() + "_Mutant")
							|| phase == IMPLEMENTED
							&& currentMethod.getName().equals(
									"_" + m.getName() + "_Implemented")
							|| phase == ORIGINAL
							&& currentMethod.getName().equals(
									"_" + m.getName() + "_Original")) {
						m.setName(currentMethod.getName());
					}
				}
			}

			if (getEnvironment().currentClassName() == "<anonymous class>") {
				AnonymousMethodCall mc = new AnonymousMethodCall(m);
				AnonymousClassEnvironment anonyEnv = (AnonymousClassEnvironment) super
						.getParent(getEnvironment());

				ExpressionList pl = m.getArguments();
				OJClass[] param = new OJClass[pl.size()];
				for (int i = 0; i < param.length; i++) {
					try {
						param[i] = pl.get(i).getType(getEnvironment());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				OJMethod method = anonyEnv.lookupMethod(m.getName(), param);
				result = method.getReturnType();
				if (result == null) {
					try {
						result = mc.getType(getParent(anonyEnv), param);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					result = p.getType(getEnvironment());
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

			if (phase == MUTANT || phase == IMPLEMENTED || phase == ORIGINAL) {
				if (currentMethod.getName().equals(m.getName()))
					m.setName(oldName);
			}
		} else {
			try {
				result = p.getType(getEnvironment());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		if (result == null) {
			System.err.println("cannot resolve the type of expression");
			System.err.println(p.getClass() + " : " + p);
			System.err.println(getEnvironment());
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

	private boolean equalParameters(MethodDeclaration m1, MethodCall m2,
			Environment env) {
		ParameterList pl = m1.getParameters();
		ExpressionList el = m2.getArguments();
		if (pl.size() != el.size())
			return false;
		for (int i = 0; i < pl.size(); i++) {
			try {
				String typeName1 = pl.get(i).getTypeSpecifier().toString();
				String typeName2 = el.get(i).getType(env).getName();
				System.out.println("Comp : " + typeName1 + " : " + typeName2);
				if (!typeName1.equals(typeName2))
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	protected boolean hasSideEffect(OJMethod method) {
		OJClass clz = method.getDeclaringClass();
		String key;
		if (clz != null)
			key = method.getDeclaringClass().getName() + "/" + method.getName();
		else
			key = "/" + method.getName();

		if (this.sideEffect.contains(key)) {
			return true;
		}

		return false;
	}

	protected void insertWrapperCode(MethodDeclaration m)
			throws ParseTreeException {
		String name = m.getName();

		m.setName("_" + m.getName() + "_Wrapper");

		// insert new method which has the same name of original method m
		writeMethodSignature(m);

		writeTab();
		out.print("{");
		out.println();
		increaseLineNumber();
		pushNest();

		// insert new body
		ModifierList modifierList = m.getModifiers();
		if (modifierList.contains(ModifierList.STATIC)) {
			writeTab();
			out.println("WrapperUtility __util = WrapperUtility.getUtility();");
			increaseLineNumber();
			writeTab();
			out.println("__util.recordCall(null, \"" + m.getName() + "()\");");
			increaseLineNumber();
		} else {
			writeTab();
			out.println("__util.recordCall(this, \"" + m.getName() + "()\");");
			increaseLineNumber();
		}

		writeTab();
		out.println("mugamma.Comparator.clean();");
		increaseLineNumber();
		writeTab();
		out.println("mugamma.Comparator comparator = mugamma."
				+ mutantOperatorName + "MetaMutant.getInstance();");
		increaseLineNumber();
		writeTab();
		out
				.println("__util.setMaximumSubType(comparator.getMaximumSubType());");
		increaseLineNumber();
		out.println();
		increaseLineNumber();

		writeTab();
		TypeName ts = m.getReturnType();
		if (!ts.getName().equals("void")) {
			out.print(ts.toString() + " original = ");
		}

		out.print("_" + name + "_Implemented");
		out.print("(");

		ParameterList params = m.getParameters();
		int size = params.size();
		for (int i = 0; i < size; i++) {
			Parameter p = params.get(i);
			if (i != 0)
				out.print(", ");
			out.print(p.getVariable());
		}
		out.println(");");
		increaseLineNumber();

		writeTab();
		out.println("java.util.List list = comparator.getDifferentMutants();");
		increaseLineNumber();
		writeTab();
		out
				.println("for (java.util.Iterator iter = list.iterator(); iter.hasNext();) {");
		increaseLineNumber();
		pushNest();
		writeTab();
		out
				.println("mugamma.SubMutant mutant = (mugamma.SubMutant)iter.next();");
		increaseLineNumber();
		writeTab();
		out.println("comparator.setCurrentMutant(mutant);");
		increaseLineNumber();
		writeTab();
		out.print("////_" + name + "_Mutant");
		out.print("(");
		size = params.size();
		for (int i = 0; i < size; i++) {
			Parameter p = params.get(i);
			if (i != 0)
				out.print(", ");
			out.print(p.getVariable());
		}
		out.println(");");
		increaseLineNumber();
		writeTab();

		out.print("__util.kill(");
		modifierList = m.getModifiers();
		if (modifierList.contains(ModifierList.STATIC))
			out.print("null");
		else
			out.print("this");
		out.println(", mutant);");

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();

		// end here

		writeTab();
		out.print("return ");

		if (!ts.getName().equals("void")) {
			out.print(" original");
		}
		out.println(";");
		increaseLineNumber();

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	protected void insertMutatedCode(MethodDeclaration m)
			throws ParseTreeException {
		// insert new method which has the same name of original method m
		m.setName("_" + m.getName() + "_Mutant");
		writeMethodSignature(m);

		writeTab();
		out.print("{");
		out.println();
		increaseLineNumber();
		pushNest();

		writeTab();
		out.println("String __mutantID = Monitor.getMonitor().getMutantID();");
		increaseLineNumber();

		m.getBody().accept(this);

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	protected void insertOriginalCode(MethodDeclaration m)
			throws ParseTreeException {
		m.setName("_" + m.getName() + "_Original");
		writeMethodSignature(m);
		writeTab();
		out.print("{");
		out.println();
		increaseLineNumber();
		pushNest();

		m.getBody().accept(this);

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	protected void insertImplementedCode(MethodDeclaration m)
			throws ParseTreeException {
		m.setName("_" + m.getName() + "_Implemented");
		writeMethodSignature(m);
		writeTab();
		out.print("{");
		out.println();
		increaseLineNumber();
		pushNest();

		writeTab();
		out.println("String __mutantID = Monitor.getMonitor().getMutantID();");
		increaseLineNumber();

		m.getBody().accept(this);

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	/*
	 * Print the method name, for example,
	 * mugamma.Comparator.getMutantDoubleValue It does not contain any (, ), and ".
	 */
	protected void writeGetMutantValue(OJClass retType) {
		out.print("mugamma.Comparator.getMutant");
		writeTypeValue(retType);
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

	/*
	 * Print the method name, for example, DoubleValue It does not contain any (, ),
	 * and ".
	 */
	protected void writeTypeValue(OJClass retType) {
		if (OJSystem.DOUBLE.equals(retType))
			out.print("DoubleValue");
		else if (OJSystem.LONG.equals(retType))
			out.print("LongValue");
		else if (OJSystem.INT.equals(retType))
			out.print("IntValue");
		else if (OJSystem.FLOAT.equals(retType))
			out.print("FloatValue");
		else if (OJSystem.BOOLEAN.equals(retType))
			out.print("BooleanValue");
		else if (OJSystem.SHORT.equals(retType))
			out.print("ShortValue");
		else if (OJSystem.CHAR.equals(retType))
			out.print("CharValue");
		else if (OJSystem.BYTE.equals(retType))
			out.print("ByteValue");
		else
			out.print("ObjectValue");
	}

	public int getLocalMutantCount() {
		return mutantPoints.size();
	}

	protected String getMutantOperatorName() {
		return mutantOperatorName;
	}

	public List<MuJavaMutantInfo> getMutantInfos() {
		return mutantExpressions;
	}
}

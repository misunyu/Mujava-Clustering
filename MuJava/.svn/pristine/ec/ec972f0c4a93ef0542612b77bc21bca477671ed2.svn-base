package mujava.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJField;
import openjava.ptree.AllocationExpression;
import openjava.ptree.ArrayAccess;
import openjava.ptree.ArrayAllocationExpression;
import openjava.ptree.ArrayInitializer;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Block;
import openjava.ptree.BreakStatement;
import openjava.ptree.CaseGroup;
import openjava.ptree.CaseGroupList;
import openjava.ptree.CaseLabel;
import openjava.ptree.CaseLabelList;
import openjava.ptree.CastExpression;
import openjava.ptree.CatchBlock;
import openjava.ptree.CatchList;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.ClassLiteral;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConditionalExpression;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.ContinueStatement;
import openjava.ptree.DoWhileStatement;
import openjava.ptree.EmptyStatement;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ForStatement;
import openjava.ptree.IfStatement;
import openjava.ptree.InstanceofExpression;
import openjava.ptree.LabeledStatement;
import openjava.ptree.Leaf;
import openjava.ptree.List;
import openjava.ptree.Literal;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MemberInitializer;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ReturnStatement;
import openjava.ptree.SelfAccess;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;
import openjava.ptree.SwitchStatement;
import openjava.ptree.SynchronizedStatement;
import openjava.ptree.ThrowStatement;
import openjava.ptree.TryStatement;
import openjava.ptree.TypeName;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.ptree.VariableInitializer;
import openjava.ptree.WhileStatement;
import openjava.ptree.util.VariableBinder;

import org.eclipse.core.resources.IProject;

public abstract class GenericCodeWriter extends VariableBinder {
	/**
	 * Returns the strength of the union of the operator.
	 * 
	 * @param op
	 *            the id number of operator.
	 * @return the strength of the union.
	 */
	protected static final int getPriorityOfOperator(int op) {
		switch (op) {
		case BinaryExpression.TIMES:
		case BinaryExpression.DIVIDE:
		case BinaryExpression.MOD:
			return 40;
		case BinaryExpression.PLUS:
		case BinaryExpression.MINUS:
			return 35;
		case BinaryExpression.SHIFT_L:
		case BinaryExpression.SHIFT_R:
		case BinaryExpression.SHIFT_RR:
			return 30;
		case BinaryExpression.LESS:
		case BinaryExpression.GREATER:
		case BinaryExpression.LESSEQUAL:
		case BinaryExpression.GREATEREQUAL:
		case BinaryExpression.INSTANCEOF:
			return 25;
		case BinaryExpression.EQUAL:
		case BinaryExpression.NOTEQUAL:
			return 20;
		case BinaryExpression.BITAND:
			return 16;
		case BinaryExpression.XOR:
			return 14;
		case BinaryExpression.BITOR:
			return 12;
		case BinaryExpression.LOGICAL_AND:
			return 10;
		case BinaryExpression.LOGICAL_OR:
			return 8;
		}
		return 100;
	}

	protected static final boolean isOperatorNeededLeftPar(int operator,
			Expression leftexpr) {
		if (leftexpr instanceof AssignmentExpression
				|| leftexpr instanceof ConditionalExpression) {
			return true;
		}

		int op = getPriorityOfOperator(operator);

		if (leftexpr instanceof InstanceofExpression) {
			return (op > getPriorityOfOperator(BinaryExpression.INSTANCEOF));
		}

		if (leftexpr instanceof BinaryExpression) {
			BinaryExpression lbexpr = (BinaryExpression) leftexpr;
			return (op > getPriorityOfOperator(lbexpr.getOperator()));
		}
		return false;
	}

	protected static final boolean isOperatorNeededRightPar(int operator,
			Expression rightexpr) {
		if (rightexpr instanceof AssignmentExpression
				|| rightexpr instanceof ConditionalExpression) {
			return true;
		}

		int op = getPriorityOfOperator(operator);

		if (rightexpr instanceof InstanceofExpression) {
			return (op >= getPriorityOfOperator(BinaryExpression.INSTANCEOF));
		}

		if (rightexpr instanceof BinaryExpression) {
			BinaryExpression lbexpr = (BinaryExpression) rightexpr;
			return (op >= getPriorityOfOperator(lbexpr.getOperator()));
		}
		return false;
	}

	private IProject eclipseProject;
	private int line_num = 1;
	private int nest = 0;
	private int tabSize = 4;
	protected PrintWriter out;
	ByteArrayOutputStream byteStream;

	/**
	 * @see #getCopiedString()
	 */
	protected int startOutputCopy() {
		out.flush();
		int index = byteStream.size();
		return index;
	}

	/**
	 * 
	 * @return
	 * @see #startOutputCopy()
	 */
	protected String getCopiedString(int index) {
		if (index >= 0) {

			out.flush();
			String tempString = byteStream.toString();

			int newIndex = index;
			index = -1;

			return tempString.substring(newIndex);
		}
		return "";
	}

	protected GenericCodeWriter(Environment env) throws IOException {
		super(env);
		// this.mutantInfo = mutantInfo;

		byteStream = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(byteStream);
		setWriter(out);
	}

	public void closeWriter() throws IOException {
		if (out != null) {
			out.flush();
			out.close();
		}
	}

	public byte[] getByte() {
		out.flush();
		return byteStream.toByteArray();
	}

	public IProject getEclipseProject() {
		return eclipseProject;
	}

	public void resetWriter() throws IOException {
		closeWriter();
		byteStream.reset();
		PrintWriter out = new PrintWriter(byteStream);
		setWriter(out);
	}

	public void setTabSize(int size) {
		tabSize = size;
	}

	public void visit(AllocationExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression encloser = p.getEncloser();
		if (encloser != null) {
			encloser.accept(this);
			out.print(" . ");
		}

		out.print("new ");

		TypeName tn = p.getClassType();
		tn.accept(this);

		ExpressionList args = p.getArguments();
		writeArguments(args);

		MemberDeclarationList mdlst = p.getClassBody();
		if (mdlst != null) {
			out.println("{");
			line_num++;
			pushNest();
			mdlst.accept(this);
			popNest();
			writeTab();
			out.print("}");
		}

		super.evaluateUp(p);
	}

	public void visit(ArrayAccess p) throws ParseTreeException {
		super.evaluateDown(p);
		Expression expr = p.getReferenceExpr();
		if (expr instanceof Leaf || expr instanceof ArrayAccess
				|| expr instanceof FieldAccess || expr instanceof MethodCall
				|| expr instanceof Variable) {
			expr.accept(this);
		} else {
			writeParenthesis(expr);
		}

		Expression index_expr = p.getIndexExpr();
		out.print("[");
		index_expr.accept(this);
		out.print("]");
		super.evaluateUp(p);
	}

	public void visit(ArrayAllocationExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print("new ");

		TypeName tn = p.getTypeName();
		tn.accept(this);

		ExpressionList dl = p.getDimExprList();
		for (int i = 0; i < dl.size(); ++i) {
			Expression expr = dl.get(i);
			out.print("[");
			if (expr != null) {
				expr.accept(this);
			}
			out.print("]");
		}

		ArrayInitializer ainit = p.getInitializer();
		if (ainit != null)
			ainit.accept(this);

		super.evaluateUp(p);
	}

	public void visit(ArrayInitializer p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print("{ ");
		writeListWithDelimiter(p, ", ");
		if (p.isRemainderOmitted())
			out.print(",");
		out.print(" }");

		super.evaluateUp(p);
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression lexpr = p.getLeft();

		if (lexpr instanceof AssignmentExpression) {
			writeParenthesis(lexpr);

		} else {
			lexpr.accept(this);
		}

		String operator = p.operatorString();
		out.print(" " + operator + " ");

		Expression rexp = p.getRight();
		rexp.accept(this);

		super.evaluateUp(p);
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression lexpr = p.getLeft();
		if (isOperatorNeededLeftPar(p.getOperator(), lexpr)) {
			writeParenthesis(lexpr);
		} else {
			lexpr.accept(this);
		}

		String operator = p.operatorString();
		out.print(" " + operator + " ");

		Expression rexpr = p.getRight();
		if (isOperatorNeededRightPar(p.getOperator(), rexpr)) {
			writeParenthesis(rexpr);
		} else {
			rexpr.accept(this);
		}

		super.evaluateUp(p);
	}

	public void visit(Block p) throws ParseTreeException {
		super.evaluateDown(p);

		StatementList stmts = p.getStatements();
		writeTab();
		writeStatementsBlock(stmts);
		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(BreakStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("break");

		String label = p.getLabel();
		if (label != null) {
			out.print(" ");
			out.print(label);
		}

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(CaseGroup p) throws ParseTreeException {
		super.evaluateDown(p);

		ExpressionList labels = p.getLabels();
		for (int i = 0; i < labels.size(); ++i) {
			writeTab();
			Expression label = labels.get(i);
			if (label == null) {
				out.print("default ");
			} else {
				out.print("case ");
				label.accept(this);
			}
			out.println(" :");
			line_num++;

		}

		pushNest();
		StatementList stmts = p.getStatements();
		stmts.accept(this);
		popNest();

		super.evaluateUp(p);
	}

	public void visit(CaseGroupList p) throws ParseTreeException {
		super.evaluateDown(p);
		// writeListWithSuffix( p, NEWLINE );
		writeListWithSuffixNewline(p);
		super.evaluateUp(p);
	}

	public void visit(CaseLabel p) throws ParseTreeException {
		super.evaluateDown(p);
		Expression expr = p.getExpression();
		if (expr != null) {
			out.print("case ");
			expr.accept(this);
		} else {
			out.print("default");
		}
		out.print(":");
		super.evaluateUp(p);
	}

	public void visit(CaseLabelList p) throws ParseTreeException {
		super.evaluateDown(p);
		// writeListWithSuffix( p, NEWLINE );
		writeListWithSuffixNewline(p);
		super.evaluateUp(p);
	}

	public void visit(CastExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print("(");
		TypeName ts = p.getTypeSpecifier();
		ts.accept(this);
		out.print(")");

		out.print(" ");

		Expression expr = p.getExpression();

		if (expr instanceof AssignmentExpression
				|| expr instanceof ConditionalExpression
				|| expr instanceof BinaryExpression
				|| expr instanceof InstanceofExpression
				|| expr instanceof UnaryExpression) {

			writeParenthesis(expr);
		} else {
			expr.accept(this);
		}

		super.evaluateUp(p);
	}

	public void visit(CatchBlock p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print(" catch ");

		out.print("( ");

		Parameter param = p.getParameter();
		param.accept(this);

		out.print(" ) ");

		StatementList stmts = p.getBody();
		writeStatementsBlock(stmts);

		super.evaluateUp(p);
	}

	public void visit(CatchList p) throws ParseTreeException {
		super.evaluateDown(p);

		writeList(p);

		super.evaluateUp(p);
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
		line_num++;

		MemberDeclarationList classbody = p.getBody();
		writeTab();
		out.println("{");
		line_num++;
		if (classbody.isEmpty()) {
			classbody.accept(this);
		} else {
			out.println();
			line_num++;
			pushNest();

			classbody.accept(this);

			popNest();
			out.println();
			line_num++;
		}
		writeTab();
		out.print("}");
		out.println();
		line_num++;
		// }

		super.evaluateUp(p);
	}

	public void visit(ClassDeclarationList p) throws ParseTreeException {
		super.evaluateDown(p);
		// writeListWithDelimiter( p, NEWLINE + NEWLINE );
		writeListWithDelimiterNewline(p);
		super.evaluateUp(p);
	}

	public void visit(ClassLiteral p) throws ParseTreeException {
		super.evaluateDown(p);

		TypeName type = p.getTypeName();
		type.accept(this);
		out.print(".class");

		super.evaluateUp(p);
	}

	public void visit(CompilationUnit p) throws ParseTreeException {
		super.evaluateDown(p);

		out.println("// This is mutant program.");
		line_num++;
		out.println("// Author : ysma");
		line_num++;
		out.println();
		line_num++;

		/* package statement */
		String qn = p.getPackage();
		if (qn != null) {
			out.print("package " + qn + ";");
			out.println();
			line_num++;

			out.println();
			line_num++;
			out.println();
			line_num++;
		}

		/* import statement list */
		String[] islst = p.getDeclaredImports();
		if (islst.length != 0) {
			for (int i = 0; i < islst.length; ++i) {
				out.println("import " + islst[i] + ";");
				line_num++;
			}
			out.println("import java.util.*;");
			line_num++;
			out.println();
			line_num++;
			out.println();
			line_num++;
		}

		/* type declaration list */
		ClassDeclarationList tdlst = p.getClassDeclarations();
		tdlst.accept(this);

		super.evaluateUp(p);
	}

	public void visit(ConditionalExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression condition = p.getCondition();
		if (condition instanceof AssignmentExpression
				|| condition instanceof ConditionalExpression) {
			writeParenthesis(condition);
		} else {
			condition.accept(this);
		}

		out.print(" ? ");

		Expression truecase = p.getTrueCase();
		if (truecase instanceof AssignmentExpression) {
			writeParenthesis(truecase);
		} else {
			truecase.accept(this);
		}

		out.print(" : ");

		Expression falsecase = p.getFalseCase();
		if (falsecase instanceof AssignmentExpression) {
			writeParenthesis(falsecase);
		} else {
			falsecase.accept(this);
		}

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
			line_num++;
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
			line_num++;
		} else {
			out.println();
			line_num++;

			writeTab();
			out.println("{");
			line_num++;
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
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(ConstructorInvocation p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		if (p.isSelfInvocation()) {
			out.print("this");
		} else {
			Expression enclosing = p.getEnclosing();
			if (enclosing != null) {
				enclosing.accept(this);
				out.print(" . ");
			}
			out.print("super");
		}

		ExpressionList exprs = p.getArguments();
		writeArguments(exprs);

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(ContinueStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("continue");

		String label = p.getLabel();
		if (label != null) {
			out.print(" " + label);
		}

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(DoWhileStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("do ");

		StatementList stmts = p.getStatements();

		if (stmts.isEmpty()) {
			out.print(" ; ");
		} else {
			writeStatementsBlock(stmts);
		}

		out.print(" while ");

		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.print(")");

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(EmptyStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(ExpressionList p) throws ParseTreeException {
		super.evaluateDown(p);

		writeListWithDelimiter(p, ", ");

		super.evaluateUp(p);
	}

	public void visit(ExpressionStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		Expression expr = p.getExpression();

		expr.accept(this);

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression expr = p.getReferenceExpr();
		TypeName typename = p.getReferenceType();

		if (expr != null) {
			if (expr instanceof Leaf || expr instanceof ArrayAccess
					|| expr instanceof FieldAccess
					|| expr instanceof MethodCall || expr instanceof Variable) {
				expr.accept(this);
			} else {
				out.print("(");
				expr.accept(this);
				out.print(")");
			}
			out.print(".");
		} else if (typename != null) {
			typename.accept(this);
			out.print(".");
		}

		String name = p.getName();
		out.print(name);

		super.evaluateUp(p);
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		/* ModifierList */
		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}
		out.print(" ");

		/* TypeName */
		TypeName ts = p.getTypeSpecifier();
		ts.accept(this);
		out.print(" ");

		/* Variable */
		String variable = p.getVariable();
		out.print(variable);

		/* "=" VariableInitializer */
		VariableInitializer initializer = p.getInitializer();
		if (initializer != null) {
			out.print(" = ");
			initializer.accept(this);
		}
		/* ";" */
		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(ForStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("for ");

		out.print("(");

		ExpressionList init = p.getInit();
		TypeName tspec = p.getInitDeclType();
		VariableDeclarator[] vdecls = p.getInitDecls();
		if (init != null && (!init.isEmpty())) {
			init.get(0).accept(this);
			for (int i = 1; i < init.size(); ++i) {
				out.print(", ");
				init.get(i).accept(this);
			}
		} else if (tspec != null && vdecls != null && vdecls.length != 0) {
			tspec.accept(this);
			out.print(" ");
			vdecls[0].accept(this);
			for (int i = 1; i < vdecls.length; ++i) {
				out.print(", ");
				vdecls[i].accept(this);
			}
		}

		out.print(";");

		Expression expr = p.getCondition();
		if (expr != null) {
			out.print(" ");
			expr.accept(this);
		}

		out.print(";");

		ExpressionList incr = p.getIncrement();
		if (incr != null && (!incr.isEmpty())) {
			out.print(" ");
			incr.get(0).accept(this);
			for (int i = 1; i < incr.size(); ++i) {
				out.print(", ");
				incr.get(i).accept(this);
			}
		}

		out.print(") ");

		StatementList stmts = p.getStatements();
		if (stmts.isEmpty()) {
			out.print(";");
		} else {
			writeStatementsBlock(stmts);
		}

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(IfStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("if ");

		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.print(") ");

		/* then part */
		StatementList stmts = p.getStatements();
		writeStatementsBlock(stmts);

		/* else part */
		StatementList elsestmts = p.getElseStatements();
		if (!elsestmts.isEmpty()) {
			out.print(" else ");
			writeStatementsBlock(elsestmts);
		}

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(InstanceofExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		/* this is too strict for + or - */
		Expression lexpr = p.getExpression();
		if (lexpr instanceof AssignmentExpression
				|| lexpr instanceof ConditionalExpression
				|| lexpr instanceof BinaryExpression) {
			writeParenthesis(lexpr);
		} else {
			lexpr.accept(this);
		}

		out.print(" instanceof ");

		TypeName tspec = p.getTypeSpecifier();
		tspec.accept(this);

		super.evaluateUp(p);
	}

	public void visit(LabeledStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		String name = p.getLabel();
		out.print(name);

		out.println(" : ");
		line_num++;

		Statement statement = p.getStatement();
		statement.accept(this);

		super.evaluateUp(p);
	}

	public void visit(Literal p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print(p.toString());

		super.evaluateUp(p);
	}

	public void visit(MemberDeclarationList p) throws ParseTreeException {
		super.evaluateDown(p);
		writeListWithDelimiterNewline(p);
		super.evaluateUp(p);
	}

	public void visit(MemberInitializer p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		if (p.isStatic()) {
			out.print("static ");
		}

		StatementList stmts = p.getBody();
		writeStatementsBlock(stmts);

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(MethodCall p) throws ParseTreeException {
		super.evaluateDown(p);

		Expression expr = p.getReferenceExpr();
		TypeName reftype = p.getReferenceType();

		if (expr != null) {

			if (expr instanceof Leaf || expr instanceof ArrayAccess
					|| expr instanceof FieldAccess
					|| expr instanceof MethodCall || expr instanceof Variable) {
				expr.accept(this);
			} else {
				writeParenthesis(expr);
			}
			out.print(".");
		} else if (reftype != null) {

			reftype.accept(this);
			out.print(".");
		}

		String name = p.getName();
		out.print(name);

		ExpressionList args = p.getArguments();
		writeArguments(args);

		super.evaluateUp(p);
	}

	public void visit(MethodDeclaration p) throws ParseTreeException {
		
		evaluateDown(p);

		writeTab();

		/* ModifierList */
		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			// if (!modifs.isEmptyAsRegular())
			// out.print(" ");
		}
		out.print(" ");

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
			line_num++;
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
			line_num++;
			writeTab();
			out.print("{");
			out.println();
			line_num++;
			pushNest();
			bl.accept(this);
			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		line_num++;

		evaluateUp(p);
	}

	public void visit(ModifierList p) throws ParseTreeException {
		super.evaluateDown(p);
		out.print(ModifierList.toString(p.getRegular()));
		super.evaluateUp(p);
	}

	public void visit(Parameter p) throws ParseTreeException {
		super.evaluateDown(p);

		ModifierList modifs = p.getModifiers();
		modifs.accept(this);
		if (!modifs.isEmptyAsRegular())
			out.print(" ");

		TypeName typespec = p.getTypeSpecifier();
		typespec.accept(this);

		out.print(" ");

		String declname = p.getVariable();
		out.print(declname);

	}

	public void visit(ParameterList p) throws ParseTreeException {
		super.evaluateDown(p);

		writeListWithDelimiter(p, ", ");

		super.evaluateUp(p);
	}

	public void visit(ReturnStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("return");

		Expression expr = p.getExpression();
		if (expr != null) {
			out.print(" ");
			expr.accept(this);
		}

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(SelfAccess p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print(p.toString());

		super.evaluateUp(p);
	}

	public void visit(StatementList p) throws ParseTreeException {
		super.evaluateDown(p);

		writeList(p);

		super.evaluateUp(p);
	}

	public void visit(SwitchStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("switch ");
		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.print(")");

		out.println(" {");
		line_num++;

		CaseGroupList casegrouplist = p.getCaseGroupList();
		casegrouplist.accept(this);

		writeTab();
		out.print("}");
		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(SynchronizedStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("synchronized ");

		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.println(")");
		line_num++;

		StatementList stmts = p.getStatements();
		writeStatementsBlock(stmts);

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(ThrowStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("throw ");

		Expression expr = p.getExpression();
		expr.accept(this);

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(TryStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("try ");

		StatementList stmts = p.getBody();
		writeStatementsBlock(stmts);

		CatchList catchlist = p.getCatchList();
		if (!catchlist.isEmpty()) {
			catchlist.accept(this);
		}

		StatementList finstmts = p.getFinallyBody();
		if (!finstmts.isEmpty()) {
			out.println(" finally ");
			line_num++;
			writeStatementsBlock(finstmts);
		}

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	/** ****rough around innerclass******* */
	public void visit(TypeName p) throws ParseTreeException {
		super.evaluateDown(p);

		String typename = p.getName().replace('$', '.');
		out.print(typename);

		int dims = p.getDimension();
		out.print(TypeName.stringFromDimension(dims));

		super.evaluateUp(p);
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		super.evaluateDown(p);

		if (p.isPrefix()) {
			String operator = p.operatorString();
			out.print(operator);
		}

		Expression expr = p.getExpression();
		if (expr instanceof AssignmentExpression
				|| expr instanceof ConditionalExpression
				|| expr instanceof BinaryExpression
				|| expr instanceof InstanceofExpression
				|| expr instanceof CastExpression
				|| expr instanceof UnaryExpression) {
			writeParenthesis(expr);
		} else {
			expr.accept(this);
		}

		if (p.isPostfix()) {
			String operator = p.operatorString();
			out.print(operator);
		}

		super.evaluateUp(p);
	}

	public void visit(Variable p) throws ParseTreeException {
		super.evaluateDown(p);

		out.print(p.toString());

		super.evaluateUp(p);
	}

	public void visit(VariableDeclaration p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		ModifierList modifs = p.getModifiers();
		modifs.accept(this);
		if (!modifs.isEmptyAsRegular())
			out.print(" ");

		TypeName typespec = p.getTypeSpecifier();
		typespec.accept(this);

		out.print(" ");

		VariableDeclarator vd = p.getVariableDeclarator();
		vd.accept(this);

		out.print(";");

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	public void visit(VariableDeclarator p) throws ParseTreeException {
		super.evaluateDown(p);

		String declname = p.getVariable();
		out.print(declname);

		for (int i = 0; i < p.getDimension(); ++i) {
			out.print("[]");
		}

		VariableInitializer varinit = p.getInitializer();
		if (varinit != null) {
			out.print(" = ");
			varinit.accept(this);
		}

		super.evaluateUp(p);
	}

	public void visit(WhileStatement p) throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		out.print("while ");

		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.print(") ");

		StatementList stmts = p.getStatements();
		if (stmts.isEmpty()) {
			out.print(" ;");
		} else {
			writeStatementsBlock(stmts);
		}

		out.println();
		line_num++;

		super.evaluateUp(p);
	}

	protected int getLineNumber() {
		return this.line_num;
	}

	protected int getNest() {
		return nest;
	}

	protected void increaseLineNumber() {
		this.line_num++;
	}

	protected boolean isLocalVariable(Expression p) {
		if (p instanceof ArrayAccess) {
			ArrayAccess aa = (ArrayAccess) p;
			return isLocalVariable(aa.getReferenceExpr());
		} else if (p instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess) p;
			Expression exp = fa.getReferenceExpr();
			if (exp == null) {
				OJClass clz = getEnvironment().lookupBind(fa.getName());
				if (clz != null && !isSelfClassVariable(fa.getName()))
					return true;
				return false;
			}
			return isLocalVariable(exp);
		} else if (p instanceof Variable) {
			Variable v = (Variable) p;

			OJClass clz = getEnvironment().lookupBind(v.toString());
			if (clz != null && !isSelfClassVariable(v.toString()))
				return true;
		}

		return false;
	}

	protected boolean isSameParseTreeElement(ParseTree p, ParseTree q) {
		if (p == null && q == null)
			return true;
		if (p == null || q == null)
			return false;
		return (p.getObjectID() == q.getObjectID());
	}

	protected boolean isSelfClassVariable(String name) {
		Environment env = getEnvironment();
		OJClass cClass;
		try {
			cClass = OJClass.forName(env.currentClassName());
		} catch (OJClassNotFoundException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			OJField field = cClass.getField(name, cClass);
			if (field != null)
				return true;
		} catch (NoSuchMemberException e) {
		}

		return false;
	}

	protected boolean isVoidType(OJClass retType) {
		OJClass clz;
		try {
			clz = OJClass.forName("void");
			if (retType != null && retType.equals(clz))
				return true;
		} catch (OJClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Decrease the number of Tab
	 */
	protected void popNest() {
		setNest(getNest() - 1);
	}

	/**
	 * Increase the number of Tab
	 */
	protected void pushNest() {
		setNest(getNest() + 1);
	}

	protected String removeNewline(String str) {
		int index;
		while ((index = str.indexOf("\n")) >= 0) {
			if (index > 0 && index < str.length()) {
				str = str.substring(0, index - 1)
						+ str.substring(index + 1, str.length());
			} else if (index == 0) {
				str = str.substring(1, str.length());
			} else if (index == str.length()) {
				str = str.substring(0, index - 1);
			}
		}
		return str;
	}

	protected void setLineNumber(int i) {
		this.line_num = i;
	}

	protected void setNest(int i) {
		nest = i;
	}

	protected void setWriter(PrintWriter writer) {
		this.out = writer;
	}

	protected String toSimpleClassName(String givenName) {
		String str = new String(givenName);
		int index = str.lastIndexOf(".");
		if (index > 0) {
			assert (index + 1 != str.length());
			str = str.substring(index + 1);
		}
		return str;
	}

	protected void writeAnonymous(Object obj) throws ParseTreeException {
		if (obj == null) {
		} else if (obj instanceof ParseTree) {
			((ParseTree) obj).accept(this);
		} else {
			out.print(obj.toString());
		}
	}

	protected void writeArguments(ExpressionList args)
			throws ParseTreeException {
		out.print("(");
		if (!args.isEmpty()) {
			out.print(" ");
			args.accept(this);
			out.print(" ");
		} else {
			args.accept(this);
		}
		out.print(")");
	}

	protected void writeList(List list) throws ParseTreeException {
		Enumeration<?> it = list.elements();

		while (it.hasMoreElements()) {
			Object elem = it.nextElement();
			writeAnonymous(elem);
		}
	}

	protected void writeListWithDelimiter(List list, String delimiter)
			throws ParseTreeException {
		Enumeration<?> it = list.elements();

		if (!it.hasMoreElements())
			return;

		writeAnonymous(it.nextElement());
		while (it.hasMoreElements()) {
			out.print(delimiter);
			writeAnonymous(it.nextElement());
		}
	}

	protected void writeListWithDelimiterNewline(List list)
			throws ParseTreeException {
		Enumeration<?> it = list.elements();

		if (!it.hasMoreElements())
			return;

		writeAnonymous(it.nextElement());
		while (it.hasMoreElements()) {
			out.println();
			line_num++;
			writeAnonymous(it.nextElement());
		}
	}

	protected void writeListWithSuffix(List list, String suffix)
			throws ParseTreeException {
		Enumeration<?> it = list.elements();

		while (it.hasMoreElements()) {
			writeAnonymous(it.nextElement());
			out.print(suffix);
		}
	}

	protected void writeListWithSuffixNewline(List list)
			throws ParseTreeException {
		Enumeration<?> it = list.elements();

		while (it.hasMoreElements()) {
			writeAnonymous(it.nextElement());
			out.println();
			line_num++;
		}
	}

	protected void writeParenthesis(Expression expr) throws ParseTreeException {
		out.print("(");
		expr.accept(this);
		out.print(")");
	}

	protected void writeStatementsBlock(StatementList stmts)
			throws ParseTreeException {
		out.println("{");
		line_num++;
		pushNest();

		stmts.accept(this);

		popNest();
		writeTab();
		out.print("}");
	}

	protected void writeTab() {
		for (int i = 0; i < nest; i++)
			for (int j = 0; j < this.tabSize; j++) {
				out.print(" ");
			}
	}
}

	package mujava.gen.writer.state.traditional;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import mujava.MuJavaMutantInfo;
import mujava.gen.writer.state.MutantNormalStateWriter;
import openjava.mop.Environment;
import openjava.ptree.AllocationExpression;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.BinaryExpression;
import openjava.ptree.CaseGroup;
import openjava.ptree.CastExpression;
import openjava.ptree.CatchBlock;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConditionalExpression;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.DoWhileStatement;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ForStatement;
import openjava.ptree.IfStatement;
import openjava.ptree.InstanceofExpression;
import openjava.ptree.Literal;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ReturnStatement;
import openjava.ptree.StatementList;
import openjava.ptree.SwitchStatement;
import openjava.ptree.TryStatement;
import openjava.ptree.TypeName;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.ptree.WhileStatement;

public class TraditionalMutantNormalStateWriter extends MutantNormalStateWriter {
			
	protected TraditionalMutantNormalStateWriter(Environment env, String name, boolean isFixed)
			throws IOException {
		super(env, name, isFixed);		
	}
	
	
	public void visit(CompilationUnit p) throws ParseTreeException {

	//	System.out.println("TraditionalMutantNormalWriter visit(CompilationUnit p)");
	
		super.evaluateDown(p);

		// package statement 
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

		// import statement list - msyu
		out.println("import interstate.*;");
		increaseLineNumber();
		out.println("import java.util.Vector;");
		increaseLineNumber();
		out.println("import com.thoughtworks.xstream.*;");
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
		// type declaration list 
		ClassDeclarationList tdlst = p.getClassDeclarations();
		tdlst.accept(this);
	
		super.evaluateUp(p);

	}
	
	protected void writeStatementsBlock(StatementList stmts)
			throws ParseTreeException {
	//	System.out.println("writeStatementsBlock(StatementList stmts)");
		
		out.println("{");				
		increaseLineNumber();	
		
		
		pushNest();

		//if(!written) {
		if(needStateSavingCode){		
			writeStateSavingCode();
		}
		
		stmts.accept(this);

		popNest();
		writeTab();
		
		// 20170201 YSMA 지워야 할 것 같아서 주석 처리함
		/*
		if(!written) {			
			writeStateSavingCode();
		}
		*/
		out.print("}");
	}
	
	
	protected void setStateSavingMutantInfo(MuJavaMutantInfo m){
		if(isInConstructor) {
			m.setObjID(currentConstructorDeclaration.getObjectID());
		} else {
			if(currentMethod == null) {
				m.setObjID(0);
			} else {
				m.setObjID(currentMethod.getObjectID());
			}
		}
		
		needStateSavingCode = true;
		curMutant = m;
	}
	
	protected void writeStatSavingBlock()
			throws ParseTreeException {
	//	System.out.println("writeStatementsBlock(StatementList stmts)");
		
		out.println("{");				
		increaseLineNumber();	
		
		
		pushNest();

		//if(!written) {
		if(needStateSavingCode){		
			writeStateSavingCode();
		}
		
		popNest();
		writeTab();
		
		// 20170201 YSMA 지워야 할 것 같아서 주석 처리함
		/*
		if(!written) {			
			writeStateSavingCode();
		}
		*/
		out.print("}");
	}
	
	public void visit(TryStatement p) throws ParseTreeException {

		int curMethodID =  getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);
		
		super.visit(p);
		
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);
	}
	
	public void visit(CatchBlock p) throws ParseTreeException {
		int curMethodID =  getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);
		
		super.visit(p);
		
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);

		
	}
	
	public void visit(DoWhileStatement p) throws ParseTreeException {
		int curMethodID =  getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);
		
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

		if(needStateSavingCode){
			out. print(" {");
			out.println();
			pushNest();
			writeStateSavingCode();
			popNest();
			out. print(" }");
			out.println();
		}else{
			out.print(";");
			out.println();
		}

		
		increaseLineNumber();

		super.evaluateUp(p);

		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);

	}
	
	public void visit(WhileStatement p) throws ParseTreeException {
		
	//	System.out.println("start WhileStatement");
		int curMethodID =  getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);
				
		//super.visit(p);
		

		writeTab();
		out.print("while ");

		out.print("(");
		Expression expr = p.getExpression();
		expr.accept(this);
		out.print(") ");

		StatementList stmts = p.getStatements();
		if (stmts.isEmpty()) {
			if(needStateSavingCode){
				writeStateSavingCode();
			}
		} else {
			writeStatementsBlock(stmts);
		}
		
		
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);
	//	System.out.println("end WhileStatement");

	}

	
	
	public void visit(CaseGroup p) throws ParseTreeException {

		//System.out.println("visit(CaseGroup p) p " + p);
		
		int curMethodID =  getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);

		
		//boolean prevWritten = written;
		boolean prevWritten = !(needStateSavingCode);

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
			increaseLineNumber();
						
			//if(!written) {
			/*if(needStateSavingCode){
				writeStateSavingCode();
			}*/
		}

		pushNest();
		StatementList stmts = p.getStatements();
		stmts.accept(this);
		popNest();

		super.evaluateUp(p);
		
//		tmpAssignSet = addOutBlockVarAssign(curMethodID, tmpAssignSet, tmpVarSet);
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);

		
		//written = prevWritten;
		needStateSavingCode = !(prevWritten);
	}
	
	public void visit(SwitchStatement p) throws ParseTreeException {

		//boolean prevWritten = written;
		//boolean prevWritten = !(needStateSavingCode);
		super.visit(p);
		//if(prevWritten && !written) {
		/*if(prevWritten && needStateSavingCode) {
		
			//written = true;
			needStateSavingCode = false;
		}*/
		if(needStateSavingCode){
			writeStateSavingCode();
		}		
	}
	
	private HashSet<String> getPrevAssignVarInfo(HashSet beforeAssignVarSet)
	{
		if(beforeAssignVarSet != null) {
			return (HashSet)beforeAssignVarSet.clone();
		} 			

		return null;
	}
	
	private Hashtable<String, Boolean> getPrevVarInfo(Hashtable beforeVarSet)
	{
		if(beforeVarSet != null) {
			return (Hashtable)beforeVarSet.clone();
		} 			

		return null;
	}

	private HashSet<String> getPrevAssignInfo(HashSet beforeAssignSet)
	{
	
		if(beforeAssignSet != null) {
			return (HashSet)beforeAssignSet.clone();
		} 	
		return null;
	}

	private void restorePrevAssignVarInfo(/*HashSet varSet, */HashSet assignSet)
	{
		if(assignSet != null) {
			assignedVarSet.put(currentMethod.getObjectID(), assignSet);
		} else {
			assignedVarSet.remove(currentMethod.getObjectID());
		}
	}
	
	private void restorePrevVarInfo(Hashtable varSet/*, HashSet assignSet*/)
	{
		if(varSet != null) {
			blockLocalVarSet.put(currentMethod.getObjectID(), varSet);
		} else {
			blockLocalVarSet.remove(currentMethod.getObjectID());
		}
	}

	
	public void visit(IfStatement p) throws ParseTreeException {
		//System.out.println("visit(IfStatement p) START ");
		
		int curMethodID = getCurMethodID();
		
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
	
		Hashtable<String, Boolean> tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet<String> tmpAssignSet = getPrevAssignInfo(beforeAssignSet);
		/**/
		
		super.evaluateDown(p);
		
		writeTab();
		out.print("if ");

		out.print("(");
		Expression expr = p.getExpression();
		if(expr instanceof AssignmentExpression) {
			String left = ((AssignmentExpression) expr).getLeft().toString();
			
			if(beforeVarSet != null && beforeVarSet.contains(left)) {
				beforeAssignSet.add(left);
			}	
		}


		expr.accept(this);
		out.print(") ");

		//boolean tmpWritten = written;
		boolean preNeedStateSavingCode = needStateSavingCode;
		
		//boolean tmpWritten = !(needStateSavingCode);
		//boolean blockWritten = false;
		
		//System.out.println("then part");

		// then part 
		StatementList stmts = p.getStatements();
		writeStatementsBlock(stmts);
		
		//if(written)
		/*if(!needStateSavingCode){
			blockWritten = true;
		}*/
		
		/*restore*/
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);
		/**/
				
		// else part
		if(preNeedStateSavingCode){ needStateSavingCode = true;}
		
		StatementList elsestmts = p.getElseStatements();
		if (!elsestmts.isEmpty()) {
			
			//written = tmpWritten;
			//needStateSavingCode = !(tmpWritten);
			
			beforeVarSet = blockLocalVarSet.get(curMethodID); //check
			beforeAssignSet = assignedVarSet.get(curMethodID);

	
			tmpVarSet = getPrevVarInfo(beforeVarSet);
			tmpAssignSet = getPrevAssignInfo(beforeAssignSet);
		
			
			out.print(" else ");
			writeStatementsBlock(elsestmts);
		
			//if(written)
			/*if(!needStateSavingCode){
				blockWritten = true;
			}*/
			
			/*restore*/
			restorePrevVarInfo(tmpVarSet);
			restorePrevAssignVarInfo(tmpAssignSet);
			/**/
		}else{
			if(needStateSavingCode){
				out.print(" else ");
				writeStatSavingBlock();
			}
		}
		
		//written = blockWritten;
		//needStateSavingCode = !(blockWritten);
		
	//	if(!written)
	//		written = true;
		
		out.println();
		increaseLineNumber();
		
		super.evaluateUp(p);
		
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);
	//	System.out.println("visit(IfStatement p) END");
	}
	
	
	public void visit(ForStatement p) throws ParseTreeException {
		
	//	System.out.println("start visit(ForStatement p)");
		int curMethodID = getCurMethodID();
		Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
		HashSet beforeAssignSet = assignedVarSet.get(curMethodID);

		Hashtable tmpVarSet = getPrevVarInfo(beforeVarSet);
		HashSet tmpAssignSet = getPrevAssignInfo(beforeAssignSet);

		super.evaluateDown(p);

		writeTab();

		out.print("for ");
		out.print("(");

		ExpressionList init = p.getInit();
		TypeName tspec = p.getInitDeclType();
		VariableDeclarator[] vdecls = p.getInitDecls();
	//	System.out.println(" visit(ForStatement p) 2");
	
		if(vdecls != null) {
			String vname = null;
			for(int i = 0; i < vdecls.length; i++) {
				vname = vdecls[i].getVariable();
				addBlockLocalVar(curMethodID, vname, false);
				addAssignedVar(curMethodID, vname);
			}
		}
		//System.out.println(" visit(ForStatement p) 3");
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
			if(needStateSavingCode){
				writeStateSavingCode();
			}
		} else {
			writeStatementsBlock(stmts);
		}

		out.println();
		increaseLineNumber();

		super.evaluateUp(p);
		
		restorePrevVarInfo(tmpVarSet);
		restorePrevAssignVarInfo(tmpAssignSet);
	//	System.out.println("end visit(ForStatement p)");


	}

	
	public void visit(UnaryExpression p) throws ParseTreeException {
		//System.out.println("TraditionalMutantNormalWriter visit(UnaryExpression p) " + p);
		
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

		addAssignedVar(currentMethod.getObjectID(), p.toString());
		
		if (p.isPostfix()) {
			String operator = p.operatorString();
			out.print(operator);
		}

	}
	

	
	public void visit(AssignmentExpression p) throws ParseTreeException {
		//System.out.println("start  visit(AssignmentExpression p) " + p);
		//System.out.println("parent: " + p.getParent());
		
		super.evaluateDown(p);

		Expression lexpr = p.getLeft();

		if (lexpr instanceof AssignmentExpression) {
			writeParenthesis(lexpr);

		} else {
			
			addAssignedVar(currentMethod.getObjectID(), lexpr.toString());
			lexpr.accept(this);
		}			

		String operator = p.operatorString();
		out.print(" " + operator + " ");

		Expression rexp = p.getRight();
		rexp.accept(this);
		
		/*
		if(!written) {
			if((p.getParent() instanceof ExpressionStatement)) {
				out.println(";");
				increaseLineNumber();
				
				writeStateSavingCode();
				//written = true;
				//System.out.println("WRITTEN TRUE 2");
			}
		}*/
		
		super.evaluateUp(p);
		
		//System.out.println("end  visit(AssignmentExpression p) " + p);
	}
	
	// 20170131 YSMA added
	public void visit(ExpressionStatement p) throws ParseTreeException {
		super.visit(p);
		//if(!written) {
		if(needStateSavingCode){
			increaseLineNumber();
			writeStateSavingCode();
		}
	}
	


	protected void writeVarSet(HashSet<String> set, Hashtable<String, Boolean> varTable, String methodName, int methodID)
	{	
		if(set == null) 
			return;
		if(set.size() == 0)
			return;
			
		//System.out.println("start writeVarSet");

		Hashtable <String, Boolean> classFields = classFieldSet.get(currentClassDeclaration.getObjectID());
		Iterator iter = set.iterator();
		while(iter.hasNext()) {
			String var = (String)iter.next();
			Boolean isJavaIOPackage = false;
			
			if(varTable != null) {
				isJavaIOPackage = varTable.get(var);
			} else {
				if(classFields != null) {
					isJavaIOPackage = classFields.get(var);
					if(isJavaIOPackage == null) {
						isJavaIOPackage = false;
					}
				}
			}
			if (isJavaIOPackage == null)
				isJavaIOPackage = false;
			
			if(isJavaIOPackage) continue;
			
			writeTab();
			out.print("_"+ methodName + "Vector" + methodID + ".add(" );
			out.print("_" + methodName + "XStream" + methodID + ".toXML(");
			out.print(var);
			out.print(")");
			out.println(");");			
			increaseLineNumber();
		}
		//System.out.println("end writeVarSet");
	}
	
	protected void writeMethodParameters(Hashtable<String, Boolean> set, String methodName, int methodID)
	{

		if(set == null)
			return;
		if(set.size() == 0)
			return;
		//System.out.println("start writeMethodParameters");

		Enumeration<String> vars = set.keys();
		while(vars.hasMoreElements()) {
			String var = vars.nextElement();
			Boolean isJavaIOPackage = set.get(var);
			
			if(isJavaIOPackage) continue; //io package
			
			writeTab();
			out.print("_"+ methodName + "Vector" + methodID + ".add(" );
			out.print("_" + methodName + "XStream" + methodID + ".toXML(");
			out.print(var);
			out.print(")");
			out.println(");");			
			increaseLineNumber();
			
		}	
	}
	
	protected void writeFields(HashSet<String> set, String methodName, int methodID)
	{
		if(set == null)
			return;
		if(set.size() == 0)
			return;
		//System.out.println("start writeMethodParameters");
		Hashtable<String, Boolean> fdset = classFieldSet.get(currentClassDeclaration.getObjectID());
		
		Iterator<String> i = set.iterator();
		//Enumeration<String> vars = set.keys();
		//while(vars.hasMoreElements()) {
			//String var = vars.nextElement();
		while(i.hasNext()) {
			String var = i.next();
			Boolean isJavaIOPackage = fdset.get(var);
			
			if(isJavaIOPackage) continue; //io package
			
			writeTab();
			out.print("_"+ methodName + "Vector" + methodID + ".add(" );
			out.print("_" + methodName + "XStream" + methodID + ".toXML(");
			out.print(var);
			out.print(")");
			out.println(");");			
			increaseLineNumber();
			
		}	
	}
	
	protected int getCurMethodID()
	{
		String methodName = currentMethod.getName();
		//int methodID = -1;

		//if(methodName.length() == 0) {
		if(isInConstructor) {
			if(currentConstructorDeclaration == null) {
				return 0;
			} else {
				return currentConstructorDeclaration.getObjectID();
			}
		} else {
			return currentMethod.getObjectID();
		}
	}
	
	protected String getCurMethodName()
	{
		String methodName = currentMethod.getName();
		
		//if(methodName.length() == 0) {
		if(isInConstructor) {
			methodName = currentConstructorDeclaration.getName();
		} 
		
		return methodName;
	}
	
	protected void writeStateSavingCode()
	{	
		//System.out.println("start writeStateSavingCode");
		//field 선언에서 mutation이 발생했고, 현재 일반 함수를 수행하고 있는 중이면 상태 저장 안 함.
		//field mutation이 발생한 경우, 생성자에서만 상태 저장
		if(curMutant.getObjID() != 0 && (currentMethod.getObjectID() != curMutant.getObjID())) { 
			//written = true;
			needStateSavingCode = false;
			return;
		}
		
		String methodName = getCurMethodName();
		int methodID = getCurMethodID();


		HashSet aset = assignedVarSet.get(methodID);
		Hashtable<String, Boolean> pset = methodParSet.get(methodID);
		HashSet<String> fset = classFieldAssignSet.get(currentClassDeclaration.getObjectID());

		//System.out.println("aset = " + aset + " pset = " + pset);
		
		writeTab();
		out.println("_"+ methodName + "Vector" + methodID + ".clear();");
		increaseLineNumber();

		
		if(aset != null)
			writeVarSet(aset, blockLocalVarSet.get(methodID), methodName, methodID);
		
		if(pset != null)
			writeMethodParameters(pset, methodName, methodID);
		
		
		
		//if(fset != null && methodName.length() == 0) { //constructor에서만 allocation 된 field 저장
		if(fset != null && isInConstructor) {
			writeFields(fset, methodName, methodID);
		}
		
		writeTab();
		/*out.println("InterState.saveState(_" + methodName + "Vector" + methodID + ", \"" + 
				stateDirectory + "\", \"" + getLineNumber() + "\", " + "\"" + curMutant.getMutantID() + 
				"\", \"" + curMutant.getChangeLocation() + "\");");*/
		
		out.println("InterState.saveState(_" + methodName + "Vector" + methodID + ", \"" + 
		stateDirectory + "\", \"" + curMutant.getChangeLocation() + "\", " + "\"" + curMutant.getMutantID() + 
		"\", \"" + getLineNumber() + "\");");

		
		increaseLineNumber();

		//written = true;
		needStateSavingCode = false;
		//System.out.println("end writeStateSavingCode");
	}
	
	
	public void visit(ReturnStatement p) throws ParseTreeException {
		//System.out.println("visit(ReturnStatement p)");
		int methodID = getCurMethodID();
		
		
		//String expr = p.toString().replace("return", "");
		String returnType = currentMethod.getReturnType().toString();
		String	tmpVar = "_" + getCurMethodName() + "tempRetVar" + currentMethod.getObjectID();

		/////////////////////////
		//super.visit(p);

		super.evaluateDown(p);

		writeTab();

	//	if(p.getExpression() instanceof Literal || p.getExpression() instanceof FieldAccess || 
	//			p.getExpression() instanceof Variable) //Literal, Variable, FieldAccess
	//		out.print("return");
		
		if(returnType != null && !returnType.equals("void")) {
			out.print(tmpVar + " = " );
			Boolean isJavaIOPackage = isJavaIOPackage(returnType);			
			addBlockLocalVar(methodID, tmpVar, isJavaIOPackage);
			addAssignedVar(currentMethod.getObjectID(), tmpVar);
		}
		
		Expression expr = p.getExpression();
		if (expr != null) {
			out.print(" ");
			expr.accept(this);
		}

		out.print(";");

		out.println();
		increaseLineNumber();

		super.evaluateUp(p);
		
		
		
		/////////////////////////////////////
		//increaseLineNumber();
		
		//if(!written) {
		if(needStateSavingCode){

			if(returnType != null && !returnType.equals("void")) {
			
				//out.println(returnType + " " + tmpVar + " = " + p.toString().replace("return", ""));
				//increaseLineNumber();

				//addAssignedVar(currentMethod.getObjectID(), tmpVar);
				
				writeStateSavingCode();
				writeTab();
				out.println("return " + tmpVar + ";");
				increaseLineNumber();
				
				removeAssignedVar(methodID, tmpVar);
				removeBlockLocalVar(methodID, tmpVar);
								
				} else {
					writeStateSavingCode();
				}	
			
			
		} else {
//			if(!p.getExpression().toString().contains("return")) {
			if(returnType != null && !returnType.equals("void")) {
				writeTab();
				out.println("return " + tmpVar + ";");	
				increaseLineNumber();
			}
	//		}
			
		}
		
		//written = true;
		// needStateSavingCode = false;
		
	}

	
	
	public void visit(MethodDeclaration p) throws ParseTreeException {
		
		//System.out.println("start visit(MethodDeclaration p) name = " + p.getName());
		boolean tmpInConstructor = isInConstructor;
		isInConstructor = false;
		
		addMethodParList(p.getObjectID(), p.getParameters());
		
		super.visit(p);
		
		if(p.getBody() != null) {

		String modifier = p.getModifiers().toString().replace("final", "");
			//if(curMutant!= null && curMutant.getObjID() == p.getObjectID() && written) {
		if(curMutant!= null && curMutant.getObjID() == p.getObjectID() && !needStateSavingCode) {
				writeTab();
				out.println(modifier + " Vector _" + p.getName() + "Vector" + p.getObjectID() + " = new Vector();");
				increaseLineNumber();
				writeTab();
				out.println(modifier + " XStream _" + p.getName() + "XStream" +  p.getObjectID() + " = new XStream();");
				increaseLineNumber();	
			}
		
			TypeName returnTypeName = p.getReturnType();
			if(modifier != null && !modifier.contains("abstract") && returnTypeName != null) {
				String returnType = returnTypeName.toString();
				if(returnType != null && !returnType.equals("void")) {
					writeTab();
					out.println(modifier + " " + returnType + " _" + p.getName() + "tempRetVar" + p.getObjectID()  + ";" );
					increaseLineNumber();	
				}
			}
			
			//written = true;
			needStateSavingCode = false;
		}
		
		assignedVarSet.remove(p.getObjectID());
		blockLocalVarSet.clear();
		
//		written = true;
		
		removeMethodParList(p.getObjectID());

		isInConstructor = tmpInConstructor;
		//System.out.println("end visit(MethodDeclaration p)");
	}	
	
	
	public void visit(ConstructorDeclaration p) throws ParseTreeException {

		//System.out.println("start visit(ConstructorDeclaration p)");
		ConstructorDeclaration temp = currentConstructorDeclaration;
		currentConstructorDeclaration = p;
		boolean tmpInConstructor = isInConstructor;
		isInConstructor = true;
				
		addMethodParList(p.getObjectID(), p.getParameters());
		
		//super.visit(p);
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
			
			if(curMutant != null && curMutant.getObjID() == 0) {
			
				//if(!written) {
				if(needStateSavingCode){
					writeStateSavingCode();
					//written = false;
					//needStateSavingCode = false;
					// YSMA Question: Why? true? seem to be false;
				}
			}
			
			out.print("}");
		}

		out.println();
		increaseLineNumber();	

		//super.evaluateUp(p);
	
			
		String modifier = p.getModifiers().toString().replace("final", "");
		if(curMutant!= null && (curMutant.getObjID() == p.getObjectID() || curMutant.getObjID() == 0)) {
			out.println(modifier + " Vector _" + p.getName() + "Vector" +  + p.getObjectID() + " = new Vector();");
			increaseLineNumber();	
			out.println(modifier + " XStream _" + p.getName() + "XStream" +  + p.getObjectID() + " = new XStream();");
			increaseLineNumber();		
		}

		
		assignedVarSet.remove(p.getObjectID());
		blockLocalVarSet.clear();
		
		//written = true;
		needStateSavingCode = false;
	
		removeMethodParList(p.getObjectID());
		//System.out.println("end visit(ConstructorDeclaration p)");
		
		super.evaluateUp(p);
		
		currentConstructorDeclaration = temp;
		isInConstructor = tmpInConstructor;

	}
	
	
	public void visit(VariableDeclaration p) throws ParseTreeException {
		//System.out.println("start visit(VariableDeclaration p)");
		
		int methodID = getCurMethodID();
		String localVar = p.getVariable();
		Boolean isJavaIOPackage = isJavaIOPackage(p.getTypeSpecifier().getName());
	
		addBlockLocalVar(methodID, localVar, isJavaIOPackage);
		
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
		
		if(vd.getInitializer() != null) {
			addAssignedVar(currentMethod.getObjectID(), localVar);
		}

		out.print(";");

		out.println();
		increaseLineNumber();

		super.evaluateUp(p);
		
		//if(!written) {
		if(needStateSavingCode){
			if(p.getParent() instanceof StatementList) {
				writeStateSavingCode();
				//written = false;
				//needStateSavingCode = false;
			}
		}
		
		//System.out.println("end visit(VariableDeclaration p)");
	}

	
	public void visit(FieldDeclaration p) throws ParseTreeException {
		//System.out.println("start visit(FieldDeclaration p)");
		
		addClassField(currentClassDeclaration.getObjectID(), p);
		
		super.visit(p);
		//System.out.println("end visit(FieldDeclaration p)");
	}
	
	public void visit(AllocationExpression p) throws ParseTreeException {
		//System.out.println("visit(AllocationExpression p) ");
		
		boolean isSaveState = false;
		if(p.getClassBody() != null)
			isSaveState = true;
		
		Hashtable<String, Boolean> tmpVarSet = null;
		HashSet<String> tmpAssignSet = null;
		
		if(isSaveState) {
			int curMethodID =  getCurMethodID();
			Hashtable<String, Boolean> beforeVarSet = blockLocalVarSet.get(curMethodID);
			HashSet<String> beforeAssignSet = assignedVarSet.get(curMethodID);
		
			tmpVarSet = getPrevVarInfo(beforeVarSet);
			tmpAssignSet = getPrevAssignVarInfo(beforeAssignSet);
		}
		
		super.visit(p);
		
		if(isSaveState) {
			restorePrevVarInfo(tmpVarSet);
			restorePrevAssignVarInfo(tmpAssignSet);
		}
	}
	
	public void visit(ClassDeclaration p) throws ParseTreeException {
		//System.out.println("start visit(ClassDeclaration p)");

		//System.out.println("ClassDeclaration p name = " + p.getName());
		assignedVarSet.clear();
		blockLocalVarSet.clear();
		methodParSet.clear();
		classFieldSet.put(p.getObjectID(), new Hashtable<String, Boolean>());
		
		super.visit(p);
				
		classFieldSet.remove(p.getObjectID());
		classFieldAssignSet.remove(p.getObjectID());
		
		//System.out.println("end visit(ClassDeclaration p)");
	}

	
}

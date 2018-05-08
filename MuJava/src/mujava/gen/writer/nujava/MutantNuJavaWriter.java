package mujava.gen.writer.nujava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mujava.MuJavaMutantInfo;
import mujava.MutantID;
import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import mujava.plugin.wizards.generation.ClassState;
import mujava.plugin.wizards.generation.MethodState;
import mujava.plugin.wizards.generation.ParamState;
import mujava.plugin.wizards.generation.VariableState;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ArrayAccess;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.Leaf;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.StatementList;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;

public abstract class MutantNuJavaWriter extends MutantWriter {

	protected Hashtable<String, ClassState> states = new Hashtable<String, ClassState>();

	protected MutantNuJavaWriter(Environment env, String name)
			throws IOException {
		super(env, name, MutantOperator.GEN_NUJAVA);
	}

	/**
	 * NuJava를 위해 state를 넘겨주기 위해 저장하기 위한 함수, NuJava용 writer는 반드시 생성전에 호출되어야 한다.
	 * 호출되지 않는 경우, 모든 method는 method-level weak mutation이 불가능하다고 간주한다.
	 * 
	 * @param states
	 */
	public void setStateVariables(Hashtable<String, ClassState> states) {
		if (states != null)
			this.states = states;
	}

	@Override
	public void visit(ClassDeclaration p) throws ParseTreeException {
		// 부모 로직을 분해하기 위해 직접 복사
		ClassDeclaration temp = currentClassDeclaration;
		currentClassDeclaration = p;

		//
		this.evaluateDown(p);
		writeClassDeclSignature(p);

		writeBeginNest();

		// Metamutant의 내의 특정 change point가 수행을 위한 Instance Variable 선언
//		{
//			pushNest(); // increase tab count;
//			// Change Point 수행 여부를 확인하는 array 생성
//			writeTab();
//			out.println("/** chage point decision array **/");
//			increaseLineNumber();
//
//			// TODO array 이름 중복 확인 후 간단한 이름으로 변경
//			int MAX = 0;
//			for (ChangePoint cp : changePoints) {
//				try {
//					int value = Integer.parseInt(cp.getID());
//					if (value > MAX) {
//						MAX = value;
//					}
//				} catch (NumberFormatException e) {
//				}
//			}
//			writeTab();
//			out
//					.println("public static boolean[] __mujava__ExecuteChangePoint = new boolean[ "
//							+ (MAX + 1) + " ];");
//			increaseLineNumber();
//
//			out.println();
//			increaseLineNumber();
//
//			popNest(); // decrease tab count;
//		}

		writeClassDeclBody(p);

		// 상태 정의에 따라 Method-Weak Mutation을 지원하기 위한 기본 Class 변수 선언
		{
			String fullClassName = getEnvironment().currentClassName();
			ClassState cState = states.get(fullClassName);
			if (cState != null) {

				// 해당 클래스에 대한 상태가 null이 아니면 스텁을 반드시 생성해야 한다
				List<VariableState> variables = cState
						.getStubConstructorVariableList();

				// writeTab();
				// out
				// .println("static com.thoughtworks.xstream.XStream __stream =
				// new
				// com.thoughtworks.xstream.XStream(new
				// com.thoughtworks.xstream.io.xml.XppDomDriver());");
				// increaseLineNumber();

				writeTab();
				out.println("/* for weak mutation */");
				increaseLineNumber();

				writeTab();
				OJClass clz = getEnvironment().lookupClass(fullClassName);
				out.println("public " + clz.getSimpleName()
						+ "(nujava.IPreState preState) {");
				increaseLineNumber();
				pushNest();

				writeTab();
				out.println("Object obj = \"\";");
				increaseLineNumber();

				// state variables
				for (VariableState vState : variables) {
					String typeName = vState.getType();
					OJClass varClz = getEnvironment().lookupClass(typeName);
					if (varClz.isPrimitive())
						typeName = varClz.primitiveWrapper().getName();

					writeTab();
					out.println("obj = preState.getPreValue(\""
							+ vState.getVariableName() + "\");");
					increaseLineNumber();

					writeTab();
					out.println("if(obj == null) {");
					increaseLineNumber();
					pushNest();

					writeTab();
					out.print("obj = ");
					if (typeName.equals("java.lang.Boolean"))
						out.println(" false;");
					else if (typeName.equals("java.lang.Character")) {
						out.println(" (char)0;");
					} else if (typeName.equals("java.lang.Byte")) {
						out.println(" (byte)0;");
					} else if (typeName.equals("java.lang.Integer")) {
						out.println(" (int)0;");
					} else if (typeName.equals("java.lang.Short")) {
						out.println(" (short)0;");
					} else if (typeName.equals("java.lang.Long")) {
						out.println(" (long)0;");
					} else if (typeName.equals("java.lang.Double")) {
						out.println(" (double)0;");
					} else if (typeName.equals("java.lang.Float")) {
						out.println(" (float)0;");
					} else {
						out.println(" null;");
					}
					increaseLineNumber();

					popNest();
					writeTab();
					out.println("} ");
					increaseLineNumber();

					writeTab();
					out.println("this." + vState.getVariableName() + " = ("
							+ typeName + ")obj;");
					increaseLineNumber();
				}

				popNest();
				writeTab();
				out.println("}");
				increaseLineNumber();

				popNest();
			}
		}

		writeEndNest();

		this.evaluateUp(p);

		// 기존 visit class로 현재 작업 Class 이름을 복원
		currentClassDeclaration = temp;
	}

	private void writeEndNest() {
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	private void writeBeginNest() {
		writeTab();
		out.println("{");
		increaseLineNumber();
	}

	private void writeClassDeclBody(ClassDeclaration p)
			throws ParseTreeException {

		MemberDeclarationList classbody = p.getBody();
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

	}

	private void writeClassDeclSignature(ClassDeclaration p)
			throws ParseTreeException {
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
		increaseLineNumber();
		// }

		super.evaluateUp(p);

	}

	@Override
	public void visit(MethodDeclaration p) throws ParseTreeException {
		String fullClassName = this.getEnvironment().currentClassName();
		ClassState cState = states.get(fullClassName);

		// method-level weak mutation을 위한 instrumentation추가
		if (cState != null) {
			String key = MethodState.makeHashKeyString(p);
			MethodState mState = cState.getMethodState(key);
			if (mState != null) {

				/* weak mutation method 삽입. */
				writeTab();
				out.println("/** instrumented code*/");
				increaseLineNumber();
				writeWeakMethodCode(p, mState);

				/** 기존의 코드는 변환된 이름으로 생성 */

				// 이름 변경 후 method body 생성.
				writeTab();
				out.println("/** instrumented code*/");
				increaseLineNumber();

				int fromLocation = getLineNumber();

				evaluateDown(p);
				String methodName = p.getName();
				methodName = methodName + "_mutant_";
				writeMethodSignature(p, methodName);
				writeMethodBody(p);
				evaluateUp(p);
				out.println();
				increaseLineNumber();

				int toLocation = getLineNumber();

				// 본 method에서 생성되는 mutant를 구분하는 사후 작업.
				List<MuJavaMutantInfo> newlyCreatedMutants = new ArrayList<MuJavaMutantInfo>();
				for (MuJavaMutantInfo info : this.mujavaMutantInfos) {
					int loc = info.getChangeLocation();
					if (loc > fromLocation && loc < toLocation)
						newlyCreatedMutants.add(info);
				}

				/* wrapper code 삽입 */
				writeTab();
				out.println("/** rewrited code*/");
				increaseLineNumber();
				writeWrapperMethodCode(p, methodName, mState,
						newlyCreatedMutants);

				return;
			}
		}

		evaluateDown(p);
		String methodName = p.getName();
		writeMethodSignature(p, methodName);
		writeMethodBody(p);
		evaluateUp(p);
		out.println();
		increaseLineNumber();
	}

	protected void writeMethodBody(MethodDeclaration p)
			throws ParseTreeException {
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
			this.visit(bl);
			popNest();
			writeTab();
			out.print("}");
		}

		out.println();
		increaseLineNumber();
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
		writeMethodSignature(m, "");
	}

	protected void writeMethodSignature(MethodDeclaration m, String methodName)
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
		if (methodName != null && !methodName.isEmpty())
			out.print(methodName);
		else
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
	}

	protected void writeWeakMethodCode(MethodDeclaration p, MethodState mState) {
		writeTab();
		out.println("static public byte[] " + p.getName() + "_weak_() {");
		increaseLineNumber();
		pushNest();

		writeTab();
		out
				.println("nujava.IPreState preState = nujava.MutantMonitor.getInstance().getPreState();");
		increaseLineNumber();
		writeTab();
		out
				.println("nujava.IPostState postState = nujava.InnerStates.newPostState();");
		increaseLineNumber();

		// parameter restore
		writeTab();
		out.println("/* restore parameters */");
		increaseLineNumber();
		for (ParamState pState : mState.getParamStates()) {
			writeTab();
			String type = pState.getType();
			out.print(type);
			out.print(" ");
			out.print(pState.getParamName());
			out.print(" = ");

			OJClass clz = this.getEnvironment().lookupClass(type);
			if (clz.isPrimitive()) {
				out.print("(");
				out.print(clz.primitiveWrapper().getName());
				out.print(")");
			}
			out.println("preState.getPreValue(\"param" + pState.getIndex()
					+ "\");");
			increaseLineNumber();
		}

		// variable restore
		writeTab();
		out.println("/* restore variables*/");
		increaseLineNumber();

		writeTab();
		String clzName = getEnvironment().currentClassName();
		out.println(clzName + " targetClass = new " + clzName + "(preState);");
		increaseLineNumber();

		// body
		writeTab();
		out.println("try {");
		increaseLineNumber();
		pushNest();

		writeTab();
		if (!"void".equals(mState.getReturnType())) {
			out.print(mState.getReturnType());
			out.print(" result = ");
		}

		out.print("targetClass." + mState.getMethodName() + "_mutant_(");
		StringBuffer sb = new StringBuffer();
		for (ParamState pState : mState.getParamStates()) {
			sb.append(pState.getParamName());
			sb.append(", ");
		}
		if (!mState.getParamStates().isEmpty())
			sb.setLength(sb.length() - 2);
		out.println(sb.toString() + ");");
		increaseLineNumber();

		if (!"void".equals(mState.getReturnType())) {
			writeTab();
			out.println("postState.addPostVariable(\".\", result);");
			increaseLineNumber();
		}

		writeTab();
		out.println("/* save post states */");
		increaseLineNumber();
		for (VariableState vState : mState.getVariableStateList()) {
			if (vState.isPostState()) {
				writeTab();
				out.println("postState.addPostVariable(\""
						+ vState.getVariableName() + "\", targetClass."
						+ vState.getVariableName() + ");");
				increaseLineNumber();
			}
		}
		popNest();
		writeTab();
		out.println("} catch (Exception e) {");
		increaseLineNumber();

		pushNest();

		writeTab();
		out.println("postState.addPostVariable(\".\", e.toString());");
		increaseLineNumber();

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();

		writeTab();
		out.println("return postState.toByteArray();");
		increaseLineNumber();

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}

	protected void writeWrapperMethodCode(MethodDeclaration m,
			String targetMethodName, MethodState mState,
			List<MuJavaMutantInfo> mutants) throws ParseTreeException {

		// MutantID에서 index를 제외한 값을 추출한다.
		List<String> list = new ArrayList<String>();
		for (MuJavaMutantInfo info : mutants) {
			String mutantID = info.getMutantID();
			String anotherMutantID = MutantID.toStringWithoutIndex(mutantID);
			if (!list.contains(anotherMutantID))
				list.add(anotherMutantID);
		}

		/** insert new method which has the same name of target method m */
		writeMethodSignature(m);

		writeTab();
		out.print("{");
		out.println();
		increaseLineNumber();
		pushNest();

		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		sb.append(targetMethodName);
		sb.append("(");
		int tempLength = sb.length();
		for (ParamState pState : mState.getParamStates()) {
			sb.append(pState.getParamName() + ",");
		}
		if (sb.length() != tempLength)
			sb.setLength(sb.length() - 1);
		sb.append(");");
		String methodCall = sb.toString();

		/** body start */
		if (!list.isEmpty()) {
			/** 생성된 mutant에 해당할때만 상태를 저장한다. */
			sb.setLength(0);
			sb.append("if (nujava.NuJavaHelper.isExpWeakMode() && ( ");
			for (String mutantID : list) {
				sb.append("nujava.NuJavaHelper.isGivenID(\"" + mutantID
						+ "\", " + MutantID.getChangePointID(mutantID)
						+ ") || ");
			}
			sb.setLength(sb.length() - 4);
			sb.append(")) {");

			writeTab();
			out.println(sb.toString());
			increaseLineNumber();
			pushNest();

			writeTab();
			out
					.println("nujava.IPreState preState = nujava.InnerStates.newPreState();");
			increaseLineNumber();

			/** save parameters */
			writeTab();
			out.println("/* save parameters */");
			increaseLineNumber();

			for (ParamState pState : mState.getParamStates()) {
				// parameter type을 구한다.
				String typeName = pState.getType();
				OJClass varClz = getEnvironment().lookupClass(typeName);
				if (varClz.isPrimitive())
					typeName = varClz.primitiveWrapper().getName();

				// 파일에 parameter를 저장할 코드를 생성한다.
				writeTab();
				out.println("preState.addPreVariable(\"param"
						+ pState.getIndex() + "\", (" + typeName + ")"
						+ pState.getParamName() + ");");
				increaseLineNumber();
			}

			/** save pre states */
			writeTab();
			out.println("/* save pre states */");
			increaseLineNumber();
			for (VariableState vState : mState.getVariableStateList()) {
				// type을 구한다.
				String typeName = vState.getType();
				OJClass varClz = getEnvironment().lookupClass(typeName);
				if (varClz.isPrimitive())
					typeName = varClz.primitiveWrapper().getName();

				writeTab();
				if (vState.isPreState()) {
					out.print("preState.addPreVariable");
				} else {
					out.print("preState.addExtraVariable");
				}
				out
						.println("(\"" + vState.getVariableName() + "\", ("
								+ typeName + ")this."
								+ vState.getVariableName() + ");");
				increaseLineNumber();
			}
			writeTab();
			out
					.println("nujava.MutantMonitor.getInstance().pushPreStateTable(\""
							+ mState.toHashKeyString() + "\", preState);");
			increaseLineNumber();

			writeTab();
			if (!"void".equalsIgnoreCase(m.getReturnType().getName())) {
				TypeName tName = m.getReturnType();
				String typename = tName.getName().replace('$', '.');
				out.print(typename);

				int dims = tName.getDimension();
				out.print(TypeName.stringFromDimension(dims));
				out.print(" result = ");
			}
			out.println(methodCall);
			increaseLineNumber();

			writeTab();
			out
					.println("nujava.MutantMonitor.getInstance().popPreStateTable(\""
							+ mState.toHashKeyString() + "\");");
			increaseLineNumber();

			writeTab();
			out.print("return ");
			if (!"void".equalsIgnoreCase(m.getReturnType().getName())) {
				out.print("result");
			}
			out.println(";");
			increaseLineNumber();

			popNest();
			writeTab();
			out.println("}");
			increaseLineNumber();
		}

		writeTab();
		if (!"void".equalsIgnoreCase(m.getReturnType().getName())) {
			out.print("return ");
		}
		out.println(methodCall);
		increaseLineNumber();

		popNest();
		writeTab();
		out.println("}");
		increaseLineNumber();
	}
}
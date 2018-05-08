package mujava.gen.seeker;

import java.util.ArrayList;
import java.util.List;

import kaist.selab.util.Helper;
import kaist.selab.util.MuJavaLogger;
import mujava.op.util.ChangePoint;
import openjava.mop.CannotAlterException;
import openjava.mop.ClosedEnvironment;
import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJField;
import openjava.mop.OJMethod;
import openjava.mop.Toolbox;
import openjava.ptree.ArrayAccess;
import openjava.ptree.CaseLabel;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.MemberDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;
import openjava.ptree.util.AnonymousClassEnvironment;
import openjava.ptree.util.AnonymousMethodCall;
import openjava.ptree.util.VariableBinder;

import org.eclipse.jdt.core.IJavaElement;

public abstract class AbstractChangePointSeeker extends VariableBinder {
	private List<ChangePoint> changePoints = new ArrayList<ChangePoint>();

	protected MethodDeclaration currentMethod = Helper.OutOfMethod;

	protected Expression currentStatement = null;

	protected Environment fileEnvironment;

	protected String operatorName = new String();

	protected IJavaElement sourceFile = null;

	private MethodDeclaration previousMethod = Helper.OutOfMethod;

	public AbstractChangePointSeeker(Environment env,
			IJavaElement originalSourceFile, String opName) {
		super(env);

		this.fileEnvironment = env;
		this.sourceFile = originalSourceFile;
		this.operatorName = opName;

		// Delegates a set of change point to the its own writer
		ChangePoint.resetCount(0);
	}

	protected void addChangePoint(ChangePoint point) {
		changePoints.add(point);
	}

	/**
	 * Never return null object
	 */
	public List<ChangePoint> getChangePoints() {
		return changePoints;
	}

	public Environment getFileEnvironment() {
		return fileEnvironment;
	}

	public String getOperatorName() {
		return operatorName;
	}

	protected OJClass getSelfType() throws ParseTreeException {
		OJClass result;
		try {
			Environment env = getEnvironment();
			String selfname = env.currentClassName();
			result = env.lookupClass(selfname);
		} catch (Exception ex) {
			throw new ParseTreeException(ex);
		}
		return result;
	}

	// /**
	// * It return the target soruce file. For internal uses for generating
	// unique
	// * mutant ID.
	// *
	// * see getMutantCodeWriter()
	// */
	// public IJavaElement getSourceJavaElement() {
	// return sourceFile;
	// }
	
	OJClass getAnonymousMethodReturnType(MethodCall methodCall) {

		AnonymousMethodCall amc = new AnonymousMethodCall(methodCall);

		ExpressionList args = amc.getArguments();

		OJClass[] paramClasses = new OJClass[args.size()];
		for (int i = 0; i < paramClasses.length; i++) {
			Expression argExp = args.get(i);

			try {
				paramClasses[i] = argExp.getType(getEnvironment());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		AnonymousClassEnvironment anonyEnv = (AnonymousClassEnvironment) super
				.getParent(getEnvironment());
		OJMethod method = anonyEnv.lookupMethod(amc.getName(), paramClasses);

		OJClass methodType = method.getReturnType();
		if (methodType != null) {
			return methodType;
		}

		try {
			Environment targetEnv = getParent(anonyEnv);

			return amc.getType(targetEnv, paramClasses);

		} catch (Exception e) {
			MuJavaLogger.getLogger().error(
					"[First Level in CommonMutator.getType()]");
			e.printStackTrace();
		}

		return null;
	}

	OJClass getMethodReturnType(MethodCall methodCall)
			throws NoSuchMemberException, ParseTreeException {

		// There are two cases in the MethodCall,
		// one is a general MethodCall located in normal class.
		// The other is a MethodCall located in anonymous class,
		// OpenJava does not support to find some methods from those yet.
		String className = getEnvironment().currentClassName();
		if ("<anonymous class>".equalsIgnoreCase(className)) {
			try{

				return getAnonymousMethodReturnType(methodCall);
			}catch(Exception e){
				throw new ParseTreeException(e);
			}

		} else { // General Method

			Environment targetEnv = getEnvironment();

			try {		
				return methodCall.getType(targetEnv);
			} catch (Exception e) {
				MuJavaLogger
						.getLogger()
						.error("[Unhandled Exception in AbstractChangePointSeeker.getMethodReturnType()]");
				e.printStackTrace();
			}

		}

		throw new NoSuchMemberException(methodCall.toString());
	}
	
	protected OJClass getType(Expression p) throws ParseTreeException {

		if (p instanceof MethodCall) {

			MethodCall m = (MethodCall) p;

			try {
				OJClass type = getMethodReturnType(m);
				if (type != null) {
					return type;
				}
			} catch (NoSuchMemberException e) {
				e.printStackTrace();
			}

		} else {

			try {
				OJClass type = p.getType(getEnvironment());

				if (type != null) {
					return type;
				}
			} catch (Exception e) {
				MuJavaLogger
						.getLogger()
						.error("[Unhandled Exception in AbstractChangePointSeeker.getType()]");
				e.printStackTrace();
			}
		}

		MuJavaLogger.getLogger().error("cannot resolve the type of expression");
		MuJavaLogger.getLogger().error(p.getClass() + " : " + p);
		MuJavaLogger.getLogger().error(getEnvironment());

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
			MuJavaLogger.getLogger().error(
					refexpr + " : " + refexprtype + " : " + comptype);
		}

		return null;
	}

	protected boolean isFinalVariable(Expression p) {
		if (p instanceof ArrayAccess) {
			ArrayAccess aa = (ArrayAccess) p;
			return isFinalVariable(aa.getReferenceExpr());
		} else if (p instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess) p;
			Expression exp = fa.getReferenceExpr();
			String name = fa.getName();
			if (exp == null) {
				Environment env = getEnvironment();
				if (env instanceof ClosedEnvironment)
					return ((ClosedEnvironment) env).isFinal(name);
			} else {
				try {
					OJClass clz = getType(exp);
					OJField field = clz.getField(name, clz);
					if (field.getModifiers().isFinal())
						return true;
				} catch (ParseTreeException e) {
					e.printStackTrace();
				} catch (NoSuchMemberException e) {
					e.printStackTrace();
				}
			}
			return isFinalVariable(exp);
		} else if (p instanceof Variable) {
			Variable v = (Variable) p;
			String name = v.toString();

			Environment env = getEnvironment();
			if (env instanceof ClosedEnvironment)
				return ((ClosedEnvironment) env).isFinal(name);
		}
		return false;
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

	protected boolean isOverRidingField(String name, OJClass child) {
		MemberDeclarationList cList = null;
		try {
			ClassDeclaration cSrc = child.getSourceCode();

			if (cSrc == null)
				return false;

			cList = cSrc.getBody();
			if (cList == null)
				return false;

			if (cList.size() < 1)
				return false;
		} catch (CannotAlterException e) {
			e.printStackTrace();
		}

		// search the target from the child class
		FieldDeclaration childVar = null;
		for (int i = 0; i < cList.size(); i++) {
			MemberDeclaration member = cList.get(i);
			if (member instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) member;
				if (name.equals(field.getName())) {
					ModifierList list = field.getModifiers();
					if (!list.contains(ModifierList.ABSTRACT)) {
						childVar = field;
						break;
					}
				}
			}
		}
		if (childVar == null)
			return false;

		return isOverRidingField(name, child, childVar);
	}

	protected boolean isOverRidingField(String name, OJClass child,
			FieldDeclaration childField) {

		OJClass parent = child.getSuperclass();
		if (parent == null)
			return false;

		MemberDeclarationList pList = null;
		try {
			ClassDeclaration pSrc = parent.getSourceCode();
			if (pSrc == null)
				return false;

			// If the body is null, the declared class is ??.
			pList = pSrc.getBody();
			if (pList == null)
				return false;
		} catch (CannotAlterException e) {
			e.printStackTrace();
		}

		// search the target from the parent class
		FieldDeclaration parentVar = null;
		for (int i = 0; i < pList.size(); i++) {
			MemberDeclaration member = pList.get(i);
			if (member instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) member;
				if (name.equals(field.getName())) {
					ModifierList list = field.getModifiers();
					if (!list.contains(ModifierList.ABSTRACT)) {
						TypeName pType = field.getTypeSpecifier();
						TypeName cType = childField.getTypeSpecifier();
						if (!pType.equals(cType))
							continue;
						ModifierList cList = childField.getModifiers();
						if (!cList.equals(list))
							continue;

						parentVar = field;
						break;
					}
				}
			}
		}
		if (parentVar != null)
			return true;

		return isOverRidingField(name, child, childField);
	}

	protected boolean isOverRidingMethod(String name, OJClass[] args,
			OJClass parent, OJMethod childMethod) {

		if (parent == null)
			return false;

		OJMethod[] methods = parent.getDeclaredMethods();
		if (methods != null && methods.length > 1) {
			// search the target from the child class
			OJMethod parentMethod = Toolbox.pickupMethod(methods, name, args);

			if (parentMethod != null) {
				OJClass[] cExceptions = childMethod.getExceptionTypes();
				OJClass[] pExceptions = parentMethod.getExceptionTypes();
				return Toolbox.isSame(cExceptions, pExceptions);
			}
		}

		return isOverRidingMethod(name, args, parent.getSuperclass(),
				childMethod);
	}

	protected boolean isOverRidingMethod(String name, OJClass[] args,
			OJClass child) {
		OJClass parent = child.getSuperclass();
		if (parent == null)
			return false;

		OJMethod[] methods = child.getDeclaredMethods();
		if (methods == null || methods.length < 1)
			return false;

		// search the target from the child class
		OJMethod childMethod = Toolbox.pickupMethod(methods, name, args);
		if (childMethod == null)
			return false;

		return isOverRidingMethod(name, args, parent, childMethod);
	}

	protected boolean isParamterVariable(Expression p) {
		if (p instanceof ArrayAccess) {
			ArrayAccess aa = (ArrayAccess) p;
			return isParamterVariable(aa.getReferenceExpr());
		} else if (p instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess) p;
			Expression exp = fa.getReferenceExpr();
			if (exp == null) {
				Environment env = getEnvironment();
				return ((ClosedEnvironment) env).isParameter(fa.getName());
			}
			return isParamterVariable(exp);
		} else if (p instanceof Variable) {
			Variable v = (Variable) p;
			Environment env = getEnvironment();
			if (env instanceof ClosedEnvironment) {
				return ((ClosedEnvironment) env).isParameter(v.toString());
			}
		}

		return false;
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

	protected boolean isStatement(Expression exp) {
		assert (exp != null);
		if (currentStatement == null)
			return false;

		return exp.getObjectID() == currentStatement.getObjectID();
	}

	// public Hashtable<Expression, MethodDeclaration> getTargetExpression() {
	// return targetExpressions;
	// }

	protected void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public void visit(CaseLabel p) throws ParseTreeException {
		// do nothing
		// System.out.println("Case Label");
	}

	/**
	 * 하나의 expression이 하나의 statement를 이루는 경우를 구분하기 위해 currentStatement를 관리한다.
	 */
	public void visit(ExpressionStatement p) throws ParseTreeException {
		currentStatement = p.getExpression();
		super.visit(p);
		currentStatement = null;
	}

	@Override
	public MemberDeclaration evaluateDown(MethodDeclaration ptree)
			throws ParseTreeException {
		if (!(ptree.getName().equals("main"))) {
			previousMethod = currentMethod;
			currentMethod = ptree;
		}
		return super.evaluateDown(ptree);
	}

	@Override
	public MemberDeclaration evaluateUp(MethodDeclaration ptree)
			throws ParseTreeException {
		if (!(ptree.getName().equals("main"))) {
			currentMethod = previousMethod;
		}
		return super.evaluateUp(ptree);
	}

	/**
	 * Returns unique hashCode for mutating target Class.
	 * 
	 * @return a hash code for this target class.
	 */
	public int getTargetFileID() {
		String packageName = fileEnvironment.getPackage();
		if (packageName == null) {
			packageName = "";
		}

		String overallName = packageName + "." + sourceFile.getElementName();

		return overallName.hashCode();
	}
}

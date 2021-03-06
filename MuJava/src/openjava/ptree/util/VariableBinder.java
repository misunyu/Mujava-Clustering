package openjava.ptree.util;

import openjava.mop.ClosedEnvironment;
import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJField;
import openjava.mop.OJMethod;
import openjava.mop.Signature;
import openjava.ptree.ArrayAccess;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.DoWhileStatement;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.FieldAccess;
import openjava.ptree.ForStatement;
import openjava.ptree.MethodCall;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.tools.DebugOut;

/**
 * The class <code>VariableBinder</code>
 * <p>
 * For example
 * 
 * <pre>
 * </pre>
 * 
 * <p>
 * 
 * @author Michiaki Tatsubori
 * @version 1.0
 * @since $Id: VariableBinder.java,v 1.1 2009-10-30 09:05:08 swkim Exp $
 * @see java.lang.Object
 */
public class VariableBinder extends ScopeHandler {

	private static void bindForInit(TypeName tspec,
			VariableDeclarator[] vdecls, Environment env) {
		for (int i = 0; i < vdecls.length; ++i) {
			String type = tspec.toString() + vdecls[i].dimensionString();
			String name = vdecls[i].getVariable();
			bindName(env, type, name);

			// Inserted code start
			if (env instanceof ClosedEnvironment) {
				((ClosedEnvironment) env).registerInitVar(name);
			}
			// Inserted code end
		}
	}

	/**
	 * Edited by swkim
	 * 
	 * @param var_decl
	 * @param env
	 */
	private static void bindLocalVariable(VariableDeclaration var_decl,
			Environment env) {
		String type = var_decl.getTypeSpecifier().toString();
		String name = var_decl.getVariable();
		bindName(env, type, name);

		// Inserted code start
		if (var_decl.getInitializer() != null
				&& env instanceof ClosedEnvironment) {
			((ClosedEnvironment) env).registerInitVar(name);
			((ClosedEnvironment) env).bindModifier(name, var_decl
					.getModifiers());
		}
		// Inserted code end
	}

	private static void bindName(Environment env, String type, String name) {
		String qtypename = env.toQualifiedName(type);
		try {
			OJClass clazz = env.lookupClass(qtypename);
			if (clazz == null)
				clazz = OJClass.forName(qtypename);
			env.bindVariable(name, clazz);
			DebugOut.println("binds variable\t" + name + "\t: " + qtypename);
		} catch (OJClassNotFoundException e) {
			System.err.println("VariableBinder.bindName() " + e.toString()
					+ " : " + qtypename);
			System.err.println(env);
		}
	}

	/**
	 * Modified by swkim.
	 * 
	 * @param param
	 * @param env
	 */
	private static void bindParameter(Parameter param, Environment env) {
		String type = param.getTypeSpecifier().toString();
		String name = param.getVariable();
		ModifierList list = param.getModifiers();

		bindName(env, type, name);

		// Inserted code start
		if (env instanceof ClosedEnvironment) {
			((ClosedEnvironment) env).registerInitVar(name);
			((ClosedEnvironment) env).bindModifier(name, list);
			((ClosedEnvironment) env).registerParameter(name);
		}
		// Inserted code end
	}

	private static OJMethod pickupMethod(OJClass reftype, String name,
			OJClass[] argtypes) {
		try {
			return reftype.getAcceptableMethod(name, argtypes, reftype);
		} catch (NoSuchMemberException e) {
			return null;
		}
	}

	// Copy initialized variable info to parent
	@Override
	public Statement evaluateUp(DoWhileStatement ptree)
			throws ParseTreeException {
		Environment env = getEnvironment();
		if (env instanceof ClosedEnvironment) {
			ClosedEnvironment cEnv = (ClosedEnvironment) env;
			cEnv.copyInitVaribleToParent();
		}

		return super.evaluateUp(ptree);
	}

	public VariableBinder(Environment env) {
		super(env);
	}

	// Inserted
	@Override
	public Expression evaluateDown(AssignmentExpression p)
			throws ParseTreeException {
		super.evaluateDown(p);
		Expression left = p.getLeft();
		if (isLocalVariable(left)) {
			initializedLocalVariable(left);
		}
		return p;
	}

	public Statement evaluateDown(ForStatement ptree) throws ParseTreeException {
		super.evaluateDown(ptree);
		TypeName tspec = ptree.getInitDeclType();
		if (tspec == null) {
			ptree.getStatements();
			return ptree;
		}

		VariableDeclarator[] vdecls = ptree.getInitDecls();
		bindForInit(tspec, vdecls, getEnvironment());

		return ptree;
	}

	public Parameter evaluateDown(Parameter ptree) throws ParseTreeException {
		super.evaluateDown(ptree);
		bindParameter(ptree, getEnvironment());

		return ptree;
	}

	public Statement evaluateDown(VariableDeclaration ptree)
			throws ParseTreeException {
		super.evaluateDown(ptree);
		bindLocalVariable(ptree, getEnvironment());

		return ptree;
	}

	@Override
	public void visit(ForStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		// modified here
		// p.childrenAccept(this);
		// instread of//--------------------

		//----------------
		ParseTree tree = p.getInitDeclType();
		TypeName tspec = p.getInitDeclType();
		VariableDeclarator[] vdecls = p.getInitDecls();

		if (tree != null)
			tree.accept(this);
		
		tree = p.getInit();
		if (tree != null) {
			Environment tempEnv = getEnvironment();
			pop();
			tree.accept(this);
			push(tempEnv);
		 }else if (tspec != null && vdecls != null && vdecls.length != 0) {
			tspec.accept(this);
			//out.print(" ");
			vdecls[0].accept(this);
			for (int i = 1; i < vdecls.length; ++i) {
				//out.print(", ");
				vdecls[i].accept(this);
			}
		}
		
		tree = p.getCondition();
		if (tree != null)
			tree.accept(this);
		tree = p.getStatements();
		if (tree != null)
			tree.accept(this);
		tree = p.getIncrement();
		if (tree != null)
			tree.accept(this);
		// modified end

		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	private void initializedLocalVariable(Expression p) {
		if (p instanceof Variable) {
			String name = ((Variable) p).toString();
			Environment env = getEnvironment();
			if (env instanceof ClosedEnvironment) {
				ClosedEnvironment cEnv = (ClosedEnvironment) env;
				cEnv.registerInitVar(name);
			}
		} else if (p instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess) p;
			Expression expr = fa.getReferenceExpr();
			if (expr == null) {
				String name = fa.getName();
				Environment env = getEnvironment();
				if (env instanceof ClosedEnvironment) {
					ClosedEnvironment cEnv = (ClosedEnvironment) env;
					cEnv.registerInitVar(name);
				}
				return;
			}

			initializedLocalVariable(expr);
		}
	}

	protected OJMethod getMethod(Environment env, MethodCall m, OJClass[] args)
			throws Exception {
		OJClass selftype = env.lookupClass(env.currentClassName());
		OJClass[] argtypes = null;

		if (args == null) {
			ExpressionList eList = m.getArguments();
			argtypes = new OJClass[eList.size()];
			for (int i = 0; i < argtypes.length; ++i) {
				argtypes[i] = eList.get(i).getType(env);
			}
		} else
			argtypes = args;

		OJMethod method = null;
		String name = m.getName();
		Expression refexpr = m.getReferenceExpr();
		OJClass reftype = null;

		if (selftype == null && env.currentClassName() == "<anonymous class>") {
			// AnonymousMethodCall mc = new AnonymousMethodCall(m);
			AnonymousClassEnvironment anonyEnv = (AnonymousClassEnvironment) super
					.getParent(getEnvironment());

			method = anonyEnv.lookupMethod(m.getName(), argtypes);
			if (method == null) {
				method = getMethod(getParent(anonyEnv), m, argtypes);
				if (method != null)
					return method;
			}
		} else {
			if (refexpr != null) {
				reftype = refexpr.getType(env);
			} else {
				TypeName refname = m.getReferenceType();
				if (refname != null) {
					String qname = env.toQualifiedName(refname.toString());
					reftype = env.lookupClass(qname);
				}
			}

			if (reftype != null)
				method = pickupMethod(reftype, name, argtypes);
			if (method != null)
				return method;

			/* try to consult this class and outer classes */
			if (reftype == null) {
				OJClass declaring = selftype;
				while (declaring != null) {
					method = pickupMethod(declaring, name, argtypes);
					if (method != null)
						return method;

					/* consult innerclasses */
					OJClass[] inners = declaring.getDeclaredClasses();
					for (int i = 0; i < inners.length; ++i) {
						method = pickupMethod(inners[i], name, argtypes);
						if (method != null)
							return method;
					}

					declaring = declaring.getDeclaringClass();
				}
				reftype = selftype;
			}
		}

		Signature sign = new Signature(name, argtypes);
		NoSuchMemberException e = new NoSuchMemberException(sign.toString());
		method = reftype.resolveException(e, name, argtypes);
		return method;
	}

	// Inserted
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

	// Inserted
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
}

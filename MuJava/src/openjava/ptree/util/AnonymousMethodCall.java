package openjava.ptree.util;

import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJMethod;
import openjava.mop.Signature;
import openjava.ptree.Expression;
import openjava.ptree.MethodCall;
import openjava.ptree.TypeName;

public class AnonymousMethodCall extends MethodCall{
	public AnonymousMethodCall(MethodCall p) {
		super((Expression)null, p.getName(), p.getArguments());
	}
	
	public OJClass getType(Environment env, OJClass[] arg) throws Exception {
		
		OJClass selftype = env.lookupClass(env.currentClassName());
		
		Expression refexpr = getReferenceExpr();
		String name = getName();
		
		OJClass reftype = null;
		
		if (refexpr != null) {
			reftype = refexpr.getType(env);
		} else {
			TypeName refname = getReferenceType();
			if (refname != null) {
				String qname = env.toQualifiedName(refname.toString());
				reftype = env.lookupClass(qname);
			}
		}
		
		
		OJMethod method = null;
		
		if (reftype != null)
			method = pickupMethod(reftype, name, arg);
		if (method != null)
			return method.getReturnType();
		
		/* try to consult this class and outer classes */
		if (reftype == null) {
			OJClass declaring = selftype;
			while (declaring != null) {
				method = pickupMethod(declaring, name, arg);
				if (method != null)
					return method.getReturnType();
				
				/* consult innerclasses */
				OJClass[] inners = declaring.getDeclaredClasses();
				for (int i = 0; i < inners.length; ++i) {
					method = pickupMethod(inners[i], name, arg);
					if (method != null)
						return method.getReturnType();
				}
				
				declaring = declaring.getDeclaringClass();
			}
			reftype = selftype;
		}
		
		Signature sign = new Signature(name, arg);
		NoSuchMemberException e = new NoSuchMemberException(sign.toString());
		method = reftype.resolveException(e, name, arg);
		return method.getReturnType();
	}
	private static OJMethod pickupMethod(
			OJClass reftype,
			String name,
			OJClass[] argtypes) {
		try {
			return reftype.getAcceptableMethod(name, argtypes, reftype);
		} catch (NoSuchMemberException e) {
			return null;
		}
	}
}

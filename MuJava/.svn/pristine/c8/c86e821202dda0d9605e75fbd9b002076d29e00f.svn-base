package openjava.ptree.util;

import openjava.mop.ClassEnvironment;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.mop.OJField;
import openjava.mop.OJMethod;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ParameterList;

/**
 * Most of codes are copied from original AnonymousClassEnvironment.java (openjava.mop.AnonymousClassEnvironment)
 * @author swkim
 *
 */
public class AnonymousClassEnvironment extends ClassEnvironment
{
    private MemberDeclarationList members;
    private String base;

    public AnonymousClassEnvironment(Environment e, String baseClassName, MemberDeclarationList mdecls) {
	super(e);
        this.members = mdecls;
        this.base = baseClassName;
    }

    public String getClassName() {
	return "<anonymous class>";
    }

    public String toString() {
        return super.toString();
    }

//    public OJClass lookupMethodReturnType(String name, OJClass[] args) {
//    	
//    	for (int i = 0, len = members.size(); i < len; ++i) {
//            if (!(members.get(i) instanceof MethodDeclaration))  continue;
//            MethodDeclaration method = (MethodDeclaration) members.get(i);
//            
//            if (! name.equals(method.getName()))  continue;
//            
//            ParameterList params = method.getParameters();
//            if ( args.length != params.size()) continue;
//            
//            boolean found = false;
//            
//            for (int j = 0; j < params.size(); j++) {
//            	if(! args[j].getName().equals(params.get(j).getTypeSpecifier().getName())) break;
//            }
//            
//            if(found)
//				try {
//					return OJClass.forName(method.getReturnType().getName());
//				} catch (OJClassNotFoundException e) {
//				}
//        }
//		
//    	return null;
//    }
    
    public OJMethod lookupMethod(String name, OJClass[] args) {
    	
    	for (int i = 0, len = members.size(); i < len; ++i) {
            if (!(members.get(i) instanceof MethodDeclaration))  continue;
            MethodDeclaration method = (MethodDeclaration) members.get(i);
            
            if (! name.equals(method.getName()))  continue;
            
            ParameterList params = method.getParameters();
            if ( args.length != params.size()) continue;
            
            boolean found = false;
            
            for (int j = 0; j < params.size(); j++) {
            	if(! args[j].getName().equals(params.get(j).getTypeSpecifier().getName())) break;
            }
            
            if(found) return new OJMethod(this, null, method);
        }
		
    	return null;
    }
    
    public OJClass lookupBind(String name) {
        for (int i = 0, len = members.size(); i < len; ++i) {
            if (!(members.get(i) instanceof FieldDeclaration))  continue;
            FieldDeclaration field = (FieldDeclaration) members.get(i);
            if (! name.equals(field.getName()))  continue;
            return lookupClass(field.getName());
        }
        OJField field = pickupField(lookupClass(base), name);
        if (field != null)  return field.getType();
        /* not a field name of this class */
        return parent.lookupBind(name);
    }

    /**
     * Returns the qualified class name.
     *
     * @return the qualified name of the class which organizes this
     *         environment.
     */
    public String currentClassName() {
	return getClassName();
    }
}


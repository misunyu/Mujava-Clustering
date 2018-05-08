////////////////////////////////////////////////////////////////////////////
// Module : MutationSystem.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava;

import mujava.inf.IMutationSystem;
import mujava.util.InheritanceINFO;
import openjava.mop.OJSystem;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

/**
 * <p>
 * 
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class MutationSystem extends OJSystem implements IMutationSystem {
	private static MutationSystem system = null;

	/** Inheritance Informations */
	public static InheritanceINFO[] classInfo = null;

	// /**
	// * Return type of class.
	// *
	// * @param name
	// * of class
	// * @return type of class ( types: interface, abstract, GUI, main, normal,
	// * applet )
	// */
	// public static int getClassType(String class_name) {
	// try {
	// Class c = Class.forName(class_name);
	// if (c.isInterface())
	// return INTERFACE;
	// if (Modifier.isAbstract(c.getModifiers()))
	// return ABSTRACT;
	// Method[] ms = c.getDeclaredMethods();
	// if (ms != null) {
	// if ((ms.length == 1) && (ms[0].getName().equals("main")))
	// return MAIN_ONLY;
	// for (int i = 0; i < ms.length; i++) {
	// if (ms[i].getName().equals("main"))
	// return MAIN;
	// }
	// }
	// if (isGUI(c))
	// return GUI;
	// if (isApplet(c))
	// return APPLET;
	// return NORMAL;
	// } catch (Exception e) {
	// return -1;
	// } catch (Error e) {
	// return -1;
	// }
	//
	// }

	/**
	 * Return list of class names = class name of <i>dir</i> directory +
	 * <i>result</i>
	 */
	// TODO swkim
	// private static String[] getAllClassNames(String[] result, String dir) {
	// String[] classes;
	// String temp;
	// File dirF = new File(dir);
	// File[] classF = dirF.listFiles(new ExtensionFilter("class"));
	// if (classF != null) {
	// classes = new String[classF.length];
	// for (int k = 0; k < classF.length; k++) {
	// temp = classF[k].getAbsolutePath();
	// classes[k] = temp.substring(
	// MutationSystem.CLASS_PATH.length() + 1, temp.length()
	// - ".class".length());
	// classes[k] = classes[k].replace('\\', '.');
	// classes[k] = classes[k].replace('/', '.');
	// }
	// result = addClassNames(result, classes);
	// }
	// File[] sub_dir = dirF.listFiles(new DirFileFilter());
	// if (sub_dir == null)
	// return result;
	// for (int i = 0; i < sub_dir.length; i++) {
	// result = getAllClassNames(result, sub_dir[i].getAbsolutePath());
	// }
	// return result;
	// }
	// /**
	// * Return combine list of <i> list1 </i> and <i> list2</i> lists.
	// *
	// * @param list1
	// * String list
	// * @param list2
	// * String list
	// * @return combined list of list1 and list2
	// */
	// private static final String[] addClassNames(String[] list1, String[]
	// list2) {
	// if (list1 == null)
	// list1 = list2;
	// else {
	// int num = list1.length;
	// String[] temp = new String[num];
	// for (int i = 0; i < temp.length; i++) {
	// temp[i] = list1[i];
	// }
	// num = num + list2.length;
	// list1 = new String[num];
	// for (int i = 0; i < temp.length; i++) {
	// list1[i] = temp[i];
	// }
	// for (int i = temp.length; i < num; i++) {
	// list1[i] = list2[i - temp.length];
	// }
	// }
	// return list1;
	// }
	public static int getClassType(IType type) throws JavaModelException {

		if (type.isInterface())
			return INTERFACE;
		if (!type.isClass())
			return ABSTRACT;
		if(type.isAnonymous())
			return ANONYMOUS;
		
		String qName = type.getFullyQualifiedName();
		if (qName.startsWith("java.awt") || qName.startsWith("javax.swing"))
			return GUI;
		if (qName.startsWith("java.applet"))
			return APPLET;

		ITypeHierarchy hier = type
				.newTypeHierarchy(type.getJavaProject(), null);
		IType[] supers = hier.getAllSuperclasses(type);
		for (int i = 0; i < supers.length; i++) {
			String superQName = supers[i].getFullyQualifiedName();
			if (superQName.startsWith("java.awt")
					|| superQName.startsWith("javax.swing"))
				return GUI;
			if (superQName.startsWith("java.applet"))
				return APPLET;
		}

		return NORMAL;
	}

	/**
	 * Return inheritance information for given class <br>
	 * 
	 * @param class_name
	 *            name of class
	 * @return inheritance information
	 */
	public static InheritanceINFO getInheritanceInfo(String class_name) {
		for (int i = 0; i < classInfo.length; i++) {
			if (classInfo[i].getClassName().equals(class_name))
				return classInfo[i];
		}
		return null;
	}

	public static MutationSystem getMutationSystem() {
		if (system == null) {
			system = new MutationSystem();
		}
		return system;
	}

	// /** Examine if class <i>c</i> is an applet class */
	// private static boolean isApplet(Class c) {
	// while (c != null) {
	// if (c.getName().indexOf("java.applet") == 0)
	// return true;
	// c = c.getSuperclass();
	// if (c.getName().indexOf("java.lang") == 0)
	// return false;
	// }
	// return false;
	// }
	//
	// /** Examine if class <i>c</i> is a GUI class */
	// private static boolean isGUI(Class c) {
	// while (c != null) {
	// if ((c.getName().indexOf("java.awt") == 0)
	// || (c.getName().indexOf("javax.swing") == 0))
	// return true;
	// c = c.getSuperclass();
	// if (c.getName().indexOf("java.lang") == 0)
	// return false;
	// }
	// return false;
	// }

	public static boolean hasMainFunction(IType type) {
		IMethod[] ms = null;
		try {
			ms = type.getMethods();
		} catch (JavaModelException e) {
		}

		if (ms != null) {
			for (int i = 0; i < ms.length; i++) {
				if (ms[i].getElementName().equals("main"))
					return true;
			}
		}

		return false;
	}

	// public static boolean isPrimitive(OJClass type) {
	// if (type.equals(BOOLEAN))
	// return true;
	// if (type.equals(BYTE))
	// return true;
	// if (type.equals(CHAR))
	// return true;
	// if (type.equals(SHORT))
	// return true;
	// if (type.equals(INT))
	// return true;
	// if (type.equals(LONG))
	// return true;
	// if (type.equals(DOUBLE))
	// return true;
	// if (type.equals(VOID))
	// return true;
	// return false;
	// }

	/**
	 * Recognize inheritance relation of all classes located at CLASS_PATH
	 * directory. <br>
	 * <b>* CAUTION: </b> this function should be called before using
	 * <i>MutantsGenerator</i> or sub-classes of <i>MutantsGenerator</i>.
	 * 
	 * @see MutantsGenerator, AllMutantsGenerator, TraditionalMutantsGenerator,
	 *      ClassMutantsGenerator, ExceptionMutantsGenerator
	 */
	// // TODO swkim
	// // public static void recordInheritanceRelation() {
	// // String[] classes = null;
	// // classes = MutationSystem.getAllClassNames(classes,
	// // MutationSystem.CLASS_PATH);
	// // if (classes == null) {
	// // System.err
	// // .println("[ERROR] There is no classes to apply mutation.");
	// // System.err.println(" Pleas check the directory "
	// // + MutationSystem.CLASS_PATH);
	// // Runtime.getRuntime().exit(0);
	// // }
	// // classInfo = new InheritanceINFO[classes.length];
	// //
	// // boolean[] bad = new boolean[classes.length];
	// // for (int i = 0; i < classes.length; i++) {
	// // bad[i] = false;
	// // try {
	// // Class c = Class.forName(classes[i]);
	// // Class parent = c.getSuperclass();
	// // if ((parent == null)
	// // || (parent.getName().equals("java.lang.Object"))) {
	// // classInfo[i] = new InheritanceINFO(classes[i], "");
	// // } else {
	// // classInfo[i] = new InheritanceINFO(classes[i], parent
	// // .getName());
	// // }
	// // } catch (ClassNotFoundException e) {
	// // System.err.println(" Can't find class " + classes[i]);
	// // System.err.println(" Please check CLASSPATH ");
	// // bad[i] = true;
	// // classInfo[i] = new InheritanceINFO(classes[i], "");
	// // // Runtime.getRuntime().exit(0);
	// // } catch (Error er) {
	// // // Sometimes error occurred. However, I can't solve..
	// // // To muJava users: try do your best to solve it. ^^;
	// // System.out.println("[ERROR] for class " + classes[i] + " => "
	// // + er.getMessage());
	// // bad[i] = true;
	// // classInfo[i] = new InheritanceINFO(classes[i], "");
	// // }
	// // }
	// //
	// // for (int i = 0; i < classes.length; i++) {
	// // if (bad[i])
	// // continue;
	// //
	// // String parent_name = classInfo[i].getParentName();
	// //
	// // for (int j = 0; j < classes.length; j++) {
	// // if (bad[j])
	// // continue;
	// // if (classInfo[j].getClassName().equals(parent_name)) {
	// // classInfo[i].setParent(classInfo[j]);
	// // classInfo[j].addChild(classInfo[i]);
	// // break;
	// // }
	// // }
	// // }
	// // }
	// // /** Re-setting MuJava structure for give class name <br>
	// // * @param name of class (including package name) */
	// // public static void setJMutationPaths(String whole_class_name) {
	// // int temp_start = whole_class_name.lastIndexOf(".") + 1;
	// // if (temp_start < 0) {
	// // temp_start = 0;
	// // }
	// // int temp_end = whole_class_name.length();
	// // MutationSystem.CLASS_NAME = whole_class_name.substring(temp_start,
	// // temp_end);
	// // MutationSystem.DIR_NAME = whole_class_name;
	// // MutationSystem.ORIGINAL_PATH = MutationSystem.MUTANT_HOME + "/"
	// // + whole_class_name + "/" + MutationSystem.ORIGINAL_DIR_NAME;
	// // MutationSystem.CLASS_MUTANT_PATH = MutationSystem.MUTANT_HOME + "/"
	// // + whole_class_name + "/" + MutationSystem.CM_DIR_NAME;
	// // MutationSystem.TRADITIONAL_MUTANT_PATH = MutationSystem.MUTANT_HOME
	// // + "/" + whole_class_name + "/" + MutationSystem.TM_DIR_NAME;
	// // MutationSystem.EXCEPTION_MUTANT_PATH = MutationSystem.MUTANT_HOME +
	// "/"
	// // + whole_class_name + "/" + MutationSystem.EM_DIR_NAME;
	// //
	// // }
	// /**
	// * <b> Default mujava system structure setting function </b>
	// * <p>
	// * Recognize file structure for mutation system based on "mujava.config".
	// * </p>
	// * <p> ** CAUTION : this function or `setJMutationStructure(String
	// * home_path)' shoule be called before generating and running mutants.
	// */
	// // TODO swkim
	// // public static void setJMutationStructure() {
	// // try {
	// // File f = new File("mujava.config");
	// // FileReader r = new FileReader(f);
	// // BufferedReader reader = new BufferedReader(r);
	// // String str = reader.readLine();
	// // String home_path = str.substring("MuJava_HOME=".length(), str
	// // .length());
	// //
	// // SYSTEM_HOME = home_path;
	// // CLASS_PATH = home_path + "/classes";
	// // TESTSET_PATH = home_path + "/testset";
	// // } catch (FileNotFoundException e1) {
	// // System.err.println(" I can't find mujava.config file");
	// // e1.printStackTrace();
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// // }
}

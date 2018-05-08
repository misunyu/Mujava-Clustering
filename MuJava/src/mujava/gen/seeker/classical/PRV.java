package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.ClosedEnvironment;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.mop.OJField;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Variable;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Assignable Variable들을 모두 찾아 변경시킨다. 단, local Variable들 중에 initialize가 안된 경우에는
 * 제외해야 Compile time error가 발생하지 않는다.
 * 
 * @author swkim
 * 
 */
public class PRV extends AbstractChangePointSeeker {

	Map<Expression, List<String>> variableTable = new HashMap<Expression, List<String>>();

	public PRV(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "PRV");
	}

	public Map<Expression, List<String>> getVariableTable() {
		return variableTable;
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {

		Expression leftExp = p.getLeft();
		Expression rightExp = p.getRight();

		/**
		 * Object variable or field access can be mutated to another object
		 * variable or field access expression which
		 */
		if ((leftExp instanceof Variable || leftExp instanceof FieldAccess)
				&& (rightExp instanceof Variable || rightExp instanceof FieldAccess)) {

			OJClass leftType = getType(leftExp);

			if (!leftType.isPrimitive()) {
				TreeMap<String, OJClass> vars = getAccessibleVariables(leftType);
				List<String> tempList = new ArrayList<String>();
				for (String element : vars.keySet()) {
					// Consider all possible expressions to be replaced to
					// original right hand expression except right hand
					// expression itself and left hand expression.

					if (element.equals(leftExp.toString())) {
						continue;
					}

					if (element.equals(rightExp.toString())) {
						continue;
					}

					tempList.add(element);
				}

				if (!tempList.isEmpty()) {
					addChangePoint(new ChangePoint(p, currentMethod));
					Collections.sort(tempList);
					this.variableTable.put(p, tempList);
				}
			}
		}

		super.visit(p);
	}

	/**
	 * Never do not return null.
	 * 
	 * @return array of String
	 * @throws ParseTreeException
	 */
	private TreeMap<String, OJClass> getAccessibleVariables(OJClass leftClass)
			throws ParseTreeException {
		TreeMap<String, OJClass> var_set = new TreeMap<String, OJClass>();

		// For all visible variables
		Environment cEnv = this.getEnvironment();
		if (cEnv instanceof ClosedEnvironment) {
			List<String> list = ((ClosedEnvironment) cEnv)
					.getAccessibleVariables();

			for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				OJClass clz = cEnv.lookupBind(element);
				if (isAssignable(leftClass, clz) && isInitialized(element))
					var_set.put(element, clz);
			}
		}

		// For current instance variables
		OJClass clazz = getSelfType();
		OJField[] fs = clazz.getDeclaredFields();
		if (fs != null) {
			for (int k = 0; k < fs.length; k++) {
				OJClass clz = cEnv.lookupBind(fs[k].getName());
				if (isAssignable(leftClass, clz))
					var_set.put(fs[k].getName(), clz);
			}
		}

		return var_set;
	}

	private boolean isInitialized(String element) {
		Environment env = getEnvironment();
		if (env instanceof ClosedEnvironment) {
			ClosedEnvironment cEnv = (ClosedEnvironment) env;
			return cEnv.isInitialized(element);
		}

		return false;
	}

	/**
	 * Compare the class type of left and right, and then return the possibility
	 * to assign right to left. In case the both class are same and right class
	 * is sub class of left class, this method returns true. Otherwise, it
	 * returns false.
	 * 
	 * @param left
	 * @param right
	 * @return true if right class is able to assign to left class. false
	 *         otherwise.
	 */
	private boolean isAssignable(OJClass left, OJClass right) {
		if (left.getName().equals(right.getName()))
			return true;

		OJClass clazz = right.getSuperclass();
		while (clazz != null) {
			String name = clazz.getName();
			if (name.equals(left.getName()))
				return true;
			clazz = clazz.getSuperclass();
		}

		return false;
	}
}

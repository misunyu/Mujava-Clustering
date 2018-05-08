package mujava.plugin.editor.mutantreport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kaist.selab.util.Helper;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class TestCase {

	public static TestCase getInstance(Properties prop) {
		TestCase tc = new TestCase();
		tc.setProperties(prop);

		return tc;
	}

	private String loc;

	private List<MethodSignature> methods = new ArrayList<MethodSignature>();

	public TestCase(Method testMethod) {
		MethodSignature ms = new MethodSignature();
		ms.className = testMethod.getDeclaringClass().getCanonicalName();
		ms.methodName = testMethod.getName();
		Class<?>[] params = testMethod.getParameterTypes();
		List<String> list = new ArrayList<String>();
		for (Class<?> clz : params) {
			list.add(clz.getCanonicalName());
		}
		ms.params = (String[]) list.toArray(new String[list.size()]);
		ms.retType = testMethod.getReturnType().getCanonicalName();

		methods.add(ms);
	}

	public TestCase(IMethod testMethod)
	{
		MethodSignature ms = new MethodSignature();
		ms.className = testMethod.getDeclaringType().getFullyQualifiedName();
		ms.methodName = testMethod.getElementName();
		ms.params = testMethod.getParameterTypes();

		try
		{
			ms.retType = testMethod.getReturnType();
		} catch (JavaModelException e)
		{
			e.printStackTrace();

			// type을 지정할 수 없는 경우는 Void로 가정한다.
			ms.retType = "void";
		}

		methods.add(ms);
	}

	TestCase() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof TestCase))
			return false;

		TestCase objTestCase = (TestCase) obj;
		if (this.toString().equals(objTestCase.toString())
				&& this.loc.equals(objTestCase.loc))
			return true;

		return false;
	}

	public String getFileLoactions() {
		return this.loc;
	}

	public Properties getProperties() {
		Properties prop = new Properties();

		prop.setProperty("Loc", this.loc.toString());
		prop.setProperty("MS_SIZE", Integer.toString(methods.size()));

		StringBuffer sb = new StringBuffer("MS_");
		int startIndexOfI = sb.length();
		for (int i = 0; i < methods.size(); i++) {
			int length = sb.length();
			if (startIndexOfI < length)
				sb.delete(startIndexOfI, length);
			sb.append(i);

			int startIndexOfNames = sb.length();

			MethodSignature ms = (MethodSignature) methods.get(i);
			sb.append("_CLZ");
			prop.setProperty(sb.toString(), ms.className);
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_MEH");
			prop.setProperty(sb.toString(), ms.methodName);
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_RET");
			prop.setProperty(sb.toString(), ms.retType);
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_PARAM_SIZE");
			prop.setProperty(sb.toString(), Integer.toString(ms.params.length));

			sb.delete(startIndexOfNames, sb.length());
			sb.append("_PARAM_");
			int size = (ms.params != null) ? ms.params.length : 0;
			int startIndex = sb.length();
			for (int j = 0; j < size; j++) {
				int jLength = sb.length();
				if (startIndex < jLength)
					sb.delete(startIndex, jLength);
				sb.append(j);
				prop.setProperty(sb.toString(), (String) ms.params[j]);
			}
		}

		return prop;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public void setFileLocation(IFile file) {
		this.loc = file.getLocation().toOSString();
	}

	public void setProperties(Properties prop) {
		this.loc = prop.getProperty("Loc");
		this.methods.clear();

		int msize = Helper.getIntValueFromProperties(prop, "MS_SIZE");

		StringBuffer sb = new StringBuffer("MS_");
		int startIndexOfI = sb.length();
		for (int i = 0; i < msize; i++) {
			int length = sb.length();
			if (startIndexOfI < length) {
				sb.delete(startIndexOfI, length);
			}
			sb.append(i);

			MethodSignature ms = new MethodSignature();

			int startIndexOfNames = sb.length();
			sb.append("_CLZ");
			ms.className = Helper.getValueFromProperties(prop, sb.toString());
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_MEH");
			ms.methodName = Helper.getValueFromProperties(prop, sb.toString());
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_RET");
			ms.retType = Helper.getValueFromProperties(prop, sb.toString());
			sb.delete(startIndexOfNames, sb.length());
			sb.append("_PARAM_SIZE");
			int psize = Helper.getIntValueFromProperties(prop, sb.toString());

			sb.delete(startIndexOfNames, sb.length());
			sb.append("_PARAM_");

			int startIndexOfParam = sb.length();
			List<String> list = new ArrayList<String>();
			for (int j = 0; j < psize; j++) {
				int jLength = sb.length();
				if (startIndexOfParam < jLength)
					sb.delete(startIndexOfParam, jLength);
				sb.append(j);
				list.add(Helper.getValueFromProperties(prop, sb.toString()));
			}
			ms.params = (String[]) list.toArray(new String[list.size()]);

			methods.add(ms);
		}

	}

	public String toSimpleString() {

		StringBuffer buf = new StringBuffer();
		for (MethodSignature ms : methods) {
			buf.append(ms.className);
			buf.append(".");
			buf.append(ms.methodName);
			buf.append("(");
			buf.append(")");
			buf.append("->");
		}

		// 마지막의 ->를 제거하는 경우.
		if (buf.length() > 2)
			buf.delete(buf.length() - 2, buf.length());

		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (MethodSignature ms : methods) {
			buf.append(ms.retType);
			buf.append(" ");
			buf.append(ms.className);
			buf.append(".");
			buf.append(ms.methodName);
			buf.append("(");

			boolean flag = false;
			for (String param : ms.params) {
				buf.append(param);
				buf.append(", ");
				flag = true;
			}
			if (flag)
				buf.delete(buf.length() - 2, buf.length());

			buf.append(")");
			buf.append("->");
		}
		if (buf.length() > 2)
			buf.delete(buf.length() - 2, buf.length());

		return buf.toString();
	}
}

class MethodSignature {
	public String retType;
	String className;
	String methodName;
	String[] params;
}

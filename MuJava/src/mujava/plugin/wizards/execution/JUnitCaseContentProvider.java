package mujava.plugin.wizards.execution;

import java.util.ArrayList;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JUnitCaseContentProvider implements ITreeContentProvider
{
	protected static final Object[] NO_CHILDREN = new Object[0];

	private IType[] testClasses = null;

	Comparator<IFile> testClassComparator = new Comparator<IFile>()
	{
		@Override
		public int compare(IFile file0, IFile file1)
		{

			String arg0 = file0.getName();
			if (arg0.indexOf(".") != -1)
			{
				arg0 = arg0.substring(0, arg0.indexOf("."));
			}
			String arg1 = file1.getName();
			if (arg1.indexOf(".") != -1)
			{
				arg1 = arg1.substring(0, arg1.indexOf("."));
			}
			String header1 = getStringHeader(arg0);
			String header2 = getStringHeader(arg1);

			int diff = header1.compareTo(header2);
			if (diff == 0)
			{
				int index1 = arg0.indexOf(header1);
				int index2 = arg1.indexOf(header1);

				String integerStr1 = arg0.substring(index1 + header1.length());
				String integerStr2 = arg1.substring(index2 + header1.length());

				int value1 = 0;
				try
				{
					value1 = Integer.parseInt(integerStr1);
				}
				catch (NumberFormatException e)
				{
				}

				int value2 = 0;
				try
				{
					value2 = Integer.parseInt(integerStr2);
				}
				catch (NumberFormatException e)
				{
				}

				return value1 - value2;
			}

			return diff;
		}

		private String getStringHeader(String str)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < str.length(); i++)
			{
				char ch = str.charAt(i);
				if (Character.isDigit(ch))
				{
					break;
				}
				sb.append(ch);
			}

			return sb.toString();
		}
	};

	public JUnitCaseContentProvider()
	{
	}

	public void dispose()
	{
	}

	public Object[] getChildren(Object element)
	{
		if (!(element instanceof IType))
		{
			return NO_CHILDREN;
		}

		IType type = (IType) element;

		if (type instanceof IMethod)
		{
			return NO_CHILDREN;
		}

		try
		{
			if (!type.isClass())
			{
				return NO_CHILDREN;
			}

			ArrayList<IMethod> methods = new ArrayList<IMethod>();

			for (IMethod method : type.getMethods())
			{
				IAnnotation testAnnotation = method.getAnnotation("test");
				boolean higherJUnit = (testAnnotation != null && testAnnotation
						.exists());
				/*
				boolean lowerJUnit = method.getElementName().startsWith("test");
				boolean isPublic = Flags.isPublic(method.getFlags());
				if (!isPublic)
				{
					lowerJUnit = false;
				}

				if (lowerJUnit || higherJUnit)
				{
					methods.add(method);
				}*/
				
				boolean isPublic = Flags.isPublic(method.getFlags());
				if (isPublic || higherJUnit)
				{
					methods.add(method);
				}

			}

			if (methods.isEmpty())
			{
				return NO_CHILDREN;
			}

			// Collections.sort(files, testClassComparator);

			return (Object[]) methods.toArray(new Object[methods.size()]);

		}
		catch (JavaModelException e)
		{
			return NO_CHILDREN;
		}

	}

	public Object[] getElements(Object parent)
	{
		if (testClasses == null)
		{
			return NO_CHILDREN;
		}

		return testClasses;
	}

	public Object getParent(Object element)
	{
		if (element == null)
		{
			return null;
		}

		if (element instanceof IMethod)
		{
			IMethod method = (IMethod) element;
			return method.getParent();
		}

		return null;
	}

	public boolean hasChildren(Object element)
	{
		if (element == null)
		{
			return false;
		}

		if (!(element instanceof IType))
		{
			return false;
		}

		IType type = (IType) element;

		if (type.isBinary())
		{
			return false;
		}

		try
		{
			for (IMethod method : type.getMethods())
			{
				IAnnotation testAnnotation = method.getAnnotation("test");
				boolean higherJUnit = (testAnnotation != null && testAnnotation
						.exists());
				/*boolean lowerJUnit = method.getElementName().startsWith("test");

				if (lowerJUnit || higherJUnit)
				{
					return true;
				}*/
				if (higherJUnit)
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	// IType[] TestCases
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput instanceof IType[])
		{
			testClasses = (IType[]) newInput;
		}
	}

}

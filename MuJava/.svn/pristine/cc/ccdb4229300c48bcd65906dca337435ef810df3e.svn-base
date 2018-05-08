package mujava.plugin.wizards.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TestCaseContentProvider implements ITreeContentProvider
{
	protected static final Object[] NO_CHILDREN = new Object[0];

	private Object root;

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

	public TestCaseContentProvider()
	{
	}

	public void dispose()
	{
	}

	public Object[] getChildren(Object element)
	{
		try
		{
			if (element instanceof IFolder)
			{

				List<IFile> files = new ArrayList<IFile>();
				List<IFolder> folders = new ArrayList<IFolder>();

				IFolder folder = (IFolder) element;
				IResource[] res = folder.members();
				for (IResource resource : res)
				{
					if (resource instanceof IFile)
					{
						if ("class".equalsIgnoreCase(resource
								.getFileExtension()))
						{
							files.add((IFile) resource);
						}
					}
					else if (resource instanceof IFolder)
					{
						IFolder childFolder = (IFolder) resource;

						// Directory 이름 중 .으로 시작하는 경우, 무시한다. (SVN or CVS folder)
						if (!childFolder.getName().startsWith("."))
						{
							folders.add((IFolder) resource);
						}
					}
				}

				Collections.sort(files, testClassComparator);
				List<Object> children = new ArrayList<Object>();
				children.addAll(folders);
				children.addAll(files);

				return (Object[]) children.toArray(new Object[children.size()]);
			}
		}
		catch (JavaModelException e)
		{
			return NO_CHILDREN;
		}
		catch (CoreException e)
		{
			return NO_CHILDREN;
		}
		return NO_CHILDREN;
	}

	public Object[] getElements(Object parent)
	{

		return getChildren(root);
	}

	public Object getParent(Object element)
	{
		if (element == null)
			return null;

		assert (element instanceof IResource);

		IResource res = (IResource) element;
		IContainer con = res.getParent();
		IProject prj = res.getProject();

		if (prj.equals(con))
			return null;

		return con;
	}

	public boolean hasChildren(Object element)
	{
		Object[] elements = getChildren(element);

		if (elements != null && elements.length != 0)
			return true;

		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		root = newInput;
	}

}

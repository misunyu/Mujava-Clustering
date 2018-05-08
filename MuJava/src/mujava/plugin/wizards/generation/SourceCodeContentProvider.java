package mujava.plugin.wizards.generation;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

public class SourceCodeContentProvider extends
		StandardJavaElementContentProvider {

	@Override
	public Object[] getChildren(Object element) {
		Object[] elements = super.getChildren(element);
		ArrayList<IJavaElement> results = new ArrayList<IJavaElement>();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof IJavaElement)
				results.add((IJavaElement) elements[i]);
		}
		try {
			results = getSourceCode(results.toArray(new IJavaElement[results
					.size()]));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return results.toArray();
	}

	@Override
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	ArrayList<IJavaElement> getSourceCode(IJavaElement[] items)
			throws JavaModelException {
		ArrayList<IJavaElement> list = new ArrayList<IJavaElement>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getElementType() == IJavaElement.COMPILATION_UNIT)
				list.add(items[i]);
			else if (items[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {
				IPackageFragmentRoot root = (IPackageFragmentRoot) items[i];
				list.addAll(getSourceCode(root.getChildren()));
			} else if (items[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
				IPackageFragment frag = (IPackageFragment) items[i];
				if (getSourceCode(frag.getChildren()).size() > 0)
					list.add(items[i]);
			}
		}
		return list;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] elements = super.getChildren(element);
		ArrayList<IJavaElement> results = new ArrayList<IJavaElement>();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof IJavaElement)
				results.add((IJavaElement) elements[i]);
		}
		try {
			results = getSourceCode(results.toArray(new IJavaElement[results
					.size()]));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		if (results != null && results.size() != 0)
			return true;

		return false;
	}

}

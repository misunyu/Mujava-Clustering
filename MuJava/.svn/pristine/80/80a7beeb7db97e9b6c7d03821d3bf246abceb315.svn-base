package mujava.plugin.editor.mujavaproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mujava.MuJavaMutantInfo;
import mujava.MutantTable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MutantListContentProvider implements ITreeContentProvider {
	HashMap<IJavaElement, List<String>> maps = new HashMap<IJavaElement, List<String>>();

	public void dispose() {
		maps.clear();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		maps.clear();

		if (newInput == null)
			return;

		assert (newInput instanceof MutantTable);
		MutantTable mutantTable = (MutantTable) newInput;

		// List<String> files = new ArrayList<String>();
		for (String mutantID : mutantTable.getAllMutantID()) {
			MuJavaMutantInfo info = mutantTable.getMutantInfo(mutantID);

			if (info == null)
				continue;

			String fileName = info.getTargetFile();

			// 주어진 filename으로부터 IJavaElement를 구한다.
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource res = root.findMember(new Path(fileName));
			IJavaElement element = JavaCore.create(res, JavaCore.create(res
					.getProject()));

			List<String> mutantIDs = maps.get(element);
			if (mutantIDs == null)
				mutantIDs = new ArrayList<String>();

			if (!mutantIDs.contains(mutantID)) {
				mutantIDs.add(mutantID);
			}
			maps.put(element, mutantIDs);
		}
	}

	public Object[] getElements(Object inputElement) {
		Set<IJavaElement> keys = maps.keySet();
		return keys.toArray(new Object[keys.size()]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) parentElement;
			// String name = element.getResource().getFullPath().toString();
			List<String> IDs = maps.get(element);
			if (IDs != null)
				return IDs.toArray(new Object[IDs.size()]);
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof MuJavaMutantInfo) {
			MuJavaMutantInfo info = (MuJavaMutantInfo) element;
			for (IJavaElement source : maps.keySet()) {
				if (source.getResource().getFullPath().toString()
						.equalsIgnoreCase(info.getTargetFile())) {
					return source;
				}
			}
		}

		// FileName의 부모는 Root이므로 Null이 된다.
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		if (children != null && children.length > 0)
			return true;

		return false;
	}
}

package mujava.plugin.editor.mujavaproject;

import mujava.MuJavaMutantInfo;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class MutantSorter extends ViewerSorter {
	public MutantSorter()
	{
		super();
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(e1 instanceof MuJavaMutantInfo && e2 instanceof MuJavaMutantInfo) {
			MuJavaMutantInfo p = (MuJavaMutantInfo)e1;
			MuJavaMutantInfo q = (MuJavaMutantInfo)e2;
			return p.compareTo(q);
		}
		return super.compare(viewer, e1, e2);
	}
}


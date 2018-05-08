package mujava.plugin.actions;

import javax.annotation.Resource;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import mujava.plugin.wizards.generation.MutantGenerationWizard;


public class GenerateMutantsActionDelegate implements
		IWorkbenchWindowActionDelegate {

	private ISelection selection;

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		MutantGenerationWizard wizard = new MutantGenerationWizard(selection);
		WizardDialog dialog = new WizardDialog(window.getShell(),wizard);
		dialog.open();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}

package mujava.plugin.actions;

import mujava.plugin.wizards.execution.TestExecutionWizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class RunMutantsActionDelegate implements IWorkbenchWindowActionDelegate {

	private ISelection selection;
	
	@Override
	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		TestExecutionWizard wizard = new TestExecutionWizard(selection);
		
		WizardDialog dialog = new WizardDialog(window.getShell(),wizard);
		dialog.open();
		
	}

	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
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

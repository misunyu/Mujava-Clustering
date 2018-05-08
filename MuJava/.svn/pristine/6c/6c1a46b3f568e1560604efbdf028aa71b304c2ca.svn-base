package mujava.plugin.editor.mujavaproject;

import java.io.IOException;

import mujava.CodeSet;
import mujava.GeneratedCodeTable;
import mujava.MuJavaMutantInfo;
import mujava.MuJavaProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class MutantDescriptionComposite extends Composite {
	Text textarea;
	private MuJavaMutantInfo mInfo;

	public MutantDescriptionComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		textarea = new Text(this, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		textarea.setLayoutData(gd);
		Button bt = new Button(parent, SWT.PUSH);
		bt.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (mInfo == null)
					return;

				// show new window
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();

						IProject eclipseProject = mInfo.getResource()
								.getProject();
						IWorkspaceRoot root = eclipseProject.getWorkspace()
								.getRoot();
						String muPrjFileName = mInfo.getMuJavaProject();

						MuJavaProject muProject = MuJavaProject
								.getMuJavaProject(muPrjFileName, null);

						String cID = mInfo.getCodeID();
						GeneratedCodeTable cTable = null;
						try {
							cTable = GeneratedCodeTable.getGeneratedCodeTable(
									muProject, null);
							IPath codePath = cTable.getCodeSetDirectory();
							if (mInfo != null
									&& !mInfo.getMutationOperatorName()
											.isEmpty()) {

								codePath = codePath.append(mInfo
										.getMutationOperatorName());
							}

							CodeSet code = CodeSet.getCodeSet(codePath, cID);
							String target = code.getTargetFile();
							IPath path = new Path(target);
							IFile file = root.getFile(path);
							IDE.openEditor(page, file, true);
							return;
						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				});
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		bt.setText("View Sourse");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.RIGHT;
		bt.setLayoutData(gd);
	}

	public void setMutantInfo(MuJavaMutantInfo info) {
		this.mInfo = info;

		StringBuffer buf = new StringBuffer();
		if (info != null) {
			buf.append("Change Location : " + info.getChangeLocation() + "\n");
			buf.append("Change Log : " + info.getChangeLog() + "\n");
			buf.append("FileName : " + info.getFileName() + "\n");
			buf.append("MuJavaProject : " + info.getMuJavaProjectName() + "\n");
			buf.append("CodeSetID : " + info.getCodeID() + "\n");
			if (info.getSubCodeID() != null) {
				buf.append("SubCodeSetID : " + info.getSubCodeID() + "\n");
			}
			buf.append("FirstSubTypeOperator : "
					+ info.getFirstSubTypeOperator() + "\n");
			buf.append("MaximumSubType : " + info.getMaximumSubType() + "\n");
			buf.append("MutantID : " + info.getMutantID() + "\n");
			buf.append("TargetFile : " + info.getTargetFile() + "\n");
		}

		textarea.setText(buf.toString());
	}

}

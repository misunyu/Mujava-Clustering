package mujava.plugin.editor.mujavaproject;

import java.io.IOException;

import mujava.MuJavaProject;
import mujava.MutantTable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

public class MuJavaProjectFileEditor extends MultiPageEditorPart implements
		IResourceChangeListener {
	MuJavaProject mujavaProject;

	ProjectInformationComposite prjInfoPage;

	TraditionalMutantComposite traditionalMutantPage;

	ClassMutantComposite classMutantPage;

	MutantCostReportComposite resultPage;

	private TextEditor projectFileEditor;

	private MutantTable table;

	private MutantCostReportTable costTable;

	/**
	 * Creates a multi-page editor example.
	 */
	public MuJavaProjectFileEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	void createPage0() {

		prjInfoPage = new ProjectInformationComposite(getContainer(), SWT.NONE);

		int index = addPage(prjInfoPage);
		setPageText(index, "Project Information");

		if (mujavaProject != null) {
			prjInfoPage.setProjectInformation(mujavaProject);
		}
	}

	void createPage1() {

		traditionalMutantPage = new TraditionalMutantComposite(getContainer(),
				SWT.NONE);

		// if (table != null)
		// traditionalMutantPage.update(table);
		int index = addPage(traditionalMutantPage);
		setPageText(index, "Traditional Mutants");
	}

	void createPage2() {

		classMutantPage = new ClassMutantComposite(getContainer(), SWT.NONE);

		// if (table != null)
		// classMutantPage.update(table);
		int index = addPage(classMutantPage);
		setPageText(index, "Class Mutants");
	}

	private void createPage4() {

		try {
			projectFileEditor = new TextEditor();

			int index = addPage(projectFileEditor, getEditorInput());
			setPageText(index, projectFileEditor.getTitle());

		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}

	}

	private void createPage3() {

		resultPage = new MutantCostReportComposite(getContainer(), SWT.NONE);

		int index = addPage(resultPage);
		setPageText(index, "Cost Result");
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		createPage3();
		createPage4();

		updatePages();

		this.setPartName(mujavaProject.getName());
	}

	private void updatePages() {
		prjInfoPage.setProjectInformation(mujavaProject);

		traditionalMutantPage.update(table);

		classMutantPage.update(table);

		resultPage.updateTable(costTable);
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		// getEditor(0).doSave(monitor);
		getEditor(3).doSave(monitor);
		IEditorInput input = getEditor(3).getEditorInput();
		if (input instanceof IFileEditorInput)
			refreshProject((IFileEditorInput) input);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(3);
		editor.doSaveAs();
		setPageText(3, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);

		IFileEditorInput input = (IFileEditorInput) editorInput;
		refreshProject(input);
	}

	private void refreshProject(IFileEditorInput editorInput) {

		IFile file = editorInput.getFile();
		mujavaProject = MuJavaProject.getMuJavaProject(file, null);

		if (prjInfoPage != null) {
			prjInfoPage.setProjectInformation(mujavaProject);
		}

		try {
			table = MutantTable.getMutantTable(mujavaProject, null);
			costTable = MutantCostReportTable.getGenerationResultTable(
					mujavaProject, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) projectFileEditor
								.getEditorInput()).getFile().getProject()
								.equals(event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(projectFileEditor
											.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			IResourceDelta delta = event.getDelta();

			IResourceDelta resultDelta = delta.findMember(mujavaProject
					.getResource().getFullPath());
			if (resultDelta != null) {
				int kind = resultDelta.getKind();
				if ((kind & IResourceDelta.CHANGED) > 0) {
					mujavaProject = MuJavaProject.getMuJavaProject(resultDelta
							.getResource(), null);
					prjInfoPage.setProjectInformation(mujavaProject);
				}
			}

			if (table != null) {
				resultDelta = delta.findMember(table.getResource()
						.getFullPath());
				if (resultDelta != null) {
					int kind = resultDelta.getKind();
					if ((kind & IResourceDelta.REMOVED) > 0) {
						if (mujavaProject != null) {
							MutantTable.deleteCache(mujavaProject);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MutantTable.deleteCache(mujavaProject);
									try {
										table = MutantTable.getMutantTable(
												mujavaProject, null);
										traditionalMutantPage.update(table);
										classMutantPage.update(table);
									} catch (IOException e) {
										e.printStackTrace();
									} catch (CoreException e) {
										e.printStackTrace();
									}
								}
							});
						}
					} else if ((kind & IResourceDelta.CHANGED) > 0) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								try {
									table = MutantTable.getMutantTable(
											mujavaProject, null);
									traditionalMutantPage.update(table);
									classMutantPage.update(table);
								} catch (IOException e) {
									e.printStackTrace();
								} catch (CoreException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			}

			if (costTable != null) {
				resultDelta = delta.findMember(costTable.getResource()
						.getFullPath());
				if (resultDelta != null) {
					int kind = resultDelta.getKind();
					if ((kind & IResourceDelta.REMOVED) > 0) {
						if (mujavaProject != null) {
							MutantCostReportTable.deleteCache(mujavaProject);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									try {
										costTable = MutantCostReportTable
												.getGenerationResultTable(
														mujavaProject, null);
										resultPage.updateTable(costTable);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
						}
					} else if ((kind & IResourceDelta.CHANGED) > 0) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								try {
									costTable = MutantCostReportTable
											.getGenerationResultTable(
													mujavaProject, null);
									resultPage.updateTable(costTable);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			}

			resultDelta = delta.findMember(mujavaProject.getResource()
					.getFullPath());
			if (resultDelta != null) {
				int kind = resultDelta.getKind();
				if ((kind & IResourceDelta.REMOVED) > 0) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage();

							IEditorReference[] editorReferences = page
									.getEditorReferences();
							for (int i = 0; i < editorReferences.length; i++) {
								IEditorReference reference = editorReferences[i];
								IWorkbenchPart part = reference.getPart(false);
								if (part instanceof MuJavaProjectFileEditor) {
									MuJavaProjectFileEditor editor = (MuJavaProjectFileEditor) part;
									if (editor.mujavaProject
											.equals(mujavaProject))
										page.closeEditor((IEditorPart) part,
												false);
									mujavaProject = null;
									table.delete();
									costTable.delete();
									break;
								}

							}
						}
					});
				}
			}
		}
	}
}
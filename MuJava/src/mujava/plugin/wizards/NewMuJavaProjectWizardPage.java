package mujava.plugin.wizards;

import mujava.TestCaseType;
import mujava.gen.GenerationType;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class NewMuJavaProjectWizardPage extends WizardPage {
	Text containerName;
	List projectList;
	Text muProjectName;
	String muProjectFileName;
	Text mutantDirText;
	Text testDirText;
	Text mainClassText;
	Text resultDirText;

	private Combo genTypeCombo;
	private Combo testCaseTypeCombo;

	private GenerationType genType;
	private TestCaseType testType;

	private static final int INDEX_GEN_NORMAL = 1;
	private static final int INDEX_GEN_EXP_REACH = 3;
	private static final int INDEX_GEN_MSG = 2;
	private static final int INDEX_GEN_NUJAVA = 0;
	private static final int INDEX_GEN_STATE = 4;

	private static final int INDEX_MUJAVA = 0;
	private static final int INDEX_JUNIT4 = 1;
	private static final int INDEX_JUNIT3 = 2;
	/**
	 * Constructor for NewMuJavaProjectWizardPage.
	 * 
	 * @param pageName
	 */
	public NewMuJavaProjectWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Create MuJava Project File");
		setDescription("This wizard creates a new file with *.mjp extension that can be opened by a mujava project editor.");
	}

	public GenerationType getGenerationWay() {
		return genType;
	}

	public TestCaseType getTestCaseType() {
		return testType;
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {

		int count = 0;

		if (containerName != null) {
			String name = containerName.getText();
			if (name == null || name.length() == 0) {
				updateStatus("At first, choose one project");
				return;
			}

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IContainer container = (IContainer) root.findMember(name);
			if (container == null) {
				updateStatus("Selected project is wrong.");
				return;
			}
			count++;
		}

		if (muProjectName != null) {
			String prjName = muProjectName.getText();
			if (prjName.length() == 0) {
				updateStatus("Project name must be specified");
				return;
			}
			if (isExistedProject(prjName)) {
				updateStatus("Project name must be identified");
				return;
			}
			count++;
		}

		 if (muProjectFileName != null) {
			
			if (muProjectFileName.length() == 0) {
				updateStatus("File name must be specified");
				return;
			}
			if (muProjectFileName.replace('\\', '/').indexOf('/', 1) > 0) {
				updateStatus("File name must be valid");
				return;
			}
			int dotLoc = muProjectFileName.lastIndexOf('.');
			if (dotLoc != -1) {
				String ext = muProjectFileName.substring(dotLoc + 1);
				if (ext.equalsIgnoreCase("mjp") == false) {
					updateStatus("File extension must be \"mjp\"");
					return;
				}
			}
			count++;
		}

		if (mutantDirText != null) {
			String mutDir = mutantDirText.getText();
			if (mutDir == null || mutDir.length() == 0) {
				updateStatus("Mutant Directory must be specified");
				return;
			}
			count++;
		}
		if (testDirText != null) {
			String testDir = testDirText.getText();
			if (testDir == null || testDir.length() == 0) {
				updateStatus("Test Directory must be specified");
				return;
			}
			count++;
		}

		if (genType == GenerationType.NONE) {
			updateStatus("Generation type must be specified.");
			return;
		}

		count++;

		if (testType == TestCaseType.NONE) {
			updateStatus("TestCase type must be specified.");
			return;
		}

		count++;

		if (count == 7)
			updateStatus(null);
		else
			updateStatus("Something wrong!!");
	}

	private void initialize() {

		// Í∏∞Î≥∏ ?¥Îçî ?¥Î¶Ñ???§Ï†ï
		mutantDirText.setText("Mutants");
		testDirText.setText("Tests");
		resultDirText.setText("Results");

	}

	private boolean isExistedProject(String prjName) {

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(prjName);

		if (resource == null || !resource.exists()
				|| !(resource instanceof IContainer)) {
			return false;
		}

		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(prjName + ".mjp"));
		try {
			if (file.exists())
				return true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	protected String getContainerName() {
		return this.containerName.getText();
	}

	protected String getMuJavaProjectFileName() {
		return muProjectFileName;
	}

	protected String getMuJavaProjectName() {
		return muProjectName.getText();
	}

	protected String getMutantDirText() {
		return mutantDirText.getText();
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		gridData3.horizontalSpan = 3;

		Label label = new Label(container, SWT.NONE);
		label.setText("Choose an existing Eclipse Java project");
		containerName = new Text(container, SWT.BORDER | SWT.SINGLE);
		containerName.setLayoutData(gridData2);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		String[] prjNames = new String[projects.length];

		for (int i = 0; i < projects.length; i++) {
			prjNames[i] = projects[i].getName();
		}

		projectList = new List(container, SWT.BORDER | SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL);
		projectList.setItems(prjNames);
		projectList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// d-clicked
			}

			public void widgetSelected(SelectionEvent e) {
				String[] result = projectList.getSelection();
				containerName.setText(result[0]);
				dialogChanged();
			}
		});
		projectList.setLayoutData(gridData3);

		label = new Label(container, SWT.NONE);
		label.setText("MuJava Project Name : ");
		muProjectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		muProjectName.setLayoutData(gridData2);
		muProjectName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				muProjectFileName = muProjectName.getText() + ".mjp";
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("Generated Directory : ");
		mutantDirText = new Text(container, SWT.BORDER | SWT.SINGLE);
		mutantDirText.setLayoutData(gridData2);

		label = new Label(container, SWT.NULL);
		label.setText("Directory to store its tests : ");
		testDirText = new Text(container, SWT.BORDER | SWT.SINGLE);
		testDirText.setLayoutData(gridData2);

		label = new Label(container, SWT.NULL);
		label.setText("Directory to store test results : ");
		resultDirText = new Text(container, SWT.BORDER | SWT.SINGLE);
		resultDirText.setLayoutData(gridData2);

		label = new Label(container, SWT.NONE);
		label.setText("Select Generation Type : ");
		label.setLayoutData(gridData1);

		genTypeCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		genTypeCombo.add(GenerationType.WS.getLabel(), INDEX_GEN_NUJAVA);
		genTypeCombo.add(GenerationType.SC.getLabel(), INDEX_GEN_NORMAL);
		genTypeCombo.add(GenerationType.MSG.getLabel(), INDEX_GEN_MSG);
		genTypeCombo.add(GenerationType.REACH.getLabel(), INDEX_GEN_EXP_REACH);
		genTypeCombo.add(GenerationType.SCC.getLabel(), INDEX_GEN_STATE);
		
		genTypeCombo.setLayoutData(gridData2);
		genTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();

				int index = c.getSelectionIndex();
				switch (index) {
				case INDEX_GEN_EXP_REACH:
					genType = GenerationType.REACH;
					break;
				case INDEX_GEN_MSG:
					genType = GenerationType.MSG;
					break;
				case INDEX_GEN_NORMAL:
					genType = GenerationType.SC;
					break;
				case INDEX_GEN_NUJAVA:
					genType = GenerationType.WS;
					break;
					
				case INDEX_GEN_STATE:
					genType = GenerationType.SCC;
					break;
				}

				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NONE);
		label.setText("Select Test Case Type : ");
		label.setLayoutData(gridData1);

		testCaseTypeCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		testCaseTypeCombo.add(TestCaseType.MJ.getLabel(), INDEX_MUJAVA);
		testCaseTypeCombo.add(TestCaseType.JU4.getLabel(), INDEX_JUNIT4);
		testCaseTypeCombo.add(TestCaseType.JU3.getLabel(), INDEX_JUNIT3);
		testCaseTypeCombo.setLayoutData(gridData2);
		testCaseTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Combo c = (Combo) e.getSource();

				int index = c.getSelectionIndex();
				switch (index) {
				case INDEX_MUJAVA:
					testType = TestCaseType.MJ;
					break;
				case INDEX_JUNIT4:
					testType = TestCaseType.JU4;
					break;
				case INDEX_JUNIT3:
					testType = TestCaseType.JU3;
					break;
				}

				dialogChanged();
			}
		});

		initialize();
		dialogChanged();
		setControl(container);
	}

	public String getResultDirText() {
		return resultDirText.getText();
	}

	public String getTestDirText() {
		return testDirText.getText();
	}

	public String isValid(Object destination) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IContainer container = (IContainer) root
				.findMember((IPath) destination);
		return (container == null) ? "You selected wrong project name" : "";
	}
}
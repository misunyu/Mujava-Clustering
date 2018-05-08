package mujava.plugin.wizards.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.GeneratedCodeTable;
import mujava.MuJavaProject;
import mujava.MutantManager;
import mujava.MutantOperator;
import mujava.MutantTable;
import mujava.MutationSystem;
import mujava.gen.GenerationType;
import mujava.gen.MuJavaMutantCompiler;
import mujava.gen.MuJavaMutantFileGenerator;
import mujava.gen.MuJavaMutantPackager;
import mujava.gen.MuJavaMutationEngine;
import mujava.gen.MutantEngine;
import mujava.gen.PerformanceElement;
import mujava.inf.IMutationSystem;
import mujava.plugin.editor.mujavaproject.MutantCostReport;
import mujava.plugin.editor.mujavaproject.MutantCostReportTable;
import mujava.plugin.wizards.MutantOperatorPage;
import mujava.plugin.wizards.ValidMuJavaProjectPage;
import openjava.mop.FileEnvironment;
import openjava.mop.OJSystem;
import openjava.ptree.CompilationUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MutantGenerationWizard extends Wizard implements INewWizard {

	private ValidMuJavaProjectPage firstPage;

	private SourcePage page1;

	private MutantOperatorPage operatorPage;

	private ISelection selection;

	private IResource selectedProjectResource = null;

	/**
	 * Constructor for GenerateMutantsForMuJavaWizard.
	 */
	public MutantGenerationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public MutantGenerationWizard(ISelection selection) {
		this();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object item = sSelection.getFirstElement();
			if (item instanceof IResource) {
				selectedProjectResource = (IResource) item;
			}
		}
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		if (selectedProjectResource == null) {
			firstPage = new ValidMuJavaProjectPage(selection, true);
		}
		page1 = new SourcePage(null);
		operatorPage = new MutantOperatorPage(null);

		if (firstPage != null) {
			addPage(firstPage);
		}
		addPage(page1);
		addPage(operatorPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);

		if (selectedProjectResource != null) {
			page1.setProject(selectedProjectResource.getProject());
		}
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		final IWizardPage nextPage = super.getNextPage(page);

		// popup menu로 시작하지 않는 경우
		if (page instanceof ValidMuJavaProjectPage) {

			selectedProjectResource = firstPage.getSelectedMuJavaProject();

			if (nextPage instanceof SourcePage) {

				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						try {
							settingSourcePage(nextPage, page, monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						} finally {
							monitor.done();
						}
					}
				};

				try {
					getContainer().run(false, false, op);
				} catch (InterruptedException e) {
					return null;
				} catch (InvocationTargetException e) {
					Throwable realException = e.getTargetException();
					MessageDialog.openError(getShell(), "Error",
							realException.getMessage());
					return null;
				}
			}

		} else if (page instanceof SourcePage) {

			if (nextPage instanceof MutantOperatorPage) {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException {
						settingMutantOperatorPage(selectedProjectResource,
								monitor);
					}
				};
				try {
					getContainer().run(false, false, op);
				} catch (InterruptedException e) {
					return null;
				} catch (InvocationTargetException e) {
					Throwable realException = e.getTargetException();
					MessageDialog.openError(getShell(), "Error",
							realException.getMessage());
					return null;
				}
			}
		}

		return super.getNextPage(page);
	}

	protected void settingMutantOperatorPage(IResource res,
			IProgressMonitor monitor) {

		if (selectedProjectResource == null) {
			selectedProjectResource = firstPage.getSelectedMuJavaProject();
		}
		MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				selectedProjectResource, null);
		operatorPage.setGenerationWay(muProject.getGenerationWay());
		monitor.worked(1);
	}

	/*
	 * Set the information to show in case it takes too long time to save the
	 * first page
	 */
	void settingSourcePage(IWizardPage page, IWizardPage oriPage,
			IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("setting next page...", 2);
		IProject project = ((ValidMuJavaProjectPage) oriPage)
				.getSelectedProject();
		monitor.worked(1);
		monitor.setTaskName("setting project information...");
		((SourcePage) page).setProject(project);
		monitor.worked(1);
	}

	/*
	 * check whether we obtain needed information from this wizard or not
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		if (operatorPage != null) {
			if (operatorPage.isPageComplete())
				return true;
		}
		return false;
	}

	/**
	 * 주어진 NodeList중에서 name에 해당하는 Node를 추출한다. 해당 Node가 여러개일수 있기때문에 List<Node>로
	 * 반환받는다. 주어진 이름에 해당하는 Node가 하나도 없는 경우에는 빈 List가 반환되고, 하나 이상의 경우에도 List에 모두
	 * 삽입된 후 반환된다. getNodeByName()와 달리 아무런 Exception을 발생하지 않는다.
	 * 
	 * @param childList
	 *            a XML NodeList having all chile nodes
	 * @param name
	 *            a string
	 * @return a list of Node
	 */

	private static List<Node> getNodeListByName(NodeList childList, String name) {
		List<Node> nodeList = new ArrayList<Node>();

		for (int i = 0; i < childList.getLength(); i++) {
			Node subNode = childList.item(i);
			if (name.equals(subNode.getNodeName()))
				nodeList.add(subNode);
		}

		return nodeList;
	}

	/**
	 * 주어진 NodeList중에서 name에 해당하는 Node를 추출한다. 주어진 이름에 해당하는 Node가 하나 이상인 경우에는
	 * InvalidDocumentException이 발생되며, 이름에 해당하는 Node가 하나도 없는경우에는 exception의 발생
	 * 없이 Null 값을 반환한다.
	 * 
	 * @param childList
	 *            a XML NodeList having all chile nodes
	 * @param name
	 *            a string
	 * 
	 * @return null if no Node whose name equals the given name, Node object
	 *         otherwise
	 */
	private static Node getNodeByName(NodeList childList, String name) {
		Node tempNode = null;
		for (int i = 0; i < childList.getLength(); i++) {
			Node subNode = childList.item(i);
			if (name.equals(subNode.getNodeName()))
				// There are more elements which have the same as the given name
				if (tempNode != null)
					; // do nothing, so the first element will be inputed
				else
					tempNode = subNode;
		}

		return tempNode;
	}

	Hashtable<String, ClassState> readXML(Document doc) {
		Hashtable<String, ClassState> classStates = new Hashtable<String, ClassState>();

		Element projectElement = doc.getDocumentElement();
		if ("states".equals(projectElement.getNodeName())) {
			NodeList childList = projectElement.getChildNodes();

			try {
				List<Node> classList = getNodeListByName(childList, "class");
				for (Node classNode : classList) {
					ClassState state = getClassState(classNode);
					classStates.put(state.getClassName(), state);
				}
			} catch (NullPointerException e) {
			}
		}

		return classStates;
	}

	ClassState getClassState(Node classNode) {
		ClassState cState = new ClassState();

		// Attribute "name"을 입력 받는다.
		NamedNodeMap maps = classNode.getAttributes();
		Node attribute = maps.getNamedItem("name");
		if (attribute == null)
			throw new NullPointerException();
		String str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();

		// Class의 이름을 저장.
		cState.setFullName(attribute.getNodeValue());

		NodeList childList = classNode.getChildNodes();

		Node consturctorNode = getNodeByName(childList, "constructor");
		if (consturctorNode != null) {
			NodeList constructorChildList = consturctorNode.getChildNodes();
			List<Node> variableList = getNodeListByName(constructorChildList,
					"variable");
			for (Node variableNode : variableList) {
				VariableState vState = getVariableState(variableNode);
				cState.addConstructorVariableState(vState);
			}
		}

		List<Node> methodList = getNodeListByName(childList, "method");
		for (Node methodNode : methodList) {
			MethodState state = getMethodState(methodNode);
			cState.addMethodState(state);
		}
		return cState;
	}

	private MethodState getMethodState(Node methodNode) {
		MethodState mState = new MethodState();

		// Attribute "name"을 입력 받는다.
		NamedNodeMap maps = methodNode.getAttributes();
		Node attribute = maps.getNamedItem("name");
		if (attribute == null)
			throw new NullPointerException();
		String str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		mState.setMethodName(str);

		// return type
		attribute = maps.getNamedItem("type");
		str = (attribute == null) ? "void" : attribute.getNodeValue();
		if (str.isEmpty())
			str = "void";
		mState.setReturnType(str);

		// param 갯수
		attribute = maps.getNamedItem("param");
		str = (attribute == null) ? "0" : attribute.getNodeValue();
		if (str.isEmpty())
			str = "0";
		int paramCount = Integer.parseInt(str);

		// param정보
		NodeList childList = methodNode.getChildNodes();
		List<Node> paramList = getNodeListByName(childList, "param");
		if (paramList.size() != paramCount)
			throw new NullPointerException();

		for (Node paramNode : paramList) {
			ParamState state = getParamState(paramNode);
			mState.addParamState(state);
		}

		// variable정보
		List<Node> variableList = getNodeListByName(childList, "variable");
		for (Node variableNode : variableList) {
			VariableState state = getVariableState(variableNode);
			mState.addVariableState(state);
		}

		return mState;
	}

	private VariableState getVariableState(Node variableNode) {
		VariableState vState = new VariableState();

		// Attribute "name"을 입력 받는다.
		NamedNodeMap maps = variableNode.getAttributes();
		Node attribute = maps.getNamedItem("name");
		if (attribute == null)
			throw new NullPointerException();
		String str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		vState.setVariableName(str);

		// Attribute "type"을 입력 받는다.
		// maps = variableNode.getAttributes();
		attribute = maps.getNamedItem("type");
		if (attribute == null)
			throw new NullPointerException();
		str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		vState.setType(str);

		// Attribute "isFinal"을 입력 받는다.
		// maps = variableNode.getAttributes();
		attribute = maps.getNamedItem("isFinal");
		if (attribute != null) {
			str = attribute.getNodeValue();
			if (!str.isEmpty() && str.equalsIgnoreCase("true"))
				vState.setFinal(true);
		}

		// Attribute "preState"을 입력 받는다.
		// maps = variableNode.getAttributes();
		attribute = maps.getNamedItem("preState");
		if (attribute != null) {
			str = attribute.getNodeValue();
			if (!str.isEmpty() && str.equalsIgnoreCase("true"))
				vState.setPreState(true);
		}

		// Attribute "postState"을 입력 받는다.
		// maps = variableNode.getAttributes();
		attribute = maps.getNamedItem("postState");
		if (attribute != null) {
			str = attribute.getNodeValue();
			if (!str.isEmpty() && str.equalsIgnoreCase("true"))
				vState.setPostState(true);
		}

		return vState;
	}

	private ParamState getParamState(Node paramNode) {
		ParamState pState = new ParamState();

		// Attribute "name"을 입력 받는다.
		NamedNodeMap maps = paramNode.getAttributes();
		Node attribute = maps.getNamedItem("name");
		if (attribute == null)
			throw new NullPointerException();
		String str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		pState.setParamName(str);

		// Attribute "index"를 입력 받는다.
		maps = paramNode.getAttributes();
		attribute = maps.getNamedItem("index");
		if (attribute == null)
			throw new NullPointerException();
		str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		pState.setIndex(Integer.parseInt(str));

		// Attribute "type"을 입력 받는다.
		maps = paramNode.getAttributes();
		attribute = maps.getNamedItem("type");
		if (attribute == null)
			throw new NullPointerException();
		str = attribute.getNodeValue();
		if (str.isEmpty())
			throw new NullPointerException();
		pState.setType(str);

		return pState;
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		// For generating mutants for mujava

		final MutantManager manager = MutantManager.getMutantManager();
		manager.setMutantFileGenerator(new MuJavaMutantFileGenerator());
		manager.setMutantCompiler(new MuJavaMutantCompiler());
		manager.setMutantPackager(new MuJavaMutantPackager());

		final ArrayList<IJavaElement> sources = page1.getSelectedSources();
		final MuJavaProject muProject = MuJavaProject.getMuJavaProject(
				selectedProjectResource, null);

		final GenerationType genWay = muProject.getGenerationWay();
		final List<String> tOps = operatorPage.getSelectedTraditonalOpeator();
		final List<String> cOps = operatorPage.getSelectedClassOpeator();
		// final File f = new File(page3.getFileName());

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {

				// get the current time to measure whole process time
				MutantCostReport result = MutantCostReport
						.newGenerationResult();
				PerformanceElement initElement = PerformanceElement
						.getCurrent();

				// start
				manager.setMuJavaProject(muProject);

				// generate associated tables
				monitor.subTask("Creating Mutant Table and CodeSet Table..");
				MutantTable table = null;
				GeneratedCodeTable cTable = null;
				MutantCostReportTable gTable = null;
				try {
					table = MutantTable.getMutantTable(muProject, monitor);
					cTable = GeneratedCodeTable.getGeneratedCodeTable(
							muProject, monitor);
					gTable = MutantCostReportTable.getGenerationResultTable(
							muProject, monitor);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				monitor.subTask("");

				// Main functionality
				monitor.setTaskName("Generating Mutants : ");

				Map<String, List<String>> map = new HashMap<String, List<String>>();
				map.put(MutantOperator.TRADITIONAL_LITERAL, tOps);
				map.put(MutantOperator.CLASSICAL_LITERAL, cOps);
				generateMutants(sources, map, genWay, monitor);

				// Save all stuffs
				monitor.setTaskName("");
				monitor.subTask("Saving Mutant Table and CodeSet Table..");
				try {
					table = MutantTable.getMutantTable(muProject, monitor);
					table.save(monitor);
					table.getResource().touch(monitor);
					cTable = GeneratedCodeTable.getGeneratedCodeTable(
							muProject, monitor);
					cTable.save(monitor);
					cTable.getResource().touch(monitor);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}

				PerformanceElement actualCost = PerformanceElement
						.getDifferentFromNow(initElement);

				// result = MutantCostReport.getCurrentResult();
				result.setActualCost(actualCost);
				result.recalculate();

				if (result.isValid())
					gTable.addGenerationResult(result);

				try {
					gTable.save(monitor);
					gTable.getResource().touch(monitor);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				MutantCostReport.setCurrentResult(null);

				// clean up all process
				monitor.done();
			}
		};

		boolean isNormal = false;
		try {
			getContainer().run(true, true, op);
			isNormal = true;
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
		} finally {
			manager.setMutantCompiler(null);
			manager.setMutantPackager(null);
			manager.setMutantFileGenerator(null);
			if (!isNormal)
				return false;
		}

		// Open file editor
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, (IFile) muProject.getResource(), true);
				} catch (PartInitException e) {
				}
			}
		});

		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private void generateMutants(ArrayList<IJavaElement> sources,
			Map<String, List<String>> map, GenerationType generationWay,
			IProgressMonitor monitor) {

		MuJavaProject muProject = MutantManager.getMutantManager()
				.getMuJavaProject();

		// prepare the location where the tool save the arifacts, from the
		// project setting.
		IPath rootPath = JavaCore.create(muProject.getResource().getProject())
				.getPath();
		try {
			rootPath = JavaCore.create(muProject.getResource().getProject())
					.getOutputLocation();
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}

		// initialize the Open Java System and prepare classloader
		monitor.subTask("initialize the Open Java System and prepare classloader");

		try {
			// add default output directory
			ArrayList<URL> urls = new ArrayList<URL>();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource res = root.findMember(rootPath);
			URL url = res.getLocation().toFile().toURI().toURL();
			urls.add(url);

			// add project libraries
			IClasspathEntry[] paths = JavaCore.create(
					muProject.getResource().getProject()).getRawClasspath();
			for (IClasspathEntry entry : paths) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					urls.add(root.getLocation().append(entry.getPath())
							.toFile().toURI().toURL());
				}
			}
			URLClassLoader clsLoader = new URLClassLoader(
					urls.toArray(new URL[urls.size()]));
			OJSystem.setClassLoader(clsLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// measuring code
		MutantCostReport result = MutantCostReport.getCurrentResult();
		PerformanceElement initElement = PerformanceElement.getCurrent();

		// OpenJava System initialization
		OJSystem.initialize();
		OJSystem.initConstants();

		// set monitor to show the progress
		int op = 0; // # of mutant operators
		List<String> traditionalOperators = map
				.get(MutantOperator.TRADITIONAL_LITERAL);
		if (traditionalOperators != null) {
			op += traditionalOperators.size(); // add traditional mutant
			// operators
		}
		List<String> classOperators = map.get(MutantOperator.CLASSICAL_LITERAL);
		if (classOperators != null) {
			op += classOperators.size(); // add class mutant operators
		}
		int totalWork = sources.size() * (op + 1); // calculate all works
		monitor.beginTask("..", totalWork);

		/**
		 * Examine if the target class is interface or abstract class. Then we
		 * can't apply mutation testing in these case.
		 */
		// possible sources to be mutated
		
		// hashCode를 최상위 단에서 미리 설정하여 hash.table에 저장하자.
		HashMap<String,String> hashData = null;
		ArrayList<HashMap<String,String>> hashList = new ArrayList<HashMap<String,String>>();
		
		Map<IJavaElement, Object[]> possibleSources = new HashMap<IJavaElement, Object[]>();
		for (IJavaElement source : sources) {
			String fileName = source.getElementName();

			monitor.subTask("Parsing & generating pre-analysis information for : "
					+ fileName);

			// look for the location where each of target source is saved.
			ICompilationUnit src = (ICompilationUnit) source;
			IType javaClassType = src.getType(fileName.substring(0,
					fileName.lastIndexOf(".")));
			if (MutationSystem.hasMainFunction(javaClassType)) {

				MuJavaLogger.getLogger().warn(
						"File (" + fileName
								+ " ) contains 'static void main()' method.");
				MuJavaLogger
						.getLogger()
						.warn("Mutants are not generated for the 'public static void main()' method");
			}

			try {

				int class_type = MutationSystem.getClassType(javaClassType);
				if (class_type == IMutationSystem.INTERFACE) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'interface' ");
					continue;
				} else if (class_type == IMutationSystem.ABSTRACT) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'abstract' class ");
					continue;
				} else if (class_type == IMutationSystem.APPLET) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'applet' class ");
					continue;
				} else if (class_type == IMutationSystem.GUI) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'GUI' class ");
					continue;
				} else if (class_type == IMutationSystem.ANONYMOUS) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'anonymous' class ");
					continue;
				} else if (class_type == IMutationSystem.UNKNOWN) {
					MuJavaLogger.getLogger().warn(
							" -- Can't apply because " + fileName
									+ " is 'unknown structure' class ");
					continue;
				}

				/*
				hashData = new HashMap<String,String>();
				hashData.put("package",source.getParent().getElementName());
				hashData.put("src", source.getElementName());
				hashData.put("hashVal",""+hashCodeVal);
				hashList.add(hashData);
				 */
				
				// Generated Parsed Tree
				FileEnvironment openJavaFileEnv = new FileEnvironment(
						OJSystem.env, null, null, source.getResource()
								.getLocation().toFile());

				CompilationUnit parseTree = MutantEngine.createParseTree(
						source, openJavaFileEnv, monitor);

				if (parseTree != null && openJavaFileEnv != null) {
					possibleSources.put(source, new Object[] { parseTree,
							openJavaFileEnv });
				}
			} catch (Exception exp) {
				MuJavaLogger.getLogger().error(
						"[Exception] " + fileName + " " + exp.toString());
				exp.printStackTrace();
			} catch (Error er) {
				MuJavaLogger.getLogger().error(
						"[Error] " + fileName + " " + er.toString());
			}
			monitor.worked(1);
		}

		// report the progress
		int elapsedWork = (sources.size() - possibleSources.size()) * op;
		monitor.worked(elapsedWork);

		// measuting code
		PerformanceElement preparingCost = PerformanceElement
				.getDifferentFromNow(initElement);
		result.setPreparingCost(preparingCost);

		// [2] Apply mutation testing
		// generate and compile mutants in the makeMutants function

		// MuJavaLogger.getLogger().debug("* Generating and Compiling Mutants ");

		for (IJavaElement src : possibleSources.keySet()) {
			ICompilationUnit jdtUnit = (ICompilationUnit) src;

			Object[] data = possibleSources.get(src);
			CompilationUnit parseTree = (CompilationUnit) data[0];
			FileEnvironment openJavaFile = (FileEnvironment) data[1];

			// MuJavaLogger.getLogger().debug("For " + src.getElementName());

			// generate and compile mutants in the makeMutants function
			MuJavaMutationEngine genEngine = new MuJavaMutationEngine(src, map,
					generationWay, monitor);

			genEngine.genMutants(jdtUnit, parseTree, openJavaFile,
					traditionalOperators, classOperators, monitor);
			
			// ICompilationUnit sourceCode.getPath().hashCode()
			// 2013/11/19 jdtUnit.getPath().hashCode();
		}

		// hash.table 만들자.
		try{
			String hashFileName = muProject.getDirectory()+ File.separator + "Mutants" + File.separator + muProject.getName() + File.separator + "hash.table";
			
			File f = new File(hashFileName);
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			for (IJavaElement src : possibleSources.keySet()) {
				ICompilationUnit jdtUnit = (ICompilationUnit) src;
				String package_name = src.getParent().getElementName();
				String source_name = src.getElementName();
				pw.println( package_name + "." + source_name 
						+ "\t" + source_name.substring(0,source_name.length()-".java".length())
								+ "\t" + jdtUnit.getPath().hashCode());
			}
			pw.flush();
			pw.close();
			hashList = null;
		}catch(Exception e){
			e.printStackTrace();
			MuJavaLogger.getLogger().error(	"[Error] Generating Hash Table");
			
		}

		MuJavaLogger.getLogger().debug("..Complete");

		OJSystem.initialize();
		OJSystem.setClassLoader(null);
	}
}
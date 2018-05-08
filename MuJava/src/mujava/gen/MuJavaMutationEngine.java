package mujava.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaist.selab.util.MuJavaLogger;
import mujava.CodeSet;
import mujava.GeneratedCodeTable;
import mujava.MuJavaMutantInfo;
import mujava.MuJavaProject;
import mujava.MutantManager;
import mujava.MutantOperator;
import mujava.MutantPackager;
import mujava.MutantTable;
import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.gen.seeker.classical.AMC;
import mujava.gen.seeker.classical.EAM;
import mujava.gen.seeker.classical.EMM;
import mujava.gen.seeker.classical.EOA;
import mujava.gen.seeker.classical.EOC;
import mujava.gen.seeker.classical.IOP;
import mujava.gen.seeker.classical.ISK;
import mujava.gen.seeker.classical.JTD;
import mujava.gen.seeker.classical.OAN;
import mujava.gen.seeker.classical.OAO;
import mujava.gen.seeker.classical.OMR;
import mujava.gen.seeker.classical.PNC;
import mujava.gen.seeker.classical.PRV;
import mujava.gen.seeker.traditional.AODSChangePointSeeker;
import mujava.gen.seeker.traditional.AODUChangePointSeeker;
import mujava.gen.seeker.traditional.AOISChangePointSeeker;
import mujava.gen.seeker.traditional.AOIUChangePointSeeker;
import mujava.gen.seeker.traditional.AORBChangePointSeeker;
import mujava.gen.seeker.traditional.AORSChangePointSeeker;
import mujava.gen.seeker.traditional.ASRSChangePointSeeker;
import mujava.gen.seeker.traditional.LODChangePointSeeker;
import mujava.gen.seeker.traditional.LOIChangePointSeeker;
import mujava.gen.seeker.traditional.LORChangePointSeeker;
import mujava.gen.seeker.traditional.BODChangePointSeeker;
import mujava.gen.seeker.traditional.BOIChangePointSeeker;
import mujava.gen.seeker.traditional.BORChangePointSeeker;
import mujava.gen.seeker.traditional.CODChangePointSeeker;
import mujava.gen.seeker.traditional.COIChangePointSeeker;
import mujava.gen.seeker.traditional.CORChangePointSeeker;
import mujava.gen.seeker.traditional.RORChangePointSeeker;
import mujava.gen.writer.MutantWriter;
import mujava.gen.writer.normal.MutantNormalWriter;
import mujava.gen.writer.state.MutantNormalStateWriter;
import mujava.op.util.ChangePoint;
import mujava.plugin.editor.mujavaproject.CostReportForOperator;
import mujava.plugin.editor.mujavaproject.MutantCostReport;
import mujava.util.MutantCompiler;
import openjava.mop.FileEnvironment;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;


public class MuJavaMutationEngine extends MutantEngine {
	private MuJavaProject mjProject;

	// Supporting different generation way:
	// normal, MSG, experimental mode, and NuJava
	public static boolean IS_WINDOWS = true;
	protected GenerationType generationMode = GenerationType.SC;

	java.util.List<String> classOp = new ArrayList<String>();

	java.util.List<String> traditionalOp = new ArrayList<String>();

	MutantTable mTable = null;

	GeneratedCodeTable cTable = null;

	// Hashtable<String, ClassState> states = null;

	/**
	 * 
	 * Initialize environment
	 * <p>
	 * { Mutation Project, Mutant Table, CodeSet Table, Mutation Operators,
	 * Generation mode, States(Option) }
	 * 
	 * @param source
	 * @param opMap
	 * @param generationMode
	 * @param states
	 * @param monitor
	 */
	public MuJavaMutationEngine(IJavaElement source,
			Map<String, List<String>> opMap, GenerationType generationMode,
			IProgressMonitor monitor) {

		this.generationMode = generationMode;

		// this.states = states;

		MutantManager manager = MutantManager.getMutantManager();
		mjProject = manager.getMuJavaProject();

		// prepare mutant repository
		try {
			mTable = MutantTable.getMutantTable(mjProject, monitor);
			cTable = GeneratedCodeTable.getGeneratedCodeTable(mjProject,
					monitor);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void genMutants(ICompilationUnit jdtUnit, CompilationUnit parseTree,
			FileEnvironment fileEnv, List<String> traditionalOperators,
			List<String> classOperators, IProgressMonitor monitor) {

		// Check unexpected condition
		ClassDeclarationList cdecls = parseTree.getClassDeclarations();
		if (cdecls == null || cdecls.size() == 0)
			return;
		

		if (!traditionalOperators.isEmpty()) {

			MuJavaLogger.getLogger().info("* Generating method mutants");
			monitor.subTask("Generating traditional mutants");

			genTraditionalMutants(jdtUnit, parseTree, fileEnv,
					traditionalOperators, monitor);
		}

		if (!classOperators.isEmpty()) {

			MuJavaLogger.getLogger().info("* Generating class mutants");
			monitor.subTask("Generating class mutants");

			genClassMutants1(jdtUnit, parseTree, fileEnv, monitor);

		}
	}

	/**
	 * 
	 * @param mutator
	 * @param sourceJavaElement
	 * @param analyzedCode
	 * @param writers
	 * @param costReport
	 * @param monitor
	 */
	private void createMutantAndCodeSet(AbstractChangePointSeeker mutator,
			ICompilationUnit sourceJavaElement, CompilationUnit analyzedCode,
			MutantWriter mainWriter, MutantWriter subWriter,
			CostReportForOperator costReport, IProgressMonitor monitor) {

		String operatorName = mutator.getOperatorName();
		MutantManager manager = MutantManager.getMutantManager();
		IProject eclipseProject = mjProject.getResource().getProject();

		// create files related to a mutant.
		IFile rootLoc = eclipseProject.getFile(mjProject.getMutantDirectory());
		IPath path = rootLoc.getFullPath();
		path = path.append(mjProject.getName());
		path = path.append(operatorName);

		/*
		 * According to the generation mode, a code set is generated by a mutant
		 * or a set of mutant
		 */
		if (generationMode == GenerationType.SC) {
			MutantNormalWriter normalWriter = (MutantNormalWriter) mainWriter;

			for (Iterator<ChangePoint> iter = normalWriter
					.getChangePointsIterator(); iter.hasNext();) {
				ChangePoint cPoint = iter.next();
				normalWriter.setCurrentChangePoint(cPoint);

				// Obtains the number of iteration to write files generated
				// from
				// a change point and repeat the generation process
				int numOfIteration = normalWriter.getSizeOfIteration(cPoint);

				for (int j = 0; j < numOfIteration; j++) {
					monitor.subTask("Create mutants and compile for : "
							+ sourceJavaElement.getElementName());

					// CodeSet codeSet = CodeSet.getNewCodeSet(path,
					// normalWriter
					// .getMutationOperatorName());

					normalWriter.emptyMutantInfo();
					normalWriter.emptyLineNumber();

					try {
						CodeSet codeSet = manager.getMutantFileGenerator()
								.generate(path, sourceJavaElement,
										analyzedCode, normalWriter, monitor);

						if (codeSet != null && codeSet.isValid()) {
							// Check whether duplicated mutants are
							// generated
							boolean flag = true;
							List<MuJavaMutantInfo> exprs = normalWriter
									.getMutantInfos();
							for (MuJavaMutantInfo info : exprs) {
								// String mutantID = info.getMutantID();
								if (!mTable.isExistedMutantID(info
										.getMutantID()))
									flag = false;
							}

							if (flag) {
								monitor.subTask("Eliminate duplicated mutants");
								codeSet.remove();
								continue;
							}

							// compile generated mutant and original
							// file
							// with
							// original class files
							MutantCompiler compiler = manager
									.getMutantCompiler();
							boolean result = compiler.compileMutants(
									eclipseProject, sourceJavaElement, codeSet,
									monitor);

							if (result) {
								// store the generated mutant into one
								// jar
								// file
								MutantPackager packager = manager
										.getMutantPackager();
								packager.packageMutants(codeSet);

								// Map codeset and its generated mutant
								// information
								monitor.subTask("Register generated mutants for :"
										+ sourceJavaElement.getElementName());

								for (MuJavaMutantInfo info : exprs) {
									if (mTable.isExistedMutantID(info
											.getMutantID()))
										continue;

									// mutant properties are saved in
									// the
									// pre-defined file
									String mutantID = info.getMutantID();

									MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(
											mjProject,
											eclipseProject.getName(), mutantID);
									mutantInfo.setMutantInfo(info);
									mutantInfo.setCodeID(codeSet.getID());
									mutantInfo.setTargetFile(sourceJavaElement
											.getResource().getFullPath()
											.toString());

									// Store the each mutant information
									// permanently
									try {
										mutantInfo.save(null);
									} catch (IOException e) {
										e.printStackTrace();
									}

									mTable.addMutant(operatorName, mutantID);
									codeSet.addMutantID(mutantID);
									costReport.addMutant(mutantID);
								}
							} else {
								monitor.subTask("Eliminate error-compiled mutants");
								//0824 codeSet.remove();
								continue;
							}

							// store the CodeSet permanently
							try {
								codeSet.save(null);
							} catch (IOException e) {
								e.printStackTrace();
							}
							cTable.addCodeSet(codeSet);
						} else {
							MuJavaLogger
									.getLogger()
									.error("*No mutant is generated although there are change points");
							codeSet.remove();
						}
					} catch (Exception e1) {
						MuJavaLogger.getLogger().error(
								"fails to create "
										+ sourceJavaElement.getResource()
												.getName());
					}
				}
			}
			
		} else if (generationMode == GenerationType.SCC) {
			//System.out.println("[createMutantAndCodeSet SCC]");
			MutantNormalStateWriter normalStateWriter = (MutantNormalStateWriter) mainWriter;


			if(IS_WINDOWS) {
				normalStateWriter.stateDirectory = eclipseProject.getLocation().toString().replace("/", "\\\\").concat("\\\\" + "States" + "\\\\");//windows
			} else {
				normalStateWriter.stateDirectory = eclipseProject.getLocation().toString().concat(File.separator + "States" + File.separator); //mac
			}
			
			String subDir = null;
			if(IS_WINDOWS) {
				subDir = mjProject.getName().toString().concat("\\\\ResultStates");//windows
			} else {
				subDir = mjProject.getName().toString().concat(File.separator + "ResultStates");//mac
			}
			
			normalStateWriter.stateDirectory = normalStateWriter.stateDirectory.concat(subDir);
			
			//System.out.println("stateDirectory = " + normalStateWriter.stateDirectory);
			
			for (Iterator<ChangePoint> iter = normalStateWriter
					.getChangePointsIterator(); iter.hasNext();) {
				ChangePoint cPoint = iter.next();
				normalStateWriter.setCurrentChangePoint(cPoint);

				// Obtains the number of iteration to write files generated
				// from
				
				// a change point and repeat the generation process
				int numOfIteration = normalStateWriter.getSizeOfIteration(cPoint);

				for (int j = 0; j < numOfIteration; j++) {
					monitor.subTask("Create mutants and compile for : "
							+ sourceJavaElement.getElementName());
					//System.out.println("Create mutants and compile for : " + sourceJavaElement.getElementName());

					// CodeSet codeSet = CodeSet.getNewCodeSet(path,
					// normalWriter
					// .getMutationOperatorName());

					normalStateWriter.emptyMutantInfo();
					normalStateWriter.emptyLineNumber();
					normalStateWriter.emptyVarInfo();

					try {
						CodeSet codeSet = manager.getMutantFileGenerator()
								.generate(path, sourceJavaElement,
										analyzedCode, normalStateWriter, monitor);

						if (codeSet != null && codeSet.isValid()) {
							// Check whether duplicated mutants are
							// generated
							boolean flag = true;
							List<MuJavaMutantInfo> exprs = normalStateWriter
									.getMutantInfos();
							for (MuJavaMutantInfo info : exprs) {
								// String mutantID = info.getMutantID();
								//normalStateWriter.clearVars();
								
								if (!mTable.isExistedMutantID(info
										.getMutantID()))
									flag = false;
							}

							if (flag) {
								monitor.subTask("Eliminate duplicated mutants");
								codeSet.remove();
								continue;
							}

							// compile generated mutant and original
							// file
							// with
							// original class files
							MutantCompiler compiler = manager
									.getMutantCompiler();
							boolean result = compiler.compileMutants(
									eclipseProject, sourceJavaElement, codeSet,
									monitor);

							if (result) {
								// store the generated mutant into one
								// jar
								// file
								MutantPackager packager = manager
										.getMutantPackager();
								packager.packageMutants(codeSet);

								// Map codeset and its generated mutant
								// information
								monitor.subTask("Register generated mutants for :"
										+ sourceJavaElement.getElementName());

								for (MuJavaMutantInfo info : exprs) {
									if (mTable.isExistedMutantID(info
											.getMutantID()))
										continue;

									// mutant properties are saved in
									// the
									// pre-defined file
									String mutantID = info.getMutantID();

									MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(
											mjProject,
											eclipseProject.getName(), mutantID);
									mutantInfo.setMutantInfo(info);
									mutantInfo.setCodeID(codeSet.getID());
									mutantInfo.setTargetFile(sourceJavaElement
											.getResource().getFullPath()
											.toString());

									// Store the each mutant information
									// permanently
									try {
										mutantInfo.save(null);
									} catch (IOException e) {
										e.printStackTrace();
									}

									mTable.addMutant(operatorName, mutantID);
									codeSet.addMutantID(mutantID);
									costReport.addMutant(mutantID);
								}
							} else {
								monitor.subTask("Eliminate error-compiled mutants");
								codeSet.remove(); //msyu
								continue;
							}

							// store the CodeSet permanently
							try {
								codeSet.save(null);
							} catch (IOException e) {
								e.printStackTrace();
							}
							cTable.addCodeSet(codeSet);
						} else {
							MuJavaLogger
									.getLogger()
									.error("*No mutant is generated although there are change points");
							codeSet.remove();
						}
					} catch (Exception e1) {
						MuJavaLogger.getLogger().error(
								"fails to create "
										+ sourceJavaElement.getResource()
												.getName());
					}
				}
			}
		} else if (generationMode == GenerationType.MSG
				|| generationMode == GenerationType.REACH) {

			MutantWriter writer = mainWriter;
			writer.emptyMutantInfo();
			writer.emptyLineNumber();

			// ?�직 ?�나??Code Set???�??generation???�행??
			// -> MSG??경우 CodeSet???�나 ?�상??가�????�도�?변�??�야??
			// CodeSet codeSet = CodeSet.getNewCodeSet(path, writer
			// .getMutationOperatorName());

			try {
				CodeSet codeSet = manager.getMutantFileGenerator().generate(
						path, sourceJavaElement, analyzedCode, writer, monitor);

				if (codeSet != null && codeSet.isValid()) {
					// Eliminate duplicated mutants
					List<MuJavaMutantInfo> infos = writer.getMutantInfos();
					List<MuJavaMutantInfo> newInfos = new ArrayList<MuJavaMutantInfo>();
					for (MuJavaMutantInfo info : infos) {
						if (!mTable.isExistedMutantID(info.getMutantID()))
							newInfos.add(info);
					}

					if (!newInfos.isEmpty()) {
						// compile generated mutant and original file
						// with
						// original class files
						MutantCompiler compiler = manager.getMutantCompiler();
						boolean result = compiler.compileMutants(
								eclipseProject, sourceJavaElement, codeSet,
								null);

						if (result) {
							// store the generated mutant into one jar
							// file
							MutantPackager packager = manager
									.getMutantPackager();
							packager.packageMutants(codeSet);

							// Map codeset and its generated mutant
							// information
							for (MuJavaMutantInfo info : newInfos) {
								// mutant properties are saved in the
								// pre-defined
								// file
								String mutantID = info.getMutantID();

								MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(
										mjProject, eclipseProject.getName(),
										mutantID);
								mutantInfo.setMutantInfo(info);
								mutantInfo.setCodeID(codeSet.getID());
								mutantInfo
										.setTargetFile(sourceJavaElement
												.getResource().getFullPath()
												.toString());

								// Store the each mutant information
								// permanently
								try {
									mutantInfo.save(null);
								} catch (IOException e) {
									e.printStackTrace();
								}

								mTable.addMutant(operatorName, mutantID);
								codeSet.addMutantID(mutantID);
								costReport.addMutant(mutantID);
							}

							// store the CodeSet permanently
							try {
								codeSet.save(null);
							} catch (IOException e) {
								e.printStackTrace();
							}
							cTable.addCodeSet(codeSet);
						} else {
							monitor.subTask("Eliminate error-compiled mutants");
							codeSet.remove();
						}
					}

					if (newInfos.isEmpty()) {
						codeSet.remove();
					}
				}
			} catch (Exception e1) {
				MuJavaLogger.getLogger().error(
						"fails to create "
								+ sourceJavaElement.getResource().getName());
			}
		} else if (generationMode == GenerationType.WS) {

			mainWriter.emptyMutantInfo();
			mainWriter.emptyLineNumber();
			subWriter.emptyMutantInfo();
			subWriter.emptyLineNumber();

			// ?�직 ?�나??Code Set???�??generation???�행??
			// -> MSG??경우 CodeSet???�나 ?�상??가�????�도�?변�??�야??
			// CodeSet codeSet = CodeSet.getNewCodeSet(path, writer
			// .getMutationOperatorName());

			try {
				CodeSet mainCodeSet = manager.getMutantFileGenerator()
						.generate(path, sourceJavaElement, analyzedCode,
								mainWriter, monitor);
				CodeSet subCodeSet = manager.getMutantFileGenerator().generate(
						path, sourceJavaElement, analyzedCode, subWriter,
						monitor);

				if (mainCodeSet != null && mainCodeSet.isValid()
						&& subCodeSet != null && subCodeSet.isValid()) {

					// Eliminate duplicated mutants
					List<MuJavaMutantInfo> mainInfos = mainWriter
							.getMutantInfos();
					List<MuJavaMutantInfo> subInfos = subWriter
							.getMutantInfos();

					List<MuJavaMutantInfo> newInfos = new ArrayList<MuJavaMutantInfo>();
					for (MuJavaMutantInfo info : mainInfos) {
						// ?�성??CodeSet???�해 ?�성�?Mutant가 기존???�성??Mutant?� 중복?�는지
						// ?�인?�다.
						// 만약 ?��? ?�성??Mutant?�면 Mutant�?추�??��? ?�는??
						String mutantID = info.getMutantID();
						if (!mTable.isExistedMutantID(mutantID)) {
							boolean hasMutantID = false;
							for (MuJavaMutantInfo subInfo : subInfos) {
								if (subInfo.getMutantID().equals(mutantID)) {
									hasMutantID = true;
									break;
								}
							}
							if (hasMutantID) {
								newInfos.add(info);
							}
						}
					}

					MutantCompiler compiler = manager.getMutantCompiler();
					MutantPackager packager = manager.getMutantPackager();

					if (!newInfos.isEmpty()) {
						// compile generated mutant and original file with
						// original class files
						boolean mainResult = compiler.compileMutants(
								eclipseProject, sourceJavaElement, mainCodeSet,
								null);
						boolean subResult = compiler.compileMutants(
								eclipseProject, sourceJavaElement, subCodeSet,
								null);

						if (mainResult && subResult) {

							// store the generated mutant into one jar file
							packager.packageMutants(mainCodeSet);
							packager.packageMutants(subCodeSet);

							// Map codeset and its generated mutant information
							for (MuJavaMutantInfo info : newInfos) {
								// mutant properties are saved in the
								// pre-defined file
								String mutantID = info.getMutantID();

								MuJavaMutantInfo mutantInfo = new MuJavaMutantInfo(
										mjProject, eclipseProject.getName(),
										mutantID);
								mutantInfo.setMutantInfo(info);
								mutantInfo.setCodeID(mainCodeSet.getID());
								mutantInfo.setSubCodeID(subCodeSet.getID());
								mutantInfo
										.setTargetFile(sourceJavaElement
												.getResource().getFullPath()
												.toString());

								// Store the each mutant information
								// permanently
								try {
									mutantInfo.save(null);
								} catch (IOException e) {
									e.printStackTrace();
								}

								mTable.addMutant(operatorName, mutantID);
								mainCodeSet.addMutantID(mutantID);
								subCodeSet.addMutantID(mutantID);
								costReport.addMutant(mutantID);
							}

							// store the CodeSet permanently
							try {
								mainCodeSet.save(null);
								subCodeSet.save(null);
							} catch (IOException e) {
								e.printStackTrace();
							}

							cTable.addCodeSet(mainCodeSet);
							cTable.addCodeSet(subCodeSet);
						} else {
							monitor.subTask("Eliminate error-compiled mutants");
							mainCodeSet.remove();
						}
					}

					if (newInfos.isEmpty()) {
						mainCodeSet.remove();
					}
				}
			} catch (Exception e1) {
				MuJavaLogger.getLogger().error(
						"fails to create "
								+ sourceJavaElement.getResource().getName());
			}
		}
	}

	void genClassMutants1(ICompilationUnit jdtUnit, CompilationUnit parseTree,
			FileEnvironment fileEnv, IProgressMonitor monitor) {

		String className = jdtUnit.getElementName();
		int index = className.lastIndexOf(".");
		if (index > 0)
			className = className.substring(0, index);

		ClassDeclarationList cdecls = parseTree.getClassDeclarations();
		for (int j = 0; j < cdecls.size(); ++j) {
			ClassDeclaration cdecl = cdecls.get(j);

			if (cdecl.getName().equals(className)) {
				// String qname = file_env.toQualifiedName(cdecl.getName());
				AbstractChangePointSeeker mutant_op = null;

				for (String op : classOp) {
					if (op.equalsIgnoreCase("AMC")) {
						MuJavaLogger.getLogger()
								.debug(" Applying AMC ... ... ");
						mutant_op = new AMC(jdtUnit, fileEnv);
					}
					if (op.equalsIgnoreCase("IOP")) {
						MuJavaLogger.getLogger().debug(" Applying IOP... ... ");
						mutant_op = new IOP(jdtUnit, fileEnv);
					}

					// if (classOp.contains("IOR")) {
					// // TODO swkim
					// // Debug.println(" Applying IOR ... ... ");
					// // try {
					// // Class parent_class = Class.forName(qname)
					// // .getSuperclass();
					// // if (!(parent_class.getName()
					// // .equals("java.lang.Object"))) {
					// // String temp_str = parent_class.getName();
					// // String result_str = "";
					// // for (int k = 0; k < temp_str.length(); k++) {
					// // char c = temp_str.charAt(k);
					// // if (c == '.') {
					// // result_str = result_str + "/";
					// // } else {
					// // result_str = result_str + c;
					// // }
					// // }
					// // // TODO swkim
					// // // File f = new File(MutationSystem.SRC_PATH,
					// // // result_str + ".java");
					// // File f = new File(".");
					// // if (f.exists()) {
					// // CompilationUnit[] parent_comp_unit = new
					// CompilationUnit[1];
					// // FileEnvironment[] parent_file_env = new
					// FileEnvironment[1];
					// // this.generateParseTree(f, parent_comp_unit,
					// // parent_file_env);
					// // this.initParseTree(parent_comp_unit,
					// // parent_file_env);
					// // mutant_op = new IOR(sourceJavaElement,
					// // file_env, cdecl, parsedSource);
					// // ((IOR) mutant_op).setParentEnv(
					// // parent_file_env[0],
					// // parent_comp_unit[0]);
					// // parsedSource.accept(mutant_op);
					// // }
					// // }
					// // } catch (ClassNotFoundException e) {
					// // System.out
					// // .println(" Exception at generating IOR mutant. file :
					// AllMutantsGenerator.java ");
					// // } catch (NullPointerException e1) {
					// // System.out.print(" IOP ^^; ");
					// // }

					// }

					if (op.equalsIgnoreCase("ISK")) {
						MuJavaLogger.getLogger()
								.debug(" Applying ISK ... ... ");
						mutant_op = new ISK(jdtUnit, fileEnv);
					}
					if (op.equalsIgnoreCase("OAO")) {
						MuJavaLogger.getLogger()
								.debug(" Applying OAO ... ... ");
						mutant_op = new OAO(jdtUnit, fileEnv);
					}
					if (op.equalsIgnoreCase("OAN")) {
						MuJavaLogger.getLogger()
								.debug(" Applying OAN ... ... ");
						mutant_op = new OAN(jdtUnit, fileEnv);
					}
					if (op.equalsIgnoreCase("OMR")) {
						MuJavaLogger.getLogger()
								.debug(" Applying OMR ... ... ");
						mutant_op = new OMR(jdtUnit, fileEnv);
					}

					// if (classOp.contains("IOP")) {
					// Debug.println(" Applying IOP ... ... ");
					// mutant_op = new IOP(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("IPC")) {
					// Debug.println(" Applying IPC ... ... ");
					// mutant_op = new IPC(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					if (op.equalsIgnoreCase("PNC")) {
						MuJavaLogger.getLogger()
								.debug(" Applying PNC ... ... ");
						mutant_op = new PNC(jdtUnit, fileEnv);
					}

					// if (classOp.contains("PMD")) {
					// Debug.println(" Applying PMD ... ... ");
					// // if(existIHD){
					// mutant_op = new PMD(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// // }
					// }

					// if (classOp.contains("PPD")) {
					// Debug.println(" Applying PPD ... ... ");
					// // if(existIHD){
					// mutant_op = new PPD(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// // }
					// }

					if (op.equalsIgnoreCase("PRV")) {
						MuJavaLogger.getLogger()
								.debug(" Applying PRV ... ... ");
						mutant_op = new PRV(jdtUnit, fileEnv);
					}
					if (op.equalsIgnoreCase("JTD")) {
						MuJavaLogger.getLogger()
								.debug(" Applying JTD ... ... ");
						mutant_op = new JTD(jdtUnit, fileEnv);
					}
					// if (op.equalsIgnoreCase("JSI")) {
					// MuJavaLogger.getLogger()
					// .debug(" Applying JSI ... ... ");
					// ScannerForJSI scanner = new ScannerForJSI(file_env);
					// try {
					// analyzedCode.accept(scanner);
					// } catch (ParseTreeException e) {
					// e.printStackTrace();
					// }
					// JSI jsi = new JSI(sourceJavaElement, file_env);
					// jsi.setScnner(scanner);
					// mutant_op = jsi;
					// }

					// if (classOp.contains("PCI")) {
					// Debug.println(" Applying PCI ... ... ");
					// mutant_op = new PCI(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("PCC")) {
					// Debug.println(" Applying PCC ... ... ");
					// mutant_op = new PCC(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("PCD")) {
					// Debug.println(" Applying PCD ... ... ");
					// mutant_op = new PCD(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("JSD")) {
					// Debug.println(" Applying JSC ... ... ");
					// mutant_op = new JSD(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("JTD")) {
					// Debug.println(" Applying JTD ... ... ");
					// mutant_op = new JTD(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("JID")) {
					// Debug.println(" Applying JID ... ... ");
					// mutant_op = new JID(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					// if (classOp.contains("OAN")) {
					// Debug.println(" Applying OAN ... ... ");
					// mutant_op = new OAN(project,sourceJavaElement, file_env,
					// cdecl, parsedSource);
					// parsedSource.accept(mutant_op);
					// }

					if (op.equalsIgnoreCase("EOA")) {
						MuJavaLogger.getLogger()
								.debug(" Applying EOA ... ... ");
						mutant_op = new EOA(jdtUnit, fileEnv);
					}

					if (op.equalsIgnoreCase("EOC")) {
						MuJavaLogger.getLogger()
								.debug(" Applying EOC ... ... ");
						mutant_op = new EOC(jdtUnit, fileEnv);
					}

					if (op.equalsIgnoreCase("EAM")) {
						MuJavaLogger.getLogger().debug(
								" Applying EAM ... ... "
										+ jdtUnit.getElementName());
						mutant_op = new EAM(jdtUnit, fileEnv);
					}

					if (op.equalsIgnoreCase("EMM")) {
						MuJavaLogger.getLogger()
								.debug(" Applying EMM ... ... ");
						mutant_op = new EMM(jdtUnit, fileEnv);
					}

					if (mutant_op != null) {
						monitor.setTaskName(".. Applying " + op + " - ");

						// measuring cost from here
						PerformanceElement initCost = PerformanceElement
								.getCurrent();

						MutantWriter writer = null;
						MutantOperator mop = MutantOperator
								.getMutantOperator(mutant_op.getOperatorName());
						try {
							parseTree.accept(mutant_op);
							int innerType = MutantOperator.GEN_NORMAL;
							switch (generationMode) {
							case MSG:
								innerType = MutantOperator.GEN_MSG;
								break;
							case WS:
								innerType = MutantOperator.GEN_NUJAVA;
								break;
							case SC:
								innerType = MutantOperator.GEN_NORMAL;
								break;
							case REACH:
								innerType = MutantOperator.GEN_EXP_REACH;
								break;
							case SCC:
								innerType = MutantOperator.GEN_STATE;
								break;	
							}
							writer = mop.getMutantCodeWriter(mutant_op,
									innerType);
						} catch (ParseTreeException e) {
							MuJavaLogger.getLogger().error(
									"Exception, during generating classical mutants for the class "
											+ className);
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// measuring analysis cost to here
						PerformanceElement analysisCost = PerformanceElement
								.getDifferentFromNow(initCost);

						CostReportForOperator operatorReport = new CostReportForOperator(
								mutant_op.getOperatorName());
						operatorReport.setAnalysisCost(analysisCost);
						operatorReport.addTarget(jdtUnit.getResource()
								.getFullPath().toString());

						if (writer != null) {
							createMutantAndCodeSet(mutant_op, jdtUnit,
									parseTree, writer, null, operatorReport,
									monitor);
						}

						// measuring generation performance
						PerformanceElement genCost = PerformanceElement
								.getDifferentFromNow(initCost);
						operatorReport.setCost(genCost);

						// manage the performance result
						MutantCostReport result = MutantCostReport
								.getCurrentResult();
						result.cumulateSubResult(operatorReport);
					}
					monitor.worked(1);
				}
			}

		}
	}

	void genTraditionalMutants(ICompilationUnit jdtUnit,
			CompilationUnit parseTree, FileEnvironment fileEnv,
			List<String> operators, IProgressMonitor monitor) {

		IType type = jdtUnit.findPrimaryType();
		String className = type.getElementName();
		ClassDeclarationList cdecls = parseTree.getClassDeclarations();

		for (int j = 0; j < cdecls.size(); ++j) {
			ClassDeclaration cdecl = cdecls.get(j);

			if (className.equals(cdecl.getName())) {
				AbstractChangePointSeeker mutant_op = null;

				for (String op : operators) {
					if (op.equalsIgnoreCase("AORB")) {
						MuJavaLogger.getLogger().debug(
								" Applying AOR-Binary ... ... ");
						mutant_op = new AORBChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("AODU")) {
						MuJavaLogger.getLogger().debug(
								" Applying AOD-Normal-Unary ... ... ");
						mutant_op = new AODUChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("AORS")) {
						MuJavaLogger.getLogger().debug(
								" Applying AOR-Short-Cut ... ... ");
						mutant_op = new AORSChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("AODS")) {
						MuJavaLogger.getLogger().debug(
								" Applying AOD-Short-Cut ... ... ");
						mutant_op = new AODSChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("AOIS")) {
						MuJavaLogger.getLogger().debug(
								" Applying AOI-Short-Cut ... ... ");
						mutant_op = new AOISChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("AOIU")) {
						MuJavaLogger.getLogger().debug(
								"  Applying AOI--Unary ... ... ");
						mutant_op = new AOIUChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("ROR")) {
						MuJavaLogger.getLogger().debug(
								"  Applying ROR ... ... ");
						mutant_op = new RORChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("COR")) {
						MuJavaLogger.getLogger()
								.debug(" Applying COR ... ... ");
						mutant_op = new CORChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("COD")) {
						MuJavaLogger.getLogger()
								.debug(" Applying COD ... ... ");
						mutant_op = new CODChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("COI")) {
						MuJavaLogger.getLogger()
								.debug(" Applying COI ... ... ");
						mutant_op = new COIChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("LOR")) {
						MuJavaLogger.getLogger()
								.debug(" Applying LOR ... ... ");
						mutant_op = new LORChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("LOD")) {
						MuJavaLogger.getLogger()
								.debug(" Applying LOD ... ... ");
						mutant_op = new LODChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("LOI")) {
						MuJavaLogger.getLogger()
								.debug(" Applying LOI ... ... ");
						mutant_op = new LOIChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("BOR")) {
						MuJavaLogger.getLogger()
								.debug(" Applying BOR ... ... ");
						mutant_op = new BORChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("BOI")) {
						MuJavaLogger.getLogger()
								.debug(" Applying BOI ... ... ");
						mutant_op = new BOIChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("BOD")) {
						MuJavaLogger.getLogger()
								.debug(" Applying BOD ... ... ");
						mutant_op = new BODChangePointSeeker(jdtUnit, fileEnv);
					} else if (op.equalsIgnoreCase("ASRS")) {
						MuJavaLogger.getLogger().debug(
								" Applying ASR-Short-Cut ... ... ");
						mutant_op = new ASRSChangePointSeeker(jdtUnit, fileEnv);
					}

					if (mutant_op != null) {
						monitor.setTaskName(".. Applying " + op + " - ");
						CostReportForOperator gResultForMutantOperator = new CostReportForOperator(
								mutant_op.getOperatorName());

						// measuring analysis cost
						PerformanceElement initCost = PerformanceElement
								.getCurrent();

						// 1�??�상??mutant writer�??�집?�다. ?�반?�으�?1개의 mutant writer�?
						// ?�성?��?�? NUJAVA??NuJAVA?� MSG??writer 2개�? ?�용?�다.
						MutantWriter mainWriter = null;
						MutantWriter subWriter = null;

						try {
							// search all change point
							parseTree.accept(mutant_op);

							MutantOperator mop = MutantOperator
									.getMutantOperator(mutant_op
											.getOperatorName());

							int innerType = MutantOperator.GEN_NORMAL;
							switch (generationMode) {
							case MSG:
								innerType = MutantOperator.GEN_MSG;
								break;
							case WS:
								innerType = MutantOperator.GEN_NUJAVA;
								break;
							case SC:
								innerType = MutantOperator.GEN_NORMAL;
								break;
							case REACH:
								innerType = MutantOperator.GEN_EXP_REACH;
								break;
							
							case SCC:
								innerType = MutantOperator.GEN_STATE;
								break;
							
							}
							
							mainWriter = mop.getMutantCodeWriter(mutant_op,
									innerType);

							if (generationMode == GenerationType.WS) {
								subWriter = mop.getMutantCodeWriter(mutant_op,
										MutantOperator.GEN_MSG);
							}

						} catch (ParseTreeException e) {
							MuJavaLogger.getLogger().error(
									"Exception, during generating traditional mutants for the class "
											+ className);
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// end of measuring anlysis cost
						PerformanceElement mutantPointGenerionCost = PerformanceElement
								.getDifferentFromNow(initCost);
						gResultForMutantOperator
								.setAnalysisCost(mutantPointGenerionCost);
						gResultForMutantOperator.addTarget(jdtUnit
								.getResource().getFullPath().toString());

						// make a mutant from a change point
						if (mainWriter == null) {
							continue;
						}
						if (generationMode == GenerationType.WS
								&& subWriter == null) {
							continue;
						}

						createMutantAndCodeSet(mutant_op, jdtUnit, parseTree,
								mainWriter, subWriter,
								gResultForMutantOperator, monitor);

						// measuring generation cost
						PerformanceElement lastCost = PerformanceElement
								.getDifferentFromNow(initCost);
						gResultForMutantOperator.setCost(lastCost);

						MutantCostReport result = MutantCostReport
								.getCurrentResult();
						result.cumulateSubResult(gResultForMutantOperator);
					}
					monitor.worked(1);
				}
			}
		}
	}
}

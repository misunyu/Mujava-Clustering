package mujava;

import java.util.List;

import mujava.gen.IMutantFileGenerator;
import mujava.inf.IMutantInfo;
import mujava.util.MutantCompiler;

public class MutantManager {
	static MutantManager manager = null;

	// private HashMap<MuJavaProject, MutantTable> mutantMap = new
	// HashMap<MuJavaProject, MutantTable>();

	public static MutantManager getMutantManager() {
		if (manager == null)
			manager = new MutantManager();

		return manager;
	}

	private MuJavaProject muProject;

	private MutantCompiler mutantCompiler;

	private MutantPackager mutantPackager;

	private IMutantFileGenerator mutantFileGenerator;

	public MuJavaProject getMuJavaProject() {
		return muProject;
	}

	public MutantCompiler getMutantCompiler() {
		return mutantCompiler;
	}

	public IMutantFileGenerator getMutantFileGenerator() {
		return mutantFileGenerator;
	}

	public List<IMutantInfo> getMutantListBySameFile(IMutantInfo mutant) {
		// MuJavaTreeModel model = ProjectManager.getProjectManager()
		// .getMuJavaTreeModel();
		// return model.getMutantFilesBySameFile(mutant);
		return null;
	}

	public MutantPackager getMutantPackager() {
		return mutantPackager;
	}

	// public MutantWriter getMutantWriter() {
	// return mutantWriter;
	// }

	// public void setMutantFilesFromDefault(MuJavaProject proj) {
	// // read report predefied file
	// MutantTable table = new MutantTable(proj);
	// try {
	// table.open(monitor);
	// } catch (InvalidPropertiesFormatException e) {
	// e.printStackTrace();
	// } catch (FileNotFoundException e) {
	// // the project has not Mutant table
	// System.out.println("creating Mutant Table");
	// try {
	// table.save(monitor);
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// // set initial mutantFiles into table model
	// // List<MuJavaMutantInfo> mutants = table.getAllMutants();
	//
	// // set new table as the report table of given project
	// mutantMap.put(proj, table);
	//
	// // insert mutantFile into MuJavaTreeModel
	// // MuJavaTreeModel model = ProjectManager.getProjectManager()
	// // .getMuJavaTreeModel();
	// // for (MuJavaMutantInfo mutant : mutants) {
	// // model.addMutantFile(proj, mutant);
	// // }
	// }

	public void setMuJavaProject(MuJavaProject muProject) {
		this.muProject = muProject;
	}

	public void setMutantCompiler(MutantCompiler mutantCompiler) {
		this.mutantCompiler = mutantCompiler;
	}

	public void setMutantPackager(MutantPackager mutantPackager) {
		this.mutantPackager = mutantPackager;
	}

	public void setMutantFileGenerator(IMutantFileGenerator mutantWriter) {
		this.mutantFileGenerator = mutantWriter;
	}
}

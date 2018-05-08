package mujava.gen.seeker.classical;

import java.util.ArrayList;
import java.util.List;

import openjava.mop.FileEnvironment;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.VariableInitializer;
import openjava.ptree.util.VariableBinder;

public class ScannerForJSI extends VariableBinder {
	private List<FieldDeclaration> nonStatics = new ArrayList<FieldDeclaration>();
	private List<FieldDeclaration> finals = new ArrayList<FieldDeclaration>();

	public ScannerForJSI(FileEnvironment file_env) {
		super(file_env);
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		/**
		 * If a non-static variable can be changed to static varable unless 1)
		 * the variable is not final, or 2) the variable is final and
		 * initialized. In case of 2), the variable is assigned only one time at
		 * the declaration.
		 */
		if (!(p.getModifiers().contains(ModifierList.STATIC))) {
			if (!p.getModifiers().contains(ModifierList.FINAL))
				addNonStaticVariables(p);
			else {
				VariableInitializer varInit = p.getInitializer();
				if (varInit != null)
					addFinalVariables(p);
			}
		}
	}

	private void addFinalVariables(FieldDeclaration parseTree) {
		this.finals.add(parseTree);
	}

	private void addNonStaticVariables(FieldDeclaration parseTree) {
		this.nonStatics.add(parseTree);
	}

	public List<FieldDeclaration> getNonStatics() {
		return nonStatics;
	}

	public List<FieldDeclaration> getFinals() {
		return finals;
	}
}

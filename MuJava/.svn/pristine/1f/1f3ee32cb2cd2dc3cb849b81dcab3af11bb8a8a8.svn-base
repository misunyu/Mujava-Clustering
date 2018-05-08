////////////////////////////////////////////////////////////////////////////
// Module : JSI.java
// Author : Ma, Yu-Seung
// COPYRIGHT 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED.
////////////////////////////////////////////////////////////////////////////

package mujava.gen.seeker.classical;

import java.util.List;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.CompilationUnit;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ParseTree;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

/**
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005 by Yu-Seung Ma, ALL RIGHTS RESERVED
 * </p>
 * 
 * @author Yu-Seung Ma
 * @edit by swkim
 * @version 1.0
 */

public class JSI extends AbstractChangePointSeeker {

	private List<FieldDeclaration> finals = null;

	public JSI(ICompilationUnit originalSourceFile, FileEnvironment file_env) {
		super(file_env, originalSourceFile, "JSI");
	}

	/**
	 * All possible variable declarations are already scanned in ScannerForJSI
	 * and would be set in the method setScanner(). Thus do nothing for
	 * FieldDeclaration.
	 * 
	 * @see ScannerForJSI
	 */
	public void visit(FieldDeclaration p) throws ParseTreeException {
		super.visit(p);

		// * Original code *
		// if (!(p.getModifiers().contains(ModifierList.STATIC))) {
		// if (!p.getModifiers().contains(ModifierList.FINAL))
		// addChangePoint(new ChangePoint(p, currentMethod));
		// }
	}

	@Override
	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression left = p.getLeft();
		for (FieldDeclaration pTree : finals) {
			TypeName name = pTree.getTypeSpecifier();
			// if(getType(left).equals(OJClass.forName(name))
		}

		super.visit(p);
	}

	@Override
	public void visit(FieldAccess p) throws ParseTreeException {
		for (FieldDeclaration pTree : finals) {
			TypeName type = pTree.getTypeSpecifier();
		}
		super.visit(p);
		p.getName();
	}

	/**
	 * Non-Static에 대해서는 반드시 변환해야 하는 대상이고, final 변수인 것들 중에서는 참조만 하는 것들은 변환 가능하다.
	 * 현재는 변환 가능에대한 여부를 알수 없으므로 Visit함수가 호출된 이후 참조만 되는지, 변경되는지에 대해 확인한다.
	 * 
	 * @param scanner
	 */
	public void setScnner(ScannerForJSI scanner) {
		List<FieldDeclaration> list = scanner.getNonStatics();
		for (ParseTree cPoint : list) {
			this.addChangePoint(new ChangePoint(cPoint, null));
		}

		this.finals = scanner.getFinals();
	}

	@Override
	public void visit(CompilationUnit p) throws ParseTreeException {
		super.visit(p);

		// In case there is no reference for the given each final variable, the
		// variable is possible to change to static variable
		for (ParseTree cPoint : this.finals) {
			this.addChangePoint(new ChangePoint(cPoint, null));
		}
	}
}

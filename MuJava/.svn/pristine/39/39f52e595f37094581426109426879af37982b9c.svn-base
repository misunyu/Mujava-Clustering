package mujava.gen.writer.MSG;

import java.io.IOException;
import java.util.List;

import mujava.MutantOperator;
import mujava.gen.writer.MutantWriter;
import mujava.op.util.ChangePoint;
import mujava.plugin.wizards.generation.ClassState;
import mujava.plugin.wizards.generation.VariableState;
import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.ModifierList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;

public abstract class MutantMSGWriter extends MutantWriter {

	protected MutantMSGWriter(Environment env, String name) throws IOException {
		super(env, name, MutantOperator.GEN_MSG);
	}

	@Override
	public void visit(ClassDeclaration p) throws ParseTreeException {
		// 부모 로직을 분해하기 위해 직접 복사
		ClassDeclaration temp = currentClassDeclaration;
		currentClassDeclaration = p;

		//
		this.evaluateDown(p);
		writeClassDeclSignature(p);

		writeBeginNest();

		// Metamutant의 내의 특정 change point가 수행을 위한 Instance Variable 선언
		// {
		// pushNest(); // increase tab count;
		// // Change Point 수행 여부를 확인하는 array 생성
		// writeTab();
		// out.println("/** chage point decision array **/");
		// increaseLineNumber();
		//
		// // TODO array 이름 중복 확인 후 간단한 이름으로 변경
		// int MAX = 0;
		// for (ChangePoint cp : changePoints) {
		// try {
		// int value = Integer.parseInt(cp.getID());
		// if (value > MAX) {
		// MAX = value;
		// }
		// } catch (NumberFormatException e) {
		// }
		// }
		// writeTab();
		// out
		// .println("public static boolean[] __mujava__ExecuteChangePoint = new boolean[ "
		// + (MAX + 1) + " ];");
		// increaseLineNumber();
		//
		// out.println();
		// increaseLineNumber();
		//
		// popNest(); // decrease tab count;

		// writeTab();
		// out.print("static");
		// writeBeginNest();
		// pushNest();
		//
		// writeTab();
		// // TODO array 이름 중복 확인 후 간단한 이름으로 변경
		// int MAX = 0;
		// for (ChangePoint cp : changePoints) {
		// try {
		// int value = Integer.parseInt(cp.getID());
		// if (value > MAX) {
		// MAX = value;
		// }
		// } catch (NumberFormatException e) {
		// }
		// }
		// out.print("MSG.MutantMonitor.getInstance().setMinimumArraySize("
		// + MAX + ");");
		// increaseLineNumber();
		//
		// writeEndNest();
		// popNest();
		// }

		writeClassDeclBody(p);

		writeEndNest();

		this.evaluateUp(p);

		// 기존 visit class로 현재 작업 Class 이름을 복원
		currentClassDeclaration = temp;
	}

	private void writeBeginNest() {
		writeTab();
		out.println("{");
		increaseLineNumber();
	}

	private void writeClassDeclBody(ClassDeclaration p)
			throws ParseTreeException {

		MemberDeclarationList classbody = p.getBody();
		if (classbody.isEmpty()) {
			classbody.accept(this);
		} else {
			out.println();
			increaseLineNumber();
			pushNest();

			classbody.accept(this);

			popNest();
			out.println();
			increaseLineNumber();
		}

	}

	private void writeClassDeclSignature(ClassDeclaration p)
			throws ParseTreeException {
		super.evaluateDown(p);

		writeTab();

		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}

		if (p.isInterface()) {
			out.print("interface ");
		} else {
			out.print("class ");
		}

		String name = p.getName();
		out.print(name);

		TypeName[] zuper = p.getBaseclasses();
		if (zuper.length != 0) {
			out.print(" extends ");
			zuper[0].accept(this);
			for (int i = 1; i < zuper.length; ++i) {
				out.print(", ");
				zuper[i].accept(this);
			}
		} else {

		}

		TypeName[] impl = p.getInterfaces();
		if (impl.length != 0) {
			out.print(" implements ");
			impl[0].accept(this);
			for (int i = 1; i < impl.length; ++i) {
				out.print(", ");
				impl[i].accept(this);
			}
		} else {

		}

		out.println();
		increaseLineNumber();
		// }

		super.evaluateUp(p);

	}

	private void writeEndNest() {
		writeTab();
		out.println("}");
		increaseLineNumber();
	}
}
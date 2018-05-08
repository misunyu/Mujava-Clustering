package mujava.gen.seeker.traditional;

import mujava.gen.seeker.AbstractChangePointSeeker;
import mujava.op.util.ChangePoint;
import openjava.mop.FileEnvironment;
import openjava.ptree.BinaryExpression;
import openjava.ptree.ParseTreeException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

public class BORChangePointSeeker extends AbstractChangePointSeeker {
	public BORChangePointSeeker(ICompilationUnit sourceJavaElement, FileEnvironment file_env) {
		super(file_env, sourceJavaElement, "BOR");
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		// sub expression 의 type이 같으며, logical operator이 있는 경우에만 replace가 발생 가능.
		if (getType(p.getLeft()) == getType(p.getRight())) {
			int op_type = p.getOperator();
			if ((op_type == BinaryExpression.BITAND)
					|| (op_type == BinaryExpression.BITOR)
					|| (op_type == BinaryExpression.XOR)) {
				addChangePoint(new ChangePoint(p, currentMethod));
			}
		}
		
		super.visit(p);
	}
}

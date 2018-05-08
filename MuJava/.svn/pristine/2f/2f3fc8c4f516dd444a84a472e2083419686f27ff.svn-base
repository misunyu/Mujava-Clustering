package mujava;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public class test extends Composite {

	private Group group = null;
	private Text timeOut = null;
	private Label timeOutLabel = null;
	private Group dependGroup = null;
	private Tree tree = null;
	private Label label = null;
	private Text text = null;
	private Button button = null;

	public test(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout1 = new GridLayout();
		this.setLayout(gridLayout1);
		createGroup();
		createDependGroup();
		setSize(new Point(300, 200));
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		GridLayout gridLayout = new GridLayout(2, false);

		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);

		group = new Group(this, SWT.NONE);
		group.setText("Options");
		group.setLayoutData(gridData2);
		group.setLayout(gridLayout);

		timeOutLabel = new Label(group, SWT.NONE);
		timeOutLabel.setText("Time Out");
		timeOut = new Text(group, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		timeOut.setLayoutData(gridData);
	}

	/**
	 * This method initializes dependGroup
	 * 
	 */
	private void createDependGroup() {
		GridLayout gridLayout = new GridLayout(3, false);
		dependGroup = new Group(this, SWT.NONE);
		dependGroup.setText("TestCase Dependency");
		dependGroup.setLayout(gridLayout);
		dependGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tree = new Tree(dependGroup, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		tree.setLayoutData(gridData);

		label = new Label(dependGroup, SWT.NONE);
		label.setText("File Name :");
		text = new Text(dependGroup, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button = new Button(dependGroup, SWT.NONE);
		button.setText("Search");
	}
}

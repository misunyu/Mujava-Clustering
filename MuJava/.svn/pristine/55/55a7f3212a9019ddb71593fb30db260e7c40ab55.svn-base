package mujava.plugin.editor.mujavaproject;

import mujava.MuJavaProject;
import mujava.TestCaseType;
import mujava.gen.GenerationType;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProjectInformationComposite extends Composite {
	Text locationText;
	Text directoryText;
	Text getTypeText;
	Text testTypeText;

	public ProjectInformationComposite(Composite parent, int style) {
		super(parent, style);

		Color white = new Color(null, 255, 255, 255);
		this.setBackground(white);
		this.setBackgroundMode(SWT.INHERIT_FORCE);

		GridLayout layout = new GridLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = 1;
		// GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		// gd2.horizontalSpan = 2;

		Label label = new Label(this, SWT.SHADOW_OUT);
		label.setBackground(white);
		label.setText("Location");
		locationText = new Text(this, SWT.SINGLE | SWT.BORDER);
		locationText.setEditable(false);
		locationText.setLayoutData(gd1);
		locationText.setText("");

		label = new Label(this, SWT.SHADOW_OUT);
		label.setBackground(white);
		label.setText("Generated mutant directory");
		directoryText = new Text(this, SWT.SINGLE | SWT.BORDER);
		directoryText.setEditable(false);
		directoryText.setLayoutData(gd1);
		directoryText.setText("");

		label = new Label(this, SWT.SHADOW_OUT);
		label.setBackground(white);
		label.setText("Generation Type");
		getTypeText = new Text(this, SWT.SINGLE | SWT.BORDER);
		getTypeText.setEditable(false);
		getTypeText.setLayoutData(gd1);
		getTypeText.setText("");

		label = new Label(this, SWT.SHADOW_OUT);
		label.setBackground(white);
		label.setText("TestCase Type");
		testTypeText = new Text(this, SWT.SINGLE | SWT.BORDER);
		testTypeText.setEditable(false);
		testTypeText.setLayoutData(gd1);
		testTypeText.setText("");

		white.dispose();
	}

	protected String getDirectory() {
		return directoryText.getText();
	}

	protected void setDirectory(String directoryText) {
		this.directoryText.setText(directoryText);
	}

	protected String getFileLocation() {
		return locationText.getText();
	}

	protected void setFileLocation(String locationText) {
		this.locationText.setText(locationText);
	}

	protected String getMainClass() {
		return getTypeText.getText();
	}

	protected void setGenerationType(GenerationType type) {
		this.getTypeText.setText(type.getLabel());
	}

	public void setProjectInformation(MuJavaProject project) {

		if (project != null) {
			this.setGenerationType(project.getGenerationWay());
			this.setTestCaesType(project.getTestCaseType());
			this.setFileLocation(project.getFileName());
			this.setDirectory(project.getMutantDirectory());
		} else {
			this.setGenerationType(GenerationType.NONE);
			this.setTestCaesType(TestCaseType.NONE);
			this.setFileLocation("");
			this.setDirectory("");
		}
	}

	private void setTestCaesType(TestCaseType type) {
		this.testTypeText.setText(type.getLabel());
	}

}

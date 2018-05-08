package mujava.inf;

import java.io.Serializable;
import java.util.Properties;

import mujava.MuJavaProject;

public interface IMutantInfo extends Serializable {
	public final static int CLASS = 2;

	public static final int Equivalent = 5;

	public final static int EXCEPTIONAL = 3;

	static public final String FILENAME = "Mutant.prop";

	public static final int NotAvailable = 1;

	public static final int NotKilled = 0;

	public static final int SKilled = 4;

	public final static int TRADITIONAL = 1;

	public static final int VWKilled = 2;

	public static final int WKilled = 3;

	public int getChangeLocation();

	public String getChangeLog();

	public abstract String getMuJavaProject();

	// public abstract String getMutantFileName();

	public abstract String getMutantID();

	public abstract String getMutationOperatorName();

	public abstract int getMutantType();

	// public abstract String getOriginalClass();

	// public abstract String getOriginalFileName();

	public Properties getProperties();

	public abstract void setChangeLocation(int line_num);

	public abstract void setChangeLog(String log_str);

	public void setFirstSubTypeOperator(int times);

	public void setSizeOfSubID(int i);

	// public abstract void setMuJavaProject(String projectName);
	public abstract void setMuJavaProject(MuJavaProject project);

	public abstract void setMutantID(String id);

	public abstract void setMutationOperatorName(String name);

	public void setMutantType(int type);

	// public abstract void setOriginalClass(String string);

	public void setSubTypeOperator(int operator);

	// public abstract void setState(int state);

	// public abstract void setWrapperClass(String packageClassName);

	// public abstract void setWrapperFileName(String mutantFileName);

	// public abstract boolean isKilled();
	//
	// public abstract void setKilled(boolean b);

}
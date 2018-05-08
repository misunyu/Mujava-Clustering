package mujava.inf;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IMuJavaProject {

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#delete()
	 */
	public abstract void delete();

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getMainClass()
	 */
	public abstract String getMainClass();

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getMutantDirectory()
	 */
	public abstract String getMutantDirectory();

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getName()
	 */
	public abstract String getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#getNote()
	 */
	public abstract String getNote();

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#open()
	 */
	public abstract void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#save()
	 */
	public abstract void save(IProgressMonitor monitor) throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setMainClass(java.lang.String)
	 */
	public abstract void setMainClass(String mainClass);

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setMutantDirectory(java.lang.String)
	 */
	public abstract void setMutantDirectory(String mutantDirectory);

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setName(java.lang.String)
	 */
	public abstract void setName(String name);

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#setNote(java.lang.String)
	 */
	public abstract void setNote(String text);

	/*
	 * (non-Javadoc)
	 * 
	 * @see mujava.IProject#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);

	// /* (non-Javadoc)
	// * @see mujava.IProject#makeFileSystem()
	// */
	// public void makeFileSystem() {
	// File f = new File(getMutantDirectory());
	// f.mkdirs();
	// }
	public abstract void setMutantTableFileName(String name);

	public abstract String getMutantTableFileName();

}
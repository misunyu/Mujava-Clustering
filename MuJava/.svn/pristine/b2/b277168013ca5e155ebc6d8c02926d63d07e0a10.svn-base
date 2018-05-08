package kaist.selab.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

public abstract class PropertyFile {
	String fileComment = new String();
	protected String fileName = new String();
	protected Properties prop = new Properties();
	protected IResource resource = null;

	public void delete() {
		if (fileName != null) {
			File file = new File(fileName);
			file.delete();
		}
	}

	public abstract void open(IProgressMonitor monitor)
			throws InvalidPropertiesFormatException, IOException;

	public abstract void save(IProgressMonitor monitor) throws IOException;

	protected String getFileComment() {
		return fileComment;
	}

	public String getFileName() {
		return fileName;
	}

	public Properties getProperties() {
		return prop;
	}

	protected void openProperty() throws InvalidPropertiesFormatException,
			IOException {
		String str = getFileName();
		File file = new File(str);
		FileInputStream fis = new FileInputStream(file);
		prop.loadFromXML(fis);
		fis.close();
	}

	protected ByteArrayOutputStream saveTemporary() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		prop.storeToXML(bos, fileComment);
		bos.flush();
		return bos;
	}

	protected int getIntValue(String key) {
		return Helper.getIntValueFromProperties(prop, key);
	}

	protected String getValue(String key) {
		return Helper.getValueFromProperties(prop, key);
	}

	protected long getLongValue(String key) {
		return Helper.getLongValueFromProperties(prop, key);
	}

	protected boolean getBooleanValue(String key) {
		return Helper.getBooleanValueFromProperties(prop, key);
	}

	protected void setFileComment(String fileComment) {
		this.fileComment = fileComment;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}

	public void setProperties(Properties prop) {
		this.prop = prop;
	}

	protected void setValue(String key, String value) {
		this.prop.setProperty(key, value);
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public void makeDirectories() {
		File file = new File(getFileName());
		if (file.exists())
			return;

		IPath path = new Path(file.getAbsolutePath());
		path = path.removeLastSegments(1);
		File dir = path.toFile();
		dir.mkdirs();
	}

	protected void createParentFolder(IPath path) {
		if (path.segmentCount() < 2)
			return;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 2; i < path.segmentCount(); i++) {
			IPath newPath = path.uptoSegment(i);
			IFolder folder = root.getFolder(newPath);
			try {
				if (!folder.exists())
					folder.create(true, false, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDirectory() {
		if (this.getFileName() != null) {
			IPath path = new Path(this.getFileName());
			path = path.removeLastSegments(1);

			return path.toOSString();
		}

		return null;
	}

}

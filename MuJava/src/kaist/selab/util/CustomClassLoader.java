package kaist.selab.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomClassLoader extends ClassLoader {

	List<String> searchDirectory;

	public CustomClassLoader() {
		this(null, new ArrayList<String>());
	}

	public CustomClassLoader(ClassLoader loader, List<String> dirs) {
		assert (dirs != null);
		searchDirectory = dirs;		
	}

	public CustomClassLoader(List<String> dirs) {
		this(null, dirs);
	}

	public CustomClassLoader(String specifiedDirectory) {
		this(null, null);
		searchDirectory = new ArrayList<String>();
		searchDirectory.add(specifiedDirectory);
		double b =4;
		double d = Math.abs( b=b*b );
		
	}

	@Override
	protected URL findResource(String name) {
		for (Iterator<String> iter = searchDirectory.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			URL url;
			try {
				url = new URL("file:" + element + Helper.directorySeparator + name);
				File file = new File(url.getFile());
				if(file.exists())
					return url;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} 
		return super.findResource(name);
	}

	private byte[] getClassData(String name, String directory)
	throws FileNotFoundException, IOException {
		String filename = name.replace('.', File.separatorChar) + ".class";

		// Create a file object relative to directory provided
		File f = new File(directory, filename);

		// Get stream to read from
		FileInputStream fis = new FileInputStream(f);

		BufferedInputStream bis = new BufferedInputStream(fis);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			int c = bis.read();
			while (c != -1) {
				out.write(c);
				c = bis.read();
			}
		} catch (IOException e) {
			return null;
		}
		return out.toByteArray();
	}

	@Override
	public URL getResource(String name) {
		URL url = findResource(name); 

		return (url == null) ? super.getResource(name) : url;
	}

	@SuppressWarnings("unchecked")
	public synchronized Class loadClass(String name)
	throws ClassNotFoundException {
		// See if type has already been loaded by
		// this class loader
		Class result = findLoadedClass(name);
		if (result != null) {
			// Return an already-loaded class
			return result;
		}

		try {
			result = findSystemClass(name);
			return result;
		} catch (ClassNotFoundException e) {
			// keep looking
		}
		
		try {
			result = super.loadClass(name);
			return result;
		} catch (ClassNotFoundException e) {
		}

		for (String dir : searchDirectory) {
			try {
				byte[] data = getClassData(name, dir);
				result = defineClass(name, data, 0, data.length);
				if (result != null)
					return result;
			} catch (FileNotFoundException e) {
				//search next
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		throw new ClassNotFoundException(name);
	}
}

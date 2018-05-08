package interstate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class InterState {

	static int fileNum = 0, errorFile = 0, notDeletedFile = 0;;
	static int clusterNum = 0;
	static Hashtable<String, String> mutantOriginalTable = new Hashtable<String, String>();
	//static Vector<Vector> clusters = null;
	static BufferedWriter outputLog = null;
	
	public static void main(String args[]) {
				
		//String testDir = "/Users/misunyu/Documents/runtime-EclipseApplication/JUnitRegExpTest/Tests";
		//String stateDir = "/Users/misunyu/Documents/runtime-EclipseApplication/JUnitRegExpTest/States/scctest";
		//String stateDir = "/Users/misunyu/Documents/runtime-EclipseApplication/teststate";
		//String testDir = "/Users/misunyu/Documents/runtime-EclipseApplication/JUnitTriTest/Tests";
		//String stateDir = "/Users/misunyu/Documents/runtime-EclipseApplication/JUnitTriTest/States/scctest";

		//String testDir = "C:\\runtime-EclipseApplication\\JUnitNum4JTest\\Tests";
		//String stateDir = "C:\\runtime-EclipseApplication\\JUnitNum4JTest\\States\\scctest";

		//String testDir = "D:\\runtime-EclipseApplication\\JUnitTriTest\\Tests";
		//String stateDir = "D:\\runtime-EclipseApplication\\JUnitTriTest\\States\\scctest";
		String testDir = "D:\\runtime-EclipseApplication\\JUnitRegExpTest\\Tests";
		String stateDir = "D:\\runtime-EclipseApplication\\JUnitRegExpTest\\States\\scctest";
		//String testDir = "G:\\JUnitRegExpTest\\Tests";
		//String stateDir = "G:\\JUnitRegExpTest\\States\\scctest";
	
		//String stateDir = "C:\\runtime-EclipseApplication\\teststate";
		//String testDir = "C:\\runtime-EclipseApplication\\JUnitJaxen\\Tests";
		//String stateDir = "C:\\runtime-EclipseApplication\\JUnitJaxen\\States\\scctest";

		try {
			outputLog = new BufferedWriter(new FileWriter(stateDir + File.separator + "clustering_result.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		clusterMutantStates(stateDir, testDir);
		
		int wks = checkSizeOfWeaklyKilledMutants(stateDir);
		
		printLog("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		printLog("# of weakly killed mutants: " + wks);

		float eff = (float)clusterNum /(float)fileNum * (float)100.0;
		printLog("fileNum: " + fileNum + ", errorFile: " + errorFile + ", notDeletedFile: " + notDeletedFile);
		printLog("# of clusters: " + clusterNum  + " efficiency: " + eff);
	   // System.out.println("# of false clusters: " + falseClusters);

	//	test1();
		System.out.println(" -- The End ---");
		
		try {
			outputLog.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printLog(String str)
	{
		System.out.println(str);
		try {
			outputLog.write(str + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
/*	
	private static void test1()
	{
		Vector vv1 = new Vector();
		Vector vv2 = new Vector();
		
		Vector t1 = new Vector();
		Vector t2 = new Vector();
		
		Vector v1 = new Vector();
		Vector v2 = new Vector();
				
		//String str1 = "a";
		//String str2 = "a";
		StringBuffer str1 = new StringBuffer("a");
		StringBuffer str2 = new StringBuffer("a");
		
		int a = 0, b = 0, c = 0, d = 0;
		
		t1.add(a);
		t1.add(str1.toString());
		t2.add(b);
		
		v1.add(c);
		v1.add(str2.toString());
		v2.add(d);
		
		vv1.add(t1);
		vv1.add(t2);
		
		vv2.add(v1);
		vv2.add(v2);
		
		System.out.println("Equals: " + equals(t1, v1));	
		System.out.println("Equals: " + equals(vv1, vv2));	
		//for(int i = 0; i < 2; i++) {
		//	System.out.println("Equals: " + equals(vv1.elementAt(i), vv2.elementAt(i)));
		//}
	}
	*/
	
	
	public static int checkSizeOfWeaklyKilledMutants(String weaklyKilledMutantsDir)
	{
		HashSet weaklyKilledMutants = new HashSet();
		File dir = new File(weaklyKilledMutantsDir);
		for (final File fileEntry : dir.listFiles()) {
			 if(fileEntry.getName().startsWith("weakly_killed")) {
		
				FileInputStream in = null;
			    HashSet wkms = null;
			    
				try {
					in = new FileInputStream(fileEntry);
				    ObjectInputStream s = new ObjectInputStream(in);
				    wkms = (HashSet)s.readObject();
					in.close();
					weaklyKilledMutants.addAll(wkms);
					
					//System.out.println("loading Weakly killed mutants = " + in.toString());
					//System.out.println("HashSet Size = " + weaklyKilledMutants.size());
				
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		
		return weaklyKilledMutants.size();
		
	}
	
	
	/*
	public static Vector testSet = new Vector();
	private static String test()
	{
		int a = 0, b = 0, c = 0;
		int t = 0;
	
//		if (t == 0) {
 //           if (a + b < c || a + c < b || b + c > a) {
		testSet.add(a);
		testSet.add(b);
		testSet.add(c);
		testSet.add(t);
		saveTestState(testSet, "test1.state");
	//	 }
//		}
		
		testSet.clear();
		t = 0;
		
//		if (t == 0) {
  //          if (a + b < c || a + c < b || b + c >= a) {
		testSet.add(a);
		testSet.add(b);
		testSet.add(c);
		testSet.add(t);
		saveTestState(testSet, "test2.state");
//		 }
//		}
		
		return "a";
	}
	*/
	
	
	static void printValueOfClusterMembers(Vector<Vector> clusters)
	{
		//mutantOriginalTable
		String str1 = "{";		
		//System.out.print("{");
		for(int i = 0; i < clusters.size(); i++) {
			Vector cluster = clusters.elementAt(i);
			//System.out.print("{");
			//str.concat("{");
			String str2 = "{";
			for(int j = 0; j < cluster.size(); j++) {
				MutantStateInfo m = (MutantStateInfo) cluster.elementAt(j);
				String mname = m.getName();
				String result =  mutantOriginalTable.get(mname);
				
				//System.out.print(isKilled + ",");
				str2 +=(result + ",");
				
			}
			//System.out.print("}");
//			if(str2.contains("false") && str2.contains("true"))
//				falseClusters++;
			
			str2 += "}";
			str1 += str2;
		}
		//System.out.println("}");
		str1 += "}";
		printLog(str1);
	}


	public static void addURL(String dir)
	{
		 File f = new File(dir);
		 URI u = f.toURI();
		 URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		 Class<URLClassLoader> urlClass = URLClassLoader.class;
		 Method method;
		try {
		
			method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[]{u.toURL()});

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void clusterMutantStates(String dir, String testDir)
	{
		printLog("[clusterMutantStates] dir = " + dir + " testDir = " + testDir);
		
		addURL(testDir);
		
				
		BufferedReader oriMutantResult = null;
		try {
			oriMutantResult = new BufferedReader(new FileReader(dir + File.separator + "original_mutant_result.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		try {
			
			while((line  = oriMutantResult.readLine()) != null) {
				try {
					String mid = line.substring(0, line.indexOf('@'));
					String mvalue = line.substring(line.indexOf('@') + 1, line.length());
					//mutantOriginalTable.put(mid, mvalue); 
					mutantOriginalTable.put(mid, mvalue.substring(0, 4)); 
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					//System.out.println("line = " + line);
					//e.printStackTrace();
					continue;
				}
			}
			oriMutantResult.close();
		
			printLog("# of states: " + mutantOriginalTable.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File path = new File(dir);
		for (final File fileEntry : path.listFiles()) {
			 if(fileEntry.isDirectory() && fileEntry.getName().startsWith("ResultState")) {			 
				 clusterStatesWithLine(fileEntry);
			 }			 
		 }
		 
	//	float eff = (float)clusterNum /(float)fileNum * (float)100.0;
	 //   System.out.println("fileNum: " + fileNum + " clusterNum: " + clusterNum  + " clustring Result: " + eff);
	
	}
	
	public static Vector<Vector> clusterStatesWithoutLine(HashSet<MutantStateInfo> stateList) 
	{		
		Vector<Vector> clusters = new Vector();
	    Iterator elements = stateList.iterator();
		clusters.add(new Vector());
		 while(elements.hasNext()){
	    	MutantStateInfo mstate1 = (MutantStateInfo)elements.next();
	    	int size1 = mstate1.getState().size();
	    	
	    	boolean found = false;
	    	for(int i = 0; i < clusters.size(); i++) {
	    		Vector<MutantStateInfo> cluster = clusters.elementAt(i);
	    		if(cluster.size() == 0) {
	    			cluster.addElement(mstate1);
	    			found = true;
	    		} else {
	    			MutantStateInfo mstate2 = cluster.firstElement();
	    			int size2 = mstate2.getState().size();
	    			//if(mstate1._line.equals("56")) {
	    			//	System.out.println("a");
	    			//}
	    			
	    			if(size1 == size2) {
		    			if(equals(mstate1.getState(), mstate2.getState())) {
		    				mstate1._state = null;
		    				cluster.addElement(mstate1);
		    				found = true;
		    			}
	    			}
	    		}    		
	    	}
	    	
	    	if(!found) {
	    		Vector<MutantStateInfo> nCluster = new Vector();
	    		nCluster.addElement(mstate1);
	    		clusters.addElement(nCluster);
	    	}
	    }

	   // System.out.println("cluster size = " + clusters.size() + " clusters = " + clusters);
	  //  System.out.println("sameLineClusterSet.keySet() = " + sameLineClusterSet.keySet());
	    return clusters;
	}
	/*
	public static Vector<Vector> clusterStatesWithoutLine(HashSet<MutantStateInfo> stateList) 
	{		
		Vector<Vector> clusters = new Vector();
	    Iterator elements = stateList.iterator();
		clusters.add(new Vector());
		 while(elements.hasNext()){
	    	MutantStateInfo mstate1 = (MutantStateInfo)elements.next();
	    	
	    	boolean found = false;
	    	for(int i = 0; i < clusters.size(); i++) {
	    		Vector<MutantStateInfo> cluster = clusters.elementAt(i);
	    		
	    		if(cluster.size() == 0) {
	    			cluster.addElement(mstate1);
	    			found = true;
	    		} else {
	    			MutantStateInfo mstate2 = cluster.firstElement();
	    			if(equals(mstate1.getState(), mstate2.getState())) {
	    				mstate1._state = null;
	    				cluster.addElement(mstate1);
	    				found = true;
	    			}
	    		}    		
	    	}
	    	
	    	if(!found) {
	    		Vector<MutantStateInfo> nCluster = new Vector();
	    		nCluster.addElement(mstate1);
	    		clusters.addElement(nCluster);
	    	}
	    }

	   // System.out.println("cluster size = " + clusters.size() + " clusters = " + clusters);
	  //  System.out.println("sameLineClusterSet.keySet() = " + sameLineClusterSet.keySet());
	    return clusters;
	}
	*/
	
	
	//���� �������� ������ ���� ������ clustering
	public static void clusterStatesWithLine(File dir) 
	{
		printLog("=====================================");
		printLog("clusterStates dir = " + dir.getName());
		
		//mutantOriginalTable
		Hashtable <String, HashSet> sameLineStateTable = new Hashtable();
		Vector<Vector> clusters = null;

		String id = "";
		String fName = "";
		for (final File file : dir.listFiles()) {
			
	    	fName = file.getName();
			
	    	//if(fName.contains("ROR_907826569_3_13")) {
	    	//	System.out.println("file = " + fName);
	    	//}

	       // String[] tokens = fName.split("_|\\.");
	       // System.out.println("file = " + dir.getPath() + File.separator + fName);
	    	if(fName.endsWith("_")) {
	    		notDeletedFile++;
	    		continue;
	    	}
	    	
	        Vector state = loadState(dir.getPath() + File.separator + fName);
	        if(state == null) {
				System.out.println("*INFINITE LOOP*");
				errorFile++;
				continue;
	        }

	        MutantStateInfo mstate = new MutantStateInfo(fName.substring(0, fName.indexOf('-')), dir.getName().replace("ResultStates.", "") + "_" + fName.substring(fName.indexOf('-') + 1, fName.length()), state);
	        
	        HashSet set = (HashSet)sameLineStateTable.get(mstate.getLine());
	        if( set == null) {
	        	set = new HashSet();	        	
	        }	
	        sameLineStateTable.put(mstate.getLine(), set);
	        set.add(mstate);	
	        
	        fileNum++;
	    }
	
			
	    Enumeration<String> lines = sameLineStateTable.keys();
	    while(lines.hasMoreElements()) {
	    	String line = lines.nextElement();
	    	HashSet<MutantStateInfo> elements = sameLineStateTable.get(line);
	    	clusters = clusterStatesWithoutLine(elements); 
	    		
	    	printLog("-------------------------------------------");
	    	printLog("line = " + line + ", elements = " + elements.size());
	    	printLog("clusters num: " + clusters.size() + " => " + clusters);
	    	clusterNum += clusters.size();
	    	
			 printValueOfClusterMembers(clusters);
			 clusters.clear();
	    }
	  
	  //  System.out.println("sameLineStateTable = " +  sameLineStateTable);
	  //  System.out.println("sameLineClusterSet.keySet() = " + sameLineClusterSet.keySet());
	    
	    //return clusters;
	}

	
	public static void clusterStates (String targetDir, String testName)
	{
		
		File dir = new File(targetDir + "." + testName);
	    for (final File fileEntry : dir.listFiles()) {
	       
	    	printLog(fileEntry.getName());
	    }
	
	}
	
	
	public static void moveStateFiles (String targetDir, String toSubDir)
	{
		String newDirStr = targetDir  + "." + toSubDir + File.separator;
		//System.out.println("[moveStateFiles] "+ targetDir + " to " + newDirStr);

        File dir = new File(targetDir + File.separator);  
        File newDir = new File(newDirStr);
        
        if(!dir.isDirectory()) {
        	System.out.println(targetDir + " is not a directory.");
        	return;
        }
        
        if(!newDir.isDirectory()) {    
        	dir.renameTo(newDir);  
        	newDir.setWritable(true,  false);
        	
        } else {       	
          
        	try {
        		         		   
               	FileChannel source = null;
            	FileChannel destination = null;       		   
            	for (final File file : dir.listFiles()) {
            	
            		source = new FileInputStream(targetDir + File.separator + file.getName()).getChannel();
            		destination = new FileOutputStream(newDirStr + file.getName()).getChannel();
					destination.transferFrom(source, 0, source.size());
					//System.out.println("source = " + file.getName());
					
					source.close();
					destination.close();
					file.delete();
										
            	}
        		   
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
         
        }

   
        dir.delete();
	}
	
	public static void saveState(Vector obj, String directory, String mutantLine, String mid, String exeLine) 
	{
		//System.out.println("saveState");
		boolean isSameMutantID = false;
		boolean isSameLineNMutantID = false;
		File prevFile = null;
		String newfilename = null;
		int prevObjCnt = 1;
		
	    obj.addElement(exeLine);

		File path = new File(directory);
		if(!path.exists()){
			path.mkdir();
			path.setWritable(true, false);			
		} 
		
		newfilename = directory+ File.separator + mutantLine + "-" + mid;
		//String justName = line + "-" + mid;
		String prefix = null;
		for (final File fileEntry : path.listFiles()) {
			 String pname = fileEntry.getName();
			 if(pname.endsWith(mid)) {
				 isSameMutantID = true;
				 //prevfilename = fileEntry.getAbsolutePath();
				 prevFile = fileEntry;
				 prefix = prevFile.getName().substring(0, prevFile.getName().indexOf('-'));
				 //if(!pname.endsWith(justName)) {
				 if(prefix.contains(mutantLine)) {
					 isSameLineNMutantID = true;
					 newfilename = prevFile.getAbsolutePath();
				 } else {
					 newfilename = directory+ File.separator + pname.substring(0, pname.indexOf('-')) + "_"+ mutantLine + "-" + mid;
					 //isSameLineNMutantID = false;
				 } 
				 break;
			 }
		}
			
		ObjectOutputStream out = null;
		FileOutputStream outfile = null;
		try {
			
			if(isSameMutantID) {
				
				if(isSameLineNMutantID)
					outfile = new FileOutputStream(newfilename + "_");
				else
					outfile = new FileOutputStream(newfilename);
				
				out = new ObjectOutputStream(outfile);
				
				FileInputStream infile = new FileInputStream(prevFile.getAbsolutePath());
				ObjectInputStream ins = new ObjectInputStream(infile);
			    int objCnt = ins.readInt();	
			    objCnt++;
			    
			    out.writeInt(objCnt);
			    
			    for(int i = 0; i < objCnt-1; i++) {
			    	
			    	Object prevobj = ins.readObject();
					out.writeObject(prevobj);
			    }		
			    out.writeObject(obj);
			    
			    out.flush();
			    out.close();
			    ins.close();
			    
			    infile.close();
			    outfile.close();
			    
				// File prevFile = new File(prevfilename);
			   prevFile.delete();

			    if(isSameLineNMutantID) {
			    	
			    	File newFile = new File(newfilename + "_");
			    	//newFile.renameTo(prevFile);
				   newFile.renameTo(new File(newfilename));
			    	//System.out.println("rename file: " + newfilename);
				    	
			    }
			    
			} else {			    
				outfile = new FileOutputStream(newfilename);
				out = new ObjectOutputStream(outfile);
				out.writeInt(prevObjCnt);
			    out.writeObject(obj);	
			    
			    out.flush();
			    out.close();
			    outfile.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			/*
			if(prevFile != null)
				prevFile.delete();
			
		    if(isSameLineNMutantID) {	
		    	File newFile = new File(newfilename + "_");
		    	newFile.delete();
		    }
			*/
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//-Xmx256m -Xms256ms
	public static Vector loadState(String fName)
	{
		//System.out.println("loadState file: " + fName);
	    FileInputStream file;
	    Vector allObj = new Vector();
	    
		try {
			Vector obj = null;
			int objCnt = 0;
			
			file = new FileInputStream(fName);
		    //System.out.println("filename: " + fName );

		    ObjectInputStream in = new ObjectInputStream(file);
		    objCnt = in.readInt();
		    //System.out.println("objCnt: " + objCnt);
		    for(int i = 0; i < objCnt; i++) {
		    	obj = (Vector)in.readObject();
		    	allObj.add(obj);
		    }
			in.close();
			file.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StackOverflowError e) {
			e.printStackTrace();
			return null;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			//infinite loop
			e.printStackTrace();
			return null;
			//System.exit(0);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    return allObj;
	}
	
	
	 public static boolean equals(Object obj1, Object obj2) {
		   return EqualsBuilder.reflectionEquals(obj1, obj2);
	}
	 	

}

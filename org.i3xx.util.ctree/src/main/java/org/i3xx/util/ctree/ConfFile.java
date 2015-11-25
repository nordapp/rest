/* robots=none */

/*
 * @(#)ConfFile2.java	1.0, 15.04.2000
 *
 * Copyright 1996-2000 by I.D.S. DialogSysteme GmbH
 * Fraunhoferstrasse 3, 25524 Itzehoe, Germany
 * All rights reserved.
 */

package org.i3xx.util.ctree;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 *	Diese Klasse liest eine Konfigurationsdatei und erstellt eine Tabelle.
 *	Der erste Wert in der Datei ist der Schl�ssel der Tabelle, die folgenden
 *	Werte der Zeile werden als Parameter-Stringarray in der Tabelle abgelegt.
 *	Die einzelnen Parameter werden in der Parameterdatei durch Leerzeichen
 *	getrennt. Kommentarzeilen beginnen mit '#'. Diese werden ebenso wie Leer-
 *	zeilen ignoriert.
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 *
 * @author	Stefan Hauptmann
 * @version 1.0, 15.04.2000
 * @since	interconnect 1.0
 */
@Deprecated
public class ConfFile {

	public static final String separator = " \t";
	
	//private static final Logger logger = LoggerFactory.getLogger(ConfFile.class);
	
	private ConfParser cparser;
	private boolean subdir;
	
	/**
	 * Constructs an <code>ConfFile2</code>. Reads the elements of the given
	 * configuration file and stores them into a table.
	 * @since	interconnect 1.0
	 */
	public ConfFile( ConfParser parser)
	{
		subdir = false;
		cparser = parser;
	}
	
	/**
	 * @param	fileName   the name and path of the configuration file.
	 * @since	interconnect 1.0
	 */
	public void parse(String fileName) throws IOException
	{
		//per Definition mu� die erste Datei eine Konfigurationsdatei sein
		//und der Pfad mu� absolut angegeben sein.
		String content = getConf(fileName, ".*")[0];
		
		LineNumberReader in = new LineNumberReader(new StringReader(content));
		parse(in, fileName, null);
	}
	
	/**
	 * @param	fileName   the name and path of the configuration file.
	 * @since	interconnect 1.0
	 */
	public void parse(LineNumberReader in, String fileName, String nodePrefix) throws IOException
	{
		String line;
		StringTokenizer parser = null;
		String key = null;
		String param = null;
		
		//Der Parameter fileName beinhaltet den Pfad der zugrundeliegenden Datei.
		//Von diesem Pfad aus werden die relativ gesetzten Include-Dateien
		//gelesen (./ und ../). Ist der 2. Parameter aus getFile(..) null,
		//so mu� die �bergebene Datei eine absolute Pfadangabe enthalten.

		do{
			line = in.readLine();

			if (line==null){
				//
			}else if (line.startsWith("#")){
				//Kommentarzeilen ignorieren
				
				//Weitere Dateien einbinden
				if(line.startsWith("#include")){
					String tmp = null;
					String prefix = null;
					String[] arr = new String[0];
					
					if(line.startsWith("#include-as")){
						tmp = line.substring(11).trim();
						//1st param is prefix
						int i = tmp.indexOf(" ");
						prefix = tmp.substring(0, i);
						//2nd param is regexp for files
						tmp = tmp.substring(i).trim();
						//get path (axis) and regexp of the include
						arr = getFileInfo(tmp, fileName);
					}else{
						tmp = line.substring(8).trim();
						//get path (axis) and regexp of the include
						arr = getFileInfo(tmp, fileName);
					}
					
					//get all filenames at the given path (axis)
					Vector<String> vIn = getFiles(arr[0]);
					for(int i=0; i<vIn.size(); i++){
						//absolute filename
						String fn = vIn.elementAt(i);
						//regular expression of the include
						String re = arr[1];
						
						//get all matching content strings
						String[] content = getConf(fn, re);
						
						//parse whole content
						for(int j=0;j<content.length;j++){
							ConfFile c = new ConfFile(cparser);
							c.parse(new LineNumberReader(
									new StringReader(content[j])), fn, prefix);
						}
					}
				}
			}else if(line.equals("")){
				//Leerzeilen ignorieren
			}else{
				parser = new StringTokenizer(line, separator+"\n\r");
				//Erster Token ist Schl�ssel
				if(parser.hasMoreTokens())
					key = parser.nextToken();
				//Zweiter Token ist Wert
				if(parser.hasMoreTokens())
					param = parser.nextToken();
				else
					param = "";
				//Weitere Token zu Wert
				while(parser.hasMoreTokens()){
					param += " " + parser.nextToken();
				}
				if(nodePrefix != null){
					key = nodePrefix + "." + key;
				}
				//Insert to confTree
				cparser.addconf(key, param);
			}//fi
		}while (line!=null);
		
		in.close();
	}

	/**
	 * Returns the filename
	 * If the path starts with "." or ".." it is set relative to the currentFile
	 *
	 * @return	the filename and the regexp
	 *
	 * @param	fileName  the filename
	 * @param	startFile the name of the first conf file
	 * @since	officebase4.01
	 */
	private String[] getFileInfo(String fileName, String currentFile) throws IOException {
		
		if((fileName==null)||fileName.equals(""))
			throw new IOException("No valid filename (filename is null or empty string).");
		
		//temp var
		String temp = fileName.trim();
		String regexp = ".*"; //match all strings
		
		//get the filename
		if(temp.startsWith("\"")){
			int n = temp.indexOf("\"", 1);
			if(n>-1){
				fileName = temp.substring(1, n);
				temp = temp.substring(n+1);
			}
		}else{
			int n = temp.indexOf(" ", 1);
			if(n>-1){
				fileName = temp.substring(0, n);
				temp = temp.substring(n);
			}
		}
		
		//get the file filter
		if(temp.indexOf(" ") > -1){
			int n = temp.indexOf(" ");
			regexp = temp.substring(n+1, temp.length());
		}
		
		//takes effort when a filepath is given.
		if(fileName.endsWith("/**")) {
			subdir = true;
			fileName = fileName.substring(0, fileName.length()-3);
		}else{
			subdir = false;
		}
		
		boolean absolute = false;
		
		//file url (always absolute)
		if(fileName.startsWith("file:///")){
			absolute = true;
			fileName = fileName.substring(8); 
		}
		
		fileName = fileName.replace('/', File.separatorChar);
		
		//absolute path (windows and unix)
		if( (fileName.length()>=3 && fileName.substring(1,3).equals(":"+File.separator)) || 
				fileName.startsWith(File.separator)){
			absolute = true;
		}
		
		//absolute path: return
		if( (currentFile == null) || absolute ){
			if(!(new File(fileName)).exists())
				throw new IOException("The file '"+fileName+"' doesn't exist.");

			return new String[]{fileName, regexp};
		}
		
		//relative path starts with [.|..] otherwise returned before
		//----------------------------------------------------------
		currentFile = currentFile.replace('/', File.separatorChar);
		
		//special case: current file is archiv
		if(new File(currentFile).exists() && 
				(new ConfFileHandler(null).getFileType(currentFile)==
					ConfFileHandler.ZIP_FILE_TYPE)){
			
			//.. and filename is "." or "./"
			//takes the archiv with another regexp
			if(fileName.equals("."))
				return new String[]{currentFile, regexp};
			
			//.. and the filename is like ./filename
			//search for the filename
			if(fileName.startsWith("."+File.separator))
				return new String[]{currentFile, "^"+fileName.substring(2)+"$"};
			
			//or search for the filename
			return new String[]{currentFile, "^"+fileName+"$"};
			
			//.. out of the archiv is not supported ../
		}
		
		//if current file has no path
		int pos = currentFile.lastIndexOf(File.separator);
		if(pos == -1){
			if(!(new File(fileName)).exists())
				throw new IOException("The file '"+fileName+"' doesn't exist.");

			return new String[]{fileName, regexp};
		}
		
		//combine home and relative path
		String startDir = currentFile.substring(0, pos);
		if(fileName.startsWith("."+File.separator))
			fileName = startDir+fileName.substring(1);
		
		//FIXME This may be a security hole cause ../../ path is allowed
		if(fileName.startsWith(".."+File.separator)){
			String tDir = startDir.endsWith(File.separator)?
					startDir.substring(0, startDir.length()-1):startDir;
			
			int i = tDir.lastIndexOf(File.separator);
			if(i == -1){
				fileName = startDir + fileName.substring(2);
			}else{
				fileName = startDir.substring(0, i) + fileName.substring(2);
			}
		}
		if(!(new File(fileName)).exists())
			throw new IOException("The file '"+fileName+"' doesn't exist.");

		return new String[]{fileName, regexp};
	}
	
	/**
	 * Creates a list of all files to include. The fileName path must be
	 * expanded to an absolute path.
	 * 
	 * @param vIn
	 * @param fileName
	 * @throws IOException
	 */
	private Vector<String> getFiles(String fileName) throws IOException {
		Vector<String> vIn = new Vector<String>();
		
		//The file is a directory: process all if name ends with '/**'
		File file = new File(fileName);
		
		if(file.isDirectory()){
			
			if(subdir) {
				String[] names = file.list(new DirFilter());
				for(int i=0; i<names.length; i++){
					vIn.addAll( getFiles(fileName+File.separator+names[i]) );
				}
				
				names = file.list(new FileFilter());
				for(int i=0; i<names.length; i++){
					File f = new File(file, names[i]);
					//add file at the end of the stack (FIFO)
					if(f.isFile() && f.exists())
						vIn.add(fileName+File.separator+names[i]);
				}
			}else{
				String[] names = file.list(new FileFilter());
				for(int i=0; i<names.length; i++){
					File f = new File(file, names[i]);
					//add file at the end of the stack (FIFO)
					if(f.isFile() && f.exists())
						vIn.add(fileName+File.separator+names[i]);
				}
			}
		}else{
			//add file at the end of the stack (FIFO)
			if(file.isFile() && file.exists())
				vIn.add(fileName);
		}//fi
		
		return vIn;
	}
	
	/**
	 * @param fileName String[]{filename, regexp}
	 * @return
	 * @throws IOException
	 */
	private String[] getConf(String fileName, String expr) throws IOException {
		
		ConfFileHandler handler = new ConfFileHandler(cparser.getRoot());
		
		//find configuration entries matching to regular expression
		RE re = null;
        try {
            re = new RE(expr);
        } catch (RESyntaxException e) {
            throw new IOException(e);
        }
		
		int fileType = handler.getFileType(fileName);
		
		if( fileType == ConfFileHandler.ZIP_FILE_TYPE ){
			String[] result = new String[0];
			ZipFile file = new ZipFile(fileName);
			
			Enumeration<? extends ZipEntry> enumeration = file.entries();
			while(enumeration.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)enumeration.nextElement();
				
				String name = entry.getName();
				if(re.match(name)){
					//resize array
					String[] temp = new String[result.length+1];
					System.arraycopy(result, 0, temp, 0, result.length);
					result = temp;
					
					//get type
					fileType = handler.getFileType(file, entry);
					
					if( fileType == ConfFileHandler.XML_FILE_TYPE ){
						result[result.length-1] = handler.getZipXFile(file, entry);
					}else{
						result[result.length-1] = handler.getZipFile(file, entry);
					}
				}
			}
			return result;
		}else if( fileType == ConfFileHandler.XML_FILE_TYPE ){
			if(re.match(fileName)){
				String fname = "file:///"+fileName.replace(File.separatorChar, '/');
				return new String[]{handler.getXFile(fname)};
			}
		}else{
			if(re.match(fileName)){
				return new String[]{handler.getFile(fileName)};
			}
		}
		
		//file is or contains no configuration file.
		return new String[]{""};
	}
	
	private class DirFilter implements FilenameFilter {
		
		public DirFilter(){}
		
		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {

			File f = null;

			if(dir == null)
				f = new File(name);
			else
				f = new File(dir, name);
			
			return f.isDirectory();
		}
	}
	
	private class FileFilter implements FilenameFilter {
		
		public FileFilter(){}
		
		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {

			File f = null;

			if(dir == null)
				f = new File(name);
			else
				f = new File(dir, name);
			
			return f.isFile();
		}
	}
}

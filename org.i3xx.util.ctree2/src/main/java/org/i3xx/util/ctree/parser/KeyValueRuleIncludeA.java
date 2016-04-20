package org.i3xx.util.ctree.parser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.i3xx.util.basic.io.CURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class uses a depth-first search
 * 
 * @author Stefan
 *
 */
public class KeyValueRuleIncludeA extends AbstractKeyValueRule {
	
	private static final Logger logger = LoggerFactory.getLogger(KeyValueRuleIncludeA.class);
	
	public static final int HJSON_FILE_TYPE = 4;
	public static final int ZIP_FILE_TYPE = 3;
	public static final int XML_FILE_TYPE = 2;
	public static final int DEFAULT_FILE_TYPE = 1;
	public static final int UNEXPECTED_FILE_TYPE = 0;
	
	private IParserWrapper wrapper;
	private String prefix;

	public KeyValueRuleIncludeA(IParserWrapper wrapper) {
		super();
		
		this.prefix = null;
		this.wrapper = wrapper;
	}

	@Override
	public void exec(String stmt, Map<String, String> params) throws IOException {
		
		String fileName = params.get("filename");
		
		String tmp = null;
		
		if(stmt.startsWith("#include-as")){
			tmp = stmt.substring(11).trim();
			//1st param is prefix
			int i = tmp.indexOf(" ");
			prefix = tmp.substring(0, i);
			//2nd param is regexp for files
			tmp = tmp.substring(i).trim();
			//get path (axis) and regexp of the include
		}else{ //#include
			prefix = null;
			tmp = stmt.substring(8).trim();
		}
		
		//get path (axis) and regexp of the include
		Object[] arr = getFileInfo(tmp, fileName);
		
		//get all filenames at the given path (axis)
		List<String> vIn = getFiles((String)arr[0], (Boolean)arr[2]);
		for(int i=0; i<vIn.size(); i++){
			//absolute filename
			String fn = vIn.get(i);
			//regular expression of the include
			String re = (String)arr[1];
			
			getConf(fn, re);
		}
	}

	@Override
	public boolean match(String stmt, Map<String, String> params) throws IOException {
		//#include and #include-as
		return stmt.startsWith("#include") && super.match(stmt, params);
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
	protected Object[] getFileInfo(String fileName, String currentFile) throws IOException {
		
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
		boolean fSubDir;
		
		//takes effort when a filepath is given.
		if(fileName.endsWith("/**")) {
			fSubDir = true;
			fileName = fileName.substring(0, fileName.length()-3);
		}else{
			fSubDir = false;
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

			return new Object[]{fileName, regexp, new Boolean(fSubDir)};
		}
		
		//relative path starts with [.|..] otherwise returned before
		//----------------------------------------------------------
		currentFile = currentFile.replace('/', File.separatorChar);
		
		//special case: current file is archiv
		//TODO:
		if(new File(currentFile).exists() && 
				getFileType(currentFile)==ZIP_FILE_TYPE){
			
			//.. and filename is "." or "./"
			//takes the archiv with another regexp
			if(fileName.equals("."))
				return new Object[]{currentFile, regexp, new Boolean(fSubDir)};
			
			//.. and the filename is like ./filename
			//search for the filename
			if(fileName.startsWith("."+File.separator))
				return new Object[]{currentFile, "^"+fileName.substring(2)+"$", new Boolean(fSubDir)};
			
			//or search for the filename
			return new Object[]{currentFile, "^"+fileName+"$", new Boolean(fSubDir)};
			
			//.. out of the archiv is not supported ../
		}
		
		//if current file has no path
		int pos = currentFile.lastIndexOf(File.separator);
		if(pos == -1){
			if(!(new File(fileName)).exists())
				throw new IOException("The file '"+fileName+"' doesn't exist.");

			return new Object[]{fileName, regexp, new Boolean(fSubDir)};
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

		return new Object[]{fileName, regexp, new Boolean(fSubDir)};
	}
	
	/**
	 * Creates a list of all files to include. The fileName path must be
	 * expanded to an absolute path.
	 * 
	 * @param vIn
	 * @param fileName
	 * @throws IOException
	 */
	protected List<String> getFiles(String fileName, boolean fSubDir) throws IOException {
		List<String> vIn = new ArrayList<String>();
		
		//The file is a directory: process all if name ends with '/**'
		File file = new File(fileName);
		
		if(file.isDirectory()){
			
			if(fSubDir) {
				String[] names = file.list(new DirFilter());
				for(int i=0; i<names.length; i++){
					vIn.addAll( getFiles(fileName+File.separator+names[i], fSubDir) );
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
	protected void getConf(String fileName, String expr) throws IOException {
		
		//find configuration entries matching to regular expression
		RE re = null;
        try {
            re = new RE(expr);
        } catch (RESyntaxException e) {
            throw new IOException(e);
        }
		
		int fileType = getFileType(fileName);
		
		if( fileType == ZIP_FILE_TYPE ){
			ZipFile file = new ZipFile(fileName);
			
			Enumeration<? extends ZipEntry> enumeration = file.entries();
			while(enumeration.hasMoreElements()) {
				ZipEntry entry = enumeration.nextElement();
				
				String name = entry.getName();
				if(re.match(name)){
					//get type
					fileType = getFileType(file, entry);
					
					if( fileType == XML_FILE_TYPE ){
						getZipXFile(file, entry);
					}else if( fileType == HJSON_FILE_TYPE ){
						getZipHFile(file, entry);
					}else{
						getZipFile(file, entry);
					}
				}
			}
		}else if( fileType == HJSON_FILE_TYPE ){
			if(re.match(fileName)){
				String fname = "file:///"+fileName.replace(File.separatorChar, '/');
				getHFile(fname);
			}
		}else if( fileType == XML_FILE_TYPE ){
			if(re.match(fileName)){
				String fname = "file:///"+fileName.replace(File.separatorChar, '/');
				getXFile(fname);
			}
		}else{
			if(re.match(fileName)){
				getFile(fileName);
			}
		}
		//file is or contains no configuration file.
	}
	
	/**
	 * @param fileName
	 * @param list
	 * @throws IOException
	 */
	protected void getFile(String fileName) throws IOException {
		
		AbstractReader reader = new FileReader(fileName);
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", fileName);
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "text/plain");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		
		logger.debug("Read conf file '{}' as file.", fileName);
	}
	
	/**
	 * @param fileName
	 * @param list
	 * @return
	 * @throws IOException
	 */
	protected void getHFile(String fileName) throws IOException {
		
		//Parse HJson and write dotted notation
		String fN;
		try {
			fN = CURL.fileURLtoFilename(fileName);
		} catch (URISyntaxException e) {
			throw new IOException(e.toString());
		}
		HJsonReader reader = new HJsonReader(fN);
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", fileName);
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "application/json");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		
		logger.debug("Read conf file '{}' as hjson file.", fileName);
	}
	
	/**
	 * @param fileName
	 * @param list
	 * @return
	 * @throws IOException
	 */
	protected void getXFile(String fileName) throws IOException {
		
		//Parse XML and write dotted notation
		String fN;
		try {
			fN = CURL.fileURLtoFilename(fileName);
		} catch (URISyntaxException e) {
			throw new IOException(e.toString());
		}
		XmlReader reader = new XmlReader(fN);
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", fileName);
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "text/xml");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		
		logger.debug("Read conf file '{}' as xml file.", fileName);
	}
	
	/**
	 * @param file
	 * @param entry
	 * @param list
	 * @return
	 * @throws IOException
	 */
	protected void getZipFile(ZipFile file, ZipEntry entry) throws IOException {
		
		AbstractReader reader = new ZipReader(file.getName(), entry.getName());
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", file.getName());
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "text/plain");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		
		logger.debug("Read conf file '{} ({})' as zip file.", file.getName(), entry.getName());
	}
	
	/**
	 * @param file
	 * @param entry
	 * @param list
	 * @return
	 * @throws IOException
	 */
	protected void getZipXFile(ZipFile file, ZipEntry entry) throws IOException {
		
		AbstractReader reader = new ZipXmlReader(file.getName(), entry.getName());
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", file.getName());
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "text/xml");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		
		logger.debug("Read conf file '{} ({})' as xml zip file.", file.getName(), entry.getName());
	}
	
	/**
	 * @param file
	 * @param entry
	 * @param list
	 * @return
	 * @throws IOException
	 */
	protected void getZipHFile(ZipFile file, ZipEntry entry) throws IOException {
		/*
		ZipHJsonReader reader = new ZipHJsonReader(file.getName(), entry.getName());
		reader.setParams(new HashMap<String, String>());
		reader.getParams().put("filename", file.getName());
		reader.getParams().put("datatype", "line");
		reader.getParams().put("mimetype", "application/json");
		reader.getParams().put("prefix", prefix);
		wrapper.process( reader );
		reader.close();
		*/
		logger.debug("Read conf file '{} ({})' as xml hjson file.", file.getName(), entry.getName());
	}
	
	/**
	 * @author Stefan
	 *
	 */
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
	
	/**
	 * @author Stefan
	 *
	 */
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
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	protected int getFileType(String fileName) throws IOException {
		
		//Test file type
		RandomAccessFile f = new RandomAccessFile(fileName, "r");
		byte[] buf = new byte[4]; 
		f.read(buf);
		f.close();
		
		if( buf[0]==0x50 && buf[1]==0x4B && buf[2]==0x3 && buf[3]==0x4 ){
			//PK34: 50 4B 03 04
			return ZIP_FILE_TYPE;
		}else if( buf[0]==0x3C && buf[1]==0x3F ){
			//<?: 3C 3F
			return XML_FILE_TYPE;
		}else if( buf[0]==0x7B ){
			//{: 7B
			return HJSON_FILE_TYPE;
		}else{
			return DEFAULT_FILE_TYPE;
		}
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	protected int getFileType(ZipFile file, ZipEntry entry) throws IOException {
		
		byte[] buf = new byte[4];
		
		InputStream in = file.getInputStream(entry);
		in.read(buf);
		in.close();
		
		if( buf[0]==0x50 && buf[1]==0x4B && buf[2]==0x3 && buf[3]==0x4 ){
			//PK34: 50 4B 03 04
			return ZIP_FILE_TYPE;
		}else if( buf[0]==0x3C && buf[1]==0x3F ){
			//<?: 3C 3F
			return XML_FILE_TYPE;
		}else if( buf[0]==0x7B ){
			//{: 7B
			return HJSON_FILE_TYPE;
		}else{
			return DEFAULT_FILE_TYPE;
		}
	}

}

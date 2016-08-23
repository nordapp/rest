package org.i3xx.util.ctree.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.hjson.JsonArray;
import org.hjson.JsonObject.Member;
import org.hjson.JsonValue;

public class ZipHjsonReader extends AbstractElementReader {

	private String fileName;
	private String entryName;
	
	public ZipHjsonReader(String fileName, String entryName) {
		this.fileName = fileName;
		this.entryName = entryName;
	}

	/**
	 * The file is closed manually
	 * @see org.i3xx.util.ctree.parser.KeyValueRuleIncludeA
	 */
	@SuppressWarnings("resource")
	public boolean available() throws IOException {
		if(!init){
			init = true;
			
			ZipFile file = new ZipFile(fileName);
			ZipEntry entry = file.getEntry(entryName);
			Reader r = new BufferedReader( new InputStreamReader( 
					file.getInputStream(entry)));
			
			JsonValue obj = JsonValue.readHjson(r);
			walk(null, obj);
			
			r.close();
		}
		return super.available();
	}
	
	private void walk(String name, JsonValue val) {
		if(val.isNull()) {
			return;
		}else if(val.isArray()){
			JsonArray arr = val.asArray();
			for(int i=0;i<arr.size();i++) {
				walk(Integer.toString(i), arr.get(i));
			}//for
		}else if(val.isObject()){
			Iterator<Member> iter = val.asObject().iterator();
			while(iter.hasNext()) {
				Member m = iter.next();
				walk((name==null?"":name+".")+m.getName(), m.getValue());
			}//while
		}else if(val.isBoolean()){
			elements.add(name+" "+val.toString());
		}else if(val.isNumber()){
			elements.add(name+" "+val.toString());
		}else if(val.isString()){
			elements.add(name+" "+val.asString());
		}
	}
	
}

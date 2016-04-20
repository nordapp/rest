package org.i3xx.util.ctree.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.hjson.JsonArray;
import org.hjson.JsonObject.Member;
import org.hjson.JsonValue;

public class HJsonReader extends AbstractElementReader {

	private String fileName;
	
	public HJsonReader(String fileName) {
		this.fileName = fileName;
	}
	
	/* (non-Javadoc)
	 * @see org.i3xx.util.ctree.parser.AbstractElementReader#available()
	 */
	public boolean available() throws IOException {
		if(!init){
			init = true;
			
			Reader r = new java.io.FileReader(fileName);
			
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

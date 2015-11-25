/*
 * Created on 30.06.2004
 */
package com.i3xx.util.ctree.xml;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.i3xx.util.ctree.IConfNode;

/**
 * @author Administrator
 * 
 * @Deprecated: Use new parser concept (com.uil.ctree.parser.*).
 */
@Deprecated
public class NodeIterator {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeIterator.class);
	
	protected final IConfNode root; //all for lookup
	protected final IConfNode adds; //only new created nodes
	protected final Vector<String> path;
	
	/**
	 * 
	 */
	public NodeIterator(IConfNode root) {
		this(root, null);
	}
	
	/**
	 * 
	 */
	public NodeIterator(IConfNode root, IConfNode adds) {
		super();
		
		this.root = root;
		this.adds = adds;
		this.path = new Vector<String>();
	}
	
	/**
	 * Die Routine ruft sich selbst (rekursiv) auf um ENTITY_REFERENCE_NODEs
	 * verfolgen zu k�nnen.
	 * 
	 * @param node
	 */
	public void iterate(Node node) throws IOException {
		
		if(node==null){
            logger.info("[NodeIterator] Node should not be null.");
			return;
		}
		
		String lastElementName = node.getNodeName();
		
		NamedNodeMap map = node.getAttributes();
		if(map != null){
			boolean refid = false;
			boolean array = false;
			
			if(map.getNamedItem("refid")!=null){
				//refid attribute: the element gets another name
				//<name refid="value">
				//path.value.data-node data-value
				refid = true;
				
				//update path
				lastElementName = map.getNamedItem("refid").getNodeValue();
			}//fi map.getNamedItem("refid")!=null
			
			if((map.getNamedItem("array")!=null)&&
					Boolean.valueOf(map.getNamedItem("array").getNodeValue()).booleanValue()){
				//array attribute: the element belongs to an array
				array = true;
				
				//get next array index
				int n=0;
				StringBuffer b = new StringBuffer();
				for(int i=2; i<path.size(); i++){
					b.append(path.elementAt(i));
					b.append('.');
				}
				b.append(lastElementName);
				b.append("."); 
				for(;;){
					try{
						root.get(b.toString()+n);
						n++;
					}catch(NoSuchElementException e){
						break;
					}
				}//for

				//index the element
				if((map.getNamedItem("index")!=null)){
					if( map.getNamedItem("index").getNodeValue().equalsIgnoreCase("true") ||
							map.getNamedItem("index").getNodeValue().equalsIgnoreCase("false")){
						//Create separate index node
						//true: set an index with the old element name
						//path.name.n value.[n] that index to the array element
						//path.value.[n].data-node data-value
						//if not refid use true|false instead value.[n]
						if(refid){
							put(node.getNodeName()+"."+String.valueOf(n), 
									lastElementName+"."+String.valueOf(n), 0);
						}else{
							put(node.getNodeName()+"."+String.valueOf(n), 
									map.getNamedItem("index").getNodeValue(), 0);
						}
						//array node
						lastElementName += "."+n;
					}else{
						//named array node
						lastElementName += "."+map.getNamedItem("index").getNodeValue();
					}//fi
				}else{
					//array node (without index)
					lastElementName += "."+n;
				}//fi
			}//fi
				
			//attributes
			for(int i=0; i<map.getLength(); i++){
				Node attr = map.item(i);
				String attrname = attr.getNodeName().toLowerCase();
				if( refid &&
					( attrname.equals("refid")||
					  attrname.equals("index"))){
					//does nothing
				}else if(attrname.equals("array")){
					//does nothing
				}else if(array &&
						attrname.equals("index") /*&&
						( ! attr.getNodeValue().equalsIgnoreCase("true"))*/ ){
					//does nothing
				}else if(attrname.startsWith("xmlns:")){
					//does nothing
				}else{
					//treat other attrs as conf-nodes
					String value = attr.getNodeValue();
					if( value != null )
						put(attr.getNodeName(), value, 0);
				}//fi
			}//for
		}//fi map!=null
		
		//insert path without value
		if(node.getChildNodes().getLength() == 0){
			//empty conf entry
			put(lastElementName, "", 0);			
		}//fi
		
		//update path
		path.add(lastElementName);
		
		//iteration
		NodeList list = node.getChildNodes();
		for (int i=0;i<list.getLength();i++){
			Node tnode = list.item(i);
			switch(tnode.getNodeType()) {
				//follow entity reference
				case Node.ENTITY_REFERENCE_NODE:
					iterate(tnode);
					break;
				case Node.ELEMENT_NODE:
					iterate(tnode);
					break;
				case Node.CDATA_SECTION_NODE:
				case Node.TEXT_NODE:
					//Value of field
					if(tnode.getNextSibling()==null && tnode.getPreviousSibling()==null){
						String value = tnode.getNodeValue();
						put(lastElementName, value, 1);
					}
					break;
				default:
			}//switch
		}//for
		
		//back to parent
		path.remove(path.size()-1);
	}

	/**
	 * @param key
	 * @param value
	 * @param c Textknoten liefern das letzte Element als key mit. Somit muss
	 * dieses im Pfad unterdr�ckt werden.
	 */
	protected void put(String key, String value, int c) throws IOException
	{
		StringBuffer b = new StringBuffer();
		for(int i=2; i<path.size()-c; i++){
			b.append(path.elementAt(i));
			b.append('.');
		}
		
		value = value.trim().equals("") ? "" : ConfParser.convert(value) ;
		
		//This needs lookup - the following line doesn't work in all cases.
		root.create(b.toString()+key).value( value );
		
		//Adds is normally used to create a non-XML text configuration to read again.
		if(adds!=null)
			adds.create(b.toString()+key).value( value );
	}
}

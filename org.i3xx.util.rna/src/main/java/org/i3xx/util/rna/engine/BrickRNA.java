package org.i3xx.util.rna.engine;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.i3xx.util.rna.core.ConcurrentAccessException;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IBrickData;
import org.i3xx.util.rna.core.IBrickHistory;
import org.i3xx.util.rna.core.IBrickInterna;
import org.i3xx.util.rna.core.IDocDb;
import org.i3xx.util.rna.core.IDocument;
import org.i3xx.util.rna.core.IIdsDocument;
import org.i3xx.util.rna.core.IIdsStore;
import org.i3xx.util.rna.core.IProxy;
import org.i3xx.util.rna.core.ITimeSlice;
import org.i3xx.util.rna.core.UserContentException;
import org.i3xx.util.rna.core.XMLException;
import org.i3xx.util.rna.engine.server.CBrickFactory;
import org.i3xx.util.rna.engine.server.RNAFactory;

/**
 * The RNA is a generator to generate the genetic information of a brick or
 * create a brick from this information.
 * 
 * The generator needs a stub to ensure the right version
 * 
 * @author Stefan
 * @since 12.04.2012
 */
public class BrickRNA {
	 
	public static final int INDICES_LENGTH = 10;
	
	public static final String PROXY_SEPARATOR = "|";
	
	public static final String FIELD_OB4_INTERN = "_id";
	public static final String FIELD_OB4_DOCUMENT = "_ax";
	public static final String FIELD_OB4_REFERENCE = "_pt";
	public static final String FIELD_FLAGS = "flags";
	public static final String FIELD_RIGHTS = "rights";
	public static final String FIELD_DATA = "data";
	public static final String FIELD_DOCUMENT_DATA = "document-data";
	public static final String FIELD_DOCUMENT = "document";
	public static final String FIELD_SIZE = "size";
	public static final String FIELD_MIMETYPE = "mimetype";
	public static final String FIELD_LENGTH = "length";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_CONTENT_N = "content-";
	public static final String FIELD_HISTORY = "history";
	public static final String FIELD_INDEX = "index";
	public static final String FIELD_REFERENCES = "references";
	
	public static final String LINK_SOUCE = "source";
	public static final String LINK_TARGET = "target";
	public static final String LINK_SOURCE_TRANSID = "sourcetransid";
	public static final String LINK_TARGET_TRANSID = "targettransid";
	public static final String LINK_TYPE = "type";
	public static final String LINK_RESOURCE = "resource";
	public static final String LINK_SIZE = "size";
	
	private static final String FIELD_OB4_KEY = "%%keys%%"; //undocumented
	
	private static final String SYM_KEY = "key";
	private static final String SYM_VAL = "val";
	private static final String SYM_CLASS = "class";
	
	/** @since M4 */
	private static final int CREATETIMESTAMP = 1;
	/** use type info for each data field */
	private static final int USE_TYPE_INFO = 10;
	/** switch long int to int where possible */
	@SuppressWarnings("unused")
	private static final int USE_INT_INSTEAD_OF_LONG = 11;
	/** accept int and long int at compare */
	private static final int IGNORE_INT_TYPE = 12;
	/** the root contains the history */
	private static final int ROOT_CONTAINS_HISTORY = 2;
	/** the field keys are sorted before use */
	private static final int SORT_FIELD_KEYS = 3;
	/** skip history bricks */
	private static final int SKIP_HISTORY_BRICKS = 4;
	/** use the history walker instead of the default iterator */
	private static final int USE_HISTORY_WALKER = 5;
	/** print child links */
	private static final int USE_CHILD_LINK_INFO = 6;
	/** skip large child sets */
	private static final int USE_SKIP_LARGE_CHILD_SETS = 7;
	/** use the timeline to iterate */
	private static final int USE_TIMELINE = 8;
	/** use raw links instead of link algorithm */
	private static final int USE_RAW_LINKS = 9;
	/** do not write index information */
	private static final int USE_NO_INTERN_INFORMATION = 13;
	/** use data export version */
	private static final int USE_EXPORT_VERSION = 14;
	
	/***/
	private final BitSet fTc;
	
	public BrickRNA() {
		fTc = new BitSet();
	}
	
	//
	// Rules
	//
	
	/**
	 * Sets the flag rule for M3/M4 conversion
	 * 
	 * The field createtimestamp is new in M4 the field is missing in former versions.
	 */
	public final void setRuleM3_M4() {
		fTc.set(CREATETIMESTAMP);
		fTc.set(USE_TYPE_INFO);
		//fTc.set(USE_INT_INSTEAD_OF_LONG);
		//fTc.set(IGNORE_INT_TYPE);
	}
	
	/**
	 * Sets the flag for field key sorting
	 */
	public final void setRuleSortFieldkeys() {
		fTc.set(SORT_FIELD_KEYS);
	}
	
	/**
	 * Sets the flag to skip any history data (not fields)
	 */
	public final void setSkipHistoryBricks() {
		fTc.set(SKIP_HISTORY_BRICKS);
	}
	
	/**
	 * @return true if the SKIP_HISTORY_BRICKS flag is set
	 */
	public final boolean getSkipHistoryBricks() {
		return fTc.get(SKIP_HISTORY_BRICKS);
	}
	
	/**
	 * set the ROOT_CONTAINS_HISTORY flag
	 * (true suggest, the history is member of the root 0..n)
	 */
	public final void setRuleHistory() {
		fTc.set(ROOT_CONTAINS_HISTORY);
	}
	
	/**
	 * @return true if the ROOT_CONTAINS_HISTORY flag is set
	 */
	public final boolean getRuleHistory(){
		return fTc.get(ROOT_CONTAINS_HISTORY);
	}
	
	/**
	 * set the USE_HISTORY_WALKER flag
	 * (true suggest, use the history walker instead of the default waker)
	 */
	public final void setRuleHistoryWalker() {
		fTc.set(USE_HISTORY_WALKER);
	}
	
	/**
	 * @return true if the USE_HISTORY_WALKER flag is set
	 */
	public final boolean getRuleHistoryWalker() {
		return fTc.get(USE_HISTORY_WALKER);
	}
	
	/**
	 * set the USE_CHILD_LINK_INFO flag
	 * (true - than add child information to link info - creates very much data)
	 */
	public final void setRuleChildLinkInfo() {
		fTc.set(USE_CHILD_LINK_INFO);
	}
	
	/**
	 * @return true if the USE_CHILD_LINK_INFO flag is set
	 */
	public final boolean getRuleChildLinkInfo() {
		return fTc.get(USE_CHILD_LINK_INFO);
	}
	
	/**
	 * set the USE_SKIP_LARGE_CHILD_SETS flag
	 * (true - than skip large child sets to avoid transaction hazard)
	 */
	public final void setRuleSkipLargeChildSets() {
		fTc.set(USE_SKIP_LARGE_CHILD_SETS);
	}
	
	/**
	 * @return true if the USE_SKIP_LARGE_CHILD_SETS flag is set
	 */
	public final boolean getRuleSkipLargeChildSets() {
		return fTc.get(USE_SKIP_LARGE_CHILD_SETS);
	}
	
	/**
	 * set the USE_TIMELINE flag
	 * (true - than skip large child sets to avoid transaction hazard)
	 */
	public final void setRuleTimeline() {
		fTc.set(USE_TIMELINE);
	}
	
	/**
	 * @return true if the USE_TIMELINE flag is set
	 */
	public final boolean getRuleTimeline() {
		return fTc.get(USE_TIMELINE);
	}
	
	/**
	 * set the USE_RAW_LINKS flag
	 * (true - than skip large child sets to avoid transaction hazard)
	 */
	public final void setRuleRawLink() {
		fTc.set(USE_RAW_LINKS);
	}
	
	/**
	 * @return true if the USE_RAW_LINKS flag is set
	 */
	public final boolean getRuleRawLink() {
		return fTc.get(USE_RAW_LINKS);
	}
	
	/**
	 * set the USE_NO_INTERN_INFORMATION flag
	 * (true - than skip the index information fields)
	 */
	public final void setRuleNoInternInformation() {
		fTc.set(USE_NO_INTERN_INFORMATION);
	}
	
	/**
	 * @return true if the USE_NO_INTERN_INFORMATION flag is set
	 */
	public final boolean getRuleNoInternInformation() {
		return fTc.get(USE_NO_INTERN_INFORMATION);
	}
	
	/**
	 * set the USE_EXPORT_VERSION flag
	 * (true - use the export data version)
	 */
	public final void setRuleExportVersion() {
		fTc.set(USE_EXPORT_VERSION);
	}
	
	/**
	 * @return true if the USE_EXPORT_VERSION flag is set
	 */
	public final boolean getRuleExportVersion() {
		return fTc.get(USE_EXPORT_VERSION);
	}
	
	//
	// Main tools
	//
	
	/**
	 * Writes the information to a single writer
	 * 
	 * @param brick The brick to write
	 * @param w The data writer
	 * @param docDb A flag to write the link (IDocDb.WRITE_REF) or the document (IDocDb.WRITE_DOC)
	 * @return
	 * @throws IOException
	 */
	public IDataWriter getInformation(IBrick brick, IDataWriter w, int docDb) throws IOException {
		return getInformation(brick, w, w, docDb);
	}
	
	/**
	 * Writes the information uses a separate writer for the links
	 * 
	 * @param brick The brick to write
	 * @param w The data writer
	 * @param lw The link writer
	 * @param docDb A flag to write the link (IDocDb.WRITE_REF) or the document (IDocDb.WRITE_DOC)
	 * @return
	 * @throws IOException
	 */
	public IDataWriter getInformation(IBrick brick, IDataWriter w, IDataWriter lw, int docDb) throws IOException {
		
        GetLinks lk = new GetLinks();
        
        addInformation(brick, w, lk);
        addHistoryInformation(brick, w, lk);
        addDataInformation(brick, w, lk);
        if(docDb==IDocDb.WRITE_NONE){
        	addDocumentDataOnly(brick, w);
        }else if(docDb==IDocDb.WRITE_REF){
        	addDocumentInformation(brick, w);
        }else if(docDb==IDocDb.WRITE_DOC){
        	addDocument(brick, w);
        }else if(docDb==IDocDb.WRITE_LATEST){
        	addDocumentLatestVersion(brick, w);
        }else
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
        addLinks(lw, lk);
        
        //w.close();
        
        return w;
	}
	
	/**
	 * @param r
	 * @param docDb
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws ConcurrentAccessException 
	 * @throws XMLException 
	 */
	public IBrick setInformation(IDataReader r, ISetLinks lk, int docDb) throws IOException, ClassNotFoundException, ConcurrentAccessException, XMLException {
		
		IBrick brick = createBrick(r);
		brick.edit();
		putInformation(brick, r);
		putHistoryInformation(brick, r);
		putDataInformation(brick, r);
        if(docDb==IDocDb.WRITE_NONE){
        	//there is a document information, but the document is empty.
			putDocumentInformation(brick, r);
        }else if(docDb==IDocDb.WRITE_REF){
			putDocumentInformation(brick, r);
        }else if(docDb==IDocDb.WRITE_DOC){
			putDocument(brick, r, docDb);
        }else if(docDb==IDocDb.WRITE_LATEST){
			putDocument(brick, r, docDb);
		}else{
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
		}
		putInformationFlags(brick, r);
		brick.clean();
		
		setLinks(r, lk);
		
		//
		// <- lk.getList()
		//for(int i=0;i<lk.getList().size();i++){
		//	LinkInfo info = lk.getList().get(i);
		//	lk.set(info, hdl);
		//}
		
		return brick;
	}
	
	/**
	 * Writes the information uses a separate writer for the links
	 * 
	 * The export data differs from the data used by the RNA process to duplicate the database.
	 * 
	 * @param brick The brick to write
	 * @param w The data writer
	 * @param lw The link writer
	 * @param docDb A flag to write the link (IDocDb.WRITE_REF) or the document (IDocDb.WRITE_DOC)
	 * @return
	 * @throws IOException
	 */
	public IDataWriter getExportInformation(IBrick brick, IDataWriter w, IDataWriter lw, int docDb) throws IOException {
		
        GetLinks lk = new GetLinks();
        
        addExportInformation(brick, w, lk);
        addExportDataInformation(brick, w, lk);
        addExportDocument(brick, w, docDb);
        addExportLinks(lw, lk);
        
        return w;
	}
	
	/**
	 * @param r
	 * @param docDb
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws ConcurrentAccessException 
	 * @throws XMLException 
	 */
	public IBrick setExportInformation(IDataReader r, ISetLinks lk, int docDb) throws IOException, ClassNotFoundException, ConcurrentAccessException, XMLException {
		
		//TODO:
		
		IDataReader _id = r.readDataReader(FIELD_OB4_INTERN);
		IDataReader _ax = r.readDataReader(FIELD_OB4_DOCUMENT);
		IDataReader _pt = r.readDataReader(FIELD_OB4_REFERENCE);
		
		IBrick brick = createBrick(_id);
		brick.edit();
		
		putInformation(brick, _id);
		putExportDataInformation(brick, r);
		
        if(docDb==IDocDb.WRITE_NONE){
        	//there is a document information, but the document is empty.
			putDocumentInformation(brick, _ax);
        }else if(docDb==IDocDb.WRITE_REF){
			putDocumentInformation(brick, _ax);
        }else if(docDb==IDocDb.WRITE_DOC){
			putDocument(brick, _ax, docDb);
        }else if(docDb==IDocDb.WRITE_LATEST){
			putDocument(brick, _ax, docDb);
		}else{
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
		}
        
		putInformationFlags(brick, _id);
		brick.clean();
		
		setExportLinks(_pt, lk);
		
		//
		// <- lk.getList()
		//for(int i=0;i<lk.getList().size();i++){
		//	LinkInfo info = lk.getList().get(i);
		//	lk.set(info, hdl);
		//}
		
		return brick;
	}
	
	/**
	 * @param ref
	 * @param val
	 * @param fCrc
	 * @param docDb
	 * @return
	 * @throws IOException
	 */
	public List<String> compare(IBrick ref, IBrick val, boolean fCrc, int docDb) throws IOException {
		
		List<String> list = compareInformation(ref, val);
		list.addAll( compareHistoryInformation(ref, val) );
		list.addAll( compareDataInformation(ref, val) );
        if(docDb==IDocDb.WRITE_NONE){
        	//there is a document information, but the document is empty.
			list.addAll( compareDocumentInformation(ref, val) );
        }else if(docDb==IDocDb.WRITE_REF){
			list.addAll( compareDocumentInformation(ref, val) );
        }else if(docDb==IDocDb.WRITE_DOC){
			list.addAll( compareDocument(ref, val, fCrc) );
        }else if(docDb==IDocDb.WRITE_LATEST){
			list.addAll( compareDocument(ref, val, fCrc) );
		}else{
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
		}
		
		return list;
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 * @throws IOException
	 */
	/*
	public boolean compare(IBrick ref, IBrick val) throws IOException {
		return compareLinks(ref, val);
	}
	*/
	
	/**
	 * Returns the hashes of the single parts of the Brick as a long array
	 * 
	 * [0] IBrick
	 * [1] Links of IBrick
	 * [2] IBrickHistory
	 * [3] Links of ITimeSlice
	 * [4] IBrickData
	 * [5] Links of IBrickData
	 * [6] IDocument
	 * [7] Links of IDocument
	 * 
	 * @param brick
	 * @param docDb
	 * @return
	 * @throws IOException
	 */
	public long[] getHash(IBrick brick, int docDb) throws IOException {
		
		long[] result = new long[8];
		
		CRC32 crc = new CRC32();
        IDataWriter w = null;
        @SuppressWarnings("unused")
		IDataWriter lw = null;
        GetLinks lk = null;
        
        //CBrick
        crc.reset();
        lk = new GetLinks();
        w = new CrcWriter(crc);
        addInformation(brick, w, lk);
        result[0] = crc.getValue();
        w.close();
        
        //CBrick links
        crc.reset();
        w = new CrcWriter(crc);
        addLinks(w, lk);
        result[1] = crc.getValue();
        w.close();
        
        //CBrickHistory
        crc.reset();
        lk = new GetLinks();
        w = new CrcWriter(crc);
        addHistoryInformation(brick, w, lk);
        result[2] = crc.getValue();
        w.close();
        
        //CBrickHistory links
        crc.reset();
        w = new CrcWriter(crc);
        addLinks(w, lk);
        result[3] = crc.getValue();
        w.close();
        
        //CData
        crc.reset();
        lk = new GetLinks();
        w = new CrcWriter(crc);
        addDataInformation(brick, w, lk);
        result[4] = crc.getValue();
        w.close();
        
        //CData links
        crc.reset();
        w = new CrcWriter(crc);
        addLinks(w, lk);
        result[5] = crc.getValue();
        w.close();
        
        //CDocument
        crc.reset();
        lk = new GetLinks();
        w = new CrcWriter(crc);
        if(docDb==IDocDb.WRITE_NONE){
        	//there is a document information, but the document is empty.
        	addDocumentDataOnly(brick, w);
        }else if(docDb==IDocDb.WRITE_REF){
        	addDocumentInformation(brick, w);
        }else if(docDb==IDocDb.WRITE_DOC){
        	addDocument(brick, w);
        }else if(docDb==IDocDb.WRITE_LATEST){
        	addDocumentLatestVersion(brick, w);
		}else{
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
		}
        result[6] = crc.getValue();
        w.close();
        
        //CDocument links
        crc.reset();
        w = new CrcWriter(crc);
        addLinks(w, lk);
        result[7] = crc.getValue();
        w.close();
        
        return result;
	}
	
	/**
	 * returns a list of link information of the brick.
	 * @param brick
	 * @return
	 */
	public List<LinkInfo> linkInformation(IBrick brick) {
		
        GetLinks lk = new GetLinks();
        
        linkInformation(brick, lk);
        linkHistory(brick, lk);
        linkDataInformation(brick, lk);
        
        List<LinkInfo> link = lk.getList();
        return link;
	}
	
	// ------------------------------------------------------------------------------------
	// Read information from brick
	// ------------------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------------------
	// Data information (CBrick)
	// ------------------------------------------------------------------------------------
	
	/**
	 * Creates a new transient brick
	 * 
	 * @param id
	 * @param classname
	 * @param name
	 * @return
	 * @throws IOException
	 */
	protected IBrick createBrick(IDataReader r) throws IOException {
		
		Long id = r.readLong( IBrick.FIELD_ID );
		String classname = r.readString( IBrick.FIELD_CLASSNAME );
		String name = r.readString( IBrick.FIELD_NAME );
		
        //set dummy brick
        IBrickData data;
		try {
			data = (IBrickData)CBrickFactory.Element().factory(
					(name.equals("") ? "generic" : name) , classname, id);
		} catch (Exception e) {
			IOException io = new IOException("Unable to create the brick "+String.valueOf(id)+
					" '"+classname+"' '"+name+"'\nThe reason is:\n"+e.toString());
			io.initCause(e);
			throw io;
		}
		
		//the bricks are created with the flag NOT_IN_ROOT (0x20) set. remove this flag.
		((IBrickInterna)data).removeFlag(IBrick.NOT_IN_ROOT);
		
        return data;
	}
	
	/**
	 * Note: The readonly flag is set last.
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected void putInformation(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		
		//
		//fields of IBrick
		//
		
		brick.aspect( r.readString( IBrick.FIELD_ASPECT ) );
		brick.childFactory( r.readString( IBrick.FIELD_CHILDFACTORY ) );
		//brick.createtimestamp( r.readString( IBrick.FIELD_CREATETIMESTAMP ) );
		((IBrickInterna)brick).setFlag( r.readInt( FIELD_FLAGS ) & (~IBrick.READONLY) ); //see note above.
		brick.group( r.readString( IBrick.FIELD_GROUP ) );
		brick.obtimestamp( r.readString( IBrick.FIELD_OBTIMESTAMP ) );
		brick.owner( r.readString( IBrick.FIELD_OWNER ) );
		//NOTE: The int will change to a long in near future.
		brick.setTransID( (int)(r.readLong( IBrick.FIELD_TRANSID ) & 0xFFFF) );
		brick.user( r.readString( IBrick.FIELD_USER ) );
		
		//
		// data of IBrick (system dependent - not part of the data)
		//
		
		//file record
		DataTool.setFileRecord(brick, r.readStringArray(IBrick.FIELD_FILERECORD));
		
		//indices
		DataTool.setIndex(brick, r.readStringArray(FIELD_INDEX));
		
		//rights
		IDataReader rd = r.readDataReader(FIELD_RIGHTS);
		DataTool.setRights(brick, rd.readLongArray(SYM_KEY), rd.readLongArray(SYM_VAL));
	}
	
	/**
	 * The readonly flag should be set last, because a readonly flag can make some troubles
	 * if set before.
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException 
	 */
	protected void putInformationFlags(IBrick brick, IDataReader r) throws IOException {
		((IBrickInterna)brick).setFlag( ( r.readInt( FIELD_FLAGS ) & IBrick.READONLY ) );
	}
	
	/**
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	protected void addInformation(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		//
		//fields of IBrick
		//
		
		w.write( IBrick.FIELD_ID ,brick.ID() );
		//w.write( IBrick.FIELD_KEY ,brick.KEY() );
		//w.write( IBrick.FIELD_MAIN ,brick.MAIN() );
		w.write( IBrick.FIELD_CLASSNAME ,brick.classname() );
		w.write( IBrick.FIELD_NAME ,brick.name() );
		
		//w.write( IBrick.FIELD_CREATETIMESTAMP ,brick.createtimestamp() );
		w.write( IBrick.FIELD_OBTIMESTAMP ,brick.obtimestamp() );
		w.write( IBrick.FIELD_TRANSID ,brick.transid() );
		w.write( IBrick.FIELD_USER ,brick.user() );
		w.write( FIELD_FLAGS ,brick.getFlags() );
		
		//
		// data of IBrick (system dependent - not part of the data)
		//
		
		if( ! fTc.get(USE_NO_INTERN_INFORMATION)) {
			
			//fields of IBrick
			w.write( IBrick.FIELD_ASPECT ,brick.aspect() );
			w.write( IBrick.FIELD_CHILDFACTORY ,brick.childFactory() );
			w.write( IBrick.FIELD_GROUP ,brick.group() );
			w.write( IBrick.FIELD_OWNER ,brick.owner() );
			
			//file record
			w.write( IBrick.FIELD_FILERECORD, brick.fileRecord() );
			
			//indices
			w.write( FIELD_INDEX, DataTool.getIndex(brick) );
			
			//rights
			IDataWriter wd = w.getSubWriter(FIELD_RIGHTS);
			long[][] tmp = DataTool.getRights(brick);
			wd.write(SYM_KEY, tmp[0]);
			wd.write(SYM_VAL, tmp[1]);
			wd.close();
		}
		
		//structure
		//linker.add(IBrick.FIELD_PARENT, brick, LinkInfo.PARENT);
		linkInformation(brick, linker);
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 */
	protected List<String> compareInformation(IBrick ref, IBrick val) {
		List<String> list = new ArrayList<String>();
		
		if( ! compare(ref.ID(), val.ID()) )
			list.add(IBrick.FIELD_ID+print(ref.ID(), val.ID()));
		//if( ! compare(ref.KEY(), val.KEY()) )
		//	list.add(IBrick.FIELD_KEY+print(ref.KEY(), val.KEY()));
		//if( ! compare(ref.MAIN(), val.MAIN()) )
		//	list.add(IBrick.FIELD_MAIN+print(ref.MAIN(), val.MAIN()));
		if( ! compare(ref.classname(), val.classname()) )
			list.add(IBrick.FIELD_CLASSNAME+print(ref.classname(), val.classname()));
		if( ! compare(ref.name(), val.name()) )
			list.add(IBrick.FIELD_NAME+print(ref.name(), val.name()));
		if( ! compare(ref.aspect(), val.aspect()) )
			list.add(IBrick.FIELD_ASPECT+print(ref.aspect(), val.aspect()));
		if( ! compare(ref.childFactory(), val.childFactory()) )
			list.add(IBrick.FIELD_CHILDFACTORY+print(ref.childFactory(), val.childFactory()));
		/*if(fTc.get(CREATETIMESTAMP)){
			if( ! compareIgnoreNull(ref.createtimestamp(), val.createtimestamp()) )
				list.add(IBrick.FIELD_CREATETIMESTAMP+print(ref.createtimestamp(), val.createtimestamp()));
		}else{
			if( ! compare(ref.createtimestamp(), val.createtimestamp()) )
				list.add(IBrick.FIELD_CREATETIMESTAMP+print(ref.createtimestamp(), val.createtimestamp()));
		}*/
		if( ! compare(ref.getFlags(), val.getFlags()) )
			list.add(FIELD_FLAGS+print(ref.getFlags(), val.getFlags()));
		if( ! compare(ref.group(), val.group()) )
			list.add(IBrick.FIELD_GROUP+print(ref.group(), val.group()));
		if( ! compare(ref.obtimestamp(), val.obtimestamp()) )
			list.add(IBrick.FIELD_OBTIMESTAMP+print(ref.obtimestamp(), val.obtimestamp()));
		if( ! compare(ref.owner(), val.owner()) )
			list.add(IBrick.FIELD_OWNER+print(ref.owner(), val.owner()));
		if( ! compare(ref.transid(), val.transid()) )
			list.add(IBrick.FIELD_TRANSID+print(ref.transid(), val.transid()));
		if( ! compare(ref.user(), val.user()) )
			list.add(IBrick.FIELD_USER+print(ref.user(), val.user()));
		
		if( ! compare(ref.fileRecord(), val.fileRecord()) )
			list.add(IBrick.FIELD_FILERECORD+print(ref.fileRecord(), val.fileRecord()));
		if( ! compare(DataTool.getIndex(ref), DataTool.getIndex(val)) )
			list.add(FIELD_INDEX+print(DataTool.getIndex(ref), DataTool.getIndex(val)));
		
		long[][] r = DataTool.getRights(ref);
		long[][] v = DataTool.getRights(val);
		if( ! (compare(r[0], v[0]) && compare(r[1], v[1])) )
			list.add(FIELD_RIGHTS+print(r[0], v[0])+print(r[1], v[1]));
		
		return list;
	}
	
	/**
	 * @param brick
	 * @param linker
	 */
	protected void linkInformation(IBrick brick, GetLinks linker) {
		
		//structure
		linker.add(IBrick.FIELD_PARENT, brick, LinkInfo.PARENT);
		
		if( fTc.get(USE_CHILD_LINK_INFO) ) {
			//child information
			boolean cont = true;
			if( fTc.get(USE_SKIP_LARGE_CHILD_SETS) ) {
				int size = ((IBrickInterna) brick).vector().size();
				if(size >= 1000)
					cont = false;
			}//fi
			
			int i=0;
			Iterator<?> iter = brick.getIterator();
			while(cont && iter.hasNext()){
				linker.add(String.valueOf(i), (IBrick)iter.next(), LinkInfo.CHILD);
				i++;
			}//while
		}//fi
	}
	
	// ------------------------------------------------------------------------------------
	// History information
	// ------------------------------------------------------------------------------------
	
	/**
	 * @param brick
	 * @param r
	 * @throws IOException 
	 */
	protected void putHistoryInformation(IBrick brick, IDataReader r) throws IOException {
		//M4
		((IBrickHistory) brick).validityBeginning( r.readString( IBrick.FIELD_VALIDITYBEGINNING ) );
	}
	
	/**
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	protected void addHistoryInformation(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		//
		// data of ITimeSlice
		//
		
		//validity beginning
		//M3
		//w.write( IBrick.FIELD_VALIDITYBEGINNING, ((ITimeSlice) brick).getTimeSliceValidityBeginning() );
		//M4
		w.write( IBrick.FIELD_VALIDITYBEGINNING, ((IBrickHistory) brick).validityBeginning() );
		
		//structure
		//linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_NEXT);
		//linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_PREV);
		//linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_NEXT_CHANGE);
		//linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_PREV_CHANGE);
		linkHistory(brick, linker);
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 */
	protected List<String> compareHistoryInformation(IBrick ref, IBrick val) {
		List<String> list = new ArrayList<String>();
		if( ! compare(((IBrickHistory) ref).validityBeginning(), ((IBrickHistory) val).validityBeginning()) )
			list.add( IBrick.FIELD_VALIDITYBEGINNING +
					print(((IBrickHistory) ref).validityBeginning(), ((IBrickHistory) val).validityBeginning()));
		
		return list;
	}
	
	/**
	 * @param brick
	 * @param linker
	 */
	protected void linkHistory(IBrick brick, GetLinks linker){
		
		//structure
		if( fTc.get(SKIP_HISTORY_BRICKS))
			return;
		
		linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_NEXT);
		linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_PREV);
		linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_NEXT_CHANGE);
		linker.add( FIELD_HISTORY, brick, LinkInfo.HISTORY_PREV_CHANGE);
		
	}
	
	// ------------------------------------------------------------------------------------
	// Data
	// ------------------------------------------------------------------------------------
	
	/**
	 * Use type info to ensure the right number type. Otherwise a Long could be stored as an Integer if
	 * the value is valid for both types.
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected void putDataInformation(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		if( fTc.get(USE_TYPE_INFO) ){
			putDataInformationC(brick, r);
		//}else if( fTc.get(USE_TYPE_INFO) ){
		//	putDataInformationB(brick, r);
		}else{
			putDataInformationA(brick, r);
		}
	}
	
	/**
	 * Use data as is
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	protected void putDataInformationA(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		
		IDataReader rd = r.readDataReader(FIELD_DATA);
		String[] keys = rd.readStringArray(SYM_KEY);
		Map<String, Object> data = (Map<String, Object>)((IBrickData) brick).getMap();
		for(int i=0;i<keys.length;i++){
			String key = keys[i];
			Object val = rd.readObject(key);
			data.put(key, val);
		}//for
	}
	
	/**
	 * Use Integer/Long switch
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	protected void putDataInformationB(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		
		IDataReader rd = r.readDataReader(FIELD_DATA);
		String[] keys = rd.readStringArray(SYM_KEY);
		Map<String, Object> data = (Map<String, Object>)((IBrickData) brick).getMap();
		for(int i=0;i<keys.length;i++){
			String key = keys[i];
			Object val = rd.readObject(key);
			
			if(val instanceof Long) {
				long v = ((Long)val).longValue();
				if(v>=Integer.MIN_VALUE && v<=Integer.MAX_VALUE)
					val = new Integer((int)v);
			}
			
			data.put(key, val);
		}//for
	}
	
	/**
	 * Use type information
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void putDataInformationC(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		
		IDataReader rd = r.readDataReader(FIELD_DATA);
		String[] keys = rd.readStringArray(SYM_KEY);
		String[] type = rd.readStringArray(SYM_CLASS);
		Map<String, Object> data = (Map<String, Object>)((IBrickData) brick).getMap();
		for(int i=0;i<keys.length;i++){
			String key = keys[i];
			String cast = type[i];
			Object val = rd.readObject(key);
			
			String name = val.getClass().getSimpleName();
			if(name.equals(cast)){
				data.put(key, val);
			}else{
				//cast type if necessary
				if(cast.equals("String")){
					data.put(key, val.toString());
				}else if(cast.equals("Long")){
					if(name.equals("String")){
						data.put(key, Long.valueOf(val.toString()));
					}else if(val instanceof Number){
						data.put(key, new Long( ((Number)val).longValue() ));
					}else{
						throw new ClassCastException("Unable to cast a "+name+" into a "+cast+".");
					}
				}else if(cast.equals("Integer")){
					if(name.equals("String")){
						data.put(key, Integer.valueOf(val.toString()));
					}else if(val instanceof Number){
						data.put(key, new Integer( ((Number)val).intValue() ));
					}else{
						throw new ClassCastException("Unable to cast a "+name+" into a "+cast+".");
					}
				}else if(cast.equals("Double")){
					if(name.equals("String")){
						data.put(key, Double.valueOf(val.toString()));
					}else if(val instanceof Number){
						data.put(key, new Double( ((Number)val).doubleValue() ));
					}else{
						throw new ClassCastException("Unable to cast a "+name+" into a "+cast+".");
					}
				}else if(cast.equals("byte[]")){
					//The base64 encoded string is the preferred encoding of a byte array.
					if(name.equals("String")){
						data.put(key, JsonReader.encodeBytesFromString((String)val));
					}else if(val instanceof ArrayList){
						data.put(key, JsonReader.encodeBytesFromArray((List)val));
					}else{
						throw new ClassCastException("Unable to cast a "+name+" into a "+cast+".");
					}
				}else{
					throw new ClassCastException("Unable to cast a "+name+" into a "+cast+".");
				}//fi
			}//fi
			
		}//for
	}
	
	/**
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	protected void addDataInformation(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		if( fTc.get(USE_TYPE_INFO) ){
			addDataInformationC(brick, w, linker);
		}else{
			//as is
			addDataInformationA(brick, w, linker);
		}
	}
	
	/**
	 * Use data as is
	 * 
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addDataInformationA(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		IDataWriter wd = w.getSubWriter(BrickRNA.FIELD_DATA);
		Map data = ((IBrickData)brick).getMap();
		
		Collection kSet = fTc.get(SORT_FIELD_KEYS) ? sortKeys(data.keySet()) : data.keySet();
		List<String> keys = new ArrayList<String>();
		for(Object k : kSet) {
			Object v = data.get(k);
			if(v==null){
				//does nothing
			}else if (v instanceof IProxy){
				linker.add((String)k, brick, (IProxy)v, LinkInfo.PROXY);
			}else{
				keys.add((String)k);
			}
		}
		wd.write(SYM_KEY, keys.toArray(new String[keys.size()]));
		for(String k : keys){
			Object v = data.get(k);
			wd.write((String)k, v);
		}
		wd.close();
	}
	
	/**
	 * Use type information
	 * 
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addDataInformationC(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		IDataWriter wd = w.getSubWriter(BrickRNA.FIELD_DATA);
		Map data = ((IBrickData)brick).getMap();
		Collection kSet = fTc.get(SORT_FIELD_KEYS) ? sortKeys(data.keySet()) : data.keySet();
		List<String> keys = new ArrayList<String>();
		List<String> type = new ArrayList<String>();
		for(Object k : kSet) {
			Object v = data.get(k);
			if(v==null){
				//does nothing
			}else if (v instanceof IProxy){
				linker.add((String)k, brick, (IProxy)v, LinkInfo.PROXY);
			}else{
				keys.add((String)k);
				type.add(v.getClass().getSimpleName());
			}
		}
		wd.write(SYM_KEY, keys.toArray(new String[keys.size()]));
		wd.write(SYM_CLASS, type.toArray(new String[keys.size()]));
		for(String k : keys){
			Object v = data.get(k);
			//do not write a byte array as json array, use base64 encoding
			if(v instanceof byte[]){
				wd.write((String)k, (byte[])v);
			}else{
				wd.write((String)k, v);
			}
		}
		wd.close();
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	protected List<String> compareDataInformation(IBrick ref, IBrick val) {
		List<String> list = new ArrayList<String>();
		
		Map data = ((IBrickData)ref).getMap();
		Map test = ((IBrickData)val).getMap();
		for(Object k : data.keySet()) {
			Object v = data.get(k);
			if (v instanceof IProxy && test.get(k) instanceof IProxy){
				if( ! compare((IProxy)data.get(k), (IProxy)test.get(k)) )
					list.add((String)k+print((IProxy)data.get(k), (IProxy)test.get(k)));
			}else{
				if( ! compare(data.get(k), test.get(k)) )
					list.add((String)k+print(data.get(k), test.get(k)));
			}
		}
		
		return list;
	}
	
	/**
	 * @param brick
	 * @param linker
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void linkDataInformation(IBrick brick, GetLinks linker){
		Map data = ((IBrickData)brick).getMap();
		for(Object k : data.keySet()) {
			Object v = data.get(k);
			if(v==null){
				//does nothing
			}else if (v instanceof IProxy){
				linker.add((String)k, brick, (IProxy)v, LinkInfo.PROXY);
			}else{
				//does nothing
			}
		}
	}
	
	// ------------------------------------------------------------------------------------
	// Data (export)
	// ------------------------------------------------------------------------------------
	
	/**
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	protected void addExportInformation(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		//
		// print the id
		//
		IDataWriter wi = w.getSubWriter(FIELD_OB4_INTERN);
		addInformation(brick, wi, linker);
		wi.close();
		
		//structure
		//linker.add(IBrick.FIELD_PARENT, brick, LinkInfo.PARENT);
		linkInformation(brick, linker);
	}
	
	/**
	 * Use data as is
	 * 
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void addExportDataInformation(IBrick brick, IDataWriter w, GetLinks linker) throws IOException {
		
		//
		// print the data map
		//
		
		Map<String, Object> data = ((IBrickData)brick).getMap();
		
		Collection<String> kSet = fTc.get(SORT_FIELD_KEYS) ? sortKeys(data.keySet()) : data.keySet();
		for(String k : kSet) {
			Object v = data.get(k);
			if(v==null){
				//does nothing
			}else if (v instanceof IProxy){
				linker.add((String)k, brick, (IProxy)v, LinkInfo.PROXY);
			}else{
				if( fTc.get(USE_TYPE_INFO) ){
					IDataWriter wd = w.getSubWriter(k);
					wd.write("$"+v.getClass().getSimpleName(), v);
					wd.close();
				}else{
					w.write(k, v);
				}//fi
			}//fi
		}//for
	}
	
	/**
	 * Use data as is
	 * 
	 * @param brick
	 * @param w
	 * @param linker
	 * @throws IOException
	 */
	protected void addExportDocument(IBrick brick, IDataWriter w, int docDb) throws IOException {
		
		IDataWriter wi = w.getSubWriter(FIELD_OB4_DOCUMENT);
		
        if(docDb==IDocDb.WRITE_NONE){
        	addDocumentDataOnly(brick, wi);
        }else if(docDb==IDocDb.WRITE_REF){
        	addDocumentInformation(brick, wi);
        }else if(docDb==IDocDb.WRITE_DOC){
        	addDocument(brick, wi);
        }else if(docDb==IDocDb.WRITE_LATEST){
        	addDocumentLatestVersion(brick, wi);
        }else{
            wi.close();
        	throw new UnsupportedOperationException("The operation docDb="+docDb+" is not specified.");
        }
        
        wi.close();
	}
	
	/**
	 * @param w
	 * @param linker
	 * @throws IOException 
	 */
	protected void addExportLinks(IDataWriter w, GetLinks linker) throws IOException {
		List<LinkInfo> link = linker.getList();
		IDataWriter wd = w.getSubWriter(FIELD_OB4_REFERENCE);
		wd.write(LINK_SIZE, link.size());
		for(int i=0;i<link.size();i++){
			LinkInfo info = link.get(i);
			IDataWriter ws = wd.getSubWriter(FIELD_CONTENT_N+i);
			ws.write(LINK_SOUCE, info.sourceID);
			ws.write(LINK_TARGET, info.targetID);
			ws.write(LINK_SOURCE_TRANSID, info.sourceTransid);
			ws.write(LINK_TARGET_TRANSID, info.targetTransid);
			ws.write(LINK_TYPE, info.type);
			ws.write(LINK_RESOURCE, info.resource);
			
			ws.close();
		}
		wd.close();
	}
	
	//TODO:
	
	/**
	 * Use data as is
	 * 
	 * @param brick
	 * @param r
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	protected void putExportDataInformation(IBrick brick, IDataReader r) throws IOException, ClassNotFoundException {
		
		String[] keys = r.readStringArray(FIELD_OB4_KEY);
		Map<String, Object> data = (Map<String, Object>)((IBrickData) brick).getMap();
		for(int i=0;i<keys.length;i++){
			String key = keys[i];
			if( fTc.get(USE_TYPE_INFO) ){
				IDataReader rd = r.readDataReader(key);
				String[] k = rd.readStringArray(FIELD_OB4_KEY);
				if(k.length>0) {
					Object val = null;
					
					if(k[0].equals("$"+String.class.getSimpleName())){
						val = r.readString(k[0]);
					}
					else if(k[0].equals("$"+Long.class.getSimpleName())){
						val = r.readLong(k[0]);
					}
					else if(k[0].equals("$"+Integer.class.getSimpleName())){
						val = r.readInt(k[0]);
					}
					else if(k[0].equals("$"+Double.class.getSimpleName())){
						val = r.readDouble(k[0]);
					}
					else{
						val = r.readObject(k[0]);
					}
					
					data.put(key, val);
				}//fi
			}else{
				Object val = r.readObject(key);
				data.put(key, val);
			}
		}//for
	}
	
	/**
	 * @param r
	 * @param hdl
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws UserContentException 
	 */
	protected void setExportLinks(IDataReader r, ISetLinks linker) throws IOException, ClassNotFoundException, UserContentException {
		IDataReader rd = r; //must be set before the method call
		int size = rd.readInt(LINK_SIZE);
		for(int i=0;i<size;i++){
			IDataReader rs = rd.readDataReader(FIELD_CONTENT_N+i);
			
			LinkInfo info = new LinkInfo();
			info.sourceID = rs.readLong(LINK_SOUCE);
			info.targetID = rs.readLong(LINK_TARGET);
			info.sourceTransid = rs.readInt(LINK_SOURCE_TRANSID);
			info.targetTransid = rs.readInt(LINK_TARGET_TRANSID);
			info.type = rs.readInt(LINK_TYPE);
			info.resource = rs.readString(LINK_RESOURCE);
			
			linker.add(info);
		}
	}
	
	
	// ------------------------------------------------------------------------------------
	// Document
	// ------------------------------------------------------------------------------------
	
	/**
	 * @param brick
	 * @param r
	 * @throws IOException 
	 * @throws UserContentException 
	 */
	protected void putDocumentInformation(IBrick brick, IDataReader r) throws IOException, UserContentException {
		((IDocument)brick).setDocumentData( r.readString( FIELD_DOCUMENT_DATA ) );
		
		String stmt = r.readString( FIELD_DOCUMENT );
		try {
			RNAFactory.setDocument(brick, stmt);
		} catch (Exception e) {
			IOException io = new IOException( e.toString() );
			io.initCause(e);
			throw io;
		}
	}
	
	/**
	 * @param brick
	 * @param w
	 * @throws IOException
	 */
	protected void addDocumentDataOnly(IBrick brick, IDataWriter w) throws IOException {
		
		//
		// data of IDocument
		//
		
		w.write( FIELD_DOCUMENT_DATA, ((IDocument)brick).getDocumentData() );
		w.write( FIELD_DOCUMENT, "" );
	}
	
	/**
	 * @param brick
	 * @param linker
	 * @throws IOException
	 */
	protected void addDocumentInformation(IBrick brick, IDataWriter w) throws IOException {
		
		//
		// data of IDocument
		//
		
		w.write( FIELD_DOCUMENT_DATA, ((IDocument)brick).getDocumentData() );
		IIdsDocument doc = ((IDocument)brick).getDocument();
		if(doc==null){
			w.write( FIELD_DOCUMENT, "" );
		}else{
			try {
				w.write( FIELD_DOCUMENT, RNAFactory.referenceToAffiliatedDatabase( doc ) );
			} catch (Exception e) {
				IOException io = new IOException( e.toString() );
				io.initCause(e);
				throw io;
			}
		}
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 * @throws IOException 
	 */
	protected List<String> compareDocumentInformation(IBrick ref, IBrick val) throws IOException {
		List<String> list = new ArrayList<String>();
		if( ! compareIgnoreNull( ((IDocument) ref).getDocumentData(), ((IDocument) val).getDocumentData() ) )
			list.add(FIELD_DOCUMENT_DATA +
					print(((IDocument) ref).getDocumentData(), ((IDocument) val).getDocumentData()));
		
		try {
			String sRef = RNAFactory.referenceToAffiliatedDatabase( ((IDocument) ref).getDocument() );
			String sVal = RNAFactory.referenceToAffiliatedDatabase( ((IDocument) val).getDocument() );
			if( ! compare( sRef, sVal ) )
				list.add(FIELD_DOCUMENT+print( sRef, sVal ));
		} catch (Exception e) {
			IOException io = new IOException( e.toString() );
			io.initCause(e);
			throw io;
		}
		
		return list;
	}
	
	/**
	 * @param brick
	 * @param r
	 * @param docDb
	 * @throws IOException 
	 * @throws UserContentException 
	 * @throws ClassNotFoundException 
	 * @throws ConcurrentAccessException 
	 */
	protected void putDocument(IBrick brick, IDataReader r, int docDb) throws IOException, UserContentException, ClassNotFoundException, ConcurrentAccessException {
		((IDocument)brick).setDocumentData( r.readString( FIELD_DOCUMENT_DATA ) );
		
		IDataReader rd = r.readDataReader(FIELD_DOCUMENT);
		@SuppressWarnings("unused")
		long id = rd.readLong(IBrick.FIELD_ID);
		int size = rd.readInt(FIELD_SIZE);
		
		if(size>0){
			for(int i=0;i<size;i++){
				IDataReader rs = rd.readDataReader(FIELD_CONTENT_N+i);
				
				@SuppressWarnings("unused")
				int length = rs.readInt(FIELD_LENGTH);
				String mimetype = rs.readString(FIELD_MIMETYPE);
				long ts = rs.readLong(FIELD_TIMESTAMP);
				int num = rs.readInt(FIELD_SIZE);
				
				List<byte[]> content = new ArrayList<byte[]>();
				for(int ii=0;ii<num;ii++){
					byte[] buf = rs.readByteArray(FIELD_CONTENT_N+ii);
					content.add(buf);
				}//for
				
				try {
					RNAFactory.createIdsStore(brick, ts, mimetype, content);
				} catch (Exception e) {
					IOException io = new IOException( e.toString() );
					io.initCause(e);
					throw io;
				}
			}//for
		}//fi
	}
	
	/**
	 * @param brick
	 * @param w
	 * @throws IOException
	 */
	protected void addDocument(IBrick brick, IDataWriter w) throws IOException {
		
		//
		// data of IDocument
		//
		
		w.write( FIELD_DOCUMENT_DATA, ((IDocument)brick).getDocumentData() );
		
		//document
		IDataWriter wd = w.getSubWriter(FIELD_DOCUMENT);
		
		IIdsDocument d = ((IDocument)brick).getDocument();
		if(d==null){
			wd.write( IBrick.FIELD_ID, brick.ID() );
			wd.write( FIELD_SIZE, 0);
		}else{
			wd.write( IBrick.FIELD_ID, brick.ID() );
			wd.write( FIELD_SIZE, d.last()+1 );
			
			for(int i=0;i<=d.last();i++){
				printDocumentVersion(d, wd, i, i);
			}//for
		}//fi
		
		wd.close();
	}
	
	/**
	 * @param brick
	 * @param w
	 * @throws IOException
	 */
	protected void addDocumentLatestVersion(IBrick brick, IDataWriter w) throws IOException {
		
		//
		// data of IDocument
		//
		
		w.write( FIELD_DOCUMENT_DATA, ((IDocument)brick).getDocumentData() );
		
		//document
		IDataWriter wd = w.getSubWriter(FIELD_DOCUMENT);
		
		IIdsDocument d = ((IDocument)brick).getDocument();
		if(d==null){
			wd.write( IBrick.FIELD_ID, brick.ID() );
			wd.write( FIELD_SIZE, 0);
		}else{
			wd.write( IBrick.FIELD_ID, brick.ID() );
			wd.write( FIELD_SIZE, 1 );
			
			printDocumentVersion(d, wd, d.last(), 0);
		}//fi
		
		wd.close();
	}
	
	/**
	 * Prints a single document version
	 * 
	 * @param d The document
	 * @param wd The writer
	 * @param i the index of the version in the document
	 * @param j the index of the version in the output
	 * @throws IOException
	 */
	private static void printDocumentVersion(IIdsDocument d, IDataWriter wd, int i, int j) throws IOException {
		IDataWriter ws = wd.getSubWriter(FIELD_CONTENT_N+j);
		IIdsStore s = d.get(i);
		
		ws.write( FIELD_LENGTH, s.length() );
		ws.write( FIELD_MIMETYPE, s.mimetype() );
		ws.write( FIELD_TIMESTAMP, s.TS() );
		ws.write( FIELD_SIZE, s.size() );
		for(int ii=0;ii<s.size();ii++){
			try {
				ws.write( FIELD_CONTENT_N+ii, s.persist(ii) );
			} catch (ConcurrentAccessException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ee) {}
			}
		}
		
		ws.close();
	}
	
	/**
	 * @param ref
	 * @param val
	 * @param fCrc
	 * @return
	 * @throws IOException 
	 */
	protected List<String> compareDocument(IBrick ref, IBrick val, boolean fCrc) {
		List<String> list = new ArrayList<String>();
		if( ! compareIgnoreNull( ((IDocument) ref).getDocumentData(), ((IDocument) val).getDocumentData() ) )
			list.add(FIELD_DOCUMENT_DATA +
					print(((IDocument) ref).getDocumentData(), ((IDocument) val).getDocumentData()));
		
		List<DocInfo> refInf = DataTool.getDocumentInfo(ref, fCrc);
		List<DocInfo> valInf = DataTool.getDocumentInfo(val, fCrc);
		
		if( ! compare( refInf , valInf ) )
			list.add(FIELD_DOCUMENT);
		
		return list;
	}
	
	// ------------------------------------------------------------------------------------
	// Links
	// ------------------------------------------------------------------------------------
	
	//TODO:
	/**
	 * @param r
	 * @param hdl
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws UserContentException 
	 */
	protected void setLinks(IDataReader r, ISetLinks linker) throws IOException, ClassNotFoundException, UserContentException {
		IDataReader rd = r.readDataReader(FIELD_REFERENCES);
		int size = rd.readInt(LINK_SIZE);
		for(int i=0;i<size;i++){
			IDataReader rs = rd.readDataReader(FIELD_CONTENT_N+i);
			
			LinkInfo info = new LinkInfo();
			info.sourceID = rs.readLong(LINK_SOUCE);
			info.targetID = rs.readLong(LINK_TARGET);
			info.sourceTransid = rs.readInt(LINK_SOURCE_TRANSID);
			info.targetTransid = rs.readInt(LINK_TARGET_TRANSID);
			info.type = rs.readInt(LINK_TYPE);
			info.resource = rs.readString(LINK_RESOURCE);
			
			linker.add(info);
		}
	}
	
	/**
	 * @param w
	 * @param linker
	 * @throws IOException 
	 */
	protected void addLinks(IDataWriter w, GetLinks linker) throws IOException {
		
		List<LinkInfo> link = linker.getList();
		IDataWriter wd = w.getSubWriter(FIELD_REFERENCES);
		wd.write(LINK_SIZE, link.size());
		for(int i=0;i<link.size();i++){
			LinkInfo info = link.get(i);
			IDataWriter ws = wd.getSubWriter(FIELD_CONTENT_N+i);
			ws.write(LINK_SOUCE, info.sourceID);
			ws.write(LINK_TARGET, info.targetID);
			ws.write(LINK_SOURCE_TRANSID, info.sourceTransid);
			ws.write(LINK_TARGET_TRANSID, info.targetTransid);
			ws.write(LINK_TYPE, info.type);
			ws.write(LINK_RESOURCE, info.resource);
			
			ws.close();
		}
		wd.close();
	}
	
	/**
	 * @param ref
	 * @param val
	 * @return
	 */
	protected boolean compareLinks(IBrick ref, IBrick val) {
		
		return getLinkCRC(ref)==getLinkCRC(val);
	}
	
	/**
	 * Creates a crc32 hash from the links of the this brick.
	 * 
	 * @param brick
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private long getLinkCRC(IBrick brick) {
		
		StringBuffer buf = new StringBuffer();
		final char separator = '|';
		
		//self
		buf.append( brick.ID() );
		buf.append(separator);
		buf.append( brick.transid() );
		buf.append(separator);
		
		//parent
		if(brick.parent()!=null){
			buf.append( brick.parent().ID() );
			buf.append(separator);
			buf.append( brick.parent().transid() );
			buf.append(separator);
		}
		
		//child
		//implied by parent
		
		//proxy
		//implied by proxy from data
		
		//history
		ITimeSlice ts = (ITimeSlice)brick; 
		if(ts.getNextTimeSlice()!=null){
			buf.append( ((IBrick) ts.getNextTimeSlice()).ID() );
			buf.append(separator);
			buf.append( ((IBrick) ts.getNextTimeSlice()).transid() );
			buf.append(separator);
		}
		if(ts.getPrevTimeSlice()!=null){
			buf.append( ((IBrick) ts.getPrevTimeSlice()).ID() );
			buf.append(separator);
			buf.append( ((IBrick) ts.getPrevTimeSlice()).transid() );
			buf.append(separator);
		}
		if(ts.getNextChangeTimeSlice()!=null){
			buf.append( ((IBrick) ts.getNextChangeTimeSlice()).ID() );
			buf.append(separator);
			buf.append( ((IBrick) ts.getNextChangeTimeSlice()).transid() );
			buf.append(separator);
		}
		if(ts.getPrevChangeTimeSlice()!=null){
			buf.append( ((IBrick) ts.getPrevChangeTimeSlice()).ID() );
			buf.append(separator);
			buf.append( ((IBrick) ts.getPrevChangeTimeSlice()).transid() );
			buf.append(separator);
		}
		
		//proxy
		Map data = ((IBrickData)brick).getMap();
		for(Object key : data.keySet()){
			Object val = data.get(key);
			if(val instanceof IProxy){
				IProxy proxy = (IProxy)val;
				if(proxy.getReference()!=null){
					buf.append( proxy.getReference().ID() );
					buf.append(separator);
					buf.append( proxy.getReference().transid() );
					buf.append(separator);
				}
			}
		}
		buf.setLength(buf.length()-1);
		
		CRC32 crc = new CRC32();
		crc.update( buf.toString().getBytes() );
		return crc.getValue();
	}
	
	// ------------------------------------------------------------------------------------
	// Tool box
	// ------------------------------------------------------------------------------------
	
	//
	// sort
	//
	
	/**
	 * A sort for a collection containing the field names.
	 * 
	 * @param col The keySet or collection of names
	 * @return
	 */
	private List<String> sortKeys(Collection<String> col) {
		List<String> keys = new ArrayList<String>();
		keys.addAll(col);
		
		Collections.sort(keys);
		return keys;
	}
	
	//
	// compare
	//
	
	private boolean compare(String a, String b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					a.equals(b);
	}
	
	//compares null and '' as equal
	private boolean compareIgnoreNull(String a, String b) {
		return a==null && b==null ? true :
			a==null && b!=null && b.equals("") ? true :
				b==null && a!=null && a.equals("") ? true :
					a==null && b!=null ? false :
						a!=null && b==null ? false :
							a.equals(b);
	}
	
	private boolean compare(String[] a, String[] b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					a.length!=b.length ? false :
						Arrays.equals(a, b);
	}
	
	private boolean compare(Object a, Object b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					compareNotNull(a, b);
	}
	private boolean compareNotNull(Object a, Object b){
		if(a.equals(b))
			return true;
		
		if(fTc.get(IGNORE_INT_TYPE) && a instanceof Number && b instanceof Number){
			return ((Number)a).longValue()==((Number)b).longValue();
		}
		
		if( a.getClass().isArray() && b.getClass().isArray() ){
			if(a instanceof byte[] && b instanceof byte[] )
				return Arrays.equals((byte[])a, (byte[])b);
			else if(a instanceof short[] && b instanceof short[] )
				return Arrays.equals((short[])a, (short[])b);
			else if(a instanceof int[] && b instanceof int[] )
				return Arrays.equals((int[])a, (int[])b);
			else if(a instanceof long[] && b instanceof long[] )
				return Arrays.equals((byte[])a, (byte[])b);
			else if(a instanceof float[] && b instanceof float[] )
				return Arrays.equals((float[])a, (float[])b);
			else if(a instanceof double[] && b instanceof double[] )
				return Arrays.equals((double[])a, (double[])b);
			
			return Arrays.equals( (Object[])a, (Object[])b );
		}
		
		return false;
	}
	
	private boolean compare(IProxy a, IProxy b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					a.getReference()==null && b.getReference()==null ? true :
						a.getReference()==null && b.getReference()!=null ? false :
							a.getReference()!=null && b.getReference()==null ? false :
								a.getReference().ID()==b.getReference().ID();
	}
	
	private boolean compare(List<DocInfo> a, List<DocInfo> b) {
		if(a.size()!=b.size())
			return false;
		
		for(int i=0;i<a.size();i++){
			DocInfo dA = a.get(i);
			DocInfo dB = b.get(i);
			
			if( !( compare(dA.length, dB.length) &&
					compare(dA.mimetype, dB.mimetype) &&
					compare(dA.size, dB.size) &&
					compare(dA.timestamp, dB.timestamp) &&
					compare(dA.crc, dB.crc) ) )
				return false;
		}
		
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean compare(BigInteger a, BigInteger b) {
		return a.equals(b);
	}
	
	@SuppressWarnings("unused")
	private boolean compare(BigInteger[] a, BigInteger[] b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					a.length!=b.length ? false :
						Arrays.equals(a, b);
	}
	
	private boolean compare(long a, long b) {
		return a==b;
	}
	
	private boolean compare(long[] a, long[] b) {
		return a==null && b==null ? true :
			a==null && b!=null ? false :
				a!=null && b==null ? false :
					a.length!=b.length ? false :
						Arrays.equals(a, b);
	}
	
	private boolean compare(int a, int b) {
		return a==b;
	}
	
	@SuppressWarnings("unused")
	private boolean compare(double a, double b) {
		return a==b;
	}
	
	//
	// print
	//
	
	private String print(String a, String b) {
		return " '"+a+"' '"+b+"'";
	}
	
	private String print(String[] a, String[] b) {
		StringBuffer bufA = new StringBuffer();
		bufA.append('[');
		if(a!=null){
			for(int i=0;i<a.length;i++){
				if(i>0)
					bufA.append(',');
				
				bufA.append(a[i]);
			}
		}
		bufA.append(']');
		
		StringBuffer bufB = new StringBuffer();
		bufB.append('[');
		if(b!=null){
			for(int i=0;i<b.length;i++){
				if(i>0)
					bufB.append(',');
				
				bufB.append(b[i]);
			}
		}
		bufB.append(']');
		
		return " "+bufA.toString()+" "+bufB.toString();
	}
	
	private String print(Object a, Object b) {
		return " '"+String.valueOf(a)+"' '"+String.valueOf(b)+"' ("+(a==null?"Null":a.getClass().getSimpleName())+", "+(b==null?"Null":b.getClass().getSimpleName())+")";
	}
	
	private String print(IProxy a, IProxy b) {
		if(a==null || b==null)
			return " '"+String.valueOf(a)+"' '"+String.valueOf(b)+"'";
		if(a.getReference()==null || b.getReference()==null)
			return " '"+String.valueOf(a.getReference())+"' '"+String.valueOf(b.getReference())+"'";
		
		return " "+String.valueOf(a.getReference().ID())+" "+String.valueOf(b.getReference().ID());
	}
	
	@SuppressWarnings("unused")
	private String print(BigInteger a, BigInteger b) {
		return " "+String.valueOf(a)+" "+String.valueOf(b);
	}
	
	@SuppressWarnings("unused")
	private String print(BigInteger[] a, BigInteger[] b) {
		if(a==null || b==null)
			return " '"+String.valueOf(a)+"' '"+String.valueOf(b)+"'";
		
		StringBuffer bufA = new StringBuffer();
		bufA.append('[');
		if(a!=null){
			for(int i=0;i<a.length;i++){
				if(i>0)
					bufA.append(',');
				
				bufA.append(a[i]);
			}
		}
		bufA.append(']');
		
		StringBuffer bufB = new StringBuffer();
		bufB.append('[');
		if(b!=null){
			for(int i=0;i<b.length;i++){
				if(i>0)
					bufB.append(',');
				
				bufB.append(b[i]);
			}
		}
		bufB.append(']');
		
		return " "+bufA.toString()+" "+bufB.toString();
	}
	
	private String print(long a, long b) {
		return " "+String.valueOf(a)+" "+String.valueOf(b);
	}
	
	private String print(long[] a, long[] b) {
		if(a==null || b==null)
			return " '"+String.valueOf(a)+"' '"+String.valueOf(b)+"'";
		
		StringBuffer bufA = new StringBuffer();
		bufA.append('[');
		if(a!=null){
			for(int i=0;i<a.length;i++){
				if(i>0)
					bufA.append(',');
				
				bufA.append(a[i]);
			}
		}
		bufA.append(']');
		
		StringBuffer bufB = new StringBuffer();
		bufB.append('[');
		if(b!=null){
			for(int i=0;i<b.length;i++){
				if(i>0)
					bufB.append(',');
				
				bufB.append(b[i]);
			}
		}
		bufB.append(']');
		
		return " "+bufA.toString()+" "+bufB.toString();
	}
	
	private String print(int a, int b) {
		return " "+String.valueOf(a)+" "+String.valueOf(b);
	}
	
	@SuppressWarnings("unused")
	private String print(double a, double b) {
		return " "+String.valueOf(a)+" "+String.valueOf(b);
	}
}

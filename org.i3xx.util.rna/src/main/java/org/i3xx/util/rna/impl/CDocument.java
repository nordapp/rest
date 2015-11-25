/*
 * Created on 16.04.2005
 */
package org.i3xx.util.rna.impl;

import java.io.File;

import org.i3xx.util.rna.core.DatabaseException;
import org.i3xx.util.rna.core.IBrick;
import org.i3xx.util.rna.core.IDocument;
import org.i3xx.util.rna.core.IIdsDocument;
import org.i3xx.util.rna.core.IRole;
import org.i3xx.util.rna.core.UserContentException;
import org.i3xx.util.rna.engine.server.DocumentFactory;

/**
 * @author S. Hauptmann
 */
public class CDocument extends CData implements IDocument {
	
	private static final long serialVersionUID = 1L;
	
	private String document;
	private String documentData;
	
	/**
	 * @param name
	 * @param classname
	 * @param id
	 */
	public CDocument(String name, String classname, Long id) {
		super(name, classname, id);
		
		document=null;
		documentData=null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		CDocument brick = (CDocument)super.clone();
		if(isFlag(IBrick.CLONE_DOC_TEMPLATE)){
			brick.setDocument(new IdsDocument());
			brick.obtimestamp( Long.toString(System.currentTimeMillis()) );
			removeFlag(IBrick.CLONE_DOC_TEMPLATE);
		}else if(isFlag(IBrick.EXTERN)){
			brick.setDocument( (IIdsDocument)getDocument().clone() );
			
			//try to reset the data - if there are not enough rights
			/*
			try {
				brick.setValue( FIELD_CONTENT, "" );
			} catch (UserContentException e) {}
			try {
				brick.setValue( FIELD_CONTENT_CRC, "" );
			} catch (UserContentException e) {}
			try {
				brick.setValue( FIELD_CONTENT_LENGTH, "" );
			} catch (UserContentException e) {}
			try {
				brick.setValue( FIELD_CONTENT_TYPE, "" );
			} catch (UserContentException e) {}
			*/
			
			//remove them if possible (need intern rights)
			brick.getMap().remove(FIELD_CONTENT);
			brick.getMap().remove(FIELD_CONTENT_CRC);
			brick.getMap().remove(FIELD_CONTENT_LENGTH);
			brick.getMap().remove(FIELD_CONTENT_TYPE);
		}else if(document!=null){
			brick.setDocument( (IIdsDocument)getDocument().clone() );
		}
		
		return brick;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IDocument#getVisitor()
	 */
	public String getVisitor() {
		return getDocumentData();
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IDocument#getDocumentData()
	 */
	public String getDocumentData() {
        if(rejectRight(IRole.RIGHT_READFIELD, this, IBrick.METHOD_INTERN))
            return null;
        
		return documentData;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IDocument#setDocumentData(java.lang.String)
	 */
	public void setDocumentData(String newValue) throws UserContentException {
        if(rejectRight(IRole.RIGHT_WRITEFIELD, this, IBrick.METHOD_INTERN))
            throw new UserContentException( "The field 'documentData' cannot be changed because of insufficient rights.");
        
		documentData=newValue;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IDocument#setDocument(com.i3xx.ob.core.IIdsDocument)
	 */
	public void setDocument(IIdsDocument doc) throws DatabaseException {

		if(rejectRight(IRole.RIGHT_WRITE, this, IBrick.METHOD_DOCUMENT))
			throw new DatabaseException("The field 'documentData' cannot be changed because of insufficient rights.");
		if(readonly())
			throw new DatabaseException("The document is readonly.");
		if(isFlag(IBrick.EXTERN))
			throw new DatabaseException("The document is extern.");
		
		if(doc!=null) {
	        if(isFlag(IBrick.EXTERN)){
	        	//does nothing
	        }else{
	        	//
				doc.ID( ID() );
	
				String tmp = DocumentFactory.getStore().migrate(doc);
				// Pfadangaben entfernen
				int i = tmp.lastIndexOf(File.separator);
				if( i==-1 )
					document = tmp;
				else
					document = tmp.substring(i);
	        }
			setFlag(IBrick.DOCUMENT);
		}
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IDocument#getDocument()
	 */
	public IIdsDocument getDocument() {
        if(rejectRight(IRole.RIGHT_READFIELD, this, IBrick.METHOD_DOCUMENT))
            return null;
		if(document==null)
			return null;
		
		//ExternalReference ref = ExternalReference.fromString(ActualPlacement.docPath() + document);
		//return (IIdsDocument)ref.getObject();
        IIdsDocument temp = null;
        if(isFlag(IBrick.EXTERN)){
        	temp = DocumentFactory.getStore().fetch(document);
        }else{
        	temp = DocumentFactory.getStore().fetch(document);
        }
            
		return temp;
	}

	/* (non-Javadoc)
	 * @see com.i3xx.ob.core.IBrick#delete()
	 */
	public void delete()
	{
		if(rejectRight(IRole.RIGHT_DELETE, this, IBrick.METHOD_INTERN))
			return;
			
		super.delete();
		if(document!=null)
			getDocument().delete();
	}
}

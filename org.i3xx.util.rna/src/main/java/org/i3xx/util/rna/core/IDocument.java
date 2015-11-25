package org.i3xx.util.rna.core;


/**
 *  Alle Elemente, die auf ein IIdsDocument verweisen, m�ssen IDocument implementieren.
 *
 *  Die �bergabe des Dokumentes kann als Base64-String erfolgen. F�r die Ausgabe sind
 *  die entsprechenden Ausgabeelemente (com.i3xx.ob.x_outserved.vdv.visitor.*) verantwortlich.
 *  Die Eingabe ist vom Element <content/> abh�ngig, das optional ist. Die Eingabe
 *  ist mittels der �nderungsklasse UpdateBrick (com.i3xx.ob.x_outserved.vdv.base.*) implementiert.
 */
public interface IDocument {

	public static final String BASE64_URL_ENCODED = "base64-url-encoded";
	public static final String BASE64_ENCODED = "base64-encoded";
	
	/** Length of the content before encoded (original file size) */
	public static final String FIELD_CONTENT_LENGTH = "content-length";
	/** the sign of the content (crc32 algorithm result of the original file) */
	public static final String FIELD_CONTENT_CRC = "content-crc";
	/** the original content type (mime-type) */
	public static final String FIELD_CONTENT_TYPE = "content-type";
	/** additional data: the data acquiring symbol */
	public static final String FIELD_CONTENT_DEFS = "content-defs";
	/** The encoding of the content (default: base64-url-encoded) */
	public static final String FIELD_CONTENT_ENCODING = "content-encoding"; //Analog content-transfer-encoding
	/** The content itself */
	public static final String FIELD_CONTENT = "content";
	
	
    // Liefert den Namen des Iterators, der die Daten zur Verf�gung stellen soll.
    String getVisitor();
    
    String getDocumentData() ;
    
    void setDocumentData(String newValue) throws UserContentException ;
    
    // Eingebettetes Dokument
    void setDocument(IIdsDocument doc) throws DatabaseException ;
    
    IIdsDocument getDocument() ;
}

/**
 * 
 */
package org.i3xx.util.rna.core;

/**
 * @author Benny
 *
 */
public class AccessViolationException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;
    
    private long mId = 0;
    private String sClassname = null;
    private int askedRight = 0;
    private String sType = null;

    public AccessViolationException() {super();}
    public AccessViolationException( String msg ) {super( msg );}
    public AccessViolationException( Throwable cause ) {super( cause );}
    public AccessViolationException( String msg, Throwable cause ) {super( msg, cause );}

    /**
     * @param sClassname
     */
    public void setClassname ( String sClassname ){
    	this.sClassname = sClassname;
    }
    
    /**
     * @return
     */
    public String getClassname(){
    	return sClassname;
    }
    
    /**
     * @param askedRight
     */
    public void setAskedRight ( int askedRight ){
    	this.askedRight = askedRight;
    }
    
    /**
     * @return
     */
    public int getAskedRight(){
    	return askedRight;
    }
    
    /**
     * @param sType
     */
    public void setType ( String sType ){
    	this.sType = sType;
    }
    
    /**
     * @return
     */
    public String getType(){
    	return sType;
    }
    
    /**
     * @param id
     */
    public void setId ( long id ){
    	this.mId = id;
    }
    
    /**
     * @return
     */
    public long getId(){
    	return mId;
    }
}

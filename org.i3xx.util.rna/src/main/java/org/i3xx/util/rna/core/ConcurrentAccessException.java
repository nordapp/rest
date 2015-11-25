package org.i3xx.util.rna.core;

public class ConcurrentAccessException extends Exception
{

    private static final long serialVersionUID = 1L;

	public ConcurrentAccessException()
    {
		super();
    }
    
    public ConcurrentAccessException(String msg)
    {
    	super(msg);
    }
    
    public ConcurrentAccessException(Throwable cause){super(cause);}
    
    public ConcurrentAccessException(String s, Throwable cause){super(s, cause);}
}

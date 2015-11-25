package org.i3xx.util.rna.core;

public class IllegalParameterException extends Exception{
    private static final long serialVersionUID = 1L;
	public IllegalParameterException(){super();}
    public IllegalParameterException(String msg){super(msg);}
    public IllegalParameterException(Throwable cause){super(cause);}
    public IllegalParameterException(String s, Throwable cause){super(s, cause);}
}

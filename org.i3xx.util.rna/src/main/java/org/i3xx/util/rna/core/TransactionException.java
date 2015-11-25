package org.i3xx.util.rna.core;

public class TransactionException extends XMLException {
    private static final long serialVersionUID = 1487235202034267818L;
	public TransactionException(String msg){super(msg);}
    public TransactionException(){super();}
    public TransactionException(Throwable cause){super(cause);}
    public TransactionException(String s, Throwable cause){super(s, cause);}
}

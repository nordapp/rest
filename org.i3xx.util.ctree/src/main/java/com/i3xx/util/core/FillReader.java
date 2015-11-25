package com.i3xx.util.core;
import java.io.*;

public abstract class FillReader extends FilterReader
{
	private final static int THROUGH=0;
	private final static int FILTER=1;

	private final static int END_OF_STREAM=-1;

	private CharArrayReader in2;
	private CharArrayWriter out2;
	private char startToken;
	private char endToken;
	private char escape;
	private boolean escaped;
	private int watch;

	public FillReader(Reader in, char startToken, char endToken)
	{
		this(in, startToken, endToken, '\\');
	}
	
	public FillReader(Reader in, char startToken, char endToken, char escape)
	{
		super(in);
		this.startToken = startToken;
		this.endToken = endToken;
		this.escape = escape;
		escaped = false;
		watch = THROUGH;
		in2 = null;
		out2 = null;
	}

	public int read(char[] buf) throws IOException
	{
		return read(buf, 0, buf.length);
	}

	public int read(char[] buf, int off, int len) throws IOException
	{
		if ((off < 0) || (off > buf.length) || (len < 0) ||
			((off + len) > buf.length) || ((off + len) < 0))
		{
			throw new IndexOutOfBoundsException();
		}
		else if (len == 0)
		{
			return 0;
		}

		for (int i = 0; i < len; ++i) {
			int c = read();
			if (c < 0) return i == 0 ? -1 : i;
			buf[off++] = (char) c;
		}
		return len;
	}

	public int read() throws IOException
	{
		int c=0;

		while(watch==THROUGH) {
			if(in2==null){
				if(out2!=null){
					watch=FILTER;
					break;
				}

				c=in.read();
				if(c==escape){
					if(escaped){
						escaped=false;
						return c;
					}else{
						escaped=true;
					}
				}else if(c==startToken){
					if(escaped){
						escaped=false;
						return c;
					}else{
						watch=FILTER;
						out2=new CharArrayWriter();
						out2.write(c);
					}
				}else{
					if(escaped)
						escaped=false;
					
					return c;
				}
			}else{
				if(escaped)
					escaped=false;
				
				c=in2.read();
				if(c==-1){
					in2=null;
				}else{
					return c;
				}
			}
		}

		do{
			c=in.read();
			if(c==escape){
				if(escaped){
					escaped=false;
					out2.write(c);
				}else{
					escaped=true;
				}
			}else if(c==startToken){
				if(escaped){
					escaped=false;
					out2.write(c);
				}else{
					in2 = new CharArrayReader(out2.toCharArray());
					out2=new CharArrayWriter();
					out2.write(c);
					c=in2.read();
					watch=THROUGH;
				}
			}else if(c==endToken){
				if(escaped){
					escaped=false;
					out2.write(c);
				}else{
					out2.write(c);
	
					/* pr�fen und konvertieren */
					char[] result = convert(out2.toCharArray());
	
					/* weiter ... */
					if(result.length==0){
						in2=null;
						c=in.read();
					}else{
						in2 = new CharArrayReader(result);
						c=in2.read();
					}
					out2=null;
					watch=THROUGH;
				}
			}else if(c==END_OF_STREAM){
				in2 = new CharArrayReader(out2.toCharArray());
				out2=null;
				watch=THROUGH;
				c=in2.read();
			}else{
				if(escaped)
					escaped=false;
				
				out2.write(c);
			}
		}while(watch==FILTER);

		return c;
	}

	public abstract char[] convert(char[] buf) throws IOException ;
}

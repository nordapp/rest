package org.i3xx.util.ctree;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;

public class ConfValues
{
	private List<String> content;

	public ConfValues(ConfNode node)
	{
		this(node.value());
	}

	public ConfValues(String value)
	{
		this(value, " ");
	}

	public ConfValues(String value, String separator)
	{
		StringTokenizer tok = new StringTokenizer(value, separator, false);
		content = new ArrayList<String>();

		while(tok.hasMoreTokens()){
			content.add(tok.nextToken());
		}
	}

	public int size()
	{
		return content.size();
	}

	public String value(int i)
	{
		String val = null;
		if( i < content.size() )
			val = content.get(i);

		if(val == null)
			return "";

		return val;
	}

	public boolean value(int i, boolean def)
	{
		try{
			return Boolean.valueOf(value(i)).booleanValue();
		}catch(NumberFormatException e){
			return def;
		}
	}

	public byte value(int i, byte def)
	{
		try {
			return Byte.parseByte(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public short value(int i, short def)
	{
		try {
			return Short.parseShort(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public int value(int i, int def)
	{
		try {
			return Integer.parseInt(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public long value(int i, long def)
	{
		try {
			return Long.parseLong(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public float value(int i, float def)
	{
		try {
			return Float.parseFloat(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public double value(int i, double def)
	{
		try {
			return Double.parseDouble(value(i));
		}catch(NumberFormatException e){
			return def;
		}
	}

	public String[] values()
	{
		return content.toArray(new String[content.size()]);
	}

	public String[] values(int off)
	{
		return values(off, ( size()-off ) );
	}

	public String[] values(int off, int len)
	{
		String[] temp = content.toArray(new String[content.size()]);
		String[] result = new String[len];
		System.arraycopy(temp, off, result, 0, len);

		return result;
	}
}

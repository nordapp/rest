package org.i3xx.util.ramdisk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class RamdiskImplURLConnection extends URLConnection {
	
	private File file;
	
	public RamdiskImplURLConnection(URL url) {
		super(url);
		
		file = null;
	}

	@Override
	public void connect() throws IOException {
		if(connected)
			return;
		
		//get file
		if( url.getProtocol().equalsIgnoreCase("ramdisk") ){
			file = new File(url.getFile());
		}
		if(file==null || (! file.exists())){
			throw new IOException("The file '"+url.toString()+"' is not available.");
		}
		
		connected = true;
	}
	
	@Override
	public InputStream getInputStream() throws IOException
	{
		if(!connected)
			connect();
		
		return new ByteArrayInputStream( file.getContent()==null ? new byte[0] : file.getContent() );
	}
	
	@Override
    public long getLastModified() {
    	return getHeaderFieldDate("last-modified", 0);
    }
	
	@Override
	public OutputStream getOutputStream() throws IOException
	{
		if(!connected)
			connect();
		
		return new MyOutputStream();
	}

	//------------------------------------------------
	// Private OutputStream
	//------------------------------------------------

	private class MyOutputStream extends ByteArrayOutputStream
	{
		public void close() throws IOException
		{
			super.close();
			file.setContent( toByteArray() );
		}
	}//class

	//------------------------------------------------
}

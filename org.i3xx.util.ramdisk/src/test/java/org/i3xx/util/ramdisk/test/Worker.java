package org.i3xx.util.ramdisk.test;

import java.io.IOException;

import org.i3xx.util.ramdisk.File;

public class Worker implements Runnable {
	
	private long timeout;
	private int nanos;
	private int num;
	private String path;
	private FileList words;
	private boolean cont;
	
	public Worker(FileList words, long timeout, int nanos) {
		this.words = words;
		this.timeout = timeout;
		this.nanos = nanos;
		this.num = 0;
		this.path = "";
		this.cont = true;
	}
	
	@Override
	public void run() {
		
		while(cont) {
			
			String word = words.getWord();
			if(word==null) {
				cont = false;
				continue;
			}
			
			if(num==0 || num==1) {
				File file = new File( path+"/"+word );
				try{
					//Note: It is possible that a directory
					//contains no files. Because of this case
					//the directory must be created now.
					if( ! file.exists()) {
						file.mkdir();
					}
				}catch(IOException e){
					e.printStackTrace();
				}
				
				path += "/"+word;
			}else{
				
				File file = new File( path+"/"+word );
				try{
					if( ! file.exists()) {
						file.getParent().mkdirs();
						file.create();
						
						file.setContentType("text/plain");
						file.setContent(word.getBytes());
					}else{
						throw new IOException("The file '"+file.getAbsolutePath()+"' already exists.");
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			num++;
			if(num>10)
				resetPath();
			
				try {
					if(timeout>0 || nanos>0){
						Thread.sleep(timeout, nanos);
					}else{
						Thread.yield();
					}
				} catch (InterruptedException e) {}
		}//while
	}
	
	public boolean getCont() {
		return cont;
	}
	
	private void resetPath() {
		this.num = 0;
		this.path = "";
	}
}

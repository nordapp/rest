package org.i3xx.util.ramdisk.test;

import java.io.IOException;

import org.i3xx.util.ramdisk.File;

public class Worker implements Runnable {
	
	private int del;
	private int num;
	private String path;
	private FileList words;
	
	public Worker(FileList words, int del) {
		this.words = words;
		this.del = del;
		this.num = 0;
		this.path = "";
	}
	
	@Override
	public void run() {
		
		while(true) {
			
			String word = words.getWord();
			if(word==null)
				break;
			
			if(num==0 || num==1) {
				path += "/"+word;
			}else{
				
				File file = new File( path+"/"+word );
				try{
					if( ! file.exists()) {
						file.getParent().mkdirs();
						file.create();
						
						file.setContentType("text/plain");
						file.setContent(word.getBytes());
					}
				}catch(IOException e){}
			}
			
			num++;
			if(num>10)
				resetPath();
			
			try {
				Thread.sleep(del);
			} catch (InterruptedException e) {}
		}//while
	}
	
	private void resetPath() {
		this.num = 0;
		this.path = "";
	}

}
